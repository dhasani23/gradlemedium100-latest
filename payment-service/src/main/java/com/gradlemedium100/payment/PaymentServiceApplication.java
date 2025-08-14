package com.gradlemedium100.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the Payment Service module.
 * 
 * This class bootstraps the Spring application context and starts the embedded web server.
 * The Payment Service handles payment processing, integrates with payment gateways,
 * and manages payment-related operations like transactions and refunds.
 * 
 * @author gradlemedium100-team
 */
@SpringBootApplication
public class PaymentServiceApplication {

    /**
     * Main method that launches the Spring Boot application.
     * 
     * This method initializes the Spring context, auto-configures the application based
     * on classpath settings, and starts the embedded web server to serve payment-related
     * REST endpoints.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Initialize and run the Spring Boot application
        SpringApplication.run(PaymentServiceApplication.class, args);
        
        // TODO: Add startup logging for payment service initialization
        // TODO: Add health check integration for payment gateway services
        
        // FIXME: Handle application shutdown gracefully to ensure in-flight payments are not interrupted
    }
}