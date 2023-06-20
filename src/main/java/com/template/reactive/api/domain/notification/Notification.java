package com.template.reactive.api.domain.notification;

import com.template.reactive.api.helper.TimeHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private static final String JSON_TEMPLATE = "{\"event\": \"%s\", \"timestamp\": \"%s\"}";

    @NotNull
    private Set<String> users;

    @NotNull
    private String event;

    @NotNull
    private Instant timestamp;

    public String toJsonString() {
        return String.format(JSON_TEMPLATE, this.getEvent(), TimeHelper.parseInstant(this.timestamp));
    }
}