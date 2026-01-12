package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.model.Feedback;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.FeedbackService;
import com.hotel.util.LoggerService;
import com.hotel.util.Validator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.util.Optional;

//
 // Controller for guest feedback submission.
 // Allows guests to submit ratings and comments after checkout.
 // Validates that reservation is eligible for feedback (checked out and paid).
//
public class FeedbackController extends BaseController {
    
    private FeedbackService feedbackService;
    private GuestRepository guestRepository;
    private ReservationRepository reservationRepository;
    private LoggerService logger;
    private EntityManager em;
    
    // Current reservation and guest (set when navigating to feedback screen)
    private Reservation currentReservation;
    private Guest currentGuest;
    
    // ========== FeedbackSubmission.fxml ==========
    @FXML private Label reservationInfoLabel;
    private ToggleGroup ratingGroup;
    @FXML private RadioButton rating1;
    @FXML private RadioButton rating2;
    @FXML private RadioButton rating3;
    @FXML private RadioButton rating4;
    @FXML private RadioButton rating5;
    @FXML private Label ratingErrorLabel;
    @FXML private TextArea commentsField;
    @FXML private Label charCountLabel;
    @FXML private Label commentsErrorLabel;
    
    // ========== FeedbackConfirmation.fxml ==========
    @FXML private Button anotherButton;
    
    @FXML
    private void initialize() {
        // Initialize services
        em = AppConfig.createEntityManager();
        feedbackService = AppConfig.createFeedbackService();
        guestRepository = AppConfig.createGuestRepository();
        reservationRepository = AppConfig.createReservationRepository();
        logger = LoggerService.getInstance();
        
        // Initialize ToggleGroup for rating radio buttons
        if (ratingGroup == null) {
            ratingGroup = new ToggleGroup();
        }
        if (rating1 != null) rating1.setToggleGroup(ratingGroup);
        if (rating2 != null) rating2.setToggleGroup(ratingGroup);
        if (rating3 != null) rating3.setToggleGroup(ratingGroup);
        if (rating4 != null) rating4.setToggleGroup(ratingGroup);
        if (rating5 != null) rating5.setToggleGroup(ratingGroup);
        
        // Initialize character count
        if (commentsField != null && charCountLabel != null) {
            commentsField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateCharCount();
            });
        }
        
        // Hide error labels initially
        if (ratingErrorLabel != null) ratingErrorLabel.setVisible(false);
        if (commentsErrorLabel != null) commentsErrorLabel.setVisible(false);
    }
    
    //
     // Set the reservation for feedback submission
     // Called when navigating to feedback screen
//
    public void setReservation(Long reservationId) {
        try {
            Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
            if (resOpt.isPresent()) {
                currentReservation = resOpt.get();
                currentGuest = currentReservation.getGuest();
                
                // Update reservation info label
                if (reservationInfoLabel != null) {
                    reservationInfoLabel.setText("Reservation #" + reservationId + " - " + currentGuest.getName());
                }
                
                // Check eligibility
                if (!feedbackService.canSubmitFeedback(currentReservation)) {
                    showError("Feedback can only be submitted after checkout and when balance is settled.");
                }
            }
        } catch (Exception e) {
            logger.logError("Failed to load reservation for feedback", e);
            showError("Failed to load reservation information.");
        }
    }
    
    // ========== Feedback Submission Methods ==========
    
    @FXML
    private void updateCharCount() {
        if (commentsField != null && charCountLabel != null) {
            int count = commentsField.getText() != null ? commentsField.getText().length() : 0;
            charCountLabel.setText(count + " / 1000 characters");
            
            if (count > 1000) {
                charCountLabel.setStyle("-fx-text-fill: red;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: black;");
            }
        }
    }
    
    @FXML
    private void handleBack() {
        // Navigate back to kiosk welcome
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/KioskWelcome.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (commentsField != null ? commentsField.getScene().getWindow() : 
                (rating1 != null ? rating1.getScene().getWindow() : null));
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate back", e);
        }
    }
    
    @FXML
    private void skipFeedback() {
        // Navigate back or to main menu
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/WelcomeScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (commentsField != null ? commentsField.getScene().getWindow() : 
                (rating1 != null ? rating1.getScene().getWindow() : null));
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate", e);
        }
    }
    
    @FXML
    private void submitFeedback() {
        // Clear previous errors
        if (ratingErrorLabel != null) ratingErrorLabel.setVisible(false);
        if (commentsErrorLabel != null) commentsErrorLabel.setVisible(false);
        
        // Validate rating
        int rating = getSelectedRating();
        if (rating == 0) {
            if (ratingErrorLabel != null) {
                ratingErrorLabel.setText("Please select a rating (1-5 stars)");
                ratingErrorLabel.setVisible(true);
            }
            return;
        }
        
        // Validate comment length
        String comments = commentsField != null ? commentsField.getText() : "";
        if (comments != null && comments.length() > 1000) {
            if (commentsErrorLabel != null) {
                commentsErrorLabel.setText("Comments must be 1000 characters or less");
                commentsErrorLabel.setVisible(true);
            }
            return;
        }
        
        // Validate eligibility
        if (currentReservation == null || currentGuest == null) {
            showError("Reservation information not available.");
            return;
        }
        
        if (!feedbackService.canSubmitFeedback(currentReservation)) {
            showError("Feedback can only be submitted after checkout and when balance is settled.");
            return;
        }
        
        try {
            // Submit feedback
            Feedback feedback = feedbackService.submitFeedback(currentGuest, currentReservation, rating, comments);
            
            // Navigate to confirmation screen
            navigateToConfirmation();
            
        } catch (Exception e) {
            logger.logError("Failed to submit feedback", e);
            showError("Failed to submit feedback: " + e.getMessage());
        }
    }
    
    private int getSelectedRating() {
        if (ratingGroup == null) return 0;
        
        RadioButton selected = (RadioButton) ratingGroup.getSelectedToggle();
        if (selected == null) return 0;
        
        if (selected == rating1) return 1;
        if (selected == rating2) return 2;
        if (selected == rating3) return 3;
        if (selected == rating4) return 4;
        if (selected == rating5) return 5;
        
        return 0;
    }
    
    private void navigateToConfirmation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/feedback/FeedbackConfirmation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) (commentsField != null ? commentsField.getScene().getWindow() : 
                (rating1 != null ? rating1.getScene().getWindow() : null));
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to confirmation", e);
        }
    }
    
    //
     // Shows error message using base class method.
     // Tries ratingErrorLabel first, then commentsErrorLabel.
//
    private void showError(String message) {
        if (ratingErrorLabel != null) {
            showError(ratingErrorLabel, message);
        } else if (commentsErrorLabel != null) {
            showError(commentsErrorLabel, message);
        }
    }
    
    // ========== Feedback Confirmation Methods ==========
    
    @FXML
    private void submitAnother() {
        // Navigate back to feedback submission (if needed)
        skipFeedback();
    }
    
    @FXML
    private void done() {
        // Navigate to admin dashboard
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = null;
            if (anotherButton != null && anotherButton.getScene() != null) {
                stage = (Stage) anotherButton.getScene().getWindow();
            } else if (commentsField != null && commentsField.getScene() != null) {
                stage = (Stage) commentsField.getScene().getWindow();
            } else if (rating1 != null && rating1.getScene() != null) {
                stage = (Stage) rating1.getScene().getWindow();
            }
            
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            } else {
                // Fallback: create new stage
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 1200, 800));
                newStage.setTitle("Admin Dashboard");
                newStage.show();
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to admin dashboard", e);
            // Fallback to welcome screen if dashboard fails
            skipFeedback();
        }
    }
}
