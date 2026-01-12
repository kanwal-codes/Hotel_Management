-- Hotel Reservation System - Database Schema
-- MySQL Database Creation Script
-- Run this script to create the database and all tables

-- Create database
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS waitlists;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS billings;
DROP TABLE IF EXISTS reservation_addons;
DROP TABLE IF EXISTS reservation_rooms;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS service_addons;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS guests;
DROP TABLE IF EXISTS admin_users;
DROP TABLE IF EXISTS hotels;

-- Create hotels table
CREATE TABLE hotels (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create guests table
CREATE TABLE guests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    customer_password_hash VARCHAR(255),
    loyalty_points INT DEFAULT 0,
    loyalty_number VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create admin_users table
CREATE TABLE admin_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN' or 'MANAGER'
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create rooms table
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT NOT NULL,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'SINGLE', 'DOUBLE', 'DELUXE', 'PENTHOUSE'
    beds INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'OCCUPIED', 'MAINTENANCE'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id) ON DELETE CASCADE
);

-- Create reservations table
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    num_adults INT NOT NULL,
    num_children INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_OUT'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE CASCADE
);

-- Create reservation_rooms table (many-to-many relationship)
CREATE TABLE reservation_rooms (
    reservation_id INT NOT NULL,
    room_id INT NOT NULL,
    PRIMARY KEY (reservation_id, room_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Create service_addons table
CREATE TABLE service_addons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    pricing_model VARCHAR(20) NOT NULL, -- 'PER_NIGHT' or 'PER_RESERVATION'
    active BOOLEAN DEFAULT TRUE
);

-- Create reservation_addons table (many-to-many relationship)
CREATE TABLE reservation_addons (
    reservation_id INT NOT NULL,
    addon_id INT NOT NULL,
    quantity INT DEFAULT 1,
    PRIMARY KEY (reservation_id, addon_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (addon_id) REFERENCES service_addons(id) ON DELETE CASCADE
);

-- Create billings table
CREATE TABLE billings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT UNIQUE NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,4) DEFAULT 0.10,
    tax_amount DECIMAL(10,2) NOT NULL,
    discount_value DECIMAL(10,2) DEFAULT 0,
    loyalty_redeemed_points INT DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0,
    balance_amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

-- Create payments table
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    billing_id INT NOT NULL,
    method VARCHAR(20) NOT NULL, -- 'CASH', 'CARD', 'POINTS'
    amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (billing_id) REFERENCES billings(id) ON DELETE CASCADE
);

-- Create feedbacks table
CREATE TABLE feedbacks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    reservation_id INT NOT NULL,
    rating INT NOT NULL, -- 1-5
    comments TEXT,
    sentiment_tag VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE CASCADE,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

-- Create amenity_bookings table
CREATE TABLE amenity_bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(20) UNIQUE NOT NULL,
    amenity_name VARCHAR(50) NOT NULL,
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
    guest_name VARCHAR(100),
    guest_email VARCHAR(50),
    guest_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create waitlists table
CREATE TABLE waitlists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    guest_id INT NOT NULL,
    requested_type VARCHAR(20) NOT NULL, -- 'SINGLE', 'DOUBLE', 'DELUXE', 'PENTHOUSE'
    date_range_start DATE NOT NULL,
    date_range_end DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'NOTIFIED', 'CONVERTED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE CASCADE
);

-- Create audit_log table
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actor VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    message VARCHAR(500),
    admin_user_id BIGINT,
    FOREIGN KEY (admin_user_id) REFERENCES admin_users(id) ON DELETE SET NULL
);

-- Create indexes for better performance
CREATE INDEX idx_guest_email ON guests(email);
CREATE INDEX idx_guest_phone ON guests(phone);
CREATE INDEX idx_guest_loyalty_number ON guests(loyalty_number);
CREATE INDEX idx_reservation_guest ON reservations(guest_id);
CREATE INDEX idx_reservation_dates ON reservations(check_in, check_out);
CREATE INDEX idx_reservation_status ON reservations(status);
CREATE INDEX idx_room_type ON rooms(type);
CREATE INDEX idx_room_status ON rooms(status);
CREATE INDEX idx_billing_reservation ON billings(reservation_id);
CREATE INDEX idx_payment_billing ON payments(billing_id);
CREATE INDEX idx_feedback_reservation ON feedbacks(reservation_id);
CREATE INDEX idx_waitlist_type ON waitlists(requested_type);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_actor ON audit_log(actor);



