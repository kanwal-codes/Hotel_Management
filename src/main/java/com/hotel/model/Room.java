package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Represents a physical room in the hotel.
 * Each room has a type, price, and availability status.
 */
@Entity
@Table(name = "room")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Room number like "101" or "205" - must be unique
    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;
    
    // Room type: SINGLE, DOUBLE, DELUXE, PENTHOUSE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RoomType type;
    
    // Number of beds in the room
    @Column(nullable = false)
    @Positive
    private int beds;
    
    // Base price per night before any multipliers
    @Column(nullable = false)
    @Positive
    private double basePrice;
    
    // Current status: AVAILABLE, OCCUPIED, MAINTENANCE
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RoomStatus status = RoomStatus.AVAILABLE;
    
    // Many rooms belong to one hotel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
    
    // Default constructor required by JPA
    public Room() {
    }
    
    // Constructor for creating new rooms
    public Room(String roomNumber, RoomType type, int beds, double basePrice, Hotel hotel) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.beds = beds;
        this.basePrice = basePrice;
        this.hotel = hotel;
        this.status = RoomStatus.AVAILABLE;
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public RoomType getType() {
        return type;
    }
    
    public void setType(RoomType type) {
        this.type = type;
    }
    
    public int getBeds() {
        return beds;
    }
    
    public void setBeds(int beds) {
        this.beds = beds;
    }
    
    public double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    
    public RoomStatus getStatus() {
        return status;
    }
    
    public void setStatus(RoomStatus status) {
        this.status = status;
    }
    
    public Hotel getHotel() {
        return hotel;
    }
    
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}



