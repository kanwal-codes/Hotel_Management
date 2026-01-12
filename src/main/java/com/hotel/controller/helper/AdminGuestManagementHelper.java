package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.repository.GuestRepository;
import com.hotel.service.ActivityLogService;
import com.hotel.util.LoggerService;
import com.hotel.security.BCryptPasswordHasher;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.util.List;
import java.util.Optional;

//
 // Helper class for guest management UI operations in AdminReservationController.
 // Handles loyalty enrollment, guest selection dialogs, and guest-related UI updates.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminGuestManagementHelper {
    
    //
     // Result class for guest selection dialogs.
//
    public static class GuestSelectionResult {
        public Guest guest;
        public boolean createAccount;
        public boolean proceedAsGuest;
        
        public GuestSelectionResult(Guest guest, boolean createAccount, boolean proceedAsGuest) {
            this.guest = guest;
            this.createAccount = createAccount;
            this.proceedAsGuest = proceedAsGuest;
        }
    }
    
    private AdminGuestManagementHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Updates the loyalty enrollment button visibility based on guest email.
//
    public static void updateLoyaltyEnrollmentButton(
            String email,
            GuestRepository guestRepository,
            Button enrollLoyaltyButton,
            HBox loyaltyContainer,
            TextField loyaltyNumberField) {
        
        if (enrollLoyaltyButton == null || email == null || loyaltyContainer == null) return;
        
        // Show enroll button if email is provided and guest might not be enrolled
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            // No email yet - show enroll button anyway so user knows they can enroll
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
            enrollLoyaltyButton.setVisible(true);
            enrollLoyaltyButton.setManaged(true);
            return;
        }
        
        try {
            java.util.Optional<Guest> guestOpt = guestRepository.findByEmail(trimmedEmail);
            if (guestOpt.isPresent()) {
                Guest guest = guestOpt.get();
                boolean isEnrolled = guest.getLoyaltyNumber() != null && !guest.getLoyaltyNumber().isEmpty();
                if (isEnrolled) {
                    // Guest is enrolled - show loyalty number field
                    loyaltyContainer.setVisible(true);
                    loyaltyContainer.setManaged(true);
                    if (loyaltyNumberField != null) {
                        loyaltyNumberField.setText(guest.getLoyaltyNumber());
                        loyaltyNumberField.setEditable(false);
                    }
                    enrollLoyaltyButton.setVisible(false);
                    enrollLoyaltyButton.setManaged(false);
                } else {
                    // Guest exists but not enrolled - show enroll button only
                    loyaltyContainer.setVisible(false);
                    loyaltyContainer.setManaged(false);
                    enrollLoyaltyButton.setVisible(true);
                    enrollLoyaltyButton.setManaged(true);
                }
            } else {
                // New guest - show enroll button only (loyalty field hidden until enrolled)
                loyaltyContainer.setVisible(false);
                loyaltyContainer.setManaged(false);
                enrollLoyaltyButton.setVisible(true);
                enrollLoyaltyButton.setManaged(true);
            }
        } catch (Exception e) {
            // Ignore errors - just show enroll button
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
            enrollLoyaltyButton.setVisible(true);
            enrollLoyaltyButton.setManaged(true);
        }
    }
    
    //
     // Enrolls a guest in the loyalty program.
     // Creates guest if they don't exist, then generates loyalty number.
//
    public static String enrollGuestInLoyalty(
            String email,
            String name,
            String phone,
            GuestRepository guestRepository,
            ActivityLogService activityLogService,
            LoggerService logger,
            String adminUsername) {
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required for loyalty enrollment");
        }
        
        try {
            String trimmedEmail = email.trim();
            Optional<Guest> guestOpt = guestRepository.findByEmail(trimmedEmail);
            
            Guest guest;
            if (guestOpt.isEmpty()) {
                // Guest doesn't exist yet - create a temporary guest for enrollment
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException("Guest name is required");
                }
                if (phone == null || phone.trim().isEmpty()) {
                    throw new IllegalArgumentException("Guest phone is required");
                }
                
                // Create guest with provided information
                guest = new Guest(
                    name.trim(),
                    phone.trim(),
                    trimmedEmail,
                    null
                );
                guest = guestRepository.save(guest);
            } else {
                guest = guestOpt.get();
            }
            
            // Check if already enrolled
            if (guest.getLoyaltyNumber() != null && !guest.getLoyaltyNumber().isEmpty()) {
                return guest.getLoyaltyNumber(); // Return existing loyalty number
            }
            
            // Generate loyalty number
            String loyaltyNumber = "L" + String.format("%06d", guest.getId());
            guest.setLoyaltyNumber(loyaltyNumber);
            guest.setLoyaltyPoints(0);
            
            guestRepository.save(guest);
            
            // Log activity
            if (activityLogService != null) {
                activityLogService.logActivity(
                    adminUsername != null ? adminUsername : "ADMIN",
                    "ENROLL_LOYALTY", 
                    "Guest", 
                    guest.getId(),
                    "Enrolled guest with loyalty number: " + loyaltyNumber
                );
            }
            
            return loyaltyNumber;
                
        } catch (Exception e) {
            logger.logError("Failed to enroll guest in loyalty program", e);
            throw e;
        }
    }
    
    //
     // Fills guest details into form fields.
//
    public static void fillGuestDetails(
            Guest guest,
            TextField guestNameField,
            TextField guestPhoneField,
            TextField guestEmailField,
            Runnable updateLoyaltyEnrollmentButton) {
        
        if (guest == null) return;
        
        if (guestNameField != null) {
            guestNameField.setText(guest.getName() != null ? guest.getName() : "");
        }
        if (guestPhoneField != null) {
            guestPhoneField.setText(guest.getPhone() != null ? guest.getPhone() : "");
        }
        if (guestEmailField != null) {
            guestEmailField.setText(guest.getEmail() != null ? guest.getEmail() : "");
        }
        
        // Update loyalty enrollment button based on email
        if (updateLoyaltyEnrollmentButton != null) {
            updateLoyaltyEnrollmentButton.run();
        }
    }
    
    // TODO: Extract dialog methods (these are more complex and tightly coupled to controller)
    // - showCustomerSelectionDialog()
    // - showNewUserOptionsDialog()
    // - showAccountCreationDialog()
}

