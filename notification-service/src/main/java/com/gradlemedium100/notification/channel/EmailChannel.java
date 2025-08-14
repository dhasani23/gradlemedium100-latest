package com.gradlemedium100.notification.channel;

import com.gradlemedium100.notification.model.NotificationRequest;
import com.gradlemedium100.notification.model.NotificationResponse;
import com.gradlemedium100.notification.model.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation of notification channel for email delivery.
 * This class handles sending notifications via email.
 */
@Component
public class EmailChannel implements NotificationChannel {

    private static final Logger logger = LoggerFactory.getLogger(EmailChannel.class);
    
    @Value("${notification.email.from:no-reply@example.com}")
    private String defaultFromEmail;
    
    /**
     * Send a notification via email channel
     *
     * @param request The notification request
     * @return Response with status of the notification delivery
     */
    @Override
    public NotificationResponse send(NotificationRequest request) {
        logger.debug("Sending email notification to: {}", request.getRecipientEmail());
        
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(request.getId());
        
        try {
            // In a real implementation, this would integrate with an email service
            // like SendGrid, Amazon SES, or a local SMTP server
            
            // Simulating email sending logic
            boolean delivered = simulateEmailDelivery(request);
            
            if (delivered) {
                response.setStatus(NotificationStatus.DELIVERED);
                response.setMessage("Email sent successfully");
            } else {
                response.setStatus(NotificationStatus.FAILED);
                response.setMessage("Failed to send email");
            }
            
            logger.info("Email notification {} to {} with subject: {}", 
                    response.getStatus(), request.getRecipientEmail(), request.getSubject());
            
        } catch (Exception e) {
            logger.error("Error sending email notification", e);
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error sending email: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Send an email directly (convenience method)
     * 
     * @param to The recipient email address
     * @param subject The email subject
     * @param body The email body
     * @return Response with status of the email delivery
     */
    public NotificationResponse sendEmail(String to, String subject, String body) {
        NotificationRequest request = new NotificationRequest();
        request.setId(java.util.UUID.randomUUID().toString());
        request.setRecipientEmail(to);
        request.setSubject(subject);
        request.setContent(body);
        
        return send(request);
    }
    
    /**
     * Simulate email delivery (for testing/development)
     * 
     * @param request The notification request
     * @return true if delivery simulation succeeds
     */
    private boolean simulateEmailDelivery(NotificationRequest request) {
        // This is a placeholder for actual email sending logic
        logger.debug("Simulating email to: {} with subject: {}", 
                request.getRecipientEmail(), request.getSubject());
        
        // For demonstration, we'll succeed 95% of the time
        return Math.random() > 0.05;
    }
}