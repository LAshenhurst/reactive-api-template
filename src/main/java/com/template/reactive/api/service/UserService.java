package com.template.reactive.api.service;

import com.template.reactive.api.domain.Role;
import com.template.reactive.api.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> findByUsername(String username);

    Flux<User> findAll();

    Flux<User> findByRole(Role role);
}