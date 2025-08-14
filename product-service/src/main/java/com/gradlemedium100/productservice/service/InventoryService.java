package com.gradlemedium100.productservice.service;

import com.gradlemedium100.productservice.dto.InventoryDTO;
import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.model.Inventory;

import java.util.List;

/**
 * Service interface for inventory management operations.
 * This interface defines the contract for operations related to product inventory,
 * including retrieving inventory information, updating stock levels, checking product
 * availability, and identifying products with low stock levels.
 */
public interface InventoryService {

    /**
     * Retrieves inventory information for a specific product.
     *
     * @param productId the ID of the product
     * @return the inventory information as a DTO
     * @throws RuntimeException if the product doesn't exist or has no inventory record
     */
    InventoryDTO getInventoryForProduct(Long productId);

    /**
     * Updates inventory information for a product.
     *
     * @param productId    the ID of the product
     * @param inventoryDTO the updated inventory information
     * @return the updated inventory information
     * @throws RuntimeException if the product doesn't exist
     */
    InventoryDTO updateInventory(Long productId, InventoryDTO inventoryDTO);

    /**
     * Checks if a product is available in the requested quantity.
     *
     * @param productId the ID of the product
     * @param quantity  the requested quantity
     * @return true if the product is available in the requested quantity, false otherwise
     */
    boolean isProductAvailable(Long productId, int quantity);

    /**
     * Retrieves products with stock levels below the specified threshold.
     *
     * @param threshold the stock level threshold
     * @return a list of products with stock levels below the threshold
     */
    List<ProductDTO> getLowStockProducts(int threshold);
    
    /**
     * Converts an Inventory entity to its DTO representation.
     *
     * @param inventory the inventory entity
     * @return the inventory DTO
     */
    InventoryDTO convertToDTO(Inventory inventory);
    
    /**
     * Converts an InventoryDTO to its entity representation.
     *
     * @param inventoryDTO the inventory DTO
     * @return the inventory entity
     */
    Inventory convertToEntity(InventoryDTO inventoryDTO);
}