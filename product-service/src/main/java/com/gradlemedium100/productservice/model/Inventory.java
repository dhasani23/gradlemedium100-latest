package com.gradlemedium100.productservice.model;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity class representing inventory information for a product
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", unique = true)
    private Product product;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Min(value = 0, message = "Low stock threshold cannot be negative")
    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Size(max = 255, message = "Location cannot exceed 255 characters")
    @Column(name = "location")
    private String location;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "reserved")
    private Integer reserved;
    
    @Column(name = "last_updated")
    private Date lastUpdated;

    /**
     * Default constructor required by JPA
     */
    public Inventory() {
        // Required by JPA
        this.reserved = 0;
    }

    /**
     * Constructor with essential fields
     *
     * @param product  product associated with this inventory record
     * @param quantity current quantity in stock
     */
    public Inventory(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.lowStockThreshold = 5; // Default value
        this.reserved = 0;
    }

    /**
     * Full constructor
     *
     * @param product           product associated with this inventory record
     * @param quantity          current quantity in stock
     * @param lowStockThreshold threshold below which stock is considered low
     * @param location          storage location of the product
     */
    public Inventory(Product product, Integer quantity, Integer lowStockThreshold, String location) {
        this.product = product;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.location = location;
        this.reserved = 0;
    }

    /**
     * Lifecycle callback to update timestamp and set default values
     */
    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = new Date();
        if (this.lowStockThreshold == null) {
            this.lowStockThreshold = 5; // Default value if not specified
        }
        if (this.reserved == null) {
            this.reserved = 0;
        }
    }

    /**
     * Lifecycle callback to update timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = new Date();
    }

    /**
     * Gets the inventory record ID
     *
     * @return the inventory record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the inventory record ID
     *
     * @param id the inventory record ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the associated product
     *
     * @return the product associated with this inventory
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the associated product
     *
     * @param product the product associated with this inventory
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the current quantity in stock
     *
     * @return the current quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the current quantity in stock
     *
     * @param quantity the current quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the timestamp of the last restock
     *
     * @return the last restock timestamp
     */
    public LocalDateTime getLastRestocked() {
        return lastRestocked;
    }

    /**
     * Sets the timestamp of the last restock
     *
     * @param lastRestocked the last restock timestamp
     */
    public void setLastRestocked(LocalDateTime lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    /**
     * Gets the low stock threshold
     *
     * @return the low stock threshold
     */
    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    /**
     * Sets the low stock threshold
     *
     * @param lowStockThreshold the low stock threshold
     */
    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    /**
     * Gets the storage location of the product
     *
     * @return the storage location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the storage location of the product
     *
     * @param location the storage location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the timestamp when the inventory was last updated
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the inventory was last updated
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the number of reserved items
     * 
     * @return the number of reserved items
     */
    public Integer getReserved() {
        return reserved;
    }
    
    /**
     * Sets the number of reserved items
     * 
     * @param reserved the number of reserved items
     */
    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }
    
    /**
     * Gets the date when the inventory was last updated
     * 
     * @return the last update date
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    /**
     * Sets the date when the inventory was last updated
     * 
     * @param lastUpdated the last update date
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Checks if the inventory is below the low stock threshold
     *
     * @return true if stock level is below threshold
     */
    public boolean isLowStock() {
        return quantity < lowStockThreshold;
    }

    /**
     * Records a restock event with the specified quantity
     * 
     * @param restockQuantity quantity added to inventory
     * @throws IllegalArgumentException if restock quantity is negative
     */
    public void restock(int restockQuantity) {
        if (restockQuantity < 0) {
            throw new IllegalArgumentException("Restock quantity cannot be negative");
        }
        
        this.quantity += restockQuantity;
        this.lastRestocked = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastUpdated = new Date();
    }

    /**
     * Attempts to reduce inventory by the specified quantity
     * 
     * @param reduceQuantity quantity to remove from inventory
     * @return true if inventory could be reduced, false if insufficient stock
     * @throws IllegalArgumentException if reduce quantity is negative
     */
    public boolean reduceStock(int reduceQuantity) {
        if (reduceQuantity < 0) {
            throw new IllegalArgumentException("Reduce quantity cannot be negative");
        }
        
        if (quantity >= reduceQuantity) {
            this.quantity -= reduceQuantity;
            this.updatedAt = LocalDateTime.now();
            this.lastUpdated = new Date();
            return true;
        }
        return false;
    }

    /**
     * Calculates days since the last restock
     * 
     * @return days since last restock, or null if never restocked
     */
    public Long daysSinceLastRestock() {
        if (lastRestocked == null) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        // TODO: Replace with java.time.temporal.ChronoUnit.DAYS.between(lastRestocked, now)
        // when using Java 9+ for better accuracy
        return (now.toLocalDate().toEpochDay() - lastRestocked.toLocalDate().toEpochDay());
    }
    
    /**
     * Calculates how many more units can be added before capacity is reached
     * 
     * @param maxCapacity maximum capacity for this product's storage location
     * @return remaining capacity
     * @throws IllegalArgumentException if max capacity is less than current quantity
     */
    public int calculateRemainingCapacity(int maxCapacity) {
        if (maxCapacity < quantity) {
            throw new IllegalArgumentException("Max capacity cannot be less than current quantity");
        }
        return maxCapacity - quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inventory inventory = (Inventory) o;
        // If ID exists, compare by ID
        if (id != null && inventory.id != null) {
            return id.equals(inventory.id);
        }
        // Otherwise compare by product
        return product != null && product.equals(inventory.product);
    }

    @Override
    public int hashCode() {
        // Use ID if available, otherwise use product
        return id != null ? id.hashCode() : (product != null ? product.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", product=" + (product != null ? product.getSku() : "null") +
                ", quantity=" + quantity +
                ", reserved=" + reserved +
                ", location='" + location + '\'' +
                ", lowStock=" + isLowStock() +
                '}';
    }
}