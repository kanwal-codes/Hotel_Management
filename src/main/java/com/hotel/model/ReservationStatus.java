package com.hotel.model;

/**
 * Enum representing the status of a reservation
 */
public enum ReservationStatus {
    PENDING,      // Reservation created but not confirmed
    CONFIRMED,    // Reservation confirmed (payment made)
    CHECKED_IN,   // Guest has checked in
    CANCELLED,    // Reservation cancelled
    CHECKED_OUT   // Guest has checked out
}



