package com.gradlemedium100.common.validation;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Base implementation of the Validator interface with common validation methods.
 * This class provides standard validation functionality including both Bean Validation
 * and pattern-based regex validations for different types of data.
 */
public class BaseValidator implements Validator {
    
    /**
     * Java Bean Validation API validator
     */
    private final javax.validation.Validator javaValidator;
    
    /**
     * Map of validation types to their corresponding regex patterns
     */
    private final Map<String, Pattern> validationTypes;
    
    /**
     * Constructs a new BaseValidator with the specified Java Bean Validator.
     *
     * @param javaValidator The Java Bean Validation API validator
     */
    public BaseValidator(javax.validation.Validator javaValidator) {
        this.javaValidator = javaValidator;
        this.validationTypes = new HashMap<>();
        
        // Initialize with some common validation patterns
        registerValidationType("email", Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"));
        registerValidationType("phone", Pattern.compile("^\\+?[0-9]{10,15}$"));
        registerValidationType("zipcode", Pattern.compile("^[0-9]{5}(-[0-9]{4})?$"));
        registerValidationType("url", Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"));
    }
    
    /**
     * Validates an object against defined rules.
     *
     * @param object The object to validate
     * @return true if the object passes all validations, false otherwise
     */
    @Override
    public boolean validate(Object object) {
        if (object == null) {
            return false;
        }
        
        Set<ConstraintViolation<Object>> violations = javaValidator.validate(object);
        return violations.isEmpty();
    }
    
    /**
     * Validates an object and returns a list of validation errors.
     *
     * @param object The object to validate
     * @return A list of error messages, empty if validation passes
     */
    @Override
    public List<String> validateWithErrors(Object object) {
        List<String> errors = new ArrayList<>();
        
        if (object == null) {
            errors.add("Object cannot be null");
            return errors;
        }
        
        Set<ConstraintViolation<Object>> violations = javaValidator.validate(object);
        for (ConstraintViolation<Object> violation : violations) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        
        return errors;
    }
    
    /**
     * Checks if a value is valid according to the specified validation type.
     *
     * @param value The value to validate
     * @param validationType The type of validation to perform
     * @return true if the value is valid, false otherwise
     * @throws IllegalArgumentException if the validation type is not registered
     */
    @Override
    public boolean isValid(String value, String validationType) {
        if (value == null || validationType == null) {
            return false;
        }
        
        Pattern pattern = validationTypes.get(validationType);
        if (pattern == null) {
            // FIXME: Consider returning false instead of throwing an exception for better resilience
            throw new IllegalArgumentException("Unknown validation type: " + validationType);
        }
        
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
    
    /**
     * Registers a new validation type with a corresponding regex pattern.
     *
     * @param typeName The name of the validation type
     * @param pattern The regex pattern for validation
     * @throws IllegalArgumentException if typeName is null or empty, or if pattern is null
     */
    public void registerValidationType(String typeName, Pattern pattern) {
        if (typeName == null || typeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be null or empty");
        }
        
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        
        // TODO: Add support for pattern validation before registration
        // TODO: Implement event notification when new validation types are registered
        
        validationTypes.put(typeName, pattern);
    }
    
    /**
     * Checks if a validation type is registered.
     *
     * @param typeName The name of the validation type
     * @return true if the validation type is registered, false otherwise
     */
    public boolean hasValidationType(String typeName) {
        return validationTypes.containsKey(typeName);
    }
    
    /**
     * Gets all registered validation type names.
     *
     * @return A set of validation type names
     */
    public Set<String> getValidationTypeNames() {
        return validationTypes.keySet();
    }
    
    /**
     * Removes a validation type from the registry.
     *
     * @param typeName The name of the validation type to remove
     * @return true if the validation type was removed, false if it wasn't registered
     */
    public boolean removeValidationType(String typeName) {
        if (validationTypes.containsKey(typeName)) {
            validationTypes.remove(typeName);
            return true;
        }
        return false;
    }
}