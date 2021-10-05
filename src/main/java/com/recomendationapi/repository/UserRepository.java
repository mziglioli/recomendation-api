package com.recomendationapi.repository;

import com.recomendationapi.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findUserByEmail(String email);
    Mono<User> findUserByMediaId(String mediaId);
}
