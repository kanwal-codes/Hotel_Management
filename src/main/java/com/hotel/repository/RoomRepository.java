package com.hotel.repository;

import com.hotel.model.Room;
import com.hotel.model.RoomStatus;
import com.hotel.model.RoomType;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// repository for room database operations
// handles all queries related to rooms
public class RoomRepository {
    private EntityManager em;
    
    public RoomRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves a room to the database
    // creates new room if id is null, updates existing if id exists
    public Room save(Room room) {
        if (room.getId() == null) {
            em.persist(room);
        } else {
            room = em.merge(room);
        }
        return room;
    }
    
    // finds a room by its id
    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(em.find(Room.class, id));
    }
    
    // finds a room by its room number (like "101" or "205")
    public Optional<Room> findByRoomNumber(String roomNumber) {
        TypedQuery<Room> query = em.createQuery(
            "SELECT r FROM Room r WHERE r.roomNumber = :roomNumber", Room.class);
        query.setParameter("roomNumber", roomNumber);
        List<Room> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Room> findAll() {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);
            List<Room> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            System.out.println("[RoomRepository] Found " + results.size() + " total rooms");
            return results;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[RoomRepository] ERROR finding all rooms: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    public List<Room> findByType(RoomType type) {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<Room> query = em.createQuery(
                "SELECT r FROM Room r WHERE r.type = :type", Room.class);
            query.setParameter("type", type);
            List<Room> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            return results;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[RoomRepository] ERROR finding rooms by type: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    public List<Room> findByStatus(RoomStatus status) {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<Room> query = em.createQuery(
                "SELECT r FROM Room r WHERE r.status = :status", Room.class);
            query.setParameter("status", status);
            List<Room> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            return results;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[RoomRepository] ERROR finding rooms by status: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    // finds rooms that are available for a specific date range
    // checks room status and excludes rooms with overlapping reservations
    // this is the main method used for booking availability checks
    public List<Room> findAvailableByTypeAndDateRange(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        // need transaction even for read operations
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        
        try {
            // clear caches to make sure we get fresh data from database
            em.clear();
            if (em.getEntityManagerFactory().getCache() != null) {
                em.getEntityManagerFactory().getCache().evictAll();
            }
            
            // first, get all rooms of this type that are marked as AVAILABLE
            String simpleQuery = "SELECT r.id FROM room r WHERE r.type = ? AND r.status = 'AVAILABLE'";
            javax.persistence.Query simpleIdQuery = em.createNativeQuery(simpleQuery);
            simpleIdQuery.setParameter(1, type.name());
            
            @SuppressWarnings("unchecked")
            List<Object> allAvailableIds = simpleIdQuery.getResultList();
            System.out.println("[RoomRepository] DEBUG: Found " + allAvailableIds.size() + " AVAILABLE " + type + " rooms (before reservation filter)");
            
            if (allAvailableIds.isEmpty()) {
                if (!transactionActive) {
                    em.getTransaction().commit();
                }
                System.out.println("[RoomRepository] Found 0 available " + type + " rooms for " + checkIn + " to " + checkOut);
                return new java.util.ArrayList<>();
            }
            
            // check if there are any active reservations that might overlap with our date range
            String reservationCheck = 
                "SELECT COUNT(*) FROM reservation res " +
                "WHERE res.status != 'CANCELLED' " +
                "AND res.status != 'CHECKED_OUT' " +
                "AND res.check_in < ? " +
                "AND res.check_out > ?";
            javax.persistence.Query resCheckQuery = em.createNativeQuery(reservationCheck);
            resCheckQuery.setParameter(1, checkOut.toString());
            resCheckQuery.setParameter(2, checkIn.toString());
            Number reservationCount = (Number) resCheckQuery.getSingleResult();
            
            List<Long> availableRoomIds = new java.util.ArrayList<>();
            
            if (reservationCount.intValue() == 0) {
                // no reservations at all - all available rooms are free
                System.out.println("[RoomRepository] DEBUG: No active reservations - all " + allAvailableIds.size() + " rooms are available");
                for (Object id : allAvailableIds) {
                    if (id instanceof Number) {
                        availableRoomIds.add(((Number) id).longValue());
                    }
                }
            } else {
                // find which rooms are booked during this date range
                System.out.println("[RoomRepository] DEBUG: Found " + reservationCount + " active reservations - filtering rooms");
                String bookedRoomsQuery = 
                    "SELECT DISTINCT rr.room_id " +
                    "FROM reservation_room rr " +
                    "INNER JOIN reservation res ON rr.reservation_id = res.id " +
                    "WHERE res.status != 'CANCELLED' " +
                    "AND res.status != 'CHECKED_OUT' " +
                    "AND res.check_in < ? " +
                    "AND res.check_out > ?";
                javax.persistence.Query bookedQuery = em.createNativeQuery(bookedRoomsQuery);
                bookedQuery.setParameter(1, checkOut.toString());
                bookedQuery.setParameter(2, checkIn.toString());
                
                @SuppressWarnings("unchecked")
                List<Object> bookedIds = bookedQuery.getResultList();
                System.out.println("[RoomRepository] DEBUG: " + bookedIds.size() + " rooms are booked for this date range");
                
                // put booked room ids in a set for fast lookup
                java.util.Set<Long> bookedSet = new java.util.HashSet<>();
                for (Object id : bookedIds) {
                    if (id instanceof Number) {
                        bookedSet.add(((Number) id).longValue());
                    }
                }
                
                // filter out booked rooms from available list
                for (Object id : allAvailableIds) {
                    if (id instanceof Number) {
                        Long roomId = ((Number) id).longValue();
                        if (!bookedSet.contains(roomId)) {
                            availableRoomIds.add(roomId);
                        }
                    }
                }
            }
            
            // finally, fetch the actual room entities by their ids
            List<Room> results = new java.util.ArrayList<>();
            if (!availableRoomIds.isEmpty()) {
                em.clear(); // clear cache again before fetching
                
                String jpqlQuery = "SELECT r FROM Room r WHERE r.id IN :ids";
                TypedQuery<Room> roomQuery = em.createQuery(jpqlQuery, Room.class);
                roomQuery.setParameter("ids", availableRoomIds);
                results = roomQuery.getResultList();
                
                System.out.println("[RoomRepository] DEBUG: Successfully loaded " + results.size() + " Room entities");
            }
            
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            
            System.out.println("[RoomRepository] Found " + results.size() + " available " + type + " rooms for " + checkIn + " to " + checkOut);
            return results;
            
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[RoomRepository] ERROR finding available rooms: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>(); // Return empty list on error
        }
    }
    
    public List<Room> findAvailableRooms() {
        return findByStatus(RoomStatus.AVAILABLE);
    }
}



