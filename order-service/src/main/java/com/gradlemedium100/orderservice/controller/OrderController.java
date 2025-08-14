package com.gradlemedium100.orderservice.controller;

import com.gradlemedium100.common.dto.OrderDTO;
import com.gradlemedium100.common.exception.ControllerException;
import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderStatus;
import com.gradlemedium100.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for order management operations
 * Handles API endpoints related to order processing and management
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    /**
     * REST endpoint to create a new order from a shopping cart
     *
     * @param userId the ID of the user placing the order
     * @param cartId the ID of the cart to convert to an order
     * @return ResponseEntity containing the created OrderDTO
     */
    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestParam Long userId, @RequestParam Long cartId) {
        logger.info("Creating order for user {} from cart {}", userId, cartId);
        
        try {
            Order order = orderService.createOrder(userId, cartId);
            return new ResponseEntity<>(convertToDTO(order), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw new ControllerException("Failed to create order: " + e.getMessage(), e);
        }
    }
    
    /**
     * REST endpoint to get an order by its ID
     *
     * @param orderId the ID of the order to retrieve
     * @return ResponseEntity containing the OrderDTO
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        logger.info("Fetching order with ID: {}", orderId);
        
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(convertToDTO(order));
        } catch (Exception e) {
            logger.error("Error fetching order {}: {}", orderId, e.getMessage(), e);
            throw new ControllerException("Failed to fetch order: " + e.getMessage(), e);
        }
    }
    
    /**
     * REST endpoint to get all orders for a user
     *
     * @param userId the ID of the user whose orders to retrieve
     * @return ResponseEntity containing a list of OrderDTOs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        logger.info("Fetching all orders for user: {}", userId);
        
        try {
            List<Order> orders = orderService.getUserOrders(userId);
            List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        } catch (Exception e) {
            logger.error("Error fetching orders for user {}: {}", userId, e.getMessage(), e);
            throw new ControllerException("Failed to fetch user orders: " + e.getMessage(), e);
        }
    }
    
    /**
     * REST endpoint to update an order's status
     *
     * @param orderId the ID of the order to update
     * @param status the new status of the order
     * @return ResponseEntity containing the updated OrderDTO
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        logger.info("Updating order {} status to: {}", orderId, status);
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(orderId, orderStatus);
            return ResponseEntity.ok(convertToDTO(updatedOrder));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status: {}", status);
            throw new ControllerException("Invalid order status: " + status);
        } catch (Exception e) {
            logger.error("Error updating order status for order {}: {}", orderId, e.getMessage(), e);
            throw new ControllerException("Failed to update order status: " + e.getMessage(), e);
        }
    }
    
    /**
     * REST endpoint to cancel an order
     *
     * @param orderId the ID of the order to cancel
     * @return ResponseEntity containing a Boolean indicating success or failure
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable Long orderId) {
        logger.info("Cancelling order with ID: {}", orderId);
        
        try {
            boolean canceled = orderService.cancelOrder(orderId);
            if (canceled) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        } catch (Exception e) {
            logger.error("Error cancelling order {}: {}", orderId, e.getMessage(), e);
            throw new ControllerException("Failed to cancel order: " + e.getMessage(), e);
        }
    }
    
    /**
     * REST endpoint to process payment for an order
     *
     * @param orderId the ID of the order to process payment for
     * @param paymentDetails a map containing payment details
     * @return ResponseEntity containing a Boolean indicating success or failure
     */
    @PostMapping("/{orderId}/payment")
    public ResponseEntity<Boolean> processPayment(
            @PathVariable Long orderId, 
            @RequestBody Map<String, String> paymentDetails) {
        logger.info("Processing payment for order: {}", orderId);
        
        try {
            // FIXME: Add validation for payment details
            boolean paymentProcessed = orderService.processPayment(orderId, paymentDetails);
            if (paymentProcessed) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(false);
            }
        } catch (Exception e) {
            logger.error("Error processing payment for order {}: {}", orderId, e.getMessage(), e);
            throw new ControllerException("Failed to process payment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts an Order entity to a DTO for API responses
     *
     * @param order the Order entity to convert
     * @return the OrderDTO representation
     */
    protected OrderDTO convertToDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        // TODO: Consider using a mapper library like ModelMapper or MapStruct
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId().toString());
        dto.setUserId(order.getUserId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().toString());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        
        // Convert order items
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                .map(item -> {
                    OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTO.setTotalPrice(item.getTotalPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }
        
        dto.setPaymentId(order.getPaymentId());
        dto.setCreatedAt(LocalDateTime.now()); // This should ideally come from the entity
        
        return dto;
    }
    
    /**
     * Global exception handler for the controller
     *
     * @param ex the exception to handle
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        logger.error("Unhandled exception in OrderController: {}", ex.getMessage(), ex);
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (ex instanceof ControllerException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}