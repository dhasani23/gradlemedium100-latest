package com.gradlemedium100.productservice.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Inventory entity.
 * This DTO is used for transferring inventory data between layers.
 */
public class InventoryDTO {
    
    // Unique identifier for the inventory record
    private Long id;
    
    // ID of the product associated with this inventory
    private Long productId;
    
    // Name of the product
    private String productName;
    
    // Current quantity in stock
    private Integer quantity;
    
    // Timestamp of the last restock
    private LocalDateTime lastRestocked;
    
    // Threshold below which stock is considered low
    private Integer lowStockThreshold;
    
    // Storage location of the product
    private String location;

    /**
     * Default constructor
     */
    public InventoryDTO() {
        // Default constructor
    }

    /**
     * Constructor with essential fields
     * 
     * @param id Inventory record ID
     * @param productId Product ID
     * @param quantity Current quantity in stock
     */
    public InventoryDTO(Long id, Long productId, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Full constructor with all fields
     * 
     * @param id Inventory record ID
     * @param productId Product ID
     * @param productName Product name
     * @param quantity Current quantity
     * @param lastRestocked Last restock date and time
     * @param lowStockThreshold Low stock threshold
     * @param location Storage location
     */
    public InventoryDTO(Long id, Long productId, String productName, Integer quantity, 
                       LocalDateTime lastRestocked, Integer lowStockThreshold, String location) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.lastRestocked = lastRestocked;
        this.lowStockThreshold = lowStockThreshold;
        this.location = location;
    }

    /**
     * Gets the inventory record ID
     * 
     * @return The inventory record ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the inventory record ID
     * 
     * @param id The inventory record ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the associated product ID
     * 
     * @return The product ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the associated product ID
     * 
     * @param productId The product ID to set
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    /**
     * Gets the product name
     * 
     * @return The product name
     */
    public String getProductName() {
        return productName;
    }
    
    /**
     * Sets the product name
     * 
     * @param productName The product name to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the current quantity in stock
     * 
     * @return The current quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the current quantity in stock
     * 
     * @param quantity The quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    /**
     * Gets the last restocked date and time
     * 
     * @return The last restocked datetime
     */
    public LocalDateTime getLastRestocked() {
        return lastRestocked;
    }
    
    /**
     * Sets the last restocked date and time
     * 
     * @param lastRestocked The last restocked datetime to set
     */
    public void setLastRestocked(LocalDateTime lastRestocked) {
        this.lastRestocked = lastRestocked;
    }
    
    /**
     * Gets the low stock threshold
     * 
     * @return The low stock threshold
     */
    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }
    
    /**
     * Sets the low stock threshold
     * 
     * @param lowStockThreshold The low stock threshold to set
     */
    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
    
    /**
     * Gets the storage location of the product
     * 
     * @return The storage location
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Sets the storage location of the product
     * 
     * @param location The storage location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Checks if the inventory is running low on stock
     * 
     * @return true if the stock is below the threshold, false otherwise
     */
    public boolean isLowStock() {
        // TODO: Add null check for quantity and lowStockThreshold
        return quantity != null && lowStockThreshold != null && quantity < lowStockThreshold;
    }
    
    /**
     * Calculates days since last restock
     * 
     * @return number of days since last restock, or null if never restocked
     */
    public Long daysSinceLastRestock() {
        if (lastRestocked == null) {
            return null;
        }
        
        // FIXME: Consider using ChronoUnit for more accurate calculation
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(lastRestocked, now).toDays();
    }
    
    @Override
    public String toString() {
        return "InventoryDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", lastRestocked=" + lastRestocked +
                ", lowStockThreshold=" + lowStockThreshold +
                ", location='" + location + '\'' +
                '}';
    }
}