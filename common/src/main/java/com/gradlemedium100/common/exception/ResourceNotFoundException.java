package com.gradlemedium100.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This exception carries information about the resource type and resource ID
 * that was not found, making it easier to provide meaningful error messages
 * to clients.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * ID of the resource that was not found.
     */
    private final String resourceId;
    
    /**
     * Type of the resource that was not found.
     */
    private final String resourceType;
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceId = null;
        this.resourceType = null;
    }
    
    /**
     * Constructs a new ResourceNotFoundException with the specified resource type and ID.
     * The detail message is automatically generated based on the resource type and ID.
     *
     * @param resourceType the type of the resource
     * @param resourceId the ID of the resource
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s with ID '%s' not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceId = null;
        this.resourceType = null;
    }
    
    /**
     * Returns the ID of the resource that was not found.
     *
     * @return the resource ID, or null if not specified
     */
    public String getResourceId() {
        return resourceId;
    }
    
    /**
     * Returns the type of the resource that was not found.
     *
     * @return the resource type, or null if not specified
     */
    public String getResourceType() {
        return resourceType;
    }
    
    // TODO: Add additional constructors to support more use cases if needed
    
    // FIXME: Consider adding methods to include additional context for more detailed error reporting
}