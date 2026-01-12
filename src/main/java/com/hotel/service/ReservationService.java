package com.hotel.service;

import com.hotel.app.AppConfig;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.util.LoggerService;
import com.hotel.util.RoomFactory;
import com.hotel.events.RoomAvailabilityPublisher;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles all reservation-related business logic.
 * Creates bookings, checks availability, suggests rooms for groups.
 */
public class ReservationService {
    
    private GuestRepository guestRepository;
    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private AddonRepository addonRepository;
    private RoomAvailabilityPublisher roomAvailabilityPublisher;
    private LoggerService logger;
    private EntityManager em;
    
    public ReservationService(EntityManager em) {
        this.em = em;
        this.guestRepository = new GuestRepository(em);
        this.roomRepository = new RoomRepository(em);
        this.reservationRepository = new ReservationRepository(em);
        this.addonRepository = new AddonRepository(em);
        this.roomAvailabilityPublisher = new RoomAvailabilityPublisher();
        this.logger = LoggerService.getInstance();
    }
    
    /**
     * Makes sure dates are valid - check-in can't be in the past,
     * and check-out must be after check-in.
     */
    public boolean validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return false;
        }
        
        if (checkIn.isBefore(LocalDate.now())) {
            logger.logWarning("Check-in date is in the past");
            return false;
        }
        
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            logger.logWarning("Check-out date must be after check-in date");
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if any rooms of this type are available for the date range.
     * Returns true if at least one room is free.
     */
    public boolean isRoomAvailable(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        try {
            logger.logInfo("Checking availability for " + roomType + " from " + checkIn + " to " + checkOut);
            List<Room> availableRooms = roomRepository.findAvailableByTypeAndDateRange(roomType, checkIn, checkOut);
            boolean available = !availableRooms.isEmpty();
            logger.logInfo("Room availability check: " + roomType + " = " + available + " (" + availableRooms.size() + " rooms)");
            return available;
        } catch (Exception e) {
            logger.logError("Error checking room availability", e);
            return false;
        }
    }
    
    /**
     * Gets the actual list of available rooms for a date range.
     * Clears cache first to make sure we get fresh data from database.
     */
    public List<Room> getAvailableRooms(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        try {
            // Clear cache so we don't get stale data
            em.clear();
            
            logger.logInfo("Getting available rooms: " + roomType + " from " + checkIn + " to " + checkOut);
            List<Room> rooms = roomRepository.findAvailableByTypeAndDateRange(roomType, checkIn, checkOut);
            logger.logInfo("Found " + rooms.size() + " available " + roomType + " rooms");
            return rooms;
        } catch (Exception e) {
            logger.logError("Error getting available rooms", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Suggests room combinations for group bookings based on occupancy rules.
     * Rules: Single/Deluxe/Penthouse = 2 people max, Double = 4 people max.
     */
    public List<RoomSuggestion> suggestRooms(int numAdults, int numChildren, LocalDate checkIn, LocalDate checkOut) {
        logger.logInfo("=== suggestRooms called ===");
        logger.logInfo("Parameters: " + numAdults + " adults, " + numChildren + " children, " + checkIn + " to " + checkOut);
        
        if (checkIn == null || checkOut == null) {
            logger.logError("Dates are null in suggestRooms");
            return new ArrayList<>();
        }
        
        if (roomRepository == null) {
            logger.logError("roomRepository is NULL in suggestRooms!");
            return new ArrayList<>();
        }
        
        List<RoomSuggestion> suggestions = new ArrayList<>();
        int totalPeople = numAdults + numChildren;
        logger.logInfo("Total people: " + totalPeople);
        
        // For 3-4 adults: suggest 1 double OR 2 singles
        if (totalPeople >= 3 && totalPeople <= 4) {
            // Option 1: One double room
            if (isRoomAvailable(RoomType.DOUBLE, checkIn, checkOut)) {
                List<Room> doubles = getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut);
                if (!doubles.isEmpty()) {
                    suggestions.add(new RoomSuggestion(doubles.get(0), 1));
                }
            }
            
            // Option 2: Two single rooms
            if (isRoomAvailable(RoomType.SINGLE, checkIn, checkOut)) {
                List<Room> singles = getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
                if (singles.size() >= 2) {
                    suggestions.add(new RoomSuggestion(singles.get(0), 2));
                }
            }
        }
        
        // For >4 adults: suggest multiple double rooms OR combination of double and single rooms
        else if (totalPeople > 4) {
            List<Room> doubles = getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut);
            List<Room> singles = getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
            
            // Option 1: Multiple double rooms (most efficient)
            int doubleRoomsNeeded = (int) Math.ceil(totalPeople / 4.0);
            if (doubles.size() >= doubleRoomsNeeded) {
                suggestions.add(new RoomSuggestion(doubles.get(0), doubleRoomsNeeded));
            }
            
            // Option 2: Combination of double and single rooms until capacity is satisfied
            // Use as many double rooms as possible, then fill with singles
            int fullDoubleRooms = totalPeople / 4; // Number of full double rooms (4 people each)
            int remainingPeople = totalPeople % 4; // Remaining people after full doubles
            
            if (fullDoubleRooms > 0 && doubles.size() >= fullDoubleRooms) {
                if (remainingPeople > 0) {
                    // Need additional single rooms for remaining people
                    int singleRoomsNeeded = (int) Math.ceil(remainingPeople / 2.0); // Singles hold 2 people each
                    if (singles.size() >= singleRoomsNeeded) {
                        // Create combination suggestion: double rooms + single rooms
                        suggestions.add(new RoomSuggestion(
                            doubles.get(0), fullDoubleRooms, 
                            RoomType.SINGLE, singleRoomsNeeded
                        ));
                    }
                } else {
                    // Exact fit with doubles only
                    suggestions.add(new RoomSuggestion(doubles.get(0), fullDoubleRooms));
                }
            }
            
            // Option 3: All single rooms (alternative option)
            int allSingleRoomsNeeded = (int) Math.ceil(totalPeople / 2.0);
            if (singles.size() >= allSingleRoomsNeeded) {
                suggestions.add(new RoomSuggestion(singles.get(0), allSingleRoomsNeeded));
            }
        }
        
        // For 1-2 people: suggest single room
        else {
            if (isRoomAvailable(RoomType.SINGLE, checkIn, checkOut)) {
                List<Room> singles = getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
                if (!singles.isEmpty()) {
                    suggestions.add(new RoomSuggestion(singles.get(0), 1));
                }
            }
        }
        
        logger.logInfo("=== suggestRooms returning " + suggestions.size() + " suggestions ===");
        return suggestions;
    }
    
    /**
     * Validate occupancy for selected rooms
     */
    public boolean validateOccupancy(List<Room> rooms, int numAdults, int numChildren) {
        int totalPeople = numAdults + numChildren;
        int totalCapacity = 0;
        
        for (Room room : rooms) {
            switch (room.getType()) {
                case SINGLE:
                case DELUXE:
                case PENTHOUSE:
                    totalCapacity += 2;
                    break;
                case DOUBLE:
                    totalCapacity += 4;
                    break;
            }
        }
        
        return totalCapacity >= totalPeople;
    }
    
    /**
     * Create a reservation
     */
    public Reservation createReservation(Guest guest, List<Room> rooms, LocalDate checkIn, 
                                        LocalDate checkOut, int numAdults, int numChildren,
                                        List<ServiceAddon> addons) {
        em.getTransaction().begin();
        
        try {
            // Validate dates
            if (!validateDates(checkIn, checkOut)) {
                throw new IllegalArgumentException("Invalid dates");
            }
            
            // Validate occupancy
            if (!validateOccupancy(rooms, numAdults, numChildren)) {
                throw new IllegalArgumentException("Selected rooms cannot accommodate all guests");
            }
            
            // Ensure guest is managed
            Guest managedGuest;
            if (guest.getId() == null) {
                em.persist(guest);
                managedGuest = guest;
            } else {
                managedGuest = em.contains(guest) ? guest : em.merge(guest);
            }
            
            // Create reservation
            Reservation reservation = new Reservation(checkIn, checkOut, numAdults, numChildren, managedGuest);
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setConfirmationNumber(generateConfirmationNumber());
            reservation = reservationRepository.save(reservation);
            
            // Assign rooms - ensure no duplicates
            java.util.Set<Long> addedRoomIds = new java.util.HashSet<>();
            for (Room room : rooms) {
                // Skip if room already added (prevent duplicates)
                if (addedRoomIds.contains(room.getId())) {
                    logger.logWarning("Skipping duplicate room: " + room.getId() + " (Room #" + room.getRoomNumber() + ")");
                    continue;
                }
                
                // Ensure room is managed
                Room managedRoom = em.merge(room);
                ReservationRoom reservationRoom = new ReservationRoom(reservation, managedRoom);
                reservation.getReservationRooms().add(reservationRoom);
                managedRoom.setStatus(RoomStatus.OCCUPIED);
                roomRepository.save(managedRoom);
                addedRoomIds.add(managedRoom.getId());
            }
            
            logger.logInfo("Created reservation with " + addedRoomIds.size() + " rooms (from " + rooms.size() + " requested)");
            
            // Add addons - ensure addons are managed entities and explicitly persist ReservationAddon
            for (ServiceAddon addon : addons) {
                // Merge the addon to ensure it's managed in the current session
                ServiceAddon managedAddon = em.merge(addon);
                
                // Check if this addon is already associated with this reservation
                // (to avoid duplicate composite keys)
                boolean alreadyExists = reservation.getReservationAddons().stream()
                    .anyMatch(ra -> ra.getAddon().getId().equals(managedAddon.getId()));
                
                if (!alreadyExists) {
                    ReservationAddon reservationAddon = new ReservationAddon(reservation, managedAddon, 1);
                    // Explicitly persist ReservationAddon instead of relying on cascade
                    em.persist(reservationAddon);
                    reservation.getReservationAddons().add(reservationAddon);
                }
            }
            
            // Flush to ensure all entities are persisted before commit
            em.flush();
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "CREATE_RESERVATION", "Reservation", reservation.getId(), 
                "Reservation created for guest: " + guest.getName());
            
            return reservation;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to create reservation", e);
            throw e;
        }
    }
    
    /**
     * Automatically update reservation status based on payment, check-in, and cancellation status
     */
    public void updateReservationStatus(Reservation reservation) {
        if (reservation == null) {
            return;
        }
        
        // Don't change status if already cancelled or checked out
        if (reservation.getStatus() == ReservationStatus.CANCELLED || 
            reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            return;
        }
        
        // Check if fully paid
        boolean isFullyPaid = false;
        if (reservation.getBilling() != null) {
            double balance = reservation.getBilling().getBalanceAmount();
            isFullyPaid = balance <= 0;
        }
        
        // Update status based on conditions
        // Priority: CANCELLED > CHECKED_OUT > CHECKED_IN > CONFIRMED > PENDING
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            // Already cancelled, don't change
            return;
        }
        
        // Status will be updated by specific actions (check-in, check-out, cancel)
        // This method is for payment-based updates
        if (isFullyPaid && reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            logger.logInfo("Reservation " + reservation.getId() + " status updated to CONFIRMED (fully paid)");
        }
    }
    
    /**
     * Cancel a reservation
     */
    public void cancelReservation(Long reservationId) {
        em.getTransaction().begin();
        
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
            reservation.setStatus(ReservationStatus.CANCELLED);
            
            // Free rooms
            for (ReservationRoom rr : reservation.getReservationRooms()) {
                Room room = rr.getRoom();
                room.setStatus(RoomStatus.AVAILABLE);
                roomRepository.save(room);
                
                // Notify observers
                roomAvailabilityPublisher.publishRoomAvailable(room);
            }
            
            reservationRepository.save(reservation);
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "CANCEL_RESERVATION", "Reservation", reservationId, 
                "Reservation cancelled");
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to cancel reservation", e);
            throw e;
        }
    }
    
    /**
     * Check-in a reservation
     */
    public void checkInReservation(Long reservationId) {
        em.getTransaction().begin();
        
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
            // Can only check in if confirmed
            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                throw new IllegalStateException("Reservation must be confirmed before check-in");
            }
            
            reservation.setStatus(ReservationStatus.CHECKED_IN);
            reservationRepository.save(reservation);
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "CHECK_IN", "Reservation", reservationId, 
                "Guest checked in");
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to check in reservation", e);
            throw e;
        }
    }
    
    /**
     * Checkout a reservation (mark as checked out and free rooms)
     * Supports early checkout - if actualCheckOutDate is provided and earlier than scheduled,
     * updates the check-out date and recalculates billing
     */
    public void checkoutReservation(Long reservationId) {
        checkoutReservation(reservationId, null);
    }
    
    /**
     * Checkout a reservation with optional early check-out date
     * @param reservationId The reservation ID
     * @param actualCheckOutDate Optional actual check-out date (for early checkout). If null, uses scheduled check-out date.
     */
    public void checkoutReservation(Long reservationId, java.time.LocalDate actualCheckOutDate) {
        em.getTransaction().begin();
        
        try {
            // Use findByIdWithRooms to eagerly fetch reservationRooms to avoid lazy initialization issues
            Reservation reservation = reservationRepository.findByIdWithRooms(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
            
            // Allow checkout regardless of status - clicking checkout will automatically set status to CHECKED_OUT
            // Check if this is a billing recalculation for an already checked-out reservation
            boolean isBillingRecalculation = reservation.getStatus() == ReservationStatus.CHECKED_OUT && 
                actualCheckOutDate != null && actualCheckOutDate.isBefore(reservation.getCheckOut());
            
            // Handle checkout date update - allow any date for flexibility
            if (actualCheckOutDate != null && !actualCheckOutDate.equals(reservation.getCheckOut())) {
                // If checkout date is before check-in, use check-in date as minimum
                final java.time.LocalDate finalCheckOutDate = actualCheckOutDate.isBefore(reservation.getCheckIn()) 
                    ? reservation.getCheckIn() 
                    : actualCheckOutDate;
                
                if (actualCheckOutDate.isBefore(reservation.getCheckIn())) {
                    logger.logInfo("Checkout date adjusted to check-in date: " + finalCheckOutDate);
                }
                reservation.setCheckOut(finalCheckOutDate);
                logger.logInfo("Checkout date updated to " + finalCheckOutDate);
                
                // Recalculate billing based on actual nights stayed
                BillingService billingService = AppConfig.createBillingService();
                Optional<Billing> billingOpt = billingService.getBillingForReservation(reservation);
                if (billingOpt.isPresent()) {
                    Billing billing = billingOpt.get();
                    long actualNights = java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckIn(), finalCheckOutDate);
                    long scheduledNights = java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckIn(), reservation.getCheckOut());
                    
                    // Ensure actualNights is at least 1 (minimum one night stay)
                    if (actualNights < 1) {
                        actualNights = 1;
                    }
                    
                    // Make actualNights effectively final for lambda
                    final long finalActualNights = actualNights;
                    
                    if (finalActualNights < scheduledNights) {
                        // Recalculate subtotal based on actual nights
                        double roomCharges = reservation.getReservationRooms().stream()
                            .mapToDouble(rr -> rr.getRoom().getBasePrice() * finalActualNights)
                            .sum();
                        
                        double addonCharges = reservation.getReservationAddons().stream()
                            .mapToDouble(ra -> {
                                ServiceAddon addon = ra.getAddon();
                                if (addon == null) return 0.0;
                                if (addon.getPricingModel() == com.hotel.model.PricingModel.PER_NIGHT) {
                                    return addon.getPrice() * ra.getQuantity() * finalActualNights;
                                } else {
                                    return addon.getPrice() * ra.getQuantity();
                                }
                            })
                            .sum();
                        
                        double newSubtotal = roomCharges + addonCharges;
                        billingService.updateBillingSubtotal(billing, newSubtotal);
                        logger.logInfo("Recalculated billing for early checkout: " + finalActualNights + " nights (was " + scheduledNights + ")");
                    }
                }
            }
            
            // Always set status to CHECKED_OUT and free rooms (even if already checked out, ensure rooms are available)
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            
            // Free rooms (only update if not already available to avoid unnecessary updates)
            for (ReservationRoom rr : reservation.getReservationRooms()) {
                Room room = rr.getRoom();
                if (room.getStatus() != RoomStatus.AVAILABLE) {
                    room.setStatus(RoomStatus.AVAILABLE);
                    roomRepository.save(room);
                    
                    // Notify observers
                    roomAvailabilityPublisher.publishRoomAvailable(room);
                }
            }
            
            reservationRepository.save(reservation);
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "CHECKOUT", "Reservation", reservationId, 
                "Guest checked out" + (actualCheckOutDate != null ? " (early checkout)" : ""));
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to checkout reservation", e);
            throw e;
        }
    }
    
    /**
     * Inner class for room suggestions
     */
    public static class RoomSuggestion {
        private Room room;
        private int quantity;
        private RoomType secondaryRoomType; // For combination suggestions
        private int secondaryQuantity; // For combination suggestions
        private String description; // Description of the suggestion
        
        // Constructor for single room type suggestion
        public RoomSuggestion(Room room, int quantity) {
            this.room = room;
            this.quantity = quantity;
            this.secondaryRoomType = null;
            this.secondaryQuantity = 0;
            this.description = null;
        }
        
        // Constructor for combination suggestion (double + single rooms)
        public RoomSuggestion(Room primaryRoom, int primaryQuantity, RoomType secondaryRoomType, int secondaryQuantity) {
            this.room = primaryRoom;
            this.quantity = primaryQuantity;
            this.secondaryRoomType = secondaryRoomType;
            this.secondaryQuantity = secondaryQuantity;
            this.description = "Combination: " + primaryQuantity + " " + primaryRoom.getType() + 
                              (secondaryQuantity > 0 ? " + " + secondaryQuantity + " " + secondaryRoomType : "");
        }
        
        // Constructor with description
        public RoomSuggestion(Room room, int quantity, String description) {
            this.room = room;
            this.quantity = quantity;
            this.secondaryRoomType = null;
            this.secondaryQuantity = 0;
            this.description = description;
        }
        
        public Room getRoom() {
            return room;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public RoomType getSecondaryRoomType() {
            return secondaryRoomType;
        }
        
        public int getSecondaryQuantity() {
            return secondaryQuantity;
        }
        
        public boolean isCombination() {
            return secondaryRoomType != null && secondaryQuantity > 0;
        }
        
        public String getDescription() {
            if (description != null) {
                return description;
            }
            if (isCombination()) {
                return "Combination: " + quantity + " " + room.getType() + 
                       " + " + secondaryQuantity + " " + secondaryRoomType;
            }
            return quantity + "x " + room.getType();
        }
    }
    
    public RoomAvailabilityPublisher getRoomAvailabilityPublisher() {
        return roomAvailabilityPublisher;
    }
    
    /**
     * Generate a unique confirmation number for a reservation
     * Format: HOTEL-YYYYMMDD-XXXXX (e.g., HOTEL-20241215-A3B2C)
     */
    private String generateConfirmationNumber() {
        String datePart = LocalDate.now().toString().replace("-", "");
        String randomPart = generateRandomAlphanumeric(5).toUpperCase();
        return "HOTEL-" + datePart + "-" + randomPart;
    }
    
    /**
     * Generate random alphanumeric string of specified length
     */
    private String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}



