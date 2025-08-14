package com.gradlemedium100.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service interface for handling refund operations.
 */
public interface RefundService {

    /**
     * Processes a refund for a payment transaction.
     *
     * @param paymentId the ID of the payment to refund
     * @param amount the amount to refund
     * @return true if the refund was processed successfully, false otherwise
     */
    boolean processRefund(Long paymentId, BigDecimal amount);

    /**
     * Validates a refund request before processing.
     *
     * @param paymentId the ID of the payment to refund
     * @param amount the amount to refund
     * @return true if the refund request is valid, false otherwise
     */
    boolean validateRefundRequest(Long paymentId, BigDecimal amount);

    /**
     * Retrieves the refund history for a payment transaction.
     *
     * @param paymentId the ID of the payment
     * @return a list of refund records as maps
     */
    List<Map<String, Object>> getRefundHistory(Long paymentId);
}