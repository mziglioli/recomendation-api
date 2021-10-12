package com.recomendationapi.controller;

import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.model.Provider;
import com.recomendationapi.repository.ProviderRepository;
import com.recomendationapi.service.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/private/provider", produces = APPLICATION_JSON_VALUE)
public class ProviderController extends DefaultController<ProviderService, Provider, ProviderForm, ProviderRepository> {

  @Autowired
  public ProviderController(ProviderService service) {
    super(service);
  }
}