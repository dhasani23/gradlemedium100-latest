package com.gradlemedium100.notification.template;

/**
 * Exception thrown when an error occurs while accessing the template registry.
 */
public class TemplateRegistryException extends RuntimeException {

    /**
     * Constructs a new TemplateRegistryException with the specified detail message.
     *
     * @param message the detail message
     */
    public TemplateRegistryException(String message) {
        super(message);
    }

    /**
     * Constructs a new TemplateRegistryException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public TemplateRegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}