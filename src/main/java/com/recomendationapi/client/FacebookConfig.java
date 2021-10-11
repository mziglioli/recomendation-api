package com.recomendationapi.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FacebookConfig {

  @Value("${facebook.api.url.base}")
  private String baseUrl;

  @Value("${facebook.api.url.me}")
  private String meUrl;
}
