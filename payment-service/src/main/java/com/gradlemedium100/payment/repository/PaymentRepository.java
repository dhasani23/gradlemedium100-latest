package com.gradlemedium100.payment.repository;

import com.gradlemedium100.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * JPA repository for Payment entities that handles database operations for payment records.
 * Provides methods for querying payment data from the database using Spring Data JPA.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds a payment record by its transaction ID.
     *
     * @param transactionId the unique transaction identifier
     * @return the payment entity if found, or null otherwise
     */
    Payment findByTransactionId(String transactionId);

    /**
     * Finds all payment records associated with an order.
     *
     * @param orderId the ID of the order
     * @return a list of payment entities associated with the specified order
     */
    List<Payment> findByOrderId(Long orderId);

    /**
     * Finds payments with a specific status created before a certain date.
     *
     * @param status the payment status to filter by
     * @param date   the date before which payments were created
     * @return a list of payment entities matching the criteria
     */
    List<Payment> findByStatusAndCreatedDateBefore(String status, Date date);

    /**
     * Finds payments created in a specific date range.
     * 
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return a list of payment entities created within the specified date range
     * 
     * TODO: Optimize query performance for large date ranges
     */
    @Query("SELECT p FROM Payment p WHERE p.createdDate BETWEEN :startDate AND :endDate ORDER BY p.createdDate DESC")
    List<Payment> findPaymentsInDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * Finds the total amount of payments by status.
     * 
     * @param status the payment status
     * @return the total amount of payments with the specified status
     * 
     * FIXME: Currently doesn't handle currency conversion for multi-currency payments
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    Double findTotalAmountByStatus(@Param("status") String status);
}