package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BasePaymentController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Billing;
import com.hotel.model.Guest;
import com.hotel.model.PaymentMethod;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.util.LoggerService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.Optional;

//
 // Controller for processing payments on reservations.
 // Allows admins to record payments (cash, card, or points) and update billing.
 // Extends BasePaymentController to inherit shared payment processing logic.
//
public class AdminPaymentController extends BasePaymentController {

    // Admin-specific UI components
    @FXML private Label reservationSummaryLabel;
    @FXML private Label discountLabel;
    @FXML private Button applyDiscountButton;

    // Services - admin-specific
    private final ReservationRepository reservationRepository = AppConfig.createReservationRepository();
    private final GuestRepository guestRepository = AppConfig.createGuestRepository();
    private final LoggerService logger = LoggerService.getInstance();

    private AdminUser currentUser;
    // Note: currentReservation and currentBilling are inherited from BasePaymentController
    private ToggleGroup paymentMethodGroup;

    @FXML
    private void initialize() {
        paymentMethodGroup = new ToggleGroup();
        if (cashRadioButton != null) {
            cashRadioButton.setToggleGroup(paymentMethodGroup);
            cashRadioButton.setSelected(true);
        }
        if (cardRadioButton != null) {
            cardRadioButton.setToggleGroup(paymentMethodGroup);
        }
        if (pointsRadioButton != null) {
            pointsRadioButton.setToggleGroup(paymentMethodGroup);
        }
    }

    public void initPaymentScreen(AdminUser user, Reservation reservation) {
        if (user == null) {
            logger.logError("initPaymentScreen called with null user", new IllegalArgumentException("User cannot be null"));
            AlertHelper.showError("Error", "User not logged in. Please log in again.");
            return;
        }
        
        if (reservation == null) {
            logger.logError("initPaymentScreen called with null reservation", new IllegalArgumentException("Reservation cannot be null"));
            AlertHelper.showError("Error", "No reservation selected.");
            return;
        }
        
        this.currentUser = user;
        this.currentReservation = reservation;
        this.currentBilling = billingService.getBillingForReservation(reservation).orElse(null);
        
        if (currentBilling == null) {
            logger.logWarning("No billing found for reservation #" + reservation.getId() + ". Creating billing...");
            // Try to create billing if it doesn't exist
            try {
                // Reload reservation to ensure we have the latest data
                Optional<Reservation> refreshedReservation = reservationRepository.findById(reservation.getId());
                if (refreshedReservation.isPresent()) {
                    currentReservation = refreshedReservation.get();
                    currentBilling = billingService.getBillingForReservation(currentReservation).orElse(null);
                    
                    if (currentBilling == null) {
                        logger.logError("Could not create or find billing for reservation #" + reservation.getId(), null);
                        AlertHelper.showError("Error", "No billing found for this reservation. Please ensure the reservation has been saved with billing information.");
                        return;
                    }
                }
            } catch (Exception e) {
                logger.logError("Failed to refresh reservation or create billing", e);
                AlertHelper.showError("Error", "Failed to load billing information: " + e.getMessage());
                return;
            }
        }
        
        if (reservationSummaryLabel != null) {
            reservationSummaryLabel.setText("Reservation #" + reservation.getId() + 
                " - Guest: " + (reservation.getGuest() != null ? reservation.getGuest().getName() : "N/A"));
        }
        // Initialize button state
        if (processPaymentButton != null) {
            processPaymentButton.setDisable(false);
        }
        refreshBalanceLabels();
        updateDiscountDisplay();
        updateLoyaltyPointsInfo();
    }

    //
     // Override to add admin-specific discount display update.
//
    @Override
    protected void refreshBalanceLabels() {
        super.refreshBalanceLabels();
        // Admin-specific: update discount display after refreshing balance
        updateDiscountDisplay();
    }
    
    private void updateDiscountDisplay() {
        if (currentBilling != null && discountLabel != null) {
            double discountValue = currentBilling.getDiscountValue();
            if (discountValue > 0) {
                discountLabel.setText(currencyFormat.format(discountValue) + " discount applied");
                discountLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                discountLabel.setText("No discount applied");
                discountLabel.setStyle("-fx-text-fill: #7f8c8d;");
            }
        }
    }
    
