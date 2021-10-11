package com.recomendationapi.config.security;

import com.recomendationapi.model.User;
import com.recomendationapi.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class ReactiveServiceAuthenticationFilter implements WebFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("ReactiveServiceAuthenticationFilter: init");
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        log.info("ReactiveServiceAuthenticationFilter: path:{}", path);

        if (path.contains("/recommendation")) {
            List<String> myToken = exchange.getRequest().getHeaders().get("my-token");
            log.info("ReactiveServiceAuthenticationFilter: myToken:{}", myToken);
            if (myToken != null && !myToken.isEmpty()) {
                User user = tokenService.getUser(myToken.get(0));
                log.info("ReactiveServiceAuthenticationFilter: user: {}", user.toString());
                UserAuthentication auth = new UserAuthentication(user);
                log.info("ReactiveServiceAuthenticationFilter: auth: {}", auth.toString());
                ReactiveSecurityContextHolder.withAuthentication(auth);
            }
        }

        return chain.filter(exchange);
    }
}