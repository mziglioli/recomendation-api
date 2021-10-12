package com.recomendationapi.repository;

import com.recomendationapi.model.Provider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends MongoRepository<Provider, String> {

    Optional<Provider> findProviderByNameIsLike(String name);
    List<Provider> findProvidersByRecommendationsUserIdsOrderByScoreAvgDesc(List<String> ids, Pageable pageable);
    List<Provider> findProvidersByActiveOrderByScoreAvgDesc(boolean active, Pageable pageable);
}
