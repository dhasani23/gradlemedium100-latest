package com.gradlemedium100.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class for Circuit Breaker patterns.
 * 
 * This class provides configuration for implementing the Circuit Breaker pattern
 * in the API Gateway to handle downstream service failures gracefully.
 * Circuit breakers help prevent cascading failures and allow the system
 * to degrade gracefully during partial outages.
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Threshold percentage for failure rate to trip circuit breaker
     */
    @Value("${resilience4j.circuitbreaker.default.failureRateThreshold:50}")
    private float defaultFailureRateThreshold;

    /**
     * Threshold percentage for slow call rate to trip circuit breaker
     */
    @Value("${resilience4j.circuitbreaker.default.slowCallRateThreshold:50}")
    private float slowCallRateThreshold;

    /**
     * Duration threshold to consider a call as slow (in milliseconds)
     */
    @Value("${resilience4j.circuitbreaker.default.slowCallDurationThreshold:2000}")
    private long slowCallDurationThresholdMs;

    /**
     * Number of calls allowed in half-open state
     */
    @Value("${resilience4j.circuitbreaker.default.permittedNumberOfCallsInHalfOpenState:10}")
    private int permittedNumberOfCallsInHalfOpenState;

    /**
     * Size of the sliding window for call statistics
     */
    @Value("${resilience4j.circuitbreaker.default.slidingWindowSize:100}")
    private int slidingWindowSize;

    /**
     * Minimum number of calls required before calculations
     */
    @Value("${resilience4j.circuitbreaker.default.minimumNumberOfCalls:10}")
    private int minimumNumberOfCalls;

    /**
     * Duration to wait before transitioning from open to half-open (in milliseconds)
     */
    @Value("${resilience4j.circuitbreaker.default.waitDurationInOpenState:60000}")
    private long waitDurationInOpenStateMs;

    /**
     * Creates and configures a circuit breaker factory with default settings.
     * The factory is used to create circuit breaker instances for different services.
     *
     * @return configured ReactiveResilience4JCircuitBreakerFactory
     */
    @Bean
    public ReactiveResilience4JCircuitBreakerFactory defaultCircuitBreakerFactory(
            CircuitBreakerRegistry circuitBreakerRegistry,
            TimeLimiterRegistry timeLimiterRegistry) {
        
        ReactiveResilience4JCircuitBreakerFactory factory = 
            new ReactiveResilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry);
        
        // Configure the default circuit breaker settings
        factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(customCircuitBreakerConfig())
                .timeLimiterConfig(timeLimiterConfig())
                .build());
        
        return factory;
    }

    /**
     * Configures custom circuit breaker behavior.
     * This method sets up the behavior of circuit breakers including thresholds,
     * sliding windows, and state transition parameters.
     *
     * @return A custom CircuitBreakerConfig configuration
     */
    @Bean
    public io.github.resilience4j.circuitbreaker.CircuitBreakerConfig customCircuitBreakerConfig() {
        return io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(defaultFailureRateThreshold)
                .slowCallRateThreshold(slowCallRateThreshold)
                .slowCallDurationThreshold(Duration.ofMillis(slowCallDurationThresholdMs))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(slidingWindowSize)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenStateMs))
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();
    }

    /**
     * Configures time limiters for circuit breakers.
     * Time limiters ensure that calls to services don't exceed specified timeouts.
     *
     * @return A TimeLimiterConfig configuration
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        // Configure timeout duration via properties
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .cancelRunningFuture(true)
                .build();
    }

    /**
     * Creates circuit breaker registry with the custom configuration
     * 
     * @return CircuitBreakerRegistry with the custom configuration
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(customCircuitBreakerConfig());
    }
    
    /**
     * Creates time limiter registry with the custom configuration
     * 
     * @return TimeLimiterRegistry with the custom configuration
     */
    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        return TimeLimiterRegistry.of(timeLimiterConfig());
    }

    /**
     * Creates circuit breaker customizer with specific configurations for different services.
     * 
     * @return Customizer for ReactiveResilience4JCircuitBreakerFactory with service-specific configurations
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> specificServiceCircuitBreakerCustomizer() {
        return factory -> {
            // Configure circuit breaker for product service with lower thresholds
            factory.configure(builder -> builder
                    .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                            .failureRateThreshold(40)
                            .waitDurationInOpenState(Duration.ofSeconds(30))
                            .slidingWindowSize(50)
                            .build())
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build()),
                    "productService");
            
            // Configure circuit breaker for payment service with higher timeout
            factory.configure(builder -> builder
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build()),
                    "paymentService");
        };
    }
}