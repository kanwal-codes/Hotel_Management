package com.hotel.repository;

import com.hotel.model.Waitlist;
import com.hotel.model.RoomType;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// repository for waitlist database operations
public class WaitlistRepository {
    private EntityManager em;
    
    public WaitlistRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves a waitlist entry to the database
    public Waitlist save(Waitlist waitlist) {
        if (waitlist.getId() == null) {
            em.persist(waitlist);
        } else {
            waitlist = em.merge(waitlist);
        }
        return waitlist;
    }
    
    // finds a waitlist entry by its id
    public Optional<Waitlist> findById(Long id) {
        return Optional.ofNullable(em.find(Waitlist.class, id));
    }
    
    // gets all waitlist entries
    public List<Waitlist> findAll() {
        TypedQuery<Waitlist> query = em.createQuery("SELECT w FROM Waitlist w", Waitlist.class);
        return query.getResultList();
    }
    
    // finds all pending waitlist entries for a specific room type
    // used when a room becomes available to see who's waiting
    public List<Waitlist> findByRoomType(RoomType roomType) {
        TypedQuery<Waitlist> query = em.createQuery(
            "SELECT w FROM Waitlist w WHERE w.requestedType = :roomType AND w.status = 'PENDING'", Waitlist.class);
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }
    
    // finds waitlist entries by status (PENDING, NOTIFIED, CONVERTED)
    public List<Waitlist> findByStatus(String status) {
        TypedQuery<Waitlist> query = em.createQuery(
            "SELECT w FROM Waitlist w WHERE w.status = :status", Waitlist.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    // deletes a waitlist entry from the database
    public void delete(Waitlist waitlist) {
        em.remove(em.contains(waitlist) ? waitlist : em.merge(waitlist));
    }
}


