package com.hotel.service.strategy;

import com.hotel.model.Billing;

// billing calculation with discount applied
// discount is taken off subtotal, then tax is calculated on the discounted amount
public class DiscountBillingStrategy implements BillingStrategy {
    
    @Override
    public double calculateTotal(Billing billing) {
        double subtotal = billing.getSubtotal();
        double discountValue = billing.getDiscountValue();
        
        // take discount off subtotal first
        double discountedSubtotal = subtotal - discountValue;
        
        // calculate tax on the discounted amount
        double taxAmount = discountedSubtotal * billing.getTaxRate();
        
        return discountedSubtotal + taxAmount;
    }
}



