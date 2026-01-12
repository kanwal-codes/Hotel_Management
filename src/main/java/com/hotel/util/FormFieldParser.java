package com.hotel.util;

import javafx.scene.control.TextField;

// utility class for parsing form field values
// extracted from controllers to reduce code duplication
public final class FormFieldParser {
    
    private FormFieldParser() {
        // utility class - prevent instantiation
    }
    
    // parses integer from textfield, returns default if parsing fails
    public static int parseInteger(TextField field, int defaultValue) {
        if (field == null || field.getText() == null || field.getText().isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    // parses integer from string, returns default if parsing fails
    public static int parseInteger(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}


