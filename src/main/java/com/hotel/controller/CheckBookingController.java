package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.NavigationHelper;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.BillingService;
import com.hotel.service.ReservationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.util.Optional;

//
 // Controller for checking existing bookings.
 // Extends BaseController to inherit common functionality.
//
public class CheckBookingController extends BaseController {
    
    @FXML private TextField reservationNumberField;
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private Label errorLabel;
    @FXML private VBox inputFormContainer;
    @FXML private VBox bookingDetailsContainer;
    @FXML private Label reservationNumberLabel;
    @FXML private Label guestNameLabel;
    @FXML private Label checkInLabel;
    @FXML private Label checkOutLabel;
    @FXML private Label statusLabel;
    @FXML private Label roomsLabel;
    @FXML private Label totalLabel;
    @FXML private Button cancelButton;
    @FXML private Button checkInButton;
    @FXML private Button checkOutButton;
    @FXML private Button payNowButton;
    
    private EntityManager entityManager;
    private ReservationRepository reservationRepository;
    private ReservationService reservationService;
    private Reservation currentReservation;
    
    @FXML
    public void initialize() {
        entityManager = AppConfig.createEntityManager();
        reservationRepository = new ReservationRepository(entityManager);
        reservationService = AppConfig.createReservationService();
    }
    
    @FXML
    private void handleViewBooking(ActionEvent event) {
        hideError();
        
        // Get input values
        String reservationNumber = reservationNumberField != null ? reservationNumberField.getText().trim() : "";
        String email = emailField != null ? emailField.getText().trim() : "";
        String name = nameField != null ? nameField.getText().trim() : "";
        
        // Validate: Reservation number is mandatory
        if (reservationNumber.isEmpty()) {
            showError("Reservation number is required.");
            return;
        }
        
        // Validate: Either email or name must be provided
        if (email.isEmpty() && name.isEmpty()) {
            showError("Please provide either email or name for verification.");
            return;
        }
        
        try {
            // Find reservation by number
            Optional<Reservation> resOpt = reservationRepository.findByIdOrConfirmationNumber(reservationNumber);
            
            if (!resOpt.isPresent()) {
                showError("No booking found with that reservation number.");
                hideBookingDetails();
                return;
            }
            
            Reservation reservation = resOpt.get();
            
            // Verify email or name matches
            boolean verified = false;
            String verificationError = "";
            
            if (reservation.getGuest() == null) {
                showError("Reservation found but guest information is missing. Please contact support.");
                hideBookingDetails();
                return;
            }
            
            // Check email match (case-insensitive)
            if (!email.isEmpty()) {
                String guestEmail = reservation.getGuest().getEmail();
                if (guestEmail != null && guestEmail.equalsIgnoreCase(email)) {
                    verified = true;
                } else {
                    verificationError = "Email does not match this reservation.";
                }
            }
            
            // Check name match (case-insensitive, partial match allowed)
            if (!name.isEmpty() && !verified) {
                String guestName = reservation.getGuest().getName();
                if (guestName != null && guestName.equalsIgnoreCase(name)) {
                    verified = true;
                } else if (verificationError.isEmpty()) {
                    verificationError = "Name does not match this reservation.";
                }
            }
            
            // If both email and name provided, both must match
            if (!email.isEmpty() && !name.isEmpty()) {
                String guestEmail = reservation.getGuest().getEmail();
                String guestName = reservation.getGuest().getName();
                boolean emailMatches = guestEmail != null && guestEmail.equalsIgnoreCase(email);
                boolean nameMatches = guestName != null && guestName.equalsIgnoreCase(name);
                
                if (!emailMatches || !nameMatches) {
                    verified = false;
                    verificationError = "Email or name does not match this reservation.";
                } else {
                    verified = true;
                }
            }
            
            if (verified) {
                currentReservation = reservation;
                displayBookingDetails();
            } else {
                showError(verificationError.isEmpty() ? "Verification failed. Please check your information and try again." : verificationError);
                hideBookingDetails();
            }
            
        } catch (Exception e) {
            showError("Error looking up booking: " + e.getMessage());
            hideBookingDetails();
        }
    }
    
