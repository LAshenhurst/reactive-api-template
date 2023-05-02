package com.template.reactive.api.controller;

import com.template.reactive.api.common.exceptions.ApiException;
import com.template.reactive.api.configuration.security.PBKDF2Encoder;
import com.template.reactive.api.domain.Role;
import com.template.reactive.api.domain.User;
import com.template.reactive.api.domain.security.AuthenticationRequest;
import com.template.reactive.api.domain.security.AuthenticationResponse;
import com.template.reactive.api.helper.JWTHelper;
import com.template.reactive.api.helper.SecurityHelper;
import com.template.reactive.api.service.AuthenticationService;
import com.template.reactive.api.service.NotificationService;
import com.template.reactive.api.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {
    private final JWTHelper jwtHelper;
    private final UserService userService;
    private final PBKDF2Encoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuthenticationService authenticationService;

    private static final Set<String> ROLES = Arrays.stream(Role.values())
            .map(Role::name)
            .collect(Collectors.toSet());

    @PostMapping("/login")
    @Operation(summary = "Log in using a username and password")
    public Mono<ResponseEntity<AuthenticationResponse>> login(@RequestBody AuthenticationRequest ar) {
        return userService.findByUsername(ar.getUsername())
                .filter(userDetails -> passwordEncoder.encode(ar.getPassword()).equals(userDetails.getPassword()))
                .map(userDetails -> ResponseEntity.ok(new AuthenticationResponse(jwtHelper.generateToken(userDetails))))
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase())));
    }

    @PostMapping("/decode-jwt/{jwt}")
    @Operation(summary = "Decode a given jwt to view the Claims contained")
    public Mono<ResponseEntity<Jws<Claims>>> decode(@PathVariable String jwt) {
        return Mono.just(ResponseEntity.ok(jwtHelper.decodeToken(jwt)));
    }

    @GetMapping("/roles")
    @Operation(summary = "Get all roles associated with the logged in user")
    public Mono<ResponseEntity<List<String>>> getRoles() {
        return authenticationService.getRoles()
                .map(Role::name)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/roles/{role}")
    @Operation(summary = "Get a list of users with a given role (ADMIN only)")
    public Mono<ResponseEntity<List<String>>> getUsersByRole(@NotNull @PathVariable String role) {
        Role validRole = Role.getRole(role);
        if (validRole == null) {
            String errorMessage = "Invalid Role provided. Valid Roles are: " + String.join(", ", ROLES);
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        Mono<List<String>> responseFlow = userService.findByRole(validRole)
                .map(User::getUsername)
                .collectList();

        return authenticationService.checkAdminPermissionAndContinue(responseFlow)
                .map(ResponseEntity::ok);
    }


    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to receive notifications")
    public Flux<ServerSentEvent<String>> subscribe() {
        return SecurityHelper.getUsername().flatMapMany(notificationService::subscribe);
    }
}
