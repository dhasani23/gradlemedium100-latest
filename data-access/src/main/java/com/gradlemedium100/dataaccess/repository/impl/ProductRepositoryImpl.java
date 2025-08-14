package com.gradlemedium100.dataaccess.repository.impl;

import com.gradlemedium100.dataaccess.entity.ProductEntity;
import com.gradlemedium100.dataaccess.repository.ProductRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the product repository interface.
 * Provides specific database operations for product entities.
 */
@Repository
@Transactional
public class ProductRepositoryImpl extends BaseRepositoryImpl<ProductEntity> implements ProductRepository {

    /**
     * Constructor with EntityManager injection.
     *
     * @param entityManager the JPA entity manager
     */
    public ProductRepositoryImpl(EntityManager entityManager) {
        super(entityManager, ProductEntity.class);
    }

    /**
     * Find products by name (partial match).
     * This method performs a case-insensitive search using the LIKE operator.
     *
     * @param name the product name to search for
     * @return a list of products with names containing the search term
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);

        // Create LIKE predicate with case insensitivity
        Predicate namePredicate = cb.like(
            cb.lower(product.get("name")),
            "%" + name.toLowerCase() + "%"
        );

        cq.select(product).where(namePredicate);
        
        TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Find products by category.
     * This method performs an exact match on the category field.
     *
     * @param category the product category to search for
     * @return a list of products with the specified category
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);

        // Create equality predicate for category
        Predicate categoryPredicate = cb.equal(product.get("category"), category);

        cq.select(product).where(categoryPredicate);
        
        TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Find products with price less than the specified amount.
     *
     * @param price the maximum price to search for
     * @return a list of products with prices less than the specified amount
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByPriceLessThan(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);

        // Create less than predicate for price
        Predicate pricePredicate = cb.lessThan(product.get("price"), price);

        cq.select(product).where(pricePredicate);
        
        TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Find products with price greater than the specified amount.
     *
     * @param price the minimum price to search for
     * @return a list of products with prices greater than the specified amount
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> findByPriceGreaterThan(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);

        // Create greater than predicate for price
        Predicate pricePredicate = cb.greaterThan(product.get("price"), price);

        cq.select(product).where(pricePredicate);
        
        TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Find a product by its SKU.
     * SKU is a business key for products and should be unique.
     *
     * @param sku the SKU to search for
     * @return the product with the specified SKU, or null if none found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductEntity findBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
            Root<ProductEntity> product = cq.from(ProductEntity.class);

            // Create equality predicate for SKU
            Predicate skuPredicate = cb.equal(product.get("sku"), sku);

            cq.select(product).where(skuPredicate);
            
            TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
            // FIXME: This could potentially return multiple results if SKU uniqueness
            // is not properly enforced at the database level
            return query.getSingleResult();
        } catch (NoResultException e) {
            // No product found with the given SKU
            return null;
        }
    }

    /**
     * Update the stock quantity for a product.
     * This method provides a direct way to update inventory levels.
     *
     * @param productId the ID of the product to update
     * @param quantity the new stock quantity
     * @throws IllegalArgumentException if product ID is null or quantity is negative
     */
    @Override
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        
        if (quantity != null && quantity < 0) {
            // TODO: Decide if negative stock quantities should be allowed based on business rules
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        findById(productId).ifPresent(product -> {
            // Update the stock quantity
            product.setStockQuantity(quantity);
        });
    }
    
    /**
     * Find products that are low on stock (below the specified threshold).
     * This is a utility method that could be used for inventory management.
     *
     * @param threshold the stock threshold to check against
     * @return a list of products with stock quantities below the threshold
     */
    @Transactional(readOnly = true)
    public List<ProductEntity> findLowStockProducts(Integer threshold) {
        if (threshold == null || threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be null or negative");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);

        // Create predicate for products with stock below threshold and that are active
        Predicate stockPredicate = cb.lessThanOrEqualTo(product.get("stockQuantity"), threshold);
        Predicate activePredicate = cb.equal(product.get("active"), true);
        Predicate finalPredicate = cb.and(stockPredicate, activePredicate);

        cq.select(product).where(finalPredicate);
        
        TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
    
    /**
     * Find products by matching a search term against name, description, or SKU.
     * This method provides a more flexible search capability.
     *
     * @param searchTerm the term to search for
     * @return a list of products matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<ProductEntity> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        
        String likePattern = "%" + searchTerm.toLowerCase() + "%";
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);
        
        // Create predicates for name, description, and SKU
        Predicate namePredicate = cb.like(cb.lower(product.get("name")), likePattern);
        Predicate descPredicate = cb.like(cb.lower(product.get("description")), likePattern);
        Predicate skuPredicate = cb.like(cb.lower(product.get("sku")), likePattern);
        
        // Combine predicates with OR
        Predicate finalPredicate = cb.or(namePredicate, descPredicate, skuPredicate);
        
        cq.select(product).where(finalPredicate);
        
        TypedQuery<ProductEntity> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}