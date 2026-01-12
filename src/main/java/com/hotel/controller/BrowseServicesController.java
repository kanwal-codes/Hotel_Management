package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.model.AmenityBooking;
import com.hotel.repository.AmenityBookingRepository;
import com.hotel.controller.helper.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//
 // Controller for browsing and booking hotel amenities
//
public class BrowseServicesController {

    @FXML
    private void handleBack(ActionEvent event) {
        navigate(event, "/view/kiosk/KioskWelcome.fxml");
    }

    @FXML
    private void handleBookAmenity(ActionEvent event) {
        Button button = (Button) event.getSource();
        String amenityName = (String) button.getUserData();
        
        if (amenityName == null || amenityName.isEmpty()) {
            AlertHelper.showError("Error", "Please select a valid amenity.");
            return;
        }
        
        // Show booking dialog
        showBookingDialog(amenityName, event);
    }
    
    private void showBookingDialog(String amenityName, ActionEvent event) {
        try {
            // Create dialog
            javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Book " + amenityName);
            dialog.setHeaderText("Book " + amenityName);
            
            // Create dialog content
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(15);
            content.setPadding(new javafx.geometry.Insets(20));
            
            // Date picker
            javafx.scene.control.Label dateLabel = new javafx.scene.control.Label("Select Date:");
            javafx.scene.control.DatePicker datePicker = new javafx.scene.control.DatePicker();
            datePicker.setValue(LocalDate.now());
            datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });
            
            // Time combo box
            javafx.scene.control.Label timeLabel = new javafx.scene.control.Label("Select Time:");
            javafx.scene.control.ComboBox<String> timeComboBox = new javafx.scene.control.ComboBox<>();
            List<String> timeSlots = generateTimeSlots();
            timeComboBox.getItems().addAll(timeSlots);
            timeComboBox.setValue(timeSlots.get(0));
            
            // Name field
            javafx.scene.control.Label nameLabel = new javafx.scene.control.Label("Your Name:");
            javafx.scene.control.TextField nameField = new javafx.scene.control.TextField();
            nameField.setPromptText("Enter your name");
            
            // Email field
            javafx.scene.control.Label emailLabel = new javafx.scene.control.Label("Email (Optional):");
            javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
            emailField.setPromptText("Enter your email");
            
            // Phone field
            javafx.scene.control.Label phoneLabel = new javafx.scene.control.Label("Phone (Optional):");
            javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
            phoneField.setPromptText("Enter your phone");
            
            content.getChildren().addAll(
                dateLabel, datePicker,
                timeLabel, timeComboBox,
                nameLabel, nameField,
                emailLabel, emailField,
                phoneLabel, phoneField
            );
            
            dialog.getDialogPane().setContent(content);
            
            // Add buttons
            javafx.scene.control.ButtonType bookButtonType = new javafx.scene.control.ButtonType("Book", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            javafx.scene.control.ButtonType cancelButtonType = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, cancelButtonType);
            
            // Validate and process booking
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == bookButtonType) {
                    // Validate inputs
                    if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                        AlertHelper.showError("Validation Error", "Please enter your name.");
                        return null;
                    }
                    if (datePicker.getValue() == null) {
                        AlertHelper.showError("Validation Error", "Please select a date.");
                        return null;
                    }
                    if (timeComboBox.getValue() == null || timeComboBox.getValue().isEmpty()) {
                        AlertHelper.showError("Validation Error", "Please select a time.");
                        return null;
                    }
                    
                    // Parse time
                    LocalTime bookingTime = parseTime(timeComboBox.getValue());
                    
                    // Create booking
                    AmenityBooking booking = new AmenityBooking(
                        amenityName,
                        datePicker.getValue(),
                        bookingTime,
                        nameField.getText().trim(),
                        emailField.getText() != null ? emailField.getText().trim() : "",
                        phoneField.getText() != null ? phoneField.getText().trim() : ""
                    );
                    
                    // Save booking
                    EntityManager em = AppConfig.createEntityManager();
                    try {
                        em.getTransaction().begin();
                        AmenityBookingRepository repo = new AmenityBookingRepository(em);
                        booking = repo.save(booking);
                        em.getTransaction().commit();
                        
                        // Show success with booking ID
                        AlertHelper.showInfo("Booking Confirmed!", 
                            "Your amenity booking has been confirmed!\n\n" +
                            "Amenity: " + amenityName + "\n" +
                            "Date: " + datePicker.getValue() + "\n" +
                            "Time: " + timeComboBox.getValue() + "\n\n" +
                            "Booking ID: " + booking.getBookingId() + "\n\n" +
                            "Please save this booking ID for your records.");
                        
                        return "SUCCESS";
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        AlertHelper.showError("Booking Error", "Failed to create booking: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    } finally {
                        em.close();
                    }
                }
                return null;
            });
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            AlertHelper.showError("Error", "Failed to open booking dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int hour = 6; hour < 22; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String timeStr = String.format("%02d:%02d", hour, minute);
                slots.add(timeStr);
            }
        }
        return slots;
    }
    
    private LocalTime parseTime(String timeStr) {
        String[] parts = timeStr.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }
    
    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
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


