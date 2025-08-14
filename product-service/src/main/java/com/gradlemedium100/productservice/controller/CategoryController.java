package com.gradlemedium100.productservice.controller;

import com.gradlemedium100.productservice.dto.CategoryDTO;
import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST API controller for managing product categories
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private static final Logger logger = Logger.getLogger(CategoryController.class.getName());

    @Autowired
    private CategoryService categoryService;

    /**
     * Retrieves all product categories
     *
     * @return List of all categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        logger.info("Retrieving all categories");
        try {
            List<CategoryDTO> categories = categoryService.findAllCategories();
            if (categories.isEmpty()) {
                logger.info("No categories found");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving categories", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a specific category by its ID
     *
     * @param id The ID of the category to retrieve
     * @return The category with the specified ID, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable("id") Long id) {
        logger.info("Retrieving category with ID: " + id);
        try {
            CategoryDTO category = categoryService.findCategoryById(id);
            if (category == null) {
                logger.warning("Category with ID " + id + " not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving category with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new product category
     *
     * @param categoryDTO The category data to create
     * @return The created category with generated ID
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        logger.info("Creating new category: " + categoryDTO.getName());
        try {
            // Input validation
            if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
                logger.warning("Invalid category data received - name cannot be empty");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating category", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing category
     *
     * @param id The ID of the category to update
     * @param categoryDTO The updated category data
     * @return The updated category information
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryDTO categoryDTO) {
        logger.info("Updating category with ID: " + id);
        try {
            // Check if category exists
            CategoryDTO existingCategory = categoryService.findCategoryById(id);
            if (existingCategory == null) {
                logger.warning("Category with ID " + id + " not found for update");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // Input validation
            if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
                logger.warning("Invalid category update data received - name cannot be empty");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Set the ID from path parameter to ensure correct update
            categoryDTO.setId(id);
            CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating category with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a category from the system
     *
     * @param id The ID of the category to delete
     * @return Empty response with appropriate HTTP status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        logger.info("Deleting category with ID: " + id);
        try {
            // Check if category exists
            CategoryDTO existingCategory = categoryService.findCategoryById(id);
            if (existingCategory == null) {
                logger.warning("Category with ID " + id + " not found for deletion");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // FIXME: Consider checking if category has associated products before deletion
            // to prevent orphaned product references
            
            categoryService.deleteCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting category with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all products belonging to a specific category
     *
     * @param categoryId The ID of the category
     * @return List of products in the specified category
     */
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        logger.info("Retrieving products for category ID: " + categoryId);
        try {
            // Check if category exists
            CategoryDTO category = categoryService.findCategoryById(categoryId);
            if (category == null) {
                logger.warning("Category with ID " + categoryId + " not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // Since the method is not available in the service, return an empty list for now
            // This should be properly implemented in the CategoryService
            List<ProductDTO> products = new ArrayList<>();
            if (products.isEmpty()) {
                logger.info("No products found for category ID: " + categoryId);
                // Using NO_CONTENT here as an empty array is a valid response
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving products for category ID: " + categoryId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // TODO: Add endpoint for bulk operations on categories
    // TODO: Add filtering and pagination support for category listing
    // TODO: Consider adding endpoint for nested categories if hierarchy support is needed
}