package com.gradlemedium100.notification.channel;

import com.gradlemedium100.notification.model.NotificationRequest;
import com.gradlemedium100.notification.model.NotificationResponse;

/**
 * Interface for notification channel implementations.
 * All notification delivery channels should implement this interface.
 */
public interface NotificationChannel {
    
    /**
     * Send a notification through this channel
     *
     * @param request The notification request with details
     * @return Response with status of the notification delivery
     */
    NotificationResponse send(NotificationRequest request);
}