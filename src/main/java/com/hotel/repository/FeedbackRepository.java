package com.hotel.repository;

import com.hotel.model.Feedback;
import com.hotel.model.Reservation;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

// repository for feedback database operations
public class FeedbackRepository {
    private EntityManager em;
    
    public FeedbackRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves feedback to the database
    public Feedback save(Feedback feedback) {
        if (feedback.getId() == null) {
            em.persist(feedback);
        } else {
            feedback = em.merge(feedback);
        }
        return feedback;
    }
    
    // finds feedback by its id
    public Optional<Feedback> findById(Long id) {
        return Optional.ofNullable(em.find(Feedback.class, id));
    }
    
    // gets all feedback entries in the database
    public List<Feedback> findAll() {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<Feedback> query = em.createQuery("SELECT f FROM Feedback f", Feedback.class);
            List<Feedback> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            return results;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[FeedbackRepository] ERROR finding all feedback: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    // finds feedback for a specific reservation
    // usually there's only one feedback per reservation
    public Optional<Feedback> findByReservation(Reservation reservation) {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<Feedback> query = em.createQuery(
                "SELECT f FROM Feedback f WHERE f.reservation = :reservation", Feedback.class);
            query.setParameter("reservation", reservation);
            List<Feedback> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[FeedbackRepository] ERROR finding feedback by reservation: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}



