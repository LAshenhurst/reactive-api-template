package com.template.reactive.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @NotNull
    private Set<String> users;

    @NotNull
    private String event;

    @NotNull
    private Long timestamp;
}