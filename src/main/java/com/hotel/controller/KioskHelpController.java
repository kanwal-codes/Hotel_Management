package com.hotel.controller;

import com.hotel.controller.helper.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//
 // Controller for the Kiosk Help Screen
 // Provides instructions and assistance options for kiosk users
//
public class KioskHelpController {

    @FXML
    private void handleBack(ActionEvent event) {
        navigate(event, "/view/kiosk/KioskWelcome.fxml");
    }

    @FXML
    private void handleCallStaff(ActionEvent event) {
        AlertHelper.showInfo("Staff Called", 
            "A staff member has been notified and will assist you shortly.\n\n" +
            "Please wait at the kiosk for assistance.");
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
}

