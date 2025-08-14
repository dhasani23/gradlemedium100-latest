package com.gradlemedium100.dataaccess.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.FetchType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class representing a payment in the database.
 * This class stores information about payments made for orders in the system.
 */
@Entity
@Table(name = "payments")
public class PaymentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "status", nullable = false)
    private String status;

    /**
     * Default constructor required by JPA
     */
    public PaymentEntity() {
        // Required empty constructor
    }

    /**
     * Constructor with essential fields
     *
     * @param order The order associated with this payment
     * @param amount The payment amount
     * @param paymentDate The date and time when payment was made
     * @param paymentMethod The method of payment
     * @param status The status of the payment
     */
    public PaymentEntity(OrderEntity order, BigDecimal amount, LocalDateTime paymentDate, 
                         String paymentMethod, String status) {
        this.order = order;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    /**
     * Get the order associated with this payment
     *
     * @return The order entity
     */
    public OrderEntity getOrder() {
        return order;
    }

    /**
     * Set the order associated with this payment
     *
     * @param order The order entity to associate with this payment
     */
    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    /**
     * Get the payment amount
     *
     * @return The amount of the payment
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Set the payment amount
     *
     * @param amount The payment amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Get the payment date and time
     *
     * @return The date and time when payment was made
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * Set the payment date and time
     *
     * @param paymentDate The payment date to set
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Get the payment method
     *
     * @return The method used for payment (e.g., CREDIT_CARD, PAYPAL)
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Set the payment method
     *
     * @param paymentMethod The payment method to set
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Get the transaction ID from the payment processor
     *
     * @return The unique transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Set the transaction ID from the payment processor
     *
     * @param transactionId The transaction ID to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Get the payment status
     *
     * @return The current status of the payment (e.g., PENDING, COMPLETED, FAILED)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the payment status
     *
     * @param status The payment status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Validates payment information before processing
     * 
     * @return True if the payment information is valid
     */
    public boolean validatePayment() {
        // Basic validation checks
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        if (paymentDate == null || order == null) {
            return false;
        }
        
        // TODO: Implement more sophisticated validation rules
        // such as payment method-specific validation
        
        return true;
    }
    
    /**
     * Updates the payment status and relevant information after processing
     * 
     * @param newStatus The new status to set
     * @param transactionId The transaction ID from payment processor
     */
    public void processPaymentResult(String newStatus, String transactionId) {
        this.status = newStatus;
        this.transactionId = transactionId;
        
        // FIXME: Need to handle case when transaction ID is already in use
        // This might indicate a duplicate payment or an error
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        
        PaymentEntity payment = (PaymentEntity) o;
        return Objects.equals(transactionId, payment.transactionId) &&
               Objects.equals(order, payment.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transactionId, order);
    }

    @Override
    public String toString() {
        return "PaymentEntity{" +
                "id=" + getId() +
                ", order=" + (order != null ? order.getId() : null) +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}