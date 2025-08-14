package com.gradlemedium100.notification.service;

import com.gradlemedium100.notification.channel.EmailChannel;
import com.gradlemedium100.notification.channel.SmsChannel;
import com.gradlemedium100.notification.channel.InAppChannel;
import com.gradlemedium100.notification.template.TemplateEngine;
import com.gradlemedium100.notification.model.NotificationRequest;
import com.gradlemedium100.notification.model.NotificationResponse;
import com.gradlemedium100.notification.model.NotificationType;
import com.gradlemedium100.notification.model.NotificationStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Core implementation of the notification service that orchestrates
 * the sending of notifications through various channels (email, SMS, in-app).
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    private final EmailChannel emailChannel;
    private final SmsChannel smsChannel;
    private final InAppChannel inAppChannel;
    private final TemplateEngine templateEngine;
    
    /**
     * Constructor for dependency injection of notification channels and template engine.
     */
    @Autowired
    public NotificationServiceImpl(EmailChannel emailChannel, 
                                  SmsChannel smsChannel,
                                  InAppChannel inAppChannel,
                                  TemplateEngine templateEngine) {
        this.emailChannel = emailChannel;
        this.smsChannel = smsChannel;
        this.inAppChannel = inAppChannel;
        this.templateEngine = templateEngine;
    }
    
    /**
     * Sends a notification based on the provided request.
     * 
     * @param request the notification request containing recipient and content details
     * @return response object with delivery status and timestamp
     */
    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        logger.debug("Processing notification request: {}", request.getId());
        
        NotificationResponse response = new NotificationResponse();
        response.setRequestId(request.getId());
        response.setId(UUID.randomUUID().toString());
        response.setTimestamp(LocalDateTime.now());
        
        try {
            // Process template if a template name is provided
            String content = request.getContent();
            if (request.getTemplateName() != null && !request.getTemplateName().isEmpty()) {
                content = processTemplate(request.getTemplateName(), request.getTemplateData());
                logger.debug("Template processed successfully: {}", request.getTemplateName());
            }
            
            // Update the content in the request
            request.setContent(content);
            
            // Select the appropriate channel and send notification
            Object channel = selectChannel(request.getNotificationType());
            NotificationResponse channelResponse = null;
            
            if (channel == emailChannel) {
                channelResponse = sendEmailNotification(request);
            } else if (channel == smsChannel) {
                channelResponse = sendSmsNotification(request);
            } else if (channel == inAppChannel) {
                channelResponse = sendInAppNotification(request);
            } else {
                logger.error("Unsupported notification type: {}", request.getNotificationType());
                response.setStatus(NotificationStatus.FAILED);
                response.setMessage("Unsupported notification type");
                return response;
            }
            
            if (channelResponse != null && channelResponse.getStatus() == NotificationStatus.DELIVERED) {
                response.setStatus(NotificationStatus.SENT);
                response.setMessage("Notification sent successfully");
            } else {
                response.setStatus(NotificationStatus.FAILED);
                response.setMessage("Failed to send notification through selected channel");
            }
            
        } catch (Exception e) {
            logger.error("Error sending notification: {}", e.getMessage(), e);
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Sends multiple notifications in bulk.
     * For better performance, notifications are processed in parallel when possible.
     * 
     * @param requests list of notification requests to process
     * @return list of notification responses
     */
    @Override
    public List<NotificationResponse> sendBulkNotifications(List<NotificationRequest> requests) {
        logger.info("Processing bulk notification request with {} items", requests.size());
        
        // FIXME: Add circuit breaker pattern to prevent overwhelming channels
        
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Process notifications in parallel for better performance
        List<CompletableFuture<NotificationResponse>> futures = requests.stream()
            .map(request -> CompletableFuture.supplyAsync(() -> sendNotification(request)))
            .collect(Collectors.toList());
        
        // Wait for all notifications to complete and collect results
        List<NotificationResponse> responses = new ArrayList<>(requests.size());
        
        for (CompletableFuture<NotificationResponse> future : futures) {
            try {
                responses.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error processing notification in bulk operation: {}", e.getMessage());
                
                // Create a failed response for tracking
                NotificationResponse errorResponse = new NotificationResponse();
                errorResponse.setId(UUID.randomUUID().toString());
                errorResponse.setStatus(NotificationStatus.FAILED);
                errorResponse.setMessage("Processing error: " + e.getMessage());
                errorResponse.setTimestamp(LocalDateTime.now());
                responses.add(errorResponse);
                
                // Restore interrupted state
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        logger.info("Bulk notification processing completed. Success: {}, Failed: {}", 
                    responses.stream().filter(r -> r.getStatus() == NotificationStatus.SENT).count(),
                    responses.stream().filter(r -> r.getStatus() == NotificationStatus.FAILED).count());
        
        return responses;
    }
    
    /**
     * Processes a notification template with provided data.
     * 
     * @param templateName name of the template to use
     * @param data key-value pairs to populate the template
     * @return processed content string
     */
    public String processTemplate(String templateName, Map<String, Object> data) {
        if (templateName == null || templateName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        
        // Delegate template processing to the template engine
        try {
            return templateEngine.processTemplate(templateName, data);
        } catch (Exception e) {
            logger.error("Error processing template {}: {}", templateName, e.getMessage());
            // TODO: Implement fallback template handling
            throw new RuntimeException("Failed to process template: " + templateName, e);
        }
    }
    
    /**
     * Selects the appropriate notification channel based on notification type.
     * 
     * @param notificationType the type of notification to send
     * @return the appropriate channel handler object
     */
    public Object selectChannel(NotificationType notificationType) {
        if (notificationType == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        switch (notificationType) {
            case EMAIL:
                return emailChannel;
            case SMS:
                return smsChannel;
            case IN_APP:
                return inAppChannel;
            default:
                logger.warn("Unknown notification type: {}", notificationType);
                throw new IllegalArgumentException("Unsupported notification type: " + notificationType);
        }
    }
    
    /**
     * Helper method to send email notification.
     */
    private NotificationResponse sendEmailNotification(NotificationRequest request) {
        if (request.getRecipientEmail() == null || request.getRecipientEmail().isEmpty()) {
            logger.error("Recipient email is required for EMAIL notification type");
            NotificationResponse response = new NotificationResponse();
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Recipient email is required");
            return response;
        }
        
        try {
            logger.debug("Sending email notification to {}", request.getRecipientEmail());
            // Delegate to email channel implementation
            return emailChannel.sendEmail(
                request.getRecipientEmail(),
                request.getSubject(),
                request.getContent()
            );
        } catch (Exception e) {
            logger.error("Failed to send email notification: {}", e.getMessage(), e);
            NotificationResponse response = new NotificationResponse();
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Helper method to send SMS notification.
     */
    private NotificationResponse sendSmsNotification(NotificationRequest request) {
        if (request.getRecipientPhone() == null || request.getRecipientPhone().isEmpty()) {
            logger.error("Recipient phone number is required for SMS notification type");
            NotificationResponse response = new NotificationResponse();
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Recipient phone is required");
            return response;
        }
        
        try {
            logger.debug("Sending SMS notification to {}", request.getRecipientPhone());
            // Delegate to SMS channel implementation
            return smsChannel.sendSms(
                request.getRecipientPhone(),
                request.getContent()
            );
        } catch (Exception e) {
            logger.error("Failed to send SMS notification: {}", e.getMessage(), e);
            NotificationResponse response = new NotificationResponse();
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Helper method to send in-app notification.
     */
    private NotificationResponse sendInAppNotification(NotificationRequest request) {
        if (request.getRecipientId() == null || request.getRecipientId().isEmpty()) {
            logger.error("Recipient ID is required for IN_APP notification type");
            NotificationResponse response = new NotificationResponse();
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Recipient ID is required");
            return response;
        }
        
        try {
            logger.debug("Sending in-app notification to user {}", request.getRecipientId());
            // Delegate to in-app channel implementation
            return inAppChannel.sendInAppNotification(
                request.getRecipientId(),
                request.getSubject(),
                request.getContent()
            );
        } catch (Exception e) {
            logger.error("Failed to send in-app notification: {}", e.getMessage(), e);
            NotificationResponse response = new NotificationResponse();
            response.setStatus(NotificationStatus.FAILED);
            response.setMessage("Error: " + e.getMessage());
            return response;
        }
    }
}