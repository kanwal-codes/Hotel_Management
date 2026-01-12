package com.hotel.repository;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// repository for reservation database operations
// handles all queries related to reservations
public class ReservationRepository {
    private EntityManager em;
    
    public ReservationRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves a reservation to the database
    // creates new reservation if id is null, updates existing if id exists
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            em.persist(reservation);
        } else {
            reservation = em.merge(reservation);
        }
        return reservation;
    }
    
    // finds a reservation by its id
    // returns basic reservation without loading related rooms/addons
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(em.find(Reservation.class, id));
    }
    
    // finds a reservation and loads all related rooms and addons
    // uses separate queries to avoid hibernate's MultipleBagFetchException
    // use this when you need to access the rooms or addons collections
    public Optional<Reservation> findByIdWithRooms(Long id) {
        // first query: load reservation with rooms
        TypedQuery<Reservation> query1 = em.createQuery(
            "SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room " +
            "WHERE r.id = :id", Reservation.class);
        query1.setParameter("id", id);
        List<Reservation> results1 = query1.getResultList();
        if (results1.isEmpty()) {
            return Optional.empty();
        }
        Reservation reservation = results1.get(0);
        
        // second query: load addons separately
        // can't fetch both collections in one query - hibernate doesn't allow it
        TypedQuery<Reservation> query2 = em.createQuery(
            "SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.reservationAddons ra " +
            "LEFT JOIN FETCH ra.addon " +
            "WHERE r.id = :id", Reservation.class);
        query2.setParameter("id", id);
        List<Reservation> results2 = query2.getResultList();
        if (!results2.isEmpty()) {
            // access the collection to make sure it's loaded
            reservation.getReservationAddons().size();
        }
        
        return Optional.of(reservation);
    }
    
    public List<Reservation> findAll() {
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r", Reservation.class);
        return query.getResultList();
    }
    
    public List<Reservation> findByGuest(Guest guest) {
        if (guest == null || guest.getId() == null) {
            return new java.util.ArrayList<>();
        }
        // use guest id instead of guest entity to avoid detached entity issues
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.guest.id = :guestId", Reservation.class);
        query.setParameter("guestId", guest.getId());
        return query.getResultList();
    }
    
    public List<Reservation> findByStatus(ReservationStatus status) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.status = :status", Reservation.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE " +
            "(r.checkIn <= :endDate AND r.checkOut >= :startDate)", Reservation.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Reservation> searchByGuestName(String name) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE LOWER(r.guest.name) LIKE LOWER(:name)", Reservation.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
    
    public List<Reservation> searchByGuestPhone(String phone) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.guest.phone = :phone", Reservation.class);
        query.setParameter("phone", phone);
        return query.getResultList();
    }

    public List<Reservation> findByGuestNameOrPhone(String searchTerm) {
        String pattern = "%" + searchTerm.toLowerCase() + "%";
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE " +
                "LOWER(r.guest.name) LIKE :pattern OR LOWER(r.guest.phone) LIKE :pattern",
            Reservation.class);
        query.setParameter("pattern", pattern);
        return query.getResultList();
    }
    
    public Optional<Reservation> findByConfirmationNumber(String confirmationNumber) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT r FROM Reservation r WHERE r.confirmationNumber = :confirmationNumber", Reservation.class);
        query.setParameter("confirmationNumber", confirmationNumber);
        List<Reservation> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<Reservation> findByIdOrConfirmationNumber(String searchTerm) {
        // try to parse as long id first
        try {
            Long id = Long.parseLong(searchTerm);
            Optional<Reservation> byId = findById(id);
            if (byId.isPresent()) {
                return byId;
            }
        } catch (NumberFormatException e) {
            // not a number, try confirmation number
        }
        // try confirmation number
        return findByConfirmationNumber(searchTerm);
    }
    
    public void delete(Reservation reservation) {
        em.remove(em.contains(reservation) ? reservation : em.merge(reservation));
    }
}



