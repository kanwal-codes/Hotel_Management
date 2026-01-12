package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BasePaymentController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.model.Billing;
import com.hotel.model.Guest;
import com.hotel.model.PaymentMethod;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import com.hotel.model.ReservationStatus;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import com.hotel.controller.FeedbackController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.util.Optional;

//
 // Controller for KioskPayment.fxml - Customer self-service payment.
 // Extends BasePaymentController to inherit shared payment processing logic.
//
public class KioskPaymentController extends BasePaymentController {

    // Kiosk-specific UI components
    @FXML private Label reservationSummaryLabel;

    // Services - kiosk-specific
    private final ReservationRepository reservationRepository = AppConfig.createReservationRepository();
    private final ReservationService reservationService = AppConfig.createReservationService();
    private final LoggerService logger = LoggerService.getInstance();

    // Note: currentReservation and currentBilling are inherited from BasePaymentController
    // Note: UI components (currentBalanceLabel, amountToPayLabel, etc.) are inherited from BasePaymentController
    private ToggleGroup paymentMethodGroup;
    private String returnScreen; // Screen to return to after payment

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

    public void initPaymentScreen(Reservation reservation, String returnScreen) {
        if (reservation == null) {
            logger.logError("initPaymentScreen called with null reservation", new IllegalArgumentException("Reservation cannot be null"));
            AlertHelper.showError("Error", "No reservation selected.");
            return;
        }
        
        this.currentReservation = reservation;
        this.returnScreen = returnScreen;
        this.currentBilling = billingService.getBillingForReservation(reservation).orElse(null);
        
        if (currentBilling == null) {
            logger.logError("No billing found for reservation #" + reservation.getId(), null);
            AlertHelper.showError("Error", "No billing found for this reservation.");
            return;
        }
        
        if (reservationSummaryLabel != null) {
            String confNumber = reservation.getConfirmationNumber() != null 
                ? reservation.getConfirmationNumber() 
                : "ID: " + reservation.getId();
            reservationSummaryLabel.setText("Reservation: " + confNumber + 
                " - Guest: " + (reservation.getGuest() != null ? reservation.getGuest().getName() : "N/A"));
        }
        
        if (processPaymentButton != null) {
            processPaymentButton.setDisable(false);
        }
        refreshBalanceLabels();
        updateLoyaltyPointsInfo();
    }

    @FXML
    private void onPaymentMethodChanged() {
        updateLoyaltyPointsInfo();
        refreshBalanceLabels();
    }
    
    //
     // Implements abstract method from BasePaymentController.
     // @return false since this is a kiosk controller
//
    @Override
    protected boolean isAdminController() {
        return false;
    }
    
