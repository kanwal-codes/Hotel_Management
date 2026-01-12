package com.hotel.controller.helper;

import com.hotel.controller.KioskController;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.ServiceAddon;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;

//
 // Service for kiosk navigation operations.
 // Extracted from KioskController to reduce controller size.
//
public final class KioskNavigationService {
    
    private KioskNavigationService() {
        // Utility class - prevent instantiation
    }
    
    //
     // Navigates to a screen with state transfer and history tracking.
//
    public static void navigateToScreen(
            String fxmlPath,
            ActionEvent event,
            Supplier<String> determineCurrentScreen,
            Runnable captureCurrentStateFromUI,
            Supplier<KioskStateHelper.BookingState> createStateSnapshot,
            Consumer<KioskStateHelper.BookingState> applyStateToFields,
            Runnable populateFieldsFromState,
            Consumer<String> handlePostNavigationLoading,
            LoggerService logger,
            Supplier<Stage> getCurrentStage,
            Consumer<Stage> setScene) {
        
        try {
            // IMPORTANT: Capture current state from UI fields before navigation
            captureCurrentStateFromUI.run();
            
            // Update navigation history before navigating
            String currentScreen = determineCurrentScreen.get();
            
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource(fxmlPath));
            Parent root = loader.load();
            Object controllerObj = loader.getController();
            
            // Only transfer state if controller is KioskController
            if (controllerObj instanceof KioskController) {
                KioskController controller = (KioskController) controllerObj;
                
                // Create state snapshot and transfer
                KioskStateHelper.BookingState state = createStateSnapshot.get();
                
                // Add current screen to history
                if (currentScreen != null && state.navigationHistory != null) {
                    state.navigationHistory.push(currentScreen);
                }
                
                // Apply state to new controller
                applyStateToFields.accept(state);
                
                logger.logInfo("State transferred - numAdults: " + state.numAdults + 
                    ", numChildren: " + state.numChildren + 
                    ", selectedRooms: " + (state.selectedRooms != null ? state.selectedRooms.size() : 0) + 
                    ", selectedAddons: " + (state.selectedAddons != null ? state.selectedAddons.size() : 0) +
                    ", guest: " + (state.currentGuest != null ? state.currentGuest.getName() : "null") +
                    ", checkIn: " + state.checkIn + ", checkOut: " + state.checkOut);

                controller.populateFieldsFromState();
                
                // CRITICAL: Re-apply state after populateFieldsFromState() in case initialize() reset it
                // This ensures state is preserved even if initialize() was called after state transfer
                applyStateToFields.accept(state);
                logger.logInfo("State re-applied after populateFieldsFromState() to ensure preservation");
                
                // Load data AFTER state transfer using helper
                handlePostNavigationLoading.accept(fxmlPath);
            } else {
                // For non-KioskController screens, just navigate without state transfer
                logger.logInfo("Navigating to screen with different controller type: " + fxmlPath);
            }
            
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                setScene.accept(stage);
            } else {
                logger.logError("Could not get current stage for navigation", null);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to load screen");
                alert.setContentText("Could not navigate to the next screen. Please try again.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to " + fxmlPath, e);
            // Show error to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load screen");
            alert.setContentText("Could not navigate to the next screen: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    //
     // Navigates to a screen without adding to history (for back navigation).
//
    public static void navigateToScreenWithoutHistory(
            String fxmlPath,
            Runnable captureCurrentStateFromUI,
            Supplier<KioskStateHelper.BookingState> createStateSnapshot,
            Consumer<KioskStateHelper.BookingState> applyStateToFields,
            Runnable populateFieldsFromState,
            Consumer<String> handlePostNavigationLoading,
            LoggerService logger,
            Supplier<Stage> getCurrentStage,
            Consumer<Stage> setScene) {
        
        try {
            // IMPORTANT: Capture current state from UI fields before navigation
            captureCurrentStateFromUI.run();
            
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource(fxmlPath));
            Parent root = loader.load();
            Object controllerObj = loader.getController();
            
            // Only transfer state if controller is KioskController
            if (controllerObj instanceof KioskController) {
                KioskController controller = (KioskController) controllerObj;
                // Create state snapshot and transfer (without adding to history - going back)
                KioskStateHelper.BookingState state = createStateSnapshot.get();
                
                // Apply state to new controller
                applyStateToFields.accept(state);
                
                controller.populateFieldsFromState();
                
                // CRITICAL: Re-apply state after populateFieldsFromState() in case initialize() reset it
                applyStateToFields.accept(state);
                
                // Load data AFTER state transfer using helper
                handlePostNavigationLoading.accept(fxmlPath);
            } else {
                // For non-KioskController screens, just navigate without state transfer
                logger.logInfo("Navigating back to screen with different controller type: " + fxmlPath);
            }
            
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                setScene.accept(stage);
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate back to " + fxmlPath, e);
        }
    }
    
    //
     // Navigates to KioskWelcome screen.
//
    public static void navigateToKioskWelcome(
            LoggerService logger,
            Supplier<Stage> getCurrentStage,
            Consumer<Stage> setScene) {
        
        try {
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource("/view/kiosk/KioskWelcome.fxml"));
            Parent root = loader.load();
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                setScene.accept(stage);
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to KioskWelcome", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load screen");
            alert.setContentText("Could not navigate to kiosk welcome: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    //
     // Navigates to room selection screen with state transfer.
//
    public static void navigateToRoomSelection(
            int numAdults,
            int numChildren,
            LocalDate checkIn,
            LocalDate checkOut,
            Guest currentGuest,
            List<Room> selectedRooms,
            List<ServiceAddon> selectedAddons,
            int singleRoomCount,
            int doubleRoomCount,
            int deluxeRoomCount,
            int penthouseRoomCount,
            boolean customSelectionActive,
            Stack<String> navigationHistory,
            ReservationService reservationService,
            LoggerService logger,
            Supplier<Stage> getCurrentStage,
            Consumer<KioskController> transferStateToController,
            Supplier<String> determineCurrentScreen) {
        
        try {
            // Validate state before navigation
            if (!validateBeforeRoomSelection(checkIn, checkOut, numAdults, reservationService, logger)) {
                return;
            }
            
            // Get room suggestions
            List<ReservationService.RoomSuggestion> suggestions = 
                reservationService.suggestRooms(numAdults, numChildren, checkIn, checkOut);
            
            if (suggestions == null) {
                logger.logError("suggestRooms() returned null");
                suggestions = new java.util.ArrayList<>();
            }
            
            logger.logInfo("Got " + suggestions.size() + " room suggestions");
            
            // Navigate to room selection
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource("/view/kiosk/RoomSelection.fxml"));
            Parent root = loader.load();
            KioskController controller = loader.getController();
            
            if (controller == null) {
                logger.logError("Controller is null after loading RoomSelection.fxml");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to initialize controller");
                alert.setContentText("The room selection screen could not be loaded. Please try again.");
                alert.showAndWait();
                return;
            }
            
            // Transfer state to new controller (includes navigation history)
            transferStateToController.accept(controller);
            controller.loadRoomSuggestions(suggestions);
            controller.loadAvailableRooms(); // Load available rooms and update spinner limits
            controller.refreshCustomSelectionUI();
            controller.updateSelectedRoomsSummary();
            
            // Add current screen to navigation history (history already transferred in lambda)
            String currentScreen = determineCurrentScreen.get();
            if (currentScreen != null) {
                controller.addToNavigationHistory(currentScreen);
            }
            
            logger.logInfo("RoomSelection state transferred - selectedRooms: " + (selectedRooms != null ? selectedRooms.size() : 0));
            
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            } else {
                logger.logError("Could not get current stage for navigation");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to get window");
                alert.setContentText("Could not navigate to room selection. Please try again.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to room selection", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load room selection");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    //
     // Validates state before navigating to room selection.
//
    private static boolean validateBeforeRoomSelection(
            LocalDate checkIn,
            LocalDate checkOut,
            int numAdults,
            ReservationService reservationService,
            LoggerService logger) {
        
        if (checkIn == null || checkOut == null) {
            logger.logError("Cannot navigate to room selection: dates are null");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Dates not set");
            alert.setContentText("Please select check-in and check-out dates before proceeding.");
            alert.showAndWait();
            return false;
        }
        
        if (numAdults <= 0) {
            logger.logError("Cannot navigate to room selection: invalid number of adults");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Invalid occupancy");
            alert.setContentText("Number of adults must be greater than 0.");
            alert.showAndWait();
            return false;
        }
        
        if (reservationService == null) {
            logger.logError("Cannot navigate to room selection: ReservationService is null");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("System Error");
            alert.setHeaderText("Service not initialized");
            alert.setContentText("The reservation service is not available. Please restart the application.");
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
    //
     // Navigates to payment screen.
//
    public static void navigateToPaymentScreen(
            Reservation reservation,
            String returnScreen,
            LoggerService logger,
            Supplier<Stage> getCurrentStage,
            Consumer<Stage> setScene) {
        
        try {
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource("/view/kiosk/KioskPayment.fxml"));
            Parent root = loader.load();
            com.hotel.controller.KioskPaymentController controller = loader.getController();
            controller.initPaymentScreen(reservation, returnScreen);
            
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                setScene.accept(stage);
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to payment screen", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load payment screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    //
     // Navigates to feedback screen.
//
    public static void navigateToFeedback(
            Reservation reservation,
            javafx.scene.control.Button feedbackButton,
            javafx.scene.control.Label reservationNumberLabel,
            javafx.scene.control.TextField nameField,
            LoggerService logger) {
        
        try {
            // Check if we have a reservation
            if (reservation == null || reservation.getId() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Feedback");
                alert.setHeaderText("No Reservation Found");
                alert.setContentText("Please complete a booking first.");
                alert.showAndWait();
                return;
            }
            
            // Navigate to feedback screen
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource("/view/feedback/FeedbackSubmission.fxml"));
            Parent root = loader.load();
            com.hotel.controller.FeedbackController controller = loader.getController();
            
            // Set the reservation for feedback
            controller.setReservation(reservation.getId());
            
            // Get current stage - try from feedbackButton first, then other fields
            Stage stage = null;
            if (feedbackButton != null && feedbackButton.getScene() != null) {
                stage = (Stage) feedbackButton.getScene().getWindow();
            } else if (reservationNumberLabel != null && reservationNumberLabel.getScene() != null) {
                stage = (Stage) reservationNumberLabel.getScene().getWindow();
            } else if (nameField != null && nameField.getScene() != null) {
                stage = (Stage) nameField.getScene().getWindow();
            }
            
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            } else {
                // Fallback: create new stage
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 1200, 800));
                newStage.setTitle("Submit Feedback");
                newStage.show();
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to feedback screen", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load feedback screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    //
     // Returns to KioskWelcome screen.
//
    public static void returnToKioskWelcome(
            java.util.Stack<String> navigationHistory,
            LoggerService logger,
            Supplier<Stage> getCurrentStage) {
        
        try {
            navigationHistory.clear();
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource("/view/kiosk/KioskWelcome.fxml"));
            Parent root = loader.load();
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
                stage.show();
            }
        } catch (Exception e) {
            logger.logError("Failed to return to kiosk welcome screen", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load start page");
            alert.setContentText("Could not navigate back to the kiosk welcome screen: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    //
     // Goes back from welcome screen to KioskWelcome.
//
    public static void goBackFromWelcome(
            LoggerService logger,
            Supplier<Stage> getCurrentStage) {
        
        try {
            FXMLLoader loader = new FXMLLoader(KioskController.class.getResource("/view/kiosk/KioskWelcome.fxml"));
            Parent root = loader.load();
            Stage stage = getCurrentStage.get();
            if (stage != null) {
                stage.setScene(new Scene(root, 1200, 800));
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to KioskWelcome", e);
        }
    }
    
    //
     // Starts a new booking by clearing session and navigating to welcome screen.
//
    public static void startNewBooking(
            Runnable clearSession,
            Runnable resetBookingState,
            Runnable navigateToWelcome) {
        
        // Clear customer session - no one should be logged in
        if (clearSession != null) clearSession.run();
        
        // Clear all booking state
        if (resetBookingState != null) resetBookingState.run();
        
        // Navigate to welcome screen
        if (navigateToWelcome != null) navigateToWelcome.run();
    }
}

