package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.WaitlistRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.events.RoomAvailabilityPublisher;
import com.hotel.events.WaitlistSubscriber;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Handles waitlist management.
 * Uses Observer pattern - subscribes to room availability notifications.
 */
public class WaitlistService {
    
    private WaitlistRepository waitlistRepository;
    private GuestRepository guestRepository;
    private RoomAvailabilityPublisher roomAvailabilityPublisher;
    private WaitlistSubscriber waitlistSubscriber;
    private LoggerService logger;
    private EntityManager em;
    
    public WaitlistService(EntityManager em, RoomAvailabilityPublisher publisher) {
        this.em = em;
        this.waitlistRepository = new WaitlistRepository(em);
        this.guestRepository = new GuestRepository(em);
        this.roomAvailabilityPublisher = publisher;
        this.waitlistSubscriber = new WaitlistSubscriber();
        this.logger = LoggerService.getInstance();
        
        // Subscribe to notifications when rooms become available
        roomAvailabilityPublisher.attach(waitlistSubscriber);
    }
    
    /**
     * Adds a guest to the waitlist when rooms aren't available.
     */
    public Waitlist addToWaitlist(Guest guest, RoomType requestedType,
                                   LocalDate dateRangeStart, LocalDate dateRangeEnd) {
        return addToWaitlist(guest, requestedType, dateRangeStart, dateRangeEnd, null, null);
    }
    
    /**
     * Adds a guest to the waitlist with number of adults and children.
     */
    public Waitlist addToWaitlist(Guest guest, RoomType requestedType,
                                   LocalDate dateRangeStart, LocalDate dateRangeEnd,
                                   Integer numAdults, Integer numChildren) {
        em.getTransaction().begin();
        
        try {
            Waitlist waitlist = new Waitlist(guest, requestedType, dateRangeStart, dateRangeEnd, numAdults, numChildren);
            waitlist = waitlistRepository.save(waitlist);
            
            guest.setWaitlist(waitlist);
            guestRepository.save(guest);
            
            em.getTransaction().commit();
            
            logger.logActivity("SYSTEM", "ADD_TO_WAITLIST", "Waitlist", waitlist.getId(), 
                "Guest " + guest.getName() + " added to waitlist for " + requestedType + 
                (numAdults != null ? " (" + numAdults + " adults" + (numChildren != null && numChildren > 0 ? ", " + numChildren + " children" : "") + ")" : ""));
            
            return waitlist;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to add to waitlist", e);
            throw e;
        }
    }
    
    /**
     * Gets all waitlist entries for a specific room type.
     * Used when a room becomes available to see who's waiting.
     */
    public List<Waitlist> getWaitlistByRoomType(RoomType roomType) {
        return waitlistRepository.findByRoomType(roomType);
    }
    
    /**
     * Gets all waitlist entries.
     */
    public List<Waitlist> getAllWaitlist() {
        return waitlistRepository.findAll();
    }
    
    /**
     * Gets waitlist entries by status (PENDING, NOTIFIED, CONVERTED).
     */
    public List<Waitlist> getWaitlistByStatus(String status) {
        return waitlistRepository.findByStatus(status);
    }
    
    /**
     * Gets a specific waitlist entry by ID.
     */
    public Optional<Waitlist> getWaitlistById(Long id) {
        return waitlistRepository.findById(id);
    }
    
    /**
     * Gets notifications from the observer.
     * These are messages about rooms becoming available.
     */
    public List<String> getNotifications() {
        return waitlistSubscriber.getNotifications();
    }
    
    /**
     * Clears the notification list.
     */
    public void clearNotifications() {
        waitlistSubscriber.clearNotifications();
    }
    
    /**
     * Removes a guest from the waitlist.
     * Usually called when waitlist entry is converted to a reservation.
     */
    public void removeFromWaitlist(Long waitlistId) {
        em.getTransaction().begin();
        
        try {
            Optional<Waitlist> waitlistOpt = waitlistRepository.findById(waitlistId);
            if (waitlistOpt.isPresent()) {
                Waitlist waitlist = waitlistOpt.get();
                Guest guest = waitlist.getGuest();
                
                // Remove the waitlist link from guest
                guest.setWaitlist(null);
                guestRepository.save(guest);
                
                // Delete the waitlist entry
                waitlistRepository.delete(waitlist);
                
                em.getTransaction().commit();
                
                logger.logActivity("SYSTEM", "REMOVE_FROM_WAITLIST", "Waitlist", waitlistId, 
                    "Waitlist entry removed");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to remove from waitlist", e);
            throw e;
        }
    }
    
    /**
     * Updates the status of a waitlist entry.
     * Status can be PENDING, NOTIFIED, or CONVERTED.
     */
    public void updateWaitlistStatus(Long waitlistId, String status) {
        em.getTransaction().begin();
        
        try {
            Optional<Waitlist> waitlistOpt = waitlistRepository.findById(waitlistId);
            if (waitlistOpt.isPresent()) {
                Waitlist waitlist = waitlistOpt.get();
                waitlist.setStatus(status);
                waitlistRepository.save(waitlist);
                
                em.getTransaction().commit();
                
                logger.logActivity("SYSTEM", "UPDATE_WAITLIST_STATUS", "Waitlist", waitlistId, 
                    "Status updated to " + status);
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to update waitlist status", e);
            throw e;
        }
    }
}