    private void displayBookingDetails() {
        if (currentReservation == null) return;
        
        String confNumber = currentReservation.getConfirmationNumber() != null 
            ? currentReservation.getConfirmationNumber() 
            : "ID: " + currentReservation.getId();
        
        reservationNumberLabel.setText("Reservation: " + confNumber);
        guestNameLabel.setText("Guest: " + currentReservation.getGuest().getName());
        checkInLabel.setText("Check-in: " + currentReservation.getCheckIn());
        checkOutLabel.setText("Check-out: " + currentReservation.getCheckOut());
        statusLabel.setText("Status: " + currentReservation.getStatus());
        
        String rooms = currentReservation.getReservationRooms().stream()
            .map(rr -> rr.getRoom().getRoomNumber())
            .reduce((a, b) -> a + ", " + b)
            .orElse("No rooms assigned");
        roomsLabel.setText("Rooms: " + rooms);
        
        // Calculate total if billing exists
        if (currentReservation.getBilling() != null) {
            totalLabel.setText("Total: $" + String.format("%.2f", currentReservation.getBilling().getTotalAmount()));
        } else {
            totalLabel.setText("Total: Pending");
        }
        
        // Show cancel button only if status allows
        cancelButton.setVisible(
            currentReservation.getStatus() == ReservationStatus.PENDING ||
            currentReservation.getStatus() == ReservationStatus.CONFIRMED
        );
        
        // Show check-in button if status is CONFIRMED
        checkInButton.setVisible(
            currentReservation.getStatus() == ReservationStatus.CONFIRMED
        );
        
        // Show check-out button if status is CHECKED_IN
        checkOutButton.setVisible(
            currentReservation.getStatus() == ReservationStatus.CHECKED_IN
        );
        
        // Show pay now button if there's a balance to pay
        if (payNowButton != null && currentReservation.getBilling() != null) {
            BillingService billingService = AppConfig.createBillingService();
            double balance = billingService.getBillingForReservation(currentReservation)
                .map(b -> b.getBalanceAmount())
                .orElse(0.0);
            payNowButton.setVisible(balance > 0);
        }
        
        // Hide input form and show booking details
        if (inputFormContainer != null) {
            inputFormContainer.setVisible(false);
            inputFormContainer.setManaged(false);
        }
        bookingDetailsContainer.setVisible(true);
        bookingDetailsContainer.setManaged(true);
    }
    
    private void hideBookingDetails() {
        // Show input form and hide booking details
        if (inputFormContainer != null) {
            inputFormContainer.setVisible(true);
            inputFormContainer.setManaged(true);
        }
        bookingDetailsContainer.setVisible(false);
        bookingDetailsContainer.setManaged(false);
    }
    
    @FXML
    private void handleNewSearch(ActionEvent event) {
        // Clear fields and show input form again
        if (reservationNumberField != null) reservationNumberField.clear();
        if (emailField != null) emailField.clear();
        if (nameField != null) nameField.clear();
        hideError();
        hideBookingDetails();
        currentReservation = null;
    }
    
    @FXML
    private void handlePrintReceipt(ActionEvent event) {
        if (currentReservation == null) {
            showError("No booking selected.");
            return;
        }
        // TODO: Implement print receipt functionality
        showError("Print receipt functionality coming soon.");
    }
    
    @FXML
    private void handleCancelBooking(ActionEvent event) {
        if (currentReservation == null) {
            showError("No booking selected.");
            return;
        }
        
        // Check if booking can be cancelled
        if (currentReservation.getStatus() != ReservationStatus.PENDING && 
            currentReservation.getStatus() != ReservationStatus.CONFIRMED) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Cancel");
            alert.setHeaderText("This booking cannot be cancelled");
            alert.setContentText("Only pending or confirmed bookings can be cancelled.");
            alert.showAndWait();
            return;
        }
        
        // Confirm cancellation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Booking");
        confirm.setHeaderText("Are you sure you want to cancel this booking?");
        confirm.setContentText("Reservation: " + 
            (currentReservation.getConfirmationNumber() != null ? 
                currentReservation.getConfirmationNumber() : "ID: " + currentReservation.getId()) +
            "\nThis action cannot be undone.");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                reservationService.cancelReservation(currentReservation.getId());
                
