package com.gradlemedium100.payment.service;

import com.gradlemedium100.payment.model.Payment;

import java.util.Map;

/**
 * Interface for processing payment transactions.
 */
public interface PaymentProcessorService {
    
    /**
     * Processes a payment transaction using the payment gateway and stores the transaction record.
     *
     * @param paymentDetails Map containing payment details
     * @return The processed Payment entity
     */
    Payment processPayment(Map<String, Object> paymentDetails);
    
    /**
     * Validates payment details before processing.
     *
     * @param paymentDetails Map containing payment details
     * @return true if the payment details are valid, false otherwise
     */
    boolean validatePaymentDetails(Map<String, Object> paymentDetails);
    
    /**
     * Retrieves the status of a payment transaction.
     *
     * @param paymentId ID of the payment
     * @return The status of the payment
     */
    String getPaymentStatus(Long paymentId);
}