package com.gradlemedium100.notification.template;

/**
 * Exception thrown when an error occurs during template processing.
 */
public class TemplateProcessingException extends RuntimeException {

    /**
     * Constructs a new TemplateProcessingException with the specified detail message.
     *
     * @param message the detail message
     */
    public TemplateProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs a new TemplateProcessingException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public TemplateProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}