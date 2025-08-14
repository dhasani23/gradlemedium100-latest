package com.gradlemedium100.notification.model;

/**
 * Enumeration of notification delivery statuses.
 * Represents the current status of a notification in the delivery pipeline.
 * 
 * @since 1.0
 */
public enum NotificationStatus {
    
    /**
     * Notification was successfully sent to the recipient.
     * This status indicates the notification delivery was completed.
     */
    SENT("Delivered"),
    
    /**
     * Notification sending failed due to an error.
     * This could be due to network issues, invalid recipient, etc.
     */
    FAILED("Failed"),
    
    /**
     * Notification is pending delivery.
     * This is the initial state when a notification is created but not yet processed.
     */
    PENDING("Pending"),
    
    /**
     * Notification was delivered to the recipient.
     * This status indicates the notification delivery was completed.
     */
    DELIVERED("Delivered");
    
    private final String displayValue;
    
    /**
     * Constructor for NotificationStatus enum.
     *
     * @param displayValue The human-readable display value for the status
     */
    NotificationStatus(String displayValue) {
        this.displayValue = displayValue;
    }
    
    /**
     * Returns the display value suitable for UI presentation.
     *
     * @return The human-readable status value
     */
    public String getDisplayValue() {
        return displayValue;
    }
    
    /**
     * Checks if the notification delivery was successful.
     * 
     * @return true if status is SENT, false otherwise
     */
    public boolean isSuccessful() {
        return this == SENT || this == DELIVERED;
    }
    
    /**
     * Checks if the notification delivery needs retry.
     * 
     * @return true if status is FAILED and might be retriable, false otherwise
     */
    public boolean needsRetry() {
        return this == FAILED;
    }
    
    /**
     * Checks if the notification is still waiting to be processed.
     * 
     * @return true if status is PENDING, false otherwise
     */
    public boolean isPending() {
        return this == PENDING;
    }
    
    /**
     * Converts a string representation to its corresponding NotificationStatus.
     * This is more robust than valueOf as it handles case insensitivity.
     *
     * @param status The string representation of the status
     * @return The corresponding NotificationStatus or PENDING if not found
     */
    public static NotificationStatus fromString(String status) {
        if (status == null || status.isEmpty()) {
            return PENDING; // Default to PENDING if no status provided
        }
        
        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // FIXME: Consider proper logging here instead of just returning a default
            return PENDING;
        }
    }
    
    // TODO: Add method to convert from external API status codes
    // TODO: Add internationalization support for display values
}