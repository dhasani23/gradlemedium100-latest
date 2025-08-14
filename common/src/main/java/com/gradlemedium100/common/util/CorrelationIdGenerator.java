package com.gradlemedium100.common.util;

import java.util.UUID;

/**
 * Utility class for generating correlation IDs used to track requests across microservices.
 * These IDs help in tracing request flows through distributed systems for monitoring,
 * debugging and logging purposes.
 */
public class CorrelationIdGenerator {
    
    private static final String PREFIX = "corr-";
    
    /**
     * Generates a new random correlation ID.
     * 
     * @return A unique correlation ID string
     */
    public String generateCorrelationId() {
        return PREFIX + UUID.randomUUID().toString();
    }
    
    /**
     * Validates if the provided correlation ID is valid.
     * A valid correlation ID should be non-null, non-empty, and properly formatted.
     *
     * @param correlationId The correlation ID to validate
     * @return true if the correlation ID is valid, false otherwise
     */
    public boolean isValidCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.trim().isEmpty()) {
            return false;
        }
        
        return correlationId.startsWith(PREFIX) && correlationId.length() > PREFIX.length();
    }
    
    /**
     * Extracts the UUID portion from a correlation ID.
     *
     * @param correlationId The correlation ID from which to extract the UUID
     * @return The UUID portion of the correlation ID or null if invalid
     */
    public String extractUUID(String correlationId) {
        if (!isValidCorrelationId(correlationId)) {
            return null;
        }
        
        return correlationId.substring(PREFIX.length());
    }
}