-- V1__init_schema.sql
-- Initial database schema creation script using Flyway migration
-- Created for gradlemedium100 project - Data Access Module
-- This script creates all necessary tables and relationships for the application

-- Create base tables with audit fields
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    -- Add indexes for frequently queried columns
    INDEX idx_product_name (name),
    INDEX idx_product_category (category),
    INDEX idx_product_price (price)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    billing_address TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    -- Foreign key constraint
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    
    -- Add indexes for frequently queried columns
    INDEX idx_order_user_id (user_id),
    INDEX idx_order_status (status),
    INDEX idx_order_date (order_date)
);

-- Many-to-many relationship between orders and products
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    
    -- Ensure a product can only appear once in an order
    UNIQUE KEY uk_order_product (order_id, product_id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    
    -- Foreign key constraint
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id),
    
    -- Add indexes for frequently queried columns
    INDEX idx_payment_order_id (order_id),
    INDEX idx_payment_status (status),
    INDEX idx_payment_method (payment_method)
);

-- Create audit trigger function for tracking updates
DELIMITER //
CREATE TRIGGER before_user_update 
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    SET NEW.version = OLD.version + 1;
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

CREATE TRIGGER before_product_update 
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    SET NEW.version = OLD.version + 1;
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

CREATE TRIGGER before_order_update 
BEFORE UPDATE ON orders
FOR EACH ROW
BEGIN
    SET NEW.version = OLD.version + 1;
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

CREATE TRIGGER before_payment_update 
BEFORE UPDATE ON payments
FOR EACH ROW
BEGIN
    SET NEW.version = OLD.version + 1;
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
DELIMITER ;

-- Create enumeration tables for status values
CREATE TABLE IF NOT EXISTS order_status_enum (
    status VARCHAR(20) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payment_status_enum (
    status VARCHAR(20) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payment_method_enum (
    method VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
);

-- Populate enumeration tables
INSERT INTO order_status_enum (status, description) VALUES
    ('PENDING', 'Order has been placed but not yet processed'),
    ('PROCESSING', 'Order is being processed'),
    ('SHIPPED', 'Order has been shipped'),
    ('DELIVERED', 'Order has been delivered'),
    ('CANCELLED', 'Order has been cancelled');

INSERT INTO payment_status_enum (status, description) VALUES
    ('PENDING', 'Payment is pending processing'),
    ('COMPLETED', 'Payment has been completed successfully'),
    ('FAILED', 'Payment processing failed'),
    ('REFUNDED', 'Payment has been refunded');

INSERT INTO payment_method_enum (method, description) VALUES
    ('CREDIT_CARD', 'Payment by credit card'),
    ('DEBIT_CARD', 'Payment by debit card'),
    ('PAYPAL', 'Payment through PayPal'),
    ('BANK_TRANSFER', 'Payment by bank transfer');

-- Create indexes for common queries
CREATE INDEX idx_product_active_price ON products (active, price);
CREATE INDEX idx_user_username_email ON users (username, email);
CREATE INDEX idx_order_date_status ON orders (order_date, status);
CREATE INDEX idx_payment_date_status ON payments (payment_date, status);

-- TODO: Add additional indexes based on query patterns once the application is in use
-- FIXME: Consider partitioning strategy for large tables (orders, payments) in production

-- Comments
-- This schema supports the core functionality of an e-commerce application
-- The version column in each table is used for optimistic locking in JPA
-- Timestamps are automatically managed for auditing purposes
-- Foreign key constraints ensure data integrity across related tables