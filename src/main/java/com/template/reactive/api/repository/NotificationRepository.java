package com.template.reactive.api.repository;

import com.template.reactive.api.domain.entity.NotificationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationEntity, Long> {
    @Query("SELECT * FROM NOTIFICATIONS WHERE USERNAME = :username")
    Flux<NotificationEntity> findByUsername(@Param("username") String username);
}
