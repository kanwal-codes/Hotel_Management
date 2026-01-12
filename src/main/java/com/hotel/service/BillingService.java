package com.hotel.service;

import com.hotel.config.DiscountPolicy;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.service.strategy.*;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * Handles all billing and payment operations.
 * Uses Strategy pattern to calculate totals differently based on discounts or loyalty points.
 */
public class BillingService {
    
    private BillingRepository billingRepository;
    private PaymentRepository paymentRepository;
    private ReservationRepository reservationRepository;
    private DiscountPolicy discountPolicy;
    private LoyaltyService loyaltyService;
    private LoggerService logger;
    private EntityManager em;
    
    public BillingService(EntityManager em, DiscountPolicy discountPolicy) {
        this.em = em;
        this.billingRepository = new BillingRepository(em);
        this.paymentRepository = new PaymentRepository(em);
        this.reservationRepository = new ReservationRepository(em);
        this.discountPolicy = discountPolicy;
        this.logger = LoggerService.getInstance();
        // LoyaltyService set separately to avoid circular dependency issues
    }
    
    /**
     * Sets the loyalty service after both services are created.
     * Needed because BillingService and LoyaltyService depend on each other.
     */
    public void setLoyaltyService(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }
    
    /**
     * Creates a new billing record for a reservation.
     * Calculates tax and sets initial totals.
     */
    public Billing createBilling(Reservation reservation, double subtotal) {
        em.getTransaction().begin();
        
        try {
            Billing billing = new Billing(reservation, subtotal);
            billing = billingRepository.save(billing);
            
            reservation.setBilling(billing);
            reservationRepository.save(reservation);
            
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "CREATE_BILLING", "Billing", billing.getId(), 
                "Billing created for reservation: " + reservation.getId());
            
            return billing;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to create billing", e);
            throw e;
        }
    }
    
    /**
     * Applies a discount to a billing.
     * Checks if the admin has permission for that discount amount based on their role.
     */
    public Billing applyDiscount(Billing billing, double discountPercent, AdminUser appliedBy) {
        em.getTransaction().begin();
        
        try {
            // Make sure this admin can actually apply this discount amount
            if (!discountPolicy.isValidDiscount(appliedBy.getRole(), discountPercent)) {
                throw new IllegalArgumentException("Discount exceeds allowed limit for role: " + appliedBy.getRole());
            }
            
            // Cap it to the max allowed for their role
            discountPercent = discountPolicy.validateAndCapDiscount(appliedBy.getRole(), discountPercent);
            
            // Calculate the dollar amount of the discount
            double discountAmount = discountPolicy.calculateDiscountAmount(billing.getSubtotal(), discountPercent);
            billing.setDiscountValue(discountAmount);
            
            // Recalculate the total with the discount
            recalculateTotalInternal(billing);
            
            billing = billingRepository.save(billing);
            em.getTransaction().commit();
            
            logger.logActivity(appliedBy.getUsername(), "APPLY_DISCOUNT", "Billing", billing.getId(), 
                "Applied " + discountPercent + "% discount");
            
            return billing;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to apply discount", e);
            throw e;
        }
    }
    
    /**
     * Updates the subtotal when reservation changes (different rooms, dates, etc.)
     * Recalculates tax and total automatically.
     */
    public Billing updateBillingSubtotal(Billing billing, double newSubtotal) {
        em.getTransaction().begin();
        try {
            billing.setSubtotal(newSubtotal);
            recalculateTotalInternal(billing);
            billing = billingRepository.save(billing);
            em.getTransaction().commit();
            return billing;
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to update billing subtotal", e);
            throw e;
        }
    }
    
    /**
     * Recalculates the total using the right strategy.
     * Public method that handles transaction.
     */
    public void recalculateTotal(Billing billing) {
        em.getTransaction().begin();
        try {
            recalculateTotalInternal(billing);
            billing = billingRepository.save(billing);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to recalculate billing total", e);
            throw e;
        }
    }
    
    /**
     * Internal method that does the actual calculation.
     * Uses Strategy pattern to pick the right calculation method.
     */
    private void recalculateTotalInternal(Billing billing) {
        BillingStrategy strategy;
        
        // Pick strategy based on what's been applied to this billing
        if (billing.getLoyaltyRedeemedPoints() > 0) {
            strategy = new LoyaltyBillingStrategy();
        } else if (billing.getDiscountValue() > 0) {
            strategy = new DiscountBillingStrategy();
        } else {
            strategy = new StandardBillingStrategy();
        }
        
        double total = strategy.calculateTotal(billing);
        billing.setTotalAmount(total);
        billing.setBalanceAmount(total - billing.getPaidAmount());
        
        // Update payment status
        if (billing.getBalanceAmount() <= 0) {
            billing.setPaymentStatus("PAID");
        } else if (billing.getPaidAmount() > 0) {
            billing.setPaymentStatus("PARTIAL");
        } else {
            billing.setPaymentStatus("PENDING");
        }
    }
    
    /**
     * Create a deposit at booking time
     * Explicit method for deposits as required by project specifications
     */
    public Payment createDeposit(Billing billing, PaymentMethod method, double depositAmount, String actor) {
        if (depositAmount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        // Log as deposit
        logger.logActivity(actor, "CREATE_DEPOSIT", "Billing", billing.getId(), 
            "Deposit of $" + depositAmount + " via " + method + " at booking time");
        
        // Process as payment (deposits are payments made at booking)
        return processPayment(billing, method, depositAmount, actor);
    }
    
    /**
     * Process a partial payment during stay
     * Explicit method for partial payments as required by project specifications
     */
    public Payment processPartialPayment(Billing billing, PaymentMethod method, double partialAmount, String actor) {
        if (partialAmount <= 0) {
            throw new IllegalArgumentException("Partial payment amount must be positive");
        }
        
        double currentBalance = billing.getBalanceAmount();
        if (partialAmount > currentBalance) {
            throw new IllegalArgumentException("Partial payment amount (" + partialAmount + 
                ") cannot exceed current balance (" + currentBalance + ")");
        }
        
        // Log as partial payment
        logger.logActivity(actor, "PARTIAL_PAYMENT", "Billing", billing.getId(), 
            "Partial payment of $" + partialAmount + " via " + method + " during stay. Remaining balance: $" + 
            (currentBalance - partialAmount));
        
        // Process as payment
        return processPayment(billing, method, partialAmount, actor);
    }
    
    /**
     * Process a payment
     */
    public Payment processPayment(Billing billing, PaymentMethod method, double amount, String actor) {
        em.getTransaction().begin();
        
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException("Payment amount must be positive");
            }
            
            Payment payment = new Payment(billing, method, amount);
            payment = paymentRepository.save(payment);
            
            billing.setPaidAmount(billing.getPaidAmount() + amount);
            recalculateTotalInternal(billing);
            
            billing = billingRepository.save(billing);
            
            // Update reservation status based on payment
            Reservation reservation = billing.getReservation();
            if (reservation != null) {
                // Check if fully paid
                double balance = billing.getBalanceAmount();
                if (balance <= 0 && reservation.getStatus() == ReservationStatus.PENDING) {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    // Use repository to ensure proper persistence
                    reservation = reservationRepository.save(reservation);
                    logger.logInfo("Reservation " + reservation.getId() + " status updated to CONFIRMED (fully paid)");
                }
            }
            
            em.getTransaction().commit();
            
            // Earn loyalty points for the guest (if loyalty service is available)
            if (loyaltyService != null && method != PaymentMethod.POINTS) {
                try {
                    Guest guest = billing.getReservation().getGuest();
                    if (guest != null) {
                        // Use a new EntityManager for loyalty service to avoid transaction issues
                        loyaltyService.earnPoints(guest, amount);
                    }
                } catch (Exception e) {
                    // Log error but don't fail payment processing
                    logger.logError("Failed to earn loyalty points", e);
                }
            }
            
            logger.logActivity(actor, "PROCESS_PAYMENT", "Payment", payment.getId(), 
                "Payment of $" + amount + " via " + method);
            
            return payment;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to process payment", e);
            throw e;
        }
    }
    
    /**
     * Process a refund (negative payment)
     */
    public Payment processRefund(Billing billing, double amount, String actor) {
        return processPayment(billing, PaymentMethod.CASH, -amount, actor);
    }
    
    /**
     * Get billing for a reservation
     */
    public Optional<Billing> getBillingForReservation(Reservation reservation) {
        return billingRepository.findByReservation(reservation);
    }
    
    /**
     * Check if reservation can be checked out (balance must be zero)
     */
    public boolean canCheckout(Reservation reservation) {
        Optional<Billing> billingOpt = getBillingForReservation(reservation);
        if (billingOpt.isEmpty()) {
            return false;
        }
        
        Billing billing = billingOpt.get();
        return billing.getBalanceAmount() <= 0;
    }
}

