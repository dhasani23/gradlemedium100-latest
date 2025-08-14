package com.gradlemedium100.productservice.controller;

import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.dto.ProductSearchCriteria;
import com.gradlemedium100.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST API controller that handles HTTP requests related to products,
 * including CRUD operations, search, and filtering.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger LOGGER = Logger.getLogger(ProductController.class.getName());

    private final ProductService productService;

    /**
     * Constructor for dependency injection.
     *
     * @param productService Service for handling product business logic
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products from the catalog.
     *
     * @return ResponseEntity containing a list of ProductDTO objects
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            LOGGER.info("Retrieving all products");
            List<ProductDTO> products = productService.findAllProducts();
            
            if (products.isEmpty()) {
                LOGGER.info("No products found in the catalog");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all products", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id The product's unique identifier
     * @return ResponseEntity containing the ProductDTO if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        try {
            LOGGER.info("Retrieving product with ID: " + id);
            
            if (id == null || id <= 0) {
                LOGGER.warning("Invalid product ID: " + id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            ProductDTO product = productService.findProductById(id);
            
            if (product == null) {
                LOGGER.warning("Product not found with ID: " + id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new product in the catalog.
     *
     * @param productDTO The product data to create
     * @return ResponseEntity containing the created ProductDTO with status 201
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            LOGGER.info("Creating new product");
            
            if (productDTO == null) {
                LOGGER.warning("Invalid product data: null");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // FIXME: Add additional validation for product data
            
            ProductDTO createdProduct = productService.createProduct(productDTO);
            LOGGER.info("Product created successfully with ID: " + createdProduct.getId());
            
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid product data: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating product", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing product's information.
     *
     * @param id The product's unique identifier
     * @param productDTO The updated product data
     * @return ResponseEntity containing the updated ProductDTO, or appropriate error status
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO) {
        try {
            LOGGER.info("Updating product with ID: " + id);
            
            if (id == null || id <= 0 || productDTO == null) {
                LOGGER.warning("Invalid update request for product ID: " + id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Ensure the ID in the path matches the ID in the DTO if present
            if (productDTO.getId() != null && !id.equals(productDTO.getId())) {
                LOGGER.warning("Path ID and body ID do not match for product update");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Set the ID to ensure we're updating the right product
            productDTO.setId(id);
            
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            
            if (updatedProduct == null) {
                LOGGER.warning("Product not found for update with ID: " + id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            LOGGER.info("Product updated successfully with ID: " + id);
            
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid update data for product ID " + id + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating product with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a product from the catalog.
     *
     * @param id The product's unique identifier
     * @return ResponseEntity with no content and status 204 on success, or appropriate error status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting product with ID: " + id);
            
            if (id == null || id <= 0) {
                LOGGER.warning("Invalid product ID for deletion: " + id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            productService.deleteProduct(id);
            
            LOGGER.info("Product deleted successfully with ID: " + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting product with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Searches products based on various criteria.
     *
     * @param criteria The search criteria for filtering products
     * @return ResponseEntity containing a list of matching ProductDTO objects
     */
    @PostMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestBody ProductSearchCriteria criteria) {
        try {
            LOGGER.info("Searching products with criteria: " + criteria);
            
            if (criteria == null) {
                // If no criteria provided, return all products
                // TODO: Consider if returning all products is appropriate for empty search criteria
                return getAllProducts();
            }
            
            // Validate search criteria
            if (!isValidSearchCriteria(criteria)) {
                LOGGER.warning("Invalid search criteria provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<ProductDTO> matchingProducts = productService.searchProducts(criteria);
            
            if (matchingProducts.isEmpty()) {
                LOGGER.info("No products found matching search criteria");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            LOGGER.info("Found " + matchingProducts.size() + " products matching search criteria");
            return new ResponseEntity<>(matchingProducts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid search criteria: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching products", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Validates the provided search criteria.
     * 
     * @param criteria The search criteria to validate
     * @return true if the criteria is valid, false otherwise
     */
    private boolean isValidSearchCriteria(ProductSearchCriteria criteria) {
        // Implement validation logic for search criteria
        // This is a simple placeholder implementation
        
        // TODO: Implement proper validation for search criteria based on business rules
        
        // For example, ensure price ranges are valid
        if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null) {
            if (criteria.getMinPrice().compareTo(criteria.getMaxPrice()) > 0) {
                return false;
            }
        }
        
        return true;
    }
}