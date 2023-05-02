package com.template.reactive.api.repository;

import com.template.reactive.api.domain.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {
    @Query("SELECT * FROM USERS WHERE ROLES LIKE CONCAT('%',:role,'%')")
    Flux<UserEntity> findByRole(String role);
}