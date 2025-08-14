package com.gradlemedium100.dataaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * DataAccessApplication
 * 
 * Main configuration class for the DataAccess module. This class is responsible for:
 * 1. Setting up the Spring Boot application context
 * 2. Configuring database connections through auto-configuration
 * 3. Enabling and configuring JPA repositories
 * 
 * This application can be run standalone for development and testing purposes,
 * or included as a module in the main application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.gradlemedium100.dataaccess.repository")
public class DataAccessApplication {

    /**
     * Main entry point for running the DataAccess module as a standalone application.
     * This is particularly useful during development and testing phases.
     * 
     * When running standalone:
     * - In-memory database will be used by default unless configured otherwise
     * - Flyway migrations will be applied automatically
     * - Repository beans will be available in the application context
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Initialize configuration based on application profile
        configureEnvironment();
        
        // Launch the Spring Boot application
        SpringApplication.run(DataAccessApplication.class, args);
        
        // Log successful startup
        logStartupInformation();
    }
    
    /**
     * Configure environment-specific settings before application startup.
     * This allows for profile-specific customization.
     * 
     * TODO: Implement profile-specific configuration if needed
     */
    private static void configureEnvironment() {
        // Set default profile if not specified
        if (System.getProperty("spring.profiles.active") == null) {
            System.setProperty("spring.profiles.active", "development");
        }
        
        // Apply any other necessary system properties
        if (Boolean.parseBoolean(System.getProperty("data-access.debug", "false"))) {
            System.setProperty("logging.level.com.gradlemedium100.dataaccess", "DEBUG");
            System.setProperty("logging.level.org.hibernate.SQL", "DEBUG");
        }
    }
    
    /**
     * Log information about the successful startup of the application.
     * 
     * FIXME: Currently using System.out for logging - should be replaced with a proper logging framework
     */
    private static void logStartupInformation() {
        String activeProfile = System.getProperty("spring.profiles.active", "default");
        System.out.println("DataAccess Module started successfully with profile: " + activeProfile);
        System.out.println("Repository scan path: com.gradlemedium100.dataaccess.repository");
    }
}