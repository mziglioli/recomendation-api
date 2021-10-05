package com.recomendationapi.repository;

import com.recomendationapi.model.Provider;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProviderRepository extends ReactiveMongoRepository<Provider, String> {

    Mono<Provider> findProviderByNameIsLike(String name);
    Flux<Provider> findProvidersByActiveOrderByScoreAvgDesc(boolean active);
}
