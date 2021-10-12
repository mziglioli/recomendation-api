package com.recomendationapi.service;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.model.Entity;
import com.recomendationapi.model.User;
import com.recomendationapi.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public abstract class DefaultService<T extends Entity, R extends MongoRepository<T, String>> {

    protected R repository;

    DefaultService(R repository) {
        this.repository = repository;
    }

    public Long count() {
        log.info("count");
        return repository.count();
    }

    public List<T> getAll() {
        log.info("getAll");
        return repository.findAll();
    }
    public Page<T> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }
    public T getById(String id) {
        log.info("getById");
        return repository.findById(id).orElse(null);
    }

    T save(T entity) {
        log.info("prepare to save");
        if (null != entity.getId()) {
            return update(entity);
        } else {
            log.info("save: pending");
            beforeInsert(entity);
            return repository.save(entity);
        }
    }
    public T save(DefaultForm form) {
        T entity = (T) form.convertToEntity();
        return save(entity);
    }
    T update(T entity) {
        log.info("update: pending");
        beforeUpdate(entity);
        return repository.save(entity);
    }
    public T update(String id, DefaultForm form) {
        T entity = getById(id);
        if (entity == null) {
            throw new RuntimeException("entity do not exists");
        }
        entity.modify(form);
        return update(entity);
    }

    T delete(T entity) {
        beforeDelete(entity);
        return repository.save(entity);
    }
    public T delete(String id) {
        log.info("deleteWait: start");
        T entity = getById(id);
        if (entity == null) {
            throw new RuntimeException("entity do not exists");
        }
        log.info("deleteWait: pending");
        return delete(entity);
    }

    void beforeInsert(T entity) {
        entity.setCreatedBy(getAuthenticatedUserId());
        entity.setCreatedDate(getTime());
        entity.setActive(true);
    }
    void beforeUpdate(T entity) {
        entity.setUpdatedBy(getAuthenticatedUserId());
        entity.setUpdatedDate(getTime());
        entity.setActive(true);
    }
    void beforeDelete(T entity) {
        beforeUpdate(entity);
        entity.setActive(false);
    }
    public String getAuthenticatedUserId() {
        return "0";
    }

    public User getAuthenticatedUser() throws AccessDeniedException {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context != null) {
                Authentication auth = context.getAuthentication();
                if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                    return (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
                }
            }
            throw new AccessDeniedException("user not found in context");
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }
    LocalDateTime getTime() {
        return LocalDateTime.now();
    }

    public DefaultResponse validateAndSave(T entity, T entityDb) {
        DefaultResponse response = DefaultResponse.builder().build();
        try {
            T entitySaved = validateAndSaveEntity(entity, entityDb);
            response.setSuccess(true);
            response.setData(entitySaved);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return response;
    }

    public T validateAndSaveEntity(T entity, T entityDb) throws RuntimeException {
        if (isNotEmpty(entityDb.getId())) {
            // entity exists
            if (entityDb.isActive()) {
                throw new RuntimeException("Entity already exists");
            } else {
                // entity is not active
                throw new RuntimeException("Entity already exists but not active");
            }
        } else {
            entity = save(entity);
            return entity;
        }
    }

}
