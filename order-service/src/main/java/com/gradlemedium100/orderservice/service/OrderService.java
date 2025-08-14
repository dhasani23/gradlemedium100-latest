package com.gradlemedium100.orderservice.service;

import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderStatus;
import com.gradlemedium100.common.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * Interface defining the core order management operations.
 * 
 * This service handles all aspects of order processing including creation,
 * retrieval, status updates, cancellation, and payment processing.
 */
public interface OrderService {
    
    /**
     * Creates a new order from a user's shopping cart.
     * 
     * @param userId the ID of the user placing the order
     * @param cartId the ID of the shopping cart to convert to an order
     * @return the newly created order
     * @throws ServiceException if the cart is empty, the cart doesn't belong to the user, 
     *                         or there are issues with item availability
     */
    Order createOrder(Long userId, Long cartId) throws ServiceException;
    
    /**
     * Retrieves an order by its ID.
     * 
     * @param orderId the unique identifier of the order to retrieve
     * @return the requested order
     * @throws ServiceException if the order doesn't exist or cannot be accessed
     */
    Order getOrderById(Long orderId) throws ServiceException;
    
    /**
     * Retrieves all orders for a specific user.
     * 
     * @param userId the ID of the user whose orders should be retrieved
     * @return a list of orders belonging to the user
     * @throws ServiceException if there are issues accessing the user's orders
     */
    List<Order> getUserOrders(Long userId) throws ServiceException;
    
    /**
     * Updates the status of an order.
     * 
     * @param orderId the ID of the order to update
     * @param status the new status to set
     * @return the updated order
     * @throws ServiceException if the order doesn't exist or the status transition is invalid
     */
    Order updateOrderStatus(Long orderId, OrderStatus status) throws ServiceException;
    
    /**
     * Cancels an order if it's in a cancellable state.
     * Orders can typically only be cancelled if they haven't been shipped yet.
     * 
     * @param orderId the ID of the order to cancel
     * @return true if the order was successfully cancelled, false otherwise
     * @throws ServiceException if the order doesn't exist or cannot be cancelled due to its current state
     */
    boolean cancelOrder(Long orderId) throws ServiceException;
    
    /**
     * Processes payment for an order.
     * 
     * @param orderId the ID of the order to process payment for
     * @param paymentDetails a map containing payment information such as payment method, card details, etc.
     * @return true if payment was successful, false otherwise
     * @throws ServiceException if the order doesn't exist, payment fails, or the order is not in a valid state for payment
     */
    boolean processPayment(Long orderId, Map<String, String> paymentDetails) throws ServiceException;
}