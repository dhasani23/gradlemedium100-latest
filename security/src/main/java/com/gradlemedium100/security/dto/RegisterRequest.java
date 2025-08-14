package com.gradlemedium100.security.dto;

/**
 * Data Transfer Object that holds user registration details from client requests.
 * This class captures all the necessary information for registering a new user
 * in the system.
 *
 * @author gradlemedium100
 * @version 1.0
 */
public class RegisterRequest {
    
    // User identification fields
    private String username;
    private String password;
    private String email;
    
    // User personal information fields
    private String firstName;
    private String lastName;
    
    /**
     * Default constructor for RegisterRequest.
     * Required for deserialization from JSON.
     */
    public RegisterRequest() {
        // Default constructor required for JSON deserialization
    }
    
    /**
     * Fully parameterized constructor for creating a complete registration request.
     *
     * @param username  the username for the new user
     * @param password  the password for the new user
     * @param email     the email address for the new user
     * @param firstName the first name of the new user
     * @param lastName  the last name of the new user
     */
    public RegisterRequest(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Returns the username.
     *
     * @return the username of the user to be registered
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the password.
     *
     * @return the password of the user to be registered
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns the email.
     *
     * @return the email of the user to be registered
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Returns the first name.
     *
     * @return the first name of the user to be registered
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Returns the last name.
     *
     * @return the last name of the user to be registered
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Returns a string representation of the registration request.
     * Note that the password is intentionally excluded for security reasons.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                // Password is intentionally excluded for security reasons
                '}';
    }
    
    /**
     * Validates if this registration request contains all required fields.
     * 
     * @return true if all required fields are present, false otherwise
     */
    public boolean isValid() {
        // TODO: Implement proper validation logic with regex patterns
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               email != null && !email.trim().isEmpty();
    }
    
    /**
     * Builder pattern implementation for RegisterRequest.
     * Allows for a more fluent API to create registration requests.
     *
     * @return a new RegisterRequestBuilder
     */
    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }
    
    /**
     * Builder class for RegisterRequest.
     */
    public static class RegisterRequestBuilder {
        private String username;
        private String password;
        private String email;
        private String firstName;
        private String lastName;
        
        /**
         * Sets the username for the RegisterRequest being built.
         *
         * @param username the username
         * @return this builder instance
         */
        public RegisterRequestBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        /**
         * Sets the password for the RegisterRequest being built.
         *
         * @param password the password
         * @return this builder instance
         */
        public RegisterRequestBuilder password(String password) {
            this.password = password;
            return this;
        }
        
        /**
         * Sets the email for the RegisterRequest being built.
         *
         * @param email the email address
         * @return this builder instance
         */
        public RegisterRequestBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        /**
         * Sets the firstName for the RegisterRequest being built.
         *
         * @param firstName the first name
         * @return this builder instance
         */
        public RegisterRequestBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        /**
         * Sets the lastName for the RegisterRequest being built.
         *
         * @param lastName the last name
         * @return this builder instance
         */
        public RegisterRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        /**
         * Builds a new RegisterRequest instance with the provided values.
         *
         * @return a new RegisterRequest instance
         */
        public RegisterRequest build() {
            return new RegisterRequest(username, password, email, firstName, lastName);
        }
    }
}