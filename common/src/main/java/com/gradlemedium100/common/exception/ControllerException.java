package com.gradlemedium100.common.exception;

/**
 * Exception thrown when there is a problem with a controller operation.
 */
public class ControllerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ControllerException with the specified message.
     *
     * @param message the controller error message
     */
    public ControllerException(String message) {
        super(message);
    }

    /**
     * Constructs a new ControllerException with the specified message and cause.
     *
     * @param message the controller error message
     * @param cause the cause of the controller failure
     */
    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ControllerException with the specified cause.
     *
     * @param cause the cause of the controller failure
     */
    public ControllerException(Throwable cause) {
        super(cause);
    }
}