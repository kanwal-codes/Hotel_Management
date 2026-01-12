package com.hotel.app;

import com.hotel.model.AdminUser;
import com.hotel.model.Role;
import com.hotel.repository.AdminUserRepository;
import com.hotel.security.BCryptPasswordHasher;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

// utility to update admin user passwords with fresh bcrypt hashes
// run this if admin login fails due to password hash issues
public class UpdateAdminPasswords {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hotelPU");
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            AdminUserRepository adminRepo = new AdminUserRepository(em);
            
            // Create/Update admin user with email admin@hotel.com
            var adminByEmail = adminRepo.findByEmail("admin@hotel.com");
            var adminByUsername = adminRepo.findByUsername("admin");
            
            AdminUser admin = null;
            if (adminByEmail.isPresent()) {
                admin = adminByEmail.get();
                admin.setPasswordHash(BCryptPasswordHasher.hash("admin123"));
                admin.setEmail("admin@hotel.com");
                admin.setRole(Role.ADMIN);
                admin.setUsername("admin");
                admin.setActive(true);
                adminRepo.save(admin);
                System.out.println("Updated admin user (admin@hotel.com)");
            } else if (adminByUsername.isPresent()) {
                admin = adminByUsername.get();
                admin.setPasswordHash(BCryptPasswordHasher.hash("admin123"));
                admin.setEmail("admin@hotel.com");
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                adminRepo.save(admin);
                System.out.println("Updated admin user email to admin@hotel.com");
            } else {
                // Create new admin user
                admin = new AdminUser("admin", "admin@hotel.com", 
                    BCryptPasswordHasher.hash("admin123"), Role.ADMIN);
                adminRepo.save(admin);
                System.out.println("Created admin user (admin@hotel.com, ADMIN role, max 15% discount)");
            }
            
            // Create/Update manager user with email manager@hotel.com
            var managerByEmail = adminRepo.findByEmail("manager@hotel.com");
            var managerByUsername = adminRepo.findByUsername("manager");
            
            AdminUser manager = null;
            if (managerByEmail.isPresent()) {
                manager = managerByEmail.get();
                manager.setPasswordHash(BCryptPasswordHasher.hash("admin123"));
                manager.setEmail("manager@hotel.com");
                manager.setRole(Role.MANAGER);
                manager.setUsername("manager");
                manager.setActive(true);
                adminRepo.save(manager);
                System.out.println("Updated manager user (manager@hotel.com)");
            } else if (managerByUsername.isPresent()) {
                manager = managerByUsername.get();
                manager.setPasswordHash(BCryptPasswordHasher.hash("admin123"));
                manager.setEmail("manager@hotel.com");
                manager.setRole(Role.MANAGER);
                manager.setActive(true);
                adminRepo.save(manager);
                System.out.println("Updated manager user email to manager@hotel.com");
            } else {
                // Create new manager user
                manager = new AdminUser("manager", "manager@hotel.com", 
                    BCryptPasswordHasher.hash("admin123"), Role.MANAGER);
                adminRepo.save(manager);
                System.out.println("Created manager user (manager@hotel.com, MANAGER role, max 30% discount)");
            }
            
            em.getTransaction().commit();
            System.out.println("\n✅ Admin users created/updated successfully!");
            System.out.println("\nLogin Credentials:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📧 Email: admin@hotel.com");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
            System.out.println("   Role: ADMIN");
            System.out.println("   Max Discount: 15%");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📧 Email: manager@hotel.com");
            System.out.println("   Username: manager");
            System.out.println("   Password: admin123");
            System.out.println("   Role: MANAGER");
            System.out.println("   Max Discount: 30%");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Failed to update passwords: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}

