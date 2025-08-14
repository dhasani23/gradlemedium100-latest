package com.gradlemedium100.dataaccess.repository;

import com.gradlemedium100.dataaccess.entity.OrderEntity;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Repository interface for order-related database operations.
 * Extends the BaseRepository to inherit common CRUD operations for OrderEntity objects.
 * Provides additional specialized methods for retrieving and manipulating order data.
 */
public interface OrderRepository extends BaseRepository<OrderEntity> {

    /**
     * Find an order by its unique order number.
     * Order numbers are business identifiers that are different from the technical primary key.
     *
     * @param orderNumber The unique order number to search for
     * @return The matching OrderEntity or null if not found
     */
    OrderEntity findByOrderNumber(String orderNumber);

    /**
     * Find all orders for a specific user.
     * Orders are returned in descending order by order date (most recent first).
     *
     * @param userId The ID of the user whose orders to find
     * @return A List of OrderEntity objects belonging to the specified user
     */
    List<OrderEntity> findByUserId(Long userId);

    /**
     * Find orders by their status.
     * Useful for querying orders in a particular state (e.g., PENDING, SHIPPED, DELIVERED).
     *
     * @param status The order status to search for
     * @return A List of OrderEntity objects with the specified status
     */
    List<OrderEntity> findByStatus(String status);

    /**
     * Find orders created within a specified date range.
     * The query is inclusive of the start and end dates.
     *
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return A List of OrderEntity objects created within the date range
     */
    List<OrderEntity> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Update the status of an order.
     * This method handles the business logic validation for status transitions.
     *
     * @param orderId The ID of the order to update
     * @param status The new status value
     * @throws RuntimeException if there's an error during the update process
     * @throws IllegalStateException if the status transition is not allowed
     */
    void updateOrderStatus(Long orderId, String status);
    
    // TODO: Add method to find recently placed orders within a time window
    
    // FIXME: Consider adding pagination support for methods that return large result sets
}