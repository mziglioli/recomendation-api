package com.recomendationapi.service;

import com.recomendationapi.model.User;
import com.recomendationapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Transactional
@Service
public class UserService extends DefaultService<User, UserRepository> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }

    public User getUserByMediaId(String mediaId) {
        return repository.findUserByMediaId(mediaId).orElse(new User());
    }

    public String getAuthenticatedUserId() {
        log.info("getAuthenticatedUserId: pending");
        User user = getAuthenticatedUser();
        if (user != null && isNotEmpty(user.getMediaId())) {
            log.info("getAuthenticatedUserId: found: {}", user.getMediaId());
            return user.getMediaId();
        } else {
            log.info("getAuthenticatedUserId: userNull");
        }
        return "0";
    }

    public User getAdmin() {
        return User.builder()
                .id("1")
                .password("admin")
                .initials("AD")
                .mediaType("admin")
                .mediaId("admin")
                .email("admin@admin.com")
                .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                .build();
    }

    public User getAuthenticatedUser() throws AccessDeniedException {
        UserDetails userDetails = getAuthenticatedUserDetails();

        // admin is not saved into db
        if ("admin".equals(userDetails.getUsername())) {
            return getAdmin();
        } else {
            log.info("getAuthenticatedUser: details: {}", userDetails.toString());
            return (User) userDetails;
        }
    }

    public UserDetails getAuthenticatedUserDetails() throws AccessDeniedException {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context != null) {
                Authentication auth = context.getAuthentication();
                if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                }
            }
            throw new AccessDeniedException("user not found in context");
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    @Override
    void beforeInsert(User entity, String userId) {
        super.beforeInsert(entity, userId);
        entity.setActive(true);
    }
}
