package com.gradlemedium100.orderservice.repository.impl;

import com.gradlemedium100.common.exception.DataAccessException;
import com.gradlemedium100.dataaccess.BaseRepository;
import com.gradlemedium100.orderservice.model.Order;
import com.gradlemedium100.orderservice.model.OrderItem;
import com.gradlemedium100.orderservice.model.OrderStatus;
import com.gradlemedium100.orderservice.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the OrderRepository interface for order data access.
 * This class handles all database operations related to orders including
 * CRUD operations and custom queries.
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository, BaseRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Order> orderMapper;

    private static final String FIND_BY_ID_SQL = 
        "SELECT * FROM orders WHERE order_id = ?";
    
    private static final String FIND_BY_USER_ID_SQL = 
        "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
    
    private static final String FIND_BY_STATUS_SQL = 
        "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";
    
    private static final String INSERT_ORDER_SQL = 
        "INSERT INTO orders (user_id, status, total_amount, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ORDER_SQL = 
        "UPDATE orders SET user_id = ?, status = ?, total_amount = ?, updated_at = ? WHERE order_id = ?";
    
    private static final String DELETE_ORDER_SQL = 
        "DELETE FROM orders WHERE order_id = ?";
    
    private static final String FIND_BY_DATE_RANGE_SQL = 
        "SELECT * FROM orders WHERE created_at BETWEEN ? AND ? ORDER BY created_at DESC";
    
    private static final String FIND_ORDER_ITEMS_SQL = 
        "SELECT * FROM order_items WHERE order_id = ?";

    @Autowired
    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderMapper = this::mapResultSetToOrder;
    }

    /**
     * Finds an order by its unique identifier.
     *
     * @param id the order ID
     * @return an Optional containing the order if found, empty otherwise
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public Optional<Order> findById(Long id) {
        try {
            Order order = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, orderMapper, id);
            if (order != null) {
                // Fetch order items for this order
                List<OrderItem> orderItems = fetchOrderItems(id);
                order.setItems(orderItems);
            }
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DataAccessException("Error finding order by ID: " + id, e);
        }
    }

    /**
     * Finds all orders for a specific user.
     *
     * @param userId the user ID
     * @return a list of orders belonging to the user
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public List<Order> findByUserId(Long userId) {
        try {
            List<Order> orders = jdbcTemplate.query(FIND_BY_USER_ID_SQL, orderMapper, userId);
            
            // Fetch order items for each order
            for (Order order : orders) {
                List<OrderItem> orderItems = fetchOrderItems(order.getId());
                order.setItems(orderItems);
            }
            
            return orders;
        } catch (Exception e) {
            throw new DataAccessException("Error finding orders for user: " + userId, e);
        }
    }

    /**
     * Finds all orders with a specific status.
     *
     * @param status the order status
     * @return a list of orders with the specified status
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        try {
            List<Order> orders = jdbcTemplate.query(FIND_BY_STATUS_SQL, orderMapper, status.name());
            
            // Fetch order items for each order
            for (Order order : orders) {
                List<OrderItem> orderItems = fetchOrderItems(order.getId());
                order.setItems(orderItems);
            }
            
            return orders;
        } catch (Exception e) {
            throw new DataAccessException("Error finding orders with status: " + status, e);
        }
    }

    /**
     * Saves or updates an order in the database.
     *
     * @param order the order to save or update
     * @return the saved order with updated ID (for new orders)
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public Order save(Order order) {
        try {
            if (order.getId() == null) {
                // Insert new order
                KeyHolder keyHolder = new GeneratedKeyHolder();
                
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_ORDER_SQL, 
                        Statement.RETURN_GENERATED_KEYS);
                    ps.setLong(1, order.getUserId());
                    ps.setString(2, order.getStatus().name());
                    ps.setDouble(3, order.getTotalAmount());
                    ps.setTimestamp(4, Timestamp.valueOf(order.getCreatedAt() != null ? 
                        order.getCreatedAt() : LocalDateTime.now()));
                    ps.setTimestamp(5, Timestamp.valueOf(order.getUpdatedAt() != null ? 
                        order.getUpdatedAt() : LocalDateTime.now()));
                    return ps;
                }, keyHolder);
                
                // Set the generated ID back to the order
                Number key = keyHolder.getKey();
                if (key != null) {
                    order.setId(key.longValue());
                } else {
                    throw new DataAccessException("Failed to get generated key for new order");
                }
                
                // Save order items
                saveOrderItems(order);
                
            } else {
                // Update existing order
                int rowsAffected = jdbcTemplate.update(UPDATE_ORDER_SQL,
                    order.getUserId(),
                    order.getStatus().name(),
                    order.getTotalAmount(),
                    Timestamp.valueOf(order.getUpdatedAt() != null ? 
                        order.getUpdatedAt() : LocalDateTime.now()),
                    order.getId()
                );
                
                if (rowsAffected == 0) {
                    throw new DataAccessException("No order found with ID: " + order.getId());
                }
                
                // Update order items (delete and recreate for simplicity)
                deleteOrderItems(order.getId());
                saveOrderItems(order);
            }
            
            return order;
        } catch (Exception e) {
            throw new DataAccessException("Error saving order: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to save order items for an order.
     * 
     * @param order the order containing items to save
     * @throws DataAccessException if a database error occurs
     */
    private void saveOrderItems(Order order) {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                jdbcTemplate.update(
                    "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)",
                    order.getId(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice()
                );
            }
        }
    }
    
    /**
     * Helper method to delete all order items for an order.
     * 
     * @param orderId the ID of the order whose items should be deleted
     * @throws DataAccessException if a database error occurs
     */
    private void deleteOrderItems(Long orderId) {
        jdbcTemplate.update("DELETE FROM order_items WHERE order_id = ?", orderId);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderId the ID of the order to delete
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public void delete(Long orderId) {
        try {
            // First delete associated order items
            deleteOrderItems(orderId);
            
            // Then delete the order
            int rowsAffected = jdbcTemplate.update(DELETE_ORDER_SQL, orderId);
            
            if (rowsAffected == 0) {
                throw new DataAccessException("No order found with ID: " + orderId);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error deleting order: " + e.getMessage(), e);
        }
    }

    /**
     * Finds orders created within a specified date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return a list of orders created within the specified date range
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public List<Order> findOrdersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Order> orders = jdbcTemplate.query(
                FIND_BY_DATE_RANGE_SQL, 
                orderMapper, 
                Timestamp.valueOf(startDate),
                Timestamp.valueOf(endDate)
            );
            
            // Fetch order items for each order
            for (Order order : orders) {
                List<OrderItem> orderItems = fetchOrderItems(order.getId());
                order.setItems(orderItems);
            }
            
            return orders;
        } catch (Exception e) {
            throw new DataAccessException(
                "Error finding orders between dates: " + startDate + " and " + endDate,
                e
            );
        }
    }

    /**
     * Maps a database result set row to an Order object.
     *
     * @param rs the result set
     * @param rowNum the row number
     * @return the mapped Order object
     * @throws SQLException if a database error occurs
     */
    @Override
    public Order mapResultSetToOrder(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("order_id"));
        order.setUserId(rs.getLong("user_id"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setTotalAmount(rs.getDouble("total_amount"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return order;
    }

    /**
     * Fetches all items for a specific order from the database.
     *
     * @param orderId the order ID
     * @return a list of order items for the order
     * @throws DataAccessException if a database error occurs
     */
    @Override
    public List<OrderItem> fetchOrderItems(Long orderId) {
        try {
            return jdbcTemplate.query(FIND_ORDER_ITEMS_SQL, (rs, rowNum) -> {
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("item_id"));
                item.setOrderId(rs.getLong("order_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                return item;
            }, orderId);
        } catch (Exception e) {
            // Log the error but don't fail the entire order retrieval
            // FIXME: Add proper logging instead of system out
            System.err.println("Error fetching order items for order " + orderId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}