    @FXML
    private void onPaymentMethodChanged() {
        updateLoyaltyPointsInfo();
    }
    
    //
     // Implements abstract method from BasePaymentController.
     // @return true since this is an admin controller
//
    @Override
    protected boolean isAdminController() {
        return true;
    }

    //
     // Processes payment for the reservation.
     // Implements abstract method from BasePaymentController.
//
    @Override
    @FXML
    protected void processPayment() {
        if (currentUser == null) {
            logger.logError("processPayment called but user is null", new IllegalStateException("User not logged in"));
            AlertHelper.showError("Error", "User not logged in. Please log in again.");
            return;
        }
        
        if (currentReservation == null) {
            logger.logError("processPayment called but reservation is null", new IllegalStateException("No reservation selected"));
            AlertHelper.showError("Error", "No reservation selected. Please select a reservation first.");
            return;
        }
        
        if (currentBilling == null) {
            logger.logError("processPayment called but billing is null for reservation #" + currentReservation.getId(), 
                new IllegalStateException("No billing found"));
            // Try to reload billing
            try {
                Optional<Reservation> refreshedReservation = reservationRepository.findById(currentReservation.getId());
                if (refreshedReservation.isPresent()) {
                    currentReservation = refreshedReservation.get();
                    currentBilling = billingService.getBillingForReservation(currentReservation).orElse(null);
                }
            } catch (Exception e) {
                logger.logError("Failed to reload reservation/billing", e);
            }
            
            if (currentBilling == null) {
                AlertHelper.showError("Error", 
                    "No billing found for this reservation. Please ensure the reservation has been saved with billing information.");
                return;
            }
        }

        try {
            PaymentMethod method = super.getSelectedPaymentMethod();
            double balanceBeforePayment = currentBilling.getBalanceAmount();
            
            if (balanceBeforePayment <= 0) {
                AlertHelper.showError("Error", "No balance to pay");
                return;
            }
            
            double paymentAmount = balanceBeforePayment;
            int pointsToRedeem = 0;
            double discountAmount = 0.0;
            
            // For points payment, calculate discount and redeem points
            if (method == PaymentMethod.POINTS) {
                Guest guest = currentReservation.getGuest();
                if (guest == null || guest.getLoyaltyPoints() <= 0) {
                    AlertHelper.showError("Error", "Guest has no loyalty points available");
                    return;
                }
                
                int availablePoints = guest.getLoyaltyPoints();
                int maxUsablePoints = Math.min(availablePoints, loyaltyPolicy.getMaxRedemptionPerReservation());
                
                if (maxUsablePoints <= 0) {
                    AlertHelper.showError("Error", "Insufficient loyalty points");
                    return;
                }
                
                // Calculate discount amount from points
                discountAmount = loyaltyPolicy.calculateDiscountAmount(balanceBeforePayment, maxUsablePoints);
                pointsToRedeem = maxUsablePoints;
                
                // Payment amount is the discount amount (points cover this much)
                paymentAmount = discountAmount;
                
                // Redeem points from guest
                loyaltyService.redeemPoints(guest, pointsToRedeem);
                
                // Apply loyalty discount to billing
                currentBilling.setLoyaltyRedeemedPoints(pointsToRedeem);
                // Recalculate billing with loyalty discount (this saves the billing)
                billingService.recalculateTotal(currentBilling);
            }

            // Process payment for the calculated amount
            billingService.processPayment(currentBilling, method, paymentAmount, currentUser.getUsername());
            
            // Refresh billing and reservation to get updated status
            Optional<Billing> updated = billingService.getBillingForReservation(currentReservation);
            updated.ifPresent(b -> currentBilling = b);
            
            // Refresh reservation to get updated status
            currentReservation = reservationRepository.findById(currentReservation.getId()).orElse(currentReservation);
            
            // Refresh guest to get updated points
            if (currentReservation.getGuest() != null && method == PaymentMethod.POINTS) {
                // Reload guest to get updated points after redemption
                Long guestId = currentReservation.getGuest().getId();
                guestRepository.findById(guestId).ifPresent(guest -> {
                    currentReservation.setGuest(guest);
                });
                // Update loyalty points info to reflect new point balance (which should be zero or reduced)
                updateLoyaltyPointsInfo();
            }
            
            refreshBalanceLabels();
            // Update loyalty points info if points payment was used (to show updated state)
            if (method == PaymentMethod.POINTS) {
                updateLoyaltyPointsInfo();
            }
            
            String methodText = method == PaymentMethod.POINTS ? "Loyalty Points" : method.toString();
            String statusMessage = "";
            if (currentReservation.getStatus() == ReservationStatus.CONFIRMED) {
                statusMessage = "\nReservation status updated to CONFIRMED.";
            }
            
            String paymentMessage = "Payment of " + currencyFormat.format(paymentAmount) + 
                " via " + methodText + " processed successfully";
            
            if (method == PaymentMethod.POINTS) {
                paymentMessage += "\n" + pointsToRedeem + " loyalty points redeemed";
                paymentMessage += "\nDiscount applied: " + currencyFormat.format(discountAmount);
                double remainingBalance = currentBilling.getBalanceAmount();
                paymentMessage += "\nUpdated Balance: " + currencyFormat.format(remainingBalance);
            }
            
            AlertHelper.showInfo("Success", paymentMessage + statusMessage);
        } catch (Exception e) {
            logger.logError("Failed to process payment", e);
            AlertHelper.showError("Error", "Failed to process payment: " + e.getMessage());
        }
    }

