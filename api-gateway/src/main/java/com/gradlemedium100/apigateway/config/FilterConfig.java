package com.gradlemedium100.apigateway.config;

import com.gradlemedium100.apigateway.filter.CorrelationIdFilter;
import com.gradlemedium100.apigateway.filter.LoggingFilter;
import com.gradlemedium100.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Configuration class for registering global filters with the API Gateway.
 * This class sets up filters for correlation ID, logging, authentication, and other
 * cross-cutting concerns that should be applied to all routes.
 */
@Configuration
public class FilterConfig {

    private final CorrelationIdFilter correlationIdFilter;
    private final LoggingFilter loggingFilter;
    private final AuthenticationFilter authenticationFilter;

    /**
     * Constructor for FilterConfig
     *
     * @param correlationIdFilter Filter for managing correlation IDs
     * @param loggingFilter Filter for request/response logging
     * @param authenticationFilter Filter for authentication
     */
    @Autowired
    public FilterConfig(
            CorrelationIdFilter correlationIdFilter,
            LoggingFilter loggingFilter,
            AuthenticationFilter authenticationFilter) {
        this.correlationIdFilter = correlationIdFilter;
        this.loggingFilter = loggingFilter;
        this.authenticationFilter = authenticationFilter;
    }

    /**
     * Creates a global filter that applies the correlation ID filter to all routes.
     * This filter ensures every request has a correlation ID for tracing.
     *
     * @return GlobalFilter for correlation ID
     */
    @Bean
    @Order(1)
    public GlobalFilter correlationIdGlobalFilter() {
        return (exchange, chain) -> {
            return correlationIdFilter.getFilter().filter(exchange, chain);
        };
    }

    /**
     * Creates a global filter that applies the logging filter to all routes.
     * This filter logs information about incoming requests and outgoing responses.
     *
     * @return GlobalFilter for logging
     */
    @Bean
    @Order(2)
    public GlobalFilter loggingGlobalFilter() {
        return (exchange, chain) -> {
            return loggingFilter.getFilter().filter(exchange, chain);
        };
    }

    /**
     * Creates a global filter that applies the authentication filter to all routes.
     * This filter verifies authentication tokens on protected endpoints.
     *
     * @return GlobalFilter for authentication
     */
    @Bean
    @Order(3)
    public GlobalFilter authenticationGlobalFilter() {
        return (exchange, chain) -> {
            return authenticationFilter.getFilter().filter(exchange, chain);
        };
    }

    // TODO: Add rate limiting global filter
    // TODO: Add metrics collection global filter
    // TODO: Add request validation global filter
}