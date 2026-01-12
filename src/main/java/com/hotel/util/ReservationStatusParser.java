package com.hotel.util;

import com.hotel.model.ReservationStatus;

// utility class for parsing reservation status strings
// converts display text to ReservationStatus enum
public final class ReservationStatusParser {
    
    private ReservationStatusParser() {
        // utility class - prevent instantiation
    }
    
    // parses status text to ReservationStatus enum
    // matches combobox display values
    public static ReservationStatus parseReservationStatus(String statusText) {
        if (statusText == null || statusText.isEmpty()) {
            return null;
        }
        
        String trimmed = statusText.trim();
        
        // convert display text to enum
        switch (trimmed) {
            case "Pending":
                return ReservationStatus.PENDING;
            case "Confirmed":
                return ReservationStatus.CONFIRMED;
            case "Checked In":
                return ReservationStatus.CHECKED_IN;
            case "Checked Out":
                return ReservationStatus.CHECKED_OUT;
            case "Cancelled":
                return ReservationStatus.CANCELLED;
            default:
                // try to parse as enum name directly
                try {
                    return ReservationStatus.valueOf(trimmed.toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    return null;
                }
        }
    }
}


