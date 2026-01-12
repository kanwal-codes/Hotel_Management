-- Migration script to add email to admin_users and confirmation_number to reservations
-- Run this after updating the AdminUser and Reservation entities

USE hotel_db;

-- Add email column to admin_users table
ALTER TABLE admin_users 
ADD COLUMN email VARCHAR(100) UNIQUE NOT NULL DEFAULT '';

-- Update existing admin users with default emails (update these with actual emails)
UPDATE admin_users SET email = 'admin@hotel.com' WHERE username = 'admin';
UPDATE admin_users SET email = 'manager@hotel.com' WHERE username = 'manager';

-- Add confirmation_number column to reservations table
ALTER TABLE reservations 
ADD COLUMN confirmation_number VARCHAR(20) UNIQUE;

-- Create index for faster lookups
CREATE INDEX idx_confirmation_number ON reservations(confirmation_number);
CREATE INDEX idx_admin_email ON admin_users(email);

