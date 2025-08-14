package com.gradlemedium100.productservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity class representing a product in the catalog
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotBlank(message = "SKU is required")
    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA
     */
    public Product() {
        // Required by JPA
    }

    /**
     * Constructor with essential fields
     *
     * @param name  name of the product
     * @param price price of the product
     * @param sku   stock keeping unit
     */
    public Product(String name, BigDecimal price, String sku) {
        this.name = name;
        this.price = price;
        this.sku = sku;
    }

    /**
     * Full constructor
     *
     * @param name        name of the product
     * @param description description of the product
     * @param price       price of the product
     * @param category    category of the product
     * @param sku         stock keeping unit
     * @param imageUrl    URL pointing to product image
     */
    public Product(String name, String description, BigDecimal price, Category category, String sku, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sku = sku;
        this.imageUrl = imageUrl;
    }

    /**
     * Lifecycle callback to set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Lifecycle callback to update timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
     * @param id the product ID
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
     * @param name the product name
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
     * @param description the product description
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
     * @param price the product price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the product category
     *
     * @return the product category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the product category
     *
     * @param category the product category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets the product SKU
     *
     * @return the stock keeping unit
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the product SKU
     *
     * @param sku the stock keeping unit
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the product image URL
     *
     * @return URL to the product image
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the product image URL
     *
     * @param imageUrl URL to the product image
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the timestamp when the product was created
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the product was created
     *
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the product was last updated
     *
     * @return last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the product was last updated
     *
     * @param updatedAt last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Calculates the discounted price based on a discount percentage
     *
     * @param discountPercent percentage to discount the product price
     * @return the discounted price
     * @throws IllegalArgumentException if discount is negative or greater than 100
     */
    public BigDecimal calculateDiscountedPrice(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        BigDecimal discountFactor = BigDecimal.valueOf((100.0 - discountPercent) / 100.0);
        return price.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Checks if the product has a valid price
     *
     * @return true if price is greater than zero
     */
    public boolean isValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if the product belongs to a specific category
     *
     * @param categoryName name of category to check
     * @return true if product belongs to the specified category
     */
    public boolean belongsToCategory(String categoryName) {
        return category != null && category.getName().equalsIgnoreCase(categoryName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        // If ID exists, compare by ID
        if (id != null && product.id != null) {
            return id.equals(product.id);
        }
        // Otherwise compare by business key (SKU)
        return sku != null && sku.equals(product.sku);
    }

    @Override
    public int hashCode() {
        // Use ID if available, otherwise use SKU
        return id != null ? id.hashCode() : (sku != null ? sku.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", category=" + (category != null ? category.getName() : "none") +
                '}';
    }
}