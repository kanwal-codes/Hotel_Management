-- Hotel Reservation System - Seed Data
-- Sample data for testing and development
-- Run this after create_schema.sql

USE hotel_db;

-- Insert hotel
INSERT INTO hotels (name, city) VALUES ('Grand Hotel', 'New York');

-- Insert admin users (passwords are hashed with BCrypt)
-- NOTE: Admin users should be created using SeedData.java or UpdateAdminPasswords.java
-- to ensure password hashes are generated correctly with the application's BCrypt library.
-- 
-- If you must use SQL, run UpdateAdminPasswords.java after running this script.
-- Default password for both: "admin123"
--
-- To create admin users properly, run one of these Java utilities:
-- 1. SeedData.java - Creates all seed data including admin users
-- 2. UpdateAdminPasswords.java - Creates/updates only admin users
--
-- For now, we'll skip admin user creation in SQL to avoid hash compatibility issues.
-- INSERT INTO admin_users (username, password, role, active) VALUES
-- ('admin', 'HASH_WILL_BE_GENERATED_BY_JAVA', 'ADMIN', TRUE),
-- ('manager', 'HASH_WILL_BE_GENERATED_BY_JAVA', 'MANAGER', TRUE);

-- Insert rooms
INSERT INTO rooms (hotel_id, room_number, type, beds, base_price, status) VALUES
-- Single rooms (10 rooms)
(1, '101', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '102', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '103', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '104', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '105', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '106', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '107', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '108', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '109', 'SINGLE', 1, 100.00, 'AVAILABLE'),
(1, '110', 'SINGLE', 1, 100.00, 'AVAILABLE'),

-- Double rooms (20 rooms)
(1, '201', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '202', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '203', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '204', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '205', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '206', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '207', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '208', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '209', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '210', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '211', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '212', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '213', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '214', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '215', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '216', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '217', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '218', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '219', 'DOUBLE', 2, 150.00, 'AVAILABLE'),
(1, '220', 'DOUBLE', 2, 150.00, 'AVAILABLE'),

-- Deluxe rooms (5 rooms)
(1, '301', 'DELUXE', 1, 200.00, 'AVAILABLE'),
(1, '302', 'DELUXE', 1, 200.00, 'AVAILABLE'),
(1, '303', 'DELUXE', 1, 200.00, 'AVAILABLE'),
(1, '304', 'DELUXE', 1, 200.00, 'AVAILABLE'),
(1, '305', 'DELUXE', 1, 200.00, 'AVAILABLE'),

-- Penthouse (1 room)
(1, 'P001', 'PENTHOUSE', 1, 500.00, 'AVAILABLE');

-- Insert service addons
INSERT INTO service_addon (name, price, pricing_model) VALUES
('Wi-Fi', 10.00, 'PER_NIGHT', TRUE),
('Breakfast', 15.00, 'PER_NIGHT', TRUE),
('Parking', 20.00, 'PER_RESERVATION', TRUE),
('Spa Access', 50.00, 'PER_RESERVATION', TRUE);

-- Insert sample guests
INSERT INTO guests (name, phone, email, address, customer_password_hash, loyalty_points, loyalty_number) VALUES
('John Doe', '555-0101', 'john.doe@email.com', '123 Main St, New York, NY', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 150, 'L000001'),
('Jane Smith', '555-0102', 'jane.smith@email.com', '456 Oak Ave, New York, NY', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 250, 'L000002'),
('Bob Johnson', '555-0103', 'bob.johnson@email.com', '789 Pine Rd, New York, NY', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, NULL);



