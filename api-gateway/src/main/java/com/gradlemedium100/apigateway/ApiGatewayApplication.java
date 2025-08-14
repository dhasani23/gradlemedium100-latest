package com.gradlemedium100.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main Spring Boot application class that bootstraps the API Gateway.
 * 
 * This class serves as the entry point for the API Gateway module which routes
 * requests to appropriate microservices. It configures Spring Cloud Gateway
 * to provide essential API gateway functionalities like:
 * - Request routing
 * - Load balancing
 * - Authentication and authorization
 * - Rate limiting
 * - Circuit breaking
 * - Request/response logging
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    /**
     * Main entry point of the application that starts the Spring Boot API Gateway.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Launch the Spring Boot application
        SpringApplication.run(ApiGatewayApplication.class, args);
        
        // TODO: Add startup logging to track gateway initialization
        // TODO: Implement graceful shutdown hooks
        
        // FIXME: Proper error handling for service registry connection failures
    }
}