package com.gradlemedium100.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application entry point for the Order Service module.
 * This class initializes the Spring Boot application context and
 * serves as the main entry point for the order service.
 * 
 * @author gradlemedium100
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.gradlemedium100.orderservice"})
@EnableScheduling
public class OrderServiceApplication {

    /**
     * Main entry point for the Order Service application.
     * Launches the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        try {
            // Initialize and start the Spring Boot application
            SpringApplication.run(OrderServiceApplication.class, args);
            System.out.println("Order Service started successfully");
            
            // TODO: Add application startup logging with proper log levels
            // FIXME: Consider using a more robust logging framework instead of System.out
        } catch (Exception e) {
            System.err.println("Failed to start Order Service: " + e.getMessage());
            e.printStackTrace();
            
            // Add additional error handling logic if needed
            // TODO: Implement proper error handling and notification mechanism
        }
    }
}