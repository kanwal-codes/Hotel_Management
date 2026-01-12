package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.repository.GuestRepository;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

//
 // Service for validation operations in KioskController.
 // Extracts validation logic to reduce controller size.
//
public final class KioskValidationService {
    
    private KioskValidationService() {
        // Utility class - prevent instantiation
    }
    
    //
     // Validates state before navigating to room selection.
//
    public static boolean validateBeforeRoomSelection(
            LocalDate checkIn,
            LocalDate checkOut,
            int numAdults,
            ReservationService reservationService,
            LoggerService logger) {
        
        if (checkIn == null || checkOut == null) {
            logger.logError("Cannot navigate to room selection: dates are null");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Dates not set");
            alert.setContentText("Please select check-in and check-out dates before proceeding.");
            alert.showAndWait();
            return false;
        }
        
        if (numAdults <= 0) {
            logger.logError("Cannot navigate to room selection: invalid number of adults");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Invalid occupancy");
            alert.setContentText("Number of adults must be greater than 0.");
            alert.showAndWait();
            return false;
        }
        
        if (reservationService == null) {
            logger.logError("Cannot navigate to room selection: ReservationService is null");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("System Error");
            alert.setHeaderText("Service not initialized");
            alert.setContentText("The reservation service is not available. Please restart the application.");
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
    //
     // Validates dates and shows errors if invalid.
//
    public static boolean validateDates(
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            ReservationService reservationService,
            Label checkInErrorLabel,
            Label checkOutErrorLabel,
            Label numNightsDisplayLabel,
            javafx.scene.layout.VBox nightsInfoContainer,
            Consumer<LocalDate> setCheckIn,
            Consumer<LocalDate> setCheckOut,
            Runnable navigateToRoomSelection) {
        
        boolean isValid = true;
        
        // Validate date fields only (occupancy was already validated in BookingDetails)
        LocalDate checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        LocalDate checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        
        if (checkIn == null) {
            if (checkInErrorLabel != null) {
                ValidationHelper.showError(checkInErrorLabel, "Check-in date is required");
            }
            isValid = false;
        } else if (checkIn.isBefore(LocalDate.now())) {
            if (checkInErrorLabel != null) {
                ValidationHelper.showError(checkInErrorLabel, "Check-in date cannot be in the past");
            }
            isValid = false;
        } else {
            if (checkInErrorLabel != null) {
                ValidationHelper.hideError(checkInErrorLabel);
            }
        }
        
        if (checkOut == null) {
            if (checkOutErrorLabel != null) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date is required");
            }
            isValid = false;
        } else if (checkIn != null && (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn))) {
            if (checkOutErrorLabel != null) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
            }
            isValid = false;
        } else {
            if (checkOutErrorLabel != null) {
                ValidationHelper.hideError(checkOutErrorLabel);
            }
        }
        
        // Validate dates using service
        if (isValid && checkIn != null && checkOut != null) {
            if (reservationService != null && !reservationService.validateDates(checkIn, checkOut)) {
                if (checkOutErrorLabel != null) {
                    ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
                }
                isValid = false;
            }
        }
        
        // Update nights display if dates are valid
        if (isValid && checkIn != null && checkOut != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            if (numNightsDisplayLabel != null) {
                numNightsDisplayLabel.setText(nights + " night(s)");
            }
            if (nightsInfoContainer != null) {
                nightsInfoContainer.setVisible(true);
            }
        }
        
        // Only proceed if all validations pass
        if (!isValid) {
            return false;
        }
        
        // Set dates
        if (setCheckIn != null) setCheckIn.accept(checkIn);
        if (setCheckOut != null) setCheckOut.accept(checkOut);
        
        // Proceed to room selection (dates are now selected, show available rooms)
        if (navigateToRoomSelection != null) {
            navigateToRoomSelection.run();
        }
        
        return true;
    }
    
    //
     // Validates guest details form and processes guest.
