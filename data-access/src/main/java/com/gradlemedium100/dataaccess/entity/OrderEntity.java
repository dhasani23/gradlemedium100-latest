package com.gradlemedium100.dataaccess.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing an order in the database.
 * Contains information about orders placed by users, including
 * order details, shipping, billing, and payment information.
 */
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    @Column(name = "billing_address", nullable = false, length = 500)
    private String billingAddress;

    /**
     * Default constructor required by JPA
     */
    public OrderEntity() {
        // Required by JPA
    }

    /**
     * Constructor with essential order information
     * 
     * @param orderNumber Unique order number
     * @param user User who placed the order
     * @param orderDate Date when the order was placed
     * @param totalAmount Total amount of the order
     * @param status Current status of the order
     */
    public OrderEntity(String orderNumber, UserEntity user, LocalDateTime orderDate, BigDecimal totalAmount, String status) {
        this.orderNumber = orderNumber;
        this.user = user;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    /**
     * Gets the unique order number.
     * 
     * @return The order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     * 
     * @param orderNumber The order number to set
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the user who placed the order.
     * 
     * @return The user entity
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets the user who placed the order.
     * 
     * @param user The user entity to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * Gets the date when the order was placed.
     * 
     * @return The order date
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the date when the order was placed.
     * 
     * @param orderDate The order date to set
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Gets the total amount of the order.
     * 
     * @return The total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount of the order.
     * 
     * @param totalAmount The total amount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            // FIXME: Consider throwing an exception for negative amounts instead of silently fixing
            this.totalAmount = BigDecimal.ZERO;
        } else {
            this.totalAmount = totalAmount;
        }
    }

    /**
     * Gets the current status of the order.
     * 
     * @return The order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the order.
     * Valid statuses might include: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
     * 
     * @param status The order status to set
     */
    public void setStatus(String status) {
        // TODO: Add validation for allowed status values
        this.status = status;
    }

    /**
     * Gets the shipping address for the order.
     * 
     * @return The shipping address
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Sets the shipping address for the order.
     * 
     * @param shippingAddress The shipping address to set
     */
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * Gets the billing address for the order.
     * 
     * @return The billing address
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the billing address for the order.
     * 
     * @param billingAddress The billing address to set
     */
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * Calculates if the order is eligible for expedited shipping
     * based on the total order amount.
     * 
     * @return true if eligible for expedited shipping
     */
    public boolean isEligibleForExpeditedShipping() {
        // Orders over $100 are eligible for expedited shipping
        return totalAmount != null && totalAmount.compareTo(new BigDecimal("100.00")) >= 0;
    }

    /**
     * Validates if the order is in a completable state.
     * 
     * @return true if the order can be completed
     */
    public boolean isCompletable() {
        // TODO: Implement more complex validation logic based on business rules
        return orderNumber != null && user != null && totalAmount != null && 
               status != null && !status.equals("CANCELLED") && 
               shippingAddress != null && billingAddress != null;
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + getId() +
                ", orderNumber='" + orderNumber + '\'' +
                ", user=" + (user != null ? user.getId() : null) +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}