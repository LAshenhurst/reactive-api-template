package com.template.reactive.api.controller;

import com.template.reactive.api.domain.mapper.NotificationMapper;
import com.template.reactive.api.domain.notification.Notification;
import com.template.reactive.api.domain.notification.NotificationRequest;
import com.template.reactive.api.helper.SecurityHelper;
import com.template.reactive.api.service.AuthenticationService;
import com.template.reactive.api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "Notifications API")
public class NotificationController {
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Subscribe to receive notifications")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> subscribe() {
        return SecurityHelper.getUsername().flatMapMany(notificationService::subscribe);
    }

    @PostMapping
    @Operation(summary = "Submit a notification to be received by a given user through the subscription system (ADMIN ONLY)")
    public Mono<ResponseEntity<Void>> sendNotification(@Valid @RequestBody NotificationRequest notificationRequest,
                                                       @NotNull @RequestParam String user) {
        Notification notification = notificationMapper.toNotification(notificationRequest.getMessage(), user);

        Mono<Void> responseFlow = Mono.fromRunnable(() -> notificationService.emit(notification));

        return authenticationService.checkAdminPermissionAndContinue(responseFlow)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/confirm/{notificationId}")
    @Operation(summary = "Confirm the receipt of a historical notification to remove it from the history")
    public Mono<ResponseEntity<Void>> confirmNotification(@NotNull @PathVariable Long notificationId) {
        notificationService.confirmNotification(notificationId).subscribe();

        return Mono.just(ResponseEntity.ok().build());
    }
}
