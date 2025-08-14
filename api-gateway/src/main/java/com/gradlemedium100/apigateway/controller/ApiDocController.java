package com.gradlemedium100.apigateway.controller;

import com.gradlemedium100.apigateway.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for serving API documentation.
 * This controller provides endpoints for retrieving Swagger UI configuration and resources
 * for all microservices in the system.
 * 
 * The documentation is aggregated from all services registered in the serviceDefinitions list.
 */
@RestController
@RequestMapping("/api-docs")
public class ApiDocController {

    /**
     * List of service definitions for API documentation
     */
    private final List<ServiceDefinition> serviceDefinitions;
    
    @Value("${springfox.documentation.swagger.v2.host:localhost:8080}")
    private String hostUrl;

    /**
     * Constructor that initializes the service definitions list
     */
    @Autowired
    public ApiDocController() {
        // Initialize service definitions
        this.serviceDefinitions = new ArrayList<>();
        
        // TODO: Move service definitions to configuration file for easier maintenance
        
        // Add service definitions for all microservices
        serviceDefinitions.add(new ServiceDefinition("product-service", "/product-service/v2/api-docs", "v1", "Product Management Service"));
        serviceDefinitions.add(new ServiceDefinition("order-service", "/order-service/v2/api-docs", "v1", "Order Management Service"));
        serviceDefinitions.add(new ServiceDefinition("payment-service", "/payment-service/v2/api-docs", "v1", "Payment Processing Service"));
        serviceDefinitions.add(new ServiceDefinition("notification-service", "/notification-service/v2/api-docs", "v1", "Notification Service"));
    }

    /**
     * Returns a list of Swagger resources for all services
     * 
     * @return List of SwaggerResource objects representing API docs for each service
     */
    @GetMapping("/swagger-resources")
    public List<SwaggerResource> getSwaggerResources() {
        return serviceDefinitions.stream()
                .map(this::createSwaggerResource)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert a ServiceDefinition to a SwaggerResource
     * 
     * @param serviceDefinition the service definition to convert
     * @return SwaggerResource for the service
     */
    private SwaggerResource createSwaggerResource(ServiceDefinition serviceDefinition) {
        SwaggerResource resource = new SwaggerResource();
        resource.setName(serviceDefinition.getName());
        resource.setLocation(serviceDefinition.getUrl());
        resource.setSwaggerVersion(serviceDefinition.getVersion());
        return resource;
    }

    /**
     * Returns configuration for Swagger UI
     * 
     * @return UiConfiguration object with UI settings
     */
    @Bean
    @GetMapping("/ui-configuration")
    public UiConfiguration getSwaggerUiConfig() {
        // Configure Swagger UI with medium complexity settings
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(true)
                .docExpansion(DocExpansion.LIST)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .validatorUrl(null)
                .build();
    }

    /**
     * Returns security configuration for Swagger
     * 
     * @return SecurityConfiguration object with security settings
     */
    @Bean
    @GetMapping("/security-configuration")
    public SecurityConfiguration getSwaggerSecurityConfiguration() {
        // FIXME: Enhance security configuration with proper authentication mechanisms
        // Current implementation provides basic security setup
        return SecurityConfigurationBuilder.builder()
                .clientId("swagger-ui")
                .clientSecret("swagger-ui-secret")
                .realm("api-gateway-realm")
                .appName("API Gateway")
                .scopeSeparator(",")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();
    }
    
    /**
     * Adds a new service definition to the list of available services
     * 
     * @param serviceDefinition the service definition to add
     */
    public void addServiceDefinition(ServiceDefinition serviceDefinition) {
        // TODO: Implement validation to prevent duplicate services
        this.serviceDefinitions.add(serviceDefinition);
    }
    
    /**
     * Removes a service definition from the list by name
     * 
     * @param serviceName name of the service to remove
     * @return true if service was found and removed, false otherwise
     */
    public boolean removeServiceDefinition(String serviceName) {
        return this.serviceDefinitions.removeIf(service -> 
            service.getName().equalsIgnoreCase(serviceName));
    }
}