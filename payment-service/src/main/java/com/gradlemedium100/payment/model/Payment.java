package com.gradlemedium100.payment.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity class that represents payment transactions in the system with all necessary attributes.
 * This class captures all details of payment transactions including references to orders,
 * payment methods, and transaction status tracking.
 */
@Entity
@Data
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier for the payment transaction from the payment gateway.
     * This is used for tracking and reconciliation with the payment provider.
     */
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    /**
     * Associated order ID that this payment is for.
     * Establishes relationship between payments and orders.
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * Amount of the payment transaction.
     * Must be non-negative and properly formatted according to currency.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * Currency code for the payment (e.g., USD, EUR, GBP).
     * Uses ISO 4217 currency codes.
     */
    @Column(length = 3)
    private String currency;

    /**
     * Status of the payment transaction.
     * Possible values: PENDING, COMPLETED, FAILED, REFUNDED
     * 
     * TODO: Consider using an enum for status values to enforce consistency
     */
    @Column(nullable = false)
    private String status;

    /**
     * Method used for the payment.
     * References a saved payment method or one-time payment details.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    /**
     * Raw response from the payment gateway.
     * Contains the complete response data for debugging and auditing purposes.
     * 
     * FIXME: Consider encrypting sensitive data in the gateway response
     */
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    /**
     * Date when the payment record was created.
     * Automatically set during entity creation.
     */
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    /**
     * Date when the payment record was last updated.
     * Automatically updated on entity modification.
     */
    @Column(name = "last_updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    /**
     * Lifecycle callback that automatically sets creation and update timestamps
     * when a new Payment entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        lastUpdatedDate = new Date();
    }

    /**
     * Lifecycle callback that automatically updates the lastUpdatedDate
     * when a Payment entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDate = new Date();
    }
    
    /**
     * Checks if this payment is in a finalized state (completed or refunded)
     * 
     * @return true if payment is in a final state, false otherwise
     */
    public boolean isFinalized() {
        return "COMPLETED".equals(status) || "REFUNDED".equals(status);
    }
    
    /**
     * Checks if a refund is possible for this payment
     * 
     * @return true if the payment can be refunded, false otherwise
     */
    public boolean isRefundable() {
        return "COMPLETED".equals(status);
    }
}