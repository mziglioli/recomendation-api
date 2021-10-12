package com.recomendationapi.service;

import com.recomendationapi.client.FacebookClient;
import com.recomendationapi.form.LoginForm;
import com.recomendationapi.form.RecommendationFindForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.Recommendation;
import com.recomendationapi.model.User;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.response.FacebookResponse;
import com.recomendationapi.response.UserLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
@Transactional
@Service
public class RecommendationService {

    private UserService userService;
    private ProviderService providerService;
    private TokenService tokenService;
    private FacebookClient facebookClient;

    @Autowired
    public RecommendationService(UserService userService, ProviderService providerService, TokenService tokenService, FacebookClient facebookClient) {
        this.userService = userService;
        this.providerService = providerService;
        this.tokenService = tokenService;
        this.facebookClient = facebookClient;
    }


    public DefaultResponse login(HttpServletResponse response, LoginForm form) {
        log.info("Login:init: {}", form.toString());
        FacebookResponse facebookResponse = facebookClient.getMe(form.getMediaToken());
        log.info("Login:facebookResponse: {}", facebookResponse.toString());

        if (isBlank(facebookResponse.getId())) {
            // not valid token should stop
            log.error("Login:facebookError");
            return DefaultResponse.builder()
                    .error("Invalid media token")
                    .build();
        }

        User user = userService.getUserByMediaId(form.getMediaId());
        if (isEmpty(user.getId())) {
            // first time user has used this, save it
            log.info("Login:user - new user will be saved");
            user.setEmail(facebookResponse.getEmail());
            user.setInitials(facebookResponse.getInitials());
            user.setMediaId(facebookResponse.getId());
            user.setName(facebookResponse.getName());
            user.setMediaType("facebook");
            user.addRole("ROLE_USER");
            user.setPassword("no_password");

            userService.save(user);

        // user exists and need to match the mediaId with facebookId
        } else if (!user.getMediaId().equals(facebookResponse.getId())) {
            log.error("Login:facebookError");
            return DefaultResponse.builder()
                    .error("Invalid media id")
                    .build();
        }

        // generate token
        String token = tokenService.createToken(user);
        tokenService.addCookie(response, user);

        return DefaultResponse.builder()
                .success(true)
                .data(UserLoginResponse.builder()
                        .facebook(facebookResponse)
                        .token(token)
                        .build())
                .build();
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

    public List<Provider> getRecommendations(RecommendationFindForm form) {
        return providerService.getProviderByUserRecommendation(form.getUserIds());
    }

    public List<Provider> getRecommendations() {
        return providerService.getAllOrderByScore();
    }

    public DefaultResponse addRecommendation (RecommendationForm form) {
        log.info("addRecommendation: " + form.toString());
        User user = userService.getUserByMediaId(form.getUserId());
        if (user == null) {
            return DefaultResponse.builder()
                    .error("Error: user not exists")
                    .build();
        }
        Provider provider = providerService.getProviderById(form.getProviderId());
        if (provider == null) {
            return DefaultResponse.builder()
                    .error("Error: provider not exists")
                    .build();
        }
        String errors = hasErrors(user.getId(), provider.getId());
        log.info("addRecommendation: errors: " + errors);

        if (errors == null) {
            Recommendation recommendation = form.buildRecommendation();

            user.addRecommendation(recommendation);
            userService.save(user);
            log.info("addRecommendation: user: success");

            provider.addRecommendation(recommendation);

            providerService.save(provider);
            log.info("addRecommendation: provider: success");

            return DefaultResponse.builder()
                    .success(true)
                    .data(recommendation)
                    .build();
        }
        return DefaultResponse.builder()
                .error(errors)
                .build();
    }

    public User buildUser(int i) {
        return User.builder()
                .email(i + "user@test.com")
                .name("User test"+ i)
                .mediaId("user_"+i)
                .mediaType("test")
                .password("user_"+i)
                .initials("U "+i)
                .roles(List.of("ROLE_USER"))
                .build();
    }
    public User buildAdmin() {
        return User.builder()
                .email("admin@admin.com")
                .name("Admin test")
                .mediaId("admin")
                .mediaType("admin")
                .password("admin")
                .initials("AD")
                .roles(List.of("ROLE_USER", "ROLE_ADMIN"))
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
        long userCount = userService.count();
        log.info("init db: users:" + userCount);
        if (userCount == 0) {
            userService.save(buildAdmin());
            userService.save(buildUser(1));
            userService.save(buildUser(2));
            userService.save(buildUser(3));
            userService.save(buildUser(4));
            userService.save(buildUser(5));
        }
        long providerCount = providerService.count();
        log.info("init db: providers:" + providerCount);
        if (providerCount == 0) {
            providerService.save(buildProvider(1));
            providerService.save(buildProvider(2));
            providerService.save(buildProvider(3));
            providerService.save(buildProvider(4));
            providerService.save(buildProvider(5));
        }
    }
}
