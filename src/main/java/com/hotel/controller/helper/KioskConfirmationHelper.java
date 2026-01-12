package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

//
 // Helper class for confirmation screen logic in KioskController.
 // Extracts confirmation display and loyalty enrollment UI logic.
//
public final class KioskConfirmationHelper {
    
    private KioskConfirmationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Loads and displays confirmation screen information.
//
    public static void loadConfirmation(
            Reservation createdReservation,
            LocalDate checkIn,
            LocalDate checkOut,
            Guest currentGuest,
            Label confirmationTitleLabel,
            Label bookingIdLabel,
            Label reservationNumberLabel,
            Label reservationIdLabel,
            HBox reservationIdRow,
            Label statusLabel,
            Label bookingDetailsLabel,
            Label billingMessageLabel,
            Button feedbackButton,
            VBox loyaltyEnrollmentContainer,
            VBox loyaltyEnrolledContainer,
            Label loyaltyNumberLabel,
            Label loyaltyPointsLabel) {
        
        if (createdReservation == null) {
            return;
        }
        
        // Get confirmation number or ID (booking ID)
        String bookingId = createdReservation.getConfirmationNumber() != null 
            ? createdReservation.getConfirmationNumber() 
            : String.valueOf(createdReservation.getId());
        
        // Get reservation status
        ReservationStatus status = createdReservation.getStatus();
        boolean isConfirmed = (status == ReservationStatus.CONFIRMED);
        boolean isPending = (status == ReservationStatus.PENDING);
        
        // Update title based on status
        if (confirmationTitleLabel != null) {
            if (isConfirmed) {
                confirmationTitleLabel.setText("✓ Booking Confirmed!");
            } else if (isPending) {
                confirmationTitleLabel.setText("✓ Reservation Created - Payment Pending");
            } else {
                confirmationTitleLabel.setText("✓ Reservation Created");
            }
        }
        
        // Display Booking ID
        if (bookingIdLabel != null) {
            bookingIdLabel.setText(bookingId);
        }
        // Backward compatibility
        if (reservationNumberLabel != null) {
            reservationNumberLabel.setText(bookingId);
        }
        
        // Display Reservation ID only if confirmed (paid)
        if (reservationIdLabel != null && reservationIdRow != null) {
            if (isConfirmed) {
                reservationIdLabel.setText(String.valueOf(createdReservation.getId()));
                reservationIdRow.setVisible(true);
                reservationIdRow.setManaged(true);
            } else {
                reservationIdRow.setVisible(false);
                reservationIdRow.setManaged(false);
            }
        }
        
        // Display Status
        if (statusLabel != null) {
            if (isConfirmed) {
                statusLabel.setText("CONFIRMED");
                statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else if (isPending) {
                statusLabel.setText("PENDING - Payment Required");
                statusLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
            } else {
                statusLabel.setText(status.toString());
                statusLabel.setStyle("-fx-text-fill: #2c3e50;");
            }
        }
        
        // Display booking details with room numbers
        if (bookingDetailsLabel != null) {
            String checkInStr = checkIn != null ? checkIn.toString() : 
                (createdReservation.getCheckIn() != null ? createdReservation.getCheckIn().toString() : "N/A");
            String checkOutStr = checkOut != null ? checkOut.toString() : 
                (createdReservation.getCheckOut() != null ? createdReservation.getCheckOut().toString() : "N/A");
            
            // Get assigned room numbers
            String roomNumbers = "N/A";
            if (createdReservation.getReservationRooms() != null && !createdReservation.getReservationRooms().isEmpty()) {
                roomNumbers = createdReservation.getReservationRooms().stream()
                    .map(rr -> {
                        if (rr.getRoom() != null) {
                            return "Room #" + rr.getRoom().getRoomNumber() + " (" + rr.getRoom().getType() + ")";
                        }
                        return "";
                    })
                    .filter(s -> !s.isEmpty())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");
            }
            
            if (isConfirmed) {
                bookingDetailsLabel.setText(
                    "Your reservation has been confirmed!\n\n" +
                    "Check-in: " + checkInStr + "\n" +
                    "Check-out: " + checkOutStr + "\n" +
                    "Assigned Rooms: " + roomNumbers
                );
            } else if (isPending) {
                bookingDetailsLabel.setText(
                    "Your reservation is pending payment.\n\n" +
                    "Please pay during check-in at the counter.\n" +
                    "Check-in: " + checkInStr + "\n" +
                    "Check-out: " + checkOutStr + "\n" +
                    "Assigned Rooms: " + roomNumbers
                );
            } else {
                bookingDetailsLabel.setText(
                    "Reservation Details:\n\n" +
                    "Check-in: " + checkInStr + "\n" +
                    "Check-out: " + checkOutStr + "\n" +
                    "Assigned Rooms: " + roomNumbers
                );
            }
        }
        
        // Update billing message based on status
        if (billingMessageLabel != null) {
            if (isConfirmed) {
                billingMessageLabel.setText("Payment completed. Thank you for your booking!");
                billingMessageLabel.setVisible(true);
                billingMessageLabel.setManaged(true);
            } else if (isPending) {
                billingMessageLabel.setText("Please pay during check-in at the front desk.");
                billingMessageLabel.setVisible(true);
                billingMessageLabel.setManaged(true);
            } else {
                billingMessageLabel.setVisible(false);
                billingMessageLabel.setManaged(false);
            }
        }
        
        // Hide feedback button - feedback is not shown after payment
        if (feedbackButton != null) {
            feedbackButton.setVisible(false);
            feedbackButton.setManaged(false);
        }
        
        // Show loyalty enrollment option if guest is not enrolled
        updateLoyaltyEnrollmentUI(
            currentGuest, loyaltyEnrollmentContainer, loyaltyEnrolledContainer,
            loyaltyNumberLabel, loyaltyPointsLabel);
    }
    
    //
     // Updates loyalty enrollment UI based on guest status.
//
    public static void updateLoyaltyEnrollmentUI(
            Guest currentGuest,
            VBox loyaltyEnrollmentContainer,
            VBox loyaltyEnrolledContainer,
            Label loyaltyNumberLabel,
            Label loyaltyPointsLabel) {
        
        if (currentGuest == null) {
            return;
        }
        
        if (currentGuest.getLoyaltyNumber() == null || currentGuest.getLoyaltyNumber().isEmpty()) {
            if (loyaltyEnrollmentContainer != null) {
                loyaltyEnrollmentContainer.setVisible(true);
                loyaltyEnrollmentContainer.setManaged(true);
            }
            if (loyaltyEnrolledContainer != null) {
                loyaltyEnrolledContainer.setVisible(false);
                loyaltyEnrolledContainer.setManaged(false);
            }
        } else {
            if (loyaltyEnrollmentContainer != null) {
                loyaltyEnrollmentContainer.setVisible(false);
                loyaltyEnrollmentContainer.setManaged(false);
            }
            if (loyaltyEnrolledContainer != null) {
                loyaltyEnrolledContainer.setVisible(true);
                loyaltyEnrolledContainer.setManaged(true);
            }
            if (loyaltyNumberLabel != null) {
                loyaltyNumberLabel.setText("Loyalty Number: " + currentGuest.getLoyaltyNumber());
            }
            if (loyaltyPointsLabel != null) {
                loyaltyPointsLabel.setText("Current Points: " + currentGuest.getLoyaltyPoints());
            }
        }
    }
}

