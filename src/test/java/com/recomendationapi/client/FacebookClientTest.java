package com.bookinggo.web.api.chat.config;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EnableCaching
@AutoConfigureWebTestClient
@EnableAspectJAutoProxy
@ActiveProfiles(profiles = "test")
@AutoConfigureWireMock(port = 9999)
public class WireMockTestBase extends UtilsTestBase {

  @BeforeEach
  public void beforeStub() {
    WireMock.resetAllRequests();
  }

  protected void addPostStub(
      String uri, StringValuePattern requestBody, int status, String bodyFile) {
    stubFor(
        post(uri)
            .withRequestBody(requestBody)
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile(bodyFile)));
  }

  protected void addPutStub(
      String uri, StringValuePattern requestBody, int status, String bodyFile) {
    stubFor(
        put(uri)
            .withRequestBody(requestBody)
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withHeader("X-RC-Application", "webapp")
                    .withBodyFile(bodyFile)));
  }

  protected void addGetStub(String uri, int status, String bodyFile) {
    stubFor(
        get(uri)
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withHeader("X-RC-Application", "webapp")
                    .withBodyFile(bodyFile)));
  }
}
