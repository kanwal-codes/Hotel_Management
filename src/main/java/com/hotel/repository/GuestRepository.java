package com.hotel.repository;

import com.hotel.model.Guest;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

// repository for guest database operations
// handles all the database queries for guests
public class GuestRepository {
    private EntityManager em;
    
    public GuestRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves a guest to the database
    // creates new guest if id is null, updates existing if id exists
    public Guest save(Guest guest) {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            if (guest.getId() == null) {
                em.persist(guest);
            } else {
                guest = em.merge(guest);
            }
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            return guest;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    // finds a guest by their id
    public Optional<Guest> findById(Long id) {
        return Optional.ofNullable(em.find(Guest.class, id));
    }
    
    // finds a guest by email address
    // used to check if guest already exists when booking
    public Optional<Guest> findByEmail(String email) {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE g.email = :email", Guest.class);
        query.setParameter("email", email);
        List<Guest> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // finds a guest by phone number
    public Optional<Guest> findByPhone(String phone) {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE g.phone = :phone", Guest.class);
        query.setParameter("phone", phone);
        List<Guest> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // gets all guests in the database
    public List<Guest> findAll() {
        TypedQuery<Guest> query = em.createQuery("SELECT g FROM Guest g", Guest.class);
        return query.getResultList();
    }
    
    // searches for guests by name (partial match, case-insensitive)
    // used in admin search functionality
    public List<Guest> searchByName(String name) {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE LOWER(g.name) LIKE LOWER(:name)", Guest.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
    
    // finds a guest by their loyalty number
    // used when guests want to use loyalty points
    public Optional<Guest> findByLoyaltyNumber(String loyaltyNumber) {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE g.loyaltyNumber = :loyaltyNumber", Guest.class);
        query.setParameter("loyaltyNumber", loyaltyNumber);
        List<Guest> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // finds all guests who are enrolled in the loyalty program (have loyalty number)
    public List<Guest> findEnrolledGuests() {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE g.loyaltyNumber IS NOT NULL AND g.loyaltyNumber != ''", Guest.class);
        return query.getResultList();
    }
    
    // finds all guests who are not enrolled but have an account (email and password)
    // these are eligible for enrollment
    public List<Guest> findNonEnrolledGuestsWithAccounts() {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE (g.loyaltyNumber IS NULL OR g.loyaltyNumber = '') " +
            "AND g.email IS NOT NULL AND g.email != '' " +
            "AND g.customerPasswordHash IS NOT NULL AND g.customerPasswordHash != ''", Guest.class);
        return query.getResultList();
    }
    
    // finds all guests who have an account (email and password)
    // this includes both enrolled and non-enrolled guests with accounts
    public List<Guest> findAllGuestsWithAccounts() {
        TypedQuery<Guest> query = em.createQuery(
            "SELECT g FROM Guest g WHERE g.email IS NOT NULL AND g.email != '' " +
            "AND g.customerPasswordHash IS NOT NULL AND g.customerPasswordHash != ''", Guest.class);
        return query.getResultList();
    }
    
    // deletes a guest from the database
    public void delete(Guest guest) {
        em.remove(em.contains(guest) ? guest : em.merge(guest));
    }
}



