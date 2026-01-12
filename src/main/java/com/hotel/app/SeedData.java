package com.hotel.app;

import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.security.BCryptPasswordHasher;
import com.hotel.util.RoomFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;

/**
 * Utility class to seed the database with initial data
 * Run this once to populate the database with test data
 */
public class SeedData {
    
    public static void seed(EntityManager em) {
        em.getTransaction().begin();
        
        try {
            // Create admin users
            AdminUserRepository adminRepo = new AdminUserRepository(em);
            
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            admin.setPasswordHash(BCryptPasswordHasher.hash("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            adminRepo.save(admin);
            
            AdminUser manager = new AdminUser();
            manager.setUsername("manager");
            manager.setPasswordHash(BCryptPasswordHasher.hash("manager123"));
            manager.setRole(Role.MANAGER);
            manager.setActive(true);
            adminRepo.save(manager);
            
            // Create hotel
            Hotel hotel = new Hotel();
            hotel.setName("Grand Hotel");
            hotel.setCity("New York");
            em.persist(hotel);
            
            // Create rooms
            RoomRepository roomRepo = new RoomRepository(em);
            
            // Single rooms
            for (int i = 1; i <= 5; i++) {
                Room room = RoomFactory.createRoom(RoomType.SINGLE, "10" + i, hotel);
                roomRepo.save(room);
            }
            
            // Double rooms
            for (int i = 1; i <= 5; i++) {
                Room room = RoomFactory.createRoom(RoomType.DOUBLE, "20" + i, hotel);
                roomRepo.save(room);
            }
            
            // Deluxe rooms
            for (int i = 1; i <= 3; i++) {
                Room room = RoomFactory.createRoom(RoomType.DELUXE, "30" + i, hotel);
                roomRepo.save(room);
            }
            
            // Penthouse
            Room penthouse = RoomFactory.createRoom(RoomType.PENTHOUSE, "P001", hotel);
            roomRepo.save(penthouse);
            
            // Create service addons
            AddonRepository addonRepo = new AddonRepository(em);
            
            ServiceAddon wifi = new ServiceAddon("Wi-Fi", 10.0, PricingModel.PER_NIGHT);
            addonRepo.save(wifi);
            
            ServiceAddon breakfast = new ServiceAddon("Breakfast", 15.0, PricingModel.PER_NIGHT);
            addonRepo.save(breakfast);
            
            ServiceAddon parking = new ServiceAddon("Parking", 20.0, PricingModel.PER_RESERVATION);
            addonRepo.save(parking);
            
            ServiceAddon spa = new ServiceAddon("Spa Access", 50.0, PricingModel.PER_NIGHT);
            addonRepo.save(spa);
            
            em.getTransaction().commit();
            System.out.println("Seed data created successfully!");
            System.out.println("Admin credentials: admin/admin123");
            System.out.println("Manager credentials: manager/manager123");
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("Failed to seed data: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}



