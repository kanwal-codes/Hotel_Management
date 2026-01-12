package com.hotel.controller;

import com.hotel.model.AdminUser;
import com.hotel.model.Guest;
import com.hotel.session.CustomerSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class KioskWelcomeController {
    
    @FXML private Label userInfoLabel;
    @FXML private Button loginButton;
    @FXML private Button logoutButton;
    @FXML private VBox bookingSubMenu;
    
    private Guest currentGuest;
    private AdminUser currentAdmin;
    
    @FXML
    public void initialize() {
        updateLoginStatus();
    }
    
    // Make updateLoginStatus accessible for navigation
    public void updateLoginStatus() {
        // Check if user is logged in
        currentGuest = CustomerSession.getAuthenticatedGuest();
        if (currentGuest != null) {
            if (userInfoLabel != null) {
                userInfoLabel.setText("Logged in as: " + currentGuest.getEmail());
            }
            if (loginButton != null) loginButton.setVisible(false);
            if (logoutButton != null) logoutButton.setVisible(true);
        } else {
            if (userInfoLabel != null) userInfoLabel.setText("");
            if (loginButton != null) loginButton.setVisible(true);
            if (logoutButton != null) logoutButton.setVisible(false);
        }
    }
    
    public void init(AdminUser admin) {
        this.currentAdmin = admin;
        if (admin != null) {
            userInfoLabel.setText("Management: " + admin.getEmail());
            logoutButton.setVisible(true);
        }
    }
    
    @FXML
    private void handleBookingToggle(ActionEvent event) {
        // Toggle booking sub-menu visibility
        boolean isVisible = bookingSubMenu.isVisible();
        bookingSubMenu.setVisible(!isVisible);
        bookingSubMenu.setManaged(!isVisible);
    }
    
    @FXML
    private void handleViewBooking(ActionEvent event) {
        // Navigate to check booking screen (which will show details and allow cancellation)
        navigate(event, "/view/kiosk/CheckBooking.fxml");
    }
    
    @FXML
    private void handleMakeBooking(ActionEvent event) {
        // If already logged in, go directly to GuestDetails (all-in-one form)
        // Guest details will be auto-filled because customer is logged in
        if (currentGuest != null || currentAdmin != null) {
            navigate(event, "/view/kiosk/GuestDetails.fxml");
        } else {
            // Not logged in, go to login page first
            navigate(event, "/view/main/UnifiedLogin.fxml");
        }
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        navigate(event, "/view/main/UnifiedLogin.fxml");
    }
    
    @FXML
    private void handleBrowseServices(ActionEvent event) {
        navigate(event, "/view/kiosk/BrowseServices.fxml");
    }
    
    @FXML
    private void handleHelp(ActionEvent event) {
        navigate(event, "/view/kiosk/HelpScreen.fxml");
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        CustomerSession.clear();
        currentGuest = null;
        currentAdmin = null;
        updateLoginStatus();
        // Refresh the screen to update UI
        navigate(event, "/view/kiosk/KioskWelcome.fxml");
    }
    
    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Pass admin to controller if needed
            if (currentAdmin != null) {
                Object controller = loader.getController();
                if (controller instanceof AdminDashboardController) {
                    ((AdminDashboardController) controller).init(currentAdmin);
                } else if (controller instanceof KioskWelcomeController) {
                    ((KioskWelcomeController) controller).init(currentAdmin);
                }
            }
            
            // If navigating to KioskWelcome, refresh login status
            if (fxmlPath.contains("KioskWelcome")) {
                Object controller = loader.getController();
                if (controller instanceof KioskWelcomeController) {
                    ((KioskWelcomeController) controller).updateLoginStatus();
                }
            }
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlPath, e);
        }
    }
}
