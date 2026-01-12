package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.ServiceAddon;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

//
 // Helper class for navigation and screen detection in KioskController.
 // Extracts screen detection and navigation post-processing logic.
//
public final class KioskNavigationHelper {
    
    private KioskNavigationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Determines current screen based on available FXML fields.
//
    public static String determineCurrentScreen(
            TextField emailField,
            TextField nameField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            TextField numAdultsField,
            TextField numChildrenField,
            Spinner<Integer> singleRoomSpinner,
            TableView<?> suggestedRoomsTable,
            CheckBox wifiCheckBox,
            Label guestNameLabel,
            Label totalAmountLabel,
            Label reservationNumberLabel) {
        
        if (emailField != null && nameField != null) {
            return "/view/kiosk/GuestDetails.fxml";
        } else if (checkInDatePicker != null && checkOutDatePicker != null) {
            return "/view/kiosk/DateSelection.fxml";
        } else if (numAdultsField != null && numChildrenField != null && checkInDatePicker == null) {
            return "/view/kiosk/BookingDetails.fxml";
        } else if (singleRoomSpinner != null || suggestedRoomsTable != null) {
            return "/view/kiosk/RoomSelection.fxml";
        } else if (wifiCheckBox != null) {
            return "/view/kiosk/AddOnServices.fxml";
        } else if (guestNameLabel != null && totalAmountLabel != null) {
            return "/view/kiosk/BookingSummary.fxml";
        } else if (reservationNumberLabel != null) {
            return "/view/kiosk/ConfirmationScreen.fxml";
        }
        return "/view/kiosk/WelcomeScreen.fxml";
    }
    
    //
     // Handles post-navigation screen loading based on detected screen type.
//
    public static void handlePostNavigationLoading(
            String fxmlPath,
            Label guestNameLabel,
            Label totalAmountLabel,
            Label reservationNumberLabel,
            Spinner<Integer> singleRoomSpinner,
            TableView<?> suggestedRoomsTable,
            Runnable loadBookingSummary,
            Runnable loadConfirmation,
            Runnable loadAvailableRooms,
            Runnable refreshCustomSelectionUI,
            Runnable updateSelectedRoomsSummary,
            com.hotel.util.LoggerService logger) {
        
        // Check if this is BookingSummary screen
        if (guestNameLabel != null && totalAmountLabel != null) {
            logger.logInfo("Detected BookingSummary screen - calling loadBookingSummary() AFTER state transfer");
            if (loadBookingSummary != null) {
                loadBookingSummary.run();
            }
        }
        
        // Check if this is ConfirmationScreen
        if (reservationNumberLabel != null) {
            logger.logInfo("Detected ConfirmationScreen - calling loadConfirmation() AFTER state transfer");
            if (loadConfirmation != null) {
                loadConfirmation.run();
            }
        }
        
        // Handle RoomSelection screen
        if ("/view/kiosk/RoomSelection.fxml".equals(fxmlPath)) {
            if (loadAvailableRooms != null) {
                loadAvailableRooms.run();
            }
            if (refreshCustomSelectionUI != null) {
                refreshCustomSelectionUI.run();
            }
            if (updateSelectedRoomsSummary != null) {
                updateSelectedRoomsSummary.run();
            }
        }
    }
    
    //
     // Gets current stage with kiosk-specific fallback nodes.
//
    public static javafx.stage.Stage getCurrentStageWithKioskFallbacks(
            javafx.scene.Node[] kioskNodes,
            javafx.scene.Node... fallbackNodes) {
        
        // Try kiosk nodes first
        if (kioskNodes != null) {
            for (javafx.scene.Node node : kioskNodes) {
                if (node != null && node.getScene() != null) {
                    return (javafx.stage.Stage) node.getScene().getWindow();
                }
            }
        }
        
        // Try fallback nodes
        if (fallbackNodes != null) {
            for (javafx.scene.Node node : fallbackNodes) {
                if (node != null && node.getScene() != null) {
                    return (javafx.stage.Stage) node.getScene().getWindow();
                }
            }
        }
        
        // Last resort: try to get from Window.getWindows() if available
        try {
            javafx.collections.ObservableList<javafx.stage.Window> windows = 
                javafx.stage.Window.getWindows();
            for (javafx.stage.Window window : windows) {
                if (window instanceof javafx.stage.Stage && window.isShowing()) {
                    return (javafx.stage.Stage) window;
                }
            }
        } catch (Exception e) {
            // Ignore if method not available
        }
        
        throw new IllegalStateException("Unable to determine current stage");
    }
    
    //
     // Gets current stage from ActionEvent with kiosk-specific fallback nodes.
//
    public static javafx.stage.Stage getCurrentStageFromEvent(
            ActionEvent event,
            javafx.scene.Node[] kioskNodes) {
        
        // Try to get from event source
        if (event != null && event.getSource() instanceof javafx.scene.Node) {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            if (source.getScene() != null) {
                return (javafx.stage.Stage) source.getScene().getWindow();
            }
        }
        
        // Fall back to kiosk nodes
        return getCurrentStageWithKioskFallbacks(kioskNodes);
    }
}

