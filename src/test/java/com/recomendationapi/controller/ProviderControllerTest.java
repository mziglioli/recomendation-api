package com.recomendationapi.controller;

import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProviderControllerTest extends TestController {

  @Test
  @DisplayName("Should return 401 an authorized exception")
  void test__anAuthorizedGetAll() {
    restTemplate = new TestRestTemplate(restTemplateBuilder.rootUri("http://localhost:"+localPort));
    ResponseEntity<ErrorResponse> t = restTemplate.getForEntity("/private/provider/all", ErrorResponse.class);
    assertEquals(401, t.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return providers when authenticated")
  void test__authorizedGetAll() {
    restTemplate = buildAuth();
    ResponseEntity<List> t = restTemplate.getForEntity("/private/provider/all", List.class);
    assertEquals(200, t.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should throw bind exception when null")
  void test__addErrorNull() {
    restTemplate = buildAuth();
    ResponseEntity<ErrorResponse> t = restTemplate.postForEntity("/private/provider/", null, ErrorResponse.class);
    assertEquals(400, t.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should throw bind exception when form is invalid")
  void test__addErrorInvalid() {
    restTemplate = buildAuth();

    // empty form
    ProviderForm form = new ProviderForm();
    ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/private/provider/", form, ErrorResponse.class);
    assertEquals(400, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("BAD_REQUEST", response.getBody().getError());
    assertTrue(response.getBody().getMessage().contains("userId"));
    assertTrue(response.getBody().getMessage().contains("name"));

    form = buildProviderFormValid();
    // only name missing
    form.setName("");
    response = restTemplate.postForEntity("/private/provider/", form, ErrorResponse.class);
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("BAD_REQUEST", response.getBody().getError());
    assertTrue(response.getBody().getMessage().contains("name"));
    assertFalse(response.getBody().getMessage().contains("userId"));
    assertFalse(response.getBody().getMessage().contains("email"));
  }

  @Test
  @DisplayName("Should save a new provider")
  void test__saveSuccess() {
    restTemplate = buildAuth();
    ProviderForm form = buildProviderFormValid();
    ResponseEntity<Provider> response = restTemplate.postForEntity("/private/provider/", form, Provider.class);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    assertEquals(USER_MEDIA_ID_VALID, response.getBody().getCreatorId());
    // not active by default
    assertFalse(response.getBody().isActive());
  }
}
