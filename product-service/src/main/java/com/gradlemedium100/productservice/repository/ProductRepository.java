package com.gradlemedium100.productservice.repository;

import com.gradlemedium100.productservice.model.Category;
import com.gradlemedium100.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Product entity.
 * Provides methods for querying products from the database.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds products by their exact name.
     * 
     * @param name the product name to search for
     * @return a list of products matching the given name
     */
    List<Product> findByName(String name);
    
    /**
     * Finds products containing the given keyword in their names.
     * This method performs a case-insensitive partial match on product names.
     * 
     * @param keyword the keyword to search for within product names
     * @return a list of products containing the keyword in their names
     */
    List<Product> findByNameContaining(String keyword);
    
    /**
     * Finds products belonging to a specific category.
     * 
     * @param category the category to filter products by
     * @return a list of products in the specified category
     */
    List<Product> findByCategory(Category category);
    
    /**
     * Finds products within a specified price range.
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return a list of products with prices within the specified range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Finds a product by its unique SKU (Stock Keeping Unit).
     * 
     * @param sku the SKU to search for
     * @return the product with the matching SKU, or null if not found
     * 
     * TODO: Consider returning Optional<Product> instead of Product directly
     * FIXME: Add unique constraint on SKU column in the database schema
     */
    Product findBySku(String sku);
    
    // Additional query methods could be added here as the application grows
}