package com.gradlemedium100.dataaccess.entity;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * Entity class representing a product in the database.
 * This class maps to the 'products' table and contains all the product-related information.
 */
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Default constructor required by JPA.
     */
    public ProductEntity() {
        // Default constructor required by JPA
    }

    /**
     * Constructor with all fields for creating a new product.
     *
     * @param name Product name
     * @param description Product description
     * @param sku Stock keeping unit, unique identifier
     * @param price Product price
     * @param stockQuantity Available quantity in stock
     * @param category Product category
     * @param active Whether the product is active
     */
    public ProductEntity(String name, String description, String sku, BigDecimal price, 
                        Integer stockQuantity, String category, boolean active) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.active = active;
    }

    /**
     * Get the product ID.
     *
     * @return The product ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the product ID.
     *
     * @param id The product ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the product name.
     *
     * @return The product name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the product name.
     *
     * @param name The product name
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        this.name = name;
    }

    /**
     * Get the product description.
     *
     * @return The product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the product description.
     *
     * @param description The product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the product SKU.
     *
     * @return The product SKU
     */
    public String getSku() {
        return sku;
    }

    /**
     * Set the product SKU.
     *
     * @param sku The product SKU
     */
    public void setSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("Product SKU cannot be empty");
        }
        // FIXME: Should validate SKU format according to business rules
        this.sku = sku;
    }

    /**
     * Get the product price.
     *
     * @return The product price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Set the product price.
     *
     * @param price The product price
     */
    public void setPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            // TODO: Decide if negative prices should be allowed based on business rules
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    /**
     * Get the available stock quantity.
     *
     * @return The available stock quantity
     */
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Set the available stock quantity.
     *
     * @param stockQuantity The available stock quantity
     */
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    /**
     * Get the product category.
     *
     * @return The product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set the product category.
     *
     * @param category The product category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Check if the product is active.
     *
     * @return true if the product is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the product active status.
     *
     * @param active The product active status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check if the product is in stock.
     *
     * @return true if the product is in stock, false otherwise
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Calculate the total value of this product's inventory.
     *
     * @return The total value (price × quantity) or zero if stockQuantity is null
     */
    public BigDecimal calculateInventoryValue() {
        if (price == null || stockQuantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(new BigDecimal(stockQuantity));
    }

    /**
     * Decrease the stock quantity by the specified amount.
     *
     * @param quantity The quantity to decrease
     * @return true if the operation was successful, false otherwise
     */
    public boolean decreaseStock(int quantity) {
        if (stockQuantity == null || quantity <= 0) {
            return false;
        }
        
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
            return true;
        } else {
            // FIXME: Consider whether to throw an exception instead
            return false;
        }
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductEntity that = (ProductEntity) o;

        // If we have IDs, compare them
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        
        // Otherwise compare by business key (SKU)
        return sku != null ? sku.equals(that.sku) : that.sku == null;
    }

    @Override
    public int hashCode() {
        // Use SKU as business key for hash code
        return sku != null ? sku.hashCode() : 0;
    }
}