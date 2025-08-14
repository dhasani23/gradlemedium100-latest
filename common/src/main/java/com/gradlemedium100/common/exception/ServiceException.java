package com.gradlemedium100.common.exception;

/**
 * Exception thrown when there is a problem with a service operation.
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ServiceException with the specified message.
     *
     * @param message the service error message
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new ServiceException with the specified message and cause.
     *
     * @param message the service error message
     * @param cause the cause of the service failure
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ServiceException with the specified cause.
     *
     * @param cause the cause of the service failure
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }
}