    @FXML
    private void openDiscountDialog() {
        if (currentBilling == null || currentUser == null || currentReservation == null) {
            AlertHelper.showError("Error", "No billing available or user not logged in.");
            return;
        }
        
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/DiscountApplication.fxml", controller -> {
                if (controller instanceof AdminDiscountController discountController) {
                    discountController.initDiscountScreen(currentUser, currentReservation, currentBilling);
                    // Set return screen to payment processing
                    discountController.setReturnScreen("/view/admin/PaymentProcessing.fxml", paymentController -> {
                        if (paymentController instanceof AdminPaymentController) {
                            ((AdminPaymentController) paymentController).initPaymentScreen(currentUser, currentReservation);
                        }
                    });
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to open discount dialog", e);
            AlertHelper.showError("Error", "Failed to open discount screen: " + e.getMessage());
        }
    }
    
    @FXML
    @Override
    protected void goBack() {
        navigateToReservationDetails();
    }

    private void navigateToReservationDetails() {
        try {
            if (currentReservation == null || currentReservation.getId() == null) {
                logger.logError("Cannot navigate back: currentReservation is null or has no ID", new IllegalStateException("No reservation to navigate to"));
                AlertHelper.showError("Error", "No reservation selected. Returning to dashboard.");
                // Navigate to dashboard instead
                Stage stage = getCurrentStage();
                AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", controller -> {
                    if (controller instanceof AdminDashboardController dashboardController && currentUser != null) {
                        dashboardController.init(currentUser);
                    }
                });
                return;
            }
            if (currentUser == null) {
                logger.logError("Cannot navigate back: currentUser is null", new IllegalStateException("User not logged in"));
                AlertHelper.showError("Error", "User session expired. Returning to dashboard.");
                Stage stage = getCurrentStage();
                AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", null);
                return;
            }
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/ReservationDetails.fxml", controller -> {
                if (controller instanceof AdminReservationController reservationController) {
                    reservationController.initForExisting(currentUser, currentReservation.getId());
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back to reservation", e);
            AlertHelper.showError("Navigation Error", "Failed to load reservation screen: " + e.getMessage());
        }
    }

    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (reservationSummaryLabel != null && reservationSummaryLabel.getScene() != null) {
            return getCurrentStageFromNode(reservationSummaryLabel);
        }
        if (currentBalanceLabel != null && currentBalanceLabel.getScene() != null) {
            return getCurrentStageFromNode(currentBalanceLabel);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}

