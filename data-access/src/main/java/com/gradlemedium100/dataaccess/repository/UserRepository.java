package com.gradlemedium100.dataaccess.repository;

import java.util.List;
import java.time.LocalDateTime;
import com.gradlemedium100.dataaccess.entity.UserEntity;

/**
 * Repository interface for user-related database operations.
 * Provides methods for finding and updating user information in the database.
 * Extends the base repository interface to inherit common CRUD operations.
 */
public interface UserRepository extends BaseRepository<UserEntity> {
    
    /**
     * Find a user by their unique username.
     *
     * @param username The username to search for
     * @return The found user entity or null if not found
     */
    UserEntity findByUsername(String username);
    
    /**
     * Find a user by their email address.
     *
     * @param email The email address to search for
     * @return The found user entity or null if not found
     */
    UserEntity findByEmail(String email);
    
    /**
     * Find all users with active status.
     *
     * @return List of active user entities
     */
    List<UserEntity> findActiveUsers();
    
    /**
     * Update a user's last login timestamp.
     * 
     * @param userId The ID of the user to update
     * @param lastLogin The new last login timestamp
     */
    void updateLastLogin(Long userId, LocalDateTime lastLogin);
    
    /**
     * Update a user's password hash.
     * 
     * @param userId The ID of the user to update
     * @param passwordHash The new password hash
     */
    void updatePassword(Long userId, String passwordHash);
    
    // TODO: Add method for finding users by role when role management is implemented
    
    /**
     * Find users who haven't logged in since the specified date.
     * 
     * @param date The cutoff date for last login
     * @return List of inactive users
     * 
     * FIXME: This method needs to be implemented in the repository implementation class
     */
    // List<UserEntity> findInactiveUsersSince(LocalDateTime date);
}