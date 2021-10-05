package com.recomendationapi.service;

import com.recomendationapi.form.DefaultForm;
import com.recomendationapi.model.Entity;
import com.recomendationapi.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public abstract class DefaultService<T extends Entity, R extends ReactiveMongoRepository<T, String>> {

    protected R repository;

    DefaultService(R repository) {
        this.repository = repository;
    }

    public Mono<Long> count() {
        log.info("count");
        return repository.count();
    }

    public Flux<T> getAll() {
        log.info("getAll");
        return repository.findAll();
    }
    public Mono<T> getById(String id) {
        log.info("getById");
        return repository.findById(id);
    }

    Mono<T> save(T entity) {
        log.info("prepare to save");
        if (null != entity.getId()) {
            return update(entity);
        } else {
            log.info("save: pending");
            beforeInsert(entity);
            return repository.save(entity);
        }
    }
    public Mono<T> save(DefaultForm form) {
        T entity = (T) form.convertToEntity();
        return save(entity);
    }
    public Mono<T> saveWait(DefaultForm form) {
        log.info("saveWait: start");
        Mono<T> res = save(form);
        log.info("saveWait: saved");
        return res;
    }
    Mono<T> update(T entity) {
        log.info("update: pending");
        beforeUpdate(entity);
        return repository.save(entity);
    }
    public Mono<T> updateWait(Entity entity) {
        log.info("updateWait: start");
        Mono<T> res = update((T) entity);
        log.info("updateWait: updated");
        return res;
    }
    public Mono<T> update(String id, DefaultForm form) {
        return getById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("entity do not exists")))
            .map(entity -> {
                // update the entity with form details
                entity.modify(form);
                updateWait(entity).subscribe();
                return entity;
            });
    }

    Mono<T> delete(T entity) {
        beforeDelete(entity);
        return repository.save(entity);
    }
    public Mono<T> delete(String id) {
        return repository.findById(id)
                .flatMap(this::delete);
    }
    public Mono<T> deleteWait(String id) {
        log.info("deleteWait: start");
        Mono<T> res = delete(id);
        log.info("deleteWait: pending");
        res.subscribe();
        log.info("deleteWait: subscribe");
        return res;
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
            // block so we have it refreshed in db
            entity = save(entity).block();
            return entity;
        }
    }

}
