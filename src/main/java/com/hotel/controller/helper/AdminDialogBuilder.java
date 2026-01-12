package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.GuestSelectionResult;
import com.hotel.model.ServiceAddon;
import com.hotel.repository.GuestRepository;
import com.hotel.security.BCryptPasswordHasher;
import com.hotel.util.LoggerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

//
 // Helper class for building admin dialogs.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminDialogBuilder {
    
    private AdminDialogBuilder() {
        // Utility class - prevent instantiation
    }
    
    //
     // Shows customer selection dialog with table of existing customers.
//
    public static Optional<GuestSelectionResult> showCustomerSelectionDialog(
            GuestRepository guestRepository,
            LoggerService logger) {
        
        try {
            List<Guest> allCustomers = guestRepository.findAllGuestsWithAccounts();
            
            Dialog<GuestSelectionResult> dialog = new Dialog<>();
            dialog.setTitle("Select Customer");
            dialog.setHeaderText("Choose an existing customer or create a new one");
            
            ButtonType selectButtonType = new ButtonType("Select", ButtonType.OK.getButtonData());
            ButtonType newUserButtonType = new ButtonType("New User", ButtonType.OK.getButtonData());
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
            dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, newUserButtonType, cancelButtonType);
            
            VBox mainContainer = new VBox(15);
            mainContainer.setPadding(new Insets(20));
            
            Label customerLabel = new Label("Existing Customers (with accounts):");
            customerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            TableView<Guest> customerTable = new TableView<>();
            ObservableList<Guest> customerList = FXCollections.observableArrayList(allCustomers);
            customerTable.setItems(customerList);
            customerTable.setPrefHeight(350);
            customerTable.setPrefWidth(600);
            
            // Enable double-click to select
            customerTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    Guest selected = customerTable.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        dialog.setResult(new GuestSelectionResult(selected, false, false));
                    }
                }
            });
            
            TableColumn<Guest, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
            nameCol.setPrefWidth(180);
            
            TableColumn<Guest, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getEmail() != null ? cell.getValue().getEmail() : ""));
            emailCol.setPrefWidth(220);
            
            TableColumn<Guest, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getPhone() != null ? cell.getValue().getPhone() : ""));
            phoneCol.setPrefWidth(150);
            
            TableColumn<Guest, String> loyaltyCol = new TableColumn<>("Loyalty Number");
            loyaltyCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getLoyaltyNumber() != null ? cell.getValue().getLoyaltyNumber() : "N/A"));
            loyaltyCol.setPrefWidth(150);
            
            customerTable.getColumns().addAll(nameCol, emailCol, phoneCol, loyaltyCol);
            
            Label instructionLabel = new Label("Double-click on a customer to select, or click 'Select' button after choosing one.");
            instructionLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
            instructionLabel.setWrapText(true);
            
            mainContainer.getChildren().addAll(customerLabel, instructionLabel, customerTable);
            
            dialog.getDialogPane().setContent(mainContainer);
            dialog.getDialogPane().setPrefWidth(650);
            dialog.getDialogPane().setPrefHeight(500);
            
            // Handle button clicks
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == selectButtonType) {
                    Guest selected = customerTable.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        return new GuestSelectionResult(selected, false, false);
                    } else {
                        AlertHelper.showError("Error", "Please select a customer from the list.");
                        return null;
                    }
                } else if (dialogButton == newUserButtonType) {
                    // Show new user options dialog
                    return showNewUserOptionsDialog().orElse(null);
                }
                return null;
            });
            
            return dialog.showAndWait();
        } catch (Exception e) {
            logger.logError("Failed to show customer selection dialog", e);
            AlertHelper.showError("Error", "Failed to load customers: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    //
     // Shows dialog for new user options (create account or proceed as guest).
//
    public static Optional<GuestSelectionResult> showNewUserOptionsDialog() {
        Dialog<GuestSelectionResult> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("How would you like to proceed?");
        
        ButtonType createAccountButtonType = new ButtonType("Create Account", ButtonType.OK.getButtonData());
        ButtonType guestButtonType = new ButtonType("Proceed as Guest", ButtonType.OK.getButtonData());
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(createAccountButtonType, guestButtonType, cancelButtonType);
        
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        
        Label infoLabel = new Label("Select an option:");
        infoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label createAccountLabel = new Label("• Create Account: Customer will have email and password for future logins");
        createAccountLabel.setWrapText(true);
        createAccountLabel.setStyle("-fx-font-size: 12px;");
        
        Label guestLabel = new Label("• Proceed as Guest: No account will be created. Customer details will be saved but they won't be able to login.");
        guestLabel.setWrapText(true);
        guestLabel.setStyle("-fx-font-size: 12px;");
        
        vbox.getChildren().addAll(infoLabel, createAccountLabel, guestLabel);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().setPrefWidth(500);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createAccountButtonType) {
                return new GuestSelectionResult(null, true, false);
            } else if (dialogButton == guestButtonType) {
                return new GuestSelectionResult(null, false, true);
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
    
    //
     // Shows dialog to create a new customer account with password.
//
    public static Optional<Guest> showAccountCreationDialog(
            GuestRepository guestRepository,
            LoggerService logger) {
        
        Dialog<Guest> dialog = new Dialog<>();
        dialog.setTitle("Create Customer Account");
        dialog.setHeaderText("Enter customer details to create an account");
        
        ButtonType createButtonType = new ButtonType("Create Account", ButtonType.OK.getButtonData());
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Confirm Password:"), 0, 4);
        grid.add(confirmPasswordField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/disable create button based on validation
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        
        // Validate fields
        Runnable validateFields = () -> {
            boolean valid = !nameField.getText().trim().isEmpty() &&
                           !phoneField.getText().trim().isEmpty() &&
                           !emailField.getText().trim().isEmpty() &&
                           !passwordField.getText().isEmpty() &&
                           passwordField.getText().equals(confirmPasswordField.getText()) &&
                           passwordField.getText().length() >= 6;
            createButton.setDisable(!valid);
        };
        
        nameField.textProperty().addListener((obs, old, val) -> validateFields.run());
        phoneField.textProperty().addListener((obs, old, val) -> validateFields.run());
        emailField.textProperty().addListener((obs, old, val) -> validateFields.run());
        passwordField.textProperty().addListener((obs, old, val) -> validateFields.run());
        confirmPasswordField.textProperty().addListener((obs, old, val) -> validateFields.run());
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                // Validate email doesn't already exist
                Optional<Guest> existing = guestRepository.findByEmail(emailField.getText().trim());
                if (existing.isPresent()) {
                    AlertHelper.showError("Error", "An account with this email already exists. Please select that customer instead.");
                    return null;
                }
                
                // Create new guest with account
                Guest guest = new Guest();
                guest.setName(nameField.getText().trim());
                guest.setPhone(phoneField.getText().trim());
                guest.setEmail(emailField.getText().trim());
                guest.setCustomerPasswordHash(BCryptPasswordHasher.hash(passwordField.getText()));
                
                guest = guestRepository.save(guest);
                return guest;
            }
            return null;
        });
        
        return dialog.showAndWait();
    }
    
    //
     // Shows service selection dialog for adding addons to reservation.
//
    public static Optional<ServiceAddon> showServiceSelectionDialog(
            List<ServiceAddon> allAddons,
            java.text.NumberFormat currencyFormat) {
        
        if (allAddons == null || allAddons.isEmpty()) {
            AlertHelper.showError("Error", "No services available.");
            return Optional.empty();
        }
        
        // Create wrapper class for better display in ChoiceDialog
        class ServiceOption {
            private final ServiceAddon addon;
            ServiceOption(ServiceAddon addon) { this.addon = addon; }
            ServiceAddon getAddon() { return addon; }
            @Override
            public String toString() {
                String pricing = addon.getPricingModel() == com.hotel.model.PricingModel.PER_NIGHT ? "per night" : "one-time";
                return addon.getName() + " - " + currencyFormat.format(addon.getPrice()) + " (" + pricing + ")";
            }
        }
        
        // Create dialog to select service
        List<ServiceOption> serviceOptions = allAddons.stream()
            .map(ServiceOption::new)
            .collect(java.util.stream.Collectors.toList());
        
        javafx.scene.control.ChoiceDialog<ServiceOption> dialog = new javafx.scene.control.ChoiceDialog<>(
            serviceOptions.get(0), serviceOptions);
        dialog.setTitle("Add Service");
        dialog.setHeaderText("Select a service to add");
        dialog.setContentText("Service:");
        
        Optional<ServiceOption> selectedOption = dialog.showAndWait();
        if (selectedOption.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(selectedOption.get().getAddon());
    }
}

