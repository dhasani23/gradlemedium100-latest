package com.gradlemedium100.security.service.impl;

import com.gradlemedium100.dataaccess.entity.UserEntity;
import com.gradlemedium100.dataaccess.repository.UserRepository;
import com.gradlemedium100.security.model.Role;
import com.gradlemedium100.security.model.SecurityUser;
import com.gradlemedium100.security.service.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of UserDetailsService that loads user-specific data for authentication.
 * This service provides methods to load users by username, create new users, and check if users exist.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads a user by username for authentication purposes.
     * This method is required by Spring Security's UserDetailsService interface.
     * 
     * @param username the username to search for
     * @return the UserDetails object containing the user's authentication information
     * @throws UsernameNotFoundException if the user cannot be found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user details for username: {}", username);
        
        try {
            UserEntity userEntity = userRepository.findByUsername(username);
            
            if (userEntity == null) {
                logger.error("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            
            // Update last login time
            userRepository.updateLastLogin(userEntity.getId(), LocalDateTime.now());
            
            // Convert UserEntity to SecurityUser
            return createSecurityUserFromEntity(userEntity);
        } catch (Exception ex) {
            logger.error("Error loading user by username: {}", username, ex);
            throw new UsernameNotFoundException("Error loading user", ex);
        }
    }

    /**
     * Creates a new user in the system with encrypted password.
     * 
     * @param user the SecurityUser object containing the user information
     * @return the created UserDetails object
     */
    @Override
    @Transactional
    public UserDetails createUser(SecurityUser user) {
        logger.debug("Creating new user with username: {}", user.getUsername());
        
        // Check if user already exists
        if (userExists(user.getUsername())) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        
        try {
            // Create new user entity
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(user.getUsername());
            userEntity.setEmail(user.getEmail());
            userEntity.setPasswordHash(passwordEncoder.encode(user.getPassword()));
            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setActive(true);
            userEntity.setLastLoginDate(LocalDateTime.now());
            
            // Save the user
            userEntity = userRepository.save(userEntity);
            logger.info("User created successfully: {}", user.getUsername());
            
            return createSecurityUserFromEntity(userEntity);
        } catch (Exception ex) {
            logger.error("Error creating user: {}", user.getUsername(), ex);
            throw new RuntimeException("Error creating user", ex);
        }
    }

    /**
     * Checks if a user with the given username exists.
     * 
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean userExists(String username) {
        logger.debug("Checking if user exists: {}", username);
        
        try {
            UserEntity userEntity = userRepository.findByUsername(username);
            return userEntity != null;
        } catch (Exception ex) {
            logger.error("Error checking if user exists: {}", username, ex);
            return false;
        }
    }
    
    /**
     * Converts a UserEntity to a SecurityUser object with appropriate roles.
     * 
     * @param userEntity the UserEntity to convert
     * @return the SecurityUser object
     */
    private SecurityUser createSecurityUserFromEntity(UserEntity userEntity) {
        SecurityUser securityUser = new SecurityUser();
        securityUser.setId(userEntity.getId());
        securityUser.setUsername(userEntity.getUsername());
        securityUser.setPassword(userEntity.getPasswordHash());
        securityUser.setEmail(userEntity.getEmail());
        securityUser.setFirstName(userEntity.getFirstName());
        securityUser.setLastName(userEntity.getLastName());
        securityUser.setEnabled(userEntity.isActive());
        securityUser.setAccountNonExpired(true);
        securityUser.setAccountNonLocked(true);
        securityUser.setCredentialsNonExpired(true);
        
        // TODO: In a real application, roles should be stored in the database
        // and associated with users. For now, we assign a default user role.
        securityUser.setRoles(Collections.singletonList(Role.USER));
        
        return securityUser;
    }
}