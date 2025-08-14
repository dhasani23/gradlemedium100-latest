package com.gradlemedium100.dataaccess.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining common CRUD operations for all entities.
 * This interface provides a standard set of data access methods that should be 
 * implemented by concrete repository classes.
 *
 * @param <T> the domain entity type managed by this repository
 */
public interface BaseRepository<T> {

    /**
     * Find an entity by its ID.
     *
     * @param id the ID of the entity to find
     * @return an Optional containing the entity if found, or empty if not found
     */
    Optional<T> findById(Long id);

    /**
     * Retrieve all entities of the managed type.
     *
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Save or update an entity.
     * If the entity already exists (has an ID), it will be updated.
     * Otherwise, a new entity will be created.
     *
     * @param entity the entity to save or update
     * @return the saved entity, possibly with generated ID or other properties
     */
    T save(T entity);

    /**
     * Delete an entity.
     *
     * @param entity the entity to delete
     * @throws IllegalArgumentException if entity is null or has no ID
     */
    void delete(T entity);

    /**
     * Delete an entity by ID.
     *
     * @param id the ID of the entity to delete
     * @throws IllegalArgumentException if ID is null
     */
    void deleteById(Long id);

    /**
     * Count all entities of the managed type.
     *
     * @return the number of entities
     */
    long count();
    
    // TODO: Add batch operations for performance optimization
    
    // FIXME: Consider adding transaction support for multi-operation scenarios
}