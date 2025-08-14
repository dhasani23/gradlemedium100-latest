package com.gradlemedium100.productservice.dto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO containing search criteria for product queries.
 * This class encapsulates all parameters used for filtering, sorting and 
 * pagination of product search results.
 */
public class ProductSearchCriteria {

    private String keyword;         // Keyword to search in product name and description
    private Long categoryId;        // ID of the category to filter products by
    private BigDecimal minPrice;    // Minimum price for price range filtering
    private BigDecimal maxPrice;    // Maximum price for price range filtering
    private String sortBy;          // Field by which to sort the results
    private String sortDirection;   // Sort direction (ASC or DESC)
    private Integer page;           // Page number for pagination
    private Integer pageSize;       // Page size for pagination
    
    /**
     * Default constructor initializing with null values.
     */
    public ProductSearchCriteria() {
        // Default constructor with no initialization
    }
    
    /**
     * Constructor with basic search parameters.
     * 
     * @param keyword Search keyword
     * @param categoryId Category filter
     * @param page Page number
     * @param pageSize Number of items per page
     */
    public ProductSearchCriteria(String keyword, Long categoryId, Integer page, Integer pageSize) {
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.page = page;
        this.pageSize = pageSize;
    }
    
    /**
     * Gets the search keyword.
     * 
     * @return The keyword to search in product name and description
     */
    public String getKeyword() {
        return keyword;
    }
    
    /**
     * Sets the search keyword.
     * 
     * @param keyword The keyword to search in product name and description
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    /**
     * Gets the category ID filter.
     * 
     * @return The ID of the category to filter products by
     */
    public Long getCategoryId() {
        return categoryId;
    }
    
    /**
     * Sets the category ID filter.
     * 
     * @param categoryId The ID of the category to filter products by
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    /**
     * Gets the minimum price filter.
     * 
     * @return The minimum price for price range filtering
     */
    public BigDecimal getMinPrice() {
        return minPrice;
    }
    
    /**
     * Sets the minimum price filter.
     * 
     * @param minPrice The minimum price for price range filtering
     */
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }
    
    /**
     * Gets the maximum price filter.
     * 
     * @return The maximum price for price range filtering
     */
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
    
    /**
     * Sets the maximum price filter.
     * 
     * @param maxPrice The maximum price for price range filtering
     */
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    /**
     * Gets the field by which to sort the results.
     * 
     * @return The sort field
     */
    public String getSortBy() {
        return sortBy;
    }
    
    /**
     * Sets the field by which to sort the results.
     * 
     * @param sortBy The sort field
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    /**
     * Gets the sort direction.
     * 
     * @return The sort direction (ASC or DESC)
     */
    public String getSortDirection() {
        return sortDirection;
    }
    
    /**
     * Sets the sort direction.
     * 
     * @param sortDirection The sort direction (ASC or DESC)
     */
    public void setSortDirection(String sortDirection) {
        // FIXME: Add validation to ensure value is either "ASC" or "DESC"
        this.sortDirection = sortDirection;
    }
    
    /**
     * Gets the page number for pagination.
     * 
     * @return The page number
     */
    public Integer getPage() {
        return page;
    }
    
    /**
     * Sets the page number for pagination.
     * 
     * @param page The page number
     */
    public void setPage(Integer page) {
        // TODO: Add validation to ensure page number is positive
        this.page = page;
    }
    
    /**
     * Gets the page size for pagination.
     * 
     * @return The page size
     */
    public Integer getPageSize() {
        return pageSize;
    }
    
    /**
     * Sets the page size for pagination.
     * 
     * @param pageSize The page size
     */
    public void setPageSize(Integer pageSize) {
        // TODO: Add validation for maximum page size to prevent excessive data requests
        this.pageSize = pageSize;
    }
    
    /**
     * Validates that the search criteria has valid pagination settings.
     * 
     * @return True if the pagination settings are valid
     */
    public boolean hasValidPagination() {
        return page != null && page >= 0 && pageSize != null && pageSize > 0;
    }
    
    /**
     * Checks if price range filtering should be applied.
     * 
     * @return True if either min or max price is specified
     */
    public boolean hasPriceFilter() {
        return minPrice != null || maxPrice != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductSearchCriteria that = (ProductSearchCriteria) o;
        
        return Objects.equals(keyword, that.keyword) &&
               Objects.equals(categoryId, that.categoryId) &&
               Objects.equals(minPrice, that.minPrice) &&
               Objects.equals(maxPrice, that.maxPrice) &&
               Objects.equals(sortBy, that.sortBy) &&
               Objects.equals(sortDirection, that.sortDirection) &&
               Objects.equals(page, that.page) &&
               Objects.equals(pageSize, that.pageSize);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(keyword, categoryId, minPrice, maxPrice, 
                          sortBy, sortDirection, page, pageSize);
    }
    
    @Override
    public String toString() {
        return "ProductSearchCriteria{" +
                "keyword='" + keyword + '\'' +
                ", categoryId=" + categoryId +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}