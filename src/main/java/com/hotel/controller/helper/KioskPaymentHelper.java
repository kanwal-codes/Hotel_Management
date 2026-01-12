package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.ServiceAddon;
import com.hotel.service.BillingService;
import com.hotel.service.PricingService;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;

//
 // Helper class for payment preparation logic in KioskController.
 // Extracts reservation creation and billing logic to reduce controller size.
//
public final class KioskPaymentHelper {
    
    private KioskPaymentHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Prepares reservation for payment by validating and creating reservation if needed.
     // Returns the reservation (existing or newly created).
//
    public static Reservation prepareReservationForPayment(
            Reservation existingReservation,
            Guest currentGuest,
            List<Room> selectedRooms,
            List<ServiceAddon> selectedAddons,
            LocalDate checkIn,
            LocalDate checkOut,
            int numAdults,
            int numChildren,
            ReservationService reservationService,
            BillingService billingService,
            PricingService pricingService,
            LoggerService logger) {
        
        // CRITICAL: Validate selectedRooms before creating reservation
        if (selectedRooms == null || selectedRooms.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Rooms Selected");
            alert.setHeaderText("Please select at least one room");
            alert.setContentText("You must select rooms before proceeding to payment.");
            alert.showAndWait();
            return null;
        }
        
        logger.logInfo("payNow: Creating reservation with " + selectedRooms.size() + " room(s)");
        for (Room room : selectedRooms) {
            logger.logInfo("  - Room: " + room.getRoomNumber() + " (" + room.getType() + ")");
        }
        
        // Create reservation first if not already created
        if (existingReservation == null) {
            try {
                Reservation newReservation = reservationService.createReservation(
                    currentGuest, selectedRooms, checkIn, checkOut, numAdults, numChildren, selectedAddons);
                
                // Create billing using helper
                double subtotal = KioskBookingSummaryHelper.calculateBillingSubtotal(
                    selectedRooms, selectedAddons, checkIn, checkOut, pricingService);
                billingService.createBilling(newReservation, subtotal);
                
                return newReservation;
            } catch (Exception e) {
                logger.logError("Failed to create reservation for payment", e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Payment Error");
                alert.setHeaderText("Failed to prepare payment");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                return null;
            }
        }
        
        return existingReservation;
    }
}


