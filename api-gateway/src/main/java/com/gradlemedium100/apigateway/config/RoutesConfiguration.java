package com.gradlemedium100.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;

import java.time.Duration;

/**
 * Configuration class defining the routing rules for different service endpoints.
 * This class establishes routes to various microservices and applies appropriate
 * filters and predicates for request routing.
 */
@Configuration
public class RoutesConfiguration {

    /**
     * URL for the product service
     */
    @Value("${service.product.url:lb://product-service}")
    private String productServiceUrl;
    
    /**
     * URL for the order service
     */
    @Value("${service.order.url:lb://order-service}")
    private String orderServiceUrl;
    
    /**
     * URL for the payment service
     */
    @Value("${service.payment.url:lb://payment-service}")
    private String paymentServiceUrl;
    
    /**
     * URL for the notification service
     */
    @Value("${service.notification.url:lb://notification-service}")
    private String notificationServiceUrl;

    /**
     * Configures routes for product service
     *
     * @param builder The RouteLocatorBuilder to build routes
     * @return RouteLocator containing product service routes
     */
    @Bean
    public RouteLocator productServiceRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-get-route", r -> r
                .path("/api/products/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .rewritePath("/api/products/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "product-service")
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setMethods(HttpMethod.GET)
                        .setBackoff(Duration.ofMillis(10), Duration.ofMillis(500), 2, true))
                )
                .uri(productServiceUrl))
            .route("product-modify-route", r -> r
                .path("/api/products/**")
                .and()
                .method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                .filters(f -> f
                    .rewritePath("/api/products/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "product-service")
                )
                .uri(productServiceUrl))
            .build();
    }

    /**
     * Configures routes for order service
     *
     * @param builder The RouteLocatorBuilder to build routes
     * @return RouteLocator containing order service routes
     */
    @Bean
    public RouteLocator orderServiceRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order-get-route", r -> r
                .path("/api/orders/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .rewritePath("/api/orders/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "order-service")
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setMethods(HttpMethod.GET)
                        .setBackoff(Duration.ofMillis(10), Duration.ofMillis(500), 2, true))
                )
                .uri(orderServiceUrl))
            .route("order-create-route", r -> r
                .path("/api/orders/**")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f
                    .rewritePath("/api/orders/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "order-service")
                    // Add circuit breaker for order creation
                    .circuitBreaker(config -> config
                        .setName("orderServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/orders"))
                )
                .uri(orderServiceUrl))
            .route("order-modify-route", r -> r
                .path("/api/orders/**")
                .and()
                .method(HttpMethod.PUT, HttpMethod.DELETE)
                .filters(f -> f
                    .rewritePath("/api/orders/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "order-service")
                )
                .uri(orderServiceUrl))
            .build();
    }

    /**
     * Configures routes for payment service
     *
     * @param builder The RouteLocatorBuilder to build routes
     * @return RouteLocator containing payment service routes
     */
    @Bean
    public RouteLocator paymentServiceRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("payment-get-route", r -> r
                .path("/api/payments/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .rewritePath("/api/payments/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "payment-service")
                    .retry(retryConfig -> retryConfig
                        .setRetries(2)
                        .setMethods(HttpMethod.GET)
                        .setBackoff(Duration.ofMillis(50), Duration.ofMillis(500), 2, true))
                )
                .uri(paymentServiceUrl))
            .route("payment-process-route", r -> r
                .path("/api/payments/**")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f
                    .rewritePath("/api/payments/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "payment-service")
                    // Remove the rate limiter temporarily until we fix the implementation
                    /*
                    .requestRateLimiter(config -> config
                        .setRateLimiter(c -> c.getBean("paymentRateLimiter"))
                        .setKeyResolver(c -> c.getBean("ipKeyResolver"))
                        .setDenyEmptyKey(true))
                    */
                    // Add circuit breaker for payment processing
                    .circuitBreaker(config -> config
                        .setName("paymentServiceCircuitBreaker")
                        .setFallbackUri("forward:/fallback/payments"))
                )
                .uri(paymentServiceUrl))
            .route("payment-update-route", r -> r
                .path("/api/payments/**")
                .and()
                .method(HttpMethod.PUT)
                .filters(f -> f
                    .rewritePath("/api/payments/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "payment-service")
                )
                .uri(paymentServiceUrl))
            .build();
    }

    /**
     * Configures routes for notification service
     *
     * @param builder The RouteLocatorBuilder to build routes
     * @return RouteLocator containing notification service routes
     */
    @Bean
    public RouteLocator notificationServiceRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("notification-get-route", r -> r
                .path("/api/notifications/**")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .rewritePath("/api/notifications/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "notification-service")
                )
                .uri(notificationServiceUrl))
            .route("notification-send-route", r -> r
                .path("/api/notifications/**")
                .and()
                .method(HttpMethod.POST)
                .filters(f -> f
                    .rewritePath("/api/notifications/(?<segment>.*)", "/${segment}")
                    .addRequestHeader("X-Service", "notification-service")
                    // Remove the rate limiter temporarily until we fix the implementation
                    /*
                    .requestRateLimiter(config -> config
                        .setRateLimiter(c -> c.getBean("notificationRateLimiter"))
                        .setKeyResolver(c -> c.getBean("userKeyResolver"))
                        .setDenyEmptyKey(true))
                    */
                    // Add retry with backoff for notification sending
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setMethods(HttpMethod.POST)
                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                )
                .uri(notificationServiceUrl))
            .route("notification-status-route", r -> r
                .path("/api/notifications/status/**")
                .filters(f -> f
                    .rewritePath("/api/notifications/status/(?<segment>.*)", "/status/${segment}")
                    .addRequestHeader("X-Service", "notification-service")
                )
                .uri(notificationServiceUrl))
            .build();
    }
}