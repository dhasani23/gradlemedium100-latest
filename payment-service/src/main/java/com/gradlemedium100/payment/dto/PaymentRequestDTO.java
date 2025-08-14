package com.gradlemedium100.payment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object that encapsulates the details required to process a payment request.
 * This class is used to transfer payment information between different layers of the application
 * and potentially to external payment processing systems.
 */
@Data
public class PaymentRequestDTO {

    /**
     * ID of the order being paid for
     */
    private Long orderId;
    
    /**
     * Amount to be paid
     */
    private BigDecimal amount;
    
    /**
     * Currency code for the payment (e.g., USD, EUR, GBP)
     */
    private String currency;
    
    /**
     * ID of the saved payment method to use (optional)
     * If provided, cardDetails may be ignored
     */
    private Long paymentMethodId;
    
    /**
     * Credit card details if not using a saved payment method
     * Common keys include:
     * - cardNumber: The credit card number
     * - expiryMonth: Card expiration month (MM)
     * - expiryYear: Card expiration year (YYYY)
     * - cvv: Card verification value
     * - cardholderName: Name of the cardholder
     */
    private Map<String, String> cardDetails;
    
    /**
     * Flag indicating whether to save this payment method for future use
     */
    private boolean savePaymentMethod;
    
    /**
     * Validates if this payment request has sufficient information to be processed
     * 
     * @return true if the payment request is valid, false otherwise
     */
    public boolean isValid() {
        // Basic validation logic
        if (orderId == null || amount == null || currency == null) {
            return false;
        }
        
        // Check if either payment method ID is provided or card details are complete
        if (paymentMethodId == null) {
            // If no saved payment method, card details must be provided
            if (cardDetails == null || cardDetails.isEmpty()) {
                return false;
            }
            
            // Check for required card fields
            String[] requiredFields = {"cardNumber", "expiryMonth", "expiryYear", "cvv"};
            for (String field : requiredFields) {
                if (!cardDetails.containsKey(field) || cardDetails.get(field).isEmpty()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Masks sensitive card information for logging purposes
     * 
     * @return A copy of this DTO with sensitive information masked
     */
    public PaymentRequestDTO maskedCopy() {
        PaymentRequestDTO masked = new PaymentRequestDTO();
        masked.setOrderId(this.orderId);
        masked.setAmount(this.amount);
        masked.setCurrency(this.currency);
        masked.setPaymentMethodId(this.paymentMethodId);
        masked.setSavePaymentMethod(this.savePaymentMethod);
        
        // Mask card details if present
        if (this.cardDetails != null && !this.cardDetails.isEmpty()) {
            Map<String, String> maskedDetails = new HashMap<>(this.cardDetails);
            // Replace card number with masked version if present
            if (maskedDetails.containsKey("cardNumber")) {
                String cardNumber = maskedDetails.get("cardNumber");
                if (cardNumber != null && cardNumber.length() > 4) {
                    String maskedNumber = "XXXX-XXXX-XXXX-" + cardNumber.substring(cardNumber.length() - 4);
                    maskedDetails.put("cardNumber", maskedNumber);
                }
            }
            // Remove CVV completely
            if (maskedDetails.containsKey("cvv")) {
                maskedDetails.put("cvv", "***");
            }
            
            masked.setCardDetails(maskedDetails);
        }
        
        return masked;
    }
    
    // TODO: Add validation for currency codes against ISO 4217 standard
}