    //
     // Processes payment for the reservation.
     // Implements abstract method from BasePaymentController.
//
    @Override
    @FXML
    protected void processPayment() {
        if (currentReservation == null || currentBilling == null) {
            AlertHelper.showError("Error", "No reservation or billing information available.");
            return;
        }
        
        PaymentMethod paymentMethod;
        if (cashRadioButton != null && cashRadioButton.isSelected()) {
            paymentMethod = PaymentMethod.CASH;
        } else if (cardRadioButton != null && cardRadioButton.isSelected()) {
            paymentMethod = PaymentMethod.CARD;
        } else if (pointsRadioButton != null && pointsRadioButton.isSelected()) {
            paymentMethod = PaymentMethod.POINTS;
        } else {
            AlertHelper.showError("Error", "Please select a payment method.");
            return;
        }
        
        double balance = currentBilling.getBalanceAmount();
        
        try {
            // Get billing object
            Optional<Billing> billingOpt = billingService.getBillingForReservation(currentReservation);
            if (billingOpt.isEmpty()) {
                AlertHelper.showError("Error", "No billing found for this reservation.");
                return;
            }
            
            Billing billing = billingOpt.get();
            
            // Get actor name (guest name or "Customer")
            String actor = "Customer";
            if (currentReservation.getGuest() != null && currentReservation.getGuest().getName() != null) {
                actor = currentReservation.getGuest().getName();
            }
            
            // Handle different payment methods
            if (paymentMethod == PaymentMethod.CASH) {
                // For cash, set status to PENDING and show message
                if (currentReservation.getStatus() != ReservationStatus.PENDING) {
                    currentReservation.setStatus(ReservationStatus.PENDING);
                    reservationRepository.save(currentReservation);
                }
                
                // Reload reservation to get latest data
                Optional<Reservation> refreshedReservation = reservationRepository.findById(currentReservation.getId());
                if (refreshedReservation.isPresent()) {
                    currentReservation = refreshedReservation.get();
                }
                
                // Get confirmation number or ID (booking ID)
                String bookingId = currentReservation.getConfirmationNumber() != null 
                    ? currentReservation.getConfirmationNumber() 
                    : String.valueOf(currentReservation.getId());
                
                // Navigate to confirmation screen first
                navigateToConfirmation();
                
                // Show info message after navigation (non-blocking)
                AlertHelper.showInfo("Reservation Created", 
                    "Your reservation has been created successfully!\n\n" +
                    "Booking ID: " + bookingId + "\n\n" +
                    "Please pay during check-in at the counter.\n" +
                    "Reservation status: PENDING");
                return;
            } else if (paymentMethod == PaymentMethod.POINTS) {
                // For loyalty points, deduct points and amount from balance (like admin portal)
                Guest guest = currentReservation.getGuest();
                if (guest == null || guest.getLoyaltyPoints() == 0) {
                    AlertHelper.showError("Error", "No loyalty points available.");
                    return;
                }
                
                int availablePoints = guest.getLoyaltyPoints();
                int maxUsablePoints = Math.min(availablePoints, loyaltyPolicy.getMaxRedemptionPerReservation());
                
                if (maxUsablePoints <= 0) {
                    AlertHelper.showError("Error", "Insufficient loyalty points");
                    return;
                }
                
                // Calculate discount amount from points
                double discountAmount = loyaltyPolicy.calculateDiscountAmount(balance, maxUsablePoints);
                int pointsToRedeem = maxUsablePoints;
                
                // Payment amount is the discount amount (points cover this much)
                double paymentAmount = discountAmount;
                
                // Redeem points from guest
                loyaltyService.redeemPoints(guest, pointsToRedeem);
                
                // Apply loyalty discount to billing
                billing.setLoyaltyRedeemedPoints(pointsToRedeem);
                // Recalculate billing with loyalty discount
                billingService.recalculateTotal(billing);
                
                // Process payment for the calculated amount
                billingService.processPayment(billing, paymentMethod, paymentAmount, actor);
                
                // Reload reservation to get updated status
                Optional<Reservation> refreshedReservation = reservationRepository.findById(currentReservation.getId());
                if (refreshedReservation.isPresent()) {
                    currentReservation = refreshedReservation.get();
                }
                
                // Reload billing to get updated balance
                Optional<Billing> updatedBilling = billingService.getBillingForReservation(currentReservation);
                if (updatedBilling.isPresent()) {
                    currentBilling = updatedBilling.get();
                }
                
                String paymentMessage = "Loyalty Points Applied Successfully\n" +
                    pointsToRedeem + " loyalty points redeemed\n" +
                    "Discount applied: " + currencyFormat.format(discountAmount);
                
                double remainingBalance = currentBilling.getBalanceAmount();
                if (remainingBalance > 0) {
                    paymentMessage += "\nRemaining Balance: " + currencyFormat.format(remainingBalance);
                    paymentMessage += "\n\nPlease select a payment method for the remaining balance.";
                    
                    AlertHelper.showInfo("Points Applied", paymentMessage);
                    
                    // Refresh the payment screen to show updated balance
                    refreshBalanceLabels();
                    updateLoyaltyPointsInfo();
                    // Stay on payment page so user can pay remaining balance
                    return;
                } else {
                    // Fully paid - ensure reservation status is CONFIRMED
                    if (currentReservation.getStatus() == ReservationStatus.PENDING) {
                        currentReservation.setStatus(ReservationStatus.CONFIRMED);
                        currentReservation = reservationRepository.save(currentReservation);
                    }
                    
                    // Navigate to confirmation screen immediately after payment
                    navigateToConfirmation();
                    
                    // Show success message after navigation (non-blocking)
                    paymentMessage += "\n\nBooking Confirmed!";
                    AlertHelper.showInfo("Payment Successful", paymentMessage);
                    return;
                }
            } else {
                // For card payment, process normally
                billingService.processPayment(billing, paymentMethod, balance, actor);
                
                // Reload reservation to get updated status (should be CONFIRMED if fully paid)
                Optional<Reservation> refreshedReservation = reservationRepository.findById(currentReservation.getId());
                if (refreshedReservation.isPresent()) {
                    currentReservation = refreshedReservation.get();
                }
                
                // Ensure reservation status is CONFIRMED if fully paid
                if (currentBilling.getBalanceAmount() <= 0 && currentReservation.getStatus() == ReservationStatus.PENDING) {
                    currentReservation.setStatus(ReservationStatus.CONFIRMED);
                    currentReservation = reservationRepository.save(currentReservation);
                }
                
                // Navigate to confirmation screen immediately after payment
                navigateToConfirmation();
                
                // Show success message after navigation (non-blocking)
                AlertHelper.showInfo("Payment Successful", 
                    "Payment of " + currencyFormat.format(balance) + " processed successfully.\n\n" +
                    "Booking Confirmed!");
                return;
            }
            
        } catch (Exception e) {
            logger.logError("Failed to process payment", e);
            AlertHelper.showError("Payment Error", "Failed to process payment: " + e.getMessage());
        }
    }
    
