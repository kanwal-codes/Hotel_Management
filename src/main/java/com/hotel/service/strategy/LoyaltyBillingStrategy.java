package com.hotel.service.strategy;

import com.hotel.model.Billing;

// billing calculation with both regular discount and loyalty points discount
// applies both discounts to subtotal, then calculates tax
public class LoyaltyBillingStrategy implements BillingStrategy {
    
    @Override
    public double calculateTotal(Billing billing) {
        double subtotal = billing.getSubtotal();
        double discountValue = billing.getDiscountValue();
        double loyaltyDiscount = calculateLoyaltyDiscount(billing);
        
        // apply both discounts to subtotal
        double discountedSubtotal = subtotal - discountValue - loyaltyDiscount;
        
        // calculate tax on the discounted amount
        double taxAmount = discountedSubtotal * billing.getTaxRate();
        
        return discountedSubtotal + taxAmount;
    }
    
    // calculates discount amount from loyalty points redeemed
    // 100 points = 1% discount, capped at 20% maximum
    private double calculateLoyaltyDiscount(Billing billing) {
        int pointsRedeemed = billing.getLoyaltyRedeemedPoints();
        if (pointsRedeemed <= 0) {
            return 0.0;
        }
        
        // convert points to discount percentage (100 points = 1%), max 20%
        double discountPercent = Math.min(pointsRedeemed / 100.0, 20.0);
        return billing.getSubtotal() * (discountPercent / 100.0);
    }
}



