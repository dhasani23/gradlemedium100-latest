package com.gradlemedium100.orderservice.validation;

import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderStatus;
import com.gradlemedium100.common.exception.ValidationException;

/**
 * Interface for validating order data before processing.
 * 
 * This interface defines methods for validating various aspects of orders including:
 * - Overall order validation (data integrity and business rules)
 * - Order status transitions
 * - Order item validation (availability and pricing)
 * 
 * Implementations should provide thorough validation to ensure data consistency
 * and adherence to business rules before orders are processed.
 */
public interface OrderValidator {
    
    /**
     * Validates an order's data integrity and business rules.
     * 
     * This method performs comprehensive validation on an order entity including:
     * - Required fields are present and valid
     * - Order items are valid
     * - User information is valid
     * - Addresses are properly formatted
     * - Total amount calculations are accurate
     * 
     * @param order The order to validate
     * @throws ValidationException If validation fails with specific validation errors
     */
    void validateOrder(Order order) throws ValidationException;
    
    /**
     * Validates if a status transition is allowed based on the business rules.
     * 
     * This method checks if transitioning from the current status to the new status
     * is allowed according to the order workflow rules. For example, an order cannot
     * transition from CANCELED to SHIPPED.
     * 
     * @param currentStatus The current status of the order
     * @param newStatus The proposed new status
     * @return true if the status transition is allowed, false otherwise
     */
    boolean validateOrderStatus(OrderStatus currentStatus, OrderStatus newStatus);
    
    /**
     * Validates the items in an order for availability and pricing.
     * 
     * This method performs validation specific to order items:
     * - Verifies that products exist
     * - Checks if products are available in requested quantities
     * - Validates that pricing information matches current product prices
     * - Ensures discount calculations are correct
     * 
     * @param order The order containing items to validate
     * @throws ValidationException If validation fails with specific item validation errors
     */
    void validateOrderItems(Order order) throws ValidationException;
}