package com.gradlemedium100.notification.channel;

import com.gradlemedium100.notification.model.NotificationRequest;
import com.gradlemedium100.notification.model.NotificationResponse;
import com.gradlemedium100.notification.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation of notification channel for SMS delivery.
 * This class handles sending notifications via SMS.
 */
@Component
public class SmsChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(SmsChannel.class);
    
    @Value("${notification.sms.provider:default}")
    private String smsProvider;
    
    /**
     * Send a notification via SMS channel
     *
     * @param request The notification request
     * @return Response with status of the notification delivery
     */
    @Override
    public NotificationResponse send(NotificationRequest request) {
        logger.debug("Sending SMS notification to: {}", request.getRecipientPhone());
        
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(request.getId());
        
        try {
            // In a real implementation, this would integrate with an SMS service
            // like Twilio, Nexmo, or other SMS gateways
            
            // Simulating SMS sending logic
            boolean delivered = simulateSmsDelivery(request);
            
            if (delivered) {
                response.setStatus(NotificationStatus.DELIVERED);
                response.setMessage("SMS sent successfully");
            } else {
                response.setStatus(NotificationStatus.FAILED);
                response.setMessage("Failed to send SMS");
            }
            
            logger.info("SMS notification {} to {}", 
                    response.getStatus(), request.getRecipientPhone());
            
        } catch (Exception e) {
            logger.error("Error sending SMS notification", e);
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error sending SMS: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Send an SMS directly (convenience method)
     * 
     * @param to The recipient phone number
     * @param message The SMS message
     * @return Response with status of the SMS delivery
     */
    public NotificationResponse sendSms(String to, String message) {
        NotificationRequest request = new NotificationRequest();
        request.setId(java.util.UUID.randomUUID().toString());
        request.setRecipientPhone(to);
        request.setContent(message);
        
        return send(request);
    }
    
    /**
     * Simulate SMS delivery (for testing/development)
     * 
     * @param request The notification request
     * @return true if delivery simulation succeeds
     */
    private boolean simulateSmsDelivery(NotificationRequest request) {
        // This is a placeholder for actual SMS sending logic
        logger.debug("Simulating SMS to: {}", request.getRecipientPhone());
        
        // For demonstration, we'll succeed 90% of the time
        return Math.random() > 0.1;
    }
}