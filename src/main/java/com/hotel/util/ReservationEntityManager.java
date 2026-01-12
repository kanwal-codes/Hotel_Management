package com.hotel.util;

import com.hotel.app.AppConfig;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationAddon;
import com.hotel.model.ReservationRoom;
import com.hotel.model.Room;
import com.hotel.model.ServiceAddon;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

// utility class for managing jpa entity operations related to reservations
// handles complex transactions for updating reservation rooms and addons
// extracted from AdminReservationController to reduce controller size
public final class ReservationEntityManager {
    
    private ReservationEntityManager() {
        // utility class - prevent instantiation
    }
    
    // updates reservation rooms within a transaction
    // manages entity state to avoid detached entity exceptions
    public static Reservation updateReservationRooms(
            Long reservationId,
            List<Room> newRooms,
            LoggerService logger) {
        
        EntityManager em = AppConfig.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // reload reservation to get latest version
            Reservation managedReservation = em.find(Reservation.class, reservationId);
            if (managedReservation == null) {
                throw new IllegalArgumentException("Reservation not found: " + reservationId);
            }
            
            // remove old reservationroom entities
            List<ReservationRoom> oldRooms = new java.util.ArrayList<>(managedReservation.getReservationRooms());
            for (ReservationRoom oldRoom : oldRooms) {
                managedReservation.getReservationRooms().remove(oldRoom);
                em.remove(oldRoom);
            }
            em.flush(); // flush to ensure deletions are processed
            
            // add new rooms - make sure room entities are managed
            for (Room room : newRooms) {
                if (room == null || room.getId() == null) {
                    logger.logError("Invalid room in newRooms: " + room, 
                        new IllegalArgumentException("Room cannot be null or have null ID"));
                    continue;
                }
                
                // find room in database to make sure it's managed
                Room managedRoom = em.find(Room.class, room.getId());
                if (managedRoom == null) {
                    logger.logError("Room with ID " + room.getId() + " not found in database", null);
                    continue;
                }
                
                // create new reservationroom with managed entities
                ReservationRoom rr = new ReservationRoom(managedReservation, managedRoom);
                managedReservation.getReservationRooms().add(rr);
            }
            
            em.flush();
            em.getTransaction().commit();
            
            logger.logInfo("Successfully updated " + newRooms.size() + " rooms for reservation #" + reservationId);
            
            // reload reservation with all associations
            return reloadReservationWithAssociations(reservationId, em);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.logError("Failed to update rooms for reservation", e);
            throw e;
        } finally {
            em.close();
        }
    }
    
    // updates reservation addons within a transaction
    // manages entity state to avoid detached entity exceptions
    public static Reservation updateReservationAddons(
            Long reservationId,
            List<ReservationAddon> newAddons,
            LoggerService logger) {
        
        EntityManager em = AppConfig.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // reload reservation to make sure it's managed
            Reservation managedReservation = em.find(Reservation.class, reservationId);
            if (managedReservation == null) {
                throw new IllegalArgumentException("Reservation not found: " + reservationId);
            }
            
            // remove old reservationaddon entities
            List<ReservationAddon> oldAddons = new java.util.ArrayList<>(managedReservation.getReservationAddons());
            for (ReservationAddon oldAddon : oldAddons) {
                managedReservation.getReservationAddons().remove(oldAddon);
                em.remove(oldAddon);
            }
            em.flush(); // flush to ensure deletions are processed
            
            // add new services - make sure addon entities are managed
            for (ReservationAddon ra : newAddons) {
                if (ra.getAddon() == null || ra.getAddon().getId() == null) {
                    logger.logError("ReservationAddon has null addon: " + ra, 
                        new IllegalArgumentException("Addon cannot be null"));
                    continue;
                }
                
                // find addon in database to make sure it's managed
                ServiceAddon managedAddon = em.find(ServiceAddon.class, ra.getAddon().getId());
                if (managedAddon == null) {
                    logger.logError("Addon with ID " + ra.getAddon().getId() + " not found in database", null);
                    continue;
                }
                
                // create new reservationaddon with managed entities
                ReservationAddon newRa = new ReservationAddon(managedReservation, managedAddon, ra.getQuantity());
                managedReservation.getReservationAddons().add(newRa);
            }
            
            em.flush();
            em.getTransaction().commit();
            
            logger.logInfo("Successfully updated " + newAddons.size() + " addons for reservation #" + reservationId);
            
            // reload reservation with all associations
            return reloadReservationWithAssociations(reservationId, em);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.logError("Failed to update addons for reservation", e);
            throw e;
        } finally {
            em.close();
        }
    }
    
    // reloads reservation with all associations eagerly fetched
    public static Reservation reloadReservationWithAssociations(Long reservationId) {
        EntityManager em = AppConfig.createEntityManager();
        try {
            return reloadReservationWithAssociations(reservationId, em);
        } finally {
            em.close();
        }
    }
    
    // reloads reservation with all associations (internal method with existing em)
    private static Reservation reloadReservationWithAssociations(Long reservationId, EntityManager em) {
        // first fetch with rooms
        javax.persistence.TypedQuery<Reservation> query1 = em.createQuery(
            "SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room " +
            "WHERE r.id = :id", Reservation.class);
        query1.setParameter("id", reservationId);
        Optional<Reservation> refreshed1 = query1.getResultList().stream().findFirst();
        
        if (refreshed1.isPresent()) {
            Reservation reservation = refreshed1.get();
            
            // then fetch addons separately to avoid MultipleBagFetchException
            javax.persistence.TypedQuery<Reservation> query2 = em.createQuery(
                "SELECT DISTINCT r FROM Reservation r " +
                "LEFT JOIN FETCH r.reservationAddons ra " +
                "LEFT JOIN FETCH ra.addon " +
                "WHERE r.id = :id", Reservation.class);
            query2.setParameter("id", reservationId);
            query2.getResultList().stream().findFirst().ifPresent(r -> {
                // initialize addons collection
                reservation.getReservationAddons().size();
            });
            
            return reservation;
        }
        
        return null;
    }
}

