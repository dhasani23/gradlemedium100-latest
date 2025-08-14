package com.gradlemedium100.orderservice.service;

import com.gradlemedium100.orderservice.model.Cart;
import com.gradlemedium100.common.exception.ServiceException;

/**
 * Interface defining the shopping cart management operations.
 * This service handles all cart-related functionality including adding/removing
 * items, updating quantities, and retrieving cart information.
 */
public interface CartService {

    /**
     * Gets the active cart for a user or creates a new one if none exists.
     *
     * @param userId ID of the user who owns the cart
     * @return The user's active shopping cart
     * @throws ServiceException if there is an issue accessing the cart data or the user is invalid
     */
    Cart getOrCreateCart(Long userId) throws ServiceException;

    /**
     * Adds an item to the user's cart. If the item already exists, its quantity will be increased.
     *
     * @param userId ID of the user who owns the cart
     * @param productId ID of the product to add to the cart
     * @param quantity Number of items to add (must be positive)
     * @return The updated cart after adding the item
     * @throws ServiceException if there is an issue adding the item or if parameters are invalid
     */
    Cart addItemToCart(Long userId, Long productId, Integer quantity) throws ServiceException;

    /**
     * Updates the quantity of an item in the user's cart.
     * If the quantity is set to 0, the item will be removed from the cart.
     *
     * @param userId ID of the user who owns the cart
     * @param productId ID of the product to update
     * @param quantity New quantity for the item (must be non-negative)
     * @return The updated cart after changing the item quantity
     * @throws ServiceException if the item doesn't exist in the cart or parameters are invalid
     */
    Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) throws ServiceException;

    /**
     * Removes an item from the user's cart.
     *
     * @param userId ID of the user who owns the cart
     * @param productId ID of the product to remove
     * @return The updated cart after removing the item
     * @throws ServiceException if the item doesn't exist in the cart or parameters are invalid
     */
    Cart removeCartItem(Long userId, Long productId) throws ServiceException;

    /**
     * Removes all items from the user's cart.
     *
     * @param userId ID of the user who owns the cart
     * @throws ServiceException if there is an issue clearing the cart or the user is invalid
     */
    void clearCart(Long userId) throws ServiceException;

    /**
     * Gets the active cart for a user.
     * Unlike getOrCreateCart, this method will return null if no cart exists.
     *
     * @param userId ID of the user who owns the cart
     * @return The user's active shopping cart, or null if none exists
     * @throws ServiceException if there is an issue accessing the cart data
     */
    Cart getCartByUserId(Long userId) throws ServiceException;
    
    // TODO: Add method to merge anonymous cart with user cart after login
    
    // TODO: Add method to handle abandoned carts and implement cleanup strategy
    
    // FIXME: Consider adding timeout mechanism for cart items to prevent inventory locking
}