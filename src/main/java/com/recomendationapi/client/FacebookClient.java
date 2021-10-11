package com.bookinggo.web.api.chat.engine.multilingual;

import static com.bookinggo.web.api.chat.common.util.LoggingUtils.START_CONFIG;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.bookinggo.web.api.chat.common.exception.ErrorResponse;
import com.bookinggo.web.api.chat.translate.TranslateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MultilingualAdapter {

  private final WebClient webClient;
  private final MultilingualConfig config;

  @Autowired
  public MultilingualAdapter(
      MultilingualConfig config,
      WebClient.Builder webClientBuilder,
      ClientHttpConnector connector) {
    this.config = config;
    this.webClient =
        webClientBuilder.baseUrl(config.getBaseUrl()).clientConnector(connector).build();
    log.info(START_CONFIG, "Multilingual", config.getBaseUrl());
  }

  public Mono<MultilingualResponse> translate(MultilingualForm form) {
    log.info("Multilingual Translate: {}", form.toString());
    return webClient
        .post()
        .uri(config.getTranslateUrl())
        .contentType(APPLICATION_JSON)
        .bodyValue(form)
        .retrieve()
        .onStatus(
            HttpStatus::is4xxClientError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ErrorResponse.class)
                    .map(
                        body ->
                            new TranslateException(
                                "Multilingual-Api 400 error: " + body.getMessage())))
        .onStatus(
            HttpStatus::is5xxServerError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ErrorResponse.class)
                    .map(
                        body ->
                            new TranslateException(
                                "Multilingual-Api 500 error: " + body.getMessage())))
        .bodyToMono(MultilingualResponse.class);
  }
}
