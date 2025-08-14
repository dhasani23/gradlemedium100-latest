package com.gradlemedium100.orderservice.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumeration of possible order statuses in the order lifecycle.
 * Each status represents a specific stage in the order processing workflow.
 */
public enum OrderStatus {
    
    /**
     * Order has been created but not processed yet
     */
    CREATED(false, 
            new OrderStatus[]{}),
    
    /**
     * Order is waiting for payment confirmation
     */
    PAYMENT_PENDING(false, 
                   new OrderStatus[]{CREATED}),
    
    /**
     * Payment has been confirmed for the order
     */
    PAYMENT_CONFIRMED(false, 
                     new OrderStatus[]{PAYMENT_PENDING}),
    
    /**
     * Order is being processed (items gathered, packaged, etc.)
     */
    PROCESSING(false, 
              new OrderStatus[]{PAYMENT_CONFIRMED}),
    
    /**
     * Order has been shipped to the delivery address
     */
    SHIPPED(false, 
           new OrderStatus[]{PROCESSING}),
    
    /**
     * Order has been successfully delivered to the customer
     */
    DELIVERED(true, 
             new OrderStatus[]{SHIPPED}),
    
    /**
     * Order has been canceled
     */
    CANCELED(true, 
            new OrderStatus[]{CREATED, PAYMENT_PENDING, PAYMENT_CONFIRMED, PROCESSING}),
    
    /**
     * Order has been refunded
     */
    REFUNDED(true, 
            new OrderStatus[]{PAYMENT_CONFIRMED, PROCESSING, SHIPPED, DELIVERED});

    private final boolean isTerminalState;
    private final Set<OrderStatus> allowedPreviousStates;

    /**
     * Constructor for OrderStatus enum.
     *
     * @param isTerminalState true if this status represents an end state in the order lifecycle
     * @param allowedPreviousStates array of statuses that can transition to this status
     */
    OrderStatus(boolean isTerminalState, OrderStatus[] allowedPreviousStates) {
        this.isTerminalState = isTerminalState;
        this.allowedPreviousStates = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(allowedPreviousStates)));
    }

    /**
     * Returns true if this status represents an end state in the order lifecycle.
     * Terminal states are those from which no further regular transitions are expected.
     *
     * @return true if this is a terminal state, false otherwise
     */
    public boolean isTerminalState() {
        return isTerminalState;
    }

    /**
     * Checks if a transition from the current status to the next status is valid.
     * The validation is based on predefined transition rules for each status.
     *
     * @param nextStatus the status to which we want to transition
     * @return true if the transition is valid, false otherwise
     * @throws IllegalArgumentException if nextStatus is null
     */
    public boolean canTransitionTo(OrderStatus nextStatus) {
        if (nextStatus == null) {
            throw new IllegalArgumentException("Next status cannot be null");
        }

        // If this is already a terminal state, no further transitions are allowed
        if (isTerminalState()) {
            return false;
        }

        // Check if this status is in the list of allowed previous states for the next status
        return nextStatus.allowedPreviousStates.contains(this);
    }

    /**
     * Utility method to get all valid next statuses from the current status.
     * 
     * @return Set of OrderStatus values that can be transitioned to from this status
     */
    public Set<OrderStatus> getValidNextStatuses() {
        if (isTerminalState()) {
            return Collections.emptySet();
        }
        
        Set<OrderStatus> validNextStatuses = new HashSet<>();
        for (OrderStatus status : OrderStatus.values()) {
            if (canTransitionTo(status)) {
                validNextStatuses.add(status);
            }
        }
        return Collections.unmodifiableSet(validNextStatuses);
    }

    // TODO: Consider adding history tracking for state transitions
    // FIXME: The transition rules might need adjustments based on business requirements
}