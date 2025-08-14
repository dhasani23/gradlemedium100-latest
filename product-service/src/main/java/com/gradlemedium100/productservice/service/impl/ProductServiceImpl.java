package com.gradlemedium100.productservice.service.impl;

import com.gradlemedium100.common.exception.ResourceNotFoundException;
import com.gradlemedium100.common.util.CacheManager;
import com.gradlemedium100.productservice.dto.ProductDTO;
import com.gradlemedium100.productservice.dto.ProductSearchCriteria;
import com.gradlemedium100.productservice.model.Product;
import com.gradlemedium100.productservice.model.Category;
import com.gradlemedium100.productservice.repository.ProductRepository;
import com.gradlemedium100.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService that manages product operations with caching support.
 * This service provides methods for retrieving, creating, updating, and deleting products,
 * with caching strategies to improve performance for frequently accessed data.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final String PRODUCTS_CACHE_KEY = "all_products";
    private static final String PRODUCT_CACHE_PREFIX = "product_";
    
    private final ProductRepository productRepository;
    private final CacheManager cacheManager;

    /**
     * Constructor for ProductServiceImpl.
     * 
     * @param productRepository Repository for product data access
     * @param cacheManager Cache manager for product data
     */
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CacheManager cacheManager) {
        this.productRepository = productRepository;
        this.cacheManager = cacheManager;
    }

    /**
     * Retrieves all products with caching support.
     * First attempts to get products from cache, if not present then fetches from database.
     * 
     * @return List of ProductDTO objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllProducts() {
        logger.debug("Finding all products with cache support");
        
        // Try to get from cache first
        @SuppressWarnings("unchecked")
        List<ProductDTO> cachedProducts = (List<ProductDTO>) cacheManager.get(PRODUCTS_CACHE_KEY);
        
        if (cachedProducts != null) {
            logger.debug("Retrieved {} products from cache", cachedProducts.size());
            return cachedProducts;
        }
        
        // If not in cache, get from database and cache the result
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Store in cache
        cacheManager.put(PRODUCTS_CACHE_KEY, productDTOs);
        logger.debug("Cached {} products", productDTOs.size());
        
        return productDTOs;
    }

    /**
     * Retrieves a product by ID with caching support.
     * First attempts to get product from cache, if not present then fetches from database.
     * 
     * @param id The ID of the product to retrieve
     * @return ProductDTO object
     * @throws ResourceNotFoundException if product is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findProductById(Long id) {
        logger.debug("Finding product by ID: {} with cache support", id);
        
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        ProductDTO cachedProduct = (ProductDTO) cacheManager.get(cacheKey);
        
        if (cachedProduct != null) {
            logger.debug("Retrieved product from cache: {}", cachedProduct.getName());
            return cachedProduct;
        }
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id.toString()));
        
        ProductDTO productDTO = convertToDTO(product);
        
        // Store in cache
        cacheManager.put(cacheKey, productDTO);
        logger.debug("Cached product: {}", productDTO.getName());
        
        return productDTO;
    }

    /**
     * Creates a new product and invalidates relevant caches.
     * 
     * @param productDTO The product data to create
     * @return Created ProductDTO object
     */
    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        logger.debug("Creating new product: {}", productDTO.getName());
        
        Product product = convertToEntity(productDTO);
        
        // Set audit fields
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        ProductDTO savedProductDTO = convertToDTO(savedProduct);
        
        // Invalidate cache for all products
        cacheManager.remove(PRODUCTS_CACHE_KEY);
        logger.debug("Invalidated all products cache after creation");
        
        return savedProductDTO;
    }

    /**
     * Updates an existing product and refreshes relevant caches.
     * 
     * @param id The ID of the product to update
     * @param productDTO The updated product data
     * @return Updated ProductDTO object
     * @throws ResourceNotFoundException if product is not found
     */
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        logger.debug("Updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id.toString()));
        
        // Update fields from DTO
        updateProductFromDTO(existingProduct, productDTO);
        
        // Update audit fields
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(existingProduct);
        ProductDTO updatedProductDTO = convertToDTO(updatedProduct);
        
        // Refresh caches
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        cacheManager.remove(PRODUCTS_CACHE_KEY);
        cacheManager.put(cacheKey, updatedProductDTO);
        
        logger.debug("Refreshed caches after updating product: {}", updatedProductDTO.getName());
        
        return updatedProductDTO;
    }

    /**
     * Deletes a product and invalidates relevant caches.
     * 
     * @param id The ID of the product to delete
     * @throws ResourceNotFoundException if product is not found
     */
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        logger.debug("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id.toString());
        }
        
        productRepository.deleteById(id);
        
        // Invalidate caches
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        cacheManager.remove(cacheKey);
        cacheManager.remove(PRODUCTS_CACHE_KEY);
        
        logger.debug("Invalidated caches after deleting product with ID: {}", id);
    }

    /**
     * Searches for products based on various criteria.
     * This method does not use caching since search results vary based on criteria.
     * 
     * @param criteria The search criteria to apply
     * @return List of ProductDTO objects matching the criteria
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(ProductSearchCriteria criteria) {
        logger.debug("Searching products with criteria: keyword={}, categoryId={}, priceRange={}-{}", 
                criteria.getKeyword(), criteria.getCategoryId(), 
                criteria.getMinPrice(), criteria.getMaxPrice());
        
        List<Product> products = new ArrayList<>();
        
        // Apply search filters - this is a simplistic implementation
        // In a real-world scenario, you might use JPA Criteria API or a search engine like Elasticsearch
        
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            products.addAll(productRepository.findByNameContaining(criteria.getKeyword()));
        } else if (criteria.getCategoryId() != null) {
            // FIXME: This implementation requires fetching the Category entity first
            // A more efficient approach would be to add a findByCategoryId method to the repository
            Category category = new Category();
            category.setId(criteria.getCategoryId());
            products.addAll(productRepository.findByCategory(category));
        } else if (criteria.getMinPrice() != null && criteria.getMaxPrice() != null) {
            products.addAll(productRepository.findByPriceBetween(criteria.getMinPrice(), criteria.getMaxPrice()));
        } else {
            // If no specific criteria, return all products
            products = productRepository.findAll();
        }
        
        // TODO: Implement sorting based on criteria.getSortBy() and criteria.getSortDirection()
        // TODO: Implement pagination based on criteria.getPage() and criteria.getPageSize()
        
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Product entity to its DTO representation.
     * 
     * @param product The Product entity to convert
     * @return ProductDTO object
     */
    private ProductDTO convertToDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setSku(product.getSku());
        productDTO.setImageUrl(product.getImageUrl());
        
        // Handle category if present
        if (product.getCategory() != null) {
            productDTO.setCategoryId(product.getCategory().getId());
            productDTO.setCategoryName(product.getCategory().getName());
        }
        
        return productDTO;
    }

    /**
     * Converts a ProductDTO to its entity representation.
     * Note: This does not fully populate the Category relationship,
     * which typically requires additional repository lookups.
     * 
     * @param productDTO The ProductDTO to convert
     * @return Product entity
     */
    private Product convertToEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }
        
        Product product = new Product();
        
        // Skip setting ID for new products, but set it for existing ones
        if (productDTO.getId() != null) {
            product.setId(productDTO.getId());
        }
        
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSku(productDTO.getSku());
        product.setImageUrl(productDTO.getImageUrl());
        
        // Handle category if categoryId is present
        if (productDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(productDTO.getCategoryId());
            product.setCategory(category);
            
            // TODO: In a real implementation, you might want to fetch the complete
            // Category entity from the repository using the categoryId
        }
        
        return product;
    }
    
    /**
     * Updates an existing product entity with values from a DTO.
     * This helper method avoids redundant code in the update method.
     * 
     * @param product The product entity to update
     * @param productDTO The DTO containing updated values
     */
    private void updateProductFromDTO(Product product, ProductDTO productDTO) {
        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        
        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }
        
        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }
        
        if (productDTO.getSku() != null) {
            product.setSku(productDTO.getSku());
        }
        
        if (productDTO.getImageUrl() != null) {
            product.setImageUrl(productDTO.getImageUrl());
        }
        
        // Handle category update if categoryId is present
        if (productDTO.getCategoryId() != null) {
            Category category = new Category();
            category.setId(productDTO.getCategoryId());
            product.setCategory(category);
            
            // FIXME: In a real implementation, you should fetch the complete
            // Category entity from the repository using the categoryId
        }
    }
}