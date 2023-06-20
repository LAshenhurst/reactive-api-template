package com.template.reactive.api.service.impl;

import com.template.reactive.api.common.exceptions.ApiException;
import com.template.reactive.api.domain.Role;
import com.template.reactive.api.domain.User;
import com.template.reactive.api.domain.mapper.UserMapper;
import com.template.reactive.api.repository.UserRepository;
import com.template.reactive.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findById(username)
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.BAD_REQUEST, "Username not found.")))
                .map(userMapper::toDomain)
                .doOnSubscribe(sub -> log.debug("Executing findByUsername service"))
                .doFinally(sub -> log.debug("Executed findByUsername service"));
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll()
                .map(userMapper::toDomain)
                .doOnSubscribe(sub -> log.debug("Executing findAll service"))
                .doFinally(sub -> log.debug("Executed findAll service"));
    }

    @Override
    public Flux<User> findByRole(Role role) {
        return userRepository.findByRole(role.name())
                .map(userMapper::toDomain)
                .doOnSubscribe(sub -> log.debug("Executing findByRole service"))
                .doFinally(sub -> log.debug("Executed findByRole service"));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void addUsers() {
        //password is password
        final String password = "Y/zMAg4P07PpGZLiyWutYveUz3f8TV1S0kMlGxWG4o0=";
        userRepository.save(userMapper.toEntity(new User("admin", password, Collections.singleton(Role.ADMIN)))).subscribe();
        userRepository.save(userMapper.toEntity(new User("user", password, Collections.singleton(Role.USER)))).subscribe();
    }
}