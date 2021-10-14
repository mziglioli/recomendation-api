package com.recomendationapi.controller;

import com.recomendationapi.form.UserForm;
import com.recomendationapi.model.User;
import com.recomendationapi.response.ErrorResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.List;

import static com.recomendationapi.TestUtils.USER_MEDIA_ID_VALID;
import static com.recomendationapi.TestUtils.buildUserFormValid;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends TestController {

  @Test
  @DisplayName("Should return 401 an authorized exception")
  void test__anAuthorizedGetAll() {
    restTemplate = new TestRestTemplate(restTemplateBuilder.rootUri("http://localhost:"+localPort));
    ResponseEntity<ErrorResponse> t = restTemplate.getForEntity("/private/user/all", ErrorResponse.class);
    assertEquals(401, t.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return users when authenticated")
  void test__authorizedGetAll() {
    restTemplate = buildAuth();
    ResponseEntity<List> t = restTemplate.getForEntity("/private/user/all", List.class);
    assertEquals(200, t.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should throw bind exception when null")
  void test__addErrorNull() {
    restTemplate = buildAuth();
    ResponseEntity<ErrorResponse> t = restTemplate.postForEntity("/private/user/", null, ErrorResponse.class);
    assertEquals(400, t.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should throw bind exception when form is invalid")
  void test__addErrorInvalid() {
    restTemplate = buildAuth();

    // empty form
    UserForm form = new UserForm();
    ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/private/user/", form, ErrorResponse.class);
    assertEquals(400, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("BAD_REQUEST", response.getBody().getError());
    assertTrue(response.getBody().getMessage().contains("mediaId"));
    assertTrue(response.getBody().getMessage().contains("password"));
    assertTrue(response.getBody().getMessage().contains("name"));
    assertTrue(response.getBody().getMessage().contains("initials"));
    assertTrue(response.getBody().getMessage().contains("mediaType"));

    form = buildUserFormValid();
    // only name missing
    form.setName("");
    response = restTemplate.postForEntity("/private/user/", form, ErrorResponse.class);
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("BAD_REQUEST", response.getBody().getError());
    assertTrue(response.getBody().getMessage().contains("name"));
    assertFalse(response.getBody().getMessage().contains("initials"));
    assertFalse(response.getBody().getMessage().contains("mediaType"));
    assertFalse(response.getBody().getMessage().contains("mediaId"));
    assertFalse(response.getBody().getMessage().contains("password"));
  }

  @Test
  @DisplayName("Should save a new user")
  void test__saveSuccess() {
    restTemplate = buildAuth();
    UserForm form = buildUserFormValid();
    ResponseEntity<User> response = restTemplate.postForEntity("/private/user/", form, User.class);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getId());
    assertEquals(USER_MEDIA_ID_VALID, response.getBody().getMediaId());
    assertTrue(response.getBody().isActive());
  }

  @Test
  @DisplayName("Should fail to update when form id and param id are different")
  void test__updateFail() {
    restTemplate = buildAuth();
    UserForm form = buildUserFormValid();
    form.setId("123456789");

    HttpEntity<UserForm> requestEntity = new HttpEntity<>(form);
    ResponseEntity<ErrorResponse> response = restTemplate.exchange("/private/user/999999999", HttpMethod.PUT, requestEntity, ErrorResponse.class);
    assertEquals(400, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
  }

  @Test
  @DisplayName("Should fail to update when update id do not exists")
  void test__updateFailIdNotExists() {
    restTemplate = buildAuth();
    UserForm form = buildUserFormValid();
    form.setId("999999999");

    HttpEntity<UserForm> requestEntity = new HttpEntity<>(form);

    ResponseEntity<ErrorResponse> response = restTemplate.exchange("/private/user/999999999", HttpMethod.PUT, requestEntity, ErrorResponse.class);
    assertEquals(400, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("entity do not exists", response.getBody().getMessage());
  }

  @Test
  @DisplayName("Should update success")
  void test__updateSuccess() {
    restTemplate = buildAuth();

    UserForm formSave = buildUserFormValid();
    formSave.setName("new save");
    ResponseEntity<User> response = restTemplate.postForEntity("/private/user/", formSave, User.class);
    assertEquals(200, response.getStatusCodeValue());

    User userDb = response.getBody();
    assertNotNull(userDb);


    UserForm form = buildUserFormValid();
    form.setId(userDb.getId());
    form.setName("my new name");

    HttpEntity<UserForm> requestEntity = new HttpEntity<>(form);
    ResponseEntity<User> responseUpdate = restTemplate.exchange("/private/user/" + userDb.getId(), HttpMethod.PUT, requestEntity, User.class);
    assertEquals(200, responseUpdate.getStatusCodeValue());
    assertNotNull(responseUpdate.getBody());
    assertEquals(userDb.getId(), responseUpdate.getBody().getId());
    assertEquals("my new name", responseUpdate.getBody().getName());
  }
}
