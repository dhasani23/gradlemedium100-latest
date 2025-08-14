package com.gradlemedium100.dataaccess.repository.impl;

import com.gradlemedium100.dataaccess.repository.BaseRepository;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository implementation providing common CRUD operations.
 * This implementation uses JPA EntityManager to perform database operations
 * and provides a reusable base for all entity repositories.
 *
 * @param <T> the entity type this repository manages
 */
@Transactional
public class BaseRepositoryImpl<T> implements BaseRepository<T> {

    /**
     * JPA EntityManager for database operations
     */
    protected final EntityManager entityManager;
    
    /**
     * Class type of the entity being managed
     */
    protected final Class<T> entityClass;

    /**
     * Constructor that initializes the repository with the entity manager and entity class.
     *
     * @param entityManager the JPA entity manager
     * @param entityClass the class of the entity being managed
     */
    public BaseRepositoryImpl(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    /**
     * Find an entity by its ID.
     *
     * @param id the ID of the entity to find
     * @return Optional containing the entity with the given ID, or empty if none found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    /**
     * Retrieve all entities of the managed type.
     *
     * @return a list of all entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rootEntry = cq.from(entityClass);
        CriteriaQuery<T> all = cq.select(rootEntry);
        
        TypedQuery<T> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }

    /**
     * Save or update an entity.
     *
     * @param entity the entity to save or update
     * @return the saved or updated entity
     * @throws IllegalArgumentException if entity is null
     */
    @Override
    @Transactional
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        // If the entity is detached, merge it; otherwise persist it
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            // FIXME: This is a simplistic approach; in a real-world scenario,
            // we would need more sophisticated handling of detached entities
            return entityManager.merge(entity);
        }
    }

    /**
     * Delete an entity.
     *
     * @param entity the entity to delete
     * @throws IllegalArgumentException if entity is null
     */
    @Override
    @Transactional
    public void delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        // Make sure the entity is managed before removing
        T managedEntity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
        entityManager.remove(managedEntity);
    }

    /**
     * Delete an entity by ID.
     *
     * @param id the ID of the entity to delete
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Count all entities of the managed type.
     *
     * @return the number of entities
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(entityClass)));
        
        return entityManager.createQuery(cq).getSingleResult();
    }
    
    /**
     * Get the entity manager.
     * 
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    /**
     * Get the entity class.
     * 
     * @return the entity class
     */
    protected Class<T> getEntityClass() {
        return entityClass;
    }
}