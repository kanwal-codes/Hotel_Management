package com.hotel.controller.helper;

import com.hotel.util.Validator;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//
 // Centralized validation helper for guest field validation.
 // Provides reusable validation methods that can be used across multiple controllers.
//
 // This class reduces code duplication by centralizing validation logic
 // that was previously duplicated in KioskController, AdminReservationController,
 // and CustomerRegistrationController.
//
public final class ValidationHelper {
    
    private ValidationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Validates a name field and displays error if invalid.
//
     // @param nameField The text field containing the name
     // @param errorLabel The label to display error messages (can be null)
     // @return true if valid, false otherwise
//
    public static boolean validateName(TextField nameField, Label errorLabel) {
        if (nameField == null) {
            return false;
        }
        
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        
        if (name.isEmpty()) {
            showError(errorLabel, "Name is required");
            return false;
        } else if (name.length() < 2) {
            showError(errorLabel, "Name must be at least 2 characters");
            return false;
        } else if (!Validator.isValidName(name)) {
            showError(errorLabel, "Please enter a valid name (2-100 characters)");
            return false;
        } else {
            hideError(errorLabel);
            return true;
        }
    }
    
    //
     // Validates a phone field and displays error if invalid.
//
     // @param phoneField The text field containing the phone number
     // @param errorLabel The label to display error messages (can be null)
     // @return true if valid, false otherwise
//
    public static boolean validatePhone(TextField phoneField, Label errorLabel) {
        if (phoneField == null) {
            return false;
        }
        
        String phone = phoneField.getText() != null ? phoneField.getText().trim() : "";
        
        if (phone.isEmpty()) {
            showError(errorLabel, "Phone number is required");
            return false;
        } else if (!Validator.isValidPhone(phone)) {
            showError(errorLabel, "Please enter a valid phone number");
            return false;
        } else {
            hideError(errorLabel);
            return true;
        }
    }
    
    //
     // Validates an email field and displays error if invalid.
//
     // @param emailField The text field containing the email
     // @param errorLabel The label to display error messages (can be null)
     // @return true if valid, false otherwise
//
    public static boolean validateEmail(TextField emailField, Label errorLabel) {
        if (emailField == null) {
            return false;
        }
        
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        
        if (email.isEmpty()) {
            showError(errorLabel, "Email address is required");
            return false;
        } else if (!Validator.isValidEmail(email)) {
            showError(errorLabel, "Please enter a valid email address");
            return false;
        } else {
            hideError(errorLabel);
            return true;
        }
    }
    
    //
     // Validates all guest fields (name, phone, email) and displays errors if invalid.
//
     // @param nameField The name text field
     // @param phoneField The phone text field
     // @param emailField The email text field
     // @param nameErrorLabel The label for name errors (can be null)
     // @param phoneErrorLabel The label for phone errors (can be null)
     // @param emailErrorLabel The label for email errors (can be null)
     // @return true if all fields are valid, false otherwise
//
    public static boolean validateGuestFields(
            TextField nameField, 
            TextField phoneField, 
            TextField emailField,
            Label nameErrorLabel,
            Label phoneErrorLabel,
            Label emailErrorLabel) {
        
        boolean nameValid = validateName(nameField, nameErrorLabel);
        boolean phoneValid = validatePhone(phoneField, phoneErrorLabel);
        boolean emailValid = validateEmail(emailField, emailErrorLabel);
        
        return nameValid && phoneValid && emailValid;
    }
    
    //
     // Validates name using Validator utility (for inline validation).
//
     // @param name The name string to validate
     // @return true if valid, false otherwise
//
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmed = name.trim();
        return trimmed.length() >= 2 && Validator.isValidName(trimmed);
    }
    
    //
     // Validates phone using Validator utility (for inline validation).
//
     // @param phone The phone string to validate
     // @return true if valid, false otherwise
//
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return Validator.isValidPhone(phone.trim());
    }
    
    //
     // Validates email using Validator utility (for inline validation).
//
     // @param email The email string to validate
     // @return true if valid, false otherwise
//
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Validator.isValidEmail(email.trim());
    }
    
    //
     // Helper method to show error in a label.
//
     // @param errorLabel The label to show error in (can be null)
     // @param message The error message
//
    public static void showError(Label errorLabel, String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }
    
    //
     // Helper method to hide error in a label.
//
     // @param errorLabel The label to hide error in (can be null)
//
    public static void hideError(Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }
}

