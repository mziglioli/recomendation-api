package com.recomendationapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recomendationapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {

  private JwtService jwtService;
  private ObjectMapper mapper;

  @Autowired
  public TokenService(JwtService jwtService, ObjectMapper mapper) {
    this.jwtService = jwtService;
    this.mapper = mapper;
  }

  public String createToken(User user) {
    try {
      log.info("createToken:pending");
      String json = jwtService.createToken(mapper.writeValueAsString(user));
      log.info("createToken:success");
      return json;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      log.error("createToken:error", e);
      return "";
    }
  }

  public User getUser(String token) {
    try {
      log.info("getUser:pending");
      String json = jwtService.decryptToken(token);
      User user = mapper.readValue(json, User.class);
      log.info("getUser:success");
      return user;
    } catch (Exception e) {
      e.printStackTrace();
      log.error("getUser:error", e);
      return null;
    }
  }
}
