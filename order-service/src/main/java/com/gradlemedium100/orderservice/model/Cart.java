package com.gradlemedium100.orderservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entity representing a user's shopping cart with items before conversion to an order.
 * Manages the lifecycle of shopping cart items and calculations.
 */
public class Cart {
    
    private Long id;
    private Long userId;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
    private List<CartItem> items;
    private BigDecimal totalAmount;
    
    /**
     * Default constructor
     */
    public Cart() {
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = this.createdDate;
    }
    
    /**
     * Parameterized constructor for creating a cart with required fields
     * 
     * @param userId The ID of the user who owns the cart
     */
    public Cart(Long userId) {
        this();
        this.userId = userId;
    }
    
    /**
     * Adds an item to the cart or increments quantity if it already exists.
     * Updates the last updated timestamp and recalculates the total.
     * 
     * @param item The cart item to add
     */
    public void addItem(CartItem item) {
        if (item == null) {
            return;
        }
        
        // Check if the item already exists in the cart (same product ID)
        boolean itemExists = false;
        for (CartItem existingItem : items) {
            if (Objects.equals(existingItem.getProductId(), item.getProductId())) {
                // Item already exists, update quantity
                int newQuantity = existingItem.getQuantity() + item.getQuantity();
                existingItem.updateQuantity(newQuantity);
                itemExists = true;
                break;
            }
        }
        
        // If item doesn't exist, add it to the cart
        if (!itemExists) {
            // Set the cartId if it's not already set and the cart has an ID
            if (this.id != null && item.getCartId() == null) {
                item.setCartId(this.id);
            }
            items.add(item);
        }
        
        // Update the last updated timestamp
        this.lastUpdated = LocalDateTime.now();
        
        // Recalculate total amount
        calculateTotal();
    }
    
    /**
     * Updates the quantity of an item in the cart.
     * Returns true if the item was found and updated, false otherwise.
     * 
     * @param productId The ID of the product to update
     * @param quantity The new quantity
     * @return true if the item was found and updated, false otherwise
     */
    public boolean updateItemQuantity(Long productId, Integer quantity) {
        if (productId == null || quantity == null || quantity < 1) {
            return false;
        }
        
        boolean updated = false;
        for (CartItem item : items) {
            if (Objects.equals(item.getProductId(), productId)) {
                item.updateQuantity(quantity);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            // Update the last updated timestamp
            this.lastUpdated = LocalDateTime.now();
            
            // Recalculate total amount
            calculateTotal();
        }
        
        return updated;
    }
    
    /**
     * Removes an item from the cart.
     * Returns true if the item was found and removed, false otherwise.
     * 
     * @param productId The ID of the product to remove
     * @return true if the item was found and removed, false otherwise
     */
    public boolean removeItem(Long productId) {
        if (productId == null) {
            return false;
        }
        
        Iterator<CartItem> iterator = items.iterator();
        boolean removed = false;
        
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (Objects.equals(item.getProductId(), productId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (removed) {
            // Update the last updated timestamp
            this.lastUpdated = LocalDateTime.now();
            
            // Recalculate total amount
            calculateTotal();
        }
        
        return removed;
    }
    
    /**
     * Removes all items from the cart.
     */
    public void clearCart() {
        if (items != null && !items.isEmpty()) {
            items.clear();
            
            // Update the last updated timestamp
            this.lastUpdated = LocalDateTime.now();
            
            // Reset total amount
            this.totalAmount = BigDecimal.ZERO;
        }
    }
    
    /**
     * Calculates the total amount of all items in the cart.
     * 
     * @return The calculated total amount
     */
    public BigDecimal calculateTotal() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            return this.totalAmount;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getTotalPrice());
        }
        
        this.totalAmount = total;
        return total;
    }
    
    /**
     * Converts the cart to an order entity.
     * The cart remains unchanged after this operation.
     * 
     * @return A new Order entity initialized with data from this cart
     */
    public Order convertToOrder() {
        Order order = new Order();
        order.setUserId(this.userId);
        
        // Convert CartItems to OrderItems
        List<OrderItem> orderItems = items.stream()
            .map(CartItem::toOrderItem)
            .collect(Collectors.toList());
        
        order.setItems(orderItems);
        
        // Set the order date to the current time
        order.setOrderDate(LocalDateTime.now());
        
        // Set the status to CREATED
        order.setStatus(OrderStatus.CREATED);
        
        // The total amount will be calculated automatically when setting items
        
        return order;
    }

    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        
        // If cart has an ID, set it for all items that don't have a cartId
        if (this.id != null) {
            for (CartItem item : this.items) {
                if (item.getCartId() == null) {
                    item.setCartId(this.id);
                }
            }
        }
        
        // Recalculate total when items are set
        calculateTotal();
        
        // Update the last updated timestamp
        this.lastUpdated = LocalDateTime.now();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    /**
     * Returns the number of items in the cart.
     * 
     * @return The number of distinct items in the cart
     */
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    
    /**
     * Returns the total quantity of all items in the cart.
     * 
     * @return The total quantity of all items
     */
    public int getTotalQuantity() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        
        int totalQuantity = 0;
        for (CartItem item : items) {
            totalQuantity += item.getQuantity();
        }
        
        return totalQuantity;
    }
    
    /**
     * Checks if the cart contains an item with the given product ID.
     * 
     * @param productId The product ID to check
     * @return true if the cart contains an item with the given product ID, false otherwise
     */
    public boolean containsItem(Long productId) {
        if (productId == null || items == null || items.isEmpty()) {
            return false;
        }
        
        for (CartItem item : items) {
            if (Objects.equals(item.getProductId(), productId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the cart item with the given product ID.
     * 
     * @param productId The product ID to look for
     * @return The CartItem if found, null otherwise
     */
    public CartItem getItemByProductId(Long productId) {
        if (productId == null || items == null || items.isEmpty()) {
            return null;
        }
        
        for (CartItem item : items) {
            if (Objects.equals(item.getProductId(), productId)) {
                return item;
            }
        }
        
        return null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", createdDate=" + createdDate +
                ", lastUpdated=" + lastUpdated +
                ", itemCount=" + getItemCount() +
                ", totalQuantity=" + getTotalQuantity() +
                ", totalAmount=" + totalAmount +
                '}';
    }
}