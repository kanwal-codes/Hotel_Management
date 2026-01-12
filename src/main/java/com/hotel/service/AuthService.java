package com.hotel.service;

import com.hotel.model.AdminUser;
import com.hotel.model.Role;
import com.hotel.repository.AdminUserRepository;
import com.hotel.security.BCryptPasswordHasher;
import com.hotel.util.LoggerService;
import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * Handles admin authentication and authorization.
 * Verifies passwords using BCrypt and checks role permissions.
 */
public class AuthService {
    private AdminUserRepository adminUserRepository;
    private LoggerService logger;
    
    public AuthService(EntityManager em) {
        this.adminUserRepository = new AdminUserRepository(em);
        this.logger = LoggerService.getInstance();
    }
    
    /**
     * Logs in an admin user with username and password.
     * Returns the admin user if login succeeds, empty if it fails.
     */
    public Optional<AdminUser> login(String username, String password) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            
            // Can't login if account is disabled
            if (!admin.isActive()) {
                logger.logWarning("Login attempt for inactive user: " + username);
                return Optional.empty();
            }
            
            // Verify password using BCrypt
            if (BCryptPasswordHasher.verify(password, admin.getPasswordHash())) {
                logger.logActivity(admin.getUsername(), "LOGIN", "AdminUser", admin.getId(), "Successful login");
                return Optional.of(admin);
            } else {
                logger.logActivity(username, "LOGIN_FAILED", "AdminUser", null, "Invalid password");
            }
        } else {
            logger.logActivity(username, "LOGIN_FAILED", "AdminUser", null, "User not found");
        }
        
        return Optional.empty();
    }
    
    /**
     * Logs in an admin user with email and password.
     * Same as login but uses email instead of username.
     */
    public Optional<AdminUser> loginByEmail(String email, String password) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByEmail(email);
        
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            
            if (!admin.isActive()) {
                logger.logWarning("Login attempt for inactive user: " + email);
                return Optional.empty();
            }
            
            if (BCryptPasswordHasher.verify(password, admin.getPasswordHash())) {
                logger.logActivity(admin.getUsername(), "LOGIN", "AdminUser", admin.getId(), "Successful login via email");
                return Optional.of(admin);
            } else {
                logger.logActivity(email, "LOGIN_FAILED", "AdminUser", null, "Invalid password");
            }
        } else {
            logger.logActivity(email, "LOGIN_FAILED", "AdminUser", null, "User not found");
        }
        
        return Optional.empty();
    }
    
    /**
     * Checks if an email address belongs to management.
     * Looks for specific domain patterns like @hotel.com.
     */
    public static boolean isManagementEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String lowerEmail = email.toLowerCase();
        return lowerEmail.endsWith("@hotel.com") ||
               lowerEmail.endsWith("@management.hotel.com") ||
               lowerEmail.endsWith("@admin.hotel.com");
    }
    
    /**
     * Checks if an admin has a specific role.
     */
    public boolean hasRole(AdminUser admin, String role) {
        if (admin == null) {
            return false;
        }
        return admin.getRole().name().equals(role);
    }
    
    /**
     * Checks if admin has ADMIN role.
     */
    public boolean isAdmin(AdminUser admin) {
        return hasRole(admin, "ADMIN");
    }
    
    /**
     * Checks if admin has MANAGER role.
     */
    public boolean isManager(AdminUser admin) {
        return hasRole(admin, "MANAGER");
    }
    
    /**
     * Checks if an admin can apply a discount of this amount.
     * Admins can apply up to 15%, Managers up to 30%.
     */
    public boolean canApplyDiscount(AdminUser admin, double discountPercent) {
        if (admin == null) {
            return false;
        }
        
        if (admin.getRole() == Role.ADMIN && discountPercent <= 15.0) {
            return true;
        }
        
        if (admin.getRole() == Role.MANAGER && discountPercent <= 30.0) {
            return true;
        }
        
        return false;
    }
}



