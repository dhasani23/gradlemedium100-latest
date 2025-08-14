-- V2__sample_data.sql
-- Sample data initialization script using Flyway migration
-- Created for gradlemedium100 project - Data Access Module

-- Disable foreign key checks temporarily for easier loading
SET FOREIGN_KEY_CHECKS = 0;

-- Clean up any existing data to start fresh
-- Note: In a production environment, this would typically not be included in a migration script
-- TRUNCATE TABLE payments;
-- TRUNCATE TABLE order_items;
-- TRUNCATE TABLE orders;
-- TRUNCATE TABLE products;
-- TRUNCATE TABLE users;

-- Insert sample users
INSERT INTO users (id, username, email, password_hash, first_name, last_name, active, last_login, created_at, updated_at, version) VALUES
(1, 'john.doe', 'john.doe@example.com', '$2a$10$Yh.rKd/6UBjBKsKwHjS3yePEF7CZ.iYT9XA4yPf5cWVThR3GQB33C', 'John', 'Doe', true, '2023-01-10 08:30:00', '2023-01-01 10:00:00', '2023-01-10 08:30:00', 1),
(2, 'jane.smith', 'jane.smith@example.com', '$2a$10$8K1p/a1JIUA1gLjmtJ6B6O8JmS4qdyWzh1R7Oru7n1bwOoJHR5WRe', 'Jane', 'Smith', true, '2023-01-12 14:45:00', '2023-01-02 11:30:00', '2023-01-12 14:45:00', 2),
(3, 'michael.brown', 'michael.brown@example.com', '$2a$10$XY7Hl8KI9pj6hlGiCuXYp.pS7nNPfdW.WrP1XNvLyM9tRXHQhA74G', 'Michael', 'Brown', true, '2023-01-09 17:20:00', '2023-01-03 09:15:00', '2023-01-09 17:20:00', 1),
(4, 'sarah.johnson', 'sarah.johnson@example.com', '$2a$10$LBStB7zlyG1nnSgkpJJJW.ZYFJHCAx.zZTODU1MD8XjZVgFc5JI.e', 'Sarah', 'Johnson', true, '2023-01-11 10:10:00', '2023-01-04 16:45:00', '2023-01-11 10:10:00', 3),
(5, 'david.wilson', 'david.wilson@example.com', '$2a$10$pEZYBP5Hd/1Xw9e1LtCPzeCu4TI0X4CU1K9JJGKEJJLNg4.y2kpIq', 'David', 'Wilson', false, '2022-12-28 09:00:00', '2022-12-15 13:30:00', '2023-01-05 09:00:00', 4);

-- Insert sample products
INSERT INTO products (id, name, description, sku, price, stock_quantity, category, active, created_at, updated_at, version) VALUES
(1, 'Smartphone X', 'Latest smartphone with advanced features', 'TECH-1001', 799.99, 50, 'Electronics', true, '2023-01-01 09:00:00', '2023-01-01 09:00:00', 0),
(2, 'Laptop Pro', 'Professional laptop with high performance', 'TECH-2002', 1299.99, 25, 'Electronics', true, '2023-01-01 09:05:00', '2023-01-01 09:05:00', 0),
(3, 'Wireless Headphones', 'Noise-cancelling wireless headphones', 'TECH-3003', 149.99, 100, 'Electronics', true, '2023-01-01 09:10:00', '2023-01-01 09:10:00', 0),
(4, 'Cotton T-Shirt', 'Comfortable cotton t-shirt', 'CLOTH-1001', 24.99, 200, 'Clothing', true, '2023-01-01 09:15:00', '2023-01-01 09:15:00', 0),
(5, 'Denim Jeans', 'Classic denim jeans', 'CLOTH-2002', 59.99, 150, 'Clothing', true, '2023-01-01 09:20:00', '2023-01-01 09:20:00', 0),
(6, 'Running Shoes', 'Performance running shoes', 'SHOE-1001', 89.99, 75, 'Footwear', true, '2023-01-01 09:25:00', '2023-01-01 09:25:00', 0),
(7, 'Coffee Maker', 'Automatic coffee maker with timer', 'HOME-1001', 79.99, 30, 'Home Appliances', true, '2023-01-01 09:30:00', '2023-01-01 09:30:00', 0),
(8, 'Blender Pro', 'High-speed blender for smoothies', 'HOME-2002', 49.99, 40, 'Home Appliances', true, '2023-01-01 09:35:00', '2023-01-01 09:35:00', 0),
(9, 'Yoga Mat', 'Non-slip yoga mat', 'FITNESS-1001', 29.99, 100, 'Fitness', true, '2023-01-01 09:40:00', '2023-01-01 09:40:00', 0),
(10, 'Protein Powder', 'Whey protein powder for fitness', 'FITNESS-2002', 34.99, 60, 'Fitness', true, '2023-01-01 09:45:00', '2023-01-01 09:45:00', 0),
(11, 'Vintage Watch', 'Classic analog watch', 'ACCESS-1001', 199.99, 15, 'Accessories', true, '2023-01-01 09:50:00', '2023-01-01 09:50:00', 0),
(12, 'Tablet Basic', 'Entry-level tablet', 'TECH-4004', 299.99, 0, 'Electronics', false, '2023-01-01 09:55:00', '2023-01-01 09:55:00', 0);

