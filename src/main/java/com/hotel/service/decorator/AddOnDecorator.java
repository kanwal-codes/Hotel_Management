package com.hotel.service.decorator;

import com.hotel.model.ServiceAddon;
import com.hotel.model.PricingModel;

// decorator that adds an add-on service to a booking
// wraps a booking component and adds the add-on price to it
public class AddOnDecorator extends BookingComponent {
    
    private BookingComponent component;
    private ServiceAddon addon;
    private int nights; // number of nights for PER_NIGHT pricing
    
    public AddOnDecorator(BookingComponent component, ServiceAddon addon, int nights) {
        this.component = component;
        this.addon = addon;
        this.nights = nights;
    }
    
    @Override
    public double getPrice() {
        double basePrice = component.getPrice();
        double addonPrice = calculateAddonPrice();
        return basePrice + addonPrice;
    }
    
    @Override
    public String getDescription() {
        return component.getDescription() + ", " + addon.getName();
    }
    
    // calculates add-on price based on pricing model
    // PER_NIGHT means multiply by nights, PER_RESERVATION means fixed price
    private double calculateAddonPrice() {
        if (addon.getPricingModel() == PricingModel.PER_NIGHT) {
            return addon.getPrice() * nights;
        } else { // PER_RESERVATION
            return addon.getPrice();
        }
    }
    
    public ServiceAddon getAddon() {
        return addon;
    }
}



