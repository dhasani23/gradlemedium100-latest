package com.gradlemedium100.security.dto;

/**
 * Data Transfer Object that holds login credentials from client requests.
 * This class is used to transfer authentication data between client and server.
 */
public class AuthenticationRequest {
    
    // Username for authentication
    private String username;
    
    // Password for authentication
    private String password;
    
    /**
     * Default constructor
     */
    public AuthenticationRequest() {
        // Empty constructor for serialization frameworks
    }
    
    /**
     * Parameterized constructor for creating authentication request with credentials
     * 
     * @param username the username for authentication
     * @param password the password for authentication
     */
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Returns the username used for authentication
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username for authentication
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the password used for authentication
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password for authentication
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns a string representation of the authentication request
     * Note: Password is intentionally not included for security reasons
     * 
     * @return string representation of the object
     */
    @Override
    public String toString() {
        // Intentionally not including password in toString for security
        return "AuthenticationRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
    
    // TODO: Consider adding validation logic for username and password
    
    // FIXME: Ensure proper encoding/encryption of sensitive data during transmission
}