-- Insert sample orders
INSERT INTO orders (id, order_number, user_id, order_date, total_amount, status, shipping_address, billing_address, created_at, updated_at, version) VALUES
(1, 'ORD-10001', 1, '2023-01-05 14:30:00', 949.98, 'DELIVERED', '123 Main St, Apt 4B, Anytown, CA 12345', '123 Main St, Apt 4B, Anytown, CA 12345', '2023-01-05 14:30:00', '2023-01-08 10:00:00', 2),
(2, 'ORD-10002', 2, '2023-01-06 10:15:00', 1349.98, 'SHIPPED', '456 Oak Ave, Springfield, IL 67890', '456 Oak Ave, Springfield, IL 67890', '2023-01-06 10:15:00', '2023-01-07 09:30:00', 1),
(3, 'ORD-10003', 3, '2023-01-07 16:45:00', 114.98, 'PROCESSING', '789 Pine Rd, Lakeside, WA 23456', '789 Pine Rd, Lakeside, WA 23456', '2023-01-07 16:45:00', '2023-01-07 16:45:00', 0),
(4, 'ORD-10004', 4, '2023-01-08 09:20:00', 179.98, 'PENDING', '321 Cedar Ln, Mountain View, CA 34567', '321 Cedar Ln, Mountain View, CA 34567', '2023-01-08 09:20:00', '2023-01-08 09:20:00', 0),
(5, 'ORD-10005', 1, '2023-01-09 13:10:00', 234.97, 'CANCELLED', '123 Main St, Apt 4B, Anytown, CA 12345', '123 Main St, Apt 4B, Anytown, CA 12345', '2023-01-09 13:10:00', '2023-01-10 11:05:00', 1);

-- Insert order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 799.99),
(1, 3, 1, 149.99),
(2, 2, 1, 1299.99),
(2, 4, 2, 24.99),
(3, 9, 1, 29.99),
(3, 10, 1, 34.99),
(3, 4, 2, 24.99),
(4, 7, 1, 79.99),
(4, 8, 2, 49.99),
(5, 6, 1, 89.99),
(5, 4, 1, 24.99),
(5, 9, 4, 29.99);

-- Insert sample payments
INSERT INTO payments (id, order_id, amount, payment_date, payment_method, transaction_id, status, created_at, updated_at, version) VALUES
(1, 1, 949.98, '2023-01-05 14:35:00', 'CREDIT_CARD', 'TXN-20230105-001', 'COMPLETED', '2023-01-05 14:35:00', '2023-01-05 14:35:00', 0),
(2, 2, 1349.98, '2023-01-06 10:20:00', 'PAYPAL', 'TXN-20230106-001', 'COMPLETED', '2023-01-06 10:20:00', '2023-01-06 10:20:00', 0),
(3, 3, 114.98, '2023-01-07 16:50:00', 'DEBIT_CARD', 'TXN-20230107-001', 'COMPLETED', '2023-01-07 16:50:00', '2023-01-07 16:50:00', 0),
(4, 4, 179.98, '2023-01-08 09:25:00', 'CREDIT_CARD', 'TXN-20230108-001', 'PENDING', '2023-01-08 09:25:00', '2023-01-08 09:25:00', 0),
(5, 5, 234.97, '2023-01-09 13:15:00', 'CREDIT_CARD', 'TXN-20230109-001', 'REFUNDED', '2023-01-09 13:15:00', '2023-01-10 11:10:00', 1);

-- Update stock quantities based on orders
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-05 14:35:00', version = 1 WHERE id = 1;
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-05 14:35:00', version = 1 WHERE id = 3;
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-06 10:20:00', version = 1 WHERE id = 2;
UPDATE products SET stock_quantity = stock_quantity - 5, updated_at = '2023-01-09 13:15:00', version = 1 WHERE id = 4;
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-07 16:50:00', version = 1 WHERE id = 9;
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-07 16:50:00', version = 1 WHERE id = 10;
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-08 09:25:00', version = 1 WHERE id = 7;
UPDATE products SET stock_quantity = stock_quantity - 2, updated_at = '2023-01-08 09:25:00', version = 1 WHERE id = 8;
UPDATE products SET stock_quantity = stock_quantity - 1, updated_at = '2023-01-09 13:15:00', version = 1 WHERE id = 6;
UPDATE products SET stock_quantity = stock_quantity - 4, updated_at = '2023-01-09 13:15:00', version = 1 WHERE id = 9;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- TODO: Add more complex data relationships such as product reviews
-- TODO: Consider adding sample data for different user roles and permissions
-- FIXME: Adjust initial stock quantities for high-demand products based on business projections

-- Comments:
-- This script populates the database with realistic sample data for testing and development
-- The password hashes are BCrypt encoded (but are just placeholders - 'password123' for all users)
-- Order status follows a realistic flow from PENDING to DELIVERED or CANCELLED
-- Sample data includes examples of all entity types and their relationships
-- Product categories and inventory levels are set to realistic values
-- Payment status reflects order status (COMPLETED for delivered/shipped, PENDING for processing, etc.)