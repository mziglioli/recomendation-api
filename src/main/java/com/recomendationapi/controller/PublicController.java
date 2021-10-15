package com.recomendationapi.controller;

import com.recomendationapi.form.LoginForm;
import com.recomendationapi.response.DefaultResponse;
import com.recomendationapi.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
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
  public DefaultResponse login(HttpServletResponse response, @Valid @RequestBody LoginForm form, BindingResult bindingResult) throws BindException {
    log.info(LOG_REQUEST, "login", form);
    if(bindingResult.hasErrors()){
      throw new BindException(bindingResult);
    }
    return service.login(response, form);
  }

  // TODO remove this
  @GetMapping("/init")
  @ResponseStatus(HttpStatus.OK)
  public DefaultResponse init() {
    service.initDb();
    return DefaultResponse.builder()
            .success(true)
            .build();
  }
}