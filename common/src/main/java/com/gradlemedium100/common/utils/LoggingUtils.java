package com.gradlemedium100.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Utility class providing standardized logging methods and formatters.
 * This class helps maintain consistent logging patterns across the application.
 */
public final class LoggingUtils {

    /**
     * Standard message format for method entry logging.
     */
    public static final String ENTRY_MESSAGE = "ENTRY [{}] - Args: {}";

    /**
     * Standard message format for method exit logging.
     */
    public static final String EXIT_MESSAGE = "EXIT [{}] - Result: {}";

    /**
     * Standard message format for error logging.
     */
    public static final String ERROR_MESSAGE = "ERROR [{}] - Exception: {} - Message: {}";
    
    // Regular expression patterns for sanitizing sensitive information
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:\\d{4}[- ]?){3}\\d{4}\\b");
    private static final Pattern SSN_PATTERN = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?i)password[=:]\\s*\\S+");
    
    // Replacement texts
    private static final String MASKED_CC = "[MASKED-CC]";
    private static final String MASKED_SSN = "[MASKED-SSN]";
    private static final String MASKED_PASSWORD = "password=[MASKED]";

    // Private constructor to prevent instantiation
    private LoggingUtils() {
        throw new UnsupportedOperationException("This utility class should not be instantiated");
    }

    /**
     * Gets a logger for the specified class.
     *
     * @param clazz The class for which to get the logger
     * @return Logger instance for the specified class
     */
    public static Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Logs method entry with standardized format.
     *
     * @param logger     The logger to use
     * @param methodName The name of the method being entered
     * @param args       The arguments passed to the method
     */
    public static void logEntry(Logger logger, String methodName, Object... args) {
        if (logger == null || !logger.isDebugEnabled()) {
            return;
        }

        String safeArgs = sanitizeLogMessage(formatArgs(args));
        logger.debug(ENTRY_MESSAGE, methodName, safeArgs);
    }

    /**
     * Logs method exit with standardized format.
     *
     * @param logger     The logger to use
     * @param methodName The name of the method being exited
     * @param result     The result returned by the method
     */
    public static void logExit(Logger logger, String methodName, Object result) {
        if (logger == null || !logger.isDebugEnabled()) {
            return;
        }
        
        // Handle null result
        String resultStr = (result == null) ? "null" : result.toString();
        String safeResult = sanitizeLogMessage(resultStr);
        
        logger.debug(EXIT_MESSAGE, methodName, safeResult);
    }

    /**
     * Logs exceptions with standardized format.
     *
     * @param logger     The logger to use
     * @param methodName The name of the method where the exception occurred
     * @param exception  The exception that was thrown
     */
    public static void logError(Logger logger, String methodName, Throwable exception) {
        if (logger == null) {
            return;
        }
        
        if (exception == null) {
            logger.error("ERROR [{}] - Exception: null", methodName);
            return;
        }

        String exceptionName = exception.getClass().getSimpleName();
        String message = exception.getMessage();
        message = message != null ? sanitizeLogMessage(message) : "No message";
        
        logger.error(ERROR_MESSAGE, methodName, exceptionName, message);
        
        // If trace enabled, also log the stack trace
        if (logger.isTraceEnabled()) {
            logger.trace("Stack trace for exception in method [{}]:", methodName, exception);
        }
    }

    /**
     * Sanitizes log messages to remove sensitive information.
     *
     * @param message The message to sanitize
     * @return Sanitized message with sensitive information masked
     */
    public static String sanitizeLogMessage(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        // Apply sanitization patterns
        String sanitized = CREDIT_CARD_PATTERN.matcher(message).replaceAll(MASKED_CC);
        sanitized = SSN_PATTERN.matcher(sanitized).replaceAll(MASKED_SSN);
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll(MASKED_PASSWORD);
        
        // FIXME: Add more patterns for other types of sensitive information
        // TODO: Consider using a configuration file for patterns and replacements
        
        return sanitized;
    }

    /**
     * Formats method arguments into a string representation.
     *
     * @param args The arguments to format
     * @return A string representation of the arguments
     */
    private static String formatArgs(Object... args) {
        if (args == null) {
            return "null";
        }
        
        if (args.length == 0) {
            return "[]";
        }
        
        // Handle large objects or collections specially
        if (args.length == 1) {
            Object arg = args[0];
            if (arg == null) {
                return "null";
            }
            if (arg.getClass().isArray()) {
                // Convert array to string with limited length
                return formatArray(arg);
            }
        }
        
        return Arrays.toString(args);
    }
    
    /**
     * Formats an array for logging, handling different array types.
     * 
     * @param array The array to format
     * @return A string representation of the array
     */
    private static String formatArray(Object array) {
        if (array == null) {
            return "null";
        }
        
        if (array instanceof Object[]) {
            return Arrays.toString((Object[]) array);
        } else if (array instanceof int[]) {
            return Arrays.toString((int[]) array);
        } else if (array instanceof long[]) {
            return Arrays.toString((long[]) array);
        } else if (array instanceof double[]) {
            return Arrays.toString((double[]) array);
        } else if (array instanceof float[]) {
            return Arrays.toString((float[]) array);
        } else if (array instanceof boolean[]) {
            return Arrays.toString((boolean[]) array);
        } else if (array instanceof char[]) {
            return Arrays.toString((char[]) array);
        } else if (array instanceof byte[]) {
            byte[] bytes = (byte[]) array;
            // For byte arrays, just show length to avoid large logs
            return "byte[" + bytes.length + "]";
        } else if (array instanceof short[]) {
            return Arrays.toString((short[]) array);
        } else {
            // Fallback for unknown array types
            return array.toString();
        }
    }
}