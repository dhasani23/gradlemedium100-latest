package com.gradlemedium100.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gradlemedium100.common.exception.ErrorHandler;
import com.gradlemedium100.common.exception.GlobalExceptionHandler;
import com.gradlemedium100.common.util.CorrelationIdGenerator;
import com.gradlemedium100.common.validation.BaseValidator;
import com.gradlemedium100.common.validation.Validator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Spring configuration class that sets up common beans and services used throughout the application.
 * This configuration centralizes the creation of shared beans to ensure consistency across modules.
 */
@Configuration
public class CommonConfiguration {

    /**
     * Creates and configures a Jackson ObjectMapper bean for JSON serialization/deserialization.
     * The mapper is configured with standard settings used across the application.
     *
     * @return Configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register JavaTimeModule to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configure to write dates as ISO-8601 strings instead of timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Don't fail on empty beans
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // FIXME: Consider adding custom serializers/deserializers for complex types
        
        return objectMapper;
    }
    
    /**
     * Creates a JSR-303 validator factory bean
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean javaValidator() {
        return new LocalValidatorFactoryBean();
    }
    
    /**
     * Creates a validator bean for validating objects against their constraints.
     * This implementation uses the BaseValidator which provides common validation logic.
     *
     * @return Validator instance
     */
    @Bean
    public Validator validator() {
        // Create application validator with enhanced functionality
        BaseValidator validator = new BaseValidator(javaValidator());
        
        // TODO: Register common validation types and patterns
        
        return validator;
    }
    
    /**
     * Creates an error handler bean for consistent error handling across the application.
     * The error handler provides methods to convert exceptions to appropriate responses.
     *
     * @return ErrorHandler instance
     */
    @Bean
    public ErrorHandler errorHandler() {
        return new GlobalExceptionHandler();
    }
    
    /**
     * Creates a global exception handler bean for centralized exception handling.
     * This handler implementation is typically used with Spring's @ControllerAdvice
     * to handle exceptions thrown by controllers.
     *
     * @return GlobalExceptionHandler instance
     */
    @Bean
    public GlobalExceptionHandler commonExceptionHandler() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        // TODO: Configure additional exception handling strategies
        return exceptionHandler;
    }
    
    /**
     * Creates a message source for internationalization support.
     * This message source loads messages from properties files and supports
     * locale-specific message resolution.
     *
     * @return MessageSource instance
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = 
            new ReloadableResourceBundleMessageSource();
        
        // Configure base names for message properties files
        messageSource.setBasenames(
            "classpath:messages/messages",
            "classpath:messages/validation-messages"
        );
        
        // Set default encoding for message files
        messageSource.setDefaultEncoding("UTF-8");
        
        // Cache messages for performance (in seconds)
        messageSource.setCacheSeconds(60);
        
        // Use code as message if no message found (useful for development)
        messageSource.setUseCodeAsDefaultMessage(true);
        
        return messageSource;
    }
    
    /**
     * Creates a correlation ID generator for distributed tracing.
     * This generator creates unique identifiers that can be used to trace
     * requests across multiple services.
     *
     * @return CorrelationIdGenerator instance
     */
    @Bean
    public CorrelationIdGenerator correlationIdGenerator() {
        return new CorrelationIdGenerator();
    }
}