package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.NavigationHelper;
import com.hotel.controller.helper.ValidationHelper;
import com.hotel.model.Guest;
import com.hotel.repository.GuestRepository;
import com.hotel.security.BCryptPasswordHasher;
import com.hotel.session.CustomerSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.persistence.EntityManager;
import java.util.Optional;

//
 // Controller for customer registration.
 // Extends BaseController to inherit common functionality.
//
public class CustomerRegistrationController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private javafx.scene.control.CheckBox enrollLoyaltyCheckBox;

    private EntityManager entityManager;
    private GuestRepository guestRepository;

    @FXML
    public void initialize() {
        entityManager = AppConfig.createEntityManager();
        guestRepository = new GuestRepository(entityManager);
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        hideError();
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String phone = phoneField.getText() != null ? phoneField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        String confirm = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";

        // Use ValidationHelper for field validation
        if (!ValidationHelper.validateName(nameField, errorLabel)) {
            return;
        }
        if (!ValidationHelper.validatePhone(phoneField, errorLabel)) {
            return;
        }
        if (!ValidationHelper.validateEmail(emailField, errorLabel)) {
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }

        Optional<Guest> existing = guestRepository.findByEmail(email);
        if (existing.isPresent()) {
            showError("An account already exists for that email. Please log in.");
            return;
        }

        Guest guest = new Guest(name, phone, email, null);
        guest.setCustomerPasswordHash(BCryptPasswordHasher.hash(password));
        
        // Enroll in loyalty program if checkbox is selected
        if (enrollLoyaltyCheckBox != null && enrollLoyaltyCheckBox.isSelected()) {
            // Will generate loyalty number after guest is saved (needs ID)
            // We'll set it after save
        }
        
        guest = guestRepository.save(guest);
        
        // Generate loyalty number if enrollment was requested
        if (enrollLoyaltyCheckBox != null && enrollLoyaltyCheckBox.isSelected()) {
            String loyaltyNumber = "L" + String.format("%06d", guest.getId());
            guest.setLoyaltyNumber(loyaltyNumber);
            guest.setLoyaltyPoints(0);
            guest = guestRepository.save(guest);
        }

        CustomerSession.setAuthenticatedGuest(guest);
        // Navigate directly to GuestDetails (booking flow) instead of WelcomeScreen
        navigate(event, "/view/kiosk/GuestDetails.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigate(event, "/view/main/UnifiedLogin.fxml");
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
}

