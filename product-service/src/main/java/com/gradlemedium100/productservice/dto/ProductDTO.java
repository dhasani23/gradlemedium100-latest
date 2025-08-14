package com.gradlemedium100.productservice.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for Product entity.
 * This class is used to transfer product data between processes or across network boundaries,
 * particularly when communicating with the presentation layer.
 *
 * @since 1.0
 */
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private String sku;
    private String imageUrl;

    /**
     * Default constructor
     */
    public ProductDTO() {
        // Default constructor required for JSON serialization/deserialization
    }

    /**
     * Constructs a ProductDTO with essential fields
     *
     * @param id product identifier
     * @param name product name
     * @param description product description
     * @param price product price
     * @param sku stock keeping unit (unique product code)
     */
    public ProductDTO(Long id, String name, String description, BigDecimal price, String sku) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sku = sku;
    }

    /**
     * Gets the product ID
     *
     * @return the product ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the product ID
     *
     * @param id the product ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the product name
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name
     *
     * @param name the product name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product description
     *
     * @return the product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description
     *
     * @param description the product description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the product price
     *
     * @return the product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the product price
     *
     * @param price the product price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the category ID
     *
     * @return the category ID
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category ID
     *
     * @param categoryId the category ID to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the category name
     *
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the category name
     *
     * @param categoryName the category name to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Gets the SKU (Stock Keeping Unit)
     *
     * @return the product SKU
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the SKU (Stock Keeping Unit)
     *
     * @param sku the product SKU to set
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the image URL
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL
     *
     * @param imageUrl the image URL to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Checks if this ProductDTO is valid for creation
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValidForCreation() {
        // Name, price and SKU are mandatory for a valid product
        return name != null && !name.trim().isEmpty() 
            && price != null && price.compareTo(BigDecimal.ZERO) > 0
            && sku != null && !sku.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductDTO that = (ProductDTO) o;
        
        return Objects.equals(id, that.id) &&
               Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sku);
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(description.length(), 20)) + "..." : "null") + '\'' +
                ", price=" + price +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
    
    // TODO: Add validation method for product data
    // FIXME: Consider using builder pattern for more complex construction
}