package com.gradlemedium100.payment.service.impl;

import com.gradlemedium100.payment.model.Payment;
import com.gradlemedium100.payment.repository.PaymentRepository;
import com.gradlemedium100.payment.service.PaymentGatewayService;
import com.gradlemedium100.payment.service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the RefundService that handles refund processing and updates payment records accordingly.
 */
@Service
public class RefundServiceImpl implements RefundService {

    private static final Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);
    
    private final PaymentGatewayService paymentGatewayService;
    private final PaymentRepository paymentRepository;
    
    // Cache to store in-progress refunds to prevent duplicate processing
    private final Map<String, Date> inProgressRefunds = new ConcurrentHashMap<>();

    @Autowired
    public RefundServiceImpl(PaymentGatewayService paymentGatewayService, PaymentRepository paymentRepository) {
        this.paymentGatewayService = paymentGatewayService;
        this.paymentRepository = paymentRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean processRefund(Long paymentId, BigDecimal amount) {
        logger.info("Processing refund for payment ID: {} with amount: {}", paymentId, amount);
        
        // Validate the refund request
        if (!validateRefundRequest(paymentId, amount)) {
            logger.warn("Refund validation failed for payment ID: {}", paymentId);
            return false;
        }
        
        try {
            // Retrieve payment details
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));
            
            String transactionId = payment.getTransactionId();
            String refundKey = paymentId + ":" + amount.toString();
            
            // Check if a refund is already in progress for this payment/amount
            if (inProgressRefunds.containsKey(refundKey)) {
                Date startTime = inProgressRefunds.get(refundKey);
                if (System.currentTimeMillis() - startTime.getTime() < 300000) { // 5 minutes timeout
                    logger.warn("A refund is already in progress for payment ID: {}", paymentId);
                    return false;
                } else {
                    // Clean up stale refund status
                    inProgressRefunds.remove(refundKey);
                }
            }
            
            // Mark refund as in progress
            inProgressRefunds.put(refundKey, new Date());
            
            try {
                // Process refund with payment gateway
                Map<String, Object> refundResult = paymentGatewayService.processRefundWithGateway(
                        transactionId, amount);
                
                // Check if refund was successful
                boolean isSuccess = "SUCCESS".equals(refundResult.get("status"));
                
                if (isSuccess) {
                    // Update payment status if fully refunded
                    if (payment.getAmount().compareTo(amount) == 0) {
                        payment.setStatus("REFUNDED");
                    } else {
                        payment.setStatus("PARTIALLY_REFUNDED");
                    }
                    
                    // Store refund details in payment's gateway response
                    String currentResponse = payment.getGatewayResponse();
                    String refundResponse = currentResponse + ";" + 
                            "REFUND:" + new Date() + ":" + amount + ":" + refundResult.get("refundId");
                    payment.setGatewayResponse(refundResponse);
                    
                    payment.setLastUpdatedDate(new Date());
                    paymentRepository.save(payment);
                    
                    logger.info("Refund processed successfully for payment ID: {}", paymentId);
                    return true;
                } else {
                    logger.error("Refund failed with gateway response: {}", refundResult);
                    return false;
                }
            } finally {
                // Remove in-progress status
                inProgressRefunds.remove(refundKey);
            }
        } catch (Exception e) {
            logger.error("Error processing refund for payment ID: " + paymentId, e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateRefundRequest(Long paymentId, BigDecimal amount) {
        logger.debug("Validating refund request for payment ID: {} with amount: {}", paymentId, amount);
        
        // Basic validation checks
        if (paymentId == null || amount == null) {
            logger.warn("Invalid refund request: Payment ID or amount is null");
            return false;
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid refund amount: {} for payment ID: {}", amount, paymentId);
            return false;
        }
        
        try {
            // Check if payment exists
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                logger.warn("Payment not found with ID: {}", paymentId);
                return false;
            }
            
            Payment payment = paymentOpt.get();
            
            // Check if payment is in a refundable state
            if ("REFUNDED".equals(payment.getStatus())) {
                logger.warn("Payment already fully refunded, ID: {}", paymentId);
                return false;
            }
            
            if ("PENDING".equals(payment.getStatus()) || "FAILED".equals(payment.getStatus())) {
                logger.warn("Cannot refund payment with status: {} for ID: {}", payment.getStatus(), paymentId);
                return false;
            }
            
            // Check if refund amount is valid
            BigDecimal alreadyRefunded = getAlreadyRefundedAmount(payment);
            BigDecimal maxRefundable = payment.getAmount().subtract(alreadyRefunded);
            
            if (amount.compareTo(maxRefundable) > 0) {
                logger.warn("Refund amount {} exceeds available refundable amount {} for payment ID: {}", 
                        amount, maxRefundable, paymentId);
                return false;
            }
            
            // FIXME: Add additional checks for refund time limits (e.g., can't refund after X days)
            
            return true;
        } catch (Exception e) {
            logger.error("Error validating refund request for payment ID: " + paymentId, e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> getRefundHistory(Long paymentId) {
        logger.debug("Retrieving refund history for payment ID: {}", paymentId);
        
        List<Map<String, Object>> refundHistory = new ArrayList<>();
        
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                logger.warn("Payment not found with ID: {}", paymentId);
                return Collections.emptyList();
            }
            
            Payment payment = paymentOpt.get();
            String gatewayResponse = payment.getGatewayResponse();
            
            // Parse refund information from gateway response
            // Format: REFUND:date:amount:refundId
            if (gatewayResponse != null && gatewayResponse.contains("REFUND:")) {
                String[] responses = gatewayResponse.split(";");
                
                for (String response : responses) {
                    if (response.startsWith("REFUND:")) {
                        try {
                            String[] refundParts = response.substring(7).split(":");
                            if (refundParts.length >= 3) {
                                Map<String, Object> refund = new HashMap<>();
                                
                                // Parse date
                                try {
                                    refund.put("date", new Date(Long.parseLong(refundParts[0])));
                                } catch (NumberFormatException e) {
                                    // Fallback if date is stored in a different format
                                    refund.put("date", refundParts[0]);
                                }
                                
                                // Parse amount
                                refund.put("amount", new BigDecimal(refundParts[1]));
                                
                                // Add refund ID if available
                                if (refundParts.length > 2) {
                                    refund.put("refundId", refundParts[2]);
                                }
                                
                                refundHistory.add(refund);
                            }
                        } catch (Exception e) {
                            // Skip malformed refund entries
                            logger.warn("Could not parse refund entry: {}", response, e);
                        }
                    }
                }
            }
            
            // Sort by date (most recent first)
            refundHistory.sort((a, b) -> {
                Date dateA = (Date) a.get("date");
                Date dateB = (Date) b.get("date");
                return dateB.compareTo(dateA);
            });
            
            return refundHistory;
        } catch (Exception e) {
            logger.error("Error retrieving refund history for payment ID: " + paymentId, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Calculate the total amount already refunded for a payment.
     *
     * @param payment the payment entity
     * @return the total amount already refunded
     */
    private BigDecimal getAlreadyRefundedAmount(Payment payment) {
        List<Map<String, Object>> refundHistory = getRefundHistory(payment.getId());
        return refundHistory.stream()
                .map(refund -> (BigDecimal) refund.get("amount"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * TODO: Implement notification system for refund status updates
     * This would send notifications to users when refund status changes
     */
}