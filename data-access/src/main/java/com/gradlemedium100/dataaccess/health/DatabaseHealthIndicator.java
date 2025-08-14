package com.gradlemedium100.dataaccess.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Health indicator for database connection status monitoring.
 * This component is responsible for checking the health of the database connection
 * and providing status information to the Spring Boot Actuator health endpoint.
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    private static final int CONNECTION_TIMEOUT_SECONDS = 3;

    private final DataSource dataSource;

    /**
     * Constructor that takes a DataSource to monitor.
     * 
     * @param dataSource the database data source to be monitored
     */
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Implements the health check method from the HealthIndicator interface.
     * This method will be called by Spring Boot Actuator to determine the database health.
     * 
     * @return Health object containing the status and details of the database connection
     */
    @Override
    public Health health() {
        // Start building the health response
        Health.Builder builder = new Health.Builder();

        try {
            if (checkConnection()) {
                // Database connection is successful
                return builder
                        .up()
                        .withDetail("status", "Database is accessible")
                        .build();
            } else {
                // Database connection failed
                return builder
                        .down()
                        .withDetail("status", "Database is not accessible")
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error during database health check", e);
            
            // Any exceptions indicate the database is not healthy
            return builder
                    .down()
                    .withDetail("status", "Database health check failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    /**
     * Check if the database connection is valid.
     * Attempts to establish a connection to the database and verify its validity.
     * 
     * @return true if connection is valid, false otherwise
     */
    protected boolean checkConnection() {
        Connection connection = null;
        try {
            // Get a connection from the data source
            connection = dataSource.getConnection();
            
            // Check if the connection is valid
            if (connection == null) {
                logger.warn("Failed to obtain database connection");
                return false;
            }
            
            // FIXME: Connection timeout might need adjustment based on environment
            boolean isValid = connection.isValid(CONNECTION_TIMEOUT_SECONDS);
            
            if (!isValid) {
                logger.warn("Database connection is not valid");
            }
            
            return isValid;
        } catch (SQLException e) {
            logger.error("Error checking database connection", e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // TODO: Consider implementing a connection leak detection mechanism
                    logger.warn("Failed to close database connection", e);
                }
            }
        }
    }
}