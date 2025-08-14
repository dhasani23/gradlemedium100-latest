package com.gradlemedium100.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for Cart entity.
 */
public class CartDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private List<CartItemDTO> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public CartDTO() {
        // Default constructor
    }

    /**
     * Constructor with essential fields.
     *
     * @param id the cart ID
     * @param userId the user ID
     */
    public CartDTO(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    /**
     * Gets the cart ID.
     *
     * @return the cart ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the cart ID.
     *
     * @param id the cart ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Gets the cart items.
     *
     * @return the list of cart items
     */
    public List<CartItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the cart items.
     *
     * @param items the list of cart items
     */
    public void setItems(List<CartItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    /**
     * Gets the total amount.
     *
     * @return the total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount.
     *
     * @param totalAmount the total amount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp.
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Adds an item to the cart.
     *
     * @param item the item to add
     * @return this cart DTO (for method chaining)
     */
    public CartDTO addItem(CartItemDTO item) {
        if (item != null) {
            if (items == null) {
                items = new ArrayList<>();
            }
            items.add(item);
        }
        return this;
    }

    /**
     * Recalculates the total amount based on the items in the cart.
     */
    public void recalculateTotal() {
        this.totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (CartItemDTO item : items) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    this.totalAmount = this.totalAmount.add(itemTotal);
                }
            }
        }
    }

    /**
     * Gets the number of items in the cart.
     *
     * @return the number of items in the cart
     */
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * Gets the total quantity of products in the cart.
     *
     * @return the total quantity of products in the cart
     */
    public int getTotalQuantity() {
        if (items == null) {
            return 0;
        }
        return items.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
    }

    /**
     * Determines if the cart is empty.
     *
     * @return true if the cart is empty, false otherwise
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CartDTO cartDTO = (CartDTO) o;
        
        return Objects.equals(id, cartDTO.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CartDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", items=" + items +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + getItemCount() +
                '}';
    }
}

/**
 * Data Transfer Object for Cart Item entity.
 */
class CartItemDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    /**
     * Default constructor.
     */
    public CartItemDTO() {
        // Default constructor
    }

    /**
     * Constructor with essential fields.
     *
     * @param productId the product ID
     * @param quantity the quantity
     * @param price the price
     */
    public CartItemDTO(Long productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Gets the product ID.
     *
     * @return the product ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     *
     * @param productId the product ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param productName the product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price.
     *
     * @param price the price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the subtotal (price * quantity).
     *
     * @return the subtotal
     */
    public BigDecimal getSubtotal() {
        if (price != null && quantity != null) {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CartItemDTO that = (CartItemDTO) o;
        
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CartItemDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}