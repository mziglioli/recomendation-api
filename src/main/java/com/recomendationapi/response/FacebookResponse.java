package com.recomendationapi.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class DefaultResponse {

    private boolean success;
    private String error;
    private Object data;
}