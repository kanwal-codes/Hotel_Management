package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Represents feedback submitted by guests after checkout.
 * Stores rating (1-5 stars) and optional comments.
 */
@Entity
@Table(name = "feedback")
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many feedback entries can belong to one guest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    @NotNull
    private Guest guest;
    
    // Many feedback entries can belong to one reservation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @NotNull
    private Reservation reservation;
    
    // Star rating from 1 to 5
    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int rating;
    
    // Optional text comments from guest
    @Column(length = 1000)
    private String comments;
    
    // Optional tag like "positive", "negative", "neutral" for analysis
    @Column(name = "sentiment_tag", length = 50)
    private String sentimentTag;
    
    // When this feedback was submitted
    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;
    
    // Default constructor - sets timestamp automatically
    public Feedback() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor for creating new feedback
    public Feedback(Guest guest, Reservation reservation, int rating, String comments) {
        this.guest = guest;
        this.reservation = reservation;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = LocalDateTime.now();
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Guest getGuest() {
        return guest;
    }
    
    public void setGuest(Guest guest) {
        this.guest = guest;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public String getSentimentTag() {
        return sentimentTag;
    }
    
    public void setSentimentTag(String sentimentTag) {
        this.sentimentTag = sentimentTag;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



