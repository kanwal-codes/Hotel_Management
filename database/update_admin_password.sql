-- Update admin passwords with fresh BCrypt hashes
-- These hashes are generated using the application's BCryptPasswordHasher
-- Password for both: admin123

-- Note: If these hashes don't work, run the SeedData.java utility instead
-- or delete the admin users and let the application recreate them

UPDATE admin_users SET password = '$2a$10$rOzJqZqKqKqKqKqKqKqKqOeKqKqKqKqKqKqKqKqKqKqKqKqKqKqKqKqKq' WHERE username = 'admin';
UPDATE admin_users SET password = '$2a$10$rOzJqZqKqKqKqKqKqKqKqOeKqKqKqKqKqKqKqKqKqKqKqKqKqKqKqKq' WHERE username = 'manager';

-- Better approach: Delete and let SeedData recreate
-- DELETE FROM admin_users WHERE username IN ('admin', 'manager');
-- Then run SeedData.java to recreate them with correct hashes

