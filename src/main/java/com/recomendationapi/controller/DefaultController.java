package com.recomendationapi.controller;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.model.Entity;
import com.recomendationapi.service.DefaultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public abstract class DefaultController<S extends DefaultService<E, R>, E extends Entity, F extends DefaultForm, R extends ReactiveMongoRepository<E, String>> {

  protected final S service;
  public static final String LOG_REQUEST = "Request-{}: {}";
  public static final String LOG_REQUEST_ERROR = "Request-Error-{}: {}";

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public Mono<E> add(@Valid @RequestBody F form) {
    log.info(LOG_REQUEST, "add", form);
    return service.saveWait(form);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<E> disable(@PathVariable String id) {
    log.info(LOG_REQUEST, "delete", id);
    if(isBlank(id)) {
      throw new RuntimeException("Invalid id to disable");
    }
    return service.deleteWait(id);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<E> update(@PathVariable String id, @Valid @RequestBody Mono<F> form) {
    log.info(LOG_REQUEST, "update", id);
    return form.flatMap(myForm -> {
      if(isBlank(id) || isBlank(myForm.getId()) || !id.equals(myForm.getId())) {
        return Mono.error(new RuntimeException("Invalid id to update"));
      } else {
        return service.update(id, myForm);
      }}).onErrorResume(throwable -> {
        log.error(LOG_REQUEST_ERROR, "update", throwable);
        return Mono.error(new RuntimeException("Invalid id to update"));
      });
  }

  @GetMapping("/{id}")
  public Mono<E> findById(@PathVariable String id) {
    log.info(LOG_REQUEST, "find:", id);
    return service.getById(id);
  }

  @GetMapping("/all")
  public Flux<E> findAll() {
    log.info(LOG_REQUEST, "find", "all");
    return service.getAll();
  }
}
