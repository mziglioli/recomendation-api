package com.recomendationapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.recomendationapi.config.security.UserAuthentication;
import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.form.UserForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.User;
import com.recomendationapi.response.FacebookResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    public final static String USER_EMAIL = "test@test.com";
    public final static String USER_MEDIA_ID_VALID = "123_Face";
    public final static String USER_MEDIA_ID_INVALID = "0000000";
    public final static String PROVIDER_NAME_VALID = "Test Provider";
    public final static String URI_ME = "/me?fields=id,email,friends,last_name,first_name&format=json&method=get&pretty=0&transport=cors&access_token=";

    public static ObjectMapper mapper = new ObjectMapper();

    public static User buildUserValid() {
        User user = User.builder()
                .mediaId(USER_MEDIA_ID_VALID)
                .mediaType("Facebook")
                .email(USER_EMAIL)
                .name("test")
                .id("123")
                .build();
        user.setActive(true);
        return user;
    }
    public static User buildAdmin() {
        return User.builder()
                .id("1")
                .password("admin")
                .initials("AD")
                .mediaType("admin")
                .mediaId("admin")
                .email("admin@admin.com")
                .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                .build();
    }
    public static User buildUserInvalid() {
        return User.builder().build();
    }
    public static Provider buildProviderValid() {
        return Provider.builder()
                .creatorId(USER_MEDIA_ID_VALID)
                .phone("01234567")
                .name(PROVIDER_NAME_VALID)
                .email("provider@test.com")
                .build();
    }
    public static ProviderForm buildProviderFormValid() {
        return ProviderForm.builder()
                .address("1 Test Street")
                .city("Manchester")
                .postCode("M1 1TT")
                .phone("01234567")
                .name(PROVIDER_NAME_VALID)
                .userId(USER_MEDIA_ID_VALID)
                .email("provider@test.com")
                .build();
    }
    public static UserForm buildUserFormValid() {
        return UserForm.builder()
                .mediaId(USER_MEDIA_ID_VALID)
                .mediaType("Facebook")
                .email(USER_EMAIL)
                .name("test test")
                .initials("TT")
                .password("pass")
                .build();
    }

    public static RecommendationForm buildRecommendationForm(String providerId) {
        return RecommendationForm.builder()
                .userId(USER_MEDIA_ID_VALID)
                .providerId(providerId)
                .score(5)
                .comments("test")
                .build();
    }


    public static String getJsonFromFile(String filePath) throws Exception {
        File file = ResourceUtils.getFile("classpath:__files/" + filePath);
        return Files.readString(file.toPath());
    }

    public static Object getObjectFromFile(String filePath, Class clazz) throws Exception {
        String json = getJsonFromFile(filePath);
        return mapper.readValue(json, clazz);
    }

    public static FacebookResponse getFacebookSuccess() throws Exception {
        return (FacebookResponse)
                getObjectFromFile("facebook/success.json", FacebookResponse.class);
    }

    static void addGetStub(WireMockServer wireMockServer, String uri, String json, int status) {
        wireMockServer.stubFor(
                get(uri)
                        .willReturn(
                                aResponse()
                                        .withStatus(status)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(json)));
    }

    public static void addGetStub(WireMockServer wireMockServer, String uri, String json) {
        addGetStub(wireMockServer, uri, json, 200);
    }
    public static void addGetStub400(WireMockServer wireMockServer, String uri, String json) {
        addGetStub(wireMockServer, uri, json, 400);
    }

    public static void addUserIntoSecurityContext(User user) {
        UserAuthentication auth = new UserAuthentication(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    public static void addUserIntoSecurityContext() {
        addUserIntoSecurityContext(buildUserValid());
    }

}
