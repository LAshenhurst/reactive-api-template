package com.template.reactive.api.service.impl;

import com.template.reactive.api.common.exceptions.ApiException;
import com.template.reactive.api.domain.entity.NotificationEntity;
import com.template.reactive.api.domain.notification.Notification;
import com.template.reactive.api.domain.mapper.NotificationMapper;
import com.template.reactive.api.helper.SecurityHelper;
import com.template.reactive.api.repository.NotificationRepository;
import com.template.reactive.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    private static final Sinks.Many<Notification> SINK = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    private static final List<String> ACTIVE_SUBSCRIBERS = new ArrayList<>();

    @Override
    public Flux<ServerSentEvent<String>> subscribe(String username) {
        return SINK.asFlux()
                .concatWith(
                        notificationRepository.findByUsername(username)
                                .switchIfEmpty(Flux.fromIterable(new ArrayList<>()))
                                .map(notificationEntity -> {
                                    log.debug("Historical notification found for user '{}' with id '{}'", username, notificationEntity.getId());
                                    return notificationMapper.toDomain(notificationEntity);
                                })
                )
                .doOnSubscribe(sub -> {
                    emit(notificationMapper.generateSubscribeNotification(username));
                    ACTIVE_SUBSCRIBERS.add(username);
                    log.info("User '{}' is now subscribed to notifications", username);
                })
                .filter(notification -> notification.getUsers().contains(username))
                .map(notification -> ServerSentEvent.builder(notification.toJsonString()).build())
                .doOnTerminate(() -> {
                    log.info("User '{}' has disconnected from notifications", username);
                    ACTIVE_SUBSCRIBERS.remove(username);
                });
    }

    @Override
    public void emit(Notification notification) { emitMany(Collections.singleton(notification)); }

    @Override
    public void emitMany(Set<Notification> notifications) {
        if (notifications != null) {
            notifications
                    .forEach(notification -> {
                        notification.getUsers().forEach(user -> {
                            if (ACTIVE_SUBSCRIBERS.contains(user)) {
                                Sinks.EmitResult emitResult = SINK.tryEmitNext(notification);

                                if (emitResult.isSuccess()) { log.debug("Notification emitted: {}", notification); }
                                else { log.warn("Notification failure: {}, Emit Result: {}", notification, emitResult); }
                            } else {
                                NotificationEntity entity = notificationMapper.toEntity(user, notification);
                                log.info("Notification submitted for user not currently subscribed, saving to history.");
                                notificationRepository.save(entity);
                            }
                        });

                    });
        }
    }

    @Override
    public Mono<Void> confirmNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase())))
                .flatMap(notificationEntity ->
                        SecurityHelper.getUsername()
                                .flatMap(username -> {
                                    if (!notificationEntity.getUsername().equals(username)) {
                                        return Mono.error(new ApiException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()));
                                    }
                                    return notificationRepository.delete(notificationEntity);
                                })
                );
    }
}