package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Objects;

/**
 * Join table entity linking reservations to add-on services.
 * Stores which add-ons a reservation includes and the quantity.
 */
@Entity
@Table(name = "reservation_addon")
public class ReservationAddon implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Composite primary key - reservation part
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @NotNull
    private Reservation reservation;
    
    // Composite primary key - addon part
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addon_id", nullable = false)
    @NotNull
    private ServiceAddon addon;
    
    // How many of this add-on service (usually 1, but could be more)
    @Column(nullable = false)
    @Positive
    private int quantity = 1;
    
    // Default constructor required by JPA
    public ReservationAddon() {
    }
    
    // Constructor for linking an add-on to a reservation
    public ReservationAddon(Reservation reservation, ServiceAddon addon, int quantity) {
        this.reservation = reservation;
        this.addon = addon;
        this.quantity = quantity;
    }
    
    // Standard getters and setters
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public ServiceAddon getAddon() {
        return addon;
    }
    
    public void setAddon(ServiceAddon addon) {
        this.addon = addon;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    // Required for composite key - checks if two ReservationAddon entries are the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationAddon that = (ReservationAddon) o;
        return Objects.equals(reservation != null ? reservation.getId() : null, 
                             that.reservation != null ? that.reservation.getId() : null) &&
               Objects.equals(addon != null ? addon.getId() : null, 
                             that.addon != null ? that.addon.getId() : null);
    }
    
    // Required for composite key - generates hash code based on reservation and addon IDs
    @Override
    public int hashCode() {
        return Objects.hash(
            reservation != null ? reservation.getId() : null,
            addon != null ? addon.getId() : null
        );
    }
}



