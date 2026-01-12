package com.hotel.repository;

import com.hotel.model.Billing;
import com.hotel.model.Payment;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

// repository for payment database operations
public class PaymentRepository {
    private EntityManager em;
    
    public PaymentRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves a payment transaction to the database
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            em.persist(payment);
        } else {
            payment = em.merge(payment);
        }
        return payment;
    }
    
    // finds a payment by its id
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(em.find(Payment.class, id));
    }
    
    // gets all payments for a specific billing record
    // returns them in reverse chronological order (newest first)
    public List<Payment> findByBilling(Billing billing) {
        // need transaction for jpa queries
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        
        try {
            TypedQuery<Payment> query = em.createQuery(
                "SELECT p FROM Payment p WHERE p.billing = :billing ORDER BY p.createdAt DESC", Payment.class);
            query.setParameter("billing", billing);
            List<Payment> results = query.getResultList();
            
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            
            return results;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    // gets all payments in the database
    public List<Payment> findAll() {
        TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p", Payment.class);
        return query.getResultList();
    }
}



