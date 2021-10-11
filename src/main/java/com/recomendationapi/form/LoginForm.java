package com.recomendationapi.form;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFindForm extends DefaultForm {

    @NotEmpty(message = "validator.invalid.userIds")
    private List<String> userIds;
}
