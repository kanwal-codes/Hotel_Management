package com.hotel.app;

import com.hotel.config.*;
import com.hotel.events.RoomAvailabilityPublisher;
import com.hotel.repository.*;
import com.hotel.service.*;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

// this class handles all the setup and dependency injection
// instead of creating objects everywhere, we create them here and pass them around
// makes testing easier and keeps code organized
public class AppConfig {
    
    // these are singletons - only one instance exists for the whole app
    private static EntityManagerFactory emf;
    private static LoggerService logger;
    
    // business rules that don't change during runtime
    private static PricingPolicy pricingPolicy;
    private static DiscountPolicy discountPolicy;
    private static LoyaltyPolicy loyaltyPolicy;
    
    // repositories and services are created fresh for each operation
    // this prevents issues with database transactions
    
    // sets up everything the app needs to run
    // call this before doing anything else
    public static void initialize() {
        try {
            // start the logger first so we can log everything else
            logger = LoggerService.getInstance();
            logger.logInfo("Initializing application configuration...");
            
            // create the database connection factory - this is expensive so we only do it once
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("hotelPU");
                logger.logInfo("EntityManagerFactory created successfully");
            }
            
            // set up business rules - these control pricing, discounts, and loyalty points
            pricingPolicy = new PricingPolicy(1.2, 1.0); // weekends cost 20% more
            discountPolicy = new DiscountPolicy();
            loyaltyPolicy = new LoyaltyPolicy(10.0, 1000); // earn 1 point per $10, max 1000 points per use
            
            // configure seasonal multipliers for defined date ranges (e.g., peak season)
            // example: peak season from december 15 to january 15 with 50% increase
            java.time.LocalDate peakStart = java.time.LocalDate.of(2025, 12, 15);
            java.time.LocalDate peakEnd = java.time.LocalDate.of(2026, 1, 15);
            PricingPolicy.Season peakSeason = new PricingPolicy.Season("Peak Season", peakStart, peakEnd);
            pricingPolicy.addSeasonalMultiplier(peakSeason, 1.5); // 50% increase during peak season
            
            logger.logInfo("Seasonal multipliers configured: Peak Season (Dec 15 - Jan 15) with 1.5x multiplier");
            
            logger.logInfo("Application configuration initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Application initialization failed", e);
        }
    }
    
    // returns the database factory, creates it if it doesn't exist yet
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            initialize();
        }
        return emf;
    }
    
    // creates a new database connection for one operation
    // important: don't reuse this across different operations - create a new one each time
    // this prevents transaction conflicts and weird bugs
    public static EntityManager createEntityManager() {
        if (emf == null) {
            initialize();
        }
        return emf.createEntityManager();
    }
    
    // gets the logger, creates it if needed
    public static LoggerService getLogger() {
        if (logger == null) {
            logger = LoggerService.getInstance();
        }
        return logger;
    }
    
    // Factory methods for repositories - each gets a fresh database connection
    
    public static GuestRepository createGuestRepository() {
        return new GuestRepository(createEntityManager());
    }
    
    public static RoomRepository createRoomRepository() {
        return new RoomRepository(createEntityManager());
    }
    
    public static AdminUserRepository createAdminUserRepository() {
        return new AdminUserRepository(createEntityManager());
    }
    
    public static ReservationRepository createReservationRepository() {
        return new ReservationRepository(createEntityManager());
    }
    
    public static BillingRepository createBillingRepository() {
        return new BillingRepository(createEntityManager());
    }
    
    public static PaymentRepository createPaymentRepository() {
        return new PaymentRepository(createEntityManager());
    }
    
    public static AddonRepository createAddonRepository() {
        return new AddonRepository(createEntityManager());
    }
    
    public static FeedbackRepository createFeedbackRepository() {
        return new FeedbackRepository(createEntityManager());
    }
    
    public static WaitlistRepository createWaitlistRepository() {
        return new WaitlistRepository(createEntityManager());
    }
    
    public static AuditLogRepository createAuditLogRepository() {
        return new AuditLogRepository(createEntityManager());
    }
    
    // Factory methods for services - these contain the business logic
    
    public static AuthService createAuthService() {
        return new AuthService(createEntityManager());
    }
    
    public static PricingService createPricingService() {
        if (pricingPolicy == null) {
            initialize();
        }
        return new PricingService(pricingPolicy);
    }
    
    public static ReservationService createReservationService() {
        return new ReservationService(createEntityManager());
    }
    
    public static BillingService createBillingService() {
        if (discountPolicy == null) {
            initialize();
        }
        BillingService billingService = new BillingService(createEntityManager(), discountPolicy);
        // Need to set loyalty service separately to avoid circular dependency issues
        LoyaltyService loyaltyService = createLoyaltyService();
        billingService.setLoyaltyService(loyaltyService);
        return billingService;
    }
    
    public static LoyaltyService createLoyaltyService() {
        if (loyaltyPolicy == null) {
            initialize();
        }
        return new LoyaltyService(createEntityManager(), loyaltyPolicy);
    }
    
    public static WaitlistService createWaitlistService(RoomAvailabilityPublisher publisher) {
        return new WaitlistService(createEntityManager(), publisher);
    }
    
    public static FeedbackService createFeedbackService() {
        return new FeedbackService(createEntityManager());
    }
    
    public static ReportingService createReportingService() {
        return new ReportingService(createEntityManager());
    }
    
    public static ActivityLogService createActivityLogService() {
        return new ActivityLogService(createEntityManager());
    }
    
    // Getters for business rule policies
    
    public static PricingPolicy getPricingPolicy() {
        if (pricingPolicy == null) {
            initialize();
        }
        return pricingPolicy;
    }
    
    public static DiscountPolicy getDiscountPolicy() {
        if (discountPolicy == null) {
            initialize();
        }
        return discountPolicy;
    }
    
    public static LoyaltyPolicy getLoyaltyPolicy() {
        if (loyaltyPolicy == null) {
            initialize();
        }
        return loyaltyPolicy;
    }
    
    /**
     * Clean up database connections when app closes.
     * Prevents memory leaks and connection issues.
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.logInfo("EntityManagerFactory closed");
        }
    }
}
