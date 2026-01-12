package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Billing;
import com.hotel.model.Reservation;
import com.hotel.service.BillingService;
import com.hotel.util.LoggerService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Optional;

//
 // Controller for applying discounts to reservations.
 // Validates discount amounts based on admin role (Admin: 15% max, Manager: 30% max).
 // Updates billing with discount and recalculates totals.
//
public class AdminDiscountController extends BaseController {

    @FXML private TextField discountField;
    @FXML private TextField originalPriceField;
    @FXML private TextField discountedPriceField;
    @FXML private Label maxDiscountLabel;

    private final BillingService billingService = AppConfig.createBillingService();
    private final LoggerService logger = LoggerService.getInstance();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private AdminUser currentUser;
    private Reservation currentReservation;
    private Billing currentBilling;

    public void initDiscountScreen(AdminUser user, Reservation reservation, Billing billing) {
        this.currentUser = user;
        this.currentReservation = reservation;
        this.currentBilling = billing;
        if (originalPriceField != null && currentBilling != null) {
            originalPriceField.setText(currencyFormat.format(currentBilling.getTotalAmount()));
        }
        
        // Display max discount based on role
        if (maxDiscountLabel != null && currentUser != null) {
            double maxDiscount = currentUser.getRole() == com.hotel.model.Role.ADMIN ? 15.0 : 30.0;
            maxDiscountLabel.setText("Maximum discount allowed: " + maxDiscount + "% (based on your role: " + currentUser.getRole() + ")");
        }
        
        calculateDiscountAmount();
    }

    @FXML
    private void applyDiscount() {
        if (currentBilling == null || currentUser == null) {
            AlertHelper.showError("Error", "No billing selected or user not logged in");
            return;
        }
        double discountPercent;
        try {
            discountPercent = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException e) {
            AlertHelper.showError("Error", "Invalid discount percentage");
            return;
        }
        if (discountPercent <= 0) {
            AlertHelper.showError("Error", "Discount must be positive");
            return;
        }
        
        // Check role-based discount limit before applying
        double maxDiscount = currentUser.getRole() == com.hotel.model.Role.ADMIN ? 15.0 : 30.0;
        if (discountPercent > maxDiscount) {
            AlertHelper.showError("Discount Limit Exceeded", 
                "Your role (" + currentUser.getRole() + ") can only apply discounts up to " + maxDiscount + "%.\n" +
                "You attempted to apply: " + discountPercent + "%");
            return;
        }
        
        try {
            currentBilling = billingService.applyDiscount(currentBilling, discountPercent, currentUser);
            Optional<Billing> updatedBilling = billingService.getBillingForReservation(currentReservation);
            updatedBilling.ifPresent(b -> currentBilling = b);
            AlertHelper.showInfo("Success", "Discount of " + discountPercent + "% applied successfully");
            calculateDiscountAmount();
        } catch (Exception e) {
            logger.logError("Failed to apply discount", e);
            AlertHelper.showError("Error", "Failed to apply discount: " + e.getMessage());
        }
    }

    @FXML
    private void calculateDiscountAmount() {
        if (discountField == null || originalPriceField == null || discountedPriceField == null) {
            return;
        }
        if (currentBilling == null) {
            if (originalPriceField != null) originalPriceField.setText("");
            if (discountedPriceField != null) discountedPriceField.setText("");
            return;
        }
        try {
            String discountText = discountField.getText();
            if (discountText == null || discountText.trim().isEmpty()) {
                if (discountedPriceField != null) {
                    discountedPriceField.setText(currencyFormat.format(currentBilling.getTotalAmount()));
                }
                return;
            }
            double discountPercent = Double.parseDouble(discountText);
            if (discountPercent < 0 || discountPercent > 100) {
                return;
            }
            double originalTotal = currentBilling.getSubtotal() + currentBilling.getTaxAmount();
            double discountAmount = originalTotal * (discountPercent / 100.0);
            double discountedPrice = originalTotal - discountAmount;
            discountedPriceField.setText(currencyFormat.format(discountedPrice));
        } catch (NumberFormatException ignore) {
            // ignore invalid preview input
        }
    }

    private String returnToScreen = "/view/admin/ReservationDetails.fxml";
    private java.util.function.Consumer<Object> returnCallback = null;
    
    public void setReturnScreen(String fxmlPath, java.util.function.Consumer<Object> callback) {
        this.returnToScreen = fxmlPath;
        this.returnCallback = callback;
    }
    
    @FXML
    @Override
    protected void goBack() {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, returnToScreen, returnCallback != null ? returnCallback : controller -> {
                if (returnToScreen.contains("ReservationDetails") && controller instanceof AdminReservationController reservationController) {
                    reservationController.initForExisting(currentUser, currentReservation.getId());
                } else if (returnToScreen.contains("PaymentProcessing") && controller instanceof AdminPaymentController paymentController) {
                    paymentController.initPaymentScreen(currentUser, currentReservation);
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back", e);
            AlertHelper.showError("Navigation Error", "Failed to load screen: " + e.getMessage());
        }
    }

    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (discountField != null && discountField.getScene() != null) {
            return getCurrentStageFromNode(discountField);
        }
        if (originalPriceField != null && originalPriceField.getScene() != null) {
            return getCurrentStageFromNode(originalPriceField);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}

