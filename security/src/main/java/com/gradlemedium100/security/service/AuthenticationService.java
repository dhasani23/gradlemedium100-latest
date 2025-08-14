package com.gradlemedium100.security.service;

import com.gradlemedium100.security.dto.AuthenticationRequest;
import com.gradlemedium100.security.dto.AuthenticationResponse;
import com.gradlemedium100.security.dto.RegisterRequest;

/**
 * Service interface for user authentication, registration, and token management operations.
 * 
 * This interface defines the contract for handling user authentication flows including:
 * - User login via credentials
 * - User registration
 * - JWT token refresh mechanism
 * - Token validation
 */
public interface AuthenticationService {
    
    /**
     * Authenticates a user with the provided credentials and returns a token.
     * 
     * @param request The authentication request containing username and password
     * @return AuthenticationResponse containing JWT token and user details
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);
    
    /**
     * Registers a new user with the provided details and returns a token.
     * 
     * @param request The registration request containing user details
     * @return AuthenticationResponse containing JWT token and user details
     * @throws IllegalArgumentException if username already exists
     */
    AuthenticationResponse register(RegisterRequest request);
    
    /**
     * Refreshes an expired JWT token and issues a new one.
     * 
     * @param refreshToken The refresh token string
     * @return AuthenticationResponse containing new JWT token and user details
     * @throws io.jsonwebtoken.JwtException if token is invalid or expired
     */
    AuthenticationResponse refreshToken(String refreshToken);
    
    /**
     * Validates the provided token.
     * 
     * @param token The JWT token string to validate
     * @return true if token is valid, false otherwise
     * @throws io.jsonwebtoken.JwtException if token processing fails
     * 
     * TODO: Consider adding more specific validation responses to help client troubleshoot issues
     */
    boolean validateToken(String token);
}