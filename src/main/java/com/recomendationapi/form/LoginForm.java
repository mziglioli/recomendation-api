package com.recomendationapi.form;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm extends DefaultForm {

    @NotEmpty(message = "validator.invalid.mediaId")
    private String mediaId;

    @NotEmpty(message = "validator.invalid.mediaToken")
    private String mediaToken;
}
