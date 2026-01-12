package com.hotel.controller.helper;

import com.hotel.model.Room;
import com.hotel.service.ReservationService;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

//
 // Helper class for admin reservation form validation.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminReservationValidationHelper {
    
    private AdminReservationValidationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Validates new reservation form fields.
//
    public static boolean validateNewReservationForm(
            TextField guestNameField,
            TextField guestPhoneField,
            TextField guestEmailField,
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            List<Room> pendingRooms,
            ReservationService reservationService,
            BooleanSupplier validateRoomSelectionPrerequisites,
            Consumer<String> updateRoomSelectionError,
            BooleanSupplier validateOccupancy) {
        
        // Use ValidationHelper for guest field validation
        // Note: ValidationHelper shows errors in labels, but we need AlertHelper for admin
        // So we validate and show alerts if invalid
        if (guestNameField == null || guestNameField.getText().isBlank()) {
            AlertHelper.showError("Error", "Guest name is required.");
            return false;
        }
        if (!ValidationHelper.isValidName(guestNameField.getText())) {
            AlertHelper.showError("Error", "Please enter a valid name (at least 2 characters).");
            return false;
        }
        if (guestPhoneField == null || guestPhoneField.getText().isBlank() ||
            !ValidationHelper.isValidPhone(guestPhoneField.getText())) {
            AlertHelper.showError("Error", "A valid phone number is required.");
            return false;
        }
        if (guestEmailField == null || guestEmailField.getText().isBlank() ||
            !ValidationHelper.isValidEmail(guestEmailField.getText())) {
            AlertHelper.showError("Error", "A valid email address is required.");
            return false;
        }
        if (validateRoomSelectionPrerequisites != null && !validateRoomSelectionPrerequisites.getAsBoolean()) {
            return false;
        }
        if (pendingRooms == null || pendingRooms.isEmpty()) {
            updateRoomSelectionError.accept("Please add at least one room to the reservation.");
            return false;
        }
        if (validateOccupancy != null && !validateOccupancy.getAsBoolean()) {
            return false;
        }
        LocalDate checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        LocalDate checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        if (!reservationService.validateDates(checkIn, checkOut)) {
            AlertHelper.showError("Error", "Please select valid check-in and check-out dates.");
            return false;
        }
        return true;
    }
    
    //
     // Validates occupancy and shows confirmation dialog if needed.
     // Returns true if user wants to proceed, false if cancelled.
//
    public static boolean validateOccupancyWithConfirmation(
            List<Room> roomsToValidate,
            int adults,
            int children,
            ReservationService reservationService,
            Runnable calculateRoomCapacity,
            Runnable showOccupancyDialog) {
        
        if (!reservationService.validateOccupancy(roomsToValidate, adults, children)) {
            showOccupancyDialog.run();
            return false; // User cancelled
        }
        return true; // Valid or user chose to proceed
    }
}


