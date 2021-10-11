package com.recomendationapi.client;

import com.recomendationapi.response.FacebookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Component
@Slf4j
public class FacebookClient {

  private final FacebookConfig config;
  private final WebClient webClient;

  @Autowired
  public FacebookClient(FacebookConfig config, WebClient.Builder webClientBuilder, ClientHttpConnector connector) {
      this.config = config;
      this.webClient = webClientBuilder
              .baseUrl(config.getBaseUrl())
              .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
              .clientConnector(connector)
              .build();
      log.info("FacebookClient: start config client: {}", config.getBaseUrl());
  }

  public Mono<FacebookResponse> getMe(String token) {
    log.info("FacebookClient: authenticate - {}{}{}", config.getBaseUrl(), config.getMeUrl(), token);
    return webClient
        .get()
        .uri(config.getMeUrl() + "{token}", token)
        .retrieve()
        .bodyToMono(FacebookResponse.class)
        .doOnError(e -> log.error("FacebookClient:doOnError: getMe", e))
        .onErrorReturn(new FacebookResponse());
  }
}