//
    public static Guest validateGuestDetailsAndProcess(
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            javafx.scene.control.TextArea addressField,
            Label adultsErrorLabel,
            Label childrenErrorLabel,
            Label checkInErrorLabel,
            Label checkOutErrorLabel,
            Label nameErrorLabel,
            Label phoneErrorLabel,
            Label emailErrorLabel,
            GuestRepository guestRepository,
            ReservationService reservationService,
            LoggerService logger,
            Consumer<Integer> setNumAdults,
            Consumer<Integer> setNumChildren,
            Consumer<LocalDate> setCheckIn,
            Consumer<LocalDate> setCheckOut,
            Runnable navigateToRoomSelection) {
        
        // Step 1: Extract and validate occupancy values
        int numAdults = 0;
        int numChildren = 0;
        boolean isValid = true;
        
        try {
            if (numAdultsField == null || numAdultsField.getText().trim().isEmpty()) {
                if (adultsErrorLabel != null) {
                    ValidationHelper.showError(adultsErrorLabel, "Number of adults is required");
                }
                isValid = false;
            } else {
                numAdults = Integer.parseInt(numAdultsField.getText().trim());
                if (numAdults < 1) {
                    if (adultsErrorLabel != null) {
                        ValidationHelper.showError(adultsErrorLabel, "At least 1 adult is required");
                    }
                    isValid = false;
                } else {
                    if (adultsErrorLabel != null) {
                        ValidationHelper.hideError(adultsErrorLabel);
                    }
                }
            }
            
            // Children is optional
            numChildren = 0;
            if (numChildrenField != null && !numChildrenField.getText().trim().isEmpty()) {
                numChildren = Integer.parseInt(numChildrenField.getText().trim());
                if (numChildren < 0) {
                    if (childrenErrorLabel != null) {
                        ValidationHelper.showError(childrenErrorLabel, "Number of children cannot be negative");
                    }
                    isValid = false;
                } else {
                    if (childrenErrorLabel != null) {
                        ValidationHelper.hideError(childrenErrorLabel);
                    }
                }
            }
        } catch (NumberFormatException e) {
            if (adultsErrorLabel != null) {
                ValidationHelper.showError(adultsErrorLabel, "Please enter valid numbers");
            }
            isValid = false;
        }
        
        // Step 2: Extract and validate dates
        LocalDate checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        LocalDate checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        
        if (checkIn == null) {
            if (checkInErrorLabel != null) {
                ValidationHelper.showError(checkInErrorLabel, "Check-in date is required");
            }
            isValid = false;
        } else if (checkIn.isBefore(LocalDate.now())) {
            if (checkInErrorLabel != null) {
                ValidationHelper.showError(checkInErrorLabel, "Check-in date cannot be in the past");
            }
            isValid = false;
        } else {
            if (checkInErrorLabel != null) {
                ValidationHelper.hideError(checkInErrorLabel);
            }
        }
        
        if (checkOut == null) {
            if (checkOutErrorLabel != null) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date is required");
            }
            isValid = false;
        } else if (checkIn != null && (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn))) {
            if (checkOutErrorLabel != null) {
                ValidationHelper.showError(checkOutErrorLabel, "Check-out date must be after check-in date");
            }
            isValid = false;
        } else {
            if (checkOutErrorLabel != null) {
                ValidationHelper.hideError(checkOutErrorLabel);
            }
        }
        
        // Step 3: Validate all contact fields using ValidationHelper
        boolean contactValid = ValidationHelper.validateGuestFields(
            nameField, phoneField, emailField,
            nameErrorLabel, phoneErrorLabel, emailErrorLabel
        );
        
        if (!contactValid) {
            isValid = false;
        }
        
        // If any validation failed, stop here
        if (!isValid) {
            logger.logError("Validation failed - cannot proceed to room selection");
            return null;
        }
        
        // Step 4: Extract contact information
        String address = addressField != null ? addressField.getText().trim() : "";
        String name = nameField != null ? nameField.getText().trim() : "";
        String phone = phoneField != null ? phoneField.getText().trim() : "";
        String email = emailField != null ? emailField.getText().trim() : "";
        
        // Step 5: Create or find guest using helper
        Guest currentGuest = KioskGuestDetailsHelper.processGuestDetails(
            name, phone, email, address, guestRepository, logger, nameErrorLabel);
        
        if (currentGuest != null) {
            // Set state values
            if (setNumAdults != null) setNumAdults.accept(numAdults);
            if (setNumChildren != null) setNumChildren.accept(numChildren);
            if (setCheckIn != null) setCheckIn.accept(checkIn);
            if (setCheckOut != null) setCheckOut.accept(checkOut);
            
            logger.logInfo("All validations passed. Navigating to room selection with: " +
                numAdults + " adults, " + numChildren + " children, " +
                "check-in: " + checkIn + ", check-out: " + checkOut);
            
            // Proceed to room selection (after all guest details are filled)
            if (navigateToRoomSelection != null) {
                navigateToRoomSelection.run();
            }
        } else {
            logger.logError("Failed to process guest details");
        }
        
        return currentGuest;
    }
    
    //
     // Validates occupancy fields and proceeds to date selection.
