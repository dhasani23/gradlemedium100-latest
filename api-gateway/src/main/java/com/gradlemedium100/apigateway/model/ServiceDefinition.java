package com.gradlemedium100.apigateway.model;

/**
 * Represents the definition of a service for API documentation purposes.
 * Contains metadata about a service's API documentation.
 */
public class ServiceDefinition {
    private String name;
    private String url;
    private String version;
    private String description;

    public ServiceDefinition() {
    }

    public ServiceDefinition(String name, String url, String version, String description) {
        this.name = name;
        this.url = url;
        this.version = version;
        this.description = description;
    }

    /**
     * @return the name of the service
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the service
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the base URL where the service's API documentation is hosted
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the base URL where the service's API documentation is hosted
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the version of the service API
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version of the service API
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the description of the service
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description of the service
     */
    public void setDescription(String description) {
        this.description = description;
    }
}