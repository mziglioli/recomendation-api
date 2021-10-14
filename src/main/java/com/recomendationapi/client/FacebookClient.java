package com.recomendationapi.client;

import com.recomendationapi.response.FacebookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@Component
@Slf4j
public class FacebookClient {

  private final FacebookConfig config;
  private final RestTemplate webClient;

  @Autowired
  public FacebookClient(FacebookConfig config, RestTemplateBuilder builder) {
      this.config = config;
      this.webClient = builder.rootUri(config.getBaseUrl())
              .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
              .setConnectTimeout(Duration.ofMillis(5000))
              .build();
      log.info("FacebookClient: start config client: {}", config.getBaseUrl());
  }

  public FacebookResponse getMe(String token) {
      log.info("FacebookClient: authenticate - {}{}{}", config.getBaseUrl(), config.getMeUrl(), token);
      try {
          ResponseEntity<FacebookResponse> response = webClient.getForEntity(config.getMeUrl() + token, FacebookResponse.class);
          if (HttpStatus.OK.equals(response.getStatusCode())) {
              return response.getBody();
          }
          throw new Exception("fail to getMe: " + response.getStatusCode());
      } catch (Exception e) {
          log.error("FacebookClient:doOnError: getMe", e);
      }
      return new FacebookResponse();
  }
}
