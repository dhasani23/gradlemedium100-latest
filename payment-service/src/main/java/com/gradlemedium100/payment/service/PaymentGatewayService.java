package com.gradlemedium100.payment.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Interface defining standard operations for interacting with payment gateway providers.
 */
public interface PaymentGatewayService {
    
    /**
     * Processes a payment transaction using the configured payment gateway.
     *
     * @param paymentDetails Map containing payment details
     * @return Map containing the gateway response
     */
    Map<String, Object> processPaymentWithGateway(Map<String, Object> paymentDetails);
    
    /**
     * Processes a refund with the payment gateway.
     *
     * @param transactionId The original transaction ID
     * @param amount The refund amount
     * @return Map containing the gateway response
     */
    Map<String, Object> processRefundWithGateway(String transactionId, BigDecimal amount);
    
    /**
     * Checks the status of a transaction with the payment gateway.
     *
     * @param transactionId The transaction ID
     * @return The status of the transaction
     */
    String checkTransactionStatus(String transactionId);
}