package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.repository.GuestRepository;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

//
 // Helper class for guest details screen logic in KioskController.
 // Extracts validation, processing, and loyalty logic to reduce KioskController size.
//
 // All @FXML fields and methods remain in KioskController - this class only contains
 // the business logic that can be extracted.
//
public final class KioskGuestDetailsHelper {
    
    private KioskGuestDetailsHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Validates occupancy fields and returns validation result.
//
     // @param numAdultsField Text field for number of adults
     // @param numChildrenField Text field for number of children
     // @param adultsErrorLabel Error label for adults field
     // @param childrenErrorLabel Error label for children field
     // @return ValidationResult containing validation status and parsed values
//
    public static ValidationResult validateOccupancyFields(
            TextField numAdultsField,
            TextField numChildrenField,
            Label adultsErrorLabel,
            Label childrenErrorLabel) {
        
        ValidationResult result = new ValidationResult();
        
        // Validate adults
        if (numAdultsField == null || numAdultsField.getText().trim().isEmpty()) {
            ValidationHelper.showError(adultsErrorLabel, "Number of adults is required");
            result.isValid = false;
            return result;
        }
        
        try {
            result.numAdults = Integer.parseInt(numAdultsField.getText().trim());
            if (result.numAdults < 1) {
                ValidationHelper.showError(adultsErrorLabel, "At least 1 adult is required");
                result.isValid = false;
                return result;
            } else {
                ValidationHelper.hideError(adultsErrorLabel);
            }
        } catch (NumberFormatException e) {
            ValidationHelper.showError(adultsErrorLabel, "Please enter valid numbers");
            result.isValid = false;
            return result;
        }
        
        // Validate children (optional)
        result.numChildren = 0;
        if (numChildrenField != null && !numChildrenField.getText().trim().isEmpty()) {
            try {
                result.numChildren = Integer.parseInt(numChildrenField.getText().trim());
                if (result.numChildren < 0) {
                    ValidationHelper.showError(childrenErrorLabel, "Number of children cannot be negative");
                    result.isValid = false;
                    return result;
                } else {
                    ValidationHelper.hideError(childrenErrorLabel);
                }
            } catch (NumberFormatException e) {
                ValidationHelper.showError(childrenErrorLabel, "Please enter a valid number");
                result.isValid = false;
                return result;
            }
        } else {
            ValidationHelper.hideError(childrenErrorLabel);
        }
        
        result.isValid = true;
        return result;
    }
    
    //
     // Validates occupancy fields for display (real-time validation).
//
    public static void validateOccupancyFieldsForDisplay(
            TextField numAdultsField,
            TextField numChildrenField,
            Label adultsErrorLabel,
            Label childrenErrorLabel) {
        
        // Validate adults
        if (numAdultsField != null) {
            String adultsText = numAdultsField.getText().trim();
            if (adultsText.isEmpty()) {
                ValidationHelper.showError(adultsErrorLabel, "Number of adults is required");
            } else {
                try {
                    int adults = Integer.parseInt(adultsText);
                    if (adults < 1) {
                        ValidationHelper.showError(adultsErrorLabel, "At least 1 adult is required");
                    } else {
                        ValidationHelper.hideError(adultsErrorLabel);
                    }
                } catch (NumberFormatException e) {
                    ValidationHelper.showError(adultsErrorLabel, "Please enter a valid number");
                }
            }
        }
        
        // Validate children (optional)
        if (numChildrenField != null && !numChildrenField.getText().trim().isEmpty()) {
            try {
                int children = Integer.parseInt(numChildrenField.getText().trim());
                if (children < 0) {
                    ValidationHelper.showError(childrenErrorLabel, "Number of children cannot be negative");
                } else {
                    ValidationHelper.hideError(childrenErrorLabel);
                }
            } catch (NumberFormatException e) {
                ValidationHelper.showError(childrenErrorLabel, "Please enter a valid number");
            }
        } else if (numChildrenField != null) {
            ValidationHelper.hideError(childrenErrorLabel);
        }
    }
    
