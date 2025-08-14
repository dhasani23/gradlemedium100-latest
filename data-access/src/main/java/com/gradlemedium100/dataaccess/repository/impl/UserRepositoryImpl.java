package com.gradlemedium100.dataaccess.repository.impl;

import com.gradlemedium100.dataaccess.entity.UserEntity;
import com.gradlemedium100.dataaccess.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of the UserRepository interface that provides
 * data access operations for user entities.
 * 
 * @author gradlemedium100
 * @version 1.0
 */
@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<UserEntity> implements UserRepository {
    
    private static final Logger LOGGER = Logger.getLogger(UserRepositoryImpl.class.getName());
    
    /**
     * Constructor with EntityManager injection
     *
     * @param entityManager the JPA entity manager
     */
    @Autowired
    public UserRepositoryImpl(EntityManager entityManager) {
        super(entityManager, UserEntity.class);
    }
    
    /**
     * Finds a user entity by the provided username.
     *
     * @param username the username to search for
     * @return the user entity with the specified username, or null if not found
     */
    @Override
    public UserEntity findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Attempt to find user with null or empty username");
            return null;
        }
        
        try {
            TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.username = :username", 
                UserEntity.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.log(Level.INFO, "No user found with username: {0}", username);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
            throw e;
        }
    }
    
    /**
     * Finds a user entity by the provided email address.
     *
     * @param email the email to search for
     * @return the user entity with the specified email, or null if not found
     */
    @Override
    public UserEntity findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Attempt to find user with null or empty email");
            return null;
        }
        
        try {
            TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.email = :email", 
                UserEntity.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.log(Level.INFO, "No user found with email: {0}", email);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding user by email: " + email, e);
            throw e;
        }
    }
    
    /**
     * Retrieves all active users from the database.
     *
     * @return a list of active user entities
     */
    @Override
    public List<UserEntity> findActiveUsers() {
        try {
            TypedQuery<UserEntity> query = entityManager.createQuery(
                "SELECT u FROM UserEntity u WHERE u.active = true ORDER BY u.lastName, u.firstName", 
                UserEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding active users", e);
            throw e;
        }
    }
    
    /**
     * Updates the last login timestamp for a user.
     *
     * @param userId the ID of the user to update
     * @param lastLogin the new last login timestamp
     */
    @Override
    @Transactional
    public void updateLastLogin(Long userId, LocalDateTime lastLogin) {
        if (userId == null) {
            LOGGER.log(Level.WARNING, "Attempt to update last login with null userId");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (lastLogin == null) {
            LOGGER.log(Level.WARNING, "Attempt to update last login with null timestamp");
            throw new IllegalArgumentException("Last login timestamp cannot be null");
        }
        
        try {
            int updatedRows = entityManager.createQuery(
                "UPDATE UserEntity u SET u.lastLoginDate = :lastLogin, u.modifiedDate = CURRENT_TIMESTAMP " +
                "WHERE u.id = :userId")
                .setParameter("lastLogin", lastLogin)
                .setParameter("userId", userId)
                .executeUpdate();
            
            if (updatedRows == 0) {
                LOGGER.log(Level.WARNING, "No user found with ID: {0}", userId);
            } else {
                LOGGER.log(Level.FINE, "Updated last login for user with ID: {0}", userId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating last login for user with ID: " + userId, e);
            throw e;
        }
    }
    
    /**
     * Updates the password hash for a user.
     *
     * @param userId the ID of the user to update
     * @param passwordHash the new password hash
     */
    @Override
    @Transactional
    public void updatePassword(Long userId, String passwordHash) {
        if (userId == null) {
            LOGGER.log(Level.WARNING, "Attempt to update password with null userId");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Attempt to update with null or empty password hash");
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        
        try {
            // FIXME: Consider adding password history to prevent reuse of old passwords
            int updatedRows = entityManager.createQuery(
                "UPDATE UserEntity u SET u.passwordHash = :passwordHash, " +
                "u.passwordResetRequired = false, u.modifiedDate = CURRENT_TIMESTAMP " +
                "WHERE u.id = :userId")
                .setParameter("passwordHash", passwordHash)
                .setParameter("userId", userId)
                .executeUpdate();
            
            if (updatedRows == 0) {
                LOGGER.log(Level.WARNING, "No user found with ID: {0}", userId);
            } else {
                LOGGER.log(Level.INFO, "Updated password for user with ID: {0}", userId);
            }
            
            // TODO: Implement password change notification
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating password for user with ID: " + userId, e);
            throw e;
        }
    }
}