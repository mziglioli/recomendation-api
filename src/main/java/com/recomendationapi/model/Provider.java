package com.recomendationapi.model;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.form.ProviderForm;
import com.recomendationapi.form.UserForm;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(of = {"id", "email", "name", "address", "city", "postCode"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Provider extends Entity {
 
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String email;
    private String address;
    private String city;
    private String postCode;
    private String phone;
    private String creatorId;
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
        ProviderForm form = (ProviderForm) defaultForm;
        name = form.getName().trim().toUpperCase();
        email = form.getEmail();
        address = form.getAddress();
        city = form.getCity();
        postCode = form.getPostCode();
        phone = form.getPhone();
        creatorId = form.getUserId();
    }
}