package com.gradlemedium100.common.exception;

import com.gradlemedium100.common.dto.ErrorResponse;

/**
 * Interface for handling errors consistently across the application.
 * 
 * This interface defines methods for handling different types of exceptions
 * and converting them into standardized error responses. Implementing classes
 * should provide consistent error handling behavior throughout the application.
 * 
 * @since 1.0
 */
public interface ErrorHandler {

    /**
     * Handles a general exception and returns an appropriate error response.
     * 
     * This method serves as a catch-all for exceptions that don't have specific
     * handlers. It should extract relevant information from the exception and
     * construct a standardized error response.
     *
     * @param exception the exception to handle
     * @return a standardized error response containing information from the exception
     */
    ErrorResponse handleException(Exception exception);
    
    /**
     * Handles a resource not found exception.
     * 
     * This method processes exceptions that occur when a requested resource cannot
     * be found. It should extract the resource type and ID information and create
     * a standardized 404 error response.
     *
     * @param exception the resource not found exception
     * @return a standardized error response for resource not found cases
     */
    ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception);
    
    /**
     * Handles a bad request exception.
     * 
     * This method processes exceptions that occur when a request contains invalid
     * data or parameters. It should incorporate any validation errors into the
     * response and create a standardized 400 error response.
     *
     * @param exception the bad request exception
     * @return a standardized error response for bad request cases
     */
    ErrorResponse handleBadRequestException(BadRequestException exception);
    
    // TODO: Add methods for handling other specific exception types as the application evolves
    
    // FIXME: Consider adding a method for handling business logic exceptions
}