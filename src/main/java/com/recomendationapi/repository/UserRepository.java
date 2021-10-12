package com.recomendationapi.repository;

import com.recomendationapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByMediaId(String mediaId);
}
