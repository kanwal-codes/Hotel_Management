package com.hotel.controller.helper;

import com.hotel.app.AppConfig;
import com.hotel.config.LoyaltyPolicy;
import com.hotel.model.*;
import com.hotel.service.PricingService;
import com.hotel.service.decorator.*;
import com.hotel.util.LoggerService;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

//
 // Helper class for booking summary screen logic in KioskController.
 // Extracts booking summary calculation and display logic.
//
public final class KioskBookingSummaryHelper {
    
    private KioskBookingSummaryHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Calculates booking summary totals (rooms, addons, tax, total).
//
    public static BookingSummaryCalculation calculateBookingSummary(
            List<Room> selectedRooms,
            List<ServiceAddon> selectedAddons,
            LocalDate checkIn,
            LocalDate checkOut,
            PricingService pricingService,
            LoggerService logger) {
        
        BookingSummaryCalculation calc = new BookingSummaryCalculation();
        
        if (selectedRooms == null || selectedRooms.isEmpty() || 
            checkIn == null || checkOut == null) {
            return calc;
        }
        
        long numNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        // Room subtotal
        double roomSubtotal = 0.0;
        for (Room room : selectedRooms) {
            double roomPrice = pricingService.calculateRoomPrice(room, checkIn, checkOut);
            roomSubtotal += roomPrice;
        }
        
        // Addon subtotal using Decorator Pattern
        double addonSubtotal = 0.0;
        if (selectedAddons != null && !selectedAddons.isEmpty()) {
            // Create base component from first room (or combine all rooms)
            BookingComponent baseComponent = null;
            for (Room room : selectedRooms) {
                RoomBookingComponent roomComponent = new RoomBookingComponent(room, checkIn, checkOut, pricingService);
                if (baseComponent == null) {
                    baseComponent = roomComponent;
                } else {
                    // Combine multiple rooms by creating a wrapper
                    baseComponent = new CombinedBookingComponent(baseComponent, roomComponent);
                }
            }
            
            // Apply decorators for each addon
            BookingComponent decoratedComponent = baseComponent;
            for (ServiceAddon addon : selectedAddons) {
                decoratedComponent = new AddOnDecorator(decoratedComponent, addon, (int) numNights);
            }
            
            // Calculate total with decorators
            double totalWithDecorators = decoratedComponent.getPrice();
            double roomSubtotalFromDecorator = 0.0;
            for (Room room : selectedRooms) {
                roomSubtotalFromDecorator += pricingService.calculateRoomPrice(room, checkIn, checkOut);
            }
            addonSubtotal = totalWithDecorators - roomSubtotalFromDecorator;
        }
        
        // Tax (assuming 10%)
        double taxRate = 0.10;
        double subtotal = roomSubtotal + addonSubtotal;
        double taxAmount = subtotal * taxRate;
        double total = subtotal + taxAmount;
        
        calc.roomSubtotal = roomSubtotal;
        calc.addonSubtotal = addonSubtotal;
        calc.subtotal = subtotal;
        calc.taxRate = taxRate;
        calc.taxAmount = taxAmount;
        calc.total = total;
        calc.numNights = numNights;
        
        logger.logInfo("Booking summary calculated - Room Subtotal: $" + String.format("%.2f", roomSubtotal) +
            ", Addon Subtotal: $" + String.format("%.2f", addonSubtotal) +
            ", Tax: $" + String.format("%.2f", taxAmount) +
            ", Total: $" + String.format("%.2f", total));
        
        return calc;
    }
    
    //
     // Logs booking state for debugging.
//
    public static void logBookingState(
            Guest currentGuest,
            List<Room> selectedRooms,
            List<ServiceAddon> selectedAddons,
            LocalDate checkIn,
            LocalDate checkOut,
            int numAdults,
            int numChildren,
            LoggerService logger) {
        
        logger.logInfo("=== loadBookingSummary() called ===");
        logger.logInfo("currentGuest: " + (currentGuest != null ? currentGuest.getName() : "null"));
        logger.logInfo("selectedRooms size: " + (selectedRooms != null ? selectedRooms.size() : "null"));
        logger.logInfo("selectedAddons size: " + (selectedAddons != null ? selectedAddons.size() : "null"));
        logger.logInfo("checkIn: " + checkIn);
        logger.logInfo("checkOut: " + checkOut);
        logger.logInfo("numAdults: " + numAdults + ", numChildren: " + numChildren);
        
        // Log selected addons details
        if (selectedAddons != null && !selectedAddons.isEmpty()) {
            logger.logInfo("Selected addons:");
            for (ServiceAddon addon : selectedAddons) {
                logger.logInfo("  - " + addon.getName() + " ($" + addon.getPrice() + ", " + addon.getPricingModel() + ")");
            }
        } else {
            logger.logWarning("selectedAddons is null or empty");
        }
        
        // Log selected rooms details
        if (selectedRooms != null && !selectedRooms.isEmpty()) {
            logger.logInfo("Selected rooms:");
            for (Room room : selectedRooms) {
                logger.logInfo("  - Room " + room.getRoomNumber() + " (" + room.getType() + ", $" + room.getBasePrice() + ")");
            }
        } else {
            logger.logWarning("selectedRooms is null or empty");
        }
    }
    
