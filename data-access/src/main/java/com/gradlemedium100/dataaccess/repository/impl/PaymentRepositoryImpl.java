package com.gradlemedium100.dataaccess.repository.impl;

import com.gradlemedium100.dataaccess.entity.PaymentEntity;
import com.gradlemedium100.dataaccess.repository.PaymentRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the payment repository interface.
 * This class provides database access methods for payment-related operations.
 */
@Repository
@Transactional
public class PaymentRepositoryImpl extends BaseRepositoryImpl<PaymentEntity> implements PaymentRepository {

    private static final Logger logger = Logger.getLogger(PaymentRepositoryImpl.class.getName());

    /**
     * Constructor that initializes the repository with the entity manager.
     *
     * @param entityManager the JPA entity manager
     */
    public PaymentRepositoryImpl(EntityManager entityManager) {
        super(entityManager, PaymentEntity.class);
    }

    /**
     * Find a payment by its transaction ID.
     *
     * @param transactionId the unique transaction identifier from payment processor
     * @return the payment entity with the given transaction ID, or null if none found
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentEntity findByTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return null;
        }
        
        try {
            TypedQuery<PaymentEntity> query = entityManager.createQuery(
                "SELECT p FROM PaymentEntity p WHERE p.transactionId = :transactionId", 
                PaymentEntity.class);
            query.setParameter("transactionId", transactionId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // No payment found with the specified transaction ID
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding payment by transaction ID: " + transactionId, e);
            // FIXME: Consider better error handling strategy
            return null;
        }
    }

    /**
     * Find all payments for a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of payment entities associated with the order
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentEntity> findByOrderId(Long orderId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        
        try {
            TypedQuery<PaymentEntity> query = entityManager.createQuery(
                "SELECT p FROM PaymentEntity p WHERE p.order.id = :orderId", 
                PaymentEntity.class);
            query.setParameter("orderId", orderId);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding payments by order ID: " + orderId, e);
            // Return empty list instead of null to avoid null pointer exceptions
            return Collections.emptyList();
        }
    }

    /**
     * Find payments by payment method.
     *
     * @param paymentMethod the payment method to search for (e.g., CREDIT_CARD, PAYPAL)
     * @return a list of payment entities with the specified payment method
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentEntity> findByPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            // Using case-insensitive search for better user experience
            TypedQuery<PaymentEntity> query = entityManager.createQuery(
                "SELECT p FROM PaymentEntity p WHERE LOWER(p.paymentMethod) = LOWER(:paymentMethod)", 
                PaymentEntity.class);
            query.setParameter("paymentMethod", paymentMethod);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding payments by payment method: " + paymentMethod, e);
            return Collections.emptyList();
        }
    }

    /**
     * Find payments by status.
     *
     * @param status the payment status to search for (e.g., PENDING, COMPLETED, FAILED)
     * @return a list of payment entities with the specified status
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaymentEntity> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            TypedQuery<PaymentEntity> query = entityManager.createQuery(
                "SELECT p FROM PaymentEntity p WHERE p.status = :status", 
                PaymentEntity.class);
            query.setParameter("status", status);
            return query.getResultList();
            
            // TODO: Consider adding pagination for large result sets
            // This would require modifying the interface to accept page number and size
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding payments by status: " + status, e);
            return Collections.emptyList();
        }
    }

    /**
     * Update the status of a payment.
     *
     * @param paymentId the ID of the payment to update
     * @param status the new status to set
     */
    @Override
    @Transactional
    public void updatePaymentStatus(Long paymentId, String status) {
        if (paymentId == null || status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID and status cannot be null or empty");
        }
        
        try {
            // Get the payment entity
            findById(paymentId).ifPresent(payment -> {
                // Update status and persist the change
                payment.setStatus(status);
                entityManager.merge(payment);
                entityManager.flush();
                
                logger.info("Updated status for payment " + paymentId + " to " + status);
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating payment status for ID: " + paymentId, e);
            // FIXME: Consider whether to re-throw exception or handle silently
            throw new RuntimeException("Failed to update payment status", e);
        }
    }
    
    /**
     * Find payments created after a certain date.
     * 
     * @param date the date to compare with
     * @return list of payments created after the specified date
     * 
     * TODO: Add this method to the PaymentRepository interface
     * This is just a stub implementation for future enhancement
     */
    @Transactional(readOnly = true)
    public List<PaymentEntity> findPaymentsAfterDate(java.time.LocalDateTime date) {
        // Implementation will be added when interface is updated
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    /**
     * Calculates statistics about payments for reporting purposes.
     * 
     * @param status filter by status (optional, can be null)
     * @return summary of payment statistics
     * 
     * TODO: Define a proper return type for this method
     * and add it to the PaymentRepository interface
     */
    @Transactional(readOnly = true)
    public Object getPaymentStatistics(String status) {
        // Implementation will be added when interface is updated
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}