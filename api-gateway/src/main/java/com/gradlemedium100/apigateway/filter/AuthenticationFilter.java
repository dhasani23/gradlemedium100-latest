package com.gradlemedium100.apigateway.filter;

import com.gradlemedium100.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;

/**
 * Global filter for handling authentication of incoming requests.
 * This filter checks for valid JWT tokens in request headers and validates them 
 * using the TokenService. It also maintains a list of paths that should bypass
 * authentication.
 */
@Component
public class AuthenticationFilter implements RequestFilter {
    
    private final TokenService tokenService;
    private final List<String> skipAuthPaths;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Constructor that initializes the filter with TokenService and sets up
     * paths that should skip authentication.
     * 
     * @param tokenService Service for validating JWT tokens
     */
    @Autowired
    public AuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
        this.skipAuthPaths = new ArrayList<>();
        
        // Initialize paths that don't require authentication
        skipAuthPaths.add("/api/auth/login");
        skipAuthPaths.add("/api/auth/register");
        skipAuthPaths.add("/api/auth/refresh");
        skipAuthPaths.add("/api/public/**");
        skipAuthPaths.add("/actuator/**");
        skipAuthPaths.add("/v3/api-docs/**");
        skipAuthPaths.add("/swagger-ui/**");
        skipAuthPaths.add("/swagger-resources/**");
    }
    
    /**
     * Returns a filter that verifies authentication token in requests.
     * If the request path should skip authentication or if the token is valid,
     * the request is allowed to proceed. Otherwise, it returns an unauthorized response.
     * 
     * @return GatewayFilter that performs token validation
     */
    @Override
    public GatewayFilter getFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            
            // Skip authentication for certain paths
            if (shouldSkipAuth(path)) {
                return chain.filter(exchange);
            }
            
            // Check for Authorization header
            Optional<String> authHeader = getAuthHeader(request);
            if (!authHeader.isPresent()) {
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }
            
            // Extract and validate token
            String token = extractToken(authHeader.get());
            if (token == null) {
                return onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED);
            }
            
            try {
                // Validate token using TokenService
                if (!tokenService.validateToken(token)) {
                    return onError(exchange, "Invalid authentication token", HttpStatus.UNAUTHORIZED);
                }
                
                // Add user info to header for downstream services
                ServerHttpRequest modifiedRequest = 
                    request.mutate()
                        .header("X-User-ID", tokenService.getUserIdFromToken(token))
                        .header("X-User-Roles", String.join(",", tokenService.getRolesFromToken(token)))
                        .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, "Authentication error: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
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
        return -100; // High priority, should run early in the filter chain
    }
    
    /**
     * Determines if a path should skip authentication.
     * Uses pattern matching to check against the list of paths that don't require authentication.
     * 
     * @param path The request path to check
     * @return true if the path should skip authentication, false otherwise
     */
    public boolean shouldSkipAuth(String path) {
        return skipAuthPaths.stream()
                .anyMatch(pattern -> {
                    if (pattern.endsWith("/**")) {
                        // For wildcard pattern matching (e.g., /api/public/**)
                        String basePattern = pattern.substring(0, pattern.length() - 2);
                        return path.startsWith(basePattern);
                    } else {
                        // For exact path matching
                        return path.equals(pattern);
                    }
                });
    }
    
    /**
     * Helper method to extract Authorization header from request
     * 
     * @param request The HTTP request
     * @return Optional containing the Authorization header value or empty if not present
     */
    private Optional<String> getAuthHeader(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(AUTHORIZATION_HEADER));
    }
    
    /**
     * Helper method to extract token from Authorization header
     * 
     * @param header The Authorization header value
     * @return The extracted token or null if format is invalid
     */
    private String extractToken(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
    
    /**
     * Helper method to return error response
     * 
     * @param exchange The web exchange
     * @param message Error message
     * @param status HTTP status code
     * @return Mono<Void> representing the completion of the response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    /**
     * Adds a path that should skip authentication
     * 
     * @param path The path to add
     */
    public void addSkipAuthPath(String path) {
        // TODO: Add validation before adding paths
        this.skipAuthPaths.add(path);
    }
    
    /**
     * Returns the list of paths that skip authentication
     * 
     * @return Immutable list of paths
     */
    public List<String> getSkipAuthPaths() {
        return new ArrayList<>(skipAuthPaths);
    }
}