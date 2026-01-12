package com.hotel.service.strategy;

import com.hotel.model.Billing;

// strategy pattern interface for calculating billing totals
// different strategies handle standard billing, discounts, and loyalty point redemptions
public interface BillingStrategy {
    
    // calculates the final total for a billing record
    // each strategy implements this differently based on discounts or loyalty points
    double calculateTotal(Billing billing);
}



