package com.gradlemedium100.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gradlemedium100.common.dto.ErrorResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Centralized exception handler for converting exceptions to appropriate HTTP responses.
 * 
 * This class handles exceptions thrown by controllers and other components, converting
 * them into standardized error responses with appropriate HTTP status codes.
 * It implements the ErrorHandler interface to provide consistent exception handling
 * across the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler implements ErrorHandler {
    
    /**
     * Logger instance for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Error code constants
     */
    private static final String GENERAL_ERROR_CODE = "ERR-SYS-001";
    private static final String NOT_FOUND_ERROR_CODE = "ERR-RES-001";
    private static final String BAD_REQUEST_ERROR_CODE = "ERR-VAL-001";

    /**
     * Handles general exceptions and returns a 500 Internal Server Error response.
     * 
     * This is a catch-all handler for exceptions that don't have specific handlers.
     *
     * @param exception the exception to handle
     * @return a standardized error response
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception exception) {
        logException(exception, GENERAL_ERROR_CODE);
        
        String message = "An unexpected error occurred while processing your request";
        List<String> errors = new ArrayList<>();
        
        // Add exception message as detail, but don't expose stack trace to clients
        errors.add(exception.getMessage());
        
        return buildErrorResponse(GENERAL_ERROR_CODE, message, errors);
    }
    
    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param exception the resource not found exception
     * @return a standardized error response for not found errors
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
        logException(exception, NOT_FOUND_ERROR_CODE);
        
        String message = exception.getMessage();
        List<String> errors = new ArrayList<>();
        
        // Add resource details when available
        if (exception.getResourceType() != null && exception.getResourceId() != null) {
            errors.add(String.format("Resource of type '%s' with identifier '%s' does not exist",
                    exception.getResourceType(), exception.getResourceId()));
        }
        
        return buildErrorResponse(NOT_FOUND_ERROR_CODE, message, errors);
    }
    
    /**
     * Handles BadRequestException and returns a 400 Bad Request response.
     *
     * @param exception the bad request exception
     * @return a standardized error response for validation errors
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException exception) {
        logException(exception, BAD_REQUEST_ERROR_CODE);
        
        String message = exception.getMessage();
        List<String> errors = exception.getErrors();
        
        return buildErrorResponse(BAD_REQUEST_ERROR_CODE, message, errors);
    }
    
    /**
     * Logs an exception with the specified error code.
     * Different logging levels are used based on the exception type.
     *
     * @param exception the exception to log
     * @param errorCode the error code associated with the exception
     */
    public void logException(Exception exception, String errorCode) {
        if (exception instanceof ResourceNotFoundException) {
            // Not found exceptions are normal application flow in some cases
            logger.info("Error code [{}]: {}", errorCode, exception.getMessage());
        } else if (exception instanceof BadRequestException) {
            // Bad request exceptions are client errors, so warn level is appropriate
            logger.warn("Error code [{}]: {}", errorCode, exception.getMessage());
            
            // Log validation errors if present
            if (exception instanceof BadRequestException) {
                BadRequestException badRequest = (BadRequestException) exception;
                if (!badRequest.getErrors().isEmpty()) {
                    logger.warn("Validation errors: {}", badRequest.getErrors());
                }
            }
        } else {
            // For all other exceptions, log as errors with stack trace
            logger.error("Error code [{}]: {}", errorCode, exception.getMessage(), exception);
        }
    }
    
    /**
     * Builds a standardized error response with the specified details.
     *
     * @param errorCode the error code
     * @param message the error message
     * @param errors the list of detailed errors
     * @return the complete error response
     */
    public ErrorResponse buildErrorResponse(String errorCode, String message, List<String> errors) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        
        // Add detailed errors if present
        if (errors != null && !errors.isEmpty()) {
            errorResponse.setErrors(errors);
        }
        
        return errorResponse;
    }
    
    /**
     * Convenience method to build an error response with a single error detail.
     *
     * @param errorCode the error code
     * @param message the error message
     * @param errorDetail the detailed error message
     * @return the complete error response
     */
    public ErrorResponse buildErrorResponse(String errorCode, String message, String errorDetail) {
        return buildErrorResponse(errorCode, message, 
                errorDetail != null ? Arrays.asList(errorDetail) : null);
    }
    
    // TODO: Add additional exception handlers for other specific exceptions as needed
    
    // FIXME: Consider implementing error grouping mechanism for related errors
    // FIXME: Consider adding support for internationalized error messages
}