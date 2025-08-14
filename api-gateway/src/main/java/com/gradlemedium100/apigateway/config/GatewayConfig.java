package com.gradlemedium100.apigateway.config;

import java.util.Arrays;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for setting up API gateway routes and global filters.
 * This class configures the API Gateway routing and cross-origin resource sharing (CORS) settings.
 */
@Configuration
public class GatewayConfig {

    /**
     * Defines the route configurations for the API gateway.
     * This method sets up the primary routes for the gateway, potentially applying
     * filters and predicates to each route.
     *
     * @param builder The RouteLocatorBuilder to create routes
     * @return A RouteLocator with configured routes
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Product service routes
            .route("product-service", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .rewritePath("/api/products/(?<segment>.*)", "/api/v1/products/${segment}")
                    .circuitBreaker(config -> config
                        .setName("productServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/products")))
                .uri("lb://PRODUCT-SERVICE"))
            
            // Order service routes
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .rewritePath("/api/orders/(?<segment>.*)", "/api/v1/orders/${segment}")
                    .circuitBreaker(config -> config
                        .setName("orderServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/orders")))
                .uri("lb://ORDER-SERVICE"))
            
            // Payment service routes
            .route("payment-service", r -> r
                .path("/api/payments/**")
                .filters(f -> f
                    .rewritePath("/api/payments/(?<segment>.*)", "/api/v1/payments/${segment}")
                    .circuitBreaker(config -> config
                        .setName("paymentServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/payments")))
                .uri("lb://PAYMENT-SERVICE"))
            
            // Notification service routes
            .route("notification-service", r -> r
                .path("/api/notifications/**")
                .filters(f -> f
                    .rewritePath("/api/notifications/(?<segment>.*)", "/api/v1/notifications/${segment}")
                    .circuitBreaker(config -> config
                        .setName("notificationServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/notifications")))
                .uri("lb://NOTIFICATION-SERVICE"))
                
            // Authentication routes
            .route("auth-service", r -> r
                .path("/api/auth/**")
                .filters(f -> f
                    .rewritePath("/api/auth/(?<segment>.*)", "/api/v1/auth/${segment}"))
                .uri("lb://SECURITY-SERVICE"))
                
            .build();
    }

    /**
     * Configures CORS settings for the API gateway.
     * This allows cross-origin requests to be handled properly by the gateway.
     *
     * @return A CorsWebFilter with configured CORS settings
     */
    @Bean
    public CorsWebFilter corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*")); // FIXME: Tighten security in production
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Correlation-ID"));
        corsConfig.setExposedHeaders(Arrays.asList("X-Correlation-ID"));
        corsConfig.setMaxAge(3600L); // 1 hour
        corsConfig.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }

    // TODO: Add additional configuration for rate limiting integration
    // TODO: Add integration with API monitoring and metrics collection
    // TODO: Configure gateway timeouts for different services based on their SLAs
}