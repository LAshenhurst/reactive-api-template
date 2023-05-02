package com.template.reactive.api.service.impl;

import com.template.reactive.api.common.exceptions.ApiException;
import com.template.reactive.api.domain.Role;
import com.template.reactive.api.domain.User;
import com.template.reactive.api.helper.SecurityHelper;
import com.template.reactive.api.service.AuthenticationService;
import com.template.reactive.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;

    @Override
    public <T> Mono<T> checkAdminPermissionAndContinue(Mono<T> continueFlow) {
        return getRoles()
                .any(role -> role.equals(Role.ADMIN))
                .flatMap(isAdmin -> {
                    if (Boolean.TRUE.equals(isAdmin)) { return continueFlow; }
                    return Mono.error(new ApiException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase()));
                });
    }

    @Override
    public <T> Flux<T> checkAdminPermissionAndContinue(Flux<T> continueFlow) {
        return getRoles()
                .any(role -> role.equals(Role.ADMIN))
                .flatMapMany(isAdmin -> {
                    if (Boolean.TRUE.equals(isAdmin)) { return continueFlow; }
                    return Flux.error(new ApiException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase()));
                });
    }

    @Override
    public Flux<Role> getRoles() {
        return SecurityHelper.getUsername()
                .flatMap(userService::findByUsername)
                .flatMapIterable(User::getRoles);
    }

    @Override
    public Mono<Boolean> checkUserRole(Role expectedType) {
        return getRoles().any(type -> type.equals(expectedType));
    }

    @Override
    public <T> Mono<T> checkUserRoleAndContinue(Role expectedType, Mono<T> continueFlow) {
        return checkUserRole(expectedType)
                .flatMap(isExpectedType -> {
                    if (Boolean.TRUE.equals(isExpectedType)) { return continueFlow; }
                    return Mono.error(new ApiException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase()));
                });
    }

    @Override
    public <T> Flux<T> checkUserRoleAndContinue(Role expectedType, Flux<T> continueFlow) {
        return checkUserRole(expectedType)
                .flatMapMany(isExpectedType -> {
                    if (Boolean.TRUE.equals(isExpectedType)) { return continueFlow; }
                    return Mono.error(new ApiException(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase()));
                });
    }
}