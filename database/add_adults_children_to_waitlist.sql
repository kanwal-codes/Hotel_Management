-- Migration: Add num_adults and num_children columns to waitlist table
-- This allows storing guest occupancy information when adding to waitlist

ALTER TABLE waitlists 
ADD COLUMN num_adults INT NULL,
ADD COLUMN num_children INT NULL;

-- Update existing waitlist entries to have default values (if any exist)
UPDATE waitlists 
SET num_adults = 1, num_children = 0 
WHERE num_adults IS NULL;




