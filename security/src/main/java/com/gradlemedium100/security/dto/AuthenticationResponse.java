package com.gradlemedium100.security.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object that returns authentication results, including JWT token and user details.
 * This class encapsulates the response data sent back to clients after successful authentication.
 */
public class AuthenticationResponse {
    
    /**
     * JWT authentication token used for subsequent authorized requests
     */
    private String token;
    
    /**
     * Username of the authenticated user
     */
    private String username;
    
    /**
     * List of roles assigned to the authenticated user
     */
    private List<String> roles;
    
    /**
     * Timestamp (in milliseconds) when the token expires
     */
    private long expiresAt;
    
    /**
     * Default constructor
     */
    public AuthenticationResponse() {
        this.roles = new ArrayList<>();
    }
    
    /**
     * Constructor with parameters
     * 
     * @param token JWT authentication token
     * @param username Username of the authenticated user
     * @param roles List of roles assigned to the user
     * @param expiresAt Token expiration timestamp
     */
    public AuthenticationResponse(String token, String username, List<String> roles, long expiresAt) {
        this.token = token;
        this.username = username;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.expiresAt = expiresAt;
    }
    
    /**
     * Returns the JWT token
     * 
     * @return JWT authentication token
     */
    public String getToken() {
        return token;
    }
    
    /**
     * Sets the JWT token
     * 
     * @param token JWT authentication token
     */
    public void setToken(String token) {
        this.token = token;
    }
    
    /**
     * Returns the username
     * 
     * @return Username of the authenticated user
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username
     * 
     * @param username Username of the authenticated user
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the roles
     * 
     * @return List of roles assigned to the authenticated user
     */
    public List<String> getRoles() {
        return roles;
    }
    
    /**
     * Sets the roles
     * 
     * @param roles List of roles assigned to the authenticated user
     */
    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }
    
    /**
     * Returns the token expiration timestamp
     * 
     * @return Timestamp when the token expires
     */
    public long getExpiresAt() {
        return expiresAt;
    }
    
    /**
     * Sets the token expiration timestamp
     * 
     * @param expiresAt Timestamp when the token expires
     */
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Checks if the token has expired
     * 
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    /**
     * Returns the remaining validity time in milliseconds
     * 
     * @return Remaining time in milliseconds before token expiration
     */
    public long getTimeToLive() {
        long timeToLive = expiresAt - System.currentTimeMillis();
        return Math.max(0, timeToLive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AuthenticationResponse that = (AuthenticationResponse) o;
        
        return expiresAt == that.expiresAt &&
               Objects.equals(token, that.token) &&
               Objects.equals(username, that.username) &&
               Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, username, roles, expiresAt);
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
               "username='" + username + '\'' +
               ", roles=" + roles +
               ", expiresAt=" + expiresAt +
               // Not including token in toString for security reasons
               ", hasToken=" + (token != null && !token.isEmpty()) +
               '}';
    }
}