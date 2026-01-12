package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.NavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Guest;
import com.hotel.repository.AdminUserRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.security.BCryptPasswordHasher;
import com.hotel.service.AuthService;
import com.hotel.session.CustomerSession;
import com.hotel.util.EmailDetector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.util.Optional;

//
 // Handles login for both admin and customer users.
 // Automatically detects user type based on email domain.
 // Routes to appropriate dashboard after successful authentication.
//
public class UnifiedLoginController extends BaseController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label userTypeLabel;
    @FXML private VBox customerOptions;

    private EntityManager entityManager;
    private GuestRepository guestRepository;
    private AdminUserRepository adminUserRepository;
    private AuthService authService;
    private boolean isManagementEmail = false;

    @FXML
    public void initialize() {
        entityManager = AppConfig.createEntityManager();
        guestRepository = new GuestRepository(entityManager);
        adminUserRepository = new AdminUserRepository(entityManager);
        authService = new AuthService(entityManager);
        
        // Detect user type as they type email
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            detectUserType(newValue);
        });
    }

    private void detectUserType(String email) {
        if (email == null || email.isEmpty()) {
            userTypeLabel.setVisible(false);
            // Show customer options by default when email is empty
            customerOptions.setVisible(true);
            customerOptions.setManaged(true);
            isManagementEmail = false;
            return;
        }

        isManagementEmail = EmailDetector.isManagementEmail(email);
        
        if (isManagementEmail) {
            userTypeLabel.setText("Management Account Detected");
            userTypeLabel.setVisible(true);
            customerOptions.setVisible(false);
            customerOptions.setManaged(false);
        } else {
            userTypeLabel.setText("Customer Account");
            userTypeLabel.setVisible(true);
            customerOptions.setVisible(true);
            customerOptions.setManaged(true);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        hideError();
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        // Check if management email
        if (isManagementEmail) {
            handleManagementLogin(email, password, event);
        } else {
            handleCustomerLogin(email, password, event);
        }
    }

    private void handleManagementLogin(String email, String password, ActionEvent event) {
        try {
            Optional<AdminUser> adminOpt = authService.loginByEmail(email, password);
            if (adminOpt.isPresent()) {
                AdminUser admin = adminOpt.get();
                // Navigate to admin dashboard
                navigateToAdminDashboard(event, admin);
            } else {
                showError("Invalid email or password for management account.");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }
    
    private void navigateToAdminDashboard(ActionEvent event, AdminUser admin) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = NavigationHelper.getCurrentStage(source);
            NavigationHelper.navigateWithController(stage, "/view/admin/Dashboard.fxml", controller -> {
                if (controller instanceof AdminDashboardController) {
                    ((AdminDashboardController) controller).init(admin);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to load admin dashboard", e);
        }
    }

    private void handleCustomerLogin(String email, String password, ActionEvent event) {
        Optional<Guest> guestOpt = guestRepository.findByEmail(email);
        if (guestOpt.isEmpty()) {
            showError("No account found for that email. Please create a new account or continue as guest.");
            return;
        }

        Guest guest = guestOpt.get();
        String hash = guest.getCustomerPasswordHash();
        if (hash == null || !BCryptPasswordHasher.verify(password, hash)) {
            showError("Invalid email or password.");
            return;
        }

        CustomerSession.setAuthenticatedGuest(guest);
        // Navigate directly to GuestDetails (all-in-one form) to start booking
        navigate(event, "/view/kiosk/GuestDetails.fxml", null);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        navigate(event, "/view/main/CustomerRegistration.fxml", null);
    }

    @FXML
    private void handleGuest(ActionEvent event) {
        CustomerSession.clear();
        // Navigate directly to GuestDetails to start booking
        navigate(event, "/view/kiosk/GuestDetails.fxml", null);
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
    
    @FXML
    private void handleBack(ActionEvent event) {
        navigate(event, "/view/kiosk/KioskWelcome.fxml", null);
    }

    //
     // Navigates to a screen using NavigationHelper.
//
    private void navigate(ActionEvent event, String fxmlPath, AdminUser admin) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = NavigationHelper.getCurrentStage(source);
            
            if (admin != null) {
                // Use NavigationHelper with controller callback
                NavigationHelper.navigateWithController(stage, fxmlPath, controller -> {
                    if (controller instanceof AdminDashboardController) {
                        ((AdminDashboardController) controller).init(admin);
                    } else if (controller instanceof KioskWelcomeController) {
                        ((KioskWelcomeController) controller).init(admin);
                    }
                });
            } else {
                // Simple navigation without callback
                NavigationHelper.navigate(stage, fxmlPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlPath, e);
        }
    }
}

