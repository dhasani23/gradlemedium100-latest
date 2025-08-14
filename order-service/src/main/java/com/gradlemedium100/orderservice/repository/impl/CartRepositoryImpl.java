package com.gradlemedium100.orderservice.repository.impl;

import com.gradlemedium100.common.exception.DataAccessException;
import com.gradlemedium100.dataaccess.BaseRepository;
import com.gradlemedium100.orderservice.model.Cart;
import com.gradlemedium100.orderservice.model.CartItem;
import com.gradlemedium100.orderservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CartRepository interface for cart data access
 * This repository manages database operations for shopping carts
 */
@Repository
public class CartRepositoryImpl implements CartRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<Cart> cartMapper;

    private static final String SELECT_CART_BY_ID = 
            "SELECT id, user_id, created_date, last_updated, total_amount FROM carts WHERE id = ?";
    
    private static final String SELECT_CART_BY_USER_ID = 
            "SELECT id, user_id, created_date, last_updated, total_amount FROM carts WHERE user_id = ? ORDER BY last_updated DESC LIMIT 1";
    
    private static final String SELECT_ABANDONED_CARTS = 
            "SELECT id, user_id, created_date, last_updated, total_amount FROM carts WHERE last_updated < ?";
    
    private static final String INSERT_CART = 
            "INSERT INTO carts (user_id, created_date, last_updated, total_amount) VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_CART = 
            "UPDATE carts SET last_updated = ?, total_amount = ? WHERE id = ?";
    
    private static final String DELETE_CART = 
            "DELETE FROM carts WHERE id = ?";
    
    private static final String SELECT_CART_ITEMS = 
            "SELECT id, cart_id, product_id, product_name, quantity, unit_price, total_price, date_added FROM cart_items WHERE cart_id = ?";
    
    private static final String INSERT_CART_ITEM = 
            "INSERT INTO cart_items (cart_id, product_id, product_name, quantity, unit_price, total_price, date_added) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String DELETE_CART_ITEMS = 
            "DELETE FROM cart_items WHERE cart_id = ?";

    /**
     * Constructor for dependency injection
     * 
     * @param jdbcTemplate The Spring JDBC template
     */
    @Autowired
    public CartRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.cartMapper = this::mapResultSetToCart;
    }

    /**
     * Finds a shopping cart by its unique identifier
     *
     * @param id The cart ID
     * @return An Optional containing the cart if found, or empty if not found
     * @throws DataAccessException if there is an error accessing the database
     */
    @Override
    public Optional<Cart> findById(Long id) {
        try {
            Cart cart = jdbcTemplate.queryForObject(SELECT_CART_BY_ID, cartMapper, id);
            if (cart != null) {
                // Load cart items
                List<CartItem> cartItems = fetchCartItems(cart.getId());
                cart.getItems().addAll(cartItems);
                // Recalculate total amount
                cart.calculateTotal();
            }
            return Optional.ofNullable(cart);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DataAccessException("Error finding cart with ID " + id, e);
        }
    }

    /**
     * Finds the active shopping cart for a specific user
     *
     * @param userId The user ID
     * @return An Optional containing the user's active cart if found, or empty if not found
     * @throws DataAccessException if there is an error accessing the database
     */
    @Override
    public Optional<Cart> findByUserId(Long userId) {
        try {
            Cart cart = jdbcTemplate.queryForObject(SELECT_CART_BY_USER_ID, cartMapper, userId);
            if (cart != null) {
                // Load cart items
                List<CartItem> cartItems = fetchCartItems(cart.getId());
                cart.getItems().addAll(cartItems);
                // Recalculate total amount
                cart.calculateTotal();
            }
            return Optional.ofNullable(cart);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DataAccessException("Error finding cart for user ID " + userId, e);
        }
    }

    /**
     * Saves or updates a shopping cart in the database
     *
     * @param cart The cart to save or update
     * @return The saved or updated cart with populated ID if it was a new cart
     * @throws DataAccessException if there is an error accessing the database
     */
    @Override
    @Transactional
    public Cart save(Cart cart) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            if (cart.getId() == null) {
                // New cart - insert
                KeyHolder keyHolder = new GeneratedKeyHolder();
                
                jdbcTemplate.update(connection -> {
                    var ps = connection.prepareStatement(INSERT_CART, new String[] {"id"});
                    ps.setLong(1, cart.getUserId());
                    ps.setObject(2, now);
                    ps.setObject(3, now);
                    ps.setBigDecimal(4, cart.getTotalAmount() != null ? cart.getTotalAmount() : BigDecimal.ZERO);
                    return ps;
                }, keyHolder);
                
                Number key = keyHolder.getKey();
                if (key != null) {
                    cart.setId(key.longValue());
                } else {
                    throw new DataAccessException("Failed to generate ID for new cart");
                }
                
                cart.setCreatedDate(now);
            } else {
                // Existing cart - update
                jdbcTemplate.update(UPDATE_CART, now, cart.getTotalAmount(), cart.getId());
            }
            
            cart.setLastUpdated(now);
            
            // Handle cart items - delete existing and insert new
            if (cart.getId() != null) {
                // Delete existing items
                jdbcTemplate.update(DELETE_CART_ITEMS, cart.getId());
                
                // Insert all current items
                for (CartItem item : cart.getItems()) {
                    item.setCartId(cart.getId());
                    jdbcTemplate.update(
                        INSERT_CART_ITEM,
                        item.getCartId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice(),
                        item.getDateAdded() != null ? item.getDateAdded() : now
                    );
                }
            }
            
            return cart;
        } catch (Exception e) {
            throw new DataAccessException("Error saving cart: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a shopping cart by its ID
     *
     * @param cartId The ID of the cart to delete
     * @throws DataAccessException if there is an error accessing the database
     */
    @Override
    @Transactional
    public void delete(Long cartId) {
        try {
            // First delete all cart items
            jdbcTemplate.update(DELETE_CART_ITEMS, cartId);
            
            // Then delete the cart
            int rowsAffected = jdbcTemplate.update(DELETE_CART, cartId);
            
            if (rowsAffected == 0) {
                throw new DataAccessException("No cart found with ID " + cartId);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error deleting cart with ID " + cartId, e);
        }
    }

    /**
     * Finds carts that have not been updated since the specified date
     *
     * @param olderThan The date threshold for abandoned carts
     * @return A list of abandoned carts
     * @throws DataAccessException if there is an error accessing the database
     */
    @Override
    public List<Cart> findAbandonedCarts(LocalDateTime olderThan) {
        try {
            List<Cart> abandonedCarts = jdbcTemplate.query(SELECT_ABANDONED_CARTS, cartMapper, olderThan);
            
            // Load items for each cart
            for (Cart cart : abandonedCarts) {
                List<CartItem> cartItems = fetchCartItems(cart.getId());
                cart.getItems().addAll(cartItems);
                // Recalculate total amount
                cart.calculateTotal();
            }
            
            return abandonedCarts;
        } catch (Exception e) {
            throw new DataAccessException("Error finding abandoned carts", e);
        }
    }

    /**
     * Maps a database result set row to a Cart object
     *
     * @param rs The ResultSet containing cart data
     * @param rowNum The current row number
     * @return A populated Cart object
     * @throws SQLException if there is an error accessing the ResultSet
     */
    public Cart mapResultSetToCart(ResultSet rs, int rowNum) throws SQLException {
        Cart cart = new Cart();
        cart.setId(rs.getLong("id"));
        cart.setUserId(rs.getLong("user_id"));
        cart.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        cart.setLastUpdated(rs.getObject("last_updated", LocalDateTime.class));
        cart.setTotalAmount(rs.getBigDecimal("total_amount"));
        // Initialize empty items list - will be populated separately
        cart.setItems(new ArrayList<>());
        return cart;
    }

    /**
     * Fetches all items for a specific cart from the database
     *
     * @param cartId The ID of the cart
     * @return A list of cart items
     * @throws SQLException if there is an error accessing the database
     */
    public List<CartItem> fetchCartItems(Long cartId) {
        try {
            return jdbcTemplate.query(SELECT_CART_ITEMS, (rs, rowNum) -> {
                CartItem item = new CartItem();
                item.setId(rs.getLong("id"));
                item.setCartId(rs.getLong("cart_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setTotalPrice(rs.getBigDecimal("total_price"));
                item.setDateAdded(rs.getObject("date_added", LocalDateTime.class));
                return item;
            }, cartId);
        } catch (Exception e) {
            throw new DataAccessException("Error fetching cart items for cart ID " + cartId, e);
        }
    }
}