package com.hotel.config;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// handles all the pricing rules for the hotel
// applies weekend/weekday multipliers and seasonal pricing
public class PricingPolicy {
    
    // weekend nights cost 20% more by default
    private double weekendMultiplier = 1.2;
    
    // weekday nights are normal price
    private double weekdayMultiplier = 1.0;
    
    // can define special seasons (like peak season) with different multipliers
    private Map<Season, Double> seasonalMultipliers = new HashMap<>();
    
    // represents a date range for seasonal pricing (like "Peak Season")
    public static class Season {
        private LocalDate startDate;
        private LocalDate endDate;
        private String name;
        
        public Season(String name, LocalDate startDate, LocalDate endDate) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        // checks if a date falls within this season
        public boolean contains(LocalDate date) {
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        }
        
        public String getName() {
            return name;
        }
    }
    
    // default constructor uses default multipliers
    public PricingPolicy() {
    }
    
    // constructor to set custom multipliers
    public PricingPolicy(double weekendMultiplier, double weekdayMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
        this.weekdayMultiplier = weekdayMultiplier;
    }
    
    // calculates the price for a single date
    // applies weekend/weekday or seasonal multiplier to base price
    public double calculatePriceForDate(double basePrice, LocalDate date) {
        double multiplier = getMultiplierForDate(date);
        return basePrice * multiplier;
    }
    
    // figures out which multiplier to use for a date
    // checks seasonal first, then weekend/weekday
    private double getMultiplierForDate(LocalDate date) {
        // seasonal pricing overrides weekend/weekday
        for (Map.Entry<Season, Double> entry : seasonalMultipliers.entrySet()) {
            if (entry.getKey().contains(date)) {
                return entry.getValue();
            }
        }
        
        // otherwise use weekend or weekday multiplier
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return weekendMultiplier;
        } else {
            return weekdayMultiplier;
        }
    }
    
    // calculates total price for a date range by adding up each night
    // loops through each day and applies the right multiplier
    public double calculatePriceForDateRange(double basePrice, LocalDate checkIn, LocalDate checkOut) {
        double total = 0.0;
        LocalDate currentDate = checkIn;
        
        while (!currentDate.isAfter(checkOut) && !currentDate.equals(checkOut)) {
            total += calculatePriceForDate(basePrice, currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        return total;
    }
    
    // add a seasonal multiplier
    public void addSeasonalMultiplier(Season season, double multiplier) {
        seasonalMultipliers.put(season, multiplier);
    }
    
    // remove a seasonal multiplier
    public void removeSeasonalMultiplier(Season season) {
        seasonalMultipliers.remove(season);
    }
    
    // getters and setters
    public double getWeekendMultiplier() {
        return weekendMultiplier;
    }
    
    public void setWeekendMultiplier(double weekendMultiplier) {
        this.weekendMultiplier = weekendMultiplier;
    }
    
    public double getWeekdayMultiplier() {
        return weekdayMultiplier;
    }
    
    public void setWeekdayMultiplier(double weekdayMultiplier) {
        this.weekdayMultiplier = weekdayMultiplier;
    }
    
    public Map<Season, Double> getSeasonalMultipliers() {
        return seasonalMultipliers;
    }
}



