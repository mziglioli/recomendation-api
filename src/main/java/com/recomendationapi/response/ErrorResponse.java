package com.recomendationapi.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