//
    public static boolean validateOccupancyAndProceed(
            TextField numAdultsField,
            TextField numChildrenField,
            Label adultsErrorLabel,
            Label childrenErrorLabel,
            Consumer<Integer> setNumAdults,
            Consumer<Integer> setNumChildren,
            Runnable navigateToDateSelection) {
        
        try {
            // Validate adults
            if (numAdultsField == null || numAdultsField.getText().trim().isEmpty()) {
                if (adultsErrorLabel != null) {
                    ValidationHelper.showError(adultsErrorLabel, "Number of adults is required");
                }
                return false;
            }
            
            int numAdults = Integer.parseInt(numAdultsField.getText().trim());
            if (numAdults < 1) {
                if (adultsErrorLabel != null) {
                    ValidationHelper.showError(adultsErrorLabel, "At least 1 adult is required");
                }
                return false;
            }
            
            // Validate children (optional)
            int numChildren = 0;
            if (numChildrenField != null && !numChildrenField.getText().trim().isEmpty()) {
                numChildren = Integer.parseInt(numChildrenField.getText().trim());
                if (numChildren < 0) {
                    if (childrenErrorLabel != null) {
                        ValidationHelper.showError(childrenErrorLabel, "Number of children cannot be negative");
                    }
                    return false;
                }
            }
            
            // Set values
            if (setNumAdults != null) setNumAdults.accept(numAdults);
            if (setNumChildren != null) setNumChildren.accept(numChildren);
            
            // Proceed to date selection (step 2 in required flow)
            if (navigateToDateSelection != null) {
                navigateToDateSelection.run();
            }
            
            return true;
        } catch (NumberFormatException e) {
            if (adultsErrorLabel != null) {
                ValidationHelper.showError(adultsErrorLabel, "Please enter valid numbers");
            }
            return false;
        }
    }
    
    //
     // Validates state before proceeding to booking summary.
//
    public static boolean validateBeforeSummary(
            Guest currentGuest,
            List<Room> selectedRooms,
            LoggerService logger,
            Runnable navigateToSummary) {
        
        // Log state before navigation
        logger.logInfo("=== proceedToSummary() called ===");
        logger.logInfo("currentGuest: " + (currentGuest != null ? currentGuest.getName() : "null"));
        logger.logInfo("selectedRooms size: " + (selectedRooms != null ? selectedRooms.size() : "null"));
        
        if (currentGuest == null) {
            logger.logError("currentGuest is null - cannot proceed to summary");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Booking Error");
            alert.setHeaderText("Missing Guest Information");
            alert.setContentText("Please go back and complete guest details.");
            alert.showAndWait();
            return false;
        }
        
        if (selectedRooms == null || selectedRooms.isEmpty()) {
            logger.logError("selectedRooms is null or empty - cannot proceed to summary");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Booking Error");
            alert.setHeaderText("No Rooms Selected");
            alert.setContentText("Please go back and select at least one room.");
            alert.showAndWait();
            return false;
        }
        
        // Navigate to summary
        if (navigateToSummary != null) {
            navigateToSummary.run();
        }
        
        return true;
    }
}

