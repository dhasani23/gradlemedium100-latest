package com.gradlemedium100.common.utils;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Utility class for string manipulation operations like formatting,
 * validation, sanitization.
 * 
 * This class provides common string manipulation methods to be used
 * across the application.
 */
public final class StringUtils {

    /**
     * Empty string constant
     */
    public static final String EMPTY = "";
    
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Checks if a string is null or empty
     * 
     * @param str the string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * Checks if a string is null, empty or consists only of whitespace
     * 
     * @param str the string to check
     * @return true if the string is null, empty or whitespace, false otherwise
     */
    public static boolean isNullOrWhitespace(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Truncates a string to the specified maximum length
     * 
     * @param str the string to truncate
     * @param maxLength the maximum length of the resulting string
     * @return the truncated string, or the original string if its length is less than maxLength
     * @throws IllegalArgumentException if maxLength is negative
     */
    public static String truncate(String str, int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative");
        }
        
        if (str == null) {
            return null;
        }
        
        return str.length() <= maxLength ? str : str.substring(0, maxLength);
    }
    
    /**
     * Sanitizes HTML content to prevent XSS attacks
     * 
     * @param html the HTML content to sanitize
     * @return the sanitized HTML
     */
    public static String sanitizeHtml(String html) {
        if (html == null) {
            return null;
        }
        
        // FIXME: StringEscapeUtils is deprecated in newer versions of commons-lang3
        // Consider using a proper HTML sanitizer library like OWASP Java HTML Sanitizer
        String sanitized = org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(html);
        
        // Additional sanitization could be implemented here
        sanitized = sanitized.replaceAll("(?i)<script.*?>.*?</script>", "");
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=\\s*\".*?\"", "");
        
        return sanitized;
    }
    
    /**
     * Capitalizes the first letter of a string
     * 
     * @param str the string to capitalize
     * @return the string with the first letter capitalized, or null if input is null
     */
    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        
        // Use charAt(0) and substring(1) for better performance with short strings
        // compared to using a StringBuilder for such a simple case
        return Character.toUpperCase(str.charAt(0)) + 
               (str.length() > 1 ? str.substring(1) : "");
    }
    
    /**
     * Masks sensitive data, showing only the specified number of characters
     * 
     * @param data the sensitive data to mask
     * @param visibleChars the number of characters to leave visible (from the beginning)
     * @return the masked data
     * @throws IllegalArgumentException if visibleChars is negative
     */
    public static String maskSensitiveData(String data, int visibleChars) {
        if (visibleChars < 0) {
            throw new IllegalArgumentException("Number of visible characters cannot be negative");
        }
        
        if (data == null) {
            return null;
        }
        
        if (data.length() <= visibleChars) {
            return data;
        }
        
        StringBuilder masked = new StringBuilder();
        masked.append(data.substring(0, visibleChars));
        
        // Fill the rest with asterisks
        for (int i = visibleChars; i < data.length(); i++) {
            masked.append('*');
        }
        
        return masked.toString();
    }
    
    /**
     * Checks if a string contains another string, case insensitive
     * 
     * TODO: Optimize for large strings by using more efficient algorithms
     * 
     * @param source the source string
     * @param searchStr the string to search for
     * @return true if the source contains the search string, false otherwise
     */
    public static boolean containsIgnoreCase(String source, String searchStr) {
        if (source == null || searchStr == null) {
            return false;
        }
        
        return source.toLowerCase().contains(searchStr.toLowerCase());
    }
}