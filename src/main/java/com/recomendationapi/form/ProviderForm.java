package com.recomendationapi.form;

import com.recomendationapi.annotation.ValidEmail;
import com.recomendationapi.model.Provider;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderForm extends DefaultForm {

    @NotEmpty(message = "validator.invalid.user")
    private String userId;

    @NotEmpty(message = "validator.invalid.name")
    private String name;

    @ValidEmail
    private String email;

    private String address;
    private String city;
    private String postCode;
    private String phone;

    @Override
    public Provider convertToEntity() {
        return Provider.builder()
                .name(name != null ? name.replaceAll("\\s+","").toUpperCase() : "")
                .email(email)
                .address(address)
                .city(city)
                .postCode(postCode)
                .phone(phone)
                .creatorId(userId)
                .build();
    }
}
