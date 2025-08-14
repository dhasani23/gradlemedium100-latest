package com.gradlemedium100.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing application-wide constants and configuration values.
 * These constants are used throughout the application to maintain consistency
 * and facilitate configuration changes.
 */
public final class Constants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Current API version.
     */
    public static final String API_VERSION = "1.0.0";
    
    /**
     * Default page size for paginated results.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * Maximum allowed page size for paginated results.
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * Default sort direction (ASC or DESC).
     */
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
    
    /**
     * Standard date format used throughout the application.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Standard datetime format used throughout the application.
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    
    /**
     * Header name for API key authentication.
     */
    public static final String API_KEY_HEADER = "X-API-Key";
    
    /**
     * Header name for authentication token.
     */
    public static final String AUTH_TOKEN_HEADER = "Authorization";
    
    /**
     * Map of error codes to error messages.
     * This is implemented as a thread-safe map to ensure thread safety during error code registration.
     */
    private static final Map<String, String> ERROR_CODES_INTERNAL = new HashMap<>();
    
    /**
     * Publicly accessible unmodifiable view of error codes.
     */
    public static final Map<String, String> ERROR_CODES = Collections.unmodifiableMap(ERROR_CODES_INTERNAL);
    
    // Initialize default error codes
    static {
        ERROR_CODES_INTERNAL.put("400", "Bad Request");
        ERROR_CODES_INTERNAL.put("401", "Unauthorized");
        ERROR_CODES_INTERNAL.put("403", "Forbidden");
        ERROR_CODES_INTERNAL.put("404", "Not Found");
        ERROR_CODES_INTERNAL.put("500", "Internal Server Error");
    }
    
    /**
     * Gets the error message for the specified error code.
     * 
     * @param errorCode the error code
     * @return the error message corresponding to the error code, or null if not found
     */
    public static String getErrorMessage(String errorCode) {
        if (errorCode == null || errorCode.trim().isEmpty()) {
            return null;
        }
        return ERROR_CODES.get(errorCode);
    }
    
    /**
     * Registers a new error code with its corresponding message.
     * If the error code already exists, the message will be updated.
     * 
     * @param errorCode the error code to register
     * @param errorMessage the error message associated with the error code
     * @throws IllegalArgumentException if errorCode or errorMessage is null or empty
     */
    public static synchronized void registerErrorCode(String errorCode, String errorMessage) {
        if (errorCode == null || errorCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Error code cannot be null or empty");
        }
        
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }
        
        // Since we're using a synchronized method, this modification is thread-safe
        ERROR_CODES_INTERNAL.put(errorCode, errorMessage);
        
        // TODO: Add logging for new error code registration
        // FIXME: Consider adding validation for error code format
    }
}