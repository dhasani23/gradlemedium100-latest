package com.gradlemedium100.notification.service;

import com.gradlemedium100.notification.model.NotificationRequest;
import com.gradlemedium100.notification.model.NotificationResponse;

import java.util.List;

/**
 * NotificationService defines the contract for sending notifications through different channels.
 * Implementations of this interface should handle the delivery of notifications via
 * various channels such as email, SMS, push notifications, etc.
 * 
 * @since 1.0
 */
public interface NotificationService {

    /**
     * Sends a notification based on the provided request.
     * 
     * @param request The notification request containing all details needed for sending
     * @return A response object with the status and details of the notification delivery
     * @throws IllegalArgumentException if the request is null or contains invalid data
     * @throws NotificationDeliveryException if there's an error during notification delivery
     */
    NotificationResponse sendNotification(NotificationRequest request);
    
    /**
     * Sends multiple notifications in bulk.
     * This method optimizes sending multiple notifications by potentially batching them
     * or using parallel processing depending on the implementation.
     * 
     * @param requests List of notification requests to be processed
     * @return List of notification responses corresponding to each request in the same order
     * @throws IllegalArgumentException if the requests list is null or contains invalid entries
     * @throws NotificationDeliveryException if there's an error during notification delivery
     * 
     * TODO: Consider adding parameters for controlling batch size and concurrency
     */
    List<NotificationResponse> sendBulkNotifications(List<NotificationRequest> requests);
    
    /**
     * Checks if a notification channel is currently available.
     * 
     * @param channelType The type of channel to check
     * @return true if the channel is available, false otherwise
     * 
     * TODO: Add channel type enum or constant class for standardization
     */
    default boolean isChannelAvailable(String channelType) {
        // Default implementation returns true
        // Implementations should override this method with actual channel availability check
        return true;
    }
    
    /**
     * Cancels a previously scheduled notification if it hasn't been sent yet.
     * 
     * @param notificationId The unique identifier of the notification to cancel
     * @return true if the notification was successfully canceled, false if it couldn't be canceled
     * 
     * FIXME: Need to standardize notification IDs across different channel implementations
     */
    default boolean cancelNotification(String notificationId) {
        // Default implementation returns false indicating cancellation is not supported
        return false;
    }
}