package com.recomendationapi.controller;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.model.Entity;
import com.recomendationapi.service.DefaultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public abstract class DefaultController<S extends DefaultService<E, R>, E extends Entity, F extends DefaultForm, R extends MongoRepository<E, String>> {

  protected final S service;
  public static final String LOG_REQUEST = "Request-{}: {}";
  public static final String LOG_REQUEST_ERROR = "Request-Error-{}: {}";

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public E add(@Valid @RequestBody F form, BindingResult bindingResult) throws BindException {
    log.info(LOG_REQUEST, "add", form);
    if(bindingResult.hasErrors()){
      throw new BindException(bindingResult);
    }
    return service.save(form);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public E disable(@PathVariable String id) {
    log.info(LOG_REQUEST, "delete", id);
    if(isBlank(id)) {
      throw new RuntimeException("Invalid id to disable");
    }
    return service.delete(id);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)

  public E update(@PathVariable String id, @Valid @RequestBody F form, BindingResult bindingResult) throws BindException {
    log.info(LOG_REQUEST, "update", id);
    if(isBlank(id) || isBlank(form.getId()) || !id.equals(form.getId())) {
      bindingResult.addError(new ObjectError("id", "Invalid id to update"));
    }
    if(bindingResult.hasErrors()){
      throw new BindException(bindingResult);
    }
    return service.update(id, form);
  }

  @GetMapping("/{id}")
  public E findById(@PathVariable String id) {
    log.info(LOG_REQUEST, "find:", id);
    return service.getById(id);
  }

  @GetMapping("/all")
  public List<E> findAll() {
    log.info(LOG_REQUEST, "find", "all");
    return service.getAll();
  }
}
