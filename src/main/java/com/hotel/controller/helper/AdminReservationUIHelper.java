package com.hotel.controller.helper;

import com.hotel.model.Billing;
import com.hotel.model.Reservation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.NumberFormat;

//
 // Helper class for UI updates in AdminReservationController.
 // Handles display updates, form population, and UI state management.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminReservationUIHelper {
    
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    
    private AdminReservationUIHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Updates the reservation display fields with current reservation data.
//
    public static void updateReservationDisplay(
            Reservation currentReservation,
            Label reservationSummaryLabel,
            Label reservationIdLabel,
            Label modeLabel,
            TextField guestNameField,
            TextField guestPhoneField,
            TextField guestEmailField,
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            ComboBox<String> statusComboBox,
            Runnable updateGuestCountSummary) {
        
        if (currentReservation == null) return;

        if (reservationSummaryLabel != null) {
            reservationSummaryLabel.setText("Reservation #" + currentReservation.getId());
        }
        if (reservationIdLabel != null) {
            reservationIdLabel.setText("Reservation #" + currentReservation.getId());
        }
        if (modeLabel != null) {
            modeLabel.setVisible(true);
            modeLabel.setText("Edit Mode");
        }
        if (guestNameField != null && currentReservation.getGuest() != null) {
            guestNameField.setText(currentReservation.getGuest().getName());
        }
        if (guestPhoneField != null && currentReservation.getGuest() != null) {
            guestPhoneField.setText(currentReservation.getGuest().getPhone());
        }
        if (guestEmailField != null && currentReservation.getGuest() != null) {
            guestEmailField.setText(currentReservation.getGuest().getEmail());
        }
        if (numAdultsField != null) {
            numAdultsField.setText(String.valueOf(currentReservation.getNumAdults()));
        }
        if (numChildrenField != null) {
            numChildrenField.setText(String.valueOf(currentReservation.getNumChildren()));
        }
        if (updateGuestCountSummary != null) {
            updateGuestCountSummary.run();
        }

        if (checkInDatePicker != null) {
            checkInDatePicker.setValue(currentReservation.getCheckIn());
        }
        if (checkOutDatePicker != null) {
            checkOutDatePicker.setValue(currentReservation.getCheckOut());
        }
        if (statusComboBox != null && currentReservation.getStatus() != null) {
            statusComboBox.setDisable(false);
            statusComboBox.setValue(formatReservationStatus(currentReservation.getStatus().name()));
        }
    }
    
    //
     // Updates billing display labels with current billing information.
//
    public static void updateBillingDisplay(
            Billing currentBilling,
            Label subtotalDisplayLabel,
            Label taxDisplayLabel,
            Label discountDisplayLabel,
            Label totalDisplayLabel,
            Label paidAmountDisplayLabel,
            Label balanceDisplayLabel) {
        
        if (currentBilling == null) {
            if (subtotalDisplayLabel != null) subtotalDisplayLabel.setText("");
            if (taxDisplayLabel != null) taxDisplayLabel.setText("");
            if (discountDisplayLabel != null) discountDisplayLabel.setText("");
            if (totalDisplayLabel != null) totalDisplayLabel.setText("");
            if (paidAmountDisplayLabel != null) paidAmountDisplayLabel.setText("");
            if (balanceDisplayLabel != null) balanceDisplayLabel.setText("");
            return;
        }
        if (subtotalDisplayLabel != null) {
            subtotalDisplayLabel.setText(currencyFormat.format(currentBilling.getSubtotal()));
        }
        if (taxDisplayLabel != null) {
            taxDisplayLabel.setText(currencyFormat.format(currentBilling.getTaxAmount()));
        }
        if (discountDisplayLabel != null) {
            discountDisplayLabel.setText(currencyFormat.format(currentBilling.getDiscountValue()));
        }
        if (totalDisplayLabel != null) {
            totalDisplayLabel.setText(currencyFormat.format(currentBilling.getTotalAmount()));
        }
        if (paidAmountDisplayLabel != null) {
            paidAmountDisplayLabel.setText(currencyFormat.format(currentBilling.getPaidAmount()));
        }
        if (balanceDisplayLabel != null) {
            balanceDisplayLabel.setText(currencyFormat.format(currentBilling.getBalanceAmount()));
        }
    }
    
    //
     // Formats reservation status for display.
     // Converts enum name to display format: "CHECKED_IN" -> "Checked In"
//
    public static String formatReservationStatus(String status) {
        if (status == null) return "";
        // Convert enum name to display format matching ComboBox items
        // "CHECKED_IN" -> "Checked In", "CHECKED_OUT" -> "Checked Out", etc.
        String[] parts = status.replace("_", " ").toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(" ");
            result.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return result.toString();
    }
    
    //
     // Updates estimated billing display for new reservations.
