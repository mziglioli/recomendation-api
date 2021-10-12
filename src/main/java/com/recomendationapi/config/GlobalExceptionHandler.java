package com.recomendationapi.config;

import com.recomendationapi.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity handleGlobalException(HttpServletRequest request, WebRequest webRequest, Exception ex) throws Exception {
        log.info("aqui:handleGlobalException");
        logException(ex, request);
        return super.handleException(ex, webRequest);
    }

    public ResponseEntity handle(HttpServletRequest request, WebRequest webRequest, Exception ex, HttpStatus httpStatus) {
        ErrorResponse error = buildErrorResponse(request.getRequestURI(), httpStatus, ex.getMessage());
        logException(ex, request);
        return super.handleExceptionInternal(ex, error, new HttpHeaders(), httpStatus, webRequest);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity handAuthenticationCredentialsException(HttpServletRequest request, WebRequest webRequest, AuthenticationCredentialsNotFoundException ex) {
        log.info("aqui:AuthenticationCredentialsNotFoundException");
        return handle(request, webRequest, ex, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAccessDenied(HttpServletRequest request, WebRequest webRequest, Exception ex) {
        log.info("aqui:handleAccessDenied");
        return handle(request, webRequest, ex, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        log.info("aqui:handleBindException");
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        return handle(request, webRequest, ex, HttpStatus.BAD_REQUEST);
    }

    private void logException(Exception ex, HttpServletRequest request) {
        log.error("Exception: method:{}, uri:{}, error:{}", request.getMethod(), request.getRequestURI(), ex);
    }

    private ErrorResponse buildErrorResponse(String path, HttpStatus status, String message) {
       return ErrorResponse.builder()
                .timestamp(Instant.now().toEpochMilli())
                .status(status.value())
                .error(status.name())
                .path(path)
                .message(message).build();
    }
}