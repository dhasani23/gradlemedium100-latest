package com.gradlemedium100.dataaccess.repository;

import com.gradlemedium100.dataaccess.entity.ProductEntity;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for product-related database operations.
 * Provides methods to query and update product information in the database.
 */
public interface ProductRepository {

    /**
     * Finds products by name using partial match.
     *
     * @param name the product name to search for (supports partial matches)
     * @return a list of products matching the given name
     */
    List<ProductEntity> findByName(String name);

    /**
     * Finds products by category.
     *
     * @param category the category to filter products by
     * @return a list of products in the specified category
     */
    List<ProductEntity> findByCategory(String category);

    /**
     * Finds products with price less than the specified amount.
     *
     * @param price the maximum price threshold
     * @return a list of products with price less than the specified price
     */
    List<ProductEntity> findByPriceLessThan(BigDecimal price);

    /**
     * Finds products with price greater than the specified amount.
     *
     * @param price the minimum price threshold
     * @return a list of products with price greater than the specified price
     */
    List<ProductEntity> findByPriceGreaterThan(BigDecimal price);

    /**
     * Finds a product by its SKU (Stock Keeping Unit).
     * 
     * @param sku the unique SKU identifier
     * @return the product with the specified SKU or null if not found
     */
    ProductEntity findBySku(String sku);

    /**
     * Updates the stock quantity for a product.
     *
     * @param productId the ID of the product to update
     * @param quantity the new stock quantity
     * @throws IllegalArgumentException if the product ID is invalid
     * @throws RuntimeException if the update operation fails
     */
    void updateStock(Long productId, Integer quantity);
}