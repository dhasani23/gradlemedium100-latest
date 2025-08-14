package com.gradlemedium100.apigateway.filter;

import com.gradlemedium100.common.util.CorrelationIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String TEST_CORRELATION_ID = "corr-test-correlation-id";
    private static final String INVALID_CORRELATION_ID = "invalid-id";

    @Mock
    private CorrelationIdGenerator correlationIdGenerator;

    @Mock
    private GatewayFilterChain filterChain;

    private CorrelationIdFilter correlationIdFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        correlationIdFilter = new CorrelationIdFilter(correlationIdGenerator, CORRELATION_ID_HEADER);
        
        // Mock behavior for correlation ID generator
        when(correlationIdGenerator.generateCorrelationId()).thenReturn(TEST_CORRELATION_ID);
        when(correlationIdGenerator.isValidCorrelationId(TEST_CORRELATION_ID)).thenReturn(true);
        when(correlationIdGenerator.isValidCorrelationId(INVALID_CORRELATION_ID)).thenReturn(false);
        
        // Mock filter chain to return empty Mono
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void shouldAddCorrelationIdWhenNotPresent() {
        // Create request without correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test-endpoint")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Get the filter and apply it
        GatewayFilter filter = correlationIdFilter.getFilter();
        filter.filter(exchange, filterChain).block();
        
        // Verify correlation ID was added to both request and response
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        
        assertTrue(requestHeaders.containsKey(CORRELATION_ID_HEADER));
        assertEquals(TEST_CORRELATION_ID, requestHeaders.getFirst(CORRELATION_ID_HEADER));
        assertTrue(responseHeaders.containsKey(CORRELATION_ID_HEADER));
        assertEquals(TEST_CORRELATION_ID, responseHeaders.getFirst(CORRELATION_ID_HEADER));
        
        // Verify generator was called
        verify(correlationIdGenerator, times(1)).generateCorrelationId();
    }

    @Test
    void shouldKeepExistingValidCorrelationId() {
        // Create request with valid correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test-endpoint")
                .header(CORRELATION_ID_HEADER, TEST_CORRELATION_ID)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Get the filter and apply it
        GatewayFilter filter = correlationIdFilter.getFilter();
        filter.filter(exchange, filterChain).block();
        
        // Verify the existing correlation ID was kept
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        assertEquals(TEST_CORRELATION_ID, requestHeaders.getFirst(CORRELATION_ID_HEADER));
        
        // Verify generator was not called to create a new ID
        verify(correlationIdGenerator, never()).generateCorrelationId();
    }

    @Test
    void shouldReplaceInvalidCorrelationId() {
        // Create request with invalid correlation ID
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test-endpoint")
                .header(CORRELATION_ID_HEADER, INVALID_CORRELATION_ID)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Get the filter and apply it
        GatewayFilter filter = correlationIdFilter.getFilter();
        filter.filter(exchange, filterChain).block();
        
        // Verify the correlation ID was replaced with a valid one
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        
        assertEquals(TEST_CORRELATION_ID, requestHeaders.getFirst(CORRELATION_ID_HEADER));
        assertEquals(TEST_CORRELATION_ID, responseHeaders.getFirst(CORRELATION_ID_HEADER));
        
        // Verify validation and generation were called
        verify(correlationIdGenerator, times(1)).isValidCorrelationId(INVALID_CORRELATION_ID);
        verify(correlationIdGenerator, times(1)).generateCorrelationId();
    }

    @Test
    void shouldHaveCorrectFilterOrder() {
        assertEquals(1, correlationIdFilter.getOrder());
    }
}