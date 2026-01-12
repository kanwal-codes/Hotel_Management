package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a guest in the hotel system.
 * Maps to the 'guest' table in the database.
 */
@Entity
@Table(name = "guest")
public class Guest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    @NotNull
    private String name;
    
    @Column(nullable = false, length = 20)
    @NotNull
    private String phone;
    
    @Column(nullable = false, length = 100)
    @Email
    @NotNull
    private String email;
    
    @Column(length = 255)
    private String address;
    
    // Password hash for customer login (if we implement customer accounts)
    @Column(name = "customer_password_hash")
    private String customerPasswordHash;
    
    // Loyalty program points balance
    @Column(name = "loyalty_points")
    private int loyaltyPoints = 0;
    
    // Unique loyalty number assigned when guest enrolls
    @Column(name = "loyalty_number", length = 20)
    private String loyaltyNumber;
    
    // One guest can have many reservations
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
    
    // One guest can submit multiple feedback entries
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Feedback> feedbacks = new ArrayList<>();
    
    // One guest can be on one waitlist at a time
    @OneToOne(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Waitlist waitlist;
    
    // Default constructor required by JPA
    public Guest() {
    }
    
    // Constructor for creating new guests
    public Guest(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = 0;
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerPasswordHash() {
        return customerPasswordHash;
    }

    public void setCustomerPasswordHash(String customerPasswordHash) {
        this.customerPasswordHash = customerPasswordHash;
    }
    
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public String getLoyaltyNumber() {
        return loyaltyNumber;
    }
    
    public void setLoyaltyNumber(String loyaltyNumber) {
        this.loyaltyNumber = loyaltyNumber;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }
    
    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
    
    public Waitlist getWaitlist() {
        return waitlist;
    }
    
    public void setWaitlist(Waitlist waitlist) {
        this.waitlist = waitlist;
    }
}