    //
     // Updates assigned rooms label in booking summary.
//
    public static void updateAssignedRoomsLabelInSummary(
            Reservation createdReservation,
            List<Room> selectedRooms,
            Label assignedRoomsLabel) {
        
        if (assignedRoomsLabel == null) {
            return;
        }
        
        if (createdReservation != null && createdReservation.getReservationRooms() != null && !createdReservation.getReservationRooms().isEmpty()) {
            // Show assigned rooms from the created reservation
            KioskUIHelper.updateAssignedRoomsLabel(createdReservation, assignedRoomsLabel);
        } else if (selectedRooms != null && !selectedRooms.isEmpty()) {
            // Update assigned rooms label from selected rooms (before reservation is created)
            KioskUIHelper.updateAssignedRoomsLabelFromSelected(selectedRooms, assignedRoomsLabel);
        } else {
            assignedRoomsLabel.setText("N/A");
        }
    }
    
    //
     // Updates booking summary UI labels.
//
    public static void updateBookingSummaryUI(
            BookingSummaryCalculation calc,
            Guest currentGuest,
            LocalDate checkIn,
            LocalDate checkOut,
            int numAdults,
            int numChildren,
            Label guestNameLabel,
            Label guestPhoneLabel,
            Label guestEmailLabel,
            Label checkInLabel,
            Label checkOutLabel,
            Label numNightsLabel,
            Label occupancyLabel,
            Label roomSubtotalLabel,
            Label addOnSubtotalLabel,
            Label taxRateLabel,
            Label taxAmountLabel,
            Label totalAmountLabel,
            LoggerService logger) {
        
        if (calc == null || currentGuest == null) {
            return;
        }
        
        // Guest info
        if (guestNameLabel != null) guestNameLabel.setText(currentGuest.getName());
        if (guestPhoneLabel != null) guestPhoneLabel.setText(currentGuest.getPhone());
        if (guestEmailLabel != null) guestEmailLabel.setText(currentGuest.getEmail());
        
        // Dates and occupancy
        if (checkInLabel != null && checkIn != null) checkInLabel.setText(checkIn.toString());
        if (checkOutLabel != null && checkOut != null) checkOutLabel.setText(checkOut.toString());
        if (numNightsLabel != null) numNightsLabel.setText(calc.numNights + " night(s)");
        if (occupancyLabel != null) {
            occupancyLabel.setText(numAdults + " adults" + 
                (numChildren > 0 ? ", " + numChildren + " children" : ""));
        }
        
        // Pricing
        if (roomSubtotalLabel != null) {
            roomSubtotalLabel.setText("$" + String.format("%.2f", calc.roomSubtotal));
        }
        if (addOnSubtotalLabel != null) {
            addOnSubtotalLabel.setText("$" + String.format("%.2f", calc.addonSubtotal));
            logger.logInfo("Updated addOnSubtotalLabel to: $" + String.format("%.2f", calc.addonSubtotal));
        }
        if (taxRateLabel != null) {
            taxRateLabel.setText((calc.taxRate * 100) + "%");
        }
        if (taxAmountLabel != null) {
            taxAmountLabel.setText("$" + String.format("%.2f", calc.taxAmount));
        }
        if (totalAmountLabel != null) {
            totalAmountLabel.setText("$" + String.format("%.2f", calc.total));
        }
    }
    
    //
     // Displays room breakdown in a VBox.
//
    public static void displayRoomBreakdown(
            List<Room> selectedRooms,
            LocalDate checkIn,
            LocalDate checkOut,
            PricingService pricingService,
            VBox roomBreakdownList,
            VBox roomBreakdownContainer) {
        
        if (roomBreakdownList == null || selectedRooms == null || selectedRooms.isEmpty()) {
            if (roomBreakdownContainer != null) {
                roomBreakdownContainer.setVisible(false);
                roomBreakdownContainer.setManaged(false);
            }
            return;
        }
        
        roomBreakdownList.getChildren().clear();
        
        for (Room room : selectedRooms) {
            double roomPrice = pricingService.calculateRoomPrice(room, checkIn, checkOut);
            Label roomLabel = new Label(String.format("• %s - Room #%s: $%.2f", 
                room.getType().toString(), room.getRoomNumber(), roomPrice));
            roomLabel.getStyleClass().add("body-text");
            roomBreakdownList.getChildren().add(roomLabel);
        }
        
        if (roomBreakdownContainer != null) {
            roomBreakdownContainer.setVisible(true);
            roomBreakdownContainer.setManaged(true);
        }
    }
    
