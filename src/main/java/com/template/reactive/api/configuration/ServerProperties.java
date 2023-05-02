package com.template.reactive.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;

@Data
@Configuration
@ConfigurationProperties(prefix = "server")
public class ServerProperties {
    @NotEmpty
    private String appId;
}