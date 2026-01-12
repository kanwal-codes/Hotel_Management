package com.hotel.service.decorator;

// combines two booking components into one
// used for group bookings when multiple rooms are selected
public class CombinedBookingComponent extends BookingComponent {
    
    private BookingComponent component1;
    private BookingComponent component2;
    
    public CombinedBookingComponent(BookingComponent component1, BookingComponent component2) {
        this.component1 = component1;
        this.component2 = component2;
    }
    
    @Override
    public double getPrice() {
        return component1.getPrice() + component2.getPrice();
    }
    
    @Override
    public String getDescription() {
        return component1.getDescription() + " + " + component2.getDescription();
    }
}



