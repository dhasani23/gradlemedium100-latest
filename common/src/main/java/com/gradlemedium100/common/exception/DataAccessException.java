package com.gradlemedium100.common.exception;

/**
 * Exception thrown when there is a problem accessing data from a data source.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DataAccessException with the specified message.
     *
     * @param message the data access error message
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataAccessException with the specified message and cause.
     *
     * @param message the data access error message
     * @param cause the cause of the data access failure
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataAccessException with the specified cause.
     *
     * @param cause the cause of the data access failure
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}