package com.recomendationapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;

class TestController {

  @LocalServerPort
  protected int localPort;

  @Autowired
  protected RestTemplateBuilder restTemplateBuilder;

  protected TestRestTemplate restTemplate;

  protected TestRestTemplate buildAuth() {
    return new TestRestTemplate(restTemplateBuilder
            .basicAuthentication("admin", "admin")
            .rootUri("http://localhost:"+localPort));
  }
}
