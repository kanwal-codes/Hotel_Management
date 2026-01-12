package com.hotel.controller.base;

import com.hotel.app.AppConfig;
import com.hotel.config.LoyaltyPolicy;
import com.hotel.model.Billing;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.service.BillingService;
import com.hotel.service.LoyaltyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Optional;

//
 // Base controller for payment processing functionality.
 // Provides shared logic for both admin and kiosk payment controllers.
//
 // This class reduces duplication between AdminPaymentController and KioskPaymentController
 // by centralizing common payment-related methods like balance refresh and loyalty points display.
//
public abstract class BasePaymentController extends BaseController {
    
    // Services - shared across payment controllers
    protected final BillingService billingService = AppConfig.createBillingService();
    protected final LoyaltyService loyaltyService = AppConfig.createLoyaltyService();
    protected final LoyaltyPolicy loyaltyPolicy = AppConfig.getLoyaltyPolicy();
    protected final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    
    // Current state - shared across payment controllers
    protected Reservation currentReservation;
    protected Billing currentBilling;
    
    // UI Components - may be null in some implementations
    @FXML protected Label currentBalanceLabel;
    @FXML protected Label amountToPayLabel;
    @FXML protected RadioButton pointsRadioButton;
    @FXML protected RadioButton cashRadioButton;
    @FXML protected RadioButton cardRadioButton;
    @FXML protected VBox loyaltyPointsInfoContainer;
    @FXML protected Label availablePointsLabel;
    @FXML protected Label pointsConversionLabel;
    @FXML protected Label pointsDeductionLabel;
    @FXML protected Label finalPriceAfterPointsLabel;
    @FXML protected Button processPaymentButton;
    
    //
     // Refreshes the balance labels to show current billing information.
     // This method is shared between AdminPaymentController and KioskPaymentController.
//
     // Note: AdminPaymentController calls updateDiscountDisplay() after this,
     // which is handled in the admin-specific override.
//
    protected void refreshBalanceLabels() {
        if (currentBilling != null) {
            // Reload billing to get latest information
            Optional<Billing> refreshedBilling = billingService.getBillingForReservation(currentReservation);
            refreshedBilling.ifPresent(b -> currentBilling = b);
            
            double balance = currentBilling.getBalanceAmount();
            if (currentBalanceLabel != null) {
                currentBalanceLabel.setText(currencyFormat.format(balance));
            }
            
            // If loyalty points are selected, show the balance after discount
            if (amountToPayLabel != null) {
                if (pointsRadioButton != null && pointsRadioButton.isSelected()) {
                    // Calculate what the balance will be after points discount
                    Guest guest = currentReservation != null ? currentReservation.getGuest() : null;
                    if (guest != null) {
                        int availablePoints = guest.getLoyaltyPoints();
                        int maxUsablePoints = Math.min(availablePoints, loyaltyPolicy.getMaxRedemptionPerReservation());
                        double discountAmount = loyaltyPolicy.calculateDiscountAmount(balance, maxUsablePoints);
                        double balanceAfterPoints = Math.max(0, balance - discountAmount);
                        amountToPayLabel.setText(currencyFormat.format(balanceAfterPoints));
                    } else {
                        amountToPayLabel.setText(currencyFormat.format(balance));
                    }
                } else {
                    amountToPayLabel.setText(currencyFormat.format(balance));
                }
            }
        }
    }
    
