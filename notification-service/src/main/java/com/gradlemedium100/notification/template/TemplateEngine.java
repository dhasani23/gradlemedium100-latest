package com.gradlemedium100.notification.template;

import java.util.List;
import java.util.Map;

/**
 * Interface for processing notification templates and populating them with data.
 * 
 * This interface defines the contract for template processing engines used to generate
 * formatted notification content for various delivery channels such as email, SMS, and
 * in-app notifications.
 */
public interface TemplateEngine {
    
    /**
     * Processes a template with provided data and returns the populated content.
     * 
     * @param templateName the name of the template to process
     * @param data a map of key-value pairs containing the data to populate the template with
     * @return the processed template content as a string
     * @throws TemplateNotFoundException if the specified template cannot be found
     * @throws TemplateProcessingException if there is an error during template processing
     */
    String processTemplate(String templateName, Map<String, Object> data);
    
    /**
     * Returns a list of available template names that can be processed.
     * 
     * @return a list of available template names
     * @throws TemplateRegistryException if there is an error accessing the template registry
     */
    List<String> getAvailableTemplates();
    
    // TODO: Add method to register new templates at runtime
    
    // TODO: Add method to validate templates against a schema
    
    // FIXME: Consider adding version information to templates to handle template upgrades
}