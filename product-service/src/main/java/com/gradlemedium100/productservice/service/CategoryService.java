package com.gradlemedium100.productservice.service;

import com.gradlemedium100.productservice.dto.CategoryDTO;
import java.util.List;

/**
 * Service interface for category management operations.
 * Defines the contract for all operations related to product categories.
 */
public interface CategoryService {
    
    /**
     * Retrieves all product categories.
     * 
     * @return List of all category DTOs
     */
    List<CategoryDTO> findAllCategories();
    
    /**
     * Retrieves a category by its ID.
     * 
     * @param id The ID of the category to retrieve
     * @return The category with the specified ID, or null if not found
     */
    CategoryDTO findCategoryById(Long id);
    
    /**
     * Creates a new product category.
     * 
     * @param categoryDTO The category data to create
     * @return The created category with generated ID
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    
    /**
     * Updates an existing category.
     * 
     * @param id The ID of the category to update
     * @param categoryDTO The updated category data
     * @return The updated category information
     * @throws com.gradlemedium100.productservice.exception.ResourceNotFoundException if the category is not found
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    
    /**
     * Deletes a category.
     * 
     * @param id The ID of the category to delete
     * @throws com.gradlemedium100.productservice.exception.ResourceNotFoundException if the category is not found
     * @throws com.gradlemedium100.productservice.exception.CategoryNotEmptyException if the category has associated products
     */
    void deleteCategory(Long id);
}