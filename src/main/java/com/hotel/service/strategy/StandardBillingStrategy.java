package com.hotel.service.strategy;

import com.hotel.model.Billing;

// standard billing calculation - just adds tax to subtotal
// no discounts or loyalty points applied
public class StandardBillingStrategy implements BillingStrategy {
    
    @Override
    public double calculateTotal(Billing billing) {
        double subtotal = billing.getSubtotal();
        double taxAmount = subtotal * billing.getTaxRate();
        
        return subtotal + taxAmount;
    }
}



