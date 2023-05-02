package com.template.reactive.api.domain.mapper;

import com.template.reactive.api.domain.Role;
import com.template.reactive.api.domain.User;
import com.template.reactive.api.domain.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public class UserMapper {
    public UserEntity toEntity(User source) {
        return UserEntity.builder()
                .username(source.getUsername())
                .password(source.getPassword())
                .roles(
                        source.getRoles()
                                .stream()
                                .map(Role::name)
                                .collect(Collectors.joining(","))
                )
                .expired(!source.isAccountNonExpired())
                .locked(!source.isAccountNonLocked())
                .enabled(source.isEnabled())
                .build();
    }

    public User toDomain(UserEntity source) {
        Set<Role> roles = Arrays.stream(source.getRoles().split(","))
                .map(Role::getRole)
                .collect(Collectors.toSet());

        return new User(source.getUsername(), source.getPassword(), roles);
    }
}