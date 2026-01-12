package com.hotel.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// represents a hotel location
// one hotel has many rooms
@Entity
@Table(name = "hotel")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // hotel name
    @Column(nullable = false, length = 100)
    private String name;
    
    // city where hotel is located
    @Column(nullable = false, length = 100)
    private String city;
    
    // one hotel has many rooms
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();
    
    // default constructor required by jpa
    public Hotel() {
    }
    
    // constructor for creating new hotels
    public Hotel(String name, String city) {
        this.name = name;
        this.city = city;
    }
    
    // standard getters and setters
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
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public List<Room> getRooms() {
        return rooms;
    }
    
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}



