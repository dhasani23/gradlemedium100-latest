package com.gradlemedium100.security.service;

/**
 * Service interface for JWT token operations.
 * This interface defines methods for token generation, validation, and parsing.
 */
public interface TokenService {

    /**
     * Generates a JWT token for the given user ID and roles.
     *
     * @param userId The user ID
     * @param roles The user roles
     * @return Generated JWT token
     */
    String generateToken(String userId, String[] roles);
    
    /**
     * Validates the given JWT token.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Extracts the user ID from the given JWT token.
     *
     * @param token The JWT token
     * @return User ID extracted from the token
     */
    String getUserIdFromToken(String token);
    
    /**
     * Extracts the roles from the given JWT token.
     *
     * @param token The JWT token
     * @return User roles extracted from the token
     */
    String[] getRolesFromToken(String token);
}