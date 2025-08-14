package com.gradlemedium100.apigateway.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Global error handler for managing exceptions in the API gateway.
 * This class implements ErrorWebExceptionHandler to catch and handle 
 * all exceptions that occur during request processing in a centralized way.
 */
@Component
@Order(-2) // High precedence to ensure this handler is used before the default handlers
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    
    // Logger for error details
    private final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);
    
    // Jackson object mapper for JSON serialization
    private final ObjectMapper objectMapper;

    /**
     * Constructor that initializes the ObjectMapper.
     * 
     * @param objectMapper Used for JSON serialization of error responses
     */
    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Handles exceptions and returns appropriate error responses.
     * This method intercepts all exceptions thrown during request processing
     * and converts them into standardized error responses.
     * 
     * @param exchange The current server exchange
     * @param ex The exception that was thrown
     * @return A Mono that completes when the error response has been written
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        logger.error("Error during request processing: ", ex);
        
        ServerHttpResponse response = exchange.getResponse();
        
        // Set the response content type to JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // Determine appropriate HTTP status code based on exception type
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        } else if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
        } else {
            // Default to 500 (Internal Server Error) for unhandled exceptions
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        // Generate and write the error response
        return renderErrorResponse(ex)
            .flatMap(serverResponse -> {
                DataBufferFactory bufferFactory = response.bufferFactory();
                try {
                    // Create error payload
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("status", response.getStatusCode().value());
                    errorMap.put("error", response.getStatusCode().getReasonPhrase());
                    errorMap.put("message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred");
                    errorMap.put("timestamp", System.currentTimeMillis());
                    errorMap.put("path", exchange.getRequest().getPath().value());
                    
                    // Convert error payload to JSON and write to response
                    byte[] errorBytes = objectMapper.writeValueAsBytes(errorMap);
                    DataBuffer buffer = bufferFactory.wrap(errorBytes);
                    return response.writeWith(Mono.just(buffer));
                } catch (Exception e) {
                    // If JSON serialization fails, provide a basic error message
                    logger.error("Error writing error response: ", e);
                    String fallbackMessage = "Internal Server Error";
                    DataBuffer buffer = bufferFactory.wrap(fallbackMessage.getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(buffer));
                }
            });
    }

    /**
     * Creates a standardized error response for different types of exceptions.
     * This method determines the appropriate status code and error message
     * based on the type of exception that was thrown.
     * 
     * @param ex The exception to process
     * @return A Mono containing the appropriate ServerResponse
     */
    public Mono<ServerResponse> renderErrorResponse(Throwable ex) {
        // Map different exception types to specific HTTP status codes and messages
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            return createErrorResponse(
                responseStatusException.getStatus(),
                responseStatusException.getMessage()
            );
        } else if (ex instanceof NotFoundException) {
            return createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found: " + ex.getMessage()
            );
        } else if (ex instanceof IllegalArgumentException) {
            return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request parameters: " + ex.getMessage()
            );
        } else if (ex instanceof SecurityException) {
            return createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Security violation: " + ex.getMessage()
            );
        }
        
        // Default case for unhandled exceptions
        logger.error("Unhandled exception type: " + ex.getClass().getName(), ex);
        return createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later."
        );
    }
    
    /**
     * Helper method to create a ServerResponse with the given status and message.
     * 
     * @param status HTTP status code
     * @param message Error message
     * @return Mono containing the server response
     */
    private Mono<ServerResponse> createErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
        errorAttributes.put("message", message);
        errorAttributes.put("timestamp", System.currentTimeMillis());
        
        return ServerResponse
            .status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(errorAttributes);
    }
}