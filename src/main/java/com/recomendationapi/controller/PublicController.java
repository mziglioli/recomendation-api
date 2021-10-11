package com.recomendationapi.controller;

import com.recomendationapi.form.LoginForm;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.recomendationapi.controller.DefaultController.LOG_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/public", produces = APPLICATION_JSON_VALUE)
public class PublicController {

  private RecommendationService service;

  @Autowired
  public PublicController(RecommendationService service) {
     this.service = service;
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public Mono<DefaultResponse> login(@Valid @RequestBody LoginForm form) {
    log.info(LOG_REQUEST, "add", form);
    return service.login(form);
  }
}