package com.recomendationapi.model;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.form.UserForm;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(of = {"id", "email", "mediaId", "mediaType"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class User extends Entity {
 
    @Id
    private String id;
    private String name;
    private String email;
    private String mediaId;
    private String mediaType;
    private List<Recommendation> recommendations;

    @Transient
    public void addRecommendation(Recommendation recommendation) {
        if (recommendations == null) {
            recommendations = new ArrayList<>();
        }
        recommendations.add(recommendation);
    }

    @Transient
    @Override
    public void modify(DefaultForm defaultForm) {
        UserForm form = (UserForm) defaultForm;
        name = form.getName();
        email = form.getEmail();
        mediaId = form.getMediaId();
        mediaType = form.getMediaType();
    }
}