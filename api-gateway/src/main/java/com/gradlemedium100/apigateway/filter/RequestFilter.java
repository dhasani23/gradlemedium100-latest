package com.gradlemedium100.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.core.Ordered;

/**
 * Interface for handling API request filtering.
 * This interface standardizes the implementation of gateway filters
 * by defining methods for creating filters and determining their order
 * in the filter chain.
 */
public interface RequestFilter extends Ordered {
    
    /**
     * Returns a gateway filter implementation.
     * Each implementation should provide a filter that performs its specific
     * filtering logic.
     * 
     * @return The GatewayFilter implementation
     */
    GatewayFilter getFilter();
    
    /**
     * Returns the order of this filter in the filter chain.
     * Lower values have higher priority.
     * 
     * @return The order value for this filter
     */
    @Override
    int getOrder();
}