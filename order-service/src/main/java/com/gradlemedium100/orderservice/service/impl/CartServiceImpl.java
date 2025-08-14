package com.gradlemedium100.orderservice.service.impl;

import com.gradlemedium100.common.exception.ServiceException;
import com.gradlemedium100.orderservice.model.Cart;
import com.gradlemedium100.orderservice.model.CartItem;
import com.gradlemedium100.orderservice.repository.CartRepository;
import com.gradlemedium100.orderservice.service.CartService;
import com.gradlemedium100.productservice.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the CartService interface for managing shopping carts.
 * This service handles all cart-related operations including creating carts,
 * adding/updating/removing items, and cleaning up abandoned carts.
 */
@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    
    private final CartRepository cartRepository;
    private final ProductService productService;
    
    @Value("${cart.timeout.hours:24}")
    private Integer cartTimeoutHours;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    /**
     * Gets the active cart for a user or creates a new one if none exists
     *
     * @param userId The ID of the user
     * @return The user's active cart
     * @throws ServiceException if there's an error retrieving or creating the cart
     */
    @Override
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        if (userId == null) {
            throw new ServiceException("User ID cannot be null");
        }

        try {
            // Try to find existing cart for the user
            Optional<Cart> existingCart = cartRepository.findByUserId(userId);
            
            if (existingCart.isPresent()) {
                logger.debug("Found existing cart for user {}: {}", userId, existingCart.get().getId());
                return existingCart.get();
            }
            
            // Create a new cart if none exists
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setCreatedDate(LocalDateTime.now());
            newCart.setLastUpdated(LocalDateTime.now());
            newCart.setTotalAmount(BigDecimal.ZERO);
            
            Cart savedCart = cartRepository.save(newCart);
            logger.info("Created new cart for user {}: {}", userId, savedCart.getId());
            return savedCart;
        } catch (Exception e) {
            logger.error("Error getting or creating cart for user {}: {}", userId, e.getMessage());
            throw new ServiceException("Error getting or creating cart: " + e.getMessage(), e);
        }
    }

    /**
     * Adds an item to the user's cart
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to add
     * @param quantity The quantity to add
     * @return The updated cart
     * @throws ServiceException if there's an error adding the item
     */
    @Override
    @Transactional
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        if (userId == null || productId == null || quantity == null) {
            throw new ServiceException("User ID, product ID, and quantity are required");
        }
        
        if (quantity <= 0) {
            throw new ServiceException("Quantity must be greater than zero");
        }
        
        try {
            // Validate product exists and get details
            Map<String, Object> productDetails = getProductDetails(productId);
            if (productDetails == null || productDetails.isEmpty()) {
                throw new ServiceException("Product not found: " + productId);
            }
            
            // Check product availability
            Integer availableStock = (Integer) productDetails.get("stock");
            if (availableStock < quantity) {
                throw new ServiceException("Insufficient stock for product " + productId + 
                                          ". Requested: " + quantity + ", Available: " + availableStock);
            }
            
            // Get or create cart
            Cart cart = getOrCreateCart(userId);
            
            // Check if item already exists in cart
            boolean itemExists = false;
            for (CartItem item : cart.getItems()) {
                if (item.getProductId().equals(productId)) {
                    // Update existing item quantity
                    int newQuantity = item.getQuantity() + quantity;
                    if (newQuantity > availableStock) {
                        throw new ServiceException("Cannot add " + quantity + " more items. " +
                                                 "Max available: " + (availableStock - item.getQuantity()));
                    }
                    
                    item.updateQuantity(newQuantity);
                    itemExists = true;
                    break;
                }
            }
            
            // Add new item if it doesn't exist in cart
            if (!itemExists) {
                CartItem newItem = new CartItem();
                newItem.setCartId(cart.getId());
                newItem.setProductId(productId);
                newItem.setProductName((String) productDetails.get("name"));
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(new BigDecimal(productDetails.get("price").toString()));
                newItem.setDateAdded(LocalDateTime.now());
                newItem.calculateTotalPrice();
                
                cart.addItem(newItem);
            }
            
            // Update cart totals and timestamp
            cart.calculateTotal();
            cart.setLastUpdated(LocalDateTime.now());
            
            // Save updated cart
            Cart updatedCart = cartRepository.save(cart);
            logger.info("Added/updated product {} (quantity: {}) to cart for user {}", 
                      productId, quantity, userId);
            
            return updatedCart;
        } catch (ServiceException se) {
            // Re-throw service exceptions
            throw se;
        } catch (Exception e) {
            logger.error("Error adding item to cart: {}", e.getMessage());
            throw new ServiceException("Error adding item to cart: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the quantity of an item in the user's cart
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to update
     * @param quantity The new quantity
     * @return The updated cart
     * @throws ServiceException if there's an error updating the item
     */
    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (userId == null || productId == null || quantity == null) {
            throw new ServiceException("User ID, product ID, and quantity are required");
        }
        
        try {
            // Get cart
            Cart cart = getCartByUserId(userId);
            if (cart == null) {
                throw new ServiceException("No active cart found for user: " + userId);
            }
            
            if (quantity <= 0) {
                // If quantity is zero or negative, remove the item
                return removeCartItem(userId, productId);
            }
            
            // Validate product availability
            Map<String, Object> productDetails = getProductDetails(productId);
            if (productDetails == null || productDetails.isEmpty()) {
                throw new ServiceException("Product not found: " + productId);
            }
            
            Integer availableStock = (Integer) productDetails.get("stock");
            if (availableStock < quantity) {
                throw new ServiceException("Requested quantity exceeds available stock. " +
                                         "Requested: " + quantity + ", Available: " + availableStock);
            }
            
            // Update item quantity
            boolean updated = cart.updateItemQuantity(productId, quantity);
            if (!updated) {
                throw new ServiceException("Product not found in cart: " + productId);
            }
            
            // Update cart totals and timestamp
            cart.calculateTotal();
            cart.setLastUpdated(LocalDateTime.now());
            
            // Save updated cart
            Cart updatedCart = cartRepository.save(cart);
            logger.info("Updated quantity for product {} to {} in cart for user {}", 
                      productId, quantity, userId);
            
            return updatedCart;
        } catch (ServiceException se) {
            // Re-throw service exceptions
            throw se;
        } catch (Exception e) {
            logger.error("Error updating cart item quantity: {}", e.getMessage());
            throw new ServiceException("Error updating cart item quantity: " + e.getMessage(), e);
        }
    }

    /**
     * Removes an item from the user's cart
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to remove
     * @return The updated cart
     * @throws ServiceException if there's an error removing the item
     */
    @Override
    @Transactional
    public Cart removeCartItem(Long userId, Long productId) {
        if (userId == null || productId == null) {
            throw new ServiceException("User ID and product ID are required");
        }
        
        try {
            // Get cart
            Cart cart = getCartByUserId(userId);
            if (cart == null) {
                throw new ServiceException("No active cart found for user: " + userId);
            }
            
            // Remove item
            boolean removed = cart.removeItem(productId);
            if (!removed) {
                throw new ServiceException("Product not found in cart: " + productId);
            }
            
            // Update cart totals and timestamp
            cart.calculateTotal();
            cart.setLastUpdated(LocalDateTime.now());
            
            // Save updated cart
            Cart updatedCart = cartRepository.save(cart);
            logger.info("Removed product {} from cart for user {}", productId, userId);
            
            return updatedCart;
        } catch (ServiceException se) {
            // Re-throw service exceptions
            throw se;
        } catch (Exception e) {
            logger.error("Error removing item from cart: {}", e.getMessage());
            throw new ServiceException("Error removing item from cart: " + e.getMessage(), e);
        }
    }

    /**
     * Removes all items from the user's cart
     *
     * @param userId The ID of the user
     * @throws ServiceException if there's an error clearing the cart
     */
    @Override
    @Transactional
    public void clearCart(Long userId) {
        if (userId == null) {
            throw new ServiceException("User ID cannot be null");
        }
        
        try {
            // Get cart
            Cart cart = getCartByUserId(userId);
            if (cart == null) {
                logger.warn("No active cart found for user {} when attempting to clear cart", userId);
                return;
            }
            
            // Clear items
            cart.clearCart();
            cart.setTotalAmount(BigDecimal.ZERO);
            cart.setLastUpdated(LocalDateTime.now());
            
            // Save updated cart
            cartRepository.save(cart);
            logger.info("Cleared cart for user {}", userId);
        } catch (Exception e) {
            logger.error("Error clearing cart for user {}: {}", userId, e.getMessage());
            throw new ServiceException("Error clearing cart: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the active cart for a user
     *
     * @param userId The ID of the user
     * @return The user's active cart
     * @throws ServiceException if there's an error retrieving the cart
     */
    @Override
    @Transactional(readOnly = true)
    public Cart getCartByUserId(Long userId) {
        if (userId == null) {
            throw new ServiceException("User ID cannot be null");
        }
        
        try {
            Optional<Cart> cart = cartRepository.findByUserId(userId);
            if (!cart.isPresent()) {
                logger.debug("No active cart found for user: {}", userId);
                return null;
            }
            
            return cart.get();
        } catch (Exception e) {
            logger.error("Error retrieving cart for user {}: {}", userId, e.getMessage());
            throw new ServiceException("Error retrieving cart: " + e.getMessage(), e);
        }
    }

    /**
     * Gets product details for a cart item from the product service
     *
     * @param productId The ID of the product
     * @return Map containing product details
     * @throws ServiceException if there's an error retrieving product details
     */
    @Override
    public Map<String, Object> getProductDetails(Long productId) {
        if (productId == null) {
            throw new ServiceException("Product ID cannot be null");
        }
        
        try {
            // Call product service to get details
            Map<String, Object> productDetails = productService.getProductDetails(productId);
            
            if (productDetails == null || productDetails.isEmpty()) {
                logger.warn("Product not found: {}", productId);
                return null;
            }
            
            return productDetails;
        } catch (Exception e) {
            // FIXME: Implement circuit breaker pattern for product service calls
            logger.error("Error retrieving product details for product {}: {}", productId, e.getMessage());
            
            // Return minimal details to prevent cart operations from failing completely
            // TODO: Implement a more robust fallback mechanism
            Map<String, Object> fallbackDetails = new HashMap<>();
            fallbackDetails.put("id", productId);
            fallbackDetails.put("name", "Unknown Product");
            fallbackDetails.put("price", "0.00");
            fallbackDetails.put("stock", 0);
            
            return fallbackDetails;
        }
    }

    /**
     * Scheduled task that cleans up abandoned carts
     * Runs daily at 2:00 AM by default
     *
     * @return Number of carts cleaned up
     */
    @Override
    @Scheduled(cron = "${cart.cleanup.schedule:0 0 2 * * ?}")
    @Transactional
    public int cleanUpAbandonedCarts() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(cartTimeoutHours);
            logger.info("Running abandoned cart cleanup for carts older than: {}", cutoffTime);
            
            // Find abandoned carts
            var abandonedCarts = cartRepository.findAbandonedCarts(cutoffTime);
            
            if (abandonedCarts.isEmpty()) {
                logger.info("No abandoned carts found");
                return 0;
            }
            
            int count = 0;
            // Delete each abandoned cart
            for (Cart cart : abandonedCarts) {
                try {
                    cartRepository.delete(cart.getId());
                    count++;
                } catch (Exception e) {
                    logger.error("Failed to delete abandoned cart {}: {}", cart.getId(), e.getMessage());
                }
            }
            
            logger.info("Cleaned up {} abandoned carts", count);
            return count;
        } catch (Exception e) {
            logger.error("Error during abandoned cart cleanup: {}", e.getMessage(), e);
            return 0;
        }
    }
}