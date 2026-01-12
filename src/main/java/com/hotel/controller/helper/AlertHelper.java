package com.hotel.controller.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

//
 // Unified alert helper for all controllers (admin and kiosk).
 // Provides consistent alert dialogs across the application.
//
public final class AlertHelper {

    private AlertHelper() {
        // Utility class - prevent instantiation
    }

    //
     // Shows an information alert dialog.
//
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    //
     // Shows a warning alert dialog.
//
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    //
     // Shows an error alert dialog.
//
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    //
     // Shows a confirmation dialog and returns true if user clicked OK.
//
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    //
     // Shows a generic alert dialog of the specified type.
//
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

