package com.recomendationapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.form.UserForm;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(of = {"id", "email", "mediaId", "mediaType", "roles", "password"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class User extends Entity implements UserDetails {
 
    @Id
    private String id;
    private String name;
    private String initials;
    private String email;
    private String mediaId;
    private String mediaType;
    private List<String> roles;
    private String password;
    private List<Recommendation> recommendations;

    @Transient
    @JsonIgnore
    public void addRecommendation(Recommendation recommendation) {
        if (recommendations == null) {
            recommendations = new ArrayList<>();
        }
        recommendations.removeIf(r -> StringUtils.equals(recommendation.getUserId(), r.getUserId()));
        recommendations.add(recommendation);
    }

    @Transient
    @JsonIgnore
    public void addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.removeIf(r -> StringUtils.equals(r, role));
        roles.add(role);
    }

    @Transient
    @Override
    public void modify(DefaultForm defaultForm) {
        UserForm form = (UserForm) defaultForm;
        name = form.getName();
        email = form.getEmail();
        mediaId = form.getMediaId();
        mediaType = form.getMediaType();
        password = form.getPassword();
        initials = form.getInitials();
        roles = List.of("ROLE_USER");
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles != null) {
            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return mediaId;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return active;
    }
}