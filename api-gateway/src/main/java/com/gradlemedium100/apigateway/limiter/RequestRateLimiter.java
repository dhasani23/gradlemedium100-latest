package com.gradlemedium100.apigateway.limiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;

/**
 * Implementation of rate limiting functionality for API requests.
 * <p>
 * This class provides Redis-based rate limiting for API requests to prevent abuse
 * and ensure fair usage of the API. It uses a token bucket algorithm to control
 * the rate of requests based on configured limits.
 * </p>
 */
@Service
public class RequestRateLimiter implements RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimiter.class);
    private static final String RATE_LIMIT_EXCEEDED_MESSAGE = "{\"message\": \"Rate limit exceeded. Please try again later.\"}";
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_USER_ID = "X-User-Id";

    private final RedisRateLimiter redisRateLimiter;
    private final int requestsPerSecond;
    private final int burstCapacity;

    /**
     * Constructs a new RequestRateLimiter with Redis implementation.
     *
     * @param redisRateLimiter   The Redis-based rate limiter implementation
     * @param requestsPerSecond  Number of requests allowed per second
     * @param burstCapacity      Maximum burst capacity for requests
     */
    @Autowired
    public RequestRateLimiter(
            RedisRateLimiter redisRateLimiter,
            @Value("${rate.limit.requests-per-second:10}") int requestsPerSecond,
            @Value("${rate.limit.burst-capacity:20}") int burstCapacity) {
        this.redisRateLimiter = redisRateLimiter;
        this.requestsPerSecond = requestsPerSecond;
        this.burstCapacity = burstCapacity;
        
        logger.info("Initialized RequestRateLimiter with {} requests per second and burst capacity of {}", 
                requestsPerSecond, burstCapacity);
    }

    /**
     * Checks if a request is allowed based on rate limiting configuration.
     * <p>
     * This method uses the Redis rate limiter to determine if the request should be allowed
     * based on the configured rate limits.
     * </p>
     *
     * @param id The identifier used for rate limiting (e.g., API key, IP address, user ID)
     * @return A Mono that emits a Boolean value indicating whether the request is allowed (true) or not (false)
     */
    @Override
    public Mono<Boolean> isAllowed(String id) {
        if (id == null || id.isEmpty()) {
            logger.warn("Rate limiting skipped due to empty identifier");
            return Mono.just(true); // Default to allowing if we can't identify the source
        }

        // Apply the token bucket algorithm using Redis
        AtomicBoolean allowed = new AtomicBoolean(false);
        
        // FIXME: The token retrieval logic needs optimization for high traffic scenarios
        return redisRateLimiter.isAllowed("api-gateway", id)
                .map(response -> {
                    // Get "X-RateLimit-Remaining" from headers safely
                    Map<String, String> headers = response.getHeaders();
                    int remaining = 0;
                    
                    if (headers != null && headers.containsKey("X-RateLimit-Remaining")) {
                        try {
                            remaining = Integer.parseInt(headers.get("X-RateLimit-Remaining"));
                        } catch (NumberFormatException e) {
                            logger.warn("Failed to parse X-RateLimit-Remaining: {}", headers.get("X-RateLimit-Remaining"));
                        }
                    }
                    
                    boolean isAllowed = response.isAllowed();
                    allowed.set(isAllowed);
                    
                    if (!isAllowed) {
                        logger.warn("Rate limit exceeded for {}", id);
                    } else if (remaining < (burstCapacity * 0.2)) { // 20% capacity remaining
                        logger.info("Rate limit near capacity for {}: {} remaining", id, remaining);
                    }
                    
                    return isAllowed;
                })
                .onErrorResume(e -> {
                    logger.error("Error checking rate limit for {}: {}", id, e.getMessage(), e);
                    // TODO: Implement fallback strategy for when Redis is unavailable
                    return Mono.just(true); // Fail open to prevent blocking legitimate traffic
                });
    }

    /**
     * Generates a key for rate limiting based on IP address or user ID.
     * <p>
     * This method extracts the client IP address or user ID from the request
     * to use as a key for rate limiting.
     * </p>
     *
     * @param exchange The server web exchange containing the request details
     * @return A String representing the key for rate limiting
     */
    @Override
    public String getKey(ServerWebExchange exchange) {
        // Try to get user ID from headers
        String userId = exchange.getRequest().getHeaders().getFirst(X_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }
        
        // Fall back to IP address
        String clientIp = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(X_FORWARDED_FOR))
                .orElse(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
                
        // Remove port information if present
        if (clientIp.contains(":")) {
            clientIp = clientIp.split(":")[0];
        }
        
        // TODO: Consider using more sophisticated IP handling for IPv6 addresses
        return "ip:" + clientIp;
    }

    /**
     * Creates a response for when rate limit is exceeded.
     * <p>
     * This method builds an appropriate HTTP response to be sent back to clients
     * when they exceed their rate limits.
     * </p>
     *
     * @return A Mono that emits a ServerResponse with appropriate status and body
     */
    public Mono<ServerResponse> getRateLimitResponse() {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "10") // Suggest client to retry after 10 seconds
                .header("X-RateLimit-Limit", String.valueOf(requestsPerSecond))
                .header("Content-Type", "application/json")
                .bodyValue(RATE_LIMIT_EXCEEDED_MESSAGE);
    }
    
    /**
     * Gets the configured requests per second limit.
     *
     * @return The number of requests allowed per second
     */
    public int getRequestsPerSecond() {
        return requestsPerSecond;
    }

    /**
     * Gets the configured burst capacity.
     *
     * @return The maximum burst capacity for requests
     */
    public int getBurstCapacity() {
        return burstCapacity;
    }
}