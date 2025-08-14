package com.gradlemedium100.dataaccess.repository.impl;

import com.gradlemedium100.dataaccess.entity.OrderEntity;
import com.gradlemedium100.dataaccess.repository.OrderRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of the order repository interface that provides database operations
 * for order-related entities. Extends the BaseRepositoryImpl for common CRUD operations.
 */
@Repository
@Transactional
public class OrderRepositoryImpl extends BaseRepositoryImpl<OrderEntity> implements OrderRepository {

    private static final Logger LOGGER = Logger.getLogger(OrderRepositoryImpl.class.getName());

    /**
     * Constructor that initializes the repository with the entity manager and OrderEntity class type.
     * 
     * @param entityManager The JPA entity manager
     */
    @Autowired
    public OrderRepositoryImpl(EntityManager entityManager) {
        super(entityManager, OrderEntity.class);
    }

    /**
     * Find an order by its unique order number.
     * 
     * @param orderNumber The unique order number
     * @return The matching order entity or null if not found
     */
    @Override
    @Transactional(readOnly = true)
    public OrderEntity findByOrderNumber(String orderNumber) {
        try {
            TypedQuery<OrderEntity> query = entityManager.createQuery(
                    "SELECT o FROM OrderEntity o WHERE o.orderNumber = :orderNumber", OrderEntity.class);
            query.setParameter("orderNumber", orderNumber);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.log(Level.INFO, "No order found with order number: {0}", orderNumber);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding order by order number: " + orderNumber, e);
            return null;
        }
    }

    /**
     * Find all orders for a specific user.
     * 
     * @param userId The user ID
     * @return A list of orders for the specified user
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findByUserId(Long userId) {
        try {
            TypedQuery<OrderEntity> query = entityManager.createQuery(
                    "SELECT o FROM OrderEntity o WHERE o.user.id = :userId ORDER BY o.orderDate DESC", OrderEntity.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding orders by user ID: " + userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Find orders by their status.
     * 
     * @param status The order status
     * @return A list of orders with the specified status
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findByStatus(String status) {
        try {
            TypedQuery<OrderEntity> query = entityManager.createQuery(
                    "SELECT o FROM OrderEntity o WHERE o.status = :status", OrderEntity.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding orders by status: " + status, e);
            return Collections.emptyList();
        }
    }

    /**
     * Find orders created within a specified date range.
     * 
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return A list of orders created within the date range
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // FIXME: Consider using orderDate instead of createdAt if business requirements specify
            TypedQuery<OrderEntity> query = entityManager.createQuery(
                    "SELECT o FROM OrderEntity o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate",
                    OrderEntity.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error finding orders between dates: " + startDate + " and " + endDate, e);
            return Collections.emptyList();
        }
    }

    /**
     * Update the status of an order.
     * 
     * @param orderId The ID of the order to update
     * @param status The new status value
     */
    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        try {
            findById(orderId).ifPresent(order -> {
                // Check if the status transition is valid based on business rules
                validateStatusTransition(order.getStatus(), status);
                
                order.setStatus(status);
                // The updated timestamp will be updated by the preUpdate callback in BaseEntity
                entityManager.merge(order);
                LOGGER.log(Level.INFO, "Order status updated for order ID {0}: {1}", new Object[]{orderId, status});
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating order status for order ID: " + orderId, e);
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    /**
     * Validate that the status transition is allowed according to business rules.
     * 
     * @param currentStatus The current status of the order
     * @param newStatus The requested new status
     * @throws IllegalStateException if the transition is not allowed
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // TODO: Implement business rules for valid status transitions
        // This is a simplified example - real implementation would have more complex rules
        
        // For example: PENDING -> SHIPPED -> DELIVERED is valid
        // But DELIVERED -> PENDING is invalid
        
        if ("DELIVERED".equals(currentStatus)) {
            if (!"DELIVERED".equals(newStatus)) {
                throw new IllegalStateException("Cannot change status from DELIVERED to " + newStatus);
            }
        }
        
        // Add more validation rules based on business requirements
    }
    
    /**
     * Count orders by status.
     * This is a helper method not defined in the interface, but useful for reporting.
     * 
     * @param status The order status to count
     * @return The number of orders with the specified status
     */
    public Long countOrdersByStatus(String status) {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(o) FROM OrderEntity o WHERE o.status = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting orders by status: " + status, e);
            return 0L;
        }
    }
}