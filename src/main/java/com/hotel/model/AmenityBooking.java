package com.hotel.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

// represents a booking for a hotel amenity (gym, spa, etc)
@Entity
@Table(name = "amenity_bookings")
public class AmenityBooking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_id", unique = true, nullable = false, length = 20)
    private String bookingId;
    
    @Column(nullable = false, length = 50)
    private String amenityName;
    
    @Column(nullable = false)
    private LocalDate bookingDate;
    
    @Column(nullable = false)
    private LocalTime bookingTime;
    
    @Column(length = 100)
    private String guestName;
    
    @Column(length = 50)
    private String guestEmail;
    
    @Column(length = 20)
    private String guestPhone;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (bookingId == null || bookingId.isEmpty()) {
            bookingId = generateBookingId();
        }
    }
    
    private String generateBookingId() {
        // generate booking id: AMT + timestamp
        return "AMT" + System.currentTimeMillis();
    }
    
    // default constructor
    public AmenityBooking() {
    }
    
    // constructor
    public AmenityBooking(String amenityName, LocalDate bookingDate, LocalTime bookingTime, 
                         String guestName, String guestEmail, String guestPhone) {
        this.amenityName = amenityName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
    }
    
    // getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getAmenityName() {
        return amenityName;
    }
    
    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }
    
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public LocalTime getBookingTime() {
        return bookingTime;
    }
    
    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }
    
    public String getGuestName() {
        return guestName;
    }
    
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    
    public String getGuestEmail() {
        return guestEmail;
    }
    
    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }
    
    public String getGuestPhone() {
        return guestPhone;
    }
    
    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }
    
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}





