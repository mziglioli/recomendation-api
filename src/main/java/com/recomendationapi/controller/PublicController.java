package com.recomendationapi.controller;

import com.recomendationapi.form.LoginForm;
import com.recomendationapi.form.RecommendationFindForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.recomendationapi.controller.DefaultController.LOG_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/recommendation", produces = APPLICATION_JSON_VALUE)
public class RecommendationController {

  private RecommendationService service;

  @Autowired
  public RecommendationController(RecommendationService service) {
     this.service = service;
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public Mono<DefaultResponse> login(@Valid @RequestBody LoginForm form) {
    log.info(LOG_REQUEST, "add", form);
    return service.login(form);
  }

  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<Provider> all() {
    log.info(LOG_REQUEST, "all");
    return service.getRecommendations();
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public Mono<DefaultResponse> add(@Valid @RequestBody RecommendationForm form) {
    log.info(LOG_REQUEST, "add", form);
    return service.addRecommendation(form);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/friends")
  @ResponseStatus(HttpStatus.OK)
  public Flux<Provider> friends(@Valid @RequestBody RecommendationFindForm form) {
    log.info(LOG_REQUEST, "add", form);
    return service.getRecommendations(form);
  }
}