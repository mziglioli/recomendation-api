package com.recomendationapi.client;

import com.recomendationapi.response.FacebookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Component
@Slf4j
public class FacebookClient {

  @Value("${facebook.api.url.base}")
  private String baseUrl;

  @Value("${facebook.api.url.me}")
  private String meUrl;

  private final WebClient webClient;

  @Autowired
  public FacebookClient(WebClient.Builder webClientBuilder, ClientHttpConnector connector) {
      this.webClient = webClientBuilder.baseUrl(baseUrl).clientConnector(connector).build();
      log.info("FacebookClient: start config client: " + baseUrl);
  }

  public Mono<FacebookResponse> getMe(String token) {
    log.info("FacebookClient: authenticate - {}", token);
    return webClient
        .get()
        .uri(meUrl + "&access_token={token}", token)
        .retrieve()
        .bodyToMono(FacebookResponse.class)
        .doOnError(e -> log.error("FacebookClient:doOnError: getMe", e))
        .onErrorReturn(new FacebookResponse());
  }
}
