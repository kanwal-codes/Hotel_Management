package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.FeedbackRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Handles feedback submission and validation.
 * Guests can only submit feedback after checkout and when balance is paid.
 */
public class FeedbackService {
    
    private FeedbackRepository feedbackRepository;
    private ReservationRepository reservationRepository;
    private LoggerService logger;
    private EntityManager em;
    
    public FeedbackService(EntityManager em) {
        this.em = em;
        this.feedbackRepository = new FeedbackRepository(em);
        this.reservationRepository = new ReservationRepository(em);
        this.logger = LoggerService.getInstance();
    }
    
    /**
     * Checks if a guest can submit feedback for a reservation.
     * Requirements: reservation must be checked out and balance must be zero.
     */
    public boolean canSubmitFeedback(Reservation reservation) {
        // Must be checked out first
        if (reservation.getStatus() != ReservationStatus.CHECKED_OUT) {
            logger.logWarning("Feedback submission denied: Reservation not checked out");
            return false;
        }
        
        // Must have billing and balance must be paid off
        Billing billing = reservation.getBilling();
        if (billing == null) {
            logger.logWarning("Feedback submission denied: No billing found");
            return false;
        }
        
        if (billing.getBalanceAmount() > 0) {
            logger.logWarning("Feedback submission denied: Balance not settled");
            return false;
        }
        
        return true;
    }
    
    /**
     * Submits feedback from a guest.
     * Validates eligibility, rating, and automatically tags sentiment.
     */
    public Feedback submitFeedback(Guest guest, Reservation reservation, int rating, String comments) {
        em.getTransaction().begin();
        
        try {
            // Make sure they're allowed to submit
            if (!canSubmitFeedback(reservation)) {
                throw new IllegalArgumentException("Feedback cannot be submitted: Reservation not eligible");
            }
            
            // Rating must be 1-5
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            
            // Limit comment length to 1000 characters
            if (comments != null && comments.length() > 1000) {
                comments = comments.substring(0, 1000);
            }
            
            // Create the feedback entry
            Feedback feedback = new Feedback(guest, reservation, rating, comments);
            
            // Automatically tag sentiment based on rating
            if (rating >= 4) {
                feedback.setSentimentTag("POSITIVE");
            } else if (rating == 3) {
                feedback.setSentimentTag("NEUTRAL");
            } else {
                feedback.setSentimentTag("NEGATIVE");
            }
            
            feedback = feedbackRepository.save(feedback);
            em.getTransaction().commit();
            
            logger.logActivity(guest.getName(), "SUBMIT_FEEDBACK", "Feedback", feedback.getId(), 
                "Feedback submitted with rating: " + rating);
            
            return feedback;
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to submit feedback", e);
            throw e;
        }
    }
    
    /**
     * Gets feedback for a specific reservation.
     */
    public Optional<Feedback> getFeedbackForReservation(Reservation reservation) {
        return feedbackRepository.findByReservation(reservation);
    }
    
    /**
     * Gets all feedback entries in the system.
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}