    //
     // Validates date fields and returns validation result.
//
    public static DateValidationResult validateDateFields(
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            Label checkInErrorLabel,
            Label checkOutErrorLabel,
            ReservationService reservationService) {
        
        DateValidationResult result = new DateValidationResult();
        result.checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        result.checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        
        if (result.checkIn == null) {
            ValidationHelper.showError(checkInErrorLabel, "Check-in date is required");
            result.isValid = false;
            return result;
        } else if (result.checkIn.isBefore(LocalDate.now())) {
            ValidationHelper.showError(checkInErrorLabel, "Check-in date cannot be in the past");
            result.isValid = false;
            return result;
        } else {
            ValidationHelper.hideError(checkInErrorLabel);
        }
        
        if (result.checkOut == null) {
            ValidationHelper.showError(checkOutErrorLabel, "Check-out date is required");
            result.isValid = false;
            return result;
        } else if (result.checkIn != null && (result.checkOut.isBefore(result.checkIn) || result.checkOut.isEqual(result.checkIn))) {
            ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
            result.isValid = false;
            return result;
        } else {
            ValidationHelper.hideError(checkOutErrorLabel);
        }
        
        // Validate dates using service
        if (reservationService != null && result.checkIn != null && result.checkOut != null) {
            if (!reservationService.validateDates(result.checkIn, result.checkOut)) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
                result.isValid = false;
                return result;
            }
        }
        
        result.isValid = true;
        return result;
    }
    
    //
     // Validates individual check-in date.
//
    public static LocalDate validateCheckInDate(
            DatePicker checkInDatePicker,
            LocalDate currentCheckOut,
            Label checkInErrorLabel,
            Label checkOutErrorLabel) {
        
        LocalDate checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        
        if (checkIn == null) {
            ValidationHelper.showError(checkInErrorLabel, "Check-in date is required");
        } else if (checkIn.isBefore(LocalDate.now())) {
            ValidationHelper.showError(checkInErrorLabel, "Check-in date cannot be in the past");
        } else {
            ValidationHelper.hideError(checkInErrorLabel);
            // If check-out is already set, validate the range
            if (currentCheckOut != null && (currentCheckOut.isBefore(checkIn) || currentCheckOut.isEqual(checkIn))) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
            } else if (currentCheckOut != null) {
                ValidationHelper.hideError(checkOutErrorLabel);
            }
        }
        
        return checkIn;
    }
    
    //
     // Validates individual check-out date.
//
    public static LocalDate validateCheckOutDate(
            DatePicker checkOutDatePicker,
            LocalDate currentCheckIn,
            Label checkOutErrorLabel) {
        
        LocalDate checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        
        if (checkOut == null) {
            ValidationHelper.showError(checkOutErrorLabel, "Check-out date is required");
        } else {
            if (currentCheckIn != null && (checkOut.isBefore(currentCheckIn) || checkOut.isEqual(currentCheckIn))) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
            } else {
                ValidationHelper.hideError(checkOutErrorLabel);
            }
        }
        
        return checkOut;
    }
    
    //
     // Updates the nights display label and container.
//
    public static void updateNightsDisplay(
            LocalDate checkIn,
            LocalDate checkOut,
            Label numNightsDisplayLabel,
            VBox nightsInfoContainer) {
        
        if (checkIn != null && checkOut != null && checkOut.isAfter(checkIn)) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (numNightsDisplayLabel != null) {
                numNightsDisplayLabel.setText(nights + " night(s)");
            }
            if (nightsInfoContainer != null) {
                nightsInfoContainer.setVisible(true);
            }
        } else {
            if (nightsInfoContainer != null) {
                nightsInfoContainer.setVisible(false);
            }
        }
    }
    
    //
     // Processes guest details - creates or finds guest.
//
    public static Guest processGuestDetails(
            String name,
            String phone,
            String email,
            String address,
            GuestRepository guestRepository,
            LoggerService logger,
            Label errorLabel) {
        
        try {
            // Try to find existing guest by email
            Optional<Guest> guestOpt = guestRepository.findByEmail(email);
            Guest guest;
            if (guestOpt.isPresent()) {
                guest = guestOpt.get();
                // Update guest info
                guest.setName(name);
                guest.setPhone(phone);
                guest.setAddress(address);
                guest = guestRepository.save(guest);
            } else {
                // Create new guest
                guest = new Guest(name, phone, email, address);
                guest = guestRepository.save(guest);
            }
            return guest;
        } catch (Exception e) {
            logger.logError("Failed to save guest", e);
            ValidationHelper.showError(errorLabel, "Failed to save guest information");
            return null;
        }
    }
    
    //
     // Looks up guest by loyalty number.
