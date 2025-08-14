package com.gradlemedium100.dataaccess.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Configuration class for Flyway database migration settings.
 * This class is responsible for configuring and initializing Flyway
 * for database schema migrations.
 * 
 * @author gradlemedium100
 * @version 1.0
 */
@Configuration
public class FlywayMigrationConfig {

    /**
     * Datasource used by Flyway for running database migrations
     */
    private final DataSource dataSource;

    /**
     * Constructor to inject required dependencies.
     *
     * @param dataSource The datasource to be used for database migrations
     */
    @Autowired
    public FlywayMigrationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Configures and initializes a Flyway instance for database migrations.
     * 
     * This method sets up Flyway with the appropriate datasource and additional
     * configuration parameters like validation on migration, baseline on migration,
     * and locations for migration scripts.
     *
     * @return A configured Flyway instance
     */
    @Bean
    public Flyway configureFlyway() {
        // Create Flyway instance with the injected data source
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .validateOnMigrate(true)
                .baselineOnMigrate(true)
                .locations("classpath:db/migration")
                .load();
        
        // TODO: Add support for environment-specific migration paths
        // FIXME: Handle migration failures gracefully with proper error reporting

        return flyway;
    }

    /**
     * Executes database migrations using the configured Flyway instance.
     * 
     * This method triggers the actual execution of database migrations, ensuring
     * that the database schema is up to date with the latest migration scripts.
     * It depends on the Flyway bean being initialized first.
     */
    @Bean
    @DependsOn("configureFlyway")
    public void migrateDatabase() {
        // Get the configured Flyway instance and execute migrations
        Flyway flyway = configureFlyway();
        
        try {
            // Execute database migrations
            MigrateResult migrateResult = flyway.migrate();
            
            // Log the number of migrations applied
            System.out.println("Applied " + migrateResult.migrationsExecuted + " database migrations");
        } catch (Exception e) {
            // TODO: Implement proper logging framework instead of System.out
            System.err.println("Error executing Flyway migrations: " + e.getMessage());
            
            // FIXME: Determine if application should fail fast or continue despite migration errors
            // Depending on production requirements, we might want to throw a runtime exception here
        }
    }
}