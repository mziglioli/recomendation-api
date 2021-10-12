package com.recomendationapi.service;

import com.recomendationapi.model.User;
import com.recomendationapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
