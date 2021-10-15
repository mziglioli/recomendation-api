package com.recomendationapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.response.ErrorResponse;
import com.recomendationapi.service.JwtService;
import com.recomendationapi.service.ProviderService;
import com.recomendationapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import java.util.List;
import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationControllerTest extends TestController {

  @MockBean
  private JwtService jwtService;
  @MockBean
  private ProviderService providerService;
  @MockBean
  private UserService userService;

  private ObjectMapper mapper = new ObjectMapper();


  @BeforeEach
  public void init() throws Exception {
    given(jwtService.decryptToken("test"))
            .willReturn(mapper.writeValueAsString(buildUserValid()));
    given(userService.getUserByMediaId(USER_MEDIA_ID_VALID)).willReturn(buildUserValid());
    given(userService.getUserByMediaId(USER_MEDIA_ID_INVALID)).willReturn(null);
    given(userService.getAuthenticatedUser()).willReturn(buildUserValid());

    given(providerService.getProvider(PROVIDER_NAME_VALID)).willReturn(buildProviderValid());
    given(providerService.getProvider(PROVIDER_NAME_INVALID)).willReturn(null);
    given(providerService.getProviderById(PROVIDER_ID_VALID)).willReturn(buildProviderValid());
    given(providerService.getProviderById(PROVIDER_ID_INVALID)).willReturn(null);
    given(providerService.getAll()).willReturn(List.of());
    given(providerService.getAllOrderByScore(null, 0, 100)).willReturn(new PageImpl<Provider>(List.of()));
  }

  @Test
  @DisplayName("Should return 403 when FORBIDDEN exception")
  void test__anAuthorizedGetAll() {
    restTemplate = new TestRestTemplate(restTemplateBuilder.rootUri("http://localhost:"+localPort));
    ResponseEntity<ErrorResponse> errorResponse = restTemplate.getForEntity("/recommendation/all", ErrorResponse.class);
    assertEquals(403, errorResponse.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 200 when auth cookie is in request on all recommendation")
  void test__successGetAll() {
    restTemplate = buildAuth();
    ResponseEntity<DefaultResponse> responseEntity = restTemplate.getForEntity("/recommendation/all", DefaultResponse.class);
    assertEquals(200, responseEntity.getStatusCodeValue());
    assertNotNull(responseEntity.getBody());
    assertTrue(responseEntity.getBody().isSuccess());
    assertNotNull(responseEntity.getBody().getData());
  }

  @Test
  @DisplayName("Should return 400 when form is invalid")
  void test__submit400() {
    restTemplate = buildAuth();
    RecommendationForm form = new RecommendationForm();
    ResponseEntity<DefaultResponse> responseEntity = restTemplate.postForEntity("/recommendation/", form, DefaultResponse.class);
    assertEquals(400, responseEntity.getStatusCodeValue());
  }

  @Test
  @DisplayName("Should return 400 when invalid provider")
  void test__submit400InvalidProvider() {
    restTemplate = buildAuth();
    RecommendationForm form = buildRecommendationForm(PROVIDER_ID_INVALID);
    ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/recommendation/", form, ErrorResponse.class);
    assertEquals(400, responseEntity.getStatusCodeValue());
    assertNotNull(responseEntity.getBody());
    assertEquals(400, responseEntity.getBody().getStatus());
    assertEquals("Error: provider not exists", responseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Should return 400 when invalid user not in db")
  void test__submit400InvalidUser() {
    restTemplate = buildAuth();
    RecommendationForm form = buildRecommendationForm(PROVIDER_ID_VALID);
    form.setUserId("test");
    ResponseEntity<ErrorResponse> responseEntity = restTemplate.postForEntity("/recommendation/", form, ErrorResponse.class);
    assertEquals(403, responseEntity.getStatusCodeValue());
    assertNotNull(responseEntity.getBody());
    assertEquals(403, responseEntity.getBody().getStatus());
    assertEquals("Error: user do not match logged user", responseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Should return 200 when form is invalid")
  void test__submit200() {
    restTemplate = buildAuth();
    RecommendationForm form = buildRecommendationForm(PROVIDER_ID_VALID);
    ResponseEntity<DefaultResponse> responseEntity = restTemplate.postForEntity("/recommendation/", form, DefaultResponse.class);
    assertEquals(200, responseEntity.getStatusCodeValue());
  }

}
