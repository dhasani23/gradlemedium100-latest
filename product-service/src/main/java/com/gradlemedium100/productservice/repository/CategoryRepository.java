package com.gradlemedium100.productservice.repository;

import com.gradlemedium100.productservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Category entity.
 * Provides methods to perform CRUD operations on Category entities
 * as well as custom search operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find a category by its exact name
     *
     * @param name the category name to search for
     * @return the matching Category or null if not found
     */
    Category findByName(String name);
    
    /**
     * Find all categories that contain the specified keyword in their names
     *
     * @param keyword the partial name to search for
     * @return list of categories containing the keyword in their names
     */
    List<Category> findByNameContaining(String keyword);
    
    /**
     * Find all subcategories of a given parent category
     *
     * @param parentCategory the parent category
     * @return list of all child categories of the specified parent
     */
    List<Category> findByParentCategory(Category parentCategory);
    
    /**
     * Find all top-level categories (categories with no parent)
     *
     * @return list of all root/top-level categories
     */
    List<Category> findByParentCategoryIsNull();
    
    /**
     * Alternative method to find subcategories with a JPQL query
     * This provides an example of using a custom query
     *
     * @param parentId the ID of the parent category
     * @return list of child categories for the given parent ID
     */
    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Category> findSubcategoriesByParentId(@Param("parentId") Long parentId);
    
    /**
     * Find categories by a specific attribute
     * This is an example of a more complex query that could be implemented
     * 
     * @param attributeName the name of the attribute to filter by
     * @param attributeValue the value of the attribute
     * @return list of categories matching the attribute criteria
     * @throws UnsupportedOperationException if not implemented
     * 
     * TODO: Implement this method when attribute functionality is added to Category
     */
    @Query(value = "SELECT c.* FROM categories c JOIN category_attributes ca ON c.id = ca.category_id " +
           "WHERE ca.name = :attributeName AND ca.value = :attributeValue", nativeQuery = true)
    default List<Category> findByAttribute(@Param("attributeName") String attributeName, 
                                  @Param("attributeValue") String attributeValue) {
        // This is just a placeholder until attribute functionality is implemented
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}