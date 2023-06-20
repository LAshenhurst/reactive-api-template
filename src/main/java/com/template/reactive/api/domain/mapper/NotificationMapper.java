package com.template.reactive.api.domain.mapper;

import com.template.reactive.api.domain.notification.Notification;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

@Mapper(componentModel = "spring")
public class NotificationMapper {
    public Notification generateSubscribeNotification(String username) {
        return toNotification(String.format("%s subscribed successfully", username), username);
    }

    public Notification toNotification(String message, String user) {
        return toNotification(message, Collections.singleton(user));
    }

    public Notification toNotification(String message, Set<String> users) {
        return Notification.builder()
                .event(message)
                .users(users)
                .timestamp(Instant.now())
                .build();
    }
}