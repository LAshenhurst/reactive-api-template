package com.template.reactive.api.service;

import com.template.reactive.api.domain.notification.Notification;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface NotificationService {
    Flux<ServerSentEvent<String>> subscribe(String username);

    void emit(Notification notification);

    void emitMany(Set<Notification> notifications);

    Mono<Void> confirmNotification(Long notificationId);
}