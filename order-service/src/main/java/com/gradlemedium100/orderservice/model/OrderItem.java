package com.gradlemedium100.orderservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Entity representing an item within an order, including quantity and pricing.
 * This class handles the calculation of totals and application of discounts.
 */
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    
    /**
     * Default constructor.
     */
    public OrderItem() {
        this.quantity = 0;
        this.unitPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
    }
    
    /**
     * Parameterized constructor for creating a new order item.
     * 
     * @param productId The ID of the product
     * @param productName The name of the product
     * @param quantity The quantity of the product
     * @param unitPrice The unit price of the product
     */
    public OrderItem(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = BigDecimal.ZERO;
        calculateTotalPrice();
    }
    
    /**
     * Calculates the total price for this item based on quantity and unit price,
     * applying any discounts.
     * 
     * @return The calculated total price
     */
    public BigDecimal calculateTotalPrice() {
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal rawTotal = unitPrice.multiply(new BigDecimal(quantity));
        
        // Apply discount if available
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Ensure discount doesn't exceed the raw total
            BigDecimal effectiveDiscount = discountAmount;
            if (effectiveDiscount.compareTo(rawTotal) > 0) {
                // FIXME: Consider logging a warning when discount exceeds total
                effectiveDiscount = rawTotal;
            }
            
            totalPrice = rawTotal.subtract(effectiveDiscount);
        } else {
            totalPrice = rawTotal;
        }
        
        // Round to 2 decimal places
        totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);
        return totalPrice;
    }
    
    /**
     * Updates the quantity and recalculates the total price.
     * 
     * @param newQuantity The new quantity
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            // TODO: Consider throwing an IllegalArgumentException instead of silent validation
            return;
        }
        
        this.quantity = newQuantity;
        calculateTotalPrice();
    }
    
    /**
     * Applies a percentage discount to this item and recalculates the total price.
     * 
     * @param discountPercent The discount percentage to apply (e.g., 10.5 for 10.5%)
     */
    public void applyDiscount(BigDecimal discountPercent) {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        
        BigDecimal rawTotal = unitPrice.multiply(new BigDecimal(quantity));
        
        // Calculate discount amount based on percentage
        BigDecimal discount = rawTotal.multiply(discountPercent)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                
        // Check if discount exceeds the total price
        if (discount.compareTo(rawTotal) > 0) {
            discount = rawTotal;
        }
        
        this.discountAmount = discount;
        calculateTotalPrice();
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", discountAmount=" + discountAmount +
                '}';
    }
}