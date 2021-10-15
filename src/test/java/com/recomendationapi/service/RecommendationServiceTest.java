package com.recomendationapi.service;

import com.recomendationapi.exception.EntityNotFoundException;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.model.User;
import com.recomendationapi.response.DefaultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecommendationServiceTest {

  @Autowired
  RecommendationService service;
  @Autowired
  UserService userService;
  @Autowired
  ProviderService providerService;

  @BeforeEach
  public void beforeEach() {
    addUserIntoSecurityContext();
  }

  @Test
  @DisplayName("given a valid userid and providerid and valid recommendation should be inserted")
  void test__valid() {
    User user = buildUserValid();
    userService.save(user, "0");

    Provider provider = buildProviderValid();
    Provider providerDb = providerService.save(provider, "0");

    RecommendationForm form = buildRecommendationForm(providerDb.getId());

    DefaultResponse response = service.addRecommendation(form);
    assertNotNull(response);
    assertTrue(response.isSuccess());


    User userUpdated = userService.getUserByMediaId(USER_MEDIA_ID_VALID);
    assertNotNull(userUpdated);
    assertNotNull(userUpdated.getRecommendations());
    assertEquals(1, userUpdated.getRecommendations().size());


    Provider providerUpdated = providerService.getProviderById(providerDb.getId());
    assertNotNull(providerUpdated);
    assertNotNull(providerUpdated.getRecommendations());
    assertEquals(1, providerUpdated.getRecommendations().size());
  }

  @Test
  @DisplayName("given a invalid userId")
  void test__invalidUser() {
    User user = buildUserValid();
    userService.save(user, "0");

    Provider provider = buildProviderValid();
    Provider providerDb = providerService.save(provider, "0");

    RecommendationForm form = buildRecommendationForm(providerDb.getId());
    form.setUserId("any_wrong_id");
    Exception exception = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
      service.addRecommendation(form);
    });
    assertEquals("Error: user do not match logged user", exception.getMessage());
  }

  @Test
  @DisplayName("given a invalid providerId")
  void test__invalidProvider() {
    User user = buildUserValid();
    userService.save(user, "0");

    Provider provider = buildProviderValid();
    providerService.save(provider, "0");

    RecommendationForm form = buildRecommendationForm("any_provider_id");

    Exception exception = assertThrows(EntityNotFoundException.class, () -> {
      service.addRecommendation(form);
    });
    assertEquals("Error: provider not exists", exception.getMessage());
  }

  @Test
  @DisplayName("given a invalid userId and providerId")
  void test__invalidUserAndProvider() {
    User user = buildUserValid();
    userService.save(user, "0");

    Provider provider = buildProviderValid();
    providerService.save(provider, "0");

    RecommendationForm form = buildRecommendationForm("any_provider_id");
    Exception exception = assertThrows(EntityNotFoundException.class, () -> {
      service.addRecommendation(form);
    });
    assertEquals("Error: provider not exists", exception.getMessage());
  }

}
