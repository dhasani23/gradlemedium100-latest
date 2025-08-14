package com.gradlemedium100.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for Order entity.
 */
public class OrderDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items = new ArrayList<>();

    /**
     * Default constructor.
     */
    public OrderDTO() {
        // Default constructor
    }

    /**
     * Constructor with essential fields.
     *
     * @param id the order ID
     * @param userId the user ID
     * @param status the order status
     */
    public OrderDTO(Long id, Long userId, String status) {
        this.id = id;
        this.userId = userId;
        this.status = status;
    }

    /**
     * Gets the order ID.
     *
     * @return the order ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the order ID.
     *
     * @param id the order ID
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
     * Gets the order status.
     *
     * @return the order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the order status.
     *
     * @param status the order status
     */
    public void setStatus(String status) {
        this.status = status;
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
     * Gets the shipping address.
     *
     * @return the shipping address
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the shipping address.
     *
     * @param shippingAddress the shipping address
     */
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the payment method
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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
     * Gets the order items.
     *
     * @return the list of order items
     */
    public List<OrderItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the order items.
     *
     * @param items the list of order items
     */
    public void setItems(List<OrderItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    /**
     * Adds an item to the order.
     *
     * @param item the item to add
     * @return this order DTO (for method chaining)
     */
    public OrderDTO addItem(OrderItemDTO item) {
        if (item != null) {
            if (items == null) {
                items = new ArrayList<>();
            }
            items.add(item);
        }
        return this;
    }

    /**
     * Recalculates the total amount based on the items in the order.
     */
    public void recalculateTotal() {
        this.totalAmount = BigDecimal.ZERO;
        if (items != null) {
            for (OrderItemDTO item : items) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    this.totalAmount = this.totalAmount.add(itemTotal);
                }
            }
        }
    }

    /**
     * Gets the number of items in the order.
     *
     * @return the number of items in the order
     */
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * Gets the total quantity of products in the order.
     *
     * @return the total quantity of products in the order
     */
    public int getTotalQuantity() {
        if (items == null) {
            return 0;
        }
        return items.stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        OrderDTO orderDTO = (OrderDTO) o;
        
        return Objects.equals(id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + getItemCount() +
                ", createdAt=" + createdAt +
                '}';
    }
}

/**
 * Data Transfer Object for Order Item entity.
 */
class OrderItemDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    /**
     * Default constructor.
     */
    public OrderItemDTO() {
        // Default constructor
    }

    /**
     * Constructor with essential fields.
     *
     * @param productId the product ID
     * @param quantity the quantity
     * @param price the price
     */
    public OrderItemDTO(Long productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Gets the item ID.
     *
     * @return the item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the item ID.
     *
     * @param id the item ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the order ID.
     *
     * @return the order ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Sets the order ID.
     *
     * @param orderId the order ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
        
        OrderItemDTO that = (OrderItemDTO) o;
        
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}