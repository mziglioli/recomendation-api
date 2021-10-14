package com.recomendationapi.service;

import com.recomendationapi.exception.EntityNotFoundException;
import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.model.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

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

    public T activeById(String id, String userId) {
        log.info("getById");
        T entity = getById(id);
        if (entity == null) {
            throw new EntityNotFoundException("entity do not exists");
        }
        entity.setActive(true);
        return update(entity, userId);
    }

    public T save(T entity, String userId) {
        log.info("prepare to save by: {}", userId);
        if (null != entity.getId()) {
            return update(entity, userId);
        } else {
            log.info("save: pending");
            beforeInsert(entity, userId);
            return repository.save(entity);
        }
    }
    public T save(DefaultForm form, String userId) {
        T entity = (T) form.convertToEntity();
        return save(entity, userId);
    }
    T update(T entity, String userId) {
        log.info("update: pending by: {}", userId);
        beforeUpdate(entity, userId);
        return repository.save(entity);
    }
    public T update(String id, DefaultForm form, String userId) {
        T entity = getById(id);
        if (entity == null) {
            throw new EntityNotFoundException("entity do not exists");
        }
        entity.modify(form);
        return update(entity, userId);
    }

    T delete(T entity, String userId) {
        beforeDelete(entity, userId);
        return repository.save(entity);
    }
    public T delete(String id, String userId) {
        log.info("deleteWait: start by: {}", userId);
        T entity = getById(id);
        if (entity == null) {
            throw new EntityNotFoundException("entity do not exists");
        }
        log.info("deleteWait: pending");
        return delete(entity, userId);
    }

    void beforeInsert(T entity, String userId) {
        entity.setCreatedBy(userId);
        entity.setCreatedDate(getTime());
        entity.setActive(false);
    }
    void beforeUpdate(T entity, String userId) {
        entity.setUpdatedBy(userId);
        entity.setUpdatedDate(getTime());
    }
    void beforeDelete(T entity, String userId) {
        beforeUpdate(entity, userId);
        entity.setActive(false);
    }

    LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}
