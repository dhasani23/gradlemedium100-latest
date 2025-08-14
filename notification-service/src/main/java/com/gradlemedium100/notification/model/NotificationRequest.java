package com.gradlemedium100.notification.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Data model representing a notification request with recipient, content, and channel information.
 * 
 * This class encapsulates all the necessary information needed to send a notification
 * to a recipient through various channels (email, SMS, or in-app).
 */
public class NotificationRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // Unique identifier for the notification request
    private String id;
    
    // ID of the recipient of the notification
    private String recipientId;
    
    // Email address of the recipient
    private String recipientEmail;
    
    // Phone number of the recipient
    private String recipientPhone;
    
    // Subject of the notification
    private String subject;
    
    // Content of the notification
    private String content;
    
    // Name of the template to use
    private String templateName;
    
    // Data to populate the template with
    private Map<String, Object> templateData;
    
    // Type of notification to send
    private NotificationType notificationType;
    
    // Timestamp when the notification request was created
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public NotificationRequest() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized constructor with essential fields.
     *
     * @param recipientId      ID of the recipient
     * @param subject          Subject of the notification
     * @param content          Content of the notification
     * @param notificationType Type of notification
     */
    public NotificationRequest(String recipientId, String subject, String content, NotificationType notificationType) {
        this();
        this.recipientId = recipientId;
        this.subject = subject;
        this.content = content;
        this.notificationType = notificationType;
    }

    /**
     * Gets the ID of the notification request.
     *
     * @return the notification request ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the notification request.
     *
     * @param id the notification request ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the recipient ID.
     *
     * @return the recipient ID
     */
    public String getRecipientId() {
        return recipientId;
    }

    /**
     * Sets the recipient ID.
     *
     * @param recipientId the recipient ID to set
     */
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    /**
     * Gets the recipient email address.
     *
     * @return the recipient email
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * Sets the recipient email address.
     *
     * @param recipientEmail the recipient email to set
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    /**
     * Gets the recipient phone number.
     *
     * @return the recipient phone number
     */
    public String getRecipientPhone() {
        return recipientPhone;
    }

    /**
     * Sets the recipient phone number.
     *
     * @param recipientPhone the recipient phone number to set
     */
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    /**
     * Gets the notification subject.
     *
     * @return the notification subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the notification subject.
     *
     * @param subject the notification subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the notification content.
     *
     * @return the notification content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the notification content.
     *
     * @param content the notification content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the template name.
     *
     * @return the template name
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets the template name.
     *
     * @param templateName the template name to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * Gets the template data.
     *
     * @return the template data map
     */
    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    /**
     * Sets the template data.
     *
     * @param templateData the template data map to set
     */
    public void setTemplateData(Map<String, Object> templateData) {
        this.templateData = templateData;
    }

    /**
     * Gets the notification type.
     *
     * @return the notification type
     */
    public NotificationType getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the notification type.
     *
     * @param notificationType the notification type to set
     */
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRequest that = (NotificationRequest) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(recipientId, that.recipientId) &&
               Objects.equals(subject, that.subject) &&
               notificationType == that.notificationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, recipientId, subject, notificationType);
    }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "id='" + id + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                ", subject='" + subject + '\'' +
                // Not including full content to avoid large strings in logs
                ", content='" + (content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : "null") + '\'' +
                ", templateName='" + templateName + '\'' +
                ", notificationType=" + notificationType +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * Builder class for creating NotificationRequest instances.
     */
    public static class Builder {
        private final NotificationRequest request;

        public Builder() {
            request = new NotificationRequest();
        }

        public Builder withId(String id) {
            request.id = id;
            return this;
        }

        public Builder withRecipientId(String recipientId) {
            request.recipientId = recipientId;
            return this;
        }

        public Builder withRecipientEmail(String recipientEmail) {
            request.recipientEmail = recipientEmail;
            return this;
        }

        public Builder withRecipientPhone(String recipientPhone) {
            request.recipientPhone = recipientPhone;
            return this;
        }

        public Builder withSubject(String subject) {
            request.subject = subject;
            return this;
        }

        public Builder withContent(String content) {
            request.content = content;
            return this;
        }

        public Builder withTemplateName(String templateName) {
            request.templateName = templateName;
            return this;
        }

        public Builder withTemplateData(Map<String, Object> templateData) {
            request.templateData = templateData;
            return this;
        }

        public Builder withNotificationType(NotificationType notificationType) {
            request.notificationType = notificationType;
            return this;
        }

        public NotificationRequest build() {
            // TODO: Add validation logic for required fields
            return request;
        }
    }
}