package com.gradlemedium100.orderservice.repository;

import com.gradlemedium100.orderservice.model.Cart;
import com.gradlemedium100.common.exception.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface for data access operations on shopping carts.
 * Provides methods for CRUD operations and specialized queries related to shopping carts.
 */
public interface CartRepository {
    
    /**
     * Finds a shopping cart by its unique identifier.
     * 
     * @param id The unique identifier of the cart
     * @return An Optional containing the cart if found, or empty if not found
     * @throws DataAccessException if there is an error during data access
     */
    Optional<Cart> findById(Long id) throws DataAccessException;
    
    /**
     * Finds the active shopping cart for a specific user.
     * 
     * @param userId The unique identifier of the user
     * @return An Optional containing the user's active cart if found, or empty if not found
     * @throws DataAccessException if there is an error during data access
     */
    Optional<Cart> findByUserId(Long userId) throws DataAccessException;
    
    /**
     * Saves or updates a shopping cart in the database.
     * If the cart has an ID, it will be updated; otherwise, a new cart will be created.
     * 
     * @param cart The cart to save or update
     * @return The saved cart with any generated IDs or updated fields
     * @throws DataAccessException if there is an error during data access
     */
    Cart save(Cart cart) throws DataAccessException;
    
    /**
     * Deletes a shopping cart by its ID.
     * 
     * @param cartId The ID of the cart to delete
     * @throws DataAccessException if there is an error during data access or the cart doesn't exist
     */
    void delete(Long cartId) throws DataAccessException;
    
    /**
     * Finds abandoned shopping carts that have not been updated since a specified date.
     * Useful for cleanup operations or for retargeting customers with abandoned carts.
     * 
     * @param olderThan Date threshold for considering a cart abandoned
     * @return A list of abandoned carts that haven't been updated since the specified date
     * @throws DataAccessException if there is an error during data access
     */
    List<Cart> findAbandonedCarts(LocalDateTime olderThan) throws DataAccessException;
    
    // TODO: Add methods to batch delete abandoned carts
    
    // FIXME: Consider adding transaction support for cart operations
}