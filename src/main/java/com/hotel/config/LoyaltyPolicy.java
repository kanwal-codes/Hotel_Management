package com.hotel.config;

// configures the loyalty program rules
// sets how points are earned and how they can be redeemed
public class LoyaltyPolicy {
    
    // how much money earns 1 point (default: $10 = 1 point)
    private double earningRate = 10.0;
    
    // maximum points that can be redeemed in one reservation (default: 1000)
    private int maxRedemptionPerReservation = 1000;
    
    // how many points equal 1% discount (default: 100 points = 1%)
    private double pointsPerPercentDiscount = 100.0;
    
    // maximum discount percentage from loyalty points (default: 20%)
    private double maxLoyaltyDiscountPercent = 20.0;
    
    // constructors
    public LoyaltyPolicy() {
        // default values
    }
    
    public LoyaltyPolicy(double earningRate, int maxRedemptionPerReservation) {
        this.earningRate = earningRate;
        this.maxRedemptionPerReservation = maxRedemptionPerReservation;
    }
    
    // calculates how many points a guest earns from a payment
    public int calculatePointsEarned(double paymentAmount) {
        if (paymentAmount <= 0) {
            return 0;
        }
        return (int) (paymentAmount / earningRate);
    }
    
    // calculates discount percentage from points redeemed
    // caps at the maximum loyalty discount percentage
    public double calculateDiscountFromPoints(int pointsRedeemed) {
        if (pointsRedeemed <= 0) {
            return 0.0;
        }
        
        double discountPercent = pointsRedeemed / pointsPerPercentDiscount;
        
        // don't allow more than the maximum discount
        return Math.min(discountPercent, maxLoyaltyDiscountPercent);
    }
    
    // calculates the dollar amount of discount from points redeemed
    public double calculateDiscountAmount(double totalAmount, int pointsRedeemed) {
        double discountPercent = calculateDiscountFromPoints(pointsRedeemed);
        return totalAmount * (discountPercent / 100.0);
    }
    
    // checks if a points redemption amount is valid (within the cap)
    public boolean isValidRedemption(int pointsRedeemed) {
        return pointsRedeemed > 0 && pointsRedeemed <= maxRedemptionPerReservation;
    }
    
    // caps points redemption to the maximum allowed per reservation
    public int capRedemption(int pointsRedeemed) {
        if (pointsRedeemed < 0) {
            return 0;
        }
        return Math.min(pointsRedeemed, maxRedemptionPerReservation);
    }
    
    // getters and setters
    public double getEarningRate() {
        return earningRate;
    }
    
    public void setEarningRate(double earningRate) {
        this.earningRate = earningRate;
    }
    
    public int getMaxRedemptionPerReservation() {
        return maxRedemptionPerReservation;
    }
    
    public void setMaxRedemptionPerReservation(int maxRedemptionPerReservation) {
        this.maxRedemptionPerReservation = maxRedemptionPerReservation;
    }
    
    public double getPointsPerPercentDiscount() {
        return pointsPerPercentDiscount;
    }
    
    public void setPointsPerPercentDiscount(double pointsPerPercentDiscount) {
        this.pointsPerPercentDiscount = pointsPerPercentDiscount;
    }
    
    public double getMaxLoyaltyDiscountPercent() {
        return maxLoyaltyDiscountPercent;
    }
    
    public void setMaxLoyaltyDiscountPercent(double maxLoyaltyDiscountPercent) {
        this.maxLoyaltyDiscountPercent = maxLoyaltyDiscountPercent;
    }
}



