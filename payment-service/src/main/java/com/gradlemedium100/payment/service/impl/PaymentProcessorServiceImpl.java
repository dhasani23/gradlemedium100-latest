package com.gradlemedium100.payment.service.impl;

import com.gradlemedium100.payment.model.Payment;
import com.gradlemedium100.payment.model.PaymentMethod;
import com.gradlemedium100.payment.repository.PaymentRepository;
import com.gradlemedium100.payment.service.PaymentGatewayService;
import com.gradlemedium100.payment.service.PaymentProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the PaymentProcessorService that handles the core payment processing logic
 * and orchestrates the payment workflow.
 */
@Service
public class PaymentProcessorServiceImpl implements PaymentProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessorServiceImpl.class);
    
    private final PaymentGatewayService paymentGatewayService;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentProcessorServiceImpl(PaymentGatewayService paymentGatewayService, PaymentRepository paymentRepository) {
        this.paymentGatewayService = paymentGatewayService;
        this.paymentRepository = paymentRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Payment processPayment(Map<String, Object> paymentDetails) {
        if (!validatePaymentDetails(paymentDetails)) {
            throw new IllegalArgumentException("Invalid payment details provided");
        }
        
        logger.info("Processing payment for order ID: {}", paymentDetails.get("orderId"));
        
        try {
            // Create a new payment record
            Payment payment = new Payment();
            payment.setOrderId(Long.valueOf(paymentDetails.get("orderId").toString()));
            payment.setAmount(new BigDecimal(paymentDetails.get("amount").toString()));
            payment.setCurrency(paymentDetails.get("currency").toString());
            payment.setStatus("PENDING");
            
            // Set payment method if available
            if (paymentDetails.containsKey("paymentMethodId")) {
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setId(Long.valueOf(paymentDetails.get("paymentMethodId").toString()));
                payment.setPaymentMethod(paymentMethod);
            }
            
            // Save initial payment record
            payment = paymentRepository.save(payment);
            
            // Process the payment through the gateway
            Map<String, Object> gatewayResponse = paymentGatewayService.processPaymentWithGateway(paymentDetails);
            
            // Update payment with gateway response
            payment.setTransactionId((String) gatewayResponse.get("transactionId"));
            payment.setGatewayResponse(gatewayResponse.toString());
            
            // Update status based on gateway response
            String responseStatus = (String) gatewayResponse.get("status");
            if ("SUCCESS".equals(responseStatus)) {
                payment.setStatus("COMPLETED");
                logger.info("Payment successfully processed with transaction ID: {}", payment.getTransactionId());
            } else if ("PENDING".equals(responseStatus)) {
                payment.setStatus("PENDING");
                logger.info("Payment is pending with transaction ID: {}", payment.getTransactionId());
            } else {
                payment.setStatus("FAILED");
                logger.error("Payment failed with error: {}", gatewayResponse.get("errorMessage"));
            }
            
            // Save updated payment record
            return paymentRepository.save(payment);
        } catch (Exception e) {
            logger.error("Error processing payment", e);
            throw new RuntimeException("Payment processing failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validatePaymentDetails(Map<String, Object> paymentDetails) {
        // Validate required fields
        if (paymentDetails == null) {
            logger.error("Payment details cannot be null");
            return false;
        }
        
        // Check for required fields
        String[] requiredFields = {"orderId", "amount", "currency"};
        for (String field : requiredFields) {
            if (!paymentDetails.containsKey(field) || paymentDetails.get(field) == null) {
                logger.error("Missing required field: {}", field);
                return false;
            }
        }
        
        // Validate amount
        try {
            BigDecimal amount = new BigDecimal(paymentDetails.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Payment amount must be greater than zero: {}", amount);
                return false;
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid amount format: {}", paymentDetails.get("amount"));
            return false;
        }
        
        // Validate payment method
        if (paymentDetails.containsKey("paymentMethodId")) {
            try {
                Long.valueOf(paymentDetails.get("paymentMethodId").toString());
            } catch (NumberFormatException e) {
                logger.error("Invalid payment method ID: {}", paymentDetails.get("paymentMethodId"));
                return false;
            }
        } else if (paymentDetails.containsKey("cardDetails")) {
            // Fix the unchecked cast with explicit type checking
            Object cardDetailsObj = paymentDetails.get("cardDetails");
            if (!(cardDetailsObj instanceof Map)) {
                logger.error("Card details must be a Map");
                return false;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, String> cardDetails = (Map<String, String>) cardDetailsObj;
            
            // Validate card details
            if (!cardDetails.containsKey("cardNumber") || !cardDetails.containsKey("expiryMonth") || 
                !cardDetails.containsKey("expiryYear") || !cardDetails.containsKey("cvv")) {
                logger.error("Incomplete card details provided");
                return false;
            }
        } else {
            logger.error("No payment method provided");
            return false;
        }
        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPaymentStatus(Long paymentId) {
        logger.info("Retrieving payment status for payment ID: {}", paymentId);
        
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (!paymentOpt.isPresent()) {
            logger.warn("No payment found with ID: {}", paymentId);
            return "UNKNOWN";
        }
        
        Payment payment = paymentOpt.get();
        
        // If payment is pending, check with gateway for latest status
        if ("PENDING".equals(payment.getStatus()) && payment.getTransactionId() != null) {
            try {
                String gatewayStatus = paymentGatewayService.checkTransactionStatus(payment.getTransactionId());
                
                // Update payment status if changed
                if (!gatewayStatus.equals(payment.getStatus())) {
                    payment.setStatus(gatewayStatus);
                    paymentRepository.save(payment);
                }
                
                return gatewayStatus;
            } catch (Exception e) {
                logger.error("Error checking payment status with gateway", e);
                // Return the stored status if gateway check fails
                return payment.getStatus();
            }
        }
        
        return payment.getStatus();
    }
}