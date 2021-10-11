package com.recomendationapi.service;

import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.User;
import com.recomendationapi.repository.ProviderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<Provider> getProviderById(String id) {
        return repository.findById(id).defaultIfEmpty(new Provider());
    }

    public Mono<Provider> getProvider(String name) {
        return repository.findProviderByNameIsLike(name).defaultIfEmpty(new Provider());
    }

    public Flux<Provider> getAllOrderByScore() {
        return repository.findProvidersByActiveOrderByScoreAvgDesc(true);
    }

    public Mono<Provider> add(ProviderForm form) {
        log.info("add:provider: start form:" + form.toString());
        Provider entity = form.convertToEntity();
        log.info("add:provider: start entity:" + entity.toString());
        return Mono.zip(userService.getUserByMediaId(form.getUserId()), getProvider(entity.getName()))
            .map(objects -> {
                User user = objects.getT1();
                Provider provider = objects.getT2();
                log.info("add:provider: pending =" + user.toString() + "; " + provider.toString());
                if (isNotEmpty(user.getId())) {
                    return validateAndSaveEntity(entity, provider);
                } else {
                    throw new RuntimeException("User do NOT exists, get out :)");
                }
            });
    }
}
