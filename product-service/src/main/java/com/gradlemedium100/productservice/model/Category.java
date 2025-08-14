package com.gradlemedium100.productservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class representing a product category.
 * 
 * This class provides functionality for hierarchical categories where a category 
 * can have a parent category, creating a tree structure for product organization.
 */
public class Category {

    private Long id;
    private String name;
    private String description;
    private Category parentCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true; // Default to active
    private List<Product> products = new ArrayList<>();

    /**
     * Default constructor for Category.
     */
    public Category() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields for Category.
     *
     * @param name        the name of the category
     * @param description the description of the category
     */
    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor with all fields for Category except timestamps.
     *
     * @param id             the unique identifier
     * @param name           the name of the category
     * @param description    the description of the category
     * @param parentCategory the parent category, if any
     */
    public Category(Long id, String name, String description, Category parentCategory) {
        this(name, description);
        this.id = id;
        this.parentCategory = parentCategory;
    }

    /**
     * Gets the category ID.
     *
     * @return the unique identifier of this category
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the category ID.
     *
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the category name.
     *
     * @return the name of this category
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the category name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            // FIXME: Consider throwing an IllegalArgumentException for null or empty names
            this.name = name;
        } else {
            this.name = name.trim();
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the category description.
     *
     * @return the description of this category
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the category description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the parent category.
     *
     * @return the parent category of this category
     */
    public Category getParentCategory() {
        return parentCategory;
    }

    /**
     * Sets the parent category.
     *
     * @param parentCategory the parent category to set
     */
    public void setParentCategory(Category parentCategory) {
        // Prevent circular references in category hierarchy
        if (this.equals(parentCategory)) {
            // TODO: Add proper logging instead of printing to console
            System.err.println("Cannot set a category as its own parent");
            return;
        }
        
        // Check for circular references through ancestors
        Category ancestor = parentCategory;
        while (ancestor != null) {
            if (this.equals(ancestor)) {
                // TODO: Add proper logging instead of printing to console
                System.err.println("Circular reference detected in category hierarchy");
                return;
            }
            ancestor = ancestor.getParentCategory();
        }
        
        this.parentCategory = parentCategory;
        this.updatedAt = LocalDateTime.now();
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
     * @param createdAt the creation timestamp to set
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
     * @param updatedAt the last updated timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets whether this category is active.
     *
     * @return true if this category is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets whether this category is active.
     *
     * @param active true to set this category as active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the products in this category.
     *
     * @return list of products in this category
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Sets the products in this category.
     *
     * @param products list of products to set for this category
     */
    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    /**
     * Updates the timestamp to the current time.
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if this category has a parent.
     *
     * @return true if this category has a parent category, false otherwise
     */
    public boolean hasParent() {
        return parentCategory != null;
    }

    /**
     * Gets the root category by traversing up the hierarchy.
     *
     * @return the root category of the hierarchy this category belongs to
     */
    public Category getRootCategory() {
        if (parentCategory == null) {
            return this;
        }
        
        Category current = this;
        while (current.getParentCategory() != null) {
            current = current.getParentCategory();
        }
        return current;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(description.length(), 20)) + "..." : null) + '\'' +
                ", parentCategory=" + (parentCategory != null ? parentCategory.getId() + ":" + parentCategory.getName() : null) +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}