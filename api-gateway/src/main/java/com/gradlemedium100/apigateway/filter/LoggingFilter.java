package com.gradlemedium100.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Global filter for logging request and response details.
 * This filter logs information about incoming requests and outgoing responses
 * to help with debugging and monitoring API traffic.
 */
@Component
public class LoggingFilter implements RequestFilter {
    // Logger for recording request and response details
    private final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    
    // Flag to determine if headers should be included in logs
    @Value("${logging.include-headers:false}")
    private boolean includeHeaders;
    
    // Flag to determine if payload should be included in logs
    @Value("${logging.include-payload:false}")
    private boolean includePayload;
    
    /**
     * Returns a filter that logs request and response details.
     * The filter intercepts both the request and response to log
     * relevant information according to configuration settings.
     *
     * @return A GatewayFilter that logs request and response details
     */
    @Override
    public GatewayFilter getFilter() {
        return (exchange, chain) -> {
            // Log request details
            logRequest(exchange);
            
            // Capture the start time for request processing
            long startTime = System.currentTimeMillis();
            
            // Continue the filter chain and log response when complete
            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Calculate the request processing time
                    long executionTime = System.currentTimeMillis() - startTime;
                    logger.info("Request processed in {} ms", executionTime);
                    
                    // Log response details
                    logResponse(exchange);
                }));
        };
    }
    
    /**
     * Returns the order of this filter in the filter chain.
     * A lower value means higher precedence. This filter should run early
     * to ensure all requests and responses are logged properly.
     *
     * @return The order value of this filter
     */
    @Override
    public int getOrder() {
        // Set a high precedence (low number) to ensure logging happens early
        return 0;
    }
    
    /**
     * Logs details of the incoming request.
     * The level of detail depends on the configuration flags.
     *
     * @param exchange The server web exchange containing request information
     */
    public void logRequest(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        
        logger.info("Request: {} {}", method, path);
        
        // Log request headers if enabled
        if (includeHeaders) {
            HttpHeaders headers = request.getHeaders();
            Map<String, Object> headerMap = new LinkedHashMap<>();
            
            headers.forEach((name, values) -> {
                // Avoid logging sensitive headers like Authorization in detail
                if (name.equalsIgnoreCase("Authorization") || name.equalsIgnoreCase("Cookie")) {
                    headerMap.put(name, "[SENSITIVE]");
                } else {
                    headerMap.put(name, values);
                }
            });
            
            logger.debug("Request Headers: {}", headerMap);
        }
        
        // TODO: Implement payload logging for requests when includePayload is true
        // FIXME: Reading the request body can be tricky as it's a flux that can only be consumed once
        if (includePayload) {
            logger.debug("Request payload logging enabled but not implemented yet");
        }
    }
    
    /**
     * Logs details of the outgoing response.
     * The level of detail depends on the configuration flags.
     *
     * @param exchange The server web exchange containing response information
     */
    public void logResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;
        
        logger.info("Response Status: {}", statusCode);
        
        // Log response headers if enabled
        if (includeHeaders) {
            HttpHeaders headers = response.getHeaders();
            Map<String, List<String>> headerMap = new LinkedHashMap<>(headers);
            logger.debug("Response Headers: {}", headerMap);
        }
        
        // TODO: Implement response body logging when includePayload is true
        // This would require response body modification, which needs careful implementation
        if (includePayload) {
            // This is a placeholder for future implementation
            logger.debug("Response payload logging enabled but not implemented yet");
        }
    }
    
    /**
     * Helper method to extract body content from a DataBuffer
     * Note: This is a utility method that might be used for payload logging
     * in a future implementation.
     *
     * @param dataBuffer The data buffer to extract content from
     * @return The content as a string
     */
    private String extractBody(DataBuffer dataBuffer) {
        byte[] content = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(content);
        return new String(content, StandardCharsets.UTF_8);
    }
}