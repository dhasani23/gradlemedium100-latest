package com.gradlemedium100.common.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Data transfer object representing pagination parameters for API requests.
 * This class provides a standardized way to handle pagination across the application
 * and can be converted to Spring's PageRequest object when needed.
 */
public class PageRequest implements Serializable {

    /**
     * Serial version UID for serialization
     */
    private static final long serialVersionUID = 1234567890123456789L;

    /**
     * Page number (0-based)
     */
    private int page;

    /**
     * Page size (number of items per page)
     */
    private int size;

    /**
     * Field to sort by
     */
    private String sortBy;

    /**
     * Sort direction (ASC or DESC)
     */
    private String sortDirection;

    /**
     * Default constructor
     */
    public PageRequest() {
        // Default values
        this.page = 0;
        this.size = 20;
        this.sortDirection = "ASC";
    }

    /**
     * Constructor with page number and size
     *
     * @param page Page number (0-based)
     * @param size Page size
     */
    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
        this.sortDirection = "ASC";
    }

    /**
     * Constructor with all pagination parameters
     *
     * @param page          Page number (0-based)
     * @param size          Page size
     * @param sortBy        Field to sort by
     * @param sortDirection Sort direction (ASC or DESC)
     */
    public PageRequest(int page, int size, String sortBy, String sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    /**
     * Gets the page number
     *
     * @return the page number (0-based)
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the page number
     *
     * @param page the page number to set (0-based)
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Gets the page size
     *
     * @return the number of items per page
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the page size
     *
     * @param size the number of items per page
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Gets the field to sort by
     *
     * @return the field name to sort by
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the field to sort by
     *
     * @param sortBy the field name to sort by
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Gets the sort direction
     *
     * @return the sort direction (ASC or DESC)
     */
    public String getSortDirection() {
        return sortDirection;
    }

    /**
     * Sets the sort direction
     *
     * @param sortDirection the sort direction (ASC or DESC)
     */
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    /**
     * Converts this DTO to a Spring PageRequest object
     * 
     * @return a Spring PageRequest object configured with this DTO's pagination parameters
     * @throws UnsupportedOperationException if Spring Data is not available on the classpath
     */
    public org.springframework.data.domain.PageRequest toSpringPageRequest() {
        // FIXME: This method requires Spring Data on the classpath
        // When used in a module with Spring Data available, uncomment and use this implementation:
        /*
        Sort.Direction direction = Sort.Direction.valueOf(
                sortDirection != null ? sortDirection.toUpperCase() : "ASC");
        
        if (sortBy != null && !sortBy.isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(page, size, direction, sortBy);
        } else {
            return org.springframework.data.domain.PageRequest.of(page, size);
        }
        */
        
        // This placeholder implementation will throw an exception if called without Spring Data
        throw new UnsupportedOperationException(
                "Cannot convert to Spring PageRequest: Spring Data is not available in this module");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PageRequest that = (PageRequest) o;
        
        return page == that.page &&
               size == that.size &&
               Objects.equals(sortBy, that.sortBy) &&
               Objects.equals(sortDirection, that.sortDirection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, sortBy, sortDirection);
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}