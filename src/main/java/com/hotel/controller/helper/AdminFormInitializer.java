package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.model.Waitlist;
import com.hotel.service.ReservationService;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

//
 // Helper class for initializing admin reservation forms.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminFormInitializer {
    
    private AdminFormInitializer() {
        // Utility class - prevent instantiation
    }
    
    //
     // Initializes form for create mode.
//
    public static void startCreateMode(
            Consumer<Boolean> setCreatingNewReservation,
            Consumer<Void> clearPendingRooms,
            Consumer<Void> clearRoomTableData,
            Runnable clearReservationForm,
            VBox billingInformationContainer,
            HBox loyaltyContainer,
            Button enrollLoyaltyButton,
            ComboBox<String> statusComboBox,
            Label modeLabel,
            Runnable updateActionButtons,
            Consumer<String> updateRoomSelectionError) {
        
        setCreatingNewReservation.accept(true);
        clearPendingRooms.accept(null);
        clearRoomTableData.accept(null);
        clearReservationForm.run();
        
        // Hide billing information section for new reservations
        if (billingInformationContainer != null) {
            billingInformationContainer.setVisible(false);
            billingInformationContainer.setManaged(false);
        }
        
        // Hide loyalty container initially (will show after enrollment)
        if (loyaltyContainer != null) {
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
        }
        // Show enroll button by default when creating reservation
        if (enrollLoyaltyButton != null) {
            enrollLoyaltyButton.setVisible(true);
            enrollLoyaltyButton.setManaged(true);
        }
        
        if (statusComboBox != null) {
            statusComboBox.setValue("Pending");
            statusComboBox.setDisable(true);
        }
        if (modeLabel != null) {
            modeLabel.setVisible(true);
            modeLabel.setText("Create Mode");
        }
        updateActionButtons.run();
        updateRoomSelectionError.accept(null);
    }
    
    //
     // Clears reservation form fields.
//
    public static void clearReservationForm(
            TextField guestNameField,
            TextField guestPhoneField,
            TextField guestEmailField,
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            TextField numberOfGuestsField,
            Runnable updateGuestCountSummary) {
        
        if (guestNameField != null) guestNameField.clear();
        if (guestPhoneField != null) guestPhoneField.clear();
        if (guestEmailField != null) guestEmailField.clear();
        if (numAdultsField != null) numAdultsField.setText("1");
        if (numChildrenField != null) numChildrenField.setText("0");
        if (checkInDatePicker != null) checkInDatePicker.setValue(null);
        if (checkOutDatePicker != null) checkOutDatePicker.setValue(null);
        if (numberOfGuestsField != null) numberOfGuestsField.clear();
        updateGuestCountSummary.run();
    }
    
    //
     // Initializes form from waitlist entry.
//
    public static void initFromWaitlist(
            Waitlist waitlist,
            Consumer<Boolean> setCreatingNewReservation,
            Consumer<Waitlist> setWaitlistToRemove,
            Consumer<Void> clearPendingRooms,
            Consumer<Void> clearRoomTableData,
            TextField guestNameField,
            TextField guestPhoneField,
            TextField guestEmailField,
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            Runnable updateLoyaltyEnrollmentButton) {
        
        setCreatingNewReservation.accept(true);
        setWaitlistToRemove.accept(waitlist);
        clearPendingRooms.accept(null);
        clearRoomTableData.accept(null);
        
        // Pre-fill form with waitlist data
        if (waitlist.getGuest() != null) {
            Guest guest = waitlist.getGuest();
            if (guestNameField != null) guestNameField.setText(guest.getName() != null ? guest.getName() : "");
            if (guestPhoneField != null) guestPhoneField.setText(guest.getPhone() != null ? guest.getPhone() : "");
            if (guestEmailField != null) guestEmailField.setText(guest.getEmail() != null ? guest.getEmail() : "");
        }
        
        // Pre-fill dates
        if (checkInDatePicker != null) checkInDatePicker.setValue(waitlist.getDateRangeStart());
        if (checkOutDatePicker != null) checkOutDatePicker.setValue(waitlist.getDateRangeEnd());
        
        // Pre-fill adults and children from waitlist
        if (numAdultsField != null) {
            numAdultsField.setText(waitlist.getNumAdults() != null ? String.valueOf(waitlist.getNumAdults()) : "1");
        }
        if (numChildrenField != null) {
            numChildrenField.setText(waitlist.getNumChildren() != null ? String.valueOf(waitlist.getNumChildren()) : "0");
        }
        
        if (updateLoyaltyEnrollmentButton != null) {
            updateLoyaltyEnrollmentButton.run();
        }
    }
    
    //
     // Initializes form from waitlist with room pre-selection.
//
    public static void initFromWaitlistWithRooms(
            com.hotel.model.Waitlist waitlist,
            Consumer<Boolean> setCreatingNewReservation,
            Consumer<com.hotel.model.Waitlist> setWaitlistToRemove,
            Consumer<Void> clearPendingRooms,
            Consumer<Void> clearRoomTableData,
            TextField guestNameField,
            TextField guestPhoneField,
            TextField guestEmailField,
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            com.hotel.service.ReservationService reservationService,
            java.util.List<com.hotel.model.Room> pendingRooms,
            Runnable updateRoomTypeSummary,
            VBox billingInformationContainer,
            HBox loyaltyContainer,
            ComboBox<String> statusComboBox,
            Label modeLabel,
            Runnable updateLoyaltyEnrollmentButton) {
        
        // Initialize basic form data
        initFromWaitlist(
            waitlist,
            setCreatingNewReservation,
            setWaitlistToRemove,
            clearPendingRooms,
            clearRoomTableData,
            guestNameField,
            guestPhoneField,
            guestEmailField,
            numAdultsField,
            numChildrenField,
            checkInDatePicker,
            checkOutDatePicker,
            updateLoyaltyEnrollmentButton
        );
        
        // Pre-select the requested room type
        // Get available rooms for the requested type
        List<com.hotel.model.Room> availableRooms = reservationService.getAvailableRooms(
            waitlist.getRequestedType(),
            waitlist.getDateRangeStart(),
            waitlist.getDateRangeEnd()
        );
        
        if (!availableRooms.isEmpty() && pendingRooms != null) {
            // Add first available room to pending rooms
            pendingRooms.add(availableRooms.get(0));
            if (updateRoomTypeSummary != null) {
                updateRoomTypeSummary.run();
            }
        }
        
        // Hide billing information section (will show after reservation is created)
        if (billingInformationContainer != null) {
            billingInformationContainer.setVisible(false);
            billingInformationContainer.setManaged(false);
        }
        
        // Hide loyalty container initially
        if (loyaltyContainer != null) {
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
        }
        
        if (statusComboBox != null) {
            statusComboBox.setValue("Pending");
            statusComboBox.setDisable(true);
        }
        
        if (modeLabel != null) {
            modeLabel.setVisible(true);
            modeLabel.setText("Create Mode");
        }
    }
}

