package com.gradlemedium100.orderservice.service.impl;

import com.gradlemedium100.common.exception.ServiceException;
import com.gradlemedium100.notificationservice.service.NotificationService;
import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderStatus;
import com.gradlemedium100.orderservice.repository.OrderRepository;
import com.gradlemedium100.orderservice.service.CartService;
import com.gradlemedium100.orderservice.service.OrderService;
import com.gradlemedium100.orderservice.validation.OrderValidator;
import com.gradlemedium100.paymentservice.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the OrderService interface for managing orders.
 * This service handles order creation, retrieval, updates, and processing.
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderValidator orderValidator;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    
    /**
     * Constructor for OrderServiceImpl.
     *
     * @param orderRepository     Repository for order data access operations
     * @param cartService         Service for cart operations
     * @param orderValidator      Validator for order business rules
     * @param paymentService      Service for payment processing operations
     * @param notificationService Service for sending notifications
     */
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                           CartService cartService,
                           OrderValidator orderValidator,
                           PaymentService paymentService,
                           NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderValidator = orderValidator;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }
    
    /**
     * Creates a new order from a user's shopping cart.
     *
     * @param userId The ID of the user placing the order
     * @param cartId The ID of the shopping cart to convert into an order
     * @return The newly created order
     * @throws ServiceException if the cart is not found or cannot be converted to an order
     */
    @Override
    @Transactional
    public Order createOrder(Long userId, Long cartId) {
        try {
            logger.info("Creating order for user ID: {} from cart ID: {}", userId, cartId);
            
            // Get the user's cart
            var cart = cartService.getCartByUserId(userId);
            
            if (cart == null || !cart.getId().equals(cartId)) {
                throw new ServiceException("Cart not found or does not belong to the user");
            }
            
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new ServiceException("Cannot create order from empty cart");
            }
            
            // Convert cart to order
            Order order = cart.convertToOrder();
            order.setUserId(userId);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.CREATED);
            
            // Validate the order before saving
            orderValidator.validateOrder(order);
            
            // Save the order
            Order savedOrder = orderRepository.save(order);
            
            // Clear the cart after successful order creation
            cartService.clearCart(userId);
            
            // Send order confirmation
            sendOrderConfirmation(savedOrder);
            
            logger.info("Successfully created order ID: {} for user ID: {}", savedOrder.getId(), userId);
            return savedOrder;
        } catch (Exception e) {
            logger.error("Error creating order for user ID: {}", userId, e);
            throw new ServiceException("Failed to create order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Retrieves an order by its ID.
     *
     * @param orderId The ID of the order to retrieve
     * @return The requested order
     * @throws ServiceException if the order is not found
     */
    @Override
    public Order getOrderById(Long orderId) {
        logger.debug("Retrieving order with ID: {}", orderId);
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (!orderOpt.isPresent()) {
            logger.warn("Order not found with ID: {}", orderId);
            throw new ServiceException("Order not found with ID: " + orderId);
        }
        
        return orderOpt.get();
    }
    
    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId The ID of the user whose orders to retrieve
     * @return List of orders belonging to the user
     */
    @Override
    public List<Order> getUserOrders(Long userId) {
        logger.debug("Retrieving orders for user ID: {}", userId);
        return orderRepository.findByUserId(userId);
    }
    
    /**
     * Updates the status of an order.
     *
     * @param orderId The ID of the order to update
     * @param status  The new status to set
     * @return The updated order
     * @throws ServiceException if the order is not found or status transition is invalid
     */
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        logger.info("Updating status of order ID: {} to {}", orderId, status);
        
        Order order = getOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();
        
        // Validate status transition
        if (!orderValidator.validateOrderStatus(oldStatus, status)) {
            String message = String.format("Invalid order status transition from %s to %s", oldStatus, status);
            logger.warn(message);
            throw new ServiceException(message);
        }
        
        order.updateStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        // Notify user about status change
        sendOrderStatusUpdate(updatedOrder, oldStatus);
        
        logger.info("Successfully updated status of order ID: {} from {} to {}", orderId, oldStatus, status);
        return updatedOrder;
    }
    
    /**
     * Cancels an order if it's in a cancellable state.
     *
     * @param orderId The ID of the order to cancel
     * @return true if the order was successfully cancelled, false otherwise
     * @throws ServiceException if the order is not found or cannot be cancelled
     */
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId) {
        logger.info("Attempting to cancel order ID: {}", orderId);
        
        Order order = getOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();
        
        // Check if order can be cancelled
        if (currentStatus == OrderStatus.SHIPPED || 
            currentStatus == OrderStatus.DELIVERED || 
            currentStatus == OrderStatus.CANCELED || 
            currentStatus == OrderStatus.REFUNDED) {
            logger.warn("Cannot cancel order ID: {} with status: {}", orderId, currentStatus);
            return false;
        }
        
        // Update order status to CANCELED
        order.updateStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        
        // Send cancellation notification
        sendOrderStatusUpdate(order, currentStatus);
        
        logger.info("Successfully cancelled order ID: {}", orderId);
        return true;
    }
    
    /**
     * Processes payment for an order.
     *
     * @param orderId        The ID of the order to process payment for
     * @param paymentDetails Map containing payment details
     * @return true if payment was successfully processed, false otherwise
     * @throws ServiceException if the order is not found or payment fails
     */
    @Override
    @Transactional
    public boolean processPayment(Long orderId, Map<String, String> paymentDetails) {
        logger.info("Processing payment for order ID: {}", orderId);
        
        if (paymentDetails == null || paymentDetails.isEmpty()) {
            throw new ServiceException("Payment details cannot be empty");
        }
        
        Order order = getOrderById(orderId);
        
        // Ensure order is in a state where payment can be processed
        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            logger.warn("Cannot process payment for order ID: {} with status: {}", orderId, order.getStatus());
            throw new ServiceException("Cannot process payment for order with status: " + order.getStatus());
        }
        
        try {
            // Update order to payment pending if it's not already
            if (order.getStatus() == OrderStatus.CREATED) {
                order.updateStatus(OrderStatus.PAYMENT_PENDING);
                orderRepository.save(order);
            }
            
            // Process payment using payment service
            // FIXME: Need to handle payment failures and retries more robustly
            boolean paymentSuccessful = paymentService.processPayment(
                order.getId(), 
                order.getUserId(),
                order.getTotalAmount(),
                paymentDetails
            );
            
            if (paymentSuccessful) {
                // Update payment ID and order status
                if (paymentDetails.containsKey("paymentId")) {
                    order.setPaymentId(Long.valueOf(paymentDetails.get("paymentId")));
                }
                order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);
                orderRepository.save(order);
                
                sendOrderStatusUpdate(order, OrderStatus.PAYMENT_PENDING);
                logger.info("Payment successful for order ID: {}", orderId);
                return true;
            } else {
                logger.warn("Payment failed for order ID: {}", orderId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error processing payment for order ID: {}", orderId, e);
            throw new ServiceException("Payment processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Sends an order confirmation notification to the user.
     *
     * @param order The order to send confirmation for
     */
    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            logger.debug("Sending order confirmation for order ID: {}", order.getId());
            
            // TODO: Format notification content properly with order details and estimated delivery
            String subject = "Your Order Confirmation - Order #" + order.getId();
            String content = String.format(
                "Thank you for your order! Your order #%d has been received and is being processed. " +
                "Total amount: $%.2f",
                order.getId(),
                order.getTotalAmount()
            );
            
            notificationService.sendEmail(
                order.getUserId(),
                subject,
                content
            );
            
            logger.debug("Order confirmation sent successfully for order ID: {}", order.getId());
        } catch (Exception e) {
            // Don't let notification failure affect order processing
            logger.error("Failed to send order confirmation for order ID: {}", order.getId(), e);
        }
    }
    
    /**
     * Sends a notification about an order status change.
     *
     * @param order     The order with the updated status
     * @param oldStatus The previous status before the update
     */
    @Override
    public void sendOrderStatusUpdate(Order order, OrderStatus oldStatus) {
        try {
            logger.debug("Sending status update notification for order ID: {}", order.getId());
            
            String subject = "Order Status Update - Order #" + order.getId();
            String content = String.format(
                "The status of your order #%d has been updated from %s to %s.",
                order.getId(),
                oldStatus,
                order.getStatus()
            );
            
            // Add specific information based on new status
            switch (order.getStatus()) {
                case SHIPPED:
                    // TODO: Add tracking information when shipping integration is implemented
                    content += " Your order has been shipped and is on its way to you!";
                    break;
                case DELIVERED:
                    content += " Your order has been delivered. Thank you for shopping with us!";
                    break;
                case CANCELED:
                    content += " Your order has been canceled. Please contact customer service if you have any questions.";
                    break;
                case REFUNDED:
                    content += " Your payment has been refunded. It may take 3-5 business days to appear in your account.";
                    break;
                default:
                    // No additional information needed
            }
            
            notificationService.sendEmail(
                order.getUserId(),
                subject,
                content
            );
            
            // For important status updates, also send SMS if available
            if (order.getStatus() == OrderStatus.SHIPPED || 
                order.getStatus() == OrderStatus.DELIVERED) {
                // TODO: Implement SMS notification when phone number is available
                // FIXME: Need to handle cases where phone number might not be available
            }
            
            logger.debug("Status update notification sent successfully for order ID: {}", order.getId());
        } catch (Exception e) {
            // Don't let notification failure affect order processing
            logger.error("Failed to send status update notification for order ID: {}", order.getId(), e);
        }
    }
}