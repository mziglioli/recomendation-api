package com.recomendationapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recomendationapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class TokenService {

  private JwtService jwtService;
  private ObjectMapper mapper;
  //seconds, minutes, hs, days
  public static final int COOKIE_TIME = (60 * 60 * 24 * 1);
  public static final String COOKIE_PATH = "/";
  public static final String COOKIE_AUTH_NAME = "X-RECOMMENDATION-TOKEN";


  @Autowired
  public TokenService(JwtService jwtService, ObjectMapper mapper) {
    this.jwtService = jwtService;
    this.mapper = mapper;
  }

  public void addCookie(HttpServletResponse response, User user) {
    String jwt = createToken(user);

    Cookie cookie = new Cookie(COOKIE_AUTH_NAME, jwt);
    cookie.setPath(COOKIE_PATH);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(COOKIE_TIME);
    response.addCookie(cookie);
    response.addHeader(COOKIE_AUTH_NAME, jwt);
  }

  public User getUserFromCookie(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, COOKIE_AUTH_NAME);
    if (cookie != null) {
      String token = cookie.getValue();
      log.info("getUserFromCookie:success: {}", token);
      if (token != null) {
        return getUser(token);
      }
    }
    log.info("getUserFromCookie:error: not found user");
    return null;
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
      return new User();
    }
  }
}
