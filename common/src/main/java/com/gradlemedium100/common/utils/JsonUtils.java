package com.gradlemedium100.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for JSON serialization, deserialization, and manipulation operations.
 * Provides methods to convert between Java objects and JSON strings.
 * 
 * @since 1.0
 */
public class JsonUtils {

    private static final Logger logger = Logger.getLogger(JsonUtils.class.getName());
    
    /**
     * ObjectMapper instance for JSON operations. 
     * Thread-safe once configured and can be reused across multiple threads.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // Configure ObjectMapper with default settings
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private JsonUtils() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }

    /**
     * Converts an object to a JSON string.
     *
     * @param object The object to convert
     * @return The JSON string representation of the object
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error serializing object to JSON", e);
            // FIXME: Consider if this should throw a custom exception instead of returning null
            return null;
        }
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param json The JSON string to convert
     * @param valueType The class of the object to convert to
     * @param <T> The type of the object
     * @return The object representation of the JSON string
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(json, valueType);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deserializing JSON to object", e);
            // TODO: Implement proper exception handling strategy
            return null;
        }
    }

    /**
     * Converts a JSON array string to a list of objects of the specified type.
     *
     * @param json The JSON array string to convert
     * @param elementType The class of the elements in the list
     * @param <T> The type of the elements
     * @return The list of objects represented by the JSON array
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementType) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            // Creating a TypeReference for handling generics with Jackson
            return objectMapper.readValue(json, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deserializing JSON to list", e);
            return Collections.emptyList();
        }
    }

    /**
     * Converts a JSON object string to a map.
     *
     * @param json The JSON object string to convert
     * @return The map represented by the JSON object
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        
        try {
            // Using TypeReference to handle the Map generic type
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error deserializing JSON to map", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Formats a JSON string with indentation and line breaks for better readability.
     *
     * @param json The JSON string to format
     * @return The formatted JSON string
     */
    public static String prettyPrint(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        
        try {
            // Parse the input JSON to ensure it's valid
            Object jsonObj = objectMapper.readValue(json, Object.class);
            // Re-serialize with pretty printing enabled
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error formatting JSON string", e);
            // If formatting fails, return the original string
            return json;
        }
    }
    
    /**
     * Gets a thread-safe copy of the ObjectMapper for direct use.
     * 
     * @return A configured ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        // Return a copy to prevent external modification of the static instance
        return objectMapper.copy();
    }
}