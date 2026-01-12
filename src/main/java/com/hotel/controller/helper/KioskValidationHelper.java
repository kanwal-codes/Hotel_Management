package com.hotel.controller.helper;

import com.hotel.util.Validator;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.function.BooleanSupplier;

//
 // Helper class for validation logic in KioskController.
 // Extracts validation methods for different screen types.
//
public final class KioskValidationHelper {
    
    private KioskValidationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Validates room selection fields.
//
    public static boolean validateRoomSelectionFields(
            boolean customSelectionActive,
            javafx.scene.layout.VBox customSelectionContainer,
            javafx.scene.layout.VBox suggestedPlanContainer,
            javafx.scene.control.Spinner<Integer> singleRoomSpinner,
            javafx.scene.control.Spinner<Integer> doubleRoomSpinner,
            javafx.scene.control.Spinner<Integer> deluxeRoomSpinner,
            javafx.scene.control.Spinner<Integer> penthouseSpinner) {
        
        // Check if we're in custom selection mode
        if (customSelectionContainer != null && customSelectionContainer.isVisible()) {
            // Custom selection: check if at least one room is selected
            int totalRooms = 0;
            if (singleRoomSpinner != null) totalRooms += singleRoomSpinner.getValue();
            if (doubleRoomSpinner != null) totalRooms += doubleRoomSpinner.getValue();
            if (deluxeRoomSpinner != null) totalRooms += deluxeRoomSpinner.getValue();
            if (penthouseSpinner != null) totalRooms += penthouseSpinner.getValue();
            return totalRooms > 0;
        } else if (suggestedPlanContainer != null && suggestedPlanContainer.isVisible()) {
            // Suggested plan mode: always valid (user can accept or choose custom)
            return true;
        }
        return false;
    }
    
    //
     // Validates occupancy fields.
//
    public static boolean validateOccupancyFields(TextField numAdultsField) {
        if (numAdultsField == null) return false;
        String adultsText = numAdultsField.getText().trim();
        if (adultsText.isEmpty()) return false;
        try {
            int adults = Integer.parseInt(adultsText);
            return adults >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    //
     // Validates date fields.
//
    public static boolean validateDateFields(
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker) {
        return checkInDatePicker != null && checkOutDatePicker != null &&
               checkInDatePicker.getValue() != null && 
               checkOutDatePicker.getValue() != null &&
               checkOutDatePicker.getValue().isAfter(checkInDatePicker.getValue());
    }
    
    //
     // Validates guest fields.
//
    public static boolean validateGuestFields(
            TextField nameField,
            TextField phoneField,
            TextField emailField) {
        if (nameField == null || phoneField == null || emailField == null) return false;
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        return name.length() >= 2 && 
               Validator.isValidName(name) &&
               !phone.isEmpty() && Validator.isValidPhone(phone) &&
               !email.isEmpty() && Validator.isValidEmail(email);
    }
    
    //
     // Updates Next button state based on current screen.
//
    public static void updateNextButtonState(
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            TextField nameField,
            Button nextButtonOccupancy,
            Button nextButtonDates,
            Button nextButtonGuest,
            Button nextButtonRoomSelection,
            Button nextButtonAddOns,
            BooleanSupplier validateOccupancyFields,
            BooleanSupplier validateDateFields,
            BooleanSupplier validateGuestFields,
            BooleanSupplier validateRoomSelectionFields) {
        
        // Update Next button state based on current screen
        if (numAdultsField != null && numChildrenField != null && checkInDatePicker == null && nextButtonOccupancy != null) {
            // BookingDetails screen (occupancy only) - legacy, should not be used
            boolean isValid = validateOccupancyFields != null && validateOccupancyFields.getAsBoolean();
            nextButtonOccupancy.setDisable(!isValid);
        } else if (checkInDatePicker != null && nameField == null && nextButtonDates != null) {
            // DateSelection screen (dates only) - legacy, should not be used
            boolean isValid = validateDateFields != null && validateDateFields.getAsBoolean();
            nextButtonDates.setDisable(!isValid);
        } else if (nameField != null && nextButtonGuest != null) {
            // GuestDetails screen (now includes occupancy, dates, and contact info)
            boolean isValid = (validateGuestFields == null || validateGuestFields.getAsBoolean()) && 
                             (validateOccupancyFields == null || validateOccupancyFields.getAsBoolean()) && 
                             (validateDateFields == null || validateDateFields.getAsBoolean());
            nextButtonGuest.setDisable(!isValid);
        } else if (nextButtonRoomSelection != null) {
            // RoomSelection screen - check if rooms are selected
            boolean isValid = validateRoomSelectionFields != null && validateRoomSelectionFields.getAsBoolean();
            nextButtonRoomSelection.setDisable(!isValid);
        } else if (nextButtonAddOns != null) {
            // AddOnServices screen - no requirements, always enabled
            nextButtonAddOns.setDisable(false);
        }
    }
}

