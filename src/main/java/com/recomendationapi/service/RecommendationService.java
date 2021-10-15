package com.recomendationapi.service;

import com.recomendationapi.client.FacebookClient;
import com.recomendationapi.exception.EntityNotFoundException;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

            userService.save(user, "0");

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

    public Page<Provider> getRecommendations(RecommendationFindForm form) {
        return providerService.getProviderByUserRecommendation(form.getUserIds(), form.getPage(), form.getSize());
    }

    public DefaultResponse getRecommendations(String name, String page, String size) {
        int s = 100;
        int p = 0;
        if (isNumeric(page)) {
            p = Integer.parseInt(page);
        }
        if (isNumeric(size)) {
            s = Integer.parseInt(size);
        }
        Page<Provider> response = providerService.getAllOrderByScore(name, p, s);
        return DefaultResponse.builder()
                .success(true)
                .data(response)
                .build();
    }

    public DefaultResponse addRecommendation (RecommendationForm form) {
        User authUser = userService.getAuthenticatedUser();
        if (authUser == null || isEmpty(authUser.getId())) {
            throw new AccessDeniedException("Error: user not logged in");
        }

        // validate the form
        if (!StringUtils.equals(authUser.getMediaId(), form.getUserId())) {
            throw new AuthenticationCredentialsNotFoundException("Error: user do not match logged user");
        }

        User user = userService.getUserByMediaId(authUser.getMediaId());
        // validate the user is in db
        if (isEmpty(user.getId())) {
            throw new AuthenticationCredentialsNotFoundException("Error: user not exists");
        }

        Provider provider = providerService.getProviderById(form.getProviderId());
        if (provider == null || isEmpty(provider.getId())) {
            throw new EntityNotFoundException("Error: provider not exists");
        }

        Recommendation recommendation = form.buildRecommendation();
        user.addRecommendation(recommendation);
        userService.save(user, user.getMediaId());
        log.info("addRecommendation: user: success");

        provider.addRecommendation(recommendation);
        providerService.save(provider, user.getMediaId());
        log.info("addRecommendation: provider: success");

        return DefaultResponse.builder()
                .success(true)
                .data(recommendation)
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
    public Provider buildProvider(int i) {
        return Provider.builder()
                .email(i + "provider@test.com")
                .name("Provider test"+ i)
                .creatorId("user_"+i)
                .build();
    }

    public void initDb() {
        log.info("init db");
        long userCount = userService.count();
        log.info("init db: users:" + userCount);
        if (userCount == 0) {
            User user = User.builder()
                    .mediaId("123_Face")
                    .mediaType("Facebook")
                    .email("test@test.com")
                    .name("test")
                    .roles(List.of("ROLE_USER"))
                    .password("admin")
                    .initials("AD")
                    .build();
            userService.save(user, "0");
            userService.save(buildUser(1), "0");
            userService.save(buildUser(2), "0");
            userService.save(buildUser(3), "0");
            userService.save(buildUser(4), "0");
            userService.save(buildUser(5), "0");
        }
        long providerCount = providerService.count();
        log.info("init db: providers:" + providerCount);
        if (providerCount == 0) {
            providerService.save(buildProvider(1), "0");
            providerService.save(buildProvider(2), "0");
            providerService.save(buildProvider(3), "0");
            providerService.save(buildProvider(4), "0");
            providerService.save(buildProvider(5), "0");
        }
    }
}
