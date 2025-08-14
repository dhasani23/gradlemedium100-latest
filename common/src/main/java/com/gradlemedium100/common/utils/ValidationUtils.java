package com.gradlemedium100.common.utils;

import com.gradlemedium100.common.validation.Validator;
import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * Utility class providing common validation methods for strings, numbers, objects, etc.
 * Used throughout the application to ensure data validity and consistency.
 */
public final class ValidationUtils {

    /**
     * Regular expression pattern for validating email addresses.
     * Validates standard email format with domain and TLD.
     */
    public static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    /**
     * Regular expression pattern for validating phone numbers.
     * Supports various formats including international numbers with country codes.
     */
    public static final String PHONE_REGEX = 
        "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    
    // Static validator instance for Bean Validation API
    private static final javax.validation.Validator VALIDATOR;
    
    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }
    
    // Private constructor to prevent instantiation
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }
    
    /**
     * Validates if a string is a valid email address.
     *
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates if a string is a valid phone number.
     * 
     * @param phone the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean validatePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        
        // Remove common separators to standardize input
        String cleanPhone = phone.replaceAll("[\\s-.]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validates if a number is within the specified range (inclusive).
     * 
     * @param value the value to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @return true if the value is within the range, false otherwise
     * @throws IllegalArgumentException if min is greater than max
     */
    public static boolean validateRange(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum value cannot be greater than maximum value");
        }
        return value >= min && value <= max;
    }
    
    /**
     * Validates that an object is not null.
     * 
     * @param object the object to validate
     * @param message the error message to use if validation fails
     * @throws IllegalArgumentException if the object is null
     */
    public static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Validates bean constraints using the Bean Validation API.
     * 
     * @param bean the bean to validate
     * @param <T> the bean type
     * @return a set of constraint violations, empty if the bean is valid
     */
    public static <T> Set<ConstraintViolation<T>> validateBeanConstraints(T bean) {
        validateNotNull(bean, "Bean to validate cannot be null");
        return VALIDATOR.validate(bean);
    }
    
    /**
     * Enhanced validation method that validates an object and throws an exception
     * with detailed information about constraint violations.
     * 
     * @param bean the bean to validate
     * @param <T> the bean type
     * @throws IllegalArgumentException if any constraints are violated
     */
    public static <T> void validateAndThrow(T bean) {
        Set<ConstraintViolation<T>> violations = validateBeanConstraints(bean);
        
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            int i = 0;
            for (ConstraintViolation<T> violation : violations) {
                if (i > 0) {
                    errorMessage.append("; ");
                }
                errorMessage.append(violation.getPropertyPath())
                           .append(": ")
                           .append(violation.getMessage());
                i++;
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }
    
    /**
     * Checks if a string is null or empty.
     * 
     * @param str the string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validates that a string is not null or empty.
     * 
     * @param str the string to validate
     * @param message the error message to use if validation fails
     * @throws IllegalArgumentException if the string is null or empty
     */
    public static void validateNotEmpty(String str, String message) {
        if (isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    // TODO: Add additional validation methods for dates, URLs, etc.
    
    // FIXME: Phone validation regex might not cover all international formats
}