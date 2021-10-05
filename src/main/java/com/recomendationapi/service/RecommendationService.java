package com.recomendationapi.service;

import com.recomendationapi.form.RecommendationFindForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.Recommendation;
import com.recomendationapi.model.User;
import com.recomendationapi.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Comparator;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Transactional
@Service
public class RecommendationService {

    private UserService userService;
    private ProviderService providerService;

    @Autowired
    public RecommendationService(UserService userService, ProviderService providerService) {
        this.userService = userService;
        this.providerService = providerService;
    }

    public String hasErrors(String userId, String providerId) {
        String error = "";
        if (isEmpty(userId)) {
            error += " user not exists;";
        }
        if (isEmpty(providerId)) {
            error += " provider not exists;";
        }
        return isEmpty(error) ? null : "Error: " + error;
    }

    public Flux<Provider> getRecommendations(RecommendationFindForm form) {
        return providerService.getAll()
            .filter(provider -> {
                if (provider.getRecommendations() != null) {
                    return provider.getRecommendations().stream().anyMatch(recommendation -> form.getUserIds().contains(recommendation.getUserId()));
                }
                return false;
            })
            .sort(Comparator.comparingInt(Provider::getScoreAvg));
    }

    public Flux<Provider> getRecommendations() {
        return providerService.getAllOrderByScore();
    }

    public Mono<DefaultResponse> addRecommendation (RecommendationForm form) {
        log.info("addRecommendation: " + form.toString());
        return Mono.zip(userService.getUserByMediaId(form.getUserId()), providerService.getProviderById(form.getProviderId()))
                .map(objects -> {
                    User user = objects.getT1();
                    Provider provider = objects.getT2();
                    String errors = hasErrors(user.getId(), provider.getId());
                    log.info("addRecommendation: errors: " + errors);

                    if (errors == null) {
                        Recommendation recommendation = form.buildRecommendation();

                        user.addRecommendation(recommendation);
                        userService.save(user).subscribe();
                        log.info("addRecommendation: user: success");

                        provider.addRecommendation(recommendation);

                        providerService.save(provider).subscribe();
                        log.info("addRecommendation: provider: success");

                        return DefaultResponse.builder()
                                .success(true)
                                .data(recommendation)
                                .build();
                    }
                    return DefaultResponse.builder()
                            .error(errors)
                            .build();
                });
    }

    public User buildUser(int i) {
        return User.builder()
                .email(i + "user@test.com")
                .name("User test"+ i)
                .mediaId("user_"+i)
                .mediaType("test")
                .build();
    }

    public Provider buildProvider(int i) {
        return Provider.builder()
                .email(i + "provider@test.com")
                .name("Provider test"+ i)
                .creatorId("user_"+i)
                .build();
    }

    public RecommendationForm buildForm(int i, String providerId) {
        return RecommendationForm.builder()
                .score(5)
                .userId("user_" + i)
                .providerId(providerId)
                .comments("test")
                .build();
    }

    @PostConstruct
    public void initDb() {
        log.info("init db");
        long userCount = userService.count().block();
        log.info("init db: users:" + userCount);
        if (userCount == 0) {
            userService.save(buildUser(1)).subscribe();
            userService.save(buildUser(2)).subscribe();
            userService.save(buildUser(3)).subscribe();
            userService.save(buildUser(4)).subscribe();
            userService.save(buildUser(5)).subscribe();
        }
        long providerCount = providerService.count().block();
        log.info("init db: providers:" + providerCount);
        if (providerCount == 0) {
            providerService.save(buildProvider(1)).subscribe();
            providerService.save(buildProvider(2)).subscribe();
            providerService.save(buildProvider(3)).subscribe();
            providerService.save(buildProvider(4)).subscribe();
            providerService.save(buildProvider(5)).subscribe();
        }
    }
}
