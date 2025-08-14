package com.gradlemedium100.notification.channel;

import com.gradlemedium100.notification.model.NotificationRequest;
import com.gradlemedium100.notification.model.NotificationResponse;
import com.gradlemedium100.notification.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of notification channel for in-app notifications.
 * This class handles sending notifications within the application UI.
 */
@Component
public class InAppChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(InAppChannel.class);
    
    /**
     * Send a notification via in-app channel
     *
     * @param request The notification request
     * @return Response with status of the notification delivery
     */
    @Override
    public NotificationResponse send(NotificationRequest request) {
        logger.debug("Sending in-app notification to user: {}", request.getRecipientId());
        
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(request.getId());
        
        try {
            // In a real implementation, this might:
            // 1. Store the notification in a database
            // 2. Push it to a WebSocket connection
            // 3. Add it to a user's notification feed
            
            // Simulating in-app notification storage and delivery
            boolean delivered = simulateInAppDelivery(request);
            
            if (delivered) {
                response.setStatus(NotificationStatus.DELIVERED);
                response.setMessage("In-app notification delivered");
            } else {
                response.setStatus(NotificationStatus.PENDING);
                response.setMessage("In-app notification queued for delivery");
            }
            
            logger.info("In-app notification {} for user {}", 
                    response.getStatus(), request.getRecipientId());
            
        } catch (Exception e) {
            logger.error("Error sending in-app notification", e);
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error sending in-app notification: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Send an in-app notification directly (convenience method)
     * 
     * @param userId The recipient user ID
     * @param subject The notification subject
     * @param message The notification message
     * @return Response with status of the notification delivery
     */
    public NotificationResponse sendInAppNotification(String userId, String subject, String message) {
        NotificationRequest request = new NotificationRequest();
        request.setId(java.util.UUID.randomUUID().toString());
        request.setRecipientId(userId);
        request.setSubject(subject);
        request.setContent(message);
        
        return send(request);
    }
    
    /**
     * Simulate in-app notification delivery (for testing/development)
     * 
     * @param request The notification request
     * @return true if delivery simulation succeeds
     */
    private boolean simulateInAppDelivery(NotificationRequest request) {
        // This is a placeholder for actual in-app notification logic
        logger.debug("Simulating in-app notification to user: {}", request.getRecipientId());
        
        // In a real system, we'd check if the user is currently online
        // For simulation, we'll assume users are online 70% of the time
        boolean userOnline = Math.random() > 0.3;
        
        // If user is online, notification is delivered immediately
        // Otherwise, it's stored for later delivery
        return userOnline;
    }
}