package com.gradlemedium100.security.controller;

import com.gradlemedium100.security.dto.AuthenticationRequest;
import com.gradlemedium100.security.dto.AuthenticationResponse;
import com.gradlemedium100.security.dto.RegisterRequest;
import com.gradlemedium100.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST controller that handles authentication-related endpoints such as
 * login, registration, token refresh, and token validation.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
    
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Handles user login requests, authenticating users with username and password
     * and returning a JWT token if authentication is successful.
     *
     * @param request The authentication request containing username and password
     * @return ResponseEntity with AuthenticationResponse containing JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        LOGGER.log(Level.INFO, "Login attempt for user: {0}", request.getUsername());
        
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Login failed for user: " + request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Handles user registration requests, creating a new user account with the provided details
     * and returning a JWT token for immediate authentication.
     *
     * @param request The registration request containing user details
     * @return ResponseEntity with AuthenticationResponse containing JWT token and user details
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        LOGGER.log(Level.INFO, "Registration attempt for user: {0}", request.getUsername());
        
        try {
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Registration failed for user: " + request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Refreshes an expired JWT token using the refresh token provided in the Authorization header.
     * Returns a new access token if the refresh token is valid.
     *
     * @param request The HTTP request containing the Authorization header with the refresh token
     * @return ResponseEntity with AuthenticationResponse containing a new JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        LOGGER.log(Level.INFO, "Token refresh request received");
        
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String refreshToken = authHeader.substring(7);
                AuthenticationResponse response = authenticationService.refreshToken(refreshToken);
                return ResponseEntity.ok(response);
            }
            
            LOGGER.log(Level.WARNING, "Token refresh failed: No Bearer token found in Authorization header");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Validates a JWT token to check if it's still valid and not expired.
     *
     * @param token The JWT token to validate
     * @return ResponseEntity with Boolean indicating whether the token is valid
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        LOGGER.log(Level.INFO, "Token validation request received");
        
        try {
            boolean isValid = authenticationService.validateToken(token);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Token validation error", e);
            return ResponseEntity.ok(false);
        }
    }
    
    /**
     * TODO: Implement logout functionality that invalidates the current token
     * This could be done by adding the token to a blacklist or using Redis for token storage
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        LOGGER.log(Level.INFO, "Logout request received");
        
        // FIXME: Implement proper token invalidation mechanism
        // Currently, JWT tokens cannot be invalidated unless using a token store
        // Consider implementing a token blacklist or using Redis for token storage
        return ResponseEntity.ok().build();
    }
}