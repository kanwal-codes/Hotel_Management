package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

// represents a waitlist entry when a guest wants a room but none are available
// when a room becomes available, the observer pattern notifies admins
@Entity
@Table(name = "waitlist")
public class Waitlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // one guest can be on one waitlist at a time
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false, unique = true)
    @NotNull
    private Guest guest;
    
    // what type of room they're waiting for
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_type", nullable = false)
    @NotNull
    private RoomType requestedType;
    
    // date range they want the room
    @Column(name = "date_range_start", nullable = false)
    @NotNull
    private LocalDate dateRangeStart;
    
    @Column(name = "date_range_end", nullable = false)
    @NotNull
    private LocalDate dateRangeEnd;
    
    // status: PENDING, NOTIFIED, CONVERTED
    @Column(length = 50)
    private String status = "PENDING";
    
    // number of adults for the reservation
    @Column(name = "num_adults")
    private Integer numAdults;
    
    // number of children for the reservation
    @Column(name = "num_children")
    private Integer numChildren;
    
    // default constructor required by jpa
    public Waitlist() {
    }
    
    // constructor for creating new waitlist entries
    public Waitlist(Guest guest, RoomType requestedType, LocalDate dateRangeStart, LocalDate dateRangeEnd) {
        this.guest = guest;
        this.requestedType = requestedType;
        this.dateRangeStart = dateRangeStart;
        this.dateRangeEnd = dateRangeEnd;
        this.status = "PENDING";
    }
    
    // constructor with adults and children
    public Waitlist(Guest guest, RoomType requestedType, LocalDate dateRangeStart, LocalDate dateRangeEnd, 
                     Integer numAdults, Integer numChildren) {
        this.guest = guest;
        this.requestedType = requestedType;
        this.dateRangeStart = dateRangeStart;
        this.dateRangeEnd = dateRangeEnd;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.status = "PENDING";
    }
    
    // standard getters and setters
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
    
    public RoomType getRequestedType() {
        return requestedType;
    }
    
    public void setRequestedType(RoomType requestedType) {
        this.requestedType = requestedType;
    }
    
    public LocalDate getDateRangeStart() {
        return dateRangeStart;
    }
    
    public void setDateRangeStart(LocalDate dateRangeStart) {
        this.dateRangeStart = dateRangeStart;
    }
    
    public LocalDate getDateRangeEnd() {
        return dateRangeEnd;
    }
    
    public void setDateRangeEnd(LocalDate dateRangeEnd) {
        this.dateRangeEnd = dateRangeEnd;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getNumAdults() {
        return numAdults;
    }
    
    public void setNumAdults(Integer numAdults) {
        this.numAdults = numAdults;
    }
    
    public Integer getNumChildren() {
        return numChildren;
    }
    
    public void setNumChildren(Integer numChildren) {
        this.numChildren = numChildren;
    }
}


