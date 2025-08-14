package com.gradlemedium100.common.validation;

import java.util.List;

/**
 * Interface defining the contract for validators in the application.
 * Validators are responsible for ensuring data meets specific requirements
 * before it's processed by the system.
 */
public interface Validator {

    /**
     * Validates an object against defined rules.
     *
     * @param object The object to validate
     * @return true if the object passes all validations, false otherwise
     */
    boolean validate(Object object);
    
    /**
     * Validates an object and returns a list of validation errors.
     *
     * @param object The object to validate
     * @return A list of error messages, empty if validation passes
     */
    List<String> validateWithErrors(Object object);
    
    /**
     * Checks if a value is valid according to the specified validation type.
     *
     * @param value The value to validate
     * @param validationType The type of validation to perform
     * @return true if the value is valid, false otherwise
     */
    boolean isValid(String value, String validationType);
}