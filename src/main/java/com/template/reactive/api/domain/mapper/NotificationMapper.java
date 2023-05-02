package com.template.reactive.api.domain.mapper;

import com.template.reactive.api.domain.Notification;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

@Mapper(componentModel = "spring")
public class NotificationMapper {
    public Notification generateSubscribeNotification(String username) {
        return toNotification(
                String.format("%s subscribed successfully", username),
                Instant.now().getEpochSecond(),
                Collections.singleton(username)
        );
    }

    private Notification toNotification(String message, Long timestamp, Set<String> users) {
        return Notification.builder()
                .event(message)
                .timestamp(timestamp)
                .users(users)
                .build();
    }
}