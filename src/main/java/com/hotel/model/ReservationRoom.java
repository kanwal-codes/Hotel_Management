package com.hotel.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Join table entity linking reservations to rooms.
 * One reservation can have multiple rooms (group bookings).
 * One room can be in multiple reservations (over time).
 */
@Entity
@Table(name = "reservation_room")
public class ReservationRoom implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Composite primary key - reservation part
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    // Composite primary key - room part
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    // Default constructor required by JPA
    public ReservationRoom() {
    }
    
    // Constructor for linking a room to a reservation
    public ReservationRoom(Reservation reservation, Room room) {
        this.reservation = reservation;
        this.room = room;
    }
    
    // Standard getters and setters
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public Room getRoom() {
        return room;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }
    
    // Required for composite key - checks if two ReservationRoom entries are the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationRoom that = (ReservationRoom) o;
        return Objects.equals(reservation != null ? reservation.getId() : null, 
                             that.reservation != null ? that.reservation.getId() : null) &&
               Objects.equals(room != null ? room.getId() : null, 
                             that.room != null ? that.room.getId() : null);
    }
    
    // Required for composite key - generates hash code based on reservation and room IDs
    @Override
    public int hashCode() {
        return Objects.hash(
            reservation != null ? reservation.getId() : null,
            room != null ? room.getId() : null
        );
    }
}



