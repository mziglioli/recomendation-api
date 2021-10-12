package com.recomendationapi.config.security;

import com.recomendationapi.model.User;
import com.recomendationapi.service.TokenService;
import com.recomendationapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;


@Slf4j
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        User user = tokenService.getUserFromCookie(request);
        if (user != null && isNotEmpty(user.getId())) {
            User dbUser = userService.getUserByMediaId(user.getMediaId());
            log.info("ServiceAuthenticationFilter: user: {}", dbUser.toString());
            UserAuthentication auth = new UserAuthentication(dbUser);
            log.info("ServiceAuthenticationFilter: auth: {}", auth.toString());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filter.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/public/") || request.getServletPath().startsWith("/private/");
    }
}