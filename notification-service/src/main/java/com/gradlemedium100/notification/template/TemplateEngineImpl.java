package com.gradlemedium100.notification.template;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the template engine that processes notification templates.
 * 
 * This class handles the loading, caching, and processing of templates used in
 * the notification system. It supports variable substitution and basic template logic.
 */
public class TemplateEngineImpl implements TemplateEngine {
    
    /**
     * Cache of template content by template name to improve performance by avoiding
     * repeated file system or database access for frequently used templates.
     */
    private Map<String, String> templateCache;
    
    /**
     * Path to the templates directory where template files are stored.
     */
    private String templatePath;
    
    /**
     * Pattern to match template variables in the format ${variableName}
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    /**
     * Constructs a new TemplateEngineImpl with the default template path.
     */
    public TemplateEngineImpl() {
        this("templates");
    }
    
    /**
     * Constructs a new TemplateEngineImpl with the specified template path.
     *
     * @param templatePath the path to the directory containing templates
     */
    public TemplateEngineImpl(String templatePath) {
        this.templatePath = templatePath;
        this.templateCache = new ConcurrentHashMap<>();
        
        // Create templates directory if it doesn't exist
        File templateDir = new File(templatePath);
        if (!templateDir.exists()) {
            templateDir.mkdirs();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String processTemplate(String templateName, Map<String, Object> data) {
        if (templateName == null || templateName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        
        try {
            // Get template content (either from cache or load it)
            String template = getTemplateFromCache(templateName);
            
            // Populate the template with data
            return populateTemplate(template, data);
        } catch (IOException e) {
            throw new TemplateNotFoundException("Template not found: " + templateName, e);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error processing template: " + templateName, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAvailableTemplates() {
        try {
            File dir = new File(templatePath);
            List<String> templates = new ArrayList<>();
            
            // Check if directory exists
            if (!dir.exists() || !dir.isDirectory()) {
                return templates;
            }
            
            // List all template files
            File[] files = dir.listFiles((d, name) -> name.endsWith(".template"));
            if (files != null) {
                for (File file : files) {
                    templates.add(file.getName().replace(".template", ""));
                }
            }
            
            return templates;
        } catch (Exception e) {
            throw new TemplateRegistryException("Error getting available templates", e);
        }
    }
    
    /**
     * Loads a template from the filesystem or database.
     *
     * @param templateName the name of the template to load
     * @return the template content as a string
     * @throws IOException if the template file cannot be read
     * @throws TemplateNotFoundException if the template does not exist
     */
    public String loadTemplate(String templateName) throws IOException {
        String filePath = Paths.get(templatePath, templateName + ".template").toString();
        File templateFile = new File(filePath);
        
        if (!templateFile.exists()) {
            throw new TemplateNotFoundException("Template not found: " + templateName);
        }
        
        return new String(Files.readAllBytes(templateFile.toPath()));
    }
    
    /**
     * Populates a template string with the provided data by replacing variables
     * in the format ${variableName} with corresponding values from the data map.
     *
     * @param template the template string to populate
     * @param data a map of key-value pairs to use for variable substitution
     * @return the populated template as a string
     */
    public String populateTemplate(String template, Map<String, Object> data) {
        if (template == null) {
            return "";
        }
        
        if (data == null) {
            return template;
        }
        
        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = data.get(key);
            String replacement = (value != null) ? value.toString() : "";
            
            // Escape $ and backslashes in the replacement string for the appendReplacement method
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Clears the template cache, forcing templates to be reloaded from their source
     * on the next request.
     */
    public void clearCache() {
        templateCache.clear();
    }
    
    /**
     * Gets a template from the cache if available, otherwise loads it from
     * the file system and adds it to the cache.
     *
     * @param templateName the name of the template to get
     * @return the template content
     * @throws IOException if the template cannot be loaded
     */
    private String getTemplateFromCache(String templateName) throws IOException {
        // Check if the template is in the cache
        String template = templateCache.get(templateName);
        
        // If not in cache, load it and add to cache
        if (template == null) {
            template = loadTemplate(templateName);
            templateCache.put(templateName, template);
        }
        
        return template;
    }
    
    /**
     * Sets a new path for template storage.
     *
     * @param templatePath the new template path
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
        clearCache();  // Clear cache when changing path
    }
    
    // TODO: Add method to register new templates at runtime as mentioned in the interface
    
    // TODO: Implement template version management
    
    // FIXME: Improve error handling for malformed templates
    
    // FIXME: Add support for more complex template logic like conditionals and loops
}