package com.hotel.controller.helper;

import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.PricingService;
import com.hotel.util.LoggerService;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

//
 // Helper class for UI-related operations in KioskController.
 // Extracts UI update and display logic to reduce controller size.
//
public final class KioskUIHelper {
    
    private KioskUIHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Displays assigned rooms from a reservation in a VBox.
//
    public static void displayAssignedRoomsFromReservation(
            Reservation createdReservation,
            LocalDate checkIn,
            LocalDate checkOut,
            PricingService pricingService,
            VBox roomBreakdownList,
            VBox roomBreakdownContainer,
            Label roomBreakdownTitleLabel) {
        
        if (roomBreakdownList == null || createdReservation == null) {
            if (roomBreakdownContainer != null) {
                roomBreakdownContainer.setVisible(false);
                roomBreakdownContainer.setManaged(false);
            }
            return;
        }
        
        roomBreakdownList.getChildren().clear();
        
        if (createdReservation.getReservationRooms() != null && !createdReservation.getReservationRooms().isEmpty()) {
            // Update the label to say "Assigned Rooms" instead of "Selected Rooms"
            if (roomBreakdownTitleLabel != null) {
                roomBreakdownTitleLabel.setText("Assigned Rooms:");
            }
            
            for (com.hotel.model.ReservationRoom rr : createdReservation.getReservationRooms()) {
                com.hotel.model.Room room = rr.getRoom();
                if (room != null) {
                    // Calculate price for display
                    double roomPrice = 0.0;
                    if (checkIn != null && checkOut != null && pricingService != null) {
                        roomPrice = pricingService.calculateRoomPrice(room, checkIn, checkOut);
                    } else if (createdReservation.getCheckIn() != null && createdReservation.getCheckOut() != null && pricingService != null) {
                        roomPrice = pricingService.calculateRoomPrice(room, createdReservation.getCheckIn(), createdReservation.getCheckOut());
                    }
                    
                    Label roomLabel = new Label(String.format("• %s - Room #%s: $%.2f", 
                        room.getType().toString(), room.getRoomNumber(), roomPrice));
                    roomLabel.getStyleClass().add("body-text");
                    roomLabel.setStyle("-fx-font-weight: bold;"); // Make room numbers stand out
                    roomBreakdownList.getChildren().add(roomLabel);
                }
            }
            
            if (roomBreakdownContainer != null) {
                roomBreakdownContainer.setVisible(true);
                roomBreakdownContainer.setManaged(true);
            }
        } else {
            if (roomBreakdownContainer != null) {
                roomBreakdownContainer.setVisible(false);
                roomBreakdownContainer.setManaged(false);
            }
        }
    }
    
    //
     // Updates the assigned rooms label with room numbers from a reservation.
//
    public static void updateAssignedRoomsLabel(
            Reservation createdReservation,
            Label assignedRoomsLabel) {
        
        if (assignedRoomsLabel == null || createdReservation == null) {
            return;
        }
        
        if (createdReservation.getReservationRooms() != null && !createdReservation.getReservationRooms().isEmpty()) {
            String roomNumbers = createdReservation.getReservationRooms().stream()
                .map(rr -> {
                    if (rr.getRoom() != null) {
                        return "Room #" + rr.getRoom().getRoomNumber() + " (" + rr.getRoom().getType() + ")";
                    }
                    return "";
                })
                .filter(s -> !s.isEmpty())
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");
            assignedRoomsLabel.setText(roomNumbers);
            assignedRoomsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;"); // Make it stand out
        } else {
            assignedRoomsLabel.setText("N/A");
        }
    }
    
    //
     // Updates assigned rooms label from a list of selected rooms (before reservation is created).
//
    public static void updateAssignedRoomsLabelFromSelected(
            java.util.List<Room> selectedRooms,
            Label assignedRoomsLabel) {
        
        if (assignedRoomsLabel == null) {
            return;
        }
        
        if (selectedRooms != null && !selectedRooms.isEmpty()) {
            String roomNumbers = selectedRooms.stream()
                .map(room -> "Room #" + room.getRoomNumber() + " (" + room.getType() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");
            assignedRoomsLabel.setText(roomNumbers);
        } else {
            assignedRoomsLabel.setText("N/A");
        }
    }
    
    //
     // Shows hotel rules and regulations dialog.
//
    public static void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules and Regulations");
        alert.setHeaderText("Hotel Booking Policy");
        alert.setContentText("Please review our booking policies:\n\n" +
            "• Check-in time: 3:00 PM\n" +
            "• Check-out time: 11:00 AM\n" +
            "• Cancellation: 24 hours notice required\n" +
            "• Occupancy limits: Single/Deluxe/Penthouse: 2 people, Double: 4 people\n" +
            "• Billing will be handled at the front desk");
        alert.showAndWait();
    }
    
    //
     // Hides all error labels.
//
    public static void hideAllErrors(
            javafx.scene.control.Label adultsErrorLabel,
            javafx.scene.control.Label childrenErrorLabel,
            javafx.scene.control.Label checkInErrorLabel,
            javafx.scene.control.Label checkOutErrorLabel,
            javafx.scene.control.Label nameErrorLabel,
            javafx.scene.control.Label phoneErrorLabel,
            javafx.scene.control.Label emailErrorLabel,
            javafx.scene.control.Label occupancyValidationLabel) {
        
        if (adultsErrorLabel != null) adultsErrorLabel.setVisible(false);
        if (childrenErrorLabel != null) childrenErrorLabel.setVisible(false);
        if (checkInErrorLabel != null) checkInErrorLabel.setVisible(false);
        if (checkOutErrorLabel != null) checkOutErrorLabel.setVisible(false);
        if (nameErrorLabel != null) nameErrorLabel.setVisible(false);
        if (phoneErrorLabel != null) phoneErrorLabel.setVisible(false);
        if (emailErrorLabel != null) emailErrorLabel.setVisible(false);
        if (occupancyValidationLabel != null) occupancyValidationLabel.setVisible(false);
    }
}


