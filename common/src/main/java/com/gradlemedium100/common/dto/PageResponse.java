package com.gradlemedium100.common.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data transfer object representing paginated response data for API responses.
 * This class is used for returning paginated data in a standardized format across
 * all API endpoints in the application.
 *
 * @param <T> The type of objects contained in the page
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> extends BaseDTO {
    
    private static final long serialVersionUID = 1L;
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private boolean empty;
    
    /**
     * Default constructor.
     * Initializes an empty page response with default values.
     */
    public PageResponse() {
        this.content = new ArrayList<>();
        this.page = 0;
        this.size = 0;
        this.totalElements = 0;
        this.totalPages = 0;
        this.last = true;
        this.first = true;
        this.empty = true;
    }
    
    /**
     * Constructor with essential pagination information.
     * Calculates derived pagination properties like totalPages, first, last, and empty.
     *
     * @param content The content of the current page
     * @param page The current page number (0-based)
     * @param size The page size
     * @param totalElements The total number of elements across all pages
     */
    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content != null ? content : new ArrayList<>();
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        
        // Calculate derived properties
        this.empty = content == null || content.isEmpty();
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.first = page == 0;
        this.last = page >= totalPages - 1 || totalPages == 0;
    }
    
    /**
     * Static factory method to create a PageResponse from a Spring Page object.
     * This allows for easy conversion from Spring Data pagination results.
     *
     * @param <T> The type of objects contained in the page
     * @param page The Spring Page object to convert
     * @return A new PageResponse instance containing the same data as the Spring Page
     */
    public static <T> PageResponse<T> fromSpringPage(org.springframework.data.domain.Page<T> page) {
        if (page == null) {
            return new PageResponse<>();
        }
        
        PageResponse<T> response = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
        
        // Ensure consistency with Spring's calculations
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setEmpty(page.isEmpty());
        
        return response;
    }
    
    /**
     * Gets the content of the current page.
     *
     * @return The list of items in this page
     */
    public List<T> getContent() {
        return content;
    }
    
    /**
     * Sets the content of the current page.
     *
     * @param content The list of items in this page
     */
    public void setContent(List<T> content) {
        this.content = content != null ? content : new ArrayList<>();
        this.empty = this.content.isEmpty();
    }
    
    /**
     * Gets the current page number.
     *
     * @return The current page number (0-based)
     */
    public int getPage() {
        return page;
    }
    
    /**
     * Sets the current page number.
     *
     * @param page The current page number (0-based)
     */
    public void setPage(int page) {
        this.page = page;
        this.first = page == 0;
        this.last = page >= totalPages - 1 || totalPages == 0;
    }
    
    /**
     * Gets the page size.
     *
     * @return The page size
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Sets the page size.
     * Recalculates totalPages based on the new size.
     *
     * @param size The page size
     */
    public void setSize(int size) {
        this.size = size;
        if (size > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.last = page >= totalPages - 1 || totalPages == 0;
        }
    }
    
    /**
     * Gets the total number of elements.
     *
     * @return The total number of elements across all pages
     */
    public long getTotalElements() {
        return totalElements;
    }
    
    /**
     * Sets the total number of elements.
     * Recalculates totalPages based on the new total elements.
     *
     * @param totalElements The total number of elements across all pages
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        if (size > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.last = page >= totalPages - 1 || totalPages == 0;
        }
    }
    
    /**
     * Gets the total number of pages.
     *
     * @return The total number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }
    
    /**
     * Sets the total number of pages.
     * Updates the last flag based on the new total pages.
     *
     * @param totalPages The total number of pages
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        this.last = page >= totalPages - 1 || totalPages == 0;
    }
    
    /**
     * Checks if this is the last page.
     *
     * @return true if this is the last page, false otherwise
     */
    public boolean isLast() {
        return last;
    }
    
    /**
     * Sets whether this is the last page.
     *
     * @param last true if this is the last page, false otherwise
     */
    public void setLast(boolean last) {
        this.last = last;
    }
    
    /**
     * Checks if this is the first page.
     *
     * @return true if this is the first page, false otherwise
     */
    public boolean isFirst() {
        return first;
    }
    
    /**
     * Sets whether this is the first page.
     *
     * @param first true if this is the first page, false otherwise
     */
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    /**
     * Checks if the page is empty.
     *
     * @return true if the page is empty, false otherwise
     */
    public boolean isEmpty() {
        return empty;
    }
    
    /**
     * Sets whether the page is empty.
     *
     * @param empty true if the page is empty, false otherwise
     */
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    // TODO: Add additional utility methods for page navigation if needed
    
    // FIXME: Consider adding equals and hashCode methods for proper object comparison
}