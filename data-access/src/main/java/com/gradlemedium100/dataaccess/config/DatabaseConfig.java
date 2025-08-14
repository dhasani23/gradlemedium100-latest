package com.gradlemedium100.dataaccess.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for database connection settings and properties.
 * This class provides configuration for database connectivity using HikariCP
 * connection pool for optimal performance.
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    /**
     * Creates and configures the HikariCP connection pool settings.
     * This method sets up parameters such as connection timeout, minimum idle connections,
     * and validation timeout among other critical settings for optimal database performance.
     *
     * @return HikariConfig object with all database connection parameters configured
     */
    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        
        // Set essential connection properties
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(maxPoolSize);
        
        // Additional connection pool optimization settings
        config.setMinimumIdle(2);
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setValidationTimeout(TimeUnit.SECONDS.toMillis(5));
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
        
        // Set pool name for easier monitoring and debugging
        config.setPoolName("gradlemedium100-db-pool");
        
        // Enable auto-commit for transactions
        config.setAutoCommit(true);
        
        // Connection testing
        config.setConnectionTestQuery("SELECT 1");
        
        // FIXME: Add SSL configuration if required for production environments
        
        // TODO: Consider implementing a metrics tracker for connection pool monitoring
        
        return config;
    }

    /**
     * Creates and configures the database connection pool.
     * This method uses the HikariConfig to initialize a HikariDataSource,
     * which provides high-performance connection pooling.
     *
     * @return DataSource object for database connectivity
     */
    @Bean
    public DataSource dataSource() {
        try {
            return new HikariDataSource(hikariConfig());
        } catch (Exception e) {
            // Log the exception but still throw it to prevent application startup with invalid database config
            // FIXME: Replace with proper logging framework instead of printing to stderr
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Utility method to check database connectivity.
     * Not exposed as a bean, but can be used by health indicators.
     * 
     * @return true if connection test passes, false otherwise
     * @throws Exception if a connection cannot be established
     */
    boolean testDatabaseConnection() throws Exception {
        try (HikariDataSource dataSource = new HikariDataSource(hikariConfig())) {
            // Test if we can get a connection from the pool
            dataSource.getConnection().close();
            return true;
        } catch (Exception e) {
            // TODO: Implement better exception handling and diagnostics
            return false;
        }
    }
}