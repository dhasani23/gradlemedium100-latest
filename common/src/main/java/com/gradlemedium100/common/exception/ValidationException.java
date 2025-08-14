package com.gradlemedium100.common.exception;

/**
 * Exception thrown when validation of data fails.
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified message.
     *
     * @param message the validation error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified message and cause.
     *
     * @param message the validation error message
     * @param cause the cause of the validation failure
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ValidationException with the specified cause.
     *
     * @param cause the cause of the validation failure
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }
}