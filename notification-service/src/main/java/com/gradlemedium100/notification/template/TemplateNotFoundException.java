package com.gradlemedium100.notification.template;

/**
 * Exception thrown when a requested template cannot be found in the template registry.
 */
public class TemplateNotFoundException extends RuntimeException {

    /**
     * Constructs a new TemplateNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public TemplateNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new TemplateNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}