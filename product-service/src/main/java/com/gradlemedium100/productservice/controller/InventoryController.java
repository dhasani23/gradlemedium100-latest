package com.gradlemedium100.productservice.controller;

import com.gradlemedium100.productservice.dto.InventoryDTO;
import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for managing product inventory
 * Provides endpoints to retrieve and update inventory information, 
 * check product availability, and get low stock products.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    
    /**
     * Service for handling inventory business logic
     */
    private final InventoryService inventoryService;
    
    /**
     * Constructor for dependency injection
     * 
     * @param inventoryService the inventory service
     */
    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    /**
     * Retrieves inventory information for a specific product
     * 
     * @param productId ID of the product
     * @return ResponseEntity containing the inventory information
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryForProduct(@PathVariable Long productId) {
        logger.info("Retrieving inventory for product ID: {}", productId);
        
        try {
            InventoryDTO inventoryDTO = inventoryService.getInventoryForProduct(productId);
            
            if (inventoryDTO == null) {
                logger.warn("Inventory not found for product ID: {}", productId);
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(inventoryDTO);
        } catch (Exception e) {
            logger.error("Error retrieving inventory for product ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Updates inventory information for a product
     * 
     * @param productId ID of the product
     * @param inventoryDTO the inventory information to update
     * @return ResponseEntity containing the updated inventory information
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long productId, 
            @RequestBody InventoryDTO inventoryDTO) {
        
        logger.info("Updating inventory for product ID: {}", productId);
        
        // Validation to ensure product ID in path matches the DTO
        if (inventoryDTO.getProductId() != null && !inventoryDTO.getProductId().equals(productId)) {
            logger.warn("Product ID mismatch: path ID {} does not match DTO product ID {}", 
                    productId, inventoryDTO.getProductId());
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Set the product ID from the path parameter
            inventoryDTO.setProductId(productId);
            
            InventoryDTO updatedInventory = inventoryService.updateInventory(productId, inventoryDTO);
            return ResponseEntity.ok(updatedInventory);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid inventory update request for product ID: {}", productId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating inventory for product ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Checks if a product is available in the requested quantity
     * 
     * @param productId ID of the product
     * @param quantity the quantity to check
     * @return ResponseEntity with boolean indicating availability
     */
    @GetMapping("/products/{productId}/availability")
    public ResponseEntity<Boolean> checkProductAvailability(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        
        logger.info("Checking availability for product ID: {} with quantity: {}", productId, quantity);
        
        try {
            // Validate quantity is positive
            if (quantity <= 0) {
                logger.warn("Invalid quantity requested: {}", quantity);
                return ResponseEntity.badRequest().build();
            }
            
            boolean isAvailable = inventoryService.isProductAvailable(productId, quantity);
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            logger.error("Error checking availability for product ID: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves products with stock levels below the specified threshold
     * 
     * @param threshold the stock level threshold
     * @return ResponseEntity with a list of products below the threshold
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        
        logger.info("Retrieving products with stock below threshold: {}", threshold);
        
        try {
            // Validate threshold is not negative
            if (threshold < 0) {
                logger.warn("Invalid threshold value: {}", threshold);
                return ResponseEntity.badRequest().build();
            }
            
            List<ProductDTO> lowStockProducts = inventoryService.getLowStockProducts(threshold);
            return ResponseEntity.ok(lowStockProducts);
        } catch (Exception e) {
            logger.error("Error retrieving low stock products with threshold: {}", threshold, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // TODO: Add endpoint to batch update multiple inventory items at once
    
    // FIXME: Need to handle concurrency issues when multiple updates happen simultaneously
}