    //
     // Displays add-on breakdown in a VBox.
//
    public static void displayAddOnBreakdown(
            List<ServiceAddon> selectedAddons,
            LocalDate checkIn,
            LocalDate checkOut,
            VBox addonBreakdownList,
            VBox addonBreakdownContainer) {
        
        if (addonBreakdownList == null || selectedAddons == null || selectedAddons.isEmpty()) {
            if (addonBreakdownContainer != null) {
                addonBreakdownContainer.setVisible(false);
                addonBreakdownContainer.setManaged(false);
            }
            return;
        }
        
        addonBreakdownList.getChildren().clear();
        
        long numNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        for (ServiceAddon addon : selectedAddons) {
            double addonPrice = 0.0;
            String pricingInfo = "";
            
            if (addon.getPricingModel() == PricingModel.PER_NIGHT) {
                addonPrice = addon.getPrice() * numNights;
                pricingInfo = String.format("$%.2f × %d nights", addon.getPrice(), numNights);
            } else {
                addonPrice = addon.getPrice();
                pricingInfo = "One-time charge";
            }
            
            Label addonLabel = new Label(String.format("• %s (%s): $%.2f", 
                addon.getName(), pricingInfo, addonPrice));
            addonLabel.getStyleClass().add("body-text");
            addonBreakdownList.getChildren().add(addonLabel);
        }
        
        if (addonBreakdownContainer != null) {
            addonBreakdownContainer.setVisible(true);
            addonBreakdownContainer.setManaged(true);
        }
    }
    
    //
     // Calculates and displays loyalty effects.
//
    public static void calculateAndDisplayLoyaltyEffects(
            Guest currentGuest,
            double subtotal,
            double taxAmount,
            double total,
            VBox loyaltyContainer,
            Label loyaltyLabel,
            LoggerService logger) {
        
        if (loyaltyContainer == null || loyaltyLabel == null || currentGuest == null) {
            if (loyaltyContainer != null) {
                loyaltyContainer.setVisible(false);
                loyaltyContainer.setManaged(false);
            }
            return;
        }
        
        // Check if guest has loyalty account
        if (currentGuest.getLoyaltyNumber() == null || currentGuest.getLoyaltyNumber().isEmpty()) {
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
            return;
        }
        
        int availablePoints = currentGuest.getLoyaltyPoints();
        if (availablePoints <= 0) {
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
            return;
        }
        
        // Calculate potential discount from available points
        LoyaltyPolicy loyaltyPolicy = AppConfig.getLoyaltyPolicy();
        if (loyaltyPolicy == null) {
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
            return;
        }
        int maxRedeemable = loyaltyPolicy.getMaxRedemptionPerReservation();
        int pointsToUse = Math.min(availablePoints, maxRedeemable);
        
        double potentialDiscount = loyaltyPolicy.calculateDiscountAmount(subtotal, pointsToUse);
        double discountedTotal = total - potentialDiscount;
        
        // Display loyalty information
        loyaltyLabel.setText(String.format(
            "Available Points: %d | Potential Discount: $%.2f | New Total: $%.2f",
            availablePoints, potentialDiscount, discountedTotal
        ));
        loyaltyContainer.setVisible(true);
        loyaltyContainer.setManaged(true);
        
        logger.logInfo("Loyalty effects calculated - Points: " + availablePoints + 
            ", Discount: $" + String.format("%.2f", potentialDiscount));
    }
    
    //
     // Calculates billing subtotal for reservation creation.
//
    public static double calculateBillingSubtotal(
            List<Room> selectedRooms,
            List<ServiceAddon> selectedAddons,
            LocalDate checkIn,
            LocalDate checkOut,
            PricingService pricingService) {
        
        if (selectedRooms == null || selectedRooms.isEmpty() || 
            checkIn == null || checkOut == null) {
            return 0.0;
        }
        
        long numNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        // Room subtotal
        double roomSubtotal = 0.0;
        for (Room room : selectedRooms) {
            roomSubtotal += pricingService.calculateRoomPrice(room, checkIn, checkOut);
        }
        
        // Addon subtotal using Decorator Pattern
        double addonSubtotal = 0.0;
        if (selectedAddons != null && !selectedAddons.isEmpty()) {
            BookingComponent baseComponent = null;
            for (Room room : selectedRooms) {
                RoomBookingComponent roomComponent = new RoomBookingComponent(room, checkIn, checkOut, pricingService);
                if (baseComponent == null) {
                    baseComponent = roomComponent;
                } else {
                    baseComponent = new CombinedBookingComponent(baseComponent, roomComponent);
                }
            }
            
            BookingComponent decoratedComponent = baseComponent;
            for (ServiceAddon addon : selectedAddons) {
                decoratedComponent = new AddOnDecorator(decoratedComponent, addon, (int) numNights);
            }
            
            double totalWithDecorators = decoratedComponent.getPrice();
            addonSubtotal = totalWithDecorators - roomSubtotal;
        }
        
        return roomSubtotal + addonSubtotal;
    }
    
    // ========== Inner Classes for Return Values ==========
    
    //
     // Result of booking summary calculation.
//
    public static class BookingSummaryCalculation {
        public double roomSubtotal = 0.0;
        public double addonSubtotal = 0.0;
        public double subtotal = 0.0;
        public double taxRate = 0.10;
        public double taxAmount = 0.0;
        public double total = 0.0;
        public long numNights = 0;
    }
}

