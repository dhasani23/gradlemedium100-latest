package com.gradlemedium100.payment.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.PrePersist;
import java.util.Date;

/**
 * Entity class that represents saved payment methods for users such as credit cards or digital wallets.
 * This class stores user payment preferences with appropriate masking for sensitive information.
 */
@Entity
@Data
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Stores only the masked version of the card number for security purposes.
     * The actual card number should never be stored in the database.
     * Format: XXXX-XXXX-XXXX-1234 (where only last 4 digits are visible)
     */
    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_expiry_month")
    private String cardExpiryMonth;

    @Column(name = "card_expiry_year")
    private String cardExpiryYear;

    /**
     * Token provided by the payment gateway for tokenized payment methods.
     * This token is used for subsequent charges without needing to store the actual card details.
     */
    @Column(name = "gateway_token")
    private String gatewayToken;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    /**
     * Automatically sets the creation date when a new payment method is created.
     */
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    /**
     * Returns a masked display version of the card number that's safe for UI display.
     * @return String representation of the masked card number
     */
    public String getMaskedCardNumber() {
        // FIXME: Implement proper masking logic to ensure PCI compliance
        return this.cardNumber;
    }

    /**
     * Validates if the payment method is still valid based on expiration date.
     * @return true if the payment method is valid, false otherwise
     */
    public boolean isValid() {
        // Only validate for card-type payment methods
        if (!isCardPaymentMethod()) {
            return true;
        }
        
        try {
            Date now = new Date();
            
            // Parse expiration year and month
            int expiryYear = Integer.parseInt(cardExpiryYear);
            int expiryMonth = Integer.parseInt(cardExpiryMonth);
            
            // Get current year and month
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentYear = cal.get(java.util.Calendar.YEAR);
            int currentMonth = cal.get(java.util.Calendar.MONTH) + 1; // Calendar months are 0-based
            
            // Compare expiration date with current date
            if (expiryYear > currentYear) {
                return true;
            } else if (expiryYear == currentYear) {
                return expiryMonth >= currentMonth;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            // TODO: Add proper logging here
            return false;
        }
    }
    
    /**
     * Determines if this payment method is a card-based payment method.
     * @return true if it's a card payment method, false otherwise
     */
    private boolean isCardPaymentMethod() {
        return "CREDIT_CARD".equals(type) || "DEBIT_CARD".equals(type);
    }
    
    /**
     * Returns a formatted string representation of the card expiration date.
     * @return String in format MM/YY
     */
    public String getFormattedExpiryDate() {
        if (cardExpiryMonth != null && cardExpiryYear != null) {
            return cardExpiryMonth + "/" + (cardExpiryYear.length() > 2 ? cardExpiryYear.substring(2) : cardExpiryYear);
        }
        return "";
    }
}