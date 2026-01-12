package com.hotel.repository;

import com.hotel.model.AmenityBooking;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

// repository for amenitybooking database operations
public class AmenityBookingRepository {
    private EntityManager em;
    
    public AmenityBookingRepository(EntityManager em) {
        this.em = em;
    }
    
    public AmenityBooking save(AmenityBooking booking) {
        if (booking.getId() == null) {
            em.persist(booking);
        } else {
            booking = em.merge(booking);
        }
        return booking;
    }
    
    public List<AmenityBooking> findAll() {
        TypedQuery<AmenityBooking> query = em.createQuery(
            "SELECT a FROM AmenityBooking a ORDER BY a.bookingDate, a.bookingTime", 
            AmenityBooking.class);
        return query.getResultList();
    }
}





