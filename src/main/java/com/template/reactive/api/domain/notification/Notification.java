package com.template.reactive.api.domain.notification;

import com.template.reactive.api.helper.TimeHelper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private static final String JSON_TEMPLATE = "{\"event\": \"%s\", \"timestamp\": \"%s\"}";
    private static final String ID_JSON_TEMPLATE = "{\"id\": %d, \"event\": \"%s\", \"timestamp\": \"%s\"}";

    private Long id;

    @NotNull
    private Set<String> users;

    @NotNull
    private String event;

    @NotNull
    private Instant timestamp;

    public String toJsonString() {
        if (this.getId() == null) {
            return String.format(JSON_TEMPLATE, this.getEvent(), TimeHelper.parseInstant(this.getTimestamp()));
        }
        return String.format(ID_JSON_TEMPLATE, this.getId(), this.getEvent(), TimeHelper.parseInstant(this.getTimestamp()));
    }
}