-- Script to delete all reservations and related data
-- This will remove all reservations, payments, billings, reservation rooms, and reservation addons

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Delete reservation addons (linked to reservations)
DELETE FROM reservation_addon;

-- Delete reservation rooms (linked to reservations)
DELETE FROM reservation_room;

-- Delete payments (linked to billings, which are linked to reservations)
DELETE FROM payment;

-- Delete billings (linked to reservations)
DELETE FROM billing;

-- Delete feedback (linked to reservations)
DELETE FROM feedback;

-- Delete reservations
DELETE FROM reservation;

-- Set all rooms to AVAILABLE status
UPDATE rooms SET status = 'AVAILABLE' WHERE status = 'OCCUPIED';

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Verify deletion
SELECT COUNT(*) as remaining_reservations FROM reservation;
SELECT COUNT(*) as remaining_billings FROM billing;
SELECT COUNT(*) as remaining_payments FROM payment;

