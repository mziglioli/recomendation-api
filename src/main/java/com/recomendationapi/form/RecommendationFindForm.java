package com.recomendationapi.form;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(value = 0, message = "validator.invalid.page")
    @Max(value = 100, message = "validator.invalid.page")
    private int page;
    @Min(value = 5, message = "validator.invalid.size")
    @Max(value = 100, message = "validator.invalid.size")
    @Builder.Default
    private int size = 10;
}
