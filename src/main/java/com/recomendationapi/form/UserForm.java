package com.recomendationapi.form;

import com.recomendationapi.annotation.ValidEmail;
import com.recomendationapi.model.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
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

    @NotEmpty(message = "validator.invalid.password")
    private String password;

    @NotEmpty(message = "validator.invalid.initials")
    private String initials;

    @Override
    public User convertToEntity() {
        return User.builder()
                .id(id)
                .name(name != null ? name.replaceAll("\\s+"," ").trim().toUpperCase() : "")
                .email(email)
                .mediaType(mediaType)
                .mediaId(mediaId)
                .password(password)
                .initials(initials)
                .roles(List.of("ROLES_USER"))
                .build();
    }
}
