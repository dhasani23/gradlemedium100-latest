package com.gradlemedium100.productservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.gradlemedium100.productservice.validation.ProductValidationService;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for the product service module.
 * This class sets up all necessary beans and configurations
 * for the product service including caching, REST clients, and validation.
 */
@Configuration
@EnableCaching
public class ProductServiceConfig {
    
    /**
     * Cache expiration time in seconds for product data
     */
    @Value("${product.cache.expiration:3600}")
    private long cacheExpirationSeconds;
    
    /**
     * Maximum number of entries in the product cache
     */
    @Value("${product.cache.max-size:1000}")
    private int cacheMaxSize;
    
    /**
     * Creates and configures a cache manager for product data.
     * This custom cache manager is used for application-specific caching needs.
     * 
     * @return A configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        // Create a Caffeine cache manager with the specified settings
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure the cache builder with expiration and size limits
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheExpirationSeconds, TimeUnit.SECONDS)
                .maximumSize(cacheMaxSize)
                .recordStats());
        
        // Pre-define cache names used in the application
        cacheManager.setCacheNames(Arrays.asList("products", "categories", "inventories"));
        
        return cacheManager;
    }
    
    /**
     * Configures the Spring cache manager for products.
     * This is a more specific configuration for product-related caching.
     * 
     * @return The configured Spring cache manager
     */
    @Bean
    public CacheManager productCacheConfig() {
        CaffeineCacheManager productCacheManager = new CaffeineCacheManager("products");
        
        // Product caching might need different parameters than the default
        productCacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheExpirationSeconds, TimeUnit.SECONDS)
                .maximumSize(cacheMaxSize)
                .weakKeys()  // Allow keys to be garbage collected if needed
                .recordStats());
                
        return productCacheManager;
    }
    
    /**
     * Creates a REST client for external product APIs.
     * This REST template is configured with appropriate timeouts and error handling.
     * 
     * @return A configured RestTemplate
     */
    @Bean
    public RestTemplate productApiClient() {
        RestTemplate restTemplate = new RestTemplate();
        
        // TODO: Configure connection and read timeouts for the REST template
        // TODO: Add error handlers specific to product API interactions
        // TODO: Configure retry mechanisms for transient failures
        
        return restTemplate;
    }
    
    /**
     * Creates a validation service for product data.
     * This service ensures product data meets business requirements before processing.
     * 
     * @return A configured ProductValidationService
     */
    @Bean
    public ProductValidationService productValidationService() {
        ProductValidationService validationService = new ProductValidationService();
        
        // FIXME: Currently hardcoded validation rules - should be configurable
        validationService.setMinimumNameLength(3);
        validationService.setMaximumNameLength(100);
        validationService.setMinimumPrice(0.01);
        
        return validationService;
    }
    
    /**
     * Creates a custom cache manager wrapper for our application.
     * 
     * @param springCacheManager The Spring cache manager to wrap
     * @return Custom cache manager wrapper
     */
    @Bean
    public com.gradlemedium100.common.util.CacheManager customCacheManager(CacheManager springCacheManager) {
        // Using fully qualified name to avoid import conflict
        return new com.gradlemedium100.common.util.CacheManager(springCacheManager);
    }
}