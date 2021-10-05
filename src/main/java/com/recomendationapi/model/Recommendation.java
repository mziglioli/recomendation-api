package com.recomendationapi.model;

import lombok.*;

@Getter
@Setter
@ToString(of = {"userId", "providerId", "score", "comments"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    private String userId;
    private String providerId;
    private int score;
    private String comments;
}