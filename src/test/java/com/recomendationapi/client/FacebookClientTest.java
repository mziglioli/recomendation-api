package com.recomendationapi.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.recomendationapi.WireMockInitializer;
import com.recomendationapi.response.FacebookResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import static com.recomendationapi.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = { WireMockInitializer.class })
public class FacebookClientTest {

  @Autowired
  private WireMockServer wireMockServer;

  @Autowired
  private FacebookClient facebookClient;

  @AfterEach
  public void afterEach() {
    wireMockServer.resetAll();
  }

  @BeforeEach
  public void beforeEach() throws Exception {
    String success = getJsonFromFile("facebook/success.json");
    addGetStub(wireMockServer, URI_ME + "123", success);

    String error = getJsonFromFile("facebook/error.json");
    addGetStub400(wireMockServer, URI_ME + "999", error);

  }

  @Test
  @DisplayName("Should return a valid facebook response when valid token")
  void test__valid() {
    FacebookResponse response = facebookClient.getMe("123");
    assertNotNull(response);
    assertEquals("123456789120000", response.getId());
  }

  @Test
  @DisplayName("Should return an new empty facebook response when invalid token")
  void test__invalid() {
    FacebookResponse response = facebookClient.getMe("999");
    assertNotNull(response);
    assertNull(response.getId());
  }
}
