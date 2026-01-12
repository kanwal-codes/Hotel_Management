package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hotel reservation/booking.
 * This is the main entity that connects guests to rooms and billing.
 */
@Entity
@Table(name = "reservation")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Unique confirmation number shown to guests
    @Column(name = "confirmation_number", unique = true, length = 20)
    private String confirmationNumber;
    
    @Column(name = "check_in", nullable = false)
    @NotNull
    private LocalDate checkIn;
    
    @Column(name = "check_out", nullable = false)
    @NotNull
    private LocalDate checkOut;
    
    @Column(name = "num_adults", nullable = false)
    @Positive
    private int numAdults;
    
    @Column(name = "num_children", nullable = false)
    @javax.validation.constraints.Min(value = 0, message = "Number of children cannot be negative")
    private int numChildren = 0;
    
    // Status: PENDING, CONFIRMED, CANCELLED, CHECKED_OUT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ReservationStatus status = ReservationStatus.PENDING;
    
    // Many reservations belong to one guest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    @NotNull
    private Guest guest;
    
    // One reservation can have multiple rooms (group bookings)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationRoom> reservationRooms = new ArrayList<>();
    
    // One reservation can have multiple add-on services
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationAddon> reservationAddons = new ArrayList<>();
    
    // One reservation has one billing record
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Billing billing;
    
    // One reservation can have multiple feedback entries (though usually just one)
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();
    
    // Default constructor required by JPA
    public Reservation() {
    }
    
    // Constructor for creating new reservations
    public Reservation(LocalDate checkIn, LocalDate checkOut, int numAdults, int numChildren, Guest guest) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.guest = guest;
        this.status = ReservationStatus.PENDING;
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getConfirmationNumber() {
        return confirmationNumber;
    }
    
    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }
    
    public LocalDate getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    
    public LocalDate getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
    
    public int getNumAdults() {
        return numAdults;
    }
    
    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }
    
    public int getNumChildren() {
        return numChildren;
    }
    
    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public Guest getGuest() {
        return guest;
    }
    
    public void setGuest(Guest guest) {
        this.guest = guest;
    }
    
    public List<ReservationRoom> getReservationRooms() {
        return reservationRooms;
    }
    
    public void setReservationRooms(List<ReservationRoom> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }
    
    public List<ReservationAddon> getReservationAddons() {
        return reservationAddons;
    }
    
    public void setReservationAddons(List<ReservationAddon> reservationAddons) {
        this.reservationAddons = reservationAddons;
    }
    
    public Billing getBilling() {
        return billing;
    }
    
    public void setBilling(Billing billing) {
        this.billing = billing;
    }
    
    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
    
    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
}



