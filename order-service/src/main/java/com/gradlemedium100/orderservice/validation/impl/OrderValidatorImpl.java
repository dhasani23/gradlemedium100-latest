package com.gradlemedium100.orderservice.validation.impl;

import com.gradlemedium100.common.exception.ValidationException;
import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderItem;
import com.gradlemedium100.orderservice.model.OrderStatus;
import com.gradlemedium100.orderservice.validation.OrderValidator;
import com.gradlemedium100.productservice.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of the OrderValidator interface for validating order data.
 * 
 * This class provides validation logic for orders including:
 * - Order data integrity
 * - Business rule validation
 * - Status transition validation
 * - Order items validation
 */
@Service
public class OrderValidatorImpl implements OrderValidator {

    private final ProductService productService;
    
    private final BigDecimal minimumOrderAmount;
    private final Integer maximumOrderItems;
    
    // Regular expression for validating address format
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "^[\\w\\s,.#-]+(\\s[A-Za-z]{2})?\\s+\\d{5}(-\\d{4})?$");
    
    /**
     * Constructor for OrderValidatorImpl
     * 
     * @param productService Service for product-related operations and validations
     * @param minimumOrderAmount Minimum allowed total amount for an order
     * @param maximumOrderItems Maximum number of items allowed in an order
     */
    @Autowired
    public OrderValidatorImpl(
            ProductService productService,
            @Value("${order.validation.minimum-amount:10.00}") BigDecimal minimumOrderAmount,
            @Value("${order.validation.maximum-items:50}") Integer maximumOrderItems) {
        this.productService = productService;
        this.minimumOrderAmount = minimumOrderAmount;
        this.maximumOrderItems = maximumOrderItems;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validateOrder(Order order) throws ValidationException {
        List<String> validationErrors = new ArrayList<>();
        
        // Check for null order
        if (order == null) {
            throw new ValidationException("Order cannot be null");
        }
        
        // Validate user ID
        if (order.getUserId() == null || order.getUserId() <= 0) {
            validationErrors.add("User ID is required and must be positive");
        }
        
        // Validate shipping address
        try {
            validateShippingAddress(order.getShippingAddress());
        } catch (ValidationException e) {
            validationErrors.add(e.getMessage());
        }
        
        // Validate billing address if different from shipping address
        if (!Objects.equals(order.getBillingAddress(), order.getShippingAddress())) {
            try {
                validateShippingAddress(order.getBillingAddress());
            } catch (ValidationException e) {
                validationErrors.add("Invalid billing address: " + e.getMessage());
            }
        }
        
        // Validate order items
        if (order.getItems() == null || order.getItems().isEmpty()) {
            validationErrors.add("Order must contain at least one item");
        } else {
            // Check if order has too many items
            if (order.getItems().size() > maximumOrderItems) {
                validationErrors.add("Order cannot contain more than " + maximumOrderItems + " items");
            }
            
            try {
                validateOrderItems(order);
            } catch (ValidationException e) {
                validationErrors.add(e.getMessage());
            }
        }
        
        // Validate order amount
        try {
            BigDecimal calculatedTotal = order.calculateTotal();
            validateOrderAmount(calculatedTotal);
            
            // Verify that the set total amount matches the calculated total
            if (order.getTotalAmount() != null && 
                    calculatedTotal.compareTo(order.getTotalAmount()) != 0) {
                validationErrors.add("Order total amount does not match calculated total: " + 
                        calculatedTotal + " vs " + order.getTotalAmount());
            }
        } catch (ValidationException e) {
            validationErrors.add(e.getMessage());
        }
        
        // If there are validation errors, throw exception with all errors
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Order validation failed: " + String.join("; ", validationErrors));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateOrderStatus(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        // Use the built-in validation logic in the OrderStatus enum
        return currentStatus.canTransitionTo(newStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateOrderItems(Order order) throws ValidationException {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            throw new ValidationException("Order must contain items");
        }
        
        List<String> itemValidationErrors = new ArrayList<>();
        boolean hasValidItems = false;
        
        for (OrderItem item : order.getItems()) {
            // Skip null items
            if (item == null) {
                continue;
            }
            
            hasValidItems = true;
            
            // Validate product ID
            if (item.getProductId() == null || item.getProductId() <= 0) {
                itemValidationErrors.add("Item has invalid product ID: " + item.getProductId());
                continue;
            }
            
            // Validate quantity
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                itemValidationErrors.add("Item with product ID " + item.getProductId() + 
                        " has invalid quantity: " + item.getQuantity());
                continue;
            }
            
            // Validate unit price
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                itemValidationErrors.add("Item with product ID " + item.getProductId() + 
                        " has invalid unit price: " + item.getUnitPrice());
                continue;
            }
            
            // Check product availability
            try {
                boolean isAvailable = checkProductAvailability(item.getProductId(), item.getQuantity());
                if (!isAvailable) {
                    itemValidationErrors.add("Product " + item.getProductId() + 
                            " is not available in quantity " + item.getQuantity());
                }
            } catch (Exception e) {
                // Log the exception and add validation error
                itemValidationErrors.add("Error checking availability for product " + 
                        item.getProductId() + ": " + e.getMessage());
            }
            
            // FIXME: Add price validation against current product prices in database
            // This requires a call to the product service to get current prices
            // Temporarily disabled until ProductService API is finalized
        }
        
        if (!hasValidItems) {
            throw new ValidationException("Order contains no valid items");
        }
        
        if (!itemValidationErrors.isEmpty()) {
            throw new ValidationException("Order items validation failed: " + 
                    String.join("; ", itemValidationErrors));
        }
    }
    
    /**
     * Checks if a product is available in the requested quantity.
     * 
     * @param productId The ID of the product
     * @param quantity The requested quantity
     * @return true if the product is available in the requested quantity, false otherwise
     */
    public boolean checkProductAvailability(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        try {
            // Call the product service to check availability
            return productService.isProductAvailable(productId, quantity);
        } catch (Exception e) {
            // Log the exception
            // TODO: Implement proper logging
            System.err.println("Error checking product availability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates that the shipping address is properly formatted and valid.
     * 
     * @param address The shipping address to validate
     * @throws ValidationException If the address is invalid
     */
    public void validateShippingAddress(String address) throws ValidationException {
        if (!StringUtils.hasText(address)) {
            throw new ValidationException("Shipping address cannot be empty");
        }
        
        // Basic validation for minimum length
        if (address.length() < 10) {
            throw new ValidationException("Shipping address is too short");
        }
        
        // TODO: Implement more sophisticated address validation
        // Commented out for now as the pattern may be too restrictive for international addresses
        // if (!ADDRESS_PATTERN.matcher(address).matches()) {
        //     throw new ValidationException("Shipping address format is invalid");
        // }
    }
    
    /**
     * Validates that the order total amount meets minimum requirements.
     * 
     * @param totalAmount The total amount of the order
     * @throws ValidationException If the total amount is below the minimum requirement
     */
    public void validateOrderAmount(BigDecimal totalAmount) throws ValidationException {
        if (totalAmount == null) {
            throw new ValidationException("Order total amount cannot be null");
        }
        
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Order total amount must be greater than zero");
        }
        
        if (totalAmount.compareTo(minimumOrderAmount) < 0) {
            throw new ValidationException("Order total amount must be at least " + minimumOrderAmount);
        }
    }
}