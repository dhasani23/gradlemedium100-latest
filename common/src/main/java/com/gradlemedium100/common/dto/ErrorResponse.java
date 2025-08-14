package com.gradlemedium100.common.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data transfer object representing a standardized error response.
 * Used for consistent error reporting across the application.
 */
public class ErrorResponse implements Serializable {
    
    /** Serial version UID for serialization */
    private static final long serialVersionUID = 8675309L;
    
    /** Timestamp when the error occurred */
    private long timestamp;
    
    /** HTTP status code */
    private int status;
    
    /** Application-specific error code */
    private String errorCode;
    
    /** Error message */
    private String message;
    
    /** List of detailed error messages */
    private List<String> errors;
    
    /** Request path that caused the error */
    private String path;

    /**
     * Default constructor.
     * Initializes timestamp to current time and creates an empty errors list.
     */
    public ErrorResponse() {
        this.timestamp = Instant.now().toEpochMilli();
        this.errors = new ArrayList<>();
    }

    /**
     * Constructor with status, error code, and message.
     *
     * @param status    HTTP status code
     * @param errorCode Application-specific error code
     * @param message   Error message
     */
    public ErrorResponse(int status, String errorCode, String message) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * Constructor with status, error code, message, and list of errors.
     *
     * @param status    HTTP status code
     * @param errorCode Application-specific error code
     * @param message   Error message
     * @param errors    List of detailed error messages
     */
    public ErrorResponse(int status, String errorCode, String message, List<String> errors) {
        this(status, errorCode, message);
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }

    /**
     * Creates a new builder for ErrorResponse.
     *
     * @return New instance of ErrorResponse.Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the timestamp when the error occurred.
     *
     * @return the timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the error occurred.
     *
     * @param timestamp the timestamp in milliseconds since epoch
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the HTTP status code.
     *
     * @return the HTTP status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param status the HTTP status code to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the application-specific error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the application-specific error code.
     *
     * @param errorCode the error code to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message.
     *
     * @param message the error message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the list of detailed error messages.
     *
     * @return unmodifiable list of errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Sets the list of detailed error messages.
     *
     * @param errors the list of errors to set
     */
    public void setErrors(List<String> errors) {
        this.errors = new ArrayList<>();
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }

    /**
     * Gets the request path that caused the error.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the request path that caused the error.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Add a single error to the errors list.
     * 
     * @param error the error message to add
     * @return this ErrorResponse instance for method chaining
     */
    public ErrorResponse addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            this.errors.add(error);
        }
        return this;
    }

    /**
     * Builder pattern implementation for ErrorResponse.
     * Provides a fluent API for constructing ErrorResponse objects.
     */
    public static class Builder {
        private long timestamp;
        private int status;
        private String errorCode;
        private String message;
        private List<String> errors = new ArrayList<>();
        private String path;

        /**
         * Default constructor initializes timestamp to current time.
         */
        public Builder() {
            this.timestamp = Instant.now().toEpochMilli();
        }

        /**
         * Sets the HTTP status code.
         *
         * @param status the HTTP status code
         * @return the builder
         */
        public Builder status(int status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the application-specific error code.
         *
         * @param errorCode the error code
         * @return the builder
         */
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * Sets the error message.
         *
         * @param message the error message
         * @return the builder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the timestamp when the error occurred.
         *
         * @param timestamp the timestamp in milliseconds since epoch
         * @return the builder
         */
        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Sets the list of detailed error messages.
         *
         * @param errors the list of errors
         * @return the builder
         */
        public Builder errors(List<String> errors) {
            if (errors != null) {
                this.errors.addAll(errors);
            }
            return this;
        }

        /**
         * Adds a single error to the errors list.
         *
         * @param error the error message
         * @return the builder
         */
        public Builder addError(String error) {
            if (error != null && !error.trim().isEmpty()) {
                this.errors.add(error);
            }
            return this;
        }

        /**
         * Sets the request path that caused the error.
         *
         * @param path the path
         * @return the builder
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Builds the ErrorResponse instance.
         *
         * @return a new ErrorResponse instance
         */
        public ErrorResponse build() {
            ErrorResponse response = new ErrorResponse();
            response.timestamp = this.timestamp;
            response.status = this.status;
            response.errorCode = this.errorCode;
            response.message = this.message;
            response.errors = new ArrayList<>(this.errors);
            response.path = this.path;
            return response;
        }
    }

    /**
     * Returns a string representation of the ErrorResponse.
     * 
     * @return string representation of the object
     */
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", path='" + path + '\'' +
                '}';
    }
    
    // TODO: Add method to categorize errors by severity
    
    // FIXME: Consider implementing equals() and hashCode() methods
}