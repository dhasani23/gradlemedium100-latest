package com.gradlemedium100.dataaccess.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

/**
 * Abstract base entity class that provides common fields and functionality
 * for all entities in the application.
 * 
 * This class implements common fields like:
 * - Unique identifier
 * - Created timestamp
 * - Last updated timestamp
 * - Version for optimistic locking
 */
@MappedSuperclass
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    
    /**
     * Default constructor for BaseEntity.
     */
    protected BaseEntity() {
        // Protected constructor to prevent direct instantiation
        // but allow subclasses to call it
    }
    
    /**
     * Returns the unique identifier of this entity.
     * 
     * @return The entity ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier for this entity.
     * This should typically only be called by the persistence provider.
     * 
     * @param id The entity ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Returns the timestamp when this entity was created.
     * 
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the timestamp when this entity was created.
     * This should typically only be called by the persistence provider.
     * 
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Returns the timestamp when this entity was last updated.
     * 
     * @return The last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the timestamp when this entity was last updated.
     * This should typically only be called by the persistence provider.
     * 
     * @param updatedAt The update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Returns the version number of this entity, used for optimistic locking.
     * 
     * @return The entity version
     */
    public Long getVersion() {
        return version;
    }
    
    /**
     * Sets the version number of this entity.
     * This should typically only be called by the persistence provider.
     * 
     * @param version The version number to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    
    /**
     * Lifecycle callback that is automatically called before the entity is persisted.
     * Sets the createdAt and updatedAt timestamps to the current time.
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        
        // FIXME: Consider using a centralized timestamp provider for consistent time across the application
        
        if (this.version == null) {
            this.version = 1L;
        }
    }
    
    /**
     * Lifecycle callback that is automatically called before the entity is updated.
     * Updates the updatedAt timestamp to the current time.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        
        // TODO: Add audit logging for entity changes
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        
        BaseEntity that = (BaseEntity) o;
        
        // Entities are equal if they have the same ID and ID is not null
        return id != null && id.equals(that.getId());
    }
    
    @Override
    public int hashCode() {
        // Use a prime number for better hash distribution
        return id != null ? id.hashCode() * 31 : super.hashCode();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}