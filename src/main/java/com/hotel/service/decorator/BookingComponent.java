package com.hotel.service.decorator;

// decorator pattern - abstract base class for booking components
// can be a room booking or an add-on service, add-ons wrap around the base booking
public abstract class BookingComponent {
    
    // returns the total price of this component
    // for rooms, it's the room price, for add-ons, it adds to the wrapped component
    public abstract double getPrice();
    
    // returns a description of what this component includes
    public abstract String getDescription();
}



