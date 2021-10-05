package com.recomendationapi.form;

import com.recomendationapi.model.Recommendation;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationForm extends DefaultForm {

    @NotEmpty(message = "validator.invalid.userId")
    private String userId;

    @NotEmpty(message = "validator.invalid.providerId")
    private String providerId;

    @NotEmpty(message = "validator.invalid.score")
    @Min(value = 0, message = "validator.invalid.score")
    @Max(value = 5, message = "validator.invalid.score")
    private int score;

    @Size(max = 1000, message = "validator.invalid.comments")
    private String comments;

    public Recommendation buildRecommendation() {
        return Recommendation.builder()
                .score(score)
                .comments(comments)
                .providerId(providerId)
                .userId(userId)
                .build();
    }
}
