package com.hotel.service;

import com.hotel.config.LoyaltyPolicy;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.repository.GuestRepository;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;

/**
 * Handles the loyalty program - earning and redeeming points.
 * Guests earn points when they pay, and can redeem points for discounts.
 */
public class LoyaltyService {
    
    private GuestRepository guestRepository;
    private LoyaltyPolicy loyaltyPolicy;
    private LoggerService logger;
    private EntityManager em;
    
    public LoyaltyService(EntityManager em, LoyaltyPolicy loyaltyPolicy) {
        this.em = em;
        this.guestRepository = new GuestRepository(em);
        this.loyaltyPolicy = loyaltyPolicy;
        this.logger = LoggerService.getInstance();
    }
    
    /**
     * Gives loyalty points to a guest based on payment amount.
     * Points are earned automatically when payments are processed.
     */
    public void earnPoints(Guest guest, double paymentAmount) {
        em.getTransaction().begin();
        
        try {
            int pointsEarned = loyaltyPolicy.calculatePointsEarned(paymentAmount);
            guest.setLoyaltyPoints(guest.getLoyaltyPoints() + pointsEarned);
            guestRepository.save(guest);
            
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "EARN_POINTS", "Guest", guest.getId(), 
                "Earned " + pointsEarned + " points from payment of $" + paymentAmount);
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to earn points", e);
            throw e;
        }
    }
    
    /**
     * Redeems loyalty points for a discount.
     * Validates that guest has enough points and respects redemption caps.
     */
    public int redeemPoints(Guest guest, int pointsToRedeem) {
        em.getTransaction().begin();
        
        try {
            if (pointsToRedeem <= 0) {
                return 0;
            }
            
            // Make sure redemption amount is valid (not over the cap)
            if (!loyaltyPolicy.isValidRedemption(pointsToRedeem)) {
                pointsToRedeem = loyaltyPolicy.capRedemption(pointsToRedeem);
            }
            
            // Check if guest actually has enough points
            if (guest.getLoyaltyPoints() < pointsToRedeem) {
                throw new IllegalArgumentException("Insufficient loyalty points");
            }
            
            // Take the points away from guest's balance
            guest.setLoyaltyPoints(guest.getLoyaltyPoints() - pointsToRedeem);
            guestRepository.save(guest);
            
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "REDEEM_POINTS", "Guest", guest.getId(), 
                "Redeemed " + pointsToRedeem + " points");
            
            return pointsToRedeem;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to redeem points", e);
            throw e;
        }
    }
    
    /**
     * Gets the current loyalty points balance for a guest.
     */
    public int getBalance(Guest guest) {
        return guest.getLoyaltyPoints();
    }
    
    /**
     * Calculates how much discount the redeemed points are worth.
     */
    public double calculateDiscountFromPoints(double totalAmount, int pointsRedeemed) {
        return loyaltyPolicy.calculateDiscountAmount(totalAmount, pointsRedeemed);
    }
}



