package com.gradlemedium100.security.service.impl;

import com.gradlemedium100.security.dto.AuthenticationRequest;
import com.gradlemedium100.security.dto.AuthenticationResponse;
import com.gradlemedium100.security.dto.RegisterRequest;
import com.gradlemedium100.security.model.Role;
import com.gradlemedium100.security.model.SecurityUser;
import com.gradlemedium100.security.service.AuthenticationService;
import com.gradlemedium100.security.service.JwtService;
import com.gradlemedium100.security.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation that handles user authentication, registration,
 * and related security operations.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationServiceImpl(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user with the provided credentials and returns a token
     *
     * @param request Authentication request containing username and password
     * @return Authentication response with JWT token and user details
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Authenticate user with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Get user details
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

            // Generate JWT token with additional claims
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("authorities", buildUserAuthorities(securityUser));

            String token = jwtService.generateToken(extraClaims, securityUser);
            Date expiration = jwtService.extractExpiration(token);

            // Build and return response
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(token);
            response.setUsername(securityUser.getUsername());
            response.setRoles(buildUserAuthorities(securityUser));
            response.setExpiresAt(expiration.getTime());

            return response;
        } catch (AuthenticationException e) {
            // Log authentication failure
            // TODO: Implement proper logging
            throw e;
        }
    }

    /**
     * Registers a new user with the provided details and returns a token
     *
     * @param request Registration request with user details
     * @return Authentication response with JWT token and user details
     */
    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userDetailsService.userExists(request.getUsername())) {
            // FIXME: Should use a custom exception for better error handling
            throw new IllegalArgumentException("Username already exists");
        }

        // Create new user from request
        SecurityUser newUser = createSecurityUserFromRequest(request);
        
        // Save user
        SecurityUser savedUser = (SecurityUser) userDetailsService.createUser(newUser);
        
        // Generate JWT token with additional claims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authorities", buildUserAuthorities(savedUser));
        
        String token = jwtService.generateToken(extraClaims, savedUser);
        Date expiration = jwtService.extractExpiration(token);
        
        // Build and return response
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);
        response.setUsername(savedUser.getUsername());
        response.setRoles(buildUserAuthorities(savedUser));
        response.setExpiresAt(expiration.getTime());
        
        return response;
    }

    /**
     * Refreshes an expired JWT token and issues a new one
     *
     * @param refreshToken The refresh token
     * @return Authentication response with new JWT token
     */
    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        // Extract username from token
        String username = jwtService.extractUsername(refreshToken);
        
        // Validate token
        if (username == null || !validateToken(refreshToken)) {
            // FIXME: Should use a custom exception for better error handling
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        SecurityUser securityUser = (SecurityUser) userDetails;
        
        // Generate new JWT token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("authorities", buildUserAuthorities(securityUser));
        
        String newToken = jwtService.generateToken(extraClaims, securityUser);
        Date expiration = jwtService.extractExpiration(newToken);
        
        // Build and return response
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(newToken);
        response.setUsername(securityUser.getUsername());
        response.setRoles(buildUserAuthorities(securityUser));
        response.setExpiresAt(expiration.getTime());
        
        return response;
    }

    /**
     * Validates the provided token
     *
     * @param token JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    @Override
    public boolean validateToken(String token) {
        try {
            // Extract username from token
            String username = jwtService.extractUsername(token);
            
            if (username == null) {
                return false;
            }
            
            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // Validate token against user details
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            // Log token validation failure
            // TODO: Implement proper logging
            return false;
        }
    }

    /**
     * Builds a list of authority strings from user roles
     *
     * @param user The security user
     * @return List of authority strings
     */
    public List<String> buildUserAuthorities(SecurityUser user) {
        if (user == null || user.getRoles() == null) {
            return new ArrayList<>();
        }
        
        return user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * Creates a SecurityUser object from a registration request
     *
     * @param request Registration request
     * @return SecurityUser object
     */
    public SecurityUser createSecurityUserFromRequest(RegisterRequest request) {
        SecurityUser user = new SecurityUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Default settings for new users
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        // Assign default user role
        // TODO: Allow role selection during registration for admin users
        user.setRoles(Arrays.asList(Role.valueOf("USER")));
        
        return user;
    }
}