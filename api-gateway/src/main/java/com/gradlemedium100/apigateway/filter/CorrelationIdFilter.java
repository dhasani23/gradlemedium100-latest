package com.gradlemedium100.apigateway.filter;

import com.gradlemedium100.common.util.CorrelationIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filter for adding correlation IDs to track requests across microservices.
 * This filter ensures that every request passing through the API gateway has 
 * a correlation ID, either using an existing one from the incoming request 
 * or generating a new one. The correlation ID is then propagated to downstream 
 * services via HTTP headers.
 */
@Component
public class CorrelationIdFilter implements RequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    
    /**
     * Default priority for the correlation ID filter.
     * This should be a high priority (low number) to ensure the correlation ID
     * is available for other filters that may need it for logging or other purposes.
     */
    private static final int FILTER_ORDER = 1;
    
    /**
     * Utility for generating correlation IDs
     */
    private final CorrelationIdGenerator correlationIdGenerator;
    
    /**
     * Name of the header for correlation ID
     */
    private final String correlationIdHeaderName;
    
    /**
     * Constructor for CorrelationIdFilter
     *
     * @param correlationIdGenerator Utility for generating correlation IDs
     * @param correlationIdHeaderName Name of the header for correlation ID (injected from configuration)
     */
    @Autowired
    public CorrelationIdFilter(
            CorrelationIdGenerator correlationIdGenerator,
            @Value("${api.correlation-id.header-name:X-Correlation-ID}") String correlationIdHeaderName) {
        this.correlationIdGenerator = correlationIdGenerator;
        this.correlationIdHeaderName = correlationIdHeaderName;
    }
    
    /**
     * Returns a filter that adds correlation ID to requests.
     * If the incoming request already has a correlation ID header, it will be used;
     * otherwise, a new correlation ID will be generated and added.
     *
     * @return The GatewayFilter implementation
     */
    @Override
    public GatewayFilter getFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Check if request already has a correlation ID
            if (!request.getHeaders().containsKey(correlationIdHeaderName)) {
                // Generate and add correlation ID to request
                String correlationId = generateCorrelationId();
                logger.debug("Generated new correlation ID: {}", correlationId);
                
                // Add correlation ID to the current request
                addCorrelationId(exchange, correlationId);
            } else {
                String existingCorrelationId = request.getHeaders().getFirst(correlationIdHeaderName);
                logger.debug("Using existing correlation ID: {}", existingCorrelationId);
                
                // Validate the existing correlation ID
                if (!correlationIdGenerator.isValidCorrelationId(existingCorrelationId)) {
                    // If invalid, replace with a new one
                    String newCorrelationId = generateCorrelationId();
                    logger.debug("Replacing invalid correlation ID with new one: {}", newCorrelationId);
                    addCorrelationId(exchange, newCorrelationId);
                }
            }
            
            // Continue with the filter chain
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Log completion with correlation ID
                    String correlationId = exchange.getRequest().getHeaders().getFirst(correlationIdHeaderName);
                    logger.debug("Completed request with correlation ID: {}", correlationId);
                }));
        };
    }
    
    /**
     * Returns the order of this filter in the filter chain.
     * Lower values have higher priority.
     *
     * @return The order value for this filter
     */
    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }
    
    /**
     * Generates a unique correlation ID for tracking.
     *
     * @return A unique correlation ID string
     */
    protected String generateCorrelationId() {
        return correlationIdGenerator.generateCorrelationId();
    }
    
    /**
     * Adds correlation ID to the request and response headers.
     * This method ensures that both the incoming request and outgoing response
     * have the correlation ID header set.
     *
     * @param exchange The ServerWebExchange object containing request and response
     * @param correlationId The correlation ID to add
     */
    protected void addCorrelationId(ServerWebExchange exchange, String correlationId) {
        // Add to request headers
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header(correlationIdHeaderName, correlationId)
            .build();
        
        // Add to response headers
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(correlationIdHeaderName, correlationId);
        
        // Update the exchange with the mutated request
        exchange.mutate().request(mutatedRequest).build();
    }
    
    /**
     * Adds correlation ID to the request and response headers.
     * Convenience method that generates a new correlation ID.
     *
     * @param exchange The ServerWebExchange object containing request and response
     */
    protected void addCorrelationId(ServerWebExchange exchange) {
        addCorrelationId(exchange, generateCorrelationId());
    }
}