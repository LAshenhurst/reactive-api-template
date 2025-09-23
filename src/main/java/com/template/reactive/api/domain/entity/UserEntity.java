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
@Table("USERS")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String roles;

    @NotNull
    private Boolean expired;

    @NotNull
    private Boolean locked;

    @NotNull
    private Boolean enabled;

    @Version
    @NotNull
    private Long entityVersion;
}