    //
     // Updates the loyalty points information display.
     // This method is shared between AdminPaymentController and KioskPaymentController,
     // with minor differences in display text handled through conditional logic.
//
    protected void updateLoyaltyPointsInfo() {
        // Enable process payment button by default
        if (processPaymentButton != null) {
            processPaymentButton.setDisable(false);
        }
        
        if (pointsRadioButton == null || !pointsRadioButton.isSelected()) {
            if (loyaltyPointsInfoContainer != null) {
                loyaltyPointsInfoContainer.setVisible(false);
                loyaltyPointsInfoContainer.setManaged(false);
            }
            // Enable button when other payment methods are selected (admin-specific)
            if (processPaymentButton != null && isAdminController()) {
                processPaymentButton.setDisable(false);
            }
            return;
        }
        
        // Show loyalty points info
        if (loyaltyPointsInfoContainer != null) {
            loyaltyPointsInfoContainer.setVisible(true);
            loyaltyPointsInfoContainer.setManaged(true);
        }
        
        if (currentBilling == null || currentReservation == null) {
            return;
        }
        
        Guest guest = currentReservation.getGuest();
        if (guest == null) {
            return;
        }
        
        double balanceAmount = currentBilling.getBalanceAmount();
        int availablePoints = guest.getLoyaltyPoints();
        
        // Check if points are zero or insufficient
        boolean hasNoPoints = availablePoints <= 0;
        int maxUsablePoints = Math.min(availablePoints, loyaltyPolicy.getMaxRedemptionPerReservation());
        boolean insufficientPoints = maxUsablePoints <= 0;
        
        // Update available points label
        if (availablePointsLabel != null) {
            String pointsText = "Available Points: " + availablePoints;
            availablePointsLabel.setText(pointsText);
            if (hasNoPoints) {
                availablePointsLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else {
                availablePointsLabel.setStyle(isAdminController() ? "" : "-fx-text-fill: #27ae60;");
            }
        }
        
        // Calculate discount from points
        double discountPercent = loyaltyPolicy.calculateDiscountFromPoints(maxUsablePoints);
        double discountAmount = loyaltyPolicy.calculateDiscountAmount(balanceAmount, maxUsablePoints);
        double finalAmount = Math.max(0, balanceAmount - discountAmount);
        
        // Points conversion label
        if (pointsConversionLabel != null) {
            if (isAdminController()) {
                pointsConversionLabel.setText("Conversion Rate: 100 points = 1% discount");
            } else {
                pointsConversionLabel.setText("Conversion Rate: " + 
                    (int)loyaltyPolicy.getPointsPerPercentDiscount() + " points = 1% discount");
            }
            if (hasNoPoints && isAdminController()) {
                pointsConversionLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else if (!isAdminController()) {
                pointsConversionLabel.setStyle("");
            }
        }
        
        // Points deduction label
        if (pointsDeductionLabel != null) {
            if (maxUsablePoints > 0) {
                if (isAdminController()) {
                    pointsDeductionLabel.setText("Points to Use: " + maxUsablePoints + " points (" + 
                        String.format("%.1f", discountPercent) + "% discount = " + 
                        currencyFormat.format(discountAmount) + " off)");
                } else {
                    pointsDeductionLabel.setText("Points to Deduct: " + maxUsablePoints);
                }
                pointsDeductionLabel.setStyle("");
            } else {
                if (isAdminController()) {
                    pointsDeductionLabel.setText("⚠ Insufficient points to apply discount - Cannot use loyalty points for payment");
                } else {
                    pointsDeductionLabel.setText("Points to Deduct: 0");
                }
                pointsDeductionLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
        
        // Final price after points label
        if (finalPriceAfterPointsLabel != null) {
            if (maxUsablePoints > 0 && discountAmount > 0) {
                if (isAdminController()) {
                    finalPriceAfterPointsLabel.setText("Final Amount After Points: " + currencyFormat.format(finalAmount));
                    finalPriceAfterPointsLabel.setStyle("");
                } else {
                    if (finalAmount == 0) {
                        finalPriceAfterPointsLabel.setText("Final Price: " + currencyFormat.format(0) + " (Fully Paid with Points)");
                        finalPriceAfterPointsLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        finalPriceAfterPointsLabel.setText("Final Price: " + currencyFormat.format(finalAmount));
                        finalPriceAfterPointsLabel.setStyle("-fx-text-fill: #2c3e50;");
                    }
                }
            } else {
                if (isAdminController()) {
                    finalPriceAfterPointsLabel.setText("⚠ Final Amount: " + currencyFormat.format(balanceAmount) + 
                        " (loyalty points cannot be used - please select another payment method)");
                } else {
                    double balance = currentBilling != null ? currentBilling.getBalanceAmount() : 0;
                    finalPriceAfterPointsLabel.setText("Final Price: " + currencyFormat.format(balance));
                }
                finalPriceAfterPointsLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
        
        // Disable process payment button if points are zero or insufficient
        if (processPaymentButton != null) {
            if (hasNoPoints || insufficientPoints) {
                processPaymentButton.setDisable(true);
            } else {
                processPaymentButton.setDisable(false);
            }
        }
        
        // Admin-specific: refresh balance labels to show updated amount after points discount
        if (isAdminController()) {
            refreshBalanceLabels();
        }
    }
    
    //
     // Determines if this is an admin controller or kiosk controller.
     // Used to handle minor differences in display logic.
//
     // @return true if this is an admin controller, false if kiosk
//
    protected abstract boolean isAdminController();
    
    //
     // Gets the selected payment method from the UI.
//
     // @return The selected PaymentMethod
//
    protected com.hotel.model.PaymentMethod getSelectedPaymentMethod() {
        if (cashRadioButton != null && cashRadioButton.isSelected()) {
            return com.hotel.model.PaymentMethod.CASH;
        } else if (cardRadioButton != null && cardRadioButton.isSelected()) {
            return com.hotel.model.PaymentMethod.CARD;
        } else if (pointsRadioButton != null && pointsRadioButton.isSelected()) {
            return com.hotel.model.PaymentMethod.POINTS;
        }
        return com.hotel.model.PaymentMethod.CARD; // Default
    }
    
    //
     // Template method for processing payment.
     // Each subclass must implement its own payment processing logic
     // as admin and kiosk have different flows.
//
    @FXML
    protected abstract void processPayment();
}

