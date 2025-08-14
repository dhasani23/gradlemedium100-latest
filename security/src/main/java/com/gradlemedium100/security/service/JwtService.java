package com.gradlemedium100.security.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;

/**
 * Service interface for JWT token operations including generation, validation,
 * and claims extraction.
 * <p>
 * This interface provides methods to handle JWT tokens in the authentication process,
 * including creating tokens, validating them, and extracting information from them.
 */
public interface JwtService {

    /**
     * Extracts username from a JWT token
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    String extractUsername(String token);

    /**
     * Generates a new JWT token for the given user
     *
     * @param userDetails the user details
     * @return a JWT token string
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generates a new JWT token with extra claims
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details
     * @return a JWT token string
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Validates if the token belongs to the given user and is not expired
     *
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if the token is valid, false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Extracts token expiration date
     *
     * @param token the JWT token
     * @return the expiration date
     */
    Date extractExpiration(String token);

    /**
     * Extracts all claims from the token
     *
     * @param token the JWT token
     * @return the claims object containing all claims
     */
    Claims extractAllClaims(String token);
}