//
    public static Guest lookupLoyalty(
            String loyaltyNumber,
            GuestRepository guestRepository,
            LoggerService logger) {
        
        if (loyaltyNumber == null || loyaltyNumber.trim().isEmpty()) {
            return null;
        }
        
        try {
            Optional<Guest> guestOpt = guestRepository.findByLoyaltyNumber(loyaltyNumber.trim());
            return guestOpt.orElse(null);
        } catch (Exception e) {
            logger.logError("Failed to lookup loyalty number", e);
            return null;
        }
    }
    
    //
     // Enrolls guest in loyalty program.
//
    public static String enrollGuestInLoyalty(
            Guest guest,
            GuestRepository guestRepository,
            LoggerService logger) {
        
        if (guest == null) {
            return null;
        }
        
        try {
            // Check if already enrolled
            if (guest.getLoyaltyNumber() != null && !guest.getLoyaltyNumber().isEmpty()) {
                return guest.getLoyaltyNumber();
            }
            
            // Generate loyalty number
            String loyaltyNumber = "L" + String.format("%06d", guest.getId());
            guest.setLoyaltyNumber(loyaltyNumber);
            guest.setLoyaltyPoints(0);
            
            // Save guest
            guestRepository.save(guest);
            
            logger.logActivity("KIOSK", "ENROLL_LOYALTY", "Guest", guest.getId(), 
                "Guest enrolled with loyalty number: " + loyaltyNumber);
            
            return loyaltyNumber;
        } catch (Exception e) {
            logger.logError("Failed to enroll in loyalty program", e);
            return null;
        }
    }
    
    //
     // Populates guest fields from loyalty lookup.
//
    public static void populateGuestFromLoyalty(
            Guest guest,
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            TextArea addressField) {
        
        if (guest == null) {
            return;
        }
        
        if (nameField != null && guest.getName() != null) {
            nameField.setText(guest.getName());
        }
        if (phoneField != null && guest.getPhone() != null) {
            phoneField.setText(guest.getPhone());
        }
        if (emailField != null && guest.getEmail() != null) {
            emailField.setText(guest.getEmail());
        }
        if (addressField != null && guest.getAddress() != null) {
            addressField.setText(guest.getAddress());
        }
    }
    
    //
     // Checks and updates loyalty enrollment status display.
//
    public static void checkLoyaltyEnrollmentStatus(
            Guest guest,
            VBox loyaltyEnrollmentContainer,
            VBox loyaltyEnrolledDisplayContainer,
            Label loyaltyEnrolledLabel) {
        
        if (loyaltyEnrollmentContainer == null || loyaltyEnrolledDisplayContainer == null) {
            return;
        }
        
        if (guest != null) {
            if (guest.getLoyaltyNumber() == null || guest.getLoyaltyNumber().isEmpty()) {
                // Not enrolled - show enrollment option
                loyaltyEnrollmentContainer.setVisible(true);
                loyaltyEnrollmentContainer.setManaged(true);
                loyaltyEnrolledDisplayContainer.setVisible(false);
                loyaltyEnrolledDisplayContainer.setManaged(false);
            } else {
                // Already enrolled - show loyalty info
                loyaltyEnrollmentContainer.setVisible(false);
                loyaltyEnrollmentContainer.setManaged(false);
                loyaltyEnrolledDisplayContainer.setVisible(true);
                loyaltyEnrolledDisplayContainer.setManaged(true);
                if (loyaltyEnrolledLabel != null) {
                    loyaltyEnrolledLabel.setText(
                        "Loyalty Number: " + guest.getLoyaltyNumber() + "\n" +
                        "Current Points: " + guest.getLoyaltyPoints()
                    );
                }
            }
        } else {
            // No guest yet - hide both
            loyaltyEnrollmentContainer.setVisible(false);
            loyaltyEnrollmentContainer.setManaged(false);
            loyaltyEnrolledDisplayContainer.setVisible(false);
            loyaltyEnrolledDisplayContainer.setManaged(false);
        }
    }
    
    // ========== Inner Classes for Return Values ==========
    
    //
     // Result of occupancy validation.
//
    public static class ValidationResult {
        public boolean isValid = false;
        public int numAdults = 0;
        public int numChildren = 0;
    }
    
    //
     // Result of date validation.
//
    public static class DateValidationResult {
        public boolean isValid = false;
        public LocalDate checkIn = null;
        public LocalDate checkOut = null;
    }
}