    private void navigateToConfirmation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/ConfirmationScreen.fxml"));
            Parent root = loader.load();
            KioskController controller = loader.getController();
            
            // Set the reservation
            controller.createdReservation = this.currentReservation;
            
            // Also set guest and dates for display
            if (this.currentReservation != null && this.currentReservation.getGuest() != null) {
                controller.currentGuest = this.currentReservation.getGuest();
            }
            
            // Try to get check-in/check-out from reservation
            if (this.currentReservation != null) {
                controller.checkIn = this.currentReservation.getCheckIn();
                controller.checkOut = this.currentReservation.getCheckOut();
            }
            
            // Explicitly call loadConfirmation to display the reservation ID
            controller.loadConfirmation();
            
            Stage stage = getCurrentStage();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            logger.logError("Failed to navigate to confirmation", e);
            AlertHelper.showError("Navigation Error", "Failed to navigate: " + e.getMessage());
        }
    }
    
    private void navigateToFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/feedback/FeedbackSubmission.fxml"));
            Parent root = loader.load();
            FeedbackController controller = loader.getController();
            controller.setReservation(currentReservation.getId());
            
            Stage stage = getCurrentStage();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            logger.logError("Failed to navigate to feedback", e);
            AlertHelper.showError("Navigation Error", "Failed to navigate: " + e.getMessage());
        }
    }
    
    @FXML
    private void showRules() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Rules and Regulations");
        alert.setHeaderText("Hotel Booking Policy");
        alert.setContentText("Please review our booking policies:\n\n" +
            "• Check-in time: 3:00 PM\n" +
            "• Check-out time: 11:00 AM\n" +
            "• Cancellation: 24 hours notice required\n" +
            "• Occupancy limits: Single/Deluxe/Penthouse: 2 people, Double: 4 people\n" +
            "• Billing will be handled at the front desk");
        alert.showAndWait();
    }
    
    @FXML
    @Override
    protected void goBack() {
        try {
            if (returnScreen != null) {
                // Navigate to the return screen
                Parent root = FXMLLoader.load(getClass().getResource(returnScreen));
                Stage stage = getCurrentStage();
                stage.setScene(new Scene(root, 1200, 800));
            } else {
                // Default: go back to welcome screen
                Parent root = FXMLLoader.load(getClass().getResource("/view/kiosk/KioskWelcome.fxml"));
                Stage stage = getCurrentStage();
                stage.setScene(new Scene(root, 1200, 800));
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate back", e);
            AlertHelper.showError("Navigation Error", "Failed to navigate: " + e.getMessage());
        }
    }
    
    //
     // Gets the current stage for navigation.
     // Overrides base method to use kiosk-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try kiosk-specific fields first
        if (processPaymentButton != null && processPaymentButton.getScene() != null) {
            return getCurrentStageFromNode(processPaymentButton);
        }
        if (reservationSummaryLabel != null && reservationSummaryLabel.getScene() != null) {
            return getCurrentStageFromNode(reservationSummaryLabel);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}


