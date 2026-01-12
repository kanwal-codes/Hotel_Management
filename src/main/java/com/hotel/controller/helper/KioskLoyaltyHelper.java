package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.repository.GuestRepository;
import com.hotel.util.LoggerService;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Optional;

//
 // Helper class for loyalty program operations in KioskController.
 // Extracts loyalty enrollment and lookup logic to reduce controller size.
//
public final class KioskLoyaltyHelper {
    
    private KioskLoyaltyHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Enrolls a guest in the loyalty program from guest details screen.
//
    public static void enrollInLoyaltyFromGuestDetails(
            Guest currentGuest,
            GuestRepository guestRepository,
            LoggerService logger,
            Runnable checkLoyaltyEnrollmentStatus) {
        
        if (currentGuest == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Enrollment Error");
            alert.setHeaderText("No Guest Information");
            alert.setContentText("Please complete your guest details first.");
            alert.showAndWait();
            return;
        }
        
        // Check if already enrolled
        if (currentGuest.getLoyaltyNumber() != null && !currentGuest.getLoyaltyNumber().isEmpty()) {
            if (checkLoyaltyEnrollmentStatus != null) {
                checkLoyaltyEnrollmentStatus.run();
            }
            return;
        }
        
        // Confirm with guest before enrollment (as required by project specifications)
        // Use user information already filled
        String guestInfo = "Name: " + (currentGuest.getName() != null ? currentGuest.getName() : "N/A") + "\n" +
                          "Email: " + (currentGuest.getEmail() != null ? currentGuest.getEmail() : "N/A") + "\n" +
                          "Phone: " + (currentGuest.getPhone() != null ? currentGuest.getPhone() : "N/A");
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Loyalty Enrollment");
        confirmAlert.setHeaderText("Enroll in Loyalty Program?");
        confirmAlert.setContentText("We'll use the following information:\n\n" + guestInfo + 
            "\n\nA loyalty number will be issued to you.\n\nDo you want to proceed?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed - proceed with enrollment
            String loyaltyNumber = KioskGuestDetailsHelper.enrollGuestInLoyalty(
                currentGuest, guestRepository, logger);
            
            if (loyaltyNumber != null) {
                // Update UI
                if (checkLoyaltyEnrollmentStatus != null) {
                    checkLoyaltyEnrollmentStatus.run();
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enrollment Successful");
                alert.setHeaderText("Welcome to our Loyalty Program!");
                alert.setContentText("Your loyalty number is: " + loyaltyNumber + "\nYou'll earn points on every stay!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Enrollment Error");
                alert.setHeaderText("Failed to enroll");
                alert.setContentText("Could not enroll in loyalty program.");
                alert.showAndWait();
            }
        }
    }
    
    //
     // Enrolls a guest in the loyalty program from confirmation screen.
//
    public static void enrollInLoyalty(
            Guest currentGuest,
            GuestRepository guestRepository,
            LoggerService logger,
            VBox loyaltyEnrollmentContainer,
            VBox loyaltyEnrolledContainer,
            Label loyaltyNumberLabel,
            Label loyaltyPointsLabel) {
        
        if (currentGuest == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Enrollment Error");
            alert.setHeaderText("No Guest Information");
            alert.setContentText("Please complete your booking details first.");
            alert.showAndWait();
            return;
        }
        
        // Check if already enrolled
        if (currentGuest.getLoyaltyNumber() != null && !currentGuest.getLoyaltyNumber().isEmpty()) {
            KioskConfirmationHelper.updateLoyaltyEnrollmentUI(
                currentGuest, loyaltyEnrollmentContainer, loyaltyEnrolledContainer,
                loyaltyNumberLabel, loyaltyPointsLabel);
            return;
        }
        
        // Confirm with guest before enrollment (as required by project specifications)
        // Use user information already filled
        String guestInfo = "Name: " + (currentGuest.getName() != null ? currentGuest.getName() : "N/A") + "\n" +
                          "Email: " + (currentGuest.getEmail() != null ? currentGuest.getEmail() : "N/A") + "\n" +
                          "Phone: " + (currentGuest.getPhone() != null ? currentGuest.getPhone() : "N/A");
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Loyalty Enrollment");
        confirmAlert.setHeaderText("Enroll in Loyalty Program?");
        confirmAlert.setContentText("We'll use the following information:\n\n" + guestInfo + 
            "\n\nA loyalty number will be issued to you.\n\nDo you want to proceed?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed - proceed with enrollment
            String loyaltyNumber = KioskGuestDetailsHelper.enrollGuestInLoyalty(
                currentGuest, guestRepository, logger);
            
            if (loyaltyNumber != null) {
                // Update UI
                KioskConfirmationHelper.updateLoyaltyEnrollmentUI(
                    currentGuest, loyaltyEnrollmentContainer, loyaltyEnrolledContainer,
                    loyaltyNumberLabel, loyaltyPointsLabel);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enrollment Successful");
                alert.setHeaderText("Welcome to our Loyalty Program!");
                alert.setContentText("Your loyalty number is: " + loyaltyNumber + "\nYou'll earn points on every stay!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Enrollment Error");
                alert.setHeaderText("Failed to enroll");
                alert.setContentText("Could not enroll in loyalty program.");
                alert.showAndWait();
            }
        }
    }
    
    //
     // Looks up a guest by loyalty number and updates UI.
//
    public static void lookupLoyalty(
            String loyaltyNumber,
            GuestRepository guestRepository,
            LoggerService logger,
            javafx.scene.control.TextField loyaltyNumberField,
            javafx.scene.control.Label loyaltyLookupLabel,
            javafx.scene.control.TextField nameField,
            javafx.scene.control.TextField phoneField,
            javafx.scene.control.TextField emailField,
            javafx.scene.control.TextArea addressField,
            java.util.function.Consumer<Guest> setCurrentGuest) {
        
        if (loyaltyNumberField == null || loyaltyNumber == null || loyaltyNumber.trim().isEmpty()) {
            return;
        }
        
        Guest foundGuest = KioskGuestDetailsHelper.lookupLoyalty(
            loyaltyNumber, guestRepository, logger);
        
        if (foundGuest != null) {
            // Pre-fill guest information
            if (setCurrentGuest != null) {
                setCurrentGuest.accept(foundGuest);
            }
            KioskGuestDetailsHelper.populateGuestFromLoyalty(
                foundGuest, nameField, phoneField, emailField, addressField);
            
            if (loyaltyLookupLabel != null) {
                loyaltyLookupLabel.setText(String.format(
                    "✓ Found! Welcome back, %s. You have %d loyalty points.",
                    foundGuest.getName(), foundGuest.getLoyaltyPoints()
                ));
                loyaltyLookupLabel.setVisible(true);
                loyaltyLookupLabel.setStyle("-fx-text-fill: green;");
            }
            
            logger.logInfo("Loyalty lookup successful for: " + loyaltyNumber);
        } else {
            if (loyaltyLookupLabel != null) {
                loyaltyLookupLabel.setText("Loyalty number not found. Please check and try again.");
                loyaltyLookupLabel.setVisible(true);
                loyaltyLookupLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
}


