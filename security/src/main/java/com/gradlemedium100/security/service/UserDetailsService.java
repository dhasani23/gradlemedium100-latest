package com.gradlemedium100.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.gradlemedium100.security.model.SecurityUser;

/**
 * Interface that extends Spring Security's UserDetailsService for loading user-specific data for authentication.
 * Provides additional methods for user management operations beyond the standard Spring Security functionality.
 * 
 * This interface serves as a bridge between the application's user repository and Spring Security's
 * authentication mechanism.
 */
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {
    
    /**
     * Loads a user by username for authentication purposes.
     * Implements the method required by Spring Security's UserDetailsService interface.
     *
     * @param username the username to load; must not be {@literal null}
     * @return a fully populated user record (never {@literal null})
     * @throws UsernameNotFoundException if the user could not be found or the user has no authorities
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    /**
     * Creates a new user in the system.
     * 
     * @param user the SecurityUser object containing user details to create
     * @return the newly created UserDetails object
     * @throws IllegalArgumentException if a user with the same username already exists
     */
    UserDetails createUser(SecurityUser user);
    
    /**
     * Checks if a user with the given username exists in the system.
     * 
     * @param username the username to check; must not be {@literal null} or empty
     * @return true if the user exists, false otherwise
     * @throws IllegalArgumentException if username is null or empty
     */
    boolean userExists(String username);
}