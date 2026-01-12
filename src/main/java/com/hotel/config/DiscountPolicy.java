package com.hotel.config;

import com.hotel.model.Role;

// handles discount rules and validation
// enforces that admins can only apply discounts up to their role's limit
public class DiscountPolicy {
    
    // maximum discount each role can apply
    private static final double ADMIN_DISCOUNT_CAP = 15.0;    // admins: 15% max
    private static final double MANAGER_DISCOUNT_CAP = 30.0;  // managers: 30% max
    
    // returns the maximum discount percentage allowed for a role
    public double getMaxDiscountForRole(Role role) {
        if (role == Role.ADMIN) {
            return ADMIN_DISCOUNT_CAP;
        } else if (role == Role.MANAGER) {
            return MANAGER_DISCOUNT_CAP;
        }
        return 0.0; // unknown roles can't apply discounts
    }
    
    // checks if a discount amount is valid for the given role
    // returns false if negative or exceeds role's limit
    public boolean isValidDiscount(Role role, double discountPercent) {
        if (discountPercent < 0) {
            return false; // can't have negative discounts
        }
        
        double maxDiscount = getMaxDiscountForRole(role);
        return discountPercent <= maxDiscount;
    }
    
    // validates and caps a discount to the maximum allowed for the role
    // if discount is too high, it gets reduced to the max
    public double validateAndCapDiscount(Role role, double discountPercent) {
        if (discountPercent < 0) {
            return 0.0;
        }
        
        double maxDiscount = getMaxDiscountForRole(role);
        return Math.min(discountPercent, maxDiscount);
    }
    
    // converts a discount percentage to a dollar amount
    // example: 10% of $100 = $10 discount
    public double calculateDiscountAmount(double totalAmount, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            return 0.0;
        }
        return totalAmount * (discountPercent / 100.0);
    }
    
    // getters for constants
    public static double getAdminDiscountCap() {
        return ADMIN_DISCOUNT_CAP;
    }
    
    public static double getManagerDiscountCap() {
        return MANAGER_DISCOUNT_CAP;
    }
}



