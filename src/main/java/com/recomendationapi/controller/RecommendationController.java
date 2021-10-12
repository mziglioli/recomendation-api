package com.recomendationapi.controller;

import com.recomendationapi.form.RecommendationFindForm;
import com.recomendationapi.form.RecommendationForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.recomendationapi.controller.DefaultController.LOG_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@PreAuthorize("hasRole('USER')")
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
  public List<Provider> all() {
    log.info(LOG_REQUEST, "all");
    return service.getRecommendations();
  }

  @PostMapping("/")
  @ResponseStatus(HttpStatus.OK)
  public DefaultResponse add(@Valid @RequestBody RecommendationForm form, BindingResult bindingResult) throws BindException {
    log.info(LOG_REQUEST, "add", form);
    if(bindingResult.hasErrors()){
      throw new BindException(bindingResult);
    }
    return service.addRecommendation(form);
  }

  @PostMapping("/friends")
  @ResponseStatus(HttpStatus.OK)
  public List<Provider> friends(@Valid @RequestBody RecommendationFindForm form, BindingResult bindingResult) throws BindException {
    log.info(LOG_REQUEST, "add", form);
    if(bindingResult.hasErrors()){
      throw new BindException(bindingResult);
    }
    return service.getRecommendations(form);
  }
}