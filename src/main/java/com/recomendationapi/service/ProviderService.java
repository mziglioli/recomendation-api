package com.recomendationapi.service;

import com.recomendationapi.model.Provider;
import com.recomendationapi.repository.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Transactional
@Service
public class ProviderService extends DefaultService<Provider, ProviderRepository> {

    @Autowired
    public ProviderService(ProviderRepository repository) {
        super(repository);
    }

    public Provider getProviderById(String id) {
        return repository.findById(id).orElse(new Provider());
    }

    public Provider getProvider(String name) {
        return repository.findProviderByName(name).orElse(new Provider());
    }

    public Page<Provider> getAllByNameLike(String name, int page, int size) {
        return repository.findProvidersByActiveAndNameIsLikeOrderByScoreAvgDesc(true, name, PageRequest.of(page, size));
    }

    public Page<Provider> getAllOrderByScore(String name, int page, int size) {
        if (isNotEmpty(name)) {
            return getAllByNameLike(name, page, size);
        }
        return repository.findProvidersByActiveOrderByScoreAvgDesc(true, PageRequest.of(page, size));
    }

    public Page<Provider> getProviderByUserRecommendation(List<String> userIds, int page, int size) {
        return repository.findProvidersByActiveAndRecommendationsUserIdsOrderByScoreAvgDesc(true, userIds, PageRequest.of(page, size));
    }
}
