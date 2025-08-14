package com.gradlemedium100.orderservice.controller;

import com.gradlemedium100.orderservice.service.CartService;
import com.gradlemedium100.orderservice.model.Cart;
import com.gradlemedium100.common.dto.CartDTO;
import com.gradlemedium100.common.exception.ControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * REST controller for shopping cart operations.
 * Handles all cart-related HTTP requests.
 */
@RestController
@RequestMapping("/api/carts")
public class CartController {

    private static final Logger logger = Logger.getLogger(CartController.class.getName());
    
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Retrieves the active cart for a specific user.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing the CartDTO
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        logger.info("Getting cart for user: " + userId);
        try {
            Cart cart = cartService.getActiveCart(userId);
            if (cart == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(convertToDTO(cart), HttpStatus.OK);
        } catch (Exception e) {
            logger.severe("Error retrieving cart for user " + userId + ": " + e.getMessage());
            throw new ControllerException("Failed to retrieve cart", e);
        }
    }

    /**
     * Adds an item to the user's shopping cart.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to add
     * @param quantity  the quantity to add
     * @return ResponseEntity containing the updated CartDTO
     */
    @PostMapping("/user/{userId}/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        logger.info("Adding item to cart: userId=" + userId + ", productId=" + productId + ", quantity=" + quantity);
        
        try {
            // Validate input
            if (quantity <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Cart updatedCart = cartService.addItemToCart(userId, productId, quantity);
            return new ResponseEntity<>(convertToDTO(updatedCart), HttpStatus.OK);
        } catch (Exception e) {
            logger.severe("Error adding item to cart: " + e.getMessage());
            throw new ControllerException("Failed to add item to cart", e);
        }
    }

    /**
     * Updates the quantity of an item in the user's cart.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to update
     * @param quantity  the new quantity
     * @return ResponseEntity containing the updated CartDTO
     */
    @PutMapping("/user/{userId}/items/{productId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        logger.info("Updating cart item quantity: userId=" + userId + ", productId=" + productId + ", quantity=" + quantity);

        try {
            // Validate input
            if (quantity <= 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            Cart updatedCart = cartService.updateCartItemQuantity(userId, productId, quantity);
            if (updatedCart == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(convertToDTO(updatedCart), HttpStatus.OK);
        } catch (Exception e) {
            logger.severe("Error updating cart item quantity: " + e.getMessage());
            throw new ControllerException("Failed to update cart item quantity", e);
        }
    }

    /**
     * Removes an item from the user's cart.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to remove
     * @return ResponseEntity containing the updated CartDTO
     */
    @DeleteMapping("/user/{userId}/items/{productId}")
    public ResponseEntity<CartDTO> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        logger.info("Removing item from cart: userId=" + userId + ", productId=" + productId);
        
        try {
            Cart updatedCart = cartService.removeCartItem(userId, productId);
            if (updatedCart == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(convertToDTO(updatedCart), HttpStatus.OK);
        } catch (Exception e) {
            logger.severe("Error removing cart item: " + e.getMessage());
            throw new ControllerException("Failed to remove cart item", e);
        }
    }

    /**
     * Clears all items from a user's cart.
     *
     * @param userId the ID of the user
     * @return ResponseEntity with no content if successful
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        logger.info("Clearing cart for user: " + userId);
        
        try {
            boolean success = cartService.clearCart(userId);
            if (!success) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.severe("Error clearing cart: " + e.getMessage());
            throw new ControllerException("Failed to clear cart", e);
        }
    }

    /**
     * Converts a Cart entity to a CartDTO for API responses.
     * 
     * @param cart the Cart entity to convert
     * @return the converted CartDTO
     */
    protected CartDTO convertToDTO(Cart cart) {
        // TODO: Implement a more efficient conversion, possibly using a mapper library
        if (cart == null) {
            return null;
        }
        
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(cart.getItems());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        dto.setTotalPrice(calculateTotalPrice(cart));
        
        return dto;
    }
    
    /**
     * Calculates the total price of all items in the cart.
     * 
     * @param cart the Cart to calculate total for
     * @return the total price
     */
    private Double calculateTotalPrice(Cart cart) {
        // FIXME: This is a simplistic calculation and might not handle discounts correctly
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0.0;
        }
        
        return cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Global exception handler for the controller.
     *
     * @param ex the exception that was thrown
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        logger.severe("Controller exception: " + ex.getMessage());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (ex instanceof ControllerException) {
            // Log the original cause if available
            if (ex.getCause() != null) {
                logger.severe("Caused by: " + ex.getCause().getMessage());
            }
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}