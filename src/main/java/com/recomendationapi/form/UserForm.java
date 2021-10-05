package com.recomendationapi.form;

import com.recomendationapi.annotation.ValidEmail;
import com.recomendationapi.model.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForm extends DefaultForm {

    @NotEmpty(message = "validator.invalid.name")
    private String name;

    @ValidEmail
    private String email;

    @NotEmpty(message = "validator.invalid.mediaId")
    private String mediaId;

    @NotEmpty(message = "validator.invalid.mediaType")
    private String mediaType;

    @Override
    public User convertToEntity() {
        return User.builder()
                .id(id)
                .name(name != null ? name.replaceAll("\\s+","").toUpperCase() : "")
                .email(email)
                .mediaType(mediaType)
                .mediaId(mediaId)
                .build();
    }
}
