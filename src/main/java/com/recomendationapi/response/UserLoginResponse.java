package com.recomendationapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@ToString(of = {"id", "firstName", "lastName"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookResponse {

    private String id;

    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private FacebookFriendsResponse friends;

    public String getName() {
        return firstName + " " + lastName;
    }
    public String getInitials() {
        if (firstName == null || lastName == null) {
            return "";
        }
        return firstName.charAt(0) + " " + lastName.charAt(0);
    }

}