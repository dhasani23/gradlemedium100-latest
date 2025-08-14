package com.gradlemedium100.productservice.service.impl;

import com.gradlemedium100.productservice.dto.InventoryDTO;
import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.model.Inventory;
import com.gradlemedium100.productservice.model.Product;
import com.gradlemedium100.productservice.repository.InventoryRepository;
import com.gradlemedium100.productservice.repository.ProductRepository;
import com.gradlemedium100.productservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of InventoryService that handles product inventory operations.
 * This service provides functionality for managing inventory levels, checking product 
 * availability, and identifying products with low stock levels.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, 
                                ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Gets inventory information for a specific product.
     *
     * @param productId the ID of the product
     * @return the inventory information as a DTO
     * @throws RuntimeException if the product doesn't exist or has no inventory record
     */
    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getInventoryForProduct(Long productId) {
        logger.debug("Getting inventory for product with ID: {}", productId);
        
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            logger.error("Product with ID {} not found", productId);
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        
        Product product = productOptional.get();
        Inventory inventory = inventoryRepository.findByProduct(product);
        
        if (inventory == null) {
            logger.warn("No inventory record found for product with ID: {}", productId);
            throw new RuntimeException("No inventory record found for product with ID: " + productId);
        }
        
        return convertToDTO(inventory);
    }

    /**
     * Updates inventory information for a product.
     *
     * @param productId    the ID of the product
     * @param inventoryDTO the updated inventory information
     * @return the updated inventory information
     * @throws RuntimeException if the product doesn't exist
     */
    @Override
    @Transactional
    public InventoryDTO updateInventory(Long productId, InventoryDTO inventoryDTO) {
        logger.debug("Updating inventory for product with ID: {}", productId);
        
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            logger.error("Product with ID {} not found", productId);
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        
        Product product = productOptional.get();
        Inventory inventory = inventoryRepository.findByProduct(product);
        
        if (inventory == null) {
            // Create new inventory record if one doesn't exist
            inventory = new Inventory();
            inventory.setProduct(product);
        }
        
        // Update inventory fields
        inventory.setQuantity(inventoryDTO.getQuantity());
        inventory.setLowStockThreshold(inventoryDTO.getLowStockThreshold());
        inventory.setLocation(inventoryDTO.getLocation());
        inventory.setLastRestocked(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        
        // Save and return updated inventory
        inventory = inventoryRepository.save(inventory);
        logger.info("Inventory updated for product with ID: {}", productId);
        
        return convertToDTO(inventory);
    }

    /**
     * Checks if a product is available in the requested quantity.
     *
     * @param productId the ID of the product
     * @param quantity  the requested quantity
     * @return true if the product is available in the requested quantity, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long productId, int quantity) {
        logger.debug("Checking availability for product ID: {} with quantity: {}", productId, quantity);
        
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            logger.warn("Product availability check failed - product with ID {} not found", productId);
            return false;
        }
        
        Product product = productOptional.get();
        Inventory inventory = inventoryRepository.findByProduct(product);
        
        if (inventory == null) {
            logger.warn("Product availability check failed - no inventory record for product with ID {}", productId);
            return false;
        }
        
        boolean isAvailable = inventory.getQuantity() >= quantity;
        logger.debug("Product with ID {} availability result: {}", productId, isAvailable);
        
        return isAvailable;
    }

    /**
     * Retrieves products with stock levels below the specified threshold.
     *
     * @param threshold the stock level threshold
     * @return a list of products with stock levels below the threshold
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(int threshold) {
        logger.debug("Getting products with stock levels below threshold: {}", threshold);
        
        List<Inventory> lowStockInventory = inventoryRepository.findByQuantityLessThanEqual(threshold);
        
        if (lowStockInventory.isEmpty()) {
            logger.info("No low stock products found below threshold: {}", threshold);
            return new ArrayList<>();
        }
        
        // Convert to ProductDTOs
        List<ProductDTO> lowStockProducts = lowStockInventory.stream()
            .map(inventory -> {
                Product product = inventory.getProduct();
                ProductDTO dto = new ProductDTO();
                dto.setId(product.getId());
                dto.setName(product.getName());
                dto.setDescription(product.getDescription());
                dto.setPrice(product.getPrice());
                dto.setSku(product.getSku());
                
                // Set category info if available
                if (product.getCategory() != null) {
                    dto.setCategoryId(product.getCategory().getId());
                    dto.setCategoryName(product.getCategory().getName());
                }
                
                dto.setImageUrl(product.getImageUrl());
                return dto;
            })
            .collect(Collectors.toList());
        
        logger.info("Found {} products with stock levels below threshold: {}", 
                   lowStockProducts.size(), threshold);
        
        return lowStockProducts;
    }

    /**
     * Converts an Inventory entity to its DTO representation.
     *
     * @param inventory the inventory entity
     * @return the inventory DTO
     */
    @Override
    public InventoryDTO convertToDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        
        InventoryDTO dto = new InventoryDTO();
        dto.setId(inventory.getId());
        
        // Set product info if available
        Product product = inventory.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
        }
        
        dto.setQuantity(inventory.getQuantity());
        dto.setLastRestocked(inventory.getLastRestocked());
        dto.setLowStockThreshold(inventory.getLowStockThreshold());
        dto.setLocation(inventory.getLocation());
        
        return dto;
    }

    /**
     * Converts an InventoryDTO to its entity representation.
     * This method creates a new entity or updates an existing one if provided.
     *
     * @param inventoryDTO the inventory DTO
     * @return the inventory entity
     */
    @Override
    public Inventory convertToEntity(InventoryDTO inventoryDTO) {
        if (inventoryDTO == null) {
            return null;
        }
        
        Inventory inventory = new Inventory();
        
        // Set ID if it exists (for updates)
        if (inventoryDTO.getId() != null) {
            inventory.setId(inventoryDTO.getId());
            // FIXME: Should check if inventory with this ID exists
        }
        
        // Set product if productId is provided
        if (inventoryDTO.getProductId() != null) {
            Optional<Product> productOptional = productRepository.findById(inventoryDTO.getProductId());
            productOptional.ifPresent(inventory::setProduct);
            
            // TODO: Handle case when product doesn't exist
        }
        
        inventory.setQuantity(inventoryDTO.getQuantity());
        inventory.setLowStockThreshold(inventoryDTO.getLowStockThreshold());
        inventory.setLocation(inventoryDTO.getLocation());
        
        // Set timestamps if not provided
        if (inventoryDTO.getLastRestocked() != null) {
            inventory.setLastRestocked(inventoryDTO.getLastRestocked());
        } else {
            inventory.setLastRestocked(LocalDateTime.now());
        }
        
        inventory.setUpdatedAt(LocalDateTime.now());
        
        return inventory;
    }
}