package com.gradlemedium100.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base class for all DTOs with common fields and methods.
 * Provides basic functionality and properties shared by all data transfer objects
 * in the application.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseDTO implements Serializable {

    /**
     * Serial version UID for serialization compatibility across different versions
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the DTO
     */
    private String id;

    /**
     * Timestamp when the entity was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated
     */
    private LocalDateTime updatedAt;

    /**
     * Default constructor
     */
    public BaseDTO() {
        // Default constructor required for JSON/XML serialization
    }

    /**
     * Constructor with ID
     * 
     * @param id unique identifier for the DTO
     */
    public BaseDTO(String id) {
        this.id = id;
        // FIXME: Consider initializing timestamps here to ensure they're never null
    }

    /**
     * Gets the ID of the DTO
     * 
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the DTO
     * 
     * @param id the unique identifier to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the creation timestamp
     * 
     * @return the timestamp when the entity was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp
     * 
     * @param createdAt the timestamp to set as creation time
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp
     * 
     * @return the timestamp when the entity was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp
     * 
     * @param updatedAt the timestamp to set as last update time
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // TODO: Add methods for automated timestamp management (e.g., prePersist, preUpdate)
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseDTO baseDTO = (BaseDTO) o;
        
        return id != null ? id.equals(baseDTO.id) : baseDTO.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}