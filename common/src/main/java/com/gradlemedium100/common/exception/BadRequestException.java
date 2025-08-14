package com.gradlemedium100.common.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when a request contains invalid parameters or data.
 * This exception is used to signal that a client request could not be processed
 * due to validation errors or other issues with the request content.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Serial version UID for serialization
     */
    private static final long serialVersionUID = 1287346542176354572L;
    
    /**
     * List of validation errors associated with this exception
     */
    private List<String> errors;

    /**
     * Constructor with message
     * 
     * @param message the error message
     */
    public BadRequestException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    /**
     * Constructor with message and list of errors
     * 
     * @param message the error message
     * @param errors the list of validation errors
     */
    public BadRequestException(String message, List<String> errors) {
        super(message);
        this.errors = new ArrayList<>();
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }

    /**
     * Constructor with message and cause
     * 
     * @param message the error message
     * @param cause the throwable that caused this exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.errors = new ArrayList<>();
    }

    /**
     * Gets the list of validation errors
     * 
     * @return an unmodifiable list of validation errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Adds a validation error to the list
     * 
     * @param error the error message to add
     */
    public void addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            this.errors.add(error);
        } else {
            // FIXME: Should we throw an exception when attempting to add a null/empty error?
            // For now, silently ignore empty error messages
        }
    }
    
    /**
     * Returns a string representation of this exception including the error list
     * 
     * @return a detailed string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        
        if (!errors.isEmpty()) {
            sb.append("\nValidation errors:");
            for (String error : errors) {
                sb.append("\n - ").append(error);
            }
        }
        
        return sb.toString();
    }
    
    // TODO: Add method to categorize errors by severity (e.g., warnings vs errors)
    // TODO: Consider adding support for internationalized error messages
}