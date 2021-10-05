package com.recomendationapi.service;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.form.UserForm;
import com.recomendationapi.model.Entity;
import com.recomendationapi.model.User;
import com.recomendationapi.repository.UserRepository;
import com.recomendationapi.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Transactional
@Service
public class UserService extends DefaultService<User, UserRepository> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }

    public Mono<User> getUserByMediaId(String mediaId) {
        return repository.findUserByMediaId(mediaId).defaultIfEmpty(new User());
    }

    public Mono<DefaultResponse> create(UserForm form) {
        User entity = form.convertToEntity();
        return getUserByMediaId(form.getMediaId())
            .map(user -> validateAndSave(entity, user));
    }
}
