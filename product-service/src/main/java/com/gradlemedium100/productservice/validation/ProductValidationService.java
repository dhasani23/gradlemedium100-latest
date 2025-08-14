package com.gradlemedium100.productservice.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gradlemedium100.productservice.dto.ProductDTO;

/**
 * Service responsible for validating product data.
 * This class implements business rules for product validation
 * to ensure data integrity and compliance with business requirements.
 */
@Service
public class ProductValidationService {

    private int minimumNameLength = 3;
    private int maximumNameLength = 100;
    private double minimumPrice = 0.01;
    private int maximumDescriptionLength = 2000;

    /**
     * Validates a product DTO against business rules
     * 
     * @param product The product to validate
     * @return true if the product is valid, false otherwise
     */
    public boolean isValid(ProductDTO product) {
        return getValidationErrors(product).isEmpty();
    }
    
    /**
     * Validates a product and returns a list of validation errors
     * 
     * @param product The product to validate
     * @return List of validation error messages (empty if valid)
     */
    public List<String> getValidationErrors(ProductDTO product) {
        List<String> errors = new ArrayList<>();
        
        if (product == null) {
            errors.add("Product cannot be null");
            return errors;
        }
        
        // Validate product name
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.add("Product name is required");
        } else {
            if (product.getName().length() < minimumNameLength) {
                errors.add("Product name must be at least " + minimumNameLength + " characters long");
            }
            if (product.getName().length() > maximumNameLength) {
                errors.add("Product name cannot exceed " + maximumNameLength + " characters");
            }
        }
        
        // Validate product price
        if (product.getPrice() == null) {
            errors.add("Product price is required");
        } else {
            if (product.getPrice().compareTo(BigDecimal.valueOf(minimumPrice)) < 0) {
                errors.add("Product price must be at least " + minimumPrice);
            }
        }
        
        // Validate SKU
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            errors.add("Product SKU is required");
        }
        
        // Validate description length if present
        if (product.getDescription() != null && 
            product.getDescription().length() > maximumDescriptionLength) {
            errors.add("Product description cannot exceed " + maximumDescriptionLength + " characters");
        }
        
        // Category validation
        if (product.getCategoryId() == null) {
            errors.add("Product must belong to a category");
        }
        
        return errors;
    }

    /**
     * Set the minimum allowed length for product names
     * 
     * @param minimumNameLength The minimum length
     */
    public void setMinimumNameLength(int minimumNameLength) {
        this.minimumNameLength = minimumNameLength;
    }

    /**
     * Set the maximum allowed length for product names
     * 
     * @param maximumNameLength The maximum length
     */
    public void setMaximumNameLength(int maximumNameLength) {
        this.maximumNameLength = maximumNameLength;
    }

    /**
     * Set the minimum allowed price for products
     * 
     * @param minimumPrice The minimum price
     */
    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    /**
     * Set the maximum allowed length for product descriptions
     * 
     * @param maximumDescriptionLength The maximum length
     */
    public void setMaximumDescriptionLength(int maximumDescriptionLength) {
        this.maximumDescriptionLength = maximumDescriptionLength;
    }
}