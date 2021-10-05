package com.recomendationapi.service;

import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.Recommendation;
import com.recomendationapi.model.User;
import com.recomendationapi.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
}
