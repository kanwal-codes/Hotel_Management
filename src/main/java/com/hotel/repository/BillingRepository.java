package com.hotel.repository;

import com.hotel.model.Billing;
import com.hotel.model.Reservation;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

// repository for billing database operations
public class BillingRepository {
    private EntityManager em;
    
    public BillingRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves a billing record to the database
    public Billing save(Billing billing) {
        if (billing.getId() == null) {
            em.persist(billing);
        } else {
            billing = em.merge(billing);
        }
        return billing;
    }
    
    // finds a billing by its id
    public Optional<Billing> findById(Long id) {
        return Optional.ofNullable(em.find(Billing.class, id));
    }
    
    // finds the billing record for a specific reservation
    // each reservation has one billing record
    public Optional<Billing> findByReservation(Reservation reservation) {
        TypedQuery<Billing> query = em.createQuery(
            "SELECT b FROM Billing b WHERE b.reservation = :reservation", Billing.class);
        query.setParameter("reservation", reservation);
        List<Billing> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // gets all billing records in the database
    public List<Billing> findAll() {
        TypedQuery<Billing> query = em.createQuery("SELECT b FROM Billing b", Billing.class);
        return query.getResultList();
    }
}