//
    public static void updateEstimatedBillingDisplay(
            double subtotal,
            Label subtotalDisplayLabel,
            Label taxDisplayLabel,
            Label discountDisplayLabel,
            Label totalDisplayLabel,
            Label paidAmountDisplayLabel,
            Label balanceDisplayLabel) {
        
        // Calculate estimated billing for new reservations
        double taxRate = 0.10; // 10% tax
        double taxAmount = subtotal * taxRate;
        double totalAmount = subtotal + taxAmount;
        
        if (subtotalDisplayLabel != null) {
            subtotalDisplayLabel.setText(currencyFormat.format(subtotal));
        }
        if (taxDisplayLabel != null) {
            taxDisplayLabel.setText(currencyFormat.format(taxAmount));
        }
        if (discountDisplayLabel != null) {
            discountDisplayLabel.setText(currencyFormat.format(0.0));
        }
        if (totalDisplayLabel != null) {
            totalDisplayLabel.setText(currencyFormat.format(totalAmount));
        }
        if (paidAmountDisplayLabel != null) {
            paidAmountDisplayLabel.setText(currencyFormat.format(0.0));
        }
        if (balanceDisplayLabel != null) {
            balanceDisplayLabel.setText(currencyFormat.format(totalAmount));
        }
    }
    
    //
     // Shows payment breakdown after reservation changes.
//
    public static void showPaymentBreakdown(
            Billing currentBilling,
            double originalPaidAmount) {
        
        if (currentBilling == null) return;
        
        double newTotal = currentBilling.getTotalAmount();
        double newBalance = currentBilling.getBalanceAmount();
        
        StringBuilder message = new StringBuilder();
        message.append("Payment Breakdown After Changes:\n\n");
        message.append("Amount Paid Before: ").append(currencyFormat.format(originalPaidAmount)).append("\n");
        message.append("New Total Amount Required: ").append(currencyFormat.format(newTotal)).append("\n");
        message.append("Amount Left to Be Paid: ").append(currencyFormat.format(newBalance)).append("\n\n");
        
        if (newBalance > originalPaidAmount) {
            double additionalAmount = newBalance - originalPaidAmount;
            message.append("Additional payment needed: ").append(currencyFormat.format(additionalAmount));
        } else if (newBalance < originalPaidAmount) {
            double refundAmount = originalPaidAmount - newBalance;
            message.append("Refund amount: ").append(currencyFormat.format(refundAmount));
        } else {
            message.append("No change in payment required.");
        }
        
        com.hotel.controller.helper.AlertHelper.showInfo("Payment Breakdown", message.toString());
    }
    
    //
     // Updates guest count summary field.
//
    public static void updateGuestCountSummary(
            TextField numAdultsField,
            TextField numChildrenField,
            TextField numberOfGuestsField) {
        
        if (numberOfGuestsField == null) return;
        
        int adults = 0;
        int children = 0;
        
        try {
            if (numAdultsField != null && !numAdultsField.getText().isEmpty()) {
                adults = Integer.parseInt(numAdultsField.getText());
            }
            if (numChildrenField != null && !numChildrenField.getText().isEmpty()) {
                children = Integer.parseInt(numChildrenField.getText());
            }
        } catch (NumberFormatException e) {
            // Ignore - use defaults
        }
        
        numberOfGuestsField.setText(String.valueOf(adults + children));
    }
    
    //
     // Updates action buttons state based on reservation mode.
//
    public static void updateActionButtons(
            boolean hasReservation,
            Button saveReservationButton,
            Button processPaymentButton,
            Button checkoutButton,
            Button cancelReservationButton,
            Button deleteReservationButton,
            ComboBox<String> statusComboBox) {
        
        if (saveReservationButton != null) {
            saveReservationButton.setText(hasReservation ? "Save Changes" : "Create Reservation");
        }
        if (processPaymentButton != null) {
            processPaymentButton.setDisable(!hasReservation);
        }
        if (checkoutButton != null) {
            checkoutButton.setDisable(!hasReservation);
        }
        if (cancelReservationButton != null) {
            cancelReservationButton.setDisable(!hasReservation);
        }
        if (deleteReservationButton != null) {
            deleteReservationButton.setVisible(hasReservation);
        }
        // Always enable status ComboBox if we have a reservation
        if (statusComboBox != null) {
            statusComboBox.setDisable(!hasReservation);
            if (hasReservation) {
                statusComboBox.setDisable(false); // Explicitly enable it for editing
            }
        }
    }
    
    //
     // Updates room selection error label.
//
    public static void updateRoomSelectionError(
            String message,
            Label roomSelectionErrorLabel) {
        
        if (roomSelectionErrorLabel == null) return;
        
        if (message == null || message.isBlank()) {
            roomSelectionErrorLabel.setText("");
            roomSelectionErrorLabel.setVisible(false);
        } else {
            roomSelectionErrorLabel.setText(message);
            roomSelectionErrorLabel.setVisible(true);
        }
    }
    
    //
     // Updates service error label.
//
    public static void updateServiceError(
            String message,
            Label serviceErrorLabel) {
        
        if (serviceErrorLabel == null) return;
        
        if (message == null || message.isEmpty()) {
            serviceErrorLabel.setVisible(false);
            serviceErrorLabel.setText("");
        } else {
            serviceErrorLabel.setText(message);
            serviceErrorLabel.setVisible(true);
        }
    }
}

