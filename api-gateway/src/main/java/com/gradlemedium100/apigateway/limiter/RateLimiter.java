package com.gradlemedium100.apigateway.limiter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Interface for implementing API rate limiting functionality.
 * <p>
 * This interface defines the contract for rate limiting implementations that
 * can be used to prevent API abuse by limiting the number of requests
 * that can be made within a specific time period.
 * </p>
 */
public interface RateLimiter {

    /**
     * Determines if a request is allowed based on rate limiting rules.
     * <p>
     * This method checks whether the request identified by the given id
     * should be allowed to proceed or rejected based on configured rate limits.
     * </p>
     * 
     * @param id The identifier used for rate limiting (e.g., API key, IP address, user ID)
     * @return A Mono that emits a Boolean value indicating whether the request is allowed (true) or not (false)
     */
    Mono<Boolean> isAllowed(String id);

    /**
     * Generates a key for rate limiting based on the request.
     * <p>
     * This method extracts identifying information from the request context
     * to create a key that will be used for rate limiting. The key could be based
     * on various factors such as IP address, user ID, API key, etc.
     * </p>
     * 
     * @param exchange The server web exchange containing the request details
     * @return A String representing the key for rate limiting
     */
    String getKey(ServerWebExchange exchange);
}