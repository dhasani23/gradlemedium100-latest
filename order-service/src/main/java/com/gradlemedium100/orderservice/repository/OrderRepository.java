package com.gradlemedium100.orderservice.repository;

import com.gradlemedium100.common.exception.DataAccessException;
import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface for data access operations on orders.
 * Provides methods for retrieving, saving, and deleting order data.
 */
public interface OrderRepository {
    
    /**
     * Finds an order by its unique identifier.
     *
     * @param id the order ID to search for
     * @return an Optional containing the order if found, or empty if not found
     * @throws DataAccessException if there's an error during data access
     */
    Optional<Order> findById(Long id) throws DataAccessException;
    
    /**
     * Finds all orders for a specific user.
     *
     * @param userId the user ID to search orders for
     * @return a list of orders belonging to the specified user, or an empty list if none found
     * @throws DataAccessException if there's an error during data access
     */
    List<Order> findByUserId(Long userId) throws DataAccessException;
    
    /**
     * Finds all orders with a specific status.
     *
     * @param status the order status to search for
     * @return a list of orders with the specified status, or an empty list if none found
     * @throws DataAccessException if there's an error during data access
     */
    List<Order> findByStatus(OrderStatus status) throws DataAccessException;
    
    /**
     * Saves or updates an order in the database.
     * If the order ID is null, it creates a new order record.
     * If the order ID exists, it updates the existing record.
     *
     * @param order the order to save or update
     * @return the saved or updated order with its ID populated
     * @throws DataAccessException if there's an error during data access
     */
    Order save(Order order) throws DataAccessException;
    
    /**
     * Deletes an order by its ID.
     *
     * @param orderId the ID of the order to delete
     * @throws DataAccessException if there's an error during data access or the order doesn't exist
     */
    void delete(Long orderId) throws DataAccessException;
    
    /**
     * Finds orders created within a specified date range.
     *
     * @param startDate the beginning of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return a list of orders created within the specified date range, or an empty list if none found
     * @throws DataAccessException if there's an error during data access
     */
    List<Order> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) throws DataAccessException;
    
    /**
     * Counts the number of orders with a specific status.
     * This can be useful for dashboard statistics.
     *
     * @param status the order status to count
     * @return the count of orders with the specified status
     * @throws DataAccessException if there's an error during data access
     */
    default long countByStatus(OrderStatus status) throws DataAccessException {
        // Default implementation just gets all and counts
        // Implementations should override with a more efficient approach
        return findByStatus(status).size();
    }
    
    /**
     * Finds orders that need attention, such as stalled orders or orders with payment issues.
     * The specific criteria for "needing attention" may vary by implementation.
     *
     * @return a list of orders that need attention
     * @throws DataAccessException if there's an error during data access
     */
    default List<Order> findOrdersNeedingAttention() throws DataAccessException {
        // TODO: Implement specific business logic for identifying orders needing attention
        throw new UnsupportedOperationException("Method findOrdersNeedingAttention not implemented");
    }
    
    /**
     * Updates the status of an order.
     * This is a convenience method that allows updating just the status without retrieving the full order.
     *
     * @param orderId the ID of the order to update
     * @param newStatus the new status to set
     * @return true if the order was found and updated, false otherwise
     * @throws DataAccessException if there's an error during data access
     */
    default boolean updateOrderStatus(Long orderId, OrderStatus newStatus) throws DataAccessException {
        // Default implementation uses findById and save
        // Implementations should override with a more efficient approach
        Optional<Order> orderOpt = findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(newStatus);
            save(order);
            return true;
        }
        return false;
    }
}