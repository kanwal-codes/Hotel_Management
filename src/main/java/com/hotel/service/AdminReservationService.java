package com.hotel.service;

import com.hotel.app.AppConfig;
import com.hotel.controller.helper.AdminRoomSelectionHelper;
import com.hotel.model.*;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.BillingService;
import com.hotel.util.LoggerService;
import com.hotel.util.ReservationEntityManager;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Service for admin reservation management operations.
 * Handles business logic for creating, updating, and managing reservations.
 * Extracted from AdminReservationController to reduce controller size.
 */
public class AdminReservationService {
    
    private final ReservationService reservationService;
    private final BillingService billingService;
    private final PricingService pricingService;
    private final GuestRepository guestRepository;
    private final LoggerService logger;
    
    public AdminReservationService() {
        this.reservationService = AppConfig.createReservationService();
        this.billingService = AppConfig.createBillingService();
        this.pricingService = AppConfig.createPricingService();
        this.guestRepository = AppConfig.createGuestRepository();
        this.logger = LoggerService.getInstance();
    }
    
    /**
     * Resolves a guest from form data (creates new or updates existing).
     */
    public Guest resolveGuestFromForm(String name, String phone, String email) {
        String trimmedEmail = email.trim();
        Optional<Guest> existing = guestRepository.findByEmail(trimmedEmail);
        Guest guest = existing.orElseGet(Guest::new);
        guest.setName(name.trim());
        guest.setPhone(phone.trim());
        guest.setEmail(trimmedEmail);
        return guestRepository.save(guest);
    }
    
    /**
     * Calculates subtotal for rooms and addons.
     */
    public double calculateSubtotal(List<Room> rooms, LocalDate checkIn, LocalDate checkOut, 
                                     List<ReservationAddon> addons, Reservation currentReservation) {
        if (rooms == null || rooms.isEmpty() || checkIn == null || checkOut == null) {
            return 0.0;
        }
        
        // Calculate room subtotal using PricingService
        double roomSubtotal = rooms.stream()
            .mapToDouble(room -> pricingService.calculateRoomPrice(room, checkIn, checkOut))
            .sum();
        
        // Add add-on costs
        long nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
        double addonSubtotal = 0.0;
        List<ReservationAddon> addonsToCalculate = (addons == null || addons.isEmpty()) && currentReservation != null
            ? currentReservation.getReservationAddons()
            : addons;
        
        if (addonsToCalculate != null) {
            for (ReservationAddon ra : addonsToCalculate) {
                if (ra.getAddon() != null) {
                    double addonPrice = pricingService.calculateAddonPrice(
                        ra.getAddon().getPrice(),
                        ra.getAddon().getPricingModel(),
                        nights
                    );
                    addonSubtotal += addonPrice * ra.getQuantity();
                }
            }
        }
        
        return roomSubtotal + addonSubtotal;
    }
    
    /**
     * Recalculates billing if needed based on changes.
     */
    public Billing recalculateBillingIfNeeded(
            Reservation reservation,
            List<Room> rooms,
            LocalDate checkIn,
            LocalDate checkOut,
            BillingService billingService,
            Consumer<Billing> updateBillingDisplay,
            Runnable updateEstimatedBillingDisplay) {
        
        if (reservation == null || rooms == null || rooms.isEmpty()) {
            return null;
        }
        
        try {
            double subtotal = calculateSubtotal(rooms, checkIn, checkOut, null, reservation);
            
            // Get existing billing or create new one
            Optional<Billing> billingOpt = billingService.getBillingForReservation(reservation);
            Billing billing;
            
            if (billingOpt.isPresent()) {
                // Update existing billing
                billing = billingService.updateBillingSubtotal(billingOpt.get(), subtotal);
            } else {
                // Create new billing
                billing = billingService.createBilling(reservation, subtotal);
            }
            
            if (updateBillingDisplay != null) {
                updateBillingDisplay.accept(billing);
            }
            if (updateEstimatedBillingDisplay != null) {
                updateEstimatedBillingDisplay.run();
            }
            
            return billing;
        } catch (Exception e) {
            logger.logError("Failed to recalculate billing", e);
            return null;
        }
    }
    
