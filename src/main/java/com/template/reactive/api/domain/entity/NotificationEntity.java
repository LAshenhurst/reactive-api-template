package com.template.reactive.api.domain.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("NOTIFICATIONS")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {
    @Id
    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String message;

    @NotNull
    private Long createdTimestamp;

    @Version
    @NotNull
    private Long entityVersion;
}
