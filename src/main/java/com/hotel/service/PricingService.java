package com.hotel.service;

import com.hotel.config.PricingPolicy;
import com.hotel.model.Room;
import com.hotel.util.LoggerService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// handles all pricing calculations
// uses pricingpolicy to apply weekend/weekday and seasonal multipliers
public class PricingService {
    
    private PricingPolicy pricingPolicy;
    private LoggerService logger;
    
    public PricingService(PricingPolicy pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
        this.logger = LoggerService.getInstance();
    }
    
    // calculates the total price for a room over a date range
    // applies multipliers for weekends and seasonal pricing
    public double calculateRoomPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        double basePrice = room.getBasePrice();
        double totalPrice = pricingPolicy.calculatePriceForDateRange(basePrice, checkIn, checkOut);
        
        logger.logInfo(String.format("Calculated price for room %s: $%.2f (base: $%.2f, nights: %d)", 
            room.getRoomNumber(), totalPrice, basePrice, getNumberOfNights(checkIn, checkOut)));
        
        return totalPrice;
    }
    
    // calculates how many nights are in the stay
    public long getNumberOfNights(LocalDate checkIn, LocalDate checkOut) {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }
    
    // calculates add-on service price based on pricing model
    // PER_NIGHT means multiply by number of nights, PER_RESERVATION means fixed price
    public double calculateAddonPrice(double addonPrice, com.hotel.model.PricingModel pricingModel, long nights) {
        if (pricingModel == com.hotel.model.PricingModel.PER_NIGHT) {
            return addonPrice * nights;
        } else { // PER_RESERVATION
            return addonPrice;
        }
    }
}



