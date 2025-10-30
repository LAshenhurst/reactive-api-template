package com.template.reactive.api.domain.mapper;

import com.template.reactive.api.domain.entity.NotificationEntity;
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

    public NotificationEntity toEntity(String username, Notification notification) {
        return NotificationEntity.builder()
                .username(username)
                .message(notification.getEvent())
                .createdTimestamp(Instant.now().getEpochSecond())
                .build();
    }

    public Notification toDomain(NotificationEntity notificationEntity) {
        return Notification.builder()
                .event(notificationEntity.getMessage())
                .users(Collections.singleton(notificationEntity.getUsername()))
                .timestamp(Instant.ofEpochSecond(notificationEntity.getCreatedTimestamp()))
                .build();
    }
}