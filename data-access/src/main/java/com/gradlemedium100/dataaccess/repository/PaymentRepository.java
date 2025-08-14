package com.gradlemedium100.dataaccess.repository;

import com.gradlemedium100.dataaccess.entity.PaymentEntity;
import java.util.List;

/**
 * Repository interface for payment-related database operations.
 * This interface defines specific query methods for retrieving and manipulating
 * payment records in the database.
 */
public interface PaymentRepository extends BaseRepository<PaymentEntity> {

    /**
     * Find a payment by its transaction ID.
     * Transaction IDs should be unique as they represent external payment processor references.
     *
     * @param transactionId the unique transaction identifier from payment processor
     * @return the payment entity associated with the transaction ID, if found
     */
    PaymentEntity findByTransactionId(String transactionId);

    /**
     * Find all payments associated with a specific order.
     * This allows retrieving payment history for a given order.
     *
     * @param orderId the ID of the order to find payments for
     * @return a list of payment entities associated with the order
     */
    List<PaymentEntity> findByOrderId(Long orderId);

    /**
     * Find payments by payment method.
     * Useful for reporting and analytics on payment method usage.
     *
     * @param paymentMethod the payment method to search for (e.g., "CREDIT_CARD", "PAYPAL")
     * @return a list of payment entities with the specified payment method
     */
    List<PaymentEntity> findByPaymentMethod(String paymentMethod);

    /**
     * Find payments by their current status.
     * This allows querying for payments in specific states (e.g., pending, completed, failed).
     *
     * @param status the payment status to search for
     * @return a list of payment entities with the specified status
     */
    List<PaymentEntity> findByStatus(String status);

    /**
     * Update the status of a payment.
     * This method provides a targeted way to update just the payment status
     * without affecting other payment attributes.
     *
     * @param paymentId the ID of the payment to update
     * @param status the new status to set
     * @throws IllegalArgumentException if the payment ID is invalid or status is null
     * @throws javax.persistence.EntityNotFoundException if no payment exists with the given ID
     */
    void updatePaymentStatus(Long paymentId, String status);
    
    // TODO: Add method to find payments within a date range
    
    // TODO: Add method to find payments by amount range
    
    // FIXME: Consider adding transaction isolation for status updates to prevent race conditions
}