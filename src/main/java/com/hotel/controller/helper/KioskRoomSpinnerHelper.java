package com.hotel.controller.helper;

import com.hotel.util.LoggerService;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

import java.util.function.IntConsumer;
import java.util.function.Consumer;

//
 // Helper class for room spinner operations in KioskController.
 // Extracts spinner setup and management logic to reduce controller size.
//
public final class KioskRoomSpinnerHelper {
    
    private KioskRoomSpinnerHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Sets up a room spinner with value factory and listeners.
//
    public static void setupRoomSpinner(
            Spinner<Integer> spinner,
            HBox row,
            IntConsumer valueConsumer,
            Consumer<HBox> updateRoomRowHighlight,
            Runnable updateSelectedRoomsSummary) {
        
        if (spinner == null) {
            return;
        }
        // Initialize with a reasonable max (10) so users can select even if available count isn't loaded yet
        // This will be updated by applyRoomSpinnerLimits when available rooms are loaded
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        spinner.setValueFactory(factory);
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal != null ? newVal : 0;
            valueConsumer.accept(value);
            if (updateRoomRowHighlight != null) {
                updateRoomRowHighlight.accept(row);
            }
            if (updateSelectedRoomsSummary != null) {
                updateSelectedRoomsSummary.run();
            }
        });
    }
    
    //
     // Sets spinner value safely.
//
    public static void setSpinnerValue(Spinner<Integer> spinner, int value) {
        if (spinner != null && spinner.getValueFactory() != null) {
            spinner.getValueFactory().setValue(value);
        }
    }
    
    //
     // Clears all room spinner values.
//
    public static void clearCustomRoomSelection(
            Spinner<Integer> singleRoomSpinner,
            Spinner<Integer> doubleRoomSpinner,
            Spinner<Integer> deluxeRoomSpinner,
            Spinner<Integer> penthouseSpinner,
            Consumer<Integer> setSingleRoomCount,
            Consumer<Integer> setDoubleRoomCount,
            Consumer<Integer> setDeluxeRoomCount,
            Consumer<Integer> setPenthouseRoomCount,
            Runnable updateSelectedRoomsSummary) {
        
        if (setSingleRoomCount != null) setSingleRoomCount.accept(0);
        if (setDoubleRoomCount != null) setDoubleRoomCount.accept(0);
        if (setDeluxeRoomCount != null) setDeluxeRoomCount.accept(0);
        if (setPenthouseRoomCount != null) setPenthouseRoomCount.accept(0);
        
        setSpinnerValue(singleRoomSpinner, 0);
        setSpinnerValue(doubleRoomSpinner, 0);
        setSpinnerValue(deluxeRoomSpinner, 0);
        setSpinnerValue(penthouseSpinner, 0);
        
        if (updateSelectedRoomsSummary != null) {
            updateSelectedRoomsSummary.run();
        }
    }
}


