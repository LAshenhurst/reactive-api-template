package com.template.reactive.api.service.impl;

import com.template.reactive.api.domain.Notification;
import com.template.reactive.api.domain.mapper.NotificationMapper;
import com.template.reactive.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;

    private static final Sinks.Many<Notification> SINK = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Override
    public Flux<ServerSentEvent<String>> subscribe(String username) {
        return SINK.asFlux()
                .doOnSubscribe(sub -> {
                    emit(notificationMapper.generateSubscribeNotification(username));
                    log.info("Current subscribers: {}", SINK.currentSubscriberCount());
                })
                .filter(notification -> notification.getUsers().contains(username))
                .map(notification -> ServerSentEvent
                        .builder(notification.getEvent() + String.format(". Event timestamp: %s", notification.getTimestamp()))
                        .build()
                );
    }

    @Override
    public void emit(Notification notification) { emitMany(Collections.singleton(notification)); }

    @Override
    public void emitMany(Set<Notification> notifications) {
        if (notifications != null) {
            notifications
                    .forEach(notification -> {
                        Sinks.EmitResult emitResult = SINK.tryEmitNext(notification);

                        if (emitResult.isSuccess()) { log.debug("Notification emitted: {}", notification); }
                        else { log.warn("Notification failure: {}, Emit Result: {}", notification, emitResult); }
                    });
        }
    }
}