    /**
     * Updates reservation guest information.
     */
    public void updateReservationGuestInfo(
            Reservation reservation,
            String name,
            String phone,
            String email) {
        
        if (reservation == null) return;
        
        Guest guest = reservation.getGuest();
        if (guest == null) {
            guest = new Guest();
            reservation.setGuest(guest);
        }
        
        if (name != null && !name.trim().isEmpty()) {
            guest.setName(name.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            guest.setPhone(phone.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            guest.setEmail(email.trim());
        }
        
        guestRepository.save(guest);
    }
    
    /**
     * Determines reservation status based on various factors.
     */
    public ReservationStatus determineReservationStatus(
            ReservationStatus selectedStatus,
            boolean guestCountChanged,
            boolean roomsChanged,
            boolean servicesChanged,
            LocalDate newCheckIn,
            LocalDate newCheckOut,
            Reservation reservation) {
        
        // If user manually selected a status, use it
        if (selectedStatus != null) {
            return selectedStatus;
        }
        
        // If reservation exists and has a status, preserve it unless significant changes
        if (reservation != null && reservation.getStatus() != null) {
            ReservationStatus currentStatus = reservation.getStatus();
            
            // If dates changed significantly, might need to update status
            if (newCheckIn != null && newCheckOut != null && 
                reservation.getCheckIn() != null && reservation.getCheckOut() != null) {
                if (!newCheckIn.equals(reservation.getCheckIn()) || 
                    !newCheckOut.equals(reservation.getCheckOut())) {
                    // Dates changed - keep current status unless it's confirmed and dates are in past
                    if (currentStatus == ReservationStatus.CONFIRMED && newCheckIn.isBefore(LocalDate.now())) {
                        return ReservationStatus.CHECKED_IN;
                    }
                }
            }
            
            // If rooms or services changed significantly, might need to update
            if (roomsChanged || servicesChanged) {
                // Keep current status but might need to recalculate billing
                return currentStatus;
            }
            
            return currentStatus;
        }
        
        // Default to PENDING for new reservations
        return ReservationStatus.PENDING;
    }
    
    /**
     * Saves reservation with the specified status.
     */
    public Reservation saveReservationWithStatus(
            Reservation reservation,
            ReservationStatus status,
            LoggerService logger) {
        
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        
        reservation.setStatus(status);
        // Use repository directly to save
        EntityManager em = AppConfig.createEntityManager();
        try {
            ReservationRepository repo = new ReservationRepository(em);
            return repo.save(reservation);
        } finally {
            em.close();
        }
    }
    
    /**
     * Recalculates billing after changes to reservation.
     */
    public Billing recalculateBillingAfterChange(
            Reservation reservation,
            BillingService billingService) {
        
        if (reservation == null) {
            return null;
        }
        
        try {
            List<Room> rooms = reservation.getReservationRooms().stream()
                .map(rr -> rr.getRoom())
                .filter(room -> room != null)
                .toList();
            
            double subtotal = calculateSubtotal(rooms, reservation.getCheckIn(), 
                reservation.getCheckOut(), null, reservation);
            
            // Get existing billing and update it
            Optional<Billing> billingOpt = billingService.getBillingForReservation(reservation);
            if (billingOpt.isPresent()) {
                return billingService.updateBillingSubtotal(billingOpt.get(), subtotal);
            } else {
                // Create new billing if it doesn't exist
                return billingService.createBilling(reservation, subtotal);
            }
        } catch (Exception e) {
            logger.logError("Failed to recalculate billing after change", e);
            return null;
        }
    }
    
    /**
     * Result class for reservation creation.
     */
    public static class ReservationCreationResult {
        public Reservation reservation;
        public Billing billing;
    }
    
    /**
     * Creates a reservation from form data.
     */
    public ReservationCreationResult createReservationFromForm(
            Guest guest,
            List<Room> rooms,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            List<ServiceAddon> addons,
            ReservationService reservationService,
            BillingService billingService) {
        
        ReservationCreationResult result = new ReservationCreationResult();
        
        // Use ReservationService.createReservation which handles everything
        Reservation reservation = reservationService.createReservation(
            guest, rooms, checkIn, checkOut, adults, children, addons);
        
        // Calculate and create billing
        double subtotal = calculateSubtotal(rooms, checkIn, checkOut, null, null);
        Billing billing = billingService.createBilling(reservation, subtotal);
        
        result.reservation = reservation;
        result.billing = billing;
        
        return result;
    }
}

