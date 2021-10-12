package com.recomendationapi.service;

import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.User;
import com.recomendationapi.repository.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Transactional
@Service
public class ProviderService extends DefaultService<Provider, ProviderRepository> {

    private UserService userService;

    @Autowired
    public ProviderService(UserService userService, ProviderRepository repository) {
        super(repository);
        this.userService = userService;
    }

    public Provider getProviderById(String id) {
        return repository.findById(id).orElse(new Provider());
    }

    public Provider getProvider(String name) {
        return repository.findProviderByNameIsLike(name).orElse(new Provider());
    }

    public List<Provider> getAllOrderByScore() {
        return repository.findProvidersByActiveOrderByScoreAvgDesc(true);
    }

    public List<Provider> getProviderByUserRecommendation(List<String> userIds, int page, int size) {

        List<Provider> list = repository.findProvidersByRecommendationsUserIdsOrderByScoreAvgDesc(userIds);

        List<Provider> list2 = repository.findProvidersByRecommendationsUserIdsOrderByScoreAvgDesc(userIds, PageRequest.of(page, size));
        return list2;
    }

    public Provider add(ProviderForm form) {
        log.info("add:provider: start form:" + form.toString());
        Provider entity = form.convertToEntity();
        log.info("add:provider: start entity:" + entity.toString());

        User user = userService.getUserByMediaId(form.getUserId());
        if (isNotEmpty(user.getId())) {
            Provider provider = getProvider(entity.getName());
            log.info("add:provider: pending =" + user.toString() + "; " + provider.toString());
            return validateAndSaveEntity(entity, provider);
        } else {
            throw new RuntimeException("User do NOT exists, get out :)");
        }

    }
}
