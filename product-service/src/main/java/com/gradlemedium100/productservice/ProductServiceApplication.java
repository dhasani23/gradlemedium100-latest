package com.gradlemedium100.productservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.gradlemedium100.productservice.model.Category;
import com.gradlemedium100.productservice.model.Product;
import com.gradlemedium100.productservice.model.Inventory;
import com.gradlemedium100.productservice.repository.CategoryRepository;
import com.gradlemedium100.productservice.repository.ProductRepository;
import com.gradlemedium100.productservice.repository.InventoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Main application class that bootstraps the product service module.
 * This service handles product management, including the product catalog,
 * categories, and inventory tracking.
 *
 * @author GradleMedium100 Development Team
 */
@SpringBootApplication
@EnableCaching
public class ProductServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceApplication.class);

    /**
     * Entry point for the product service application.
     * Launches the Spring Boot application context.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        logger.info("Starting ProductServiceApplication...");
        
        try {
            SpringApplication.run(ProductServiceApplication.class, args);
            logger.info("ProductServiceApplication started successfully");
        } catch (Exception e) {
            logger.error("Failed to start ProductServiceApplication", e);
            // FIXME: Implement proper exception handling and recovery mechanism
            System.exit(1);
        }
    }

    /**
     * Initializes sample data for development environment.
     * This bean creates initial product categories, products, and inventory records
     * to allow for immediate testing and demonstration.
     *
     * @param categoryRepository Repository for category data access
     * @param productRepository Repository for product data access
     * @param inventoryRepository Repository for inventory data access
     * @return A CommandLineRunner that executes the sample data initialization
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner commandLineRunner(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository) {
        
        return args -> {
            logger.info("Initializing sample data for development environment");
            
            // Create sample categories
            List<Category> categories = createSampleCategories(categoryRepository);
            
            // Create sample products
            List<Product> products = createSampleProducts(productRepository, categories);
            
            // Create sample inventory records
            createSampleInventory(inventoryRepository, products);
            
            logger.info("Sample data initialization completed successfully");
            
            // TODO: Add more sophisticated sample data based on actual business requirements
            // TODO: Add data validation before persisting sample data
        };
    }
    
    /**
     * Creates sample product categories.
     * 
     * @param categoryRepository Repository for category persistence
     * @return List of created categories
     */
    private List<Category> createSampleCategories(CategoryRepository categoryRepository) {
        // Delete existing categories if any
        categoryRepository.deleteAll();
        
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic devices and accessories");
        
        Category clothing = new Category();
        clothing.setName("Clothing");
        clothing.setDescription("Apparel and fashion items");
        
        Category books = new Category();
        books.setName("Books");
        books.setDescription("Books and publications");
        
        // Persist categories
        categoryRepository.save(electronics);
        categoryRepository.save(clothing);
        categoryRepository.save(books);
        
        logger.debug("Created {} sample categories", 3);
        
        return Arrays.asList(electronics, clothing, books);
    }
    
    /**
     * Creates sample products.
     * 
     * @param productRepository Repository for product persistence
     * @param categories List of available categories to assign to products
     * @return List of created products
     */
    private List<Product> createSampleProducts(ProductRepository productRepository, List<Category> categories) {
        // Delete existing products if any
        productRepository.deleteAll();
        
        Product laptop = new Product();
        laptop.setName("Laptop Pro X1");
        laptop.setDescription("High-performance laptop with SSD");
        laptop.setPrice(new BigDecimal("1299.99"));
        laptop.setCategory(categories.get(0)); // Electronics
        laptop.setSku("TECH-LAPTOP-001");
        laptop.setCreatedAt(java.time.LocalDateTime.now());
        
        Product tshirt = new Product();
        tshirt.setName("Classic T-Shirt");
        tshirt.setDescription("100% cotton t-shirt");
        tshirt.setPrice(new BigDecimal("24.99"));
        tshirt.setCategory(categories.get(1)); // Clothing
        tshirt.setSku("CLOTH-TSHIRT-001");
        tshirt.setCreatedAt(java.time.LocalDateTime.now());
        
        Product novel = new Product();
        novel.setName("The Great Adventure");
        novel.setDescription("Bestselling novel");
        novel.setPrice(new BigDecimal("19.99"));
        novel.setCategory(categories.get(2)); // Books
        novel.setSku("BOOK-NOVEL-001");
        novel.setCreatedAt(java.time.LocalDateTime.now());
        
        // Persist products
        productRepository.save(laptop);
        productRepository.save(tshirt);
        productRepository.save(novel);
        
        logger.debug("Created {} sample products", 3);
        
        return Arrays.asList(laptop, tshirt, novel);
    }
    
    /**
     * Creates sample inventory records.
     * 
     * @param inventoryRepository Repository for inventory persistence
     * @param products List of products for which to create inventory
     */
    private void createSampleInventory(InventoryRepository inventoryRepository, List<Product> products) {
        // Delete existing inventory if any
        inventoryRepository.deleteAll();
        
        for (int i = 0; i < products.size(); i++) {
            Inventory inventory = new Inventory();
            inventory.setProduct(products.get(i));
            inventory.setQuantity(100 + (i * 50)); // Different quantities for each product
            inventory.setReserved(0);
            inventory.setLastUpdated(new Date());
            
            inventoryRepository.save(inventory);
        }
        
        logger.debug("Created {} inventory records", products.size());
    }
}