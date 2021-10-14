package com.recomendationapi.repository;

import com.recomendationapi.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends MongoRepository<Provider, String> {

    Optional<Provider> findProviderByName(String name);
    Page<Provider> findProvidersByActiveAndNameIsLikeOrderByScoreAvgDesc(boolean active, String name, Pageable pageable);
    Page<Provider> findProvidersByActiveAndRecommendationsUserIdsOrderByScoreAvgDesc(boolean active, List<String> ids, Pageable pageable);
    Page<Provider> findProvidersByActiveOrderByScoreAvgDesc(boolean active, Pageable pageable);
}
