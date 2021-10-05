package com.recomendationapi.controller;

import com.recomendationapi.form.UserForm;
import com.recomendationapi.model.User;
import com.recomendationapi.repository.UserRepository;
import com.recomendationapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/user", produces = APPLICATION_JSON_VALUE)
public class UserController extends DefaultController<UserService, User, UserForm, UserRepository> {

  @Autowired
  public UserController(UserService service) {
    super(service);
  }
}
