package com.gradlemedium100.payment.controller;

import com.gradlemedium100.payment.dto.PaymentRequestDTO;
import com.gradlemedium100.payment.model.Payment;
import com.gradlemedium100.payment.service.PaymentProcessorService;
import com.gradlemedium100.payment.service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RESTful API controller that handles payment-related HTTP requests such as 
 * payment processing, status inquiries, and refund requests.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentProcessorService paymentProcessorService;
    private final RefundService refundService;
    
    @Autowired
    public PaymentController(PaymentProcessorService paymentProcessorService, RefundService refundService) {
        this.paymentProcessorService = paymentProcessorService;
        this.refundService = refundService;
    }
    
    /**
     * Processes a payment request and returns the transaction result.
     * 
     * @param paymentRequestDTO The payment request data transfer object
     * @return ResponseEntity containing the transaction result
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        logger.info("Received payment request for order ID: {}", paymentRequestDTO.getOrderId());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate request
            if (paymentRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Invalid payment amount: {}", paymentRequestDTO.getAmount());
                response.put("status", "error");
                response.put("message", "Payment amount must be greater than zero");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Convert DTO to the format expected by the service
            Map<String, Object> paymentDetails = new HashMap<>();
            paymentDetails.put("orderId", paymentRequestDTO.getOrderId());
            paymentDetails.put("amount", paymentRequestDTO.getAmount());
            paymentDetails.put("currency", paymentRequestDTO.getCurrency());
            
            // Handle saved payment method vs new card details
            if (paymentRequestDTO.getPaymentMethodId() != null) {
                paymentDetails.put("paymentMethodId", paymentRequestDTO.getPaymentMethodId());
            } else if (paymentRequestDTO.getCardDetails() != null) {
                paymentDetails.put("cardDetails", paymentRequestDTO.getCardDetails());
                paymentDetails.put("savePaymentMethod", paymentRequestDTO.isSavePaymentMethod());
            } else {
                logger.warn("No payment method specified");
                response.put("status", "error");
                response.put("message", "Payment method information is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Process the payment
            Payment paymentResult = paymentProcessorService.processPayment(paymentDetails);
            
            // Convert service response to API response
            response.put("status", "success");
            response.put("transactionId", paymentResult.getTransactionId());
            response.put("paymentId", paymentResult.getId());
            response.put("orderStatus", paymentResult.getStatus());
            
            logger.info("Payment processed successfully. Transaction ID: {}", paymentResult.getTransactionId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid payment request", e);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error processing payment", e);
            response.put("status", "error");
            response.put("message", "An unexpected error occurred while processing the payment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Retrieves the status of a payment transaction by its ID.
     * 
     * @param paymentId The payment ID
     * @return ResponseEntity containing the payment status information
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long paymentId) {
        logger.info("Getting payment status for payment ID: {}", paymentId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate payment ID
            if (paymentId == null || paymentId <= 0) {
                logger.warn("Invalid payment ID: {}", paymentId);
                response.put("status", "error");
                response.put("message", "Valid payment ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Get payment status
            String status = paymentProcessorService.getPaymentStatus(paymentId);
            
            if (status == null) {
                logger.warn("Payment not found for ID: {}", paymentId);
                response.put("status", "error");
                response.put("message", "Payment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("status", "success");
            response.put("paymentId", paymentId);
            response.put("paymentStatus", status);
            
            // TODO: Include additional payment details if needed
            
            logger.info("Successfully retrieved payment status for ID: {}", paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving payment status for ID: {}", paymentId, e);
            response.put("status", "error");
            response.put("message", "An error occurred while retrieving payment status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Processes a refund for a specified payment transaction.
     * 
     * @param paymentId The payment ID to refund
     * @param amount The amount to refund
     * @return ResponseEntity containing the refund result
     */
    @PostMapping("/{paymentId}/refunds")
    public ResponseEntity<Map<String, Object>> processRefund(
            @PathVariable Long paymentId,
            @RequestParam BigDecimal amount) {
        logger.info("Processing refund of {} for payment ID: {}", amount, paymentId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate inputs
            if (paymentId == null || paymentId <= 0) {
                logger.warn("Invalid payment ID for refund: {}", paymentId);
                response.put("status", "error");
                response.put("message", "Valid payment ID is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Invalid refund amount: {}", amount);
                response.put("status", "error");
                response.put("message", "Refund amount must be greater than zero");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Process the refund
            boolean refundSuccess = refundService.processRefund(paymentId, amount);
            
            if (!refundSuccess) {
                // FIXME: This simple boolean return doesn't provide enough context for failure.
                // Consider enhancing the refundService to provide more detailed error information.
                logger.warn("Refund failed for payment ID: {}", paymentId);
                response.put("status", "error");
                response.put("message", "Refund could not be processed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            response.put("status", "success");
            response.put("message", "Refund processed successfully");
            response.put("paymentId", paymentId);
            response.put("refundedAmount", amount);
            
            // Get refund history to include in response
            List<Map<String, Object>> refundHistory = refundService.getRefundHistory(paymentId);
            response.put("refundHistory", refundHistory);
            
            logger.info("Successfully processed refund for payment ID: {}", paymentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid refund request", e);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error processing refund for payment ID: {}", paymentId, e);
            response.put("status", "error");
            response.put("message", "An unexpected error occurred while processing the refund");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}