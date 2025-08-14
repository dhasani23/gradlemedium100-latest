package com.gradlemedium100.productservice.service;

import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.dto.ProductSearchCriteria;

import java.util.List;

/**
 * Service interface for product management operations.
 * <p>
 * This interface defines the core operations for managing products in the system,
 * including retrieval, creation, update, deletion, and searching functionalities.
 * </p>
 * 
 * @since 1.0
 */
public interface ProductService {

    /**
     * Retrieves all products from the catalog.
     *
     * @return A list of all products as DTOs
     */
    List<ProductDTO> findAllProducts();

    /**
     * Retrieves a specific product by its unique identifier.
     *
     * @param id The unique identifier of the product
     * @return The product DTO if found
     * @throws com.gradlemedium100.productservice.exception.ResourceNotFoundException if the product is not found
     */
    ProductDTO findProductById(Long id);

    /**
     * Creates a new product in the catalog.
     *
     * @param productDTO The data transfer object containing the product information
     * @return The created product as a DTO with generated ID
     * @throws com.gradlemedium100.productservice.exception.ValidationException if the product data is invalid
     */
    ProductDTO createProduct(ProductDTO productDTO);

    /**
     * Updates an existing product's information.
     *
     * @param id The unique identifier of the product to update
     * @param productDTO The data transfer object containing the updated product information
     * @return The updated product as a DTO
     * @throws com.gradlemedium100.productservice.exception.ResourceNotFoundException if the product is not found
     * @throws com.gradlemedium100.productservice.exception.ValidationException if the updated product data is invalid
     */
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * Deletes a product from the catalog.
     *
     * @param id The unique identifier of the product to delete
     * @throws com.gradlemedium100.productservice.exception.ResourceNotFoundException if the product is not found
     * @throws com.gradlemedium100.productservice.exception.BusinessException if the product cannot be deleted
     */
    void deleteProduct(Long id);

    /**
     * Searches for products based on various criteria.
     * <p>
     * This method allows flexible searching of products using criteria such as:
     * - Keyword search in name and description
     * - Category filtering
     * - Price range filtering
     * - Sorting options
     * - Pagination
     * </p>
     *
     * @param criteria The search criteria containing various filter parameters
     * @return A list of products matching the search criteria
     * 
     * @see ProductSearchCriteria
     */
    List<ProductDTO> searchProducts(ProductSearchCriteria criteria);

    // TODO: Add method to handle bulk product operations
    
    // FIXME: Consider adding caching strategy for frequently accessed products
}