                // Refresh the display
                currentReservation = reservationRepository.findById(currentReservation.getId()).orElse(null);
                if (currentReservation != null) {
                    displayBookingDetails();
                }
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Booking Cancelled");
                success.setHeaderText("Booking cancelled successfully");
                success.setContentText("Your booking has been cancelled.");
                success.showAndWait();
            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Cancellation Error");
                error.setHeaderText("Failed to cancel booking");
                error.setContentText("Error: " + e.getMessage());
                error.showAndWait();
            }
        }
    }
    
    @FXML
    private void handleCheckIn(ActionEvent event) {
        if (currentReservation == null) {
            showError("No booking selected.");
            return;
        }
        
        // Check if reservation can be checked in
        if (currentReservation.getStatus() != ReservationStatus.CONFIRMED) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Check In");
            alert.setHeaderText("Reservation must be confirmed before check-in");
            alert.setContentText("Current status: " + currentReservation.getStatus());
            alert.showAndWait();
            return;
        }
        
        try {
            reservationService.checkInReservation(currentReservation.getId());
            
            // Refresh the display
            currentReservation = reservationRepository.findById(currentReservation.getId()).orElse(null);
            if (currentReservation != null) {
                displayBookingDetails();
            }
            
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Check-In Successful");
            success.setHeaderText("Guest checked in successfully");
            success.setContentText("Reservation status updated to CHECKED_IN");
            success.showAndWait();
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Check-In Error");
            error.setHeaderText("Failed to check in");
            error.setContentText("Error: " + e.getMessage());
            error.showAndWait();
        }
    }
    
    @FXML
    private void handlePayNow(ActionEvent event) {
        if (currentReservation == null) {
            showError("No booking selected.");
            return;
        }
        
        try {
            // Navigate to payment screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/KioskPayment.fxml"));
            Parent root = loader.load();
            KioskPaymentController controller = loader.getController();
            controller.initPaymentScreen(currentReservation, "/view/kiosk/CheckBooking.fxml");
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Payment Error");
            error.setHeaderText("Failed to load payment screen");
            error.setContentText("Error: " + e.getMessage());
            error.showAndWait();
        }
    }
    
    @FXML
    private void handleCheckOut(ActionEvent event) {
        if (currentReservation == null) {
            showError("No booking selected.");
            return;
        }
        
        // Check if reservation can be checked out
        if (currentReservation.getStatus() != ReservationStatus.CHECKED_IN) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Check Out");
            alert.setHeaderText("Reservation must be checked in before check-out");
            alert.setContentText("Current status: " + currentReservation.getStatus());
            alert.showAndWait();
            return;
        }
        
        // Check if balance is paid
        BillingService billingService = AppConfig.createBillingService();
        if (!billingService.canCheckout(currentReservation)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Check Out");
            alert.setHeaderText("Outstanding balance must be paid");
            alert.setContentText("Please process payment before check-out.");
            alert.showAndWait();
            return;
        }
        
        try {
            reservationService.checkoutReservation(currentReservation.getId());
            
            // Refresh the display
            currentReservation = reservationRepository.findById(currentReservation.getId()).orElse(null);
            if (currentReservation != null) {
                displayBookingDetails();
            }
            
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Check-Out Successful");
            success.setHeaderText("Guest checked out successfully");
            success.setContentText("Reservation status updated to CHECKED_OUT. Rooms are now available.");
            success.showAndWait();
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Check-Out Error");
            error.setHeaderText("Failed to check out");
            error.setContentText("Error: " + e.getMessage());
            error.showAndWait();
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        navigate(event, "/view/kiosk/KioskWelcome.fxml");
    }
    
    //
     // Shows error message using base class method.
//
    private void showError(String message) {
        showError(errorLabel, message);
    }
    
    //
     // Hides error message using base class method.
//
    private void hideError() {
        hideError(errorLabel);
    }
    
    //
     // Navigates to a screen using NavigationHelper.
//
    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Node source = (Node) event.getSource();
            NavigationHelper.navigateFromNode(source, fxmlPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlPath, e);
        }
    }
    
    @FXML
    private void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules and Regulations");
        alert.setHeaderText("Hotel Booking Policy");
        alert.setContentText("Please review our booking policies:\n\n" +
            "• Check-in time: 3:00 PM\n" +
            "• Check-out time: 11:00 AM\n" +
            "• Cancellation: 24 hours notice required\n" +
            "• Occupancy limits: Single/Deluxe/Penthouse: 2 people, Double: 4 people\n" +
            "• Billing will be handled at the front desk\n" +
            "• Smoking is strictly prohibited inside the rooms\n" +
            "• Pets are not allowed\n" +
            "• Quiet hours are from 10:00 PM to 7:00 AM\n" +
            "• Any damage to hotel property will be charged to the guest");
        alert.showAndWait();
    }
}


