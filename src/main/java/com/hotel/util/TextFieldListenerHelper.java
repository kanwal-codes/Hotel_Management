package com.hotel.util;

import javafx.scene.control.TextField;

import java.util.function.Consumer;

// utility class for attaching listeners to text fields
// extracted from controllers to reduce code duplication
public final class TextFieldListenerHelper {
    
    private TextFieldListenerHelper() {
        // utility class - prevent instantiation
    }
    
    // attaches numeric-only listener to textfield
    // only allows digits and triggers callback on change
    public static void attachNumericListener(TextField field, Runnable onChange) {
        if (field == null) return;
        
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("\\D", ""));
            }
            if (onChange != null) {
                onChange.run();
            }
        });
    }
}


