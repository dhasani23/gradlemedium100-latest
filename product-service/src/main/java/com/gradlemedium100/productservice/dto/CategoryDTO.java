package com.gradlemedium100.productservice.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Category entity.
 * This class is used to transfer category data between different layers of the application.
 * It contains all necessary fields to represent a category including its hierarchical relationship.
 */
public class CategoryDTO {
    
    private Long id;
    private String name;
    private String description;
    private Long parentCategoryId;
    private String parentCategoryName;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int productCount;
    
    /**
     * Default constructor for CategoryDTO.
     */
    public CategoryDTO() {
        // Default constructor required for serialization/deserialization
        this.active = true; // Default to active
    }
    
    /**
     * Constructs a CategoryDTO with the specified parameters.
     *
     * @param id The unique identifier for the category
     * @param name The name of the category
     * @param description The detailed description of the category
     */
    public CategoryDTO(Long id, String name, String description) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    /**
     * Constructs a CategoryDTO with the specified parameters including parent category details.
     *
     * @param id The unique identifier for the category
     * @param name The name of the category
     * @param description The detailed description of the category
     * @param parentCategoryId The ID of the parent category
     * @param parentCategoryName The name of the parent category
     */
    public CategoryDTO(Long id, String name, String description, Long parentCategoryId, String parentCategoryName) {
        this(id, name, description);
        this.parentCategoryId = parentCategoryId;
        this.parentCategoryName = parentCategoryName;
    }
    
    /**
     * Gets the category ID.
     *
     * @return The unique identifier for the category
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the category ID.
     *
     * @param id The unique identifier for the category
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the category name.
     *
     * @return The name of the category
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the category name.
     *
     * @param name The name of the category
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the category description.
     *
     * @return The detailed description of the category
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the category description.
     *
     * @param description The detailed description of the category
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the parent category ID.
     *
     * @return The ID of the parent category
     */
    public Long getParentCategoryId() {
        return parentCategoryId;
    }
    
    /**
     * Sets the parent category ID.
     *
     * @param parentCategoryId The ID of the parent category
     */
    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
    
    /**
     * Gets the parent category name.
     *
     * @return The name of the parent category
     */
    public String getParentCategoryName() {
        return parentCategoryName;
    }
    
    /**
     * Sets the parent category name.
     *
     * @param parentCategoryName The name of the parent category
     */
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }
    
    /**
     * Determines if this is a root category (no parent).
     * 
     * @return true if this is a root category, false otherwise
     */
    public boolean isRootCategory() {
        return parentCategoryId == null;
    }
    
    /**
     * Gets the active status of the category.
     * 
     * @return true if the category is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the active status of the category.
     * 
     * @param active true to mark the category as active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the timestamp when this category was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt the timestamp when this category was created
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last updated timestamp.
     * 
     * @return the timestamp when this category was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last updated timestamp.
     * 
     * @param updatedAt the timestamp when this category was last updated
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the number of products in this category.
     * 
     * @return the number of products in this category
     */
    public int getProductCount() {
        return productCount;
    }
    
    /**
     * Sets the number of products in this category.
     * 
     * @param productCount the number of products in this category
     */
    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
    
    @Override
    public String toString() {
        return "CategoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parentCategoryId=" + parentCategoryId +
                ", parentCategoryName='" + parentCategoryName + '\'' +
                ", active=" + active +
                ", productCount=" + productCount +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CategoryDTO that = (CategoryDTO) o;
        
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}