package com.gradlemedium100.notification.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data model representing the response of a notification delivery attempt.
 * This class contains information about the result of processing a notification request
 * including its status, any messages, and timestamps.
 */
public class NotificationResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String requestId;
    private String notificationId;  // Added for compatibility
    private NotificationStatus status;
    private String message;
    private LocalDateTime timestamp;
    
    /**
     * Default constructor
     */
    public NotificationResponse() {
        // Initialize with current timestamp by default
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructs a notification response with the specified parameters
     * 
     * @param id Unique identifier for the response
     * @param requestId ID of the corresponding notification request
     * @param status Status of the notification delivery
     * @param message Message associated with the response
     */
    public NotificationResponse(String id, String requestId, NotificationStatus status, String message) {
        this.id = id;
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructs a notification response with the specified parameters including timestamp
     * 
     * @param id Unique identifier for the response
     * @param requestId ID of the corresponding notification request
     * @param status Status of the notification delivery
     * @param message Message associated with the response
     * @param timestamp Timestamp when the notification was processed
     */
    public NotificationResponse(String id, String requestId, NotificationStatus status, String message, LocalDateTime timestamp) {
        this.id = id;
        this.requestId = requestId;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    /**
     * Gets the ID of the notification response
     * 
     * @return The notification response ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of the notification response
     * 
     * @param id The notification response ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the request ID
     * 
     * @return The request ID associated with this response
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Sets the request ID
     * 
     * @param requestId The request ID to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    /**
     * Gets the notification ID
     * 
     * @return The notification ID
     */
    public String getNotificationId() {
        return notificationId;
    }
    
    /**
     * Sets the notification ID
     * 
     * @param notificationId The notification ID to set
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    /**
     * Gets the notification status
     * 
     * @return The status of the notification delivery
     */
    public NotificationStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the notification status
     * 
     * @param status The status to set
     */
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the message associated with the response
     * 
     * @return The message text
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the message associated with the response
     * 
     * @param message The message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Gets the timestamp when the notification was processed
     * 
     * @return The processing timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Sets the timestamp when the notification was processed
     * 
     * @param timestamp The timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Creates a success response with the given request ID
     * 
     * @param requestId The request ID
     * @return A new notification response with SENT status
     */
    public static NotificationResponse createSuccessResponse(String requestId) {
        // TODO: Generate a unique ID for the response
        return new NotificationResponse(
            "resp-" + System.currentTimeMillis(),
            requestId,
            NotificationStatus.SENT,
            "Notification sent successfully"
        );
    }
    
    /**
     * Creates a failure response with the given request ID and error message
     * 
     * @param requestId The request ID
     * @param errorMessage The error message
     * @return A new notification response with FAILED status
     */
    public static NotificationResponse createFailureResponse(String requestId, String errorMessage) {
        // TODO: Generate a unique ID for the response
        return new NotificationResponse(
            "resp-" + System.currentTimeMillis(),
            requestId,
            NotificationStatus.FAILED,
            errorMessage
        );
    }
    
    /**
     * Creates a pending response with the given request ID
     * 
     * @param requestId The request ID
     * @return A new notification response with PENDING status
     */
    public static NotificationResponse createPendingResponse(String requestId) {
        // TODO: Generate a unique ID for the response
        return new NotificationResponse(
            "resp-" + System.currentTimeMillis(),
            requestId,
            NotificationStatus.PENDING,
            "Notification is queued for delivery"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationResponse response = (NotificationResponse) o;
        return Objects.equals(id, response.id) &&
               Objects.equals(requestId, response.requestId) &&
               status == response.status &&
               Objects.equals(message, response.message) &&
               Objects.equals(timestamp, response.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, status, message, timestamp);
    }

    @Override
    public String toString() {
        return "NotificationResponse{" +
               "id='" + id + '\'' +
               ", requestId='" + requestId + '\'' +
               ", status=" + status +
               ", message='" + message + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}