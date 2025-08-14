package com.gradlemedium100.notification.model;

/**
 * Enumeration of different notification types supported by the notification service.
 * 
 * This enum represents the various channels through which notifications can be sent
 * to users within the application.
 * 
 * @author Notification Service Team
 * @version 1.0
 */
public enum NotificationType {
    
    /**
     * Notification sent via email to the user's registered email address.
     * Used for important notifications that should be delivered even when the user is offline.
     */
    EMAIL,
    
    /**
     * Notification sent via SMS to the user's registered mobile number.
     * Used for time-sensitive notifications that require immediate attention.
     */
    SMS,
    
    /**
     * Notification displayed within the application UI.
     * Used for less critical notifications that can be viewed when the user is actively using the application.
     */
    IN_APP;
    
    /**
     * Determines if the notification type requires external delivery mechanisms.
     * 
     * @return true if notification is delivered outside the application, false otherwise
     */
    public boolean isExternal() {
        return this == EMAIL || this == SMS;
    }
    
    /**
     * Determines if the notification type requires immediate delivery.
     * 
     * @return true if notification should be delivered with high priority, false otherwise
     */
    public boolean isUrgent() {
        switch (this) {
            case SMS:
                return true;
            case EMAIL:
                // FIXME: Email urgency should be configurable based on content
                return false;
            case IN_APP:
                return false;
            default:
                // TODO: Handle new notification types as they're added
                return false;
        }
    }
    
    /**
     * Returns a user-friendly display name for this notification type.
     * 
     * @return A string representation suitable for display in UI
     */
    public String getDisplayName() {
        switch (this) {
            case EMAIL:
                return "Email";
            case SMS:
                return "Text Message";
            case IN_APP:
                return "Application Notification";
            default:
                return this.name();
        }
    }
    
    /**
     * Finds a notification type by its standard identifier code.
     * 
     * @param code The identifier code for the notification type
     * @return The matching NotificationType or null if not found
     */
    public static NotificationType findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        try {
            return NotificationType.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            // TODO: Add logging for invalid notification type codes
            return null;
        }
    }
}