package com.hotel.util;

// checks if email belongs to hotel management
// identifies management users by email domain
public class EmailDetector {
    
    // checks if email is management based on domain
    // management domains: @hotel.com, @management.hotel.com, @admin.hotel.com
    public static boolean isManagementEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String lowerEmail = email.toLowerCase().trim();
        return lowerEmail.endsWith("@hotel.com") ||
               lowerEmail.endsWith("@management.hotel.com") ||
               lowerEmail.endsWith("@admin.hotel.com");
    }
}

