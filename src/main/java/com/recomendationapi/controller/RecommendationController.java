package com.recomendationapi.controller;

import com.recomendationapi.form.RecommendationFindForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.recomendationapi.controller.DefaultController.LOG_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
//@PreAuthorize("hasRole('USER')")
@RestController
@RequestMapping(value = "/recommendation", produces = APPLICATION_JSON_VALUE)
public class RecommendationController {

  private RecommendationService service;

  @Autowired
  public RecommendationController(RecommendationService service) {
     this.service = service;
  }

  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public Flux<Provider> all() {
    log.info(LOG_REQUEST, "all");
    return service.getRecommendations();
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public Mono<DefaultResponse> add(@Valid @RequestBody RecommendationForm form) {
    log.info(LOG_REQUEST, "add", form);
    return service.addRecommendation(form);
  }

  public void getCurrentUser() {
    log.info("getCurrentUser: init");
    ReactiveSecurityContextHolder.getContext()
            .map(sc -> {
              log.info("getCurrentUser: {}", sc.getAuthentication().toString());
              return null;
            });
  }

  @PostMapping("/friends")
  @ResponseStatus(HttpStatus.OK)
  public Flux<Provider> friends(@Valid @RequestBody RecommendationFindForm form) {
    log.info(LOG_REQUEST, "add", form);
    getCurrentUser();
    return service.getRecommendations(form);
  }
}