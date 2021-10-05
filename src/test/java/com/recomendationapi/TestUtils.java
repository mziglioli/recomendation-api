package com.recomendationapi;

import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

    public final static String USER_EMAIL = "test@test.com";
    public final static String USER_MEDIA_ID_VALID = "123_Face";
    public final static String USER_MEDIA_ID_INVALID = "0000000";
    public final static String PROVIDER_NAME_VALID = "Test Provider";

    public static User buildUserValid() {
        return User.builder()
                .mediaId(USER_MEDIA_ID_VALID)
                .mediaType("Facebook")
                .email(USER_EMAIL)
                .name("test")
                .id("123")
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

    public static RecommendationForm buildRecommendationForm(String providerId) {
        return RecommendationForm.builder()
                .userId(USER_MEDIA_ID_VALID)
                .providerId(providerId)
                .score(5)
                .comments("test")
                .build();
    }


}
