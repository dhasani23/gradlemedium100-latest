package com.gradlemedium100.orderservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an item in the shopping cart before conversion to an order item.
 * Maintains information about the product, quantity, and pricing details.
 */
public class CartItem {
    
    // Unique identifier for the cart item
    private Long id;
    
    // Reference to the parent cart
    private Long cartId;
    
    // Reference to the product in the cart
    private Long productId;
    
    // Name of the product (cached for display purposes)
    private String productName;
    
    // Number of units in the cart
    private Integer quantity;
    
    // Current price per unit
    private BigDecimal unitPrice;
    
    // Total price for this item (quantity * unitPrice)
    private BigDecimal totalPrice;
    
    // Date and time when the item was added to the cart
    private LocalDateTime dateAdded;
    
    /**
     * Default constructor
     */
    public CartItem() {
        this.dateAdded = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor for creating a cart item
     *
     * @param cartId ID of the parent cart
     * @param productId ID of the product
     * @param productName Name of the product
     * @param quantity Quantity of the product
     * @param unitPrice Price per unit
     */
    public CartItem(Long cartId, Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.dateAdded = LocalDateTime.now();
        this.totalPrice = calculateTotalPrice();
    }
    
    /**
     * Updates the quantity and recalculates the total price
     *
     * @param newQuantity New quantity value
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = newQuantity;
        this.totalPrice = calculateTotalPrice();
    }
    
    /**
     * Calculates the total price for this item based on quantity and unit price
     *
     * @return The calculated total price
     */
    public BigDecimal calculateTotalPrice() {
        // FIXME: Handle potential precision issues with BigDecimal multiplication
        if (quantity == null || unitPrice == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(new BigDecimal(quantity));
    }
    
    /**
     * Converts the cart item to an order item for use in creating an order
     *
     * @return A new OrderItem based on this cart item's data
     */
    public OrderItem convertToOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(this.productId);
        orderItem.setProductName(this.productName);
        orderItem.setQuantity(this.quantity);
        orderItem.setUnitPrice(this.unitPrice);
        orderItem.setTotalPrice(this.totalPrice);
        // OrderItem does not have dateAdded field from CartItem
        // TODO: Consider if additional data needs to be transferred to the OrderItem
        
        // OrderItem has a discountAmount that CartItem doesn't have
        orderItem.setDiscountAmount(BigDecimal.ZERO); // Default to zero discount
        
        return orderItem;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
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
        // Recalculate total price when unit price changes
        if (this.quantity != null) {
            this.totalPrice = calculateTotalPrice();
        }
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id) &&
                Objects.equals(productId, cartItem.productId) &&
                Objects.equals(cartId, cartItem.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cartId, productId);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", dateAdded=" + dateAdded +
                '}';
    }
}