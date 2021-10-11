package com.recomendationapi.response;

import lombok.*;

@Setter
@Getter
@ToString(of = {"facebook", "token"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {

    private FacebookResponse facebook;

    private String token;
}