package com.gradlemedium100.orderservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing an order in the system with all associated details.
 * This class manages order information including items, status, and financial data.
 */
public class Order {
    
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private List<OrderItem> items;
    private Long paymentId;
    private String notes;
    
    /**
     * Default constructor
     */
    public Order() {
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }
    
    /**
     * Parameterized constructor for creating an order with required fields
     * 
     * @param userId The ID of the user who placed the order
     * @param shippingAddress The shipping address for the order
     * @param billingAddress The billing address for the order
     */
    public Order(Long userId, String shippingAddress, String billingAddress) {
        this();
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
    }

    /**
     * Calculates the total amount of the order based on all items.
     * This method iterates through all items and sums their total prices.
     * 
     * @return The calculated total amount
     */
    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getTotalPrice());
        }
        
        // Update the total amount field
        this.totalAmount = total;
        return total;
    }
    
    /**
     * Adds an item to the order and recalculates the total amount.
     * If the item already exists (same product ID), updates the quantity instead.
     * 
     * @param item The order item to add
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            return;
        }
        
        // Check if the item with same productId already exists
        boolean found = false;
        for (OrderItem existingItem : items) {
            if (Objects.equals(existingItem.getProductId(), item.getProductId())) {
                // Update quantity instead of adding a new item
                int newQuantity = existingItem.getQuantity() + item.getQuantity();
                existingItem.updateQuantity(newQuantity);
                found = true;
                break;
            }
        }
        
        // If not found, add as new item
        if (!found) {
            // Set the orderId if it's not already set
            if (this.id != null && item.getOrderId() == null) {
                item.setOrderId(this.id);
            }
            items.add(item);
        }
        
        // Recalculate the total
        calculateTotal();
    }
    
    /**
     * Removes an item from the order by its ID and recalculates the total amount.
     * 
     * @param itemId The ID of the item to remove
     * @return true if the item was found and removed, false otherwise
     */
    public boolean removeItem(Long itemId) {
        if (itemId == null || items == null || items.isEmpty()) {
            return false;
        }
        
        Iterator<OrderItem> iterator = items.iterator();
        boolean removed = false;
        
        while (iterator.hasNext()) {
            OrderItem item = iterator.next();
            if (Objects.equals(item.getId(), itemId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        // Recalculate total if an item was removed
        if (removed) {
            calculateTotal();
        }
        
        return removed;
    }
    
    /**
     * Updates the status of the order.
     * TODO: Add validation to ensure valid status transitions.
     * 
     * @param newStatus The new status to set
     */
    public void updateStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            return;
        }
        
        // FIXME: Implement proper status transition validation
        // For now, just update the status
        this.status = newStatus;
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        calculateTotal();  // Recalculate total when items are set
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", paymentId=" + paymentId +
                ", itemCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}