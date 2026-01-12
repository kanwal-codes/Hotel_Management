package com.hotel.controller.helper;

import com.hotel.app.AppConfig;
import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.repository.AddonRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.BillingService;
import com.hotel.service.PricingService;
import com.hotel.service.ReservationService;
import com.hotel.session.CustomerSession;
import com.hotel.util.LoggerService;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Consumer;

//
 // Helper class for initialization logic in KioskController.
 // Extracts service initialization and UI setup logic.
//
public final class KioskInitializationHelper {
    
    private KioskInitializationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Initializes room spinners with default values.
//
    public static void initializeRoomSpinners(
            Spinner<Integer> singleRoomSpinner,
            Spinner<Integer> doubleRoomSpinner,
            Spinner<Integer> deluxeRoomSpinner,
            Spinner<Integer> penthouseSpinner) {
        
        if (singleRoomSpinner != null) {
            singleRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        }
        if (doubleRoomSpinner != null) {
            doubleRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        }
        if (deluxeRoomSpinner != null) {
            deluxeRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        }
        if (penthouseSpinner != null) {
            penthouseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        }
    }
    
    //
     // Sets up add-on checkbox listeners.
//
    public static void setupAddOnCheckboxListeners(
            CheckBox wifiCheckBox,
            CheckBox breakfastCheckBox,
            CheckBox parkingCheckBox,
            CheckBox spaCheckBox,
            Runnable onAddOnChanged,
            LoggerService logger) {
        
        if (wifiCheckBox != null) {
            wifiCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                logger.logInfo("Wi-Fi checkbox changed: " + isSelected);
                if (onAddOnChanged != null) onAddOnChanged.run();
            });
        }
        if (breakfastCheckBox != null) {
            breakfastCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                logger.logInfo("Breakfast checkbox changed: " + isSelected);
                if (onAddOnChanged != null) onAddOnChanged.run();
            });
        }
        if (parkingCheckBox != null) {
            parkingCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                logger.logInfo("Parking checkbox changed: " + isSelected);
                if (onAddOnChanged != null) onAddOnChanged.run();
            });
        }
        if (spaCheckBox != null) {
            spaCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                logger.logInfo("Spa checkbox changed: " + isSelected);
                if (onAddOnChanged != null) onAddOnChanged.run();
            });
        }
    }
    
    //
     // Sets up room spinner listeners for validation.
//
    public static void setupRoomSpinnerListeners(
            Spinner<Integer> singleRoomSpinner,
            Spinner<Integer> doubleRoomSpinner,
            Spinner<Integer> deluxeRoomSpinner,
            Spinner<Integer> penthouseSpinner,
            Runnable onSpinnerChanged) {
        
        if (singleRoomSpinner != null) {
            singleRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (onSpinnerChanged != null) onSpinnerChanged.run();
            });
        }
        if (doubleRoomSpinner != null) {
            doubleRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (onSpinnerChanged != null) onSpinnerChanged.run();
            });
        }
        if (deluxeRoomSpinner != null) {
            deluxeRoomSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (onSpinnerChanged != null) onSpinnerChanged.run();
            });
        }
        if (penthouseSpinner != null) {
            penthouseSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (onSpinnerChanged != null) onSpinnerChanged.run();
            });
        }
    }
    
    //
     // Validates service initialization.
//
    public static void validateServices(
            Object reservationService,
            Object pricingService,
            Object billingService) {
        
        if (reservationService == null) {
            throw new RuntimeException("ReservationService initialization failed");
        }
        if (pricingService == null) {
            throw new RuntimeException("PricingService initialization failed");
        }
        if (billingService == null) {
            throw new RuntimeException("BillingService initialization failed");
        }
    }
    
    //
     // Tests database connection.
//
    public static void testDatabaseConnection(LoggerService logger) {
        try {
            RoomRepository testRepo = AppConfig.createRoomRepository();
            List<Room> testRooms = testRepo.findAll();
            logger.logInfo("Database connection test: Found " + testRooms.size() + " rooms in database");
            if (testRooms.isEmpty()) {
                logger.logWarning("No rooms found in database! Check seed data.");
            }
        } catch (Exception dbError) {
            logger.logError("Database connection test failed: " + dbError.getMessage(), dbError);
        }
    }
    
    //
     // Gets guest from session.
//
    public static com.hotel.model.Guest getGuestFromSession() {
        return CustomerSession.getAuthenticatedGuest();
    }
    
    //
     // Shows initialization error alert.
//
    public static void showInitializationError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText("Failed to initialize application");
            alert.setContentText("Please restart the application. Error: " + message);
            alert.showAndWait();
        });
    }
    
    //
     // Initializes all services and repositories.
     // Returns a ServiceInitResult containing all initialized services.
//
    public static ServiceInitResult initializeServices(LoggerService logger) {
        ServiceInitResult result = new ServiceInitResult();
        
        try {
            System.out.println("[KioskController] initialize() called");
            
            // Initialize services
            result.em = AppConfig.createEntityManager();
            System.out.println("[KioskController] EntityManager created: " + (result.em != null));
            
            result.reservationService = AppConfig.createReservationService();
            System.out.println("[KioskController] ReservationService created: " + (result.reservationService != null));
            
            result.pricingService = AppConfig.createPricingService();
            System.out.println("[KioskController] PricingService created: " + (result.pricingService != null));
            
            result.billingService = AppConfig.createBillingService();
            System.out.println("[KioskController] BillingService created: " + (result.billingService != null));
            
            result.guestRepository = AppConfig.createGuestRepository();
            System.out.println("[KioskController] GuestRepository created: " + (result.guestRepository != null));
            
            result.addonRepository = AppConfig.createAddonRepository();
            System.out.println("[KioskController] AddonRepository created: " + (result.addonRepository != null));
            
            // Validate all services
            validateServices(result.reservationService, result.pricingService, result.billingService);
            
            System.out.println("[KioskController] All services initialized successfully");
            
            // Test database connection
            testDatabaseConnection(logger);
            
        } catch (Exception e) {
            System.err.println("[KioskController] CRITICAL ERROR in initialize(): " + e.getMessage());
            e.printStackTrace();
            showInitializationError(e.getMessage());
            throw new RuntimeException("Service initialization failed", e);
        }
        
        return result;
    }
    
    //
     // Result container for service initialization.
//
    public static class ServiceInitResult {
        public EntityManager em;
        public ReservationService reservationService;
        public PricingService pricingService;
        public BillingService billingService;
        public GuestRepository guestRepository;
        public AddonRepository addonRepository;
    }
    
    //
     // Sets up field listeners for occupancy, guest details, dates, and loyalty.
//
    public static void setupFieldListeners(
            TextField numAdultsField,
            TextField numChildrenField,
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            TextField loyaltyNumberField,
            Runnable checkOccupancyFields,
            Runnable validateNameField,
            Runnable validatePhoneField,
            Runnable validateEmailField,
            Runnable updateNightsDisplay,
            Runnable updateNextButtonState,
            Runnable lookupLoyalty) {
        
        // Add focus listeners to occupancy fields for validation on blur
        if (numAdultsField != null) {
            numAdultsField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (wasFocused && !isNowFocused && checkOccupancyFields != null) {
                    checkOccupancyFields.run();
                }
            });
            numAdultsField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        if (numChildrenField != null) {
            numChildrenField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (wasFocused && !isNowFocused && checkOccupancyFields != null) {
                    checkOccupancyFields.run();
                }
            });
            numChildrenField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        
        // Add focus listeners to guest detail fields for validation on blur
        if (nameField != null) {
            nameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (wasFocused && !isNowFocused && validateNameField != null) {
                    validateNameField.run();
                }
            });
            nameField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        if (phoneField != null) {
            phoneField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (wasFocused && !isNowFocused && validatePhoneField != null) {
                    validatePhoneField.run();
                }
            });
            phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        if (emailField != null) {
            emailField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (wasFocused && !isNowFocused && validateEmailField != null) {
                    validateEmailField.run();
                }
            });
            emailField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        if (checkInDatePicker != null) {
            checkInDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNightsDisplay != null) updateNightsDisplay.run();
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        if (checkOutDatePicker != null) {
            checkOutDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (updateNightsDisplay != null) updateNightsDisplay.run();
                if (updateNextButtonState != null) updateNextButtonState.run();
            });
        }
        
        // Auto-lookup loyalty number when field loses focus
        if (loyaltyNumberField != null) {
            loyaltyNumberField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (wasFocused && !isNowFocused && lookupLoyalty != null) {
                    // Field lost focus - automatically perform lookup if there's a value
                    String loyaltyNumber = loyaltyNumberField.getText() != null ? loyaltyNumberField.getText().trim() : "";
                    if (!loyaltyNumber.isEmpty()) {
                        lookupLoyalty.run();
                    }
                }
            });
        }
    }
    
    //
     // Auto-fills guest details from current guest if logged in.
//
    public static void autoFillGuestDetails(
            Guest currentGuest,
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            TextArea addressField,
            LoggerService logger) {
        
        // Auto-fill guest details if customer is logged in and on GuestDetails screen
        if (currentGuest != null && nameField != null && emailField != null) {
            // Customer is logged in and we're on GuestDetails screen - auto-fill
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                nameField.setText(currentGuest.getName() != null ? currentGuest.getName() : "");
            }
            if (phoneField != null && (phoneField.getText() == null || phoneField.getText().trim().isEmpty())) {
                phoneField.setText(currentGuest.getPhone() != null ? currentGuest.getPhone() : "");
            }
            if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
                emailField.setText(currentGuest.getEmail() != null ? currentGuest.getEmail() : "");
            }
            if (addressField != null && (addressField.getText() == null || addressField.getText().trim().isEmpty())) {
                addressField.setText(currentGuest.getAddress() != null ? currentGuest.getAddress() : "");
            }
            logger.logInfo("Auto-filled guest details for logged-in customer: " + currentGuest.getEmail());
        }
    }
}

