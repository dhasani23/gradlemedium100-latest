package com.gradlemedium100.productservice.repository;

import com.gradlemedium100.productservice.model.Inventory;
import com.gradlemedium100.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Inventory entity.
 * Provides methods to access and query inventory data in the database.
 * Extends JpaRepository to inherit common CRUD operations.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Finds inventory information for a specific product.
     *
     * @param product the product to find inventory for
     * @return the inventory record associated with the product
     */
    Inventory findByProduct(Product product);
    
    /**
     * Finds inventory records with quantities below or equal to the specified threshold.
     * Can be used to identify low stock items that may need reordering.
     *
     * @param threshold the maximum quantity threshold
     * @return a list of inventory records with quantities less than or equal to the threshold
     */
    List<Inventory> findByQuantityLessThanEqual(Integer threshold);
    
    /**
     * Finds inventory records with quantities greater than the specified amount.
     * Can be used to identify well-stocked items.
     *
     * @param quantity the minimum quantity to filter by
     * @return a list of inventory records with quantities greater than the specified amount
     */
    List<Inventory> findByQuantityGreaterThan(Integer quantity);
    
    /**
     * Finds inventory records for a specific storage location.
     * Useful for warehouse management and inventory organization.
     *
     * @param location the storage location to search for
     * @return a list of inventory records stored at the specified location
     */
    List<Inventory> findByLocation(String location);
    
    // TODO: Add method to find inventory records that need restocking based on threshold
    
    // FIXME: The findByProduct method might return null if product doesn't exist,
    // should consider adding a default empty inventory or handle null values appropriately
}