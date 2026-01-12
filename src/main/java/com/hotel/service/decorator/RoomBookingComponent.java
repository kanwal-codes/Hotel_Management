package com.hotel.service.decorator;

import com.hotel.model.Room;
import com.hotel.service.PricingService;
import java.time.LocalDate;

// base booking component representing a room booking
// this is the starting point for the decorator pattern - add-ons wrap around this
public class RoomBookingComponent extends BookingComponent {
    
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private PricingService pricingService;
    
    public RoomBookingComponent(Room room, LocalDate checkIn, LocalDate checkOut, PricingService pricingService) {
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.pricingService = pricingService;
    }
    
    @Override
    public double getPrice() {
        return pricingService.calculateRoomPrice(room, checkIn, checkOut);
    }
    
    @Override
    public String getDescription() {
        return "Room " + room.getRoomNumber() + " (" + room.getType() + ")";
    }
    
    public Room getRoom() {
        return room;
    }
}



