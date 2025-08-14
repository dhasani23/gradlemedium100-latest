package com.gradlemedium100.security.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for security-related exceptions.
 * This class centralizes the handling of security exceptions like invalid tokens,
 * authentication failures, and authorization issues across the application.
 */
@ControllerAdvice
public class SecurityExceptionHandler {

    /**
     * Handles authentication exceptions thrown during the authentication process.
     * These include invalid credentials, expired sessions, etc.
     *
     * @param ex      The authentication exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return ResponseEntity containing standardized error details
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Authentication failed";
        }
        
        // Log authentication failure
        // TODO: Implement proper logging with user context information
        
        Map<String, Object> errorResponse = buildErrorResponse(errorMessage, HttpStatus.UNAUTHORIZED);
        errorResponse.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles access denied exceptions when a user attempts to access a resource
     * they don't have permission for.
     *
     * @param ex      The access denied exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return ResponseEntity containing standardized error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = "Access denied";
        }
        
        // Log access denied event
        // FIXME: Add more context information about the resource being accessed
        
        Map<String, Object> errorResponse = buildErrorResponse(errorMessage, HttpStatus.FORBIDDEN);
        errorResponse.put("path", request.getDescription(false));
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles JWT token exceptions such as expired, malformed, or invalid tokens.
     *
     * @param ex      The JWT exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return ResponseEntity containing standardized error details
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Object> handleJwtException(JwtException ex, WebRequest request) {
        String errorMessage;
        HttpStatus status;
        
        // Determine the specific type of JWT exception for more detailed error messages
        if (ex.getMessage().contains("expired")) {
            errorMessage = "JWT token has expired";
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex.getMessage().contains("malformed")) {
            errorMessage = "Malformed JWT token";
            status = HttpStatus.BAD_REQUEST;
        } else {
            errorMessage = "Invalid JWT token: " + ex.getMessage();
            status = HttpStatus.UNAUTHORIZED;
        }
        
        Map<String, Object> errorResponse = buildErrorResponse(errorMessage, status);
        errorResponse.put("path", request.getDescription(false));
        
        // TODO: Consider implementing token refresh mechanism for expired tokens
        
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles generic token-related exceptions that are not specifically JWT exceptions.
     *
     * @param ex      The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return ResponseEntity containing standardized error details
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleInvalidTokenException(Exception ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Invalid token format or token processing error";
        }
        
        Map<String, Object> errorResponse = buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
        errorResponse.put("path", request.getDescription(false));
        errorResponse.put("exception", ex.getClass().getSimpleName());
        
        // FIXME: Need to validate if all token-related exceptions are properly caught here
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Builds a standardized error response that can be returned to clients.
     *
     * @param message The error message
     * @param status  The HTTP status code
     * @return Map containing structured error information
     */
    protected Map<String, Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        
        // Add request tracking information for debugging
        // TODO: Add correlation ID to error responses for request tracking
        
        return errorResponse;
    }
}