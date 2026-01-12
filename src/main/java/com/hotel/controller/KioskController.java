package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.ValidationHelper;
import com.hotel.controller.helper.KioskGuestDetailsHelper;
import com.hotel.controller.helper.KioskRoomSelectionHelper;
import com.hotel.controller.helper.KioskAddOnHelper;
import com.hotel.controller.helper.KioskBookingSummaryHelper;
import com.hotel.controller.helper.KioskConfirmationHelper;
import com.hotel.controller.helper.KioskStateHelper;
import com.hotel.controller.helper.KioskValidationHelper;
import com.hotel.controller.helper.KioskInitializationHelper;
import com.hotel.controller.helper.KioskNavigationHelper;
import com.hotel.controller.helper.KioskNavigationService;
import com.hotel.controller.helper.KioskUIHelper;
import com.hotel.controller.helper.KioskLoyaltyHelper;
import com.hotel.controller.helper.KioskRoomSpinnerHelper;
import com.hotel.controller.helper.KioskValidationService;
import com.hotel.controller.helper.KioskPaymentHelper;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.service.*;
import com.hotel.service.decorator.*;
import com.hotel.session.CustomerSession;
import com.hotel.util.LoggerService;
import com.hotel.config.LoyaltyPolicy;
import com.hotel.util.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.util.Callback;
import java.text.NumberFormat;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

//
 // Main controller for the kiosk self-service booking system.
 // Handles the complete booking flow from date selection to payment confirmation.
 // Manages guest details, room selection, add-ons, pricing, and reservation creation.
//
public class KioskController extends BaseController {
    
    // Services
    private ReservationService reservationService;
    private PricingService pricingService;
    private BillingService billingService;
    private GuestRepository guestRepository;
    private AddonRepository addonRepository;
    private LoyaltyService loyaltyService;
    private LoggerService logger;
    private EntityManager em;
    
    // Booking state (maintained across screens)
    int numAdults = 0;  // Package-private for helper access
    int numChildren = 0;  // Package-private for helper access
    public LocalDate checkIn;
    public LocalDate checkOut;
    public Guest currentGuest;
    List<Room> selectedRooms = new ArrayList<>();  // Package-private for helper access
    List<ServiceAddon> selectedAddons = new ArrayList<>();  // Package-private for helper access
    Reservation createdReservation;
    private int singleRoomCount = 0;
    private int doubleRoomCount = 0;
    private int deluxeRoomCount = 0;
    private int penthouseRoomCount = 0;
    private boolean customSelectionActive = false;
    
    // Navigation history stack for back button (proper history tracking)
    java.util.Stack<String> navigationHistory = new java.util.Stack<>();  // Package-private for helper access
    
    // CRITICAL: Store the last applied state to preserve it even if initialize() resets fields
    private KioskStateHelper.BookingState lastAppliedState = null;
    
    // ========== WelcomeScreen.fxml ==========
    @FXML private VBox instructionalMediaContainer; // Optional instructional video/GIF container
    @FXML private javafx.scene.image.ImageView instructionalGif; // Optional GIF image
    
    // ========== DateSelection.fxml (dates only) ==========
    @FXML private DatePicker checkInDatePicker;
    @FXML private Label checkInErrorLabel;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private Label checkOutErrorLabel;
    @FXML private Label numNightsDisplayLabel;
    @FXML private VBox nightsInfoContainer;
    
    // ========== BookingDetails.fxml (occupancy only) ==========
    @FXML private TextField numAdultsField;
    @FXML private Label adultsErrorLabel;
    @FXML private TextField numChildrenField;
    @FXML private Label childrenErrorLabel;
    
    // ========== GuestDetails.fxml ==========
    @FXML private TextField nameField;
    @FXML private Label nameErrorLabel;
    @FXML private TextField phoneField;
    @FXML private Label phoneErrorLabel;
    @FXML private TextField emailField;
    @FXML private Label emailErrorLabel;
    @FXML private TextArea addressField;
    // Loyalty lookup
    @FXML private TextField loyaltyNumberField;
    @FXML private Label loyaltyLookupLabel;
    @FXML private VBox loyaltyEnrollmentContainer; // For GuestDetails enrollment option
    @FXML private VBox loyaltyEnrolledDisplayContainer; // For showing enrolled status
    @FXML private Label loyaltyEnrolledLabel; // For showing loyalty number/points
    
    // ========== RoomSelection.fxml ==========
    @FXML private VBox suggestedPlanContainer;
    @FXML private TableView<ReservationService.RoomSuggestion> suggestedRoomsTable;
    @FXML private VBox policyWarningContainer;
    @FXML private TableColumn<ReservationService.RoomSuggestion, String> roomTypeColumn;
    @FXML private TableColumn<ReservationService.RoomSuggestion, String> capacityColumn;
    @FXML private TableColumn<ReservationService.RoomSuggestion, Integer> quantityColumn;
    @FXML private TableColumn<ReservationService.RoomSuggestion, Double> pricePerNightColumn;
    @FXML private VBox customSelectionContainer;
    @FXML private HBox singleRoomRow;
    @FXML private HBox doubleRoomRow;
    @FXML private HBox deluxeRoomRow;
    @FXML private HBox penthouseRoomRow;
    @FXML private Spinner<Integer> singleRoomSpinner;
    @FXML private Spinner<Integer> doubleRoomSpinner;
    @FXML private Spinner<Integer> deluxeRoomSpinner;
    @FXML private Spinner<Integer> penthouseSpinner;
    @FXML private Label occupancyValidationLabel;
    @FXML private Label selectedRoomsSummaryLabel;
    @FXML private TableView<Room> availableRoomsTable;
    @FXML private TableColumn<Room, String> availableRoomNumberColumn;
    @FXML private TableColumn<Room, String> availableRoomTypeColumn;
    @FXML private TableColumn<Room, Double> availableRoomPriceColumn;
    
    // ========== AddOnServices.fxml ==========
    @FXML private CheckBox wifiCheckBox;
    @FXML private CheckBox breakfastCheckBox;
    @FXML private CheckBox parkingCheckBox;
    @FXML private CheckBox spaCheckBox;
    @FXML private Label addOnTotalLabel;
    // Individual add-on price labels
    @FXML private Label wifiPriceLabel;
    @FXML private Label breakfastPriceLabel;
    @FXML private Label parkingPriceLabel;
    @FXML private Label spaPriceLabel;
    // Individual add-on calculation labels
    @FXML private Label wifiCalculationLabel;
    @FXML private Label breakfastCalculationLabel;
    @FXML private Label parkingCalculationLabel;
    @FXML private Label spaCalculationLabel;
    
    // ========== BookingSummary.fxml ==========
    @FXML private Label guestNameLabel;
    @FXML private Label guestPhoneLabel;
    @FXML private Label guestEmailLabel;
    @FXML private Label checkInLabel;
    @FXML private Label checkOutLabel;
    @FXML private Label numNightsLabel;
    @FXML private Label occupancyLabel;
    @FXML private Label assignedRoomsLabel;
    @FXML private VBox roomBreakdownContainer;
    @FXML private Label roomBreakdownTitleLabel;
    @FXML private VBox roomBreakdownList;
    @FXML private VBox addonBreakdownContainer;
    @FXML private VBox addonBreakdownList;
    @FXML private Label roomSubtotalLabel;
    @FXML private Label addOnSubtotalLabel;
    @FXML private Label taxRateLabel;
    @FXML private Label taxAmountLabel;
    @FXML private VBox discountContainer;
    @FXML private Label discountLabel;
    @FXML private VBox loyaltyContainer;
    @FXML private Label loyaltyLabel;
    @FXML private Label totalAmountLabel;
    
    // ========== ConfirmationScreen.fxml ==========
    @FXML private Label reservationNumberLabel; // Keep for backward compatibility
    @FXML private Label bookingIdLabel;
    @FXML private Label reservationIdLabel;
    @FXML private Label statusLabel;
    @FXML private HBox reservationIdRow;
    @FXML private Label confirmationTitleLabel;
    @FXML private Label bookingDetailsLabel;
    @FXML private Label billingMessageLabel;
    @FXML private Button feedbackButton;
    // Loyalty enrollment (ConfirmationScreen uses different containers)
    @FXML private CheckBox enrollLoyaltyCheckBox;
    @FXML private VBox loyaltyEnrolledContainer; // ConfirmationScreen specific
    @FXML private Label loyaltyNumberLabel;
    @FXML private Label loyaltyPointsLabel;
    
    // Navigation buttons (for enabling/disabling)
    @FXML private Button nextButtonOccupancy;
    @FXML private Button nextButtonDates;
    @FXML private Button nextButtonGuest;
    @FXML private Button nextButtonRoomSelection;
    @FXML private Button nextButtonAddOns;
    
    @FXML
    private void initialize() {
        logger = LoggerService.getInstance();
        
        // Initialize all services
        KioskInitializationHelper.ServiceInitResult services = 
            KioskInitializationHelper.initializeServices(logger);
        
        // Assign services to instance variables
        em = services.em;
        reservationService = services.reservationService;
        pricingService = services.pricingService;
        billingService = services.billingService;
        guestRepository = services.guestRepository;
        addonRepository = services.addonRepository;
        
        // Initialize spinners
        KioskInitializationHelper.initializeRoomSpinners(
            singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner);
        
        // Set up table columns if on room selection screen
        if (suggestedRoomsTable != null) {
            logger.logInfo("Initializing table columns in initialize()");
            setupTableColumns();
        } else {
            logger.logInfo("suggestedRoomsTable is null in initialize() - not on room selection screen");
        }
        
        // Initialize add-on checkboxes to update total when changed (only if on add-ons screen)
        if (wifiCheckBox != null || breakfastCheckBox != null || parkingCheckBox != null || spaCheckBox != null) {
            logger.logInfo("Setting up add-on checkbox listeners");
            KioskInitializationHelper.setupAddOnCheckboxListeners(
                wifiCheckBox, breakfastCheckBox, parkingCheckBox, spaCheckBox,
                this::updateAddOnTotal, logger);
        }
        
        // Initialize add-on total label
        if (addOnTotalLabel != null) {
            addOnTotalLabel.setText("$0.00");
            logger.logInfo("Add-on total label initialized to $0.00");
        } else {
            logger.logInfo("addOnTotalLabel is null");
        }
        
        // NOTE: Do NOT call loadBookingSummary() or loadConfirmation() here
        // They will be called AFTER state transfer in navigateToScreen()
        // initialize() runs before state transfer, so data would be null/empty
        
        // Hide error labels initially
        hideAllErrors();
        
        if (currentGuest == null) {
            currentGuest = KioskInitializationHelper.getGuestFromSession();
        }
        
        // Auto-fill guest details if customer is logged in
        KioskInitializationHelper.autoFillGuestDetails(
            currentGuest, nameField, phoneField, emailField, addressField, logger);
        
        // Prepare room spinners (if on RoomSelection screen)
        initializeRoomSpinners();
        
        // Add listeners to room spinners for validation
        KioskInitializationHelper.setupRoomSpinnerListeners(
            singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner,
            () -> {
                updateSelectedRoomsSummary();
                updateNextButtonState();
            });
        
        // NOTE: Do NOT call populateFieldsFromState() here
        // It will be called AFTER state transfer in navigateToScreen()
        // Calling it here would populate fields with null/empty values before state is transferred
        
        // If on room selection screen, load available rooms to set spinner limits
        if (singleRoomSpinner != null || suggestedRoomsTable != null) {
            if (checkIn != null && checkOut != null) {
                loadAvailableRooms();
            }
        }
        
        // Set up field listeners (occupancy, guest details, dates, loyalty)
        KioskInitializationHelper.setupFieldListeners(
            numAdultsField, numChildrenField, nameField, phoneField, emailField,
            checkInDatePicker, checkOutDatePicker, loyaltyNumberField,
            this::checkOccupancyFields,
            this::validateNameField,
            this::validatePhoneField,
            this::validateEmailField,
            () -> {
                if (checkInDatePicker != null) checkIn = checkInDatePicker.getValue();
                if (checkOutDatePicker != null) checkOut = checkOutDatePicker.getValue();
                updateNightsDisplay();
            },
            this::updateNextButtonState,
            this::lookupLoyalty);
        
        // Update nights display if dates are already set
        updateNightsDisplay();
        
        // Initially disable Next buttons
        updateNextButtonState();
    }
    
    private void updateNextButtonState() {
        KioskValidationHelper.updateNextButtonState(
            numAdultsField, numChildrenField, checkInDatePicker, nameField,
            nextButtonOccupancy, nextButtonDates, nextButtonGuest, 
            nextButtonRoomSelection, nextButtonAddOns,
            this::validateOccupancyFields,
            this::validateDateFields,
            this::validateGuestFields,
            this::validateRoomSelectionFields);
    }
    
    private boolean validateRoomSelectionFields() {
        return KioskValidationHelper.validateRoomSelectionFields(
            customSelectionActive, customSelectionContainer, suggestedPlanContainer,
            singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner);
    }
    
    private boolean validateOccupancyFields() {
        return KioskValidationHelper.validateOccupancyFields(numAdultsField);
    }
    
    private boolean validateDateFields() {
        return KioskValidationHelper.validateDateFields(checkInDatePicker, checkOutDatePicker);
    }
    
    private boolean validateGuestFields() {
        return KioskValidationHelper.validateGuestFields(nameField, phoneField, emailField);
    }
    
    // ========== Navigation Methods ==========
    @FXML
    @Override
    protected void goBack() {
        // Navigate back using navigation history stack
        if (!navigationHistory.isEmpty()) {
            String previousScreen = navigationHistory.pop();
            // Don't add to history when going back - we're removing from history
            navigateToScreenWithoutHistory(previousScreen);
        } else {
            // Fallback: determine current screen and go back
            if (emailField != null && nameField != null) {
                // We're on GuestDetails, go back to KioskWelcome
                navigateToKioskWelcome();
            } else if (singleRoomSpinner != null || suggestedRoomsTable != null) {
                // We're on RoomSelection, go back to GuestDetails (preserve state)
                navigateToScreenWithoutHistory("/view/kiosk/GuestDetails.fxml");
            } else if (wifiCheckBox != null) {
                // We're on AddOnServices, go back to RoomSelection (preserve state)
                navigateToScreenWithoutHistory("/view/kiosk/RoomSelection.fxml");
            } else if (guestNameLabel != null && totalAmountLabel != null) {
                // We're on BookingSummary, go back to AddOnServices (preserve state)
                navigateToScreenWithoutHistory("/view/kiosk/AddOnServices.fxml");
            } else {
                // Default: go to kiosk welcome
                navigateToKioskWelcome();
            }
        }
    }
    
    private void navigateToKioskWelcome() {
        KioskNavigationService.navigateToKioskWelcome(
            logger,
            this::getCurrentStage,
            (stage) -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/KioskWelcome.fxml"));
                    Parent root = loader.load();
                    stage.setScene(new Scene(root, 1200, 800));
                } catch (Exception e) {
                    logger.logError("Failed to load KioskWelcome", e);
                }
            }
        );
    }
    
    private void navigateToScreenWithoutHistory(String fxmlPath) {
        KioskNavigationService.navigateToScreenWithoutHistory(
            fxmlPath,
            this::captureCurrentStateFromUI,
            () -> KioskStateHelper.createStateSnapshot(
                numAdults, numChildren, checkIn, checkOut, currentGuest,
                selectedRooms, selectedAddons, createdReservation,
                singleRoomCount, doubleRoomCount, deluxeRoomCount, penthouseRoomCount,
                customSelectionActive, navigationHistory),
            this::applyStateToFields,
            this::populateFieldsFromState,
            (path) -> KioskNavigationHelper.handlePostNavigationLoading(
                path,
                guestNameLabel, totalAmountLabel, reservationNumberLabel,
                singleRoomSpinner, suggestedRoomsTable,
                this::loadBookingSummary,
                this::loadConfirmation,
                this::loadAvailableRooms,
                this::refreshCustomSelectionUI,
                this::updateSelectedRoomsSummary,
                logger),
            logger,
            this::getCurrentStage,
            (stage) -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent root = loader.load();
                    stage.setScene(new Scene(root, 1200, 800));
                } catch (Exception e) {
                    logger.logError("Failed to load scene", e);
                }
            }
        );
    }
    
    @FXML
    private void showRules() {
        KioskUIHelper.showRules();
    }
    
    // ========== Welcome Methods ==========
    @FXML
    private void startBooking(ActionEvent event) {
        // Reset booking state
        resetBookingState();
        navigationHistory.clear();
        // Navigate to BookingDetails (occupancy step) - first step in required flow
        navigateToScreen("/view/kiosk/BookingDetails.fxml", event);
    }
    
    @FXML
    private void goBackFromWelcome() {
        KioskNavigationService.goBackFromWelcome(logger, this::getCurrentStage);
    }

    public void populateFieldsFromState() {  // Public for helper service access
        // Log selectedRooms before populating fields
        logger.logInfo("populateFieldsFromState: selectedRooms size before: " + 
            (selectedRooms != null ? selectedRooms.size() : "null") +
            ", checkIn: " + checkIn + ", checkOut: " + checkOut);
        
        // CRITICAL: Use the last applied state object instead of controller fields
        // This ensures we get the correct state even if initialize() reset the fields
        KioskStateHelper.BookingState stateToUse = lastAppliedState;
        
        if (stateToUse != null) {
            logger.logInfo("populateFieldsFromState: Using lastAppliedState - selectedRooms: " + 
                (stateToUse.selectedRooms != null ? stateToUse.selectedRooms.size() : "null") +
                ", checkIn: " + stateToUse.checkIn + ", checkOut: " + stateToUse.checkOut);
            
            // Restore state from the stored state object
            selectedRooms = stateToUse.selectedRooms != null ? new ArrayList<>(stateToUse.selectedRooms) : new ArrayList<>();
            checkIn = stateToUse.checkIn;
            checkOut = stateToUse.checkOut;
            currentGuest = stateToUse.currentGuest;
            numAdults = stateToUse.numAdults;
            numChildren = stateToUse.numChildren;
            selectedAddons = stateToUse.selectedAddons != null ? new ArrayList<>(stateToUse.selectedAddons) : new ArrayList<>();
            singleRoomCount = stateToUse.singleRoomCount;
            doubleRoomCount = stateToUse.doubleRoomCount;
            deluxeRoomCount = stateToUse.deluxeRoomCount;
            penthouseRoomCount = stateToUse.penthouseRoomCount;
            customSelectionActive = stateToUse.customSelectionActive;
            
            logger.logInfo("populateFieldsFromState: Restored state from lastAppliedState - selectedRooms: " + 
                selectedRooms.size() + ", checkIn: " + checkIn + ", checkOut: " + checkOut);
        } else {
            logger.logWarning("populateFieldsFromState: lastAppliedState is null - using controller fields");
        }
        
        // Only populate UI fields - don't modify state variables
        KioskStateHelper.populateFieldsFromState(
            currentGuest, checkIn, checkOut, numAdults, numChildren,
            nameField, phoneField, emailField, addressField,
            checkInDatePicker, checkOutDatePicker, numAdultsField, numChildrenField,
            numNightsDisplayLabel, nightsInfoContainer);
        
        // Additional field population (spinners, checkboxes, loyalty)
        KioskStateHelper.populateAdditionalFieldsFromState(
            singleRoomCount, doubleRoomCount, deluxeRoomCount, penthouseRoomCount,
            selectedAddons, currentGuest,
            singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner,
            wifiCheckBox, breakfastCheckBox, parkingCheckBox, spaCheckBox,
            loyaltyNumberField, () -> { lookupLoyalty(); return null; });
        updateNightsDisplay();
        updateNextButtonState();
        // Only update add-on total if we're on the add-ons screen AND dates are set
        if (addOnTotalLabel != null && checkIn != null && checkOut != null) {
            updateAddOnTotal();
        } else if (addOnTotalLabel != null && (checkIn == null || checkOut == null)) {
            logger.logWarning("populateFieldsFromState: Skipping updateAddOnTotal - dates not set (checkIn: " + 
                checkIn + ", checkOut: " + checkOut + ")");
        }
        
        // Log selectedRooms after populating fields
        logger.logInfo("populateFieldsFromState: selectedRooms size after: " + 
            (selectedRooms != null ? selectedRooms.size() : "null") +
            ", checkIn: " + checkIn + ", checkOut: " + checkOut);
        
        // Check and display loyalty enrollment option in GuestDetails if not enrolled
        checkLoyaltyEnrollmentStatus();
    }
    
    private void checkLoyaltyEnrollmentStatus() {
        KioskGuestDetailsHelper.checkLoyaltyEnrollmentStatus(
            currentGuest, loyaltyEnrollmentContainer, loyaltyEnrolledDisplayContainer, loyaltyEnrolledLabel);
    }
    
    @FXML
    private void enrollInLoyaltyFromGuestDetails() {
        KioskLoyaltyHelper.enrollInLoyaltyFromGuestDetails(
            currentGuest, guestRepository, logger, this::checkLoyaltyEnrollmentStatus);
    }


    @FXML
    private void returnToKioskWelcome(ActionEvent event) {
        KioskNavigationService.returnToKioskWelcome(
            navigationHistory, logger, () -> getCurrentStageFromEvent(event));
    }
    
    // ========== Occupancy Methods (Booking Details page) ==========
    @FXML
    private void checkOccupancyFields() {
        // Update Next button state as user types
        updateNextButtonState();
        
        // Also validate and show errors
        validateOccupancyFieldsForDisplay();
    }
    
    @FXML
    private void validateOccupancyAndProceed() {
        hideAllErrors();
        KioskValidationService.validateOccupancyAndProceed(
            numAdultsField, numChildrenField, adultsErrorLabel, childrenErrorLabel,
            (val) -> numAdults = val,
            (val) -> numChildren = val,
            () -> navigateToScreen("/view/kiosk/DateSelection.fxml"));
    }
    
    private void validateOccupancyFieldsForDisplay() {
        KioskGuestDetailsHelper.validateOccupancyFieldsForDisplay(
            numAdultsField, numChildrenField, adultsErrorLabel, childrenErrorLabel);
    }
    
    // ========== Date Methods ==========
    
    // Individual date validation (called when date picker value changes)
    @FXML
    private void validateCheckInDate() {
        checkIn = KioskGuestDetailsHelper.validateCheckInDate(
            checkInDatePicker, checkOut, checkInErrorLabel, checkOutErrorLabel);
        updateNightsDisplay();
        updateNextButtonState();
    }
    
    @FXML
    private void validateCheckOutDate() {
        checkOut = KioskGuestDetailsHelper.validateCheckOutDate(
            checkOutDatePicker, checkIn, checkOutErrorLabel);
        updateNightsDisplay();
        updateNextButtonState();
    }
    
    private void updateNightsDisplay() {
        KioskGuestDetailsHelper.updateNightsDisplay(
            checkIn, checkOut, numNightsDisplayLabel, nightsInfoContainer);
    }
    
    // Full validation (called when clicking Next button) - validates both occupancy and dates
    @FXML
    private void validateDates() {
        hideAllErrors();
        KioskValidationService.validateDates(
            checkInDatePicker, checkOutDatePicker, reservationService,
            checkInErrorLabel, checkOutErrorLabel, numNightsDisplayLabel, nightsInfoContainer,
            (val) -> checkIn = val,
            (val) -> checkOut = val,
            this::navigateToRoomSelection);
    }
    
    @FXML
    private void validateDatesAndProceed() {
        validateDates();
    }
    
    // ========== Guest Details Methods ==========
    
    // Individual field validation (called on focus lost)
    // Uses ValidationHelper for validation logic
    @FXML
    private void validateNameField() {
        ValidationHelper.validateName(nameField, nameErrorLabel);
        updateNextButtonState();
    }
    
    @FXML
    private void validatePhoneField() {
        ValidationHelper.validatePhone(phoneField, phoneErrorLabel);
        updateNextButtonState();
    }
    
    @FXML
    private void validateEmailField() {
        ValidationHelper.validateEmail(emailField, emailErrorLabel);
        updateNextButtonState();
    }
    
    // Full validation (called when clicking Next button)
    // This method validates occupancy, dates, AND contact information (all-in-one form)
    @FXML
    private void validateGuestDetails() {
        hideAllErrors();
        currentGuest = KioskValidationService.validateGuestDetailsAndProcess(
            numAdultsField, numChildrenField, checkInDatePicker, checkOutDatePicker,
            nameField, phoneField, emailField, addressField,
            adultsErrorLabel, childrenErrorLabel, checkInErrorLabel, checkOutErrorLabel,
            nameErrorLabel, phoneErrorLabel, emailErrorLabel,
            guestRepository, reservationService, logger,
            (val) -> numAdults = val,
            (val) -> numChildren = val,
            (val) -> checkIn = val,
            (val) -> checkOut = val,
            this::navigateToRoomSelection);
    }
    
    private void navigateToRoomSelection() {
        if (!validateBeforeRoomSelection()) {
            return;
        }
        
        // Get room suggestions
        List<ReservationService.RoomSuggestion> suggestionsList = 
            reservationService.suggestRooms(numAdults, numChildren, checkIn, checkOut);
        
        if (suggestionsList == null) {
            logger.logError("suggestRooms() returned null");
            suggestionsList = new ArrayList<>();
        }
        
        // Make final for lambda
        final List<ReservationService.RoomSuggestion> suggestions = suggestionsList;
        
        logger.logInfo("Got " + suggestions.size() + " room suggestions");
        
        KioskNavigationService.navigateToRoomSelection(
            numAdults, numChildren, checkIn, checkOut, currentGuest,
            selectedRooms, selectedAddons,
            singleRoomCount, doubleRoomCount, deluxeRoomCount, penthouseRoomCount,
            customSelectionActive, navigationHistory,
            reservationService, logger,
            this::getCurrentStage,
            (controller) -> {
                controller.setBookingState(numAdults, numChildren, checkIn, checkOut, currentGuest);
                controller.singleRoomCount = this.singleRoomCount;
                controller.doubleRoomCount = this.doubleRoomCount;
                controller.deluxeRoomCount = this.deluxeRoomCount;
                controller.penthouseRoomCount = this.penthouseRoomCount;
                controller.customSelectionActive = this.customSelectionActive;
                controller.selectedRooms = new ArrayList<>(this.selectedRooms);
                controller.selectedAddons = new ArrayList<>(this.selectedAddons);
                // Set navigation history
                controller.navigationHistory = new java.util.Stack<>();
                controller.navigationHistory.addAll(this.navigationHistory);
                controller.loadRoomSuggestions(suggestions);
                controller.loadAvailableRooms();
                controller.refreshCustomSelectionUI();
                controller.updateSelectedRoomsSummary();
            },
            () -> determineCurrentScreen()
        );
    }
    
    //
     // Validate state before navigating to room selection
//
    private boolean validateBeforeRoomSelection() {
        return KioskValidationService.validateBeforeRoomSelection(
            checkIn, checkOut, numAdults, reservationService, logger);
    }
    
    // ========== Room Selection Methods ==========
    public void setBookingState(int adults, int children, LocalDate in, LocalDate out, Guest guest) {
        // Validate parameters
        if (adults <= 0) {
            logger.logWarning("setBookingState called with invalid adults: " + adults);
            throw new IllegalArgumentException("Number of adults must be positive");
        }
        if (in == null || out == null) {
            logger.logWarning("setBookingState called with null dates");
            throw new IllegalArgumentException("Dates cannot be null");
        }
        
        this.numAdults = adults;
        this.numChildren = children;
        this.checkIn = in;
        this.checkOut = out;
        this.currentGuest = guest;
        
        logger.logInfo("Booking state set: " + adults + " adults, " + children + " children, " + 
                      "check-in: " + in + ", check-out: " + out);
    }
    
    public void loadRoomSuggestions(List<ReservationService.RoomSuggestion> suggestions) {
        // Null check
        if (suggestions == null) {
            logger.logError("Suggestions list is null, using empty list");
            suggestions = new ArrayList<>();
        }
        
        // Log suggestions
        KioskRoomSelectionHelper.logRoomSuggestions(
            suggestions, suggestedRoomsTable, roomTypeColumn, quantityColumn, pricePerNightColumn, logger);
        
        // Check if table exists
        if (suggestedRoomsTable == null) {
            return;
        }
        
        // Set up table columns first
        setupTableColumns();
        
        // Load suggestions into table using helper
        KioskRoomSelectionHelper.loadRoomSuggestionsIntoTable(
            suggestions, suggestedRoomsTable, suggestedPlanContainer, customSelectionContainer,
            checkIn, checkOut, reservationService, logger);
        
        logger.logInfo("=== loadRoomSuggestions completed ===");
    }
    
    private void setupTableColumns() {
        KioskRoomSelectionHelper.setupTableColumns(
            suggestedRoomsTable, roomTypeColumn, capacityColumn, 
            quantityColumn, pricePerNightColumn, logger);
    }
    
    public void loadAvailableRooms() {
        if (checkIn == null || checkOut == null) {
            return;
        }
        
        try {
            KioskRoomSelectionHelper.RoomCounts counts = 
                KioskRoomSelectionHelper.getAvailableRoomCounts(checkIn, checkOut, reservationService);
            
            applyRoomSpinnerLimits(singleRoomSpinner, singleRoomRow, counts.singleCount, singleRoomCount);
            applyRoomSpinnerLimits(doubleRoomSpinner, doubleRoomRow, counts.doubleCount, doubleRoomCount);
            applyRoomSpinnerLimits(deluxeRoomSpinner, deluxeRoomRow, counts.deluxeCount, deluxeRoomCount);
            applyRoomSpinnerLimits(penthouseSpinner, penthouseRoomRow, counts.penthouseCount, penthouseRoomCount);
            
            updateSelectedRoomsSummary();
        } catch (Exception e) {
            logger.logError("Failed to load available rooms", e);
        }
    }

    private void initializeRoomSpinners() {
        setupRoomSpinner(singleRoomSpinner, singleRoomRow, value -> singleRoomCount = value);
        setupRoomSpinner(doubleRoomSpinner, doubleRoomRow, value -> doubleRoomCount = value);
        setupRoomSpinner(deluxeRoomSpinner, deluxeRoomRow, value -> deluxeRoomCount = value);
        setupRoomSpinner(penthouseSpinner, penthouseRoomRow, value -> penthouseRoomCount = value);
    }

    private void setupRoomSpinner(Spinner<Integer> spinner, HBox row, IntConsumer valueConsumer) {
        KioskRoomSpinnerHelper.setupRoomSpinner(
            spinner, row, valueConsumer,
            (r) -> updateRoomRowHighlight(r, 0), // Will be updated by listener
            this::updateSelectedRoomsSummary);
    }

    private void applyRoomSpinnerLimits(Spinner<Integer> spinner, HBox row, int available, int storedValue) {
        KioskRoomSelectionHelper.applyRoomSpinnerLimits(
            spinner, row, available, storedValue, logger);
        updateRoomRowHighlight(row, storedValue);
    }

    private void updateRoomRowHighlight(HBox row, int count) {
        KioskRoomSelectionHelper.updateRoomRowHighlight(row, count, customSelectionActive);
    }

    public void refreshCustomSelectionUI() {  // Public for helper service access
        boolean showCustom = customSelectionActive;
        if (customSelectionContainer != null) {
            customSelectionContainer.setVisible(showCustom);
            customSelectionContainer.setManaged(showCustom);
        }
        if (!showCustom && selectedRoomsSummaryLabel != null) {
            selectedRoomsSummaryLabel.setVisible(false);
            selectedRoomsSummaryLabel.setManaged(false);
            selectedRoomsSummaryLabel.getStyleClass().remove("active");
        }
        updateRoomRowHighlight(singleRoomRow, singleRoomCount);
        updateRoomRowHighlight(doubleRoomRow, doubleRoomCount);
        updateRoomRowHighlight(deluxeRoomRow, deluxeRoomCount);
        updateRoomRowHighlight(penthouseRoomRow, penthouseRoomCount);
        if (showCustom) {
            updateSelectedRoomsSummary();
        }
    }

    private void clearCustomRoomSelection() {
        KioskRoomSpinnerHelper.clearCustomRoomSelection(
            singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner,
            (val) -> singleRoomCount = val,
            (val) -> doubleRoomCount = val,
            (val) -> deluxeRoomCount = val,
            (val) -> penthouseRoomCount = val,
            this::updateSelectedRoomsSummary);
    }

    private void setSpinnerValue(Spinner<Integer> spinner, int value) {
        KioskRoomSpinnerHelper.setSpinnerValue(spinner, value);
    }
    
    public void updateSelectedRoomsSummary() {  // Public for helper service access
        KioskRoomSelectionHelper.updateSelectedRoomsSummary(
            selectedRoomsSummaryLabel, customSelectionActive,
            singleRoomCount, doubleRoomCount, deluxeRoomCount, penthouseRoomCount);
    }
    
    @FXML
    private void acceptSuggestion() {
        // Get selected row from table
        ReservationService.RoomSuggestion selectedSuggestion = null;
        if (suggestedRoomsTable != null) {
            selectedSuggestion = suggestedRoomsTable.getSelectionModel().getSelectedItem();
        }
        
        // If no row selected, show error
        if (selectedSuggestion == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a room type from the table");
            alert.setContentText("Click on a row in the table to select it, then click 'Accept Suggestion'.");
            alert.showAndWait();
            return;
        }
        
        // Process accepted suggestion using helper
        selectedRooms = KioskRoomSelectionHelper.processAcceptedSuggestion(
            selectedSuggestion, checkIn, checkOut, reservationService, logger);
        
        // Log the selected rooms to verify they were set correctly
        logger.logInfo("acceptSuggestion: selectedRooms set to " + 
            (selectedRooms != null ? selectedRooms.size() : "null") + " room(s)");
        if (selectedRooms != null && !selectedRooms.isEmpty()) {
            logger.logInfo("acceptSuggestion: Room types selected: " + 
                selectedRooms.stream()
                    .map(r -> r.getType().toString())
                    .collect(java.util.stream.Collectors.joining(", ")));
        }
        
        customSelectionActive = false;
        clearCustomRoomSelection();
        refreshCustomSelectionUI();
        
        // Log state before navigation to verify it's set correctly
        logger.logInfo("acceptSuggestion: About to navigate - selectedRooms: " + 
            (selectedRooms != null ? selectedRooms.size() : "null") + 
            ", checkIn: " + checkIn + ", checkOut: " + checkOut + 
            ", currentGuest: " + (currentGuest != null ? currentGuest.getName() : "null"));
        
        // Proceed to add-on services (after room selection)
        navigateToAddOns();
    }
    
    @FXML
    private void chooseCustom() {
        // Show custom selection but keep suggestions visible
        customSelectionActive = true;
        if (customSelectionContainer != null) {
            customSelectionContainer.setVisible(true);
            customSelectionContainer.setManaged(true);
        }
        // Load available rooms to update spinner limits
        loadAvailableRooms();
        refreshCustomSelectionUI();
        updateSelectedRoomsSummary();
    }
    
    @FXML
    private void validateRoomSelection() {
        hideAllErrors();
        
        // Validate using helper
        KioskRoomSelectionHelper.RoomSelectionResult result = 
            KioskRoomSelectionHelper.validateCustomRoomSelection(
                singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner,
                checkIn, checkOut, reservationService, numAdults, numChildren, logger);
        
        if (!result.isValid) {
            showError(occupancyValidationLabel, result.errorMessage);
            return;
        }
        
        selectedRooms = result.selectedRooms;
        // Proceed to add-on services (after room selection)
        navigateToAddOns();
    }
    
    @FXML
    private void showBookingPolicy() {
        showRules();
    }
    
    // ========== Add-On Methods ==========
    private void navigateToAddOns() {
        // CRITICAL: If selectedRooms is empty but we have a selected suggestion, auto-accept it
        if ((selectedRooms == null || selectedRooms.isEmpty()) && suggestedRoomsTable != null) {
            ReservationService.RoomSuggestion selectedSuggestion = 
                suggestedRoomsTable.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                logger.logInfo("navigateToAddOns: selectedRooms is empty but suggestion is selected - auto-accepting");
                // Process the suggestion
                selectedRooms = KioskRoomSelectionHelper.processAcceptedSuggestion(
                    selectedSuggestion, checkIn, checkOut, reservationService, logger);
                logger.logInfo("navigateToAddOns: Auto-accepted suggestion - selectedRooms size: " + 
                    (selectedRooms != null ? selectedRooms.size() : "null"));
            } else {
                logger.logWarning("navigateToAddOns: selectedRooms is empty and no suggestion is selected - cannot proceed");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Room Selected");
                alert.setHeaderText("Please select a room");
                alert.setContentText("You must select a room suggestion from the table and click 'Accept Suggestion' before proceeding.");
                alert.showAndWait();
                return; // Don't navigate if no rooms selected
            }
        }
        
        // Log state before navigation
        logger.logInfo("navigateToAddOns: Navigating with selectedRooms: " + 
            (selectedRooms != null ? selectedRooms.size() : "null") +
            ", checkIn: " + checkIn + ", checkOut: " + checkOut);
        
        navigateToScreen("/view/kiosk/AddOnServices.fxml");
    }
    
    @FXML
    private void updateAddOnTotal() {
        logger.logInfo("updateAddOnTotal called");
        selectedAddons.clear();
        
        try {
            // Calculate using helper
            KioskAddOnHelper.AddOnCalculationResult result = 
                KioskAddOnHelper.calculateAddOnTotal(
                    wifiCheckBox, breakfastCheckBox, parkingCheckBox, spaCheckBox,
                    checkIn, checkOut, addonRepository, logger);
            
            selectedAddons = result.selectedAddons;
            
            // Reset individual price labels
            resetAddOnPriceLabels();
            
            // Calculate and display individual add-on prices
            for (ServiceAddon addon : selectedAddons) {
                KioskAddOnHelper.AddOnPriceInfo info = 
                    KioskAddOnHelper.getAddOnPriceInfo(addon, checkIn, checkOut);
                
                // Update individual price labels
                updateIndividualAddOnPrice(addon.getName(), info.price, info.calculationText);
            }
            
            if (addOnTotalLabel != null) {
                addOnTotalLabel.setText("$" + String.format("%.2f", result.total));
                logger.logInfo("Updated addOnTotalLabel to: $" + String.format("%.2f", result.total));
            } else {
                logger.logWarning("addOnTotalLabel is null!");
            }
            
        } catch (Exception e) {
            logger.logError("Failed to update addon total", e);
            if (addOnTotalLabel != null) {
                addOnTotalLabel.setText("$0.00");
            }
        }
    }
    
    //
     // Reset all individual add-on price labels
//
    private void resetAddOnPriceLabels() {
        KioskAddOnHelper.resetAddOnPriceLabels(
            wifiPriceLabel, breakfastPriceLabel, parkingPriceLabel, spaPriceLabel,
            wifiCalculationLabel, breakfastCalculationLabel, parkingCalculationLabel, spaCalculationLabel);
    }
    
    //
     // Update individual add-on price label
//
    private void updateIndividualAddOnPrice(String addonName, double price, String calculationText) {
        KioskAddOnHelper.updateIndividualAddOnPrice(
            addonName, price, calculationText,
            wifiPriceLabel, breakfastPriceLabel, parkingPriceLabel, spaPriceLabel,
            wifiCalculationLabel, breakfastCalculationLabel, parkingCalculationLabel, spaCalculationLabel);
    }
    
    @FXML
    private void proceedToSummary() {
        // Log selectedRooms before proceeding
        logger.logInfo("proceedToSummary: selectedRooms size before updateAddOnTotal: " + 
            (selectedRooms != null ? selectedRooms.size() : "null"));
        
        updateAddOnTotal();
        
        // Log selectedRooms after updateAddOnTotal
        logger.logInfo("proceedToSummary: selectedRooms size after updateAddOnTotal: " + 
            (selectedRooms != null ? selectedRooms.size() : "null"));
        
        KioskValidationService.validateBeforeSummary(
            currentGuest, selectedRooms, logger,
            () -> navigateToScreen("/view/kiosk/BookingSummary.fxml"));
        // loadBookingSummary() will be called in initialize() when screen loads
    }
    
    // ========== Booking Summary Methods ==========
    private void loadBookingSummary() {
        // Log booking state
        KioskBookingSummaryHelper.logBookingState(
            currentGuest, selectedRooms, selectedAddons, checkIn, checkOut, numAdults, numChildren, logger);
        
        if (currentGuest == null) {
            logger.logWarning("currentGuest is null - cannot load booking summary");
            return;
        }
        if (selectedRooms == null || selectedRooms.isEmpty()) {
            logger.logWarning("selectedRooms is null or empty - cannot load booking summary");
            return;
        }
        if (checkIn == null || checkOut == null) {
            logger.logWarning("checkIn or checkOut is null - cannot load booking summary");
            return;
        }
        
        try {
            // Calculate booking summary using helper
            KioskBookingSummaryHelper.BookingSummaryCalculation calc = 
                KioskBookingSummaryHelper.calculateBookingSummary(
                    selectedRooms, selectedAddons, checkIn, checkOut, pricingService, logger);
            
            // Update UI using helper
            KioskBookingSummaryHelper.updateBookingSummaryUI(
                calc, currentGuest, checkIn, checkOut, numAdults, numChildren,
                guestNameLabel, guestPhoneLabel, guestEmailLabel,
                checkInLabel, checkOutLabel, numNightsLabel, occupancyLabel,
                roomSubtotalLabel, addOnSubtotalLabel, taxRateLabel, taxAmountLabel, totalAmountLabel,
                logger);
            
            // Update assigned rooms label using helper
            KioskBookingSummaryHelper.updateAssignedRoomsLabelInSummary(
                createdReservation, selectedRooms, assignedRoomsLabel);
            
            displayAddOnBreakdown();
            
            // Calculate and display loyalty effects
            calculateAndDisplayLoyaltyEffects(calc.subtotal, calc.taxAmount, calc.total);
            
        } catch (Exception e) {
            logger.logError("Failed to load booking summary", e);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void payNow() {
        createdReservation = KioskPaymentHelper.prepareReservationForPayment(
            createdReservation, currentGuest, selectedRooms, selectedAddons,
            checkIn, checkOut, numAdults, numChildren,
            reservationService, billingService, pricingService, logger);
        
        if (createdReservation != null) {
            // Navigate to payment screen
            navigateToPaymentScreen(createdReservation, "/view/kiosk/BookingSummary.fxml");
        }
    }
    
    @FXML
    private void navigateToPaymentScreen(Reservation reservation, String returnScreen) {
        KioskNavigationService.navigateToPaymentScreen(
            reservation,
            returnScreen,
            logger,
            this::getCurrentStage,
            (stage) -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/KioskPayment.fxml"));
                    Parent root = loader.load();
                    KioskPaymentController controller = loader.getController();
                    controller.initPaymentScreen(reservation, returnScreen);
                    stage.setScene(new Scene(root, 1200, 800));
                } catch (Exception e) {
                    logger.logError("Failed to load payment screen", e);
                }
            }
        );
    }
    
    // ========== Confirmation Methods ==========
    public void loadConfirmation() {
        KioskConfirmationHelper.loadConfirmation(
            createdReservation, checkIn, checkOut, currentGuest,
            confirmationTitleLabel, bookingIdLabel, reservationNumberLabel,
            reservationIdLabel, reservationIdRow, statusLabel, bookingDetailsLabel,
            billingMessageLabel, feedbackButton, loyaltyEnrollmentContainer,
            loyaltyEnrolledContainer, loyaltyNumberLabel, loyaltyPointsLabel);
    }
    
    @FXML
    private void goToFeedback() {
        KioskNavigationService.navigateToFeedback(
            createdReservation,
            feedbackButton,
            reservationNumberLabel,
            nameField,
            logger
        );
    }
    
    @FXML
    private void startNewBooking() {
        KioskNavigationService.startNewBooking(
            CustomerSession::clear,
            this::resetBookingState,
            () -> navigateToScreen("/view/kiosk/WelcomeScreen.fxml"));
    }
    
    //
     // Called when booking is confirmed - clears session and returns to welcome screen
     // This is for the "Return to Main Menu" button on confirmation screen
//
    @FXML
    public void returnToWelcomeAfterBooking() {
        try {
            // Clear customer session - no one should be logged in after booking
            CustomerSession.clear();
            
            // Clear all booking state
            resetBookingState();
            
            // Navigate to KioskWelcome (the actual home screen where the application starts)
            navigateToKioskWelcome();
        } catch (Exception e) {
            logger.logError("Failed to navigate to welcome screen after booking", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load screen");
            alert.setContentText("Could not navigate to the main menu: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private void resetBookingState() {
        KioskStateHelper.BookingState resetState = KioskStateHelper.createResetState();
        applyStateToFields(resetState);
    }
    
    private void applyStateToFields(KioskStateHelper.BookingState state) {
        if (state == null) {
            logger.logWarning("applyStateToFields: state is null, cannot apply");
            return;
        }
        
        // CRITICAL: Store the state object so populateFieldsFromState() can use it
        // This ensures we preserve state even if initialize() resets the fields
        lastAppliedState = state;
        
        // Log before applying
        logger.logInfo("applyStateToFields: Applying state - selectedRooms: " + 
            (state.selectedRooms != null ? state.selectedRooms.size() : "null") +
            ", checkIn: " + state.checkIn + ", checkOut: " + state.checkOut);
        
        numAdults = state.numAdults;
        numChildren = state.numChildren;
        checkIn = state.checkIn;
        checkOut = state.checkOut;
        currentGuest = state.currentGuest;
        selectedRooms = state.selectedRooms != null ? new ArrayList<>(state.selectedRooms) : new ArrayList<>();
        selectedAddons = state.selectedAddons != null ? new ArrayList<>(state.selectedAddons) : new ArrayList<>();
        createdReservation = state.createdReservation;
        singleRoomCount = state.singleRoomCount;
        doubleRoomCount = state.doubleRoomCount;
        deluxeRoomCount = state.deluxeRoomCount;
        penthouseRoomCount = state.penthouseRoomCount;
        customSelectionActive = state.customSelectionActive;
        navigationHistory = state.navigationHistory != null ? new java.util.Stack<>() {{
            addAll(state.navigationHistory);
        }} : new java.util.Stack<>();
        
        // Log after applying to verify
        logger.logInfo("applyStateToFields: Applied state - selectedRooms size: " + 
            (selectedRooms != null ? selectedRooms.size() : "null") +
            ", checkIn: " + checkIn + ", checkOut: " + checkOut +
            ", currentGuest: " + (currentGuest != null ? currentGuest.getName() : "null"));
    }
    
    //
     // Adds a screen to the navigation history.
     // Public for helper service access.
//
    public void addToNavigationHistory(String screen) {
        if (screen != null && navigationHistory != null) {
            navigationHistory.push(screen);
        }
    }
    
    //
     // Display detailed room breakdown on booking summary
//
    private void displayRoomBreakdown() {
        // Update label to say "Assigned Rooms" - rooms are assigned by the system, not selected by customer
        if (roomBreakdownTitleLabel != null) {
            roomBreakdownTitleLabel.setText("Assigned Rooms:");
        }
        KioskBookingSummaryHelper.displayRoomBreakdown(
            selectedRooms, checkIn, checkOut, pricingService, 
            roomBreakdownList, roomBreakdownContainer);
    }
    
    //
     // Display assigned rooms from the created reservation (after payment/confirmation).
     // Shows the actual room numbers that were assigned to the customer.
//
    private void displayAssignedRoomsFromReservation() {
        KioskUIHelper.displayAssignedRoomsFromReservation(
            createdReservation, checkIn, checkOut, pricingService,
            roomBreakdownList, roomBreakdownContainer, roomBreakdownTitleLabel);
    }
    
    //
     // Updates the assigned rooms label with room numbers from the created reservation.
//
    private void updateAssignedRoomsLabel() {
        if (createdReservation != null) {
            KioskUIHelper.updateAssignedRoomsLabel(createdReservation, assignedRoomsLabel);
        } else if (selectedRooms != null && !selectedRooms.isEmpty()) {
            KioskUIHelper.updateAssignedRoomsLabelFromSelected(selectedRooms, assignedRoomsLabel);
        } else if (assignedRoomsLabel != null) {
            assignedRoomsLabel.setText("N/A");
        }
    }
    
    //
     // Display detailed add-on breakdown on booking summary
//
    private void displayAddOnBreakdown() {
        KioskBookingSummaryHelper.displayAddOnBreakdown(
            selectedAddons, checkIn, checkOut, addonBreakdownList, addonBreakdownContainer);
    }
    
    //
     // Calculate and display loyalty effects on booking summary
//
    private void calculateAndDisplayLoyaltyEffects(double subtotal, double taxAmount, double total) {
        KioskBookingSummaryHelper.calculateAndDisplayLoyaltyEffects(
            currentGuest, subtotal, taxAmount, total, loyaltyContainer, loyaltyLabel, logger);
    }
    
    //
     // Lookup guest by loyalty number
//
    @FXML
    private void lookupLoyalty() {
        if (loyaltyNumberField == null || loyaltyNumberField.getText().trim().isEmpty()) {
            return;
        }
        
        KioskLoyaltyHelper.lookupLoyalty(
            loyaltyNumberField.getText().trim(),
            guestRepository, logger,
            loyaltyNumberField, loyaltyLookupLabel,
            nameField, phoneField, emailField, addressField,
            (guest) -> currentGuest = guest);
    }
    
    //
     // Enroll guest in loyalty program
//
    @FXML
    private void enrollInLoyalty() {
        KioskLoyaltyHelper.enrollInLoyalty(
            currentGuest, guestRepository, logger,
            loyaltyEnrollmentContainer, loyaltyEnrolledContainer,
            loyaltyNumberLabel, loyaltyPointsLabel);
    }
    
    // ========== Helper Methods ==========
    
    //
     // Hides all error labels.
     // Note: showError() and hideError() are inherited from BaseController.
//
    private void hideAllErrors() {
        KioskUIHelper.hideAllErrors(
            adultsErrorLabel, childrenErrorLabel, checkInErrorLabel, checkOutErrorLabel,
            nameErrorLabel, phoneErrorLabel, emailErrorLabel, occupancyValidationLabel);
    }
    
    private void navigateToScreen(String fxmlPath) {
        navigateToScreen(fxmlPath, null);
    }
    
    private void navigateToScreen(String fxmlPath, ActionEvent event) {
        KioskNavigationService.navigateToScreen(
            fxmlPath,
            event,
            this::determineCurrentScreen,
            this::captureCurrentStateFromUI,
            () -> {
                String currentScreen = determineCurrentScreen();
                // Log selectedRooms before creating state snapshot
                logger.logInfo("Creating state snapshot - selectedRooms size: " + 
                    (selectedRooms != null ? selectedRooms.size() : "null"));
                KioskStateHelper.BookingState state = KioskStateHelper.createStateSnapshot(
                    numAdults, numChildren, checkIn, checkOut, currentGuest,
                    selectedRooms, selectedAddons, createdReservation,
                    singleRoomCount, doubleRoomCount, deluxeRoomCount, penthouseRoomCount,
                    customSelectionActive, navigationHistory);
                // Log state after creation
                logger.logInfo("State snapshot created - selectedRooms in state: " + 
                    (state.selectedRooms != null ? state.selectedRooms.size() : "null"));
                if (currentScreen != null && state.navigationHistory != null) {
                    state.navigationHistory.push(currentScreen);
                }
                return state;
            },
            this::applyStateToFields,
            this::populateFieldsFromState,
            (path) -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                    Parent root = loader.load();
                    Object controllerObj = loader.getController();
                    if (controllerObj instanceof KioskController) {
                        KioskController controller = (KioskController) controllerObj;
                        KioskNavigationHelper.handlePostNavigationLoading(
                            path,
                            controller.guestNameLabel, controller.totalAmountLabel, controller.reservationNumberLabel,
                            controller.singleRoomSpinner, controller.suggestedRoomsTable,
                            controller::loadBookingSummary,
                            controller::loadConfirmation,
                            controller::loadAvailableRooms,
                            controller::refreshCustomSelectionUI,
                            controller::updateSelectedRoomsSummary,
                            logger);
                    }
                } catch (Exception e) {
                    logger.logError("Failed to handle post navigation loading", e);
                }
            },
            logger,
            () -> getCurrentStageFromEvent(event),
            (stage) -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent root = loader.load();
                    stage.setScene(new Scene(root, 1200, 800));
                } catch (Exception e) {
                    logger.logError("Failed to load scene", e);
                }
            }
        );
    }
    
    //
     // Captures current state from UI fields before navigation.
     // This ensures any changes made on the current screen are preserved.
//
    private void captureCurrentStateFromUI() {
        // Store current state values before capturing from UI
        // This ensures we don't lose state if UI fields don't exist on current screen
        LocalDate savedCheckIn = checkIn;
        LocalDate savedCheckOut = checkOut;
        int savedNumAdults = numAdults;
        int savedNumChildren = numChildren;
        Guest savedGuest = currentGuest;
        List<Room> savedSelectedRooms = selectedRooms != null ? new ArrayList<>(selectedRooms) : new ArrayList<>();
        
        KioskStateHelper.captureCurrentStateFromUI(
            numAdultsField, numChildrenField, checkInDatePicker, checkOutDatePicker,
            nameField, phoneField, emailField, addressField,
            singleRoomSpinner, doubleRoomSpinner, deluxeRoomSpinner, penthouseSpinner,
            wifiCheckBox, breakfastCheckBox, parkingCheckBox, spaCheckBox,
            (val) -> numAdults = val,
            (val) -> numChildren = val,
            (val) -> {
                // Only update if we got a value from UI, otherwise preserve existing
                if (val != null) {
                    checkIn = val;
                } else if (checkIn == null) {
                    checkIn = savedCheckIn;
                }
            },
            (val) -> {
                // Only update if we got a value from UI, otherwise preserve existing
                if (val != null) {
                    checkOut = val;
                } else if (checkOut == null) {
                    checkOut = savedCheckOut;
                }
            },
            (guest) -> {
                if (currentGuest != null && guest == null) {
                    // Update current guest from fields
                    if (nameField != null && nameField.getText() != null && !nameField.getText().trim().isEmpty()) {
                        currentGuest.setName(nameField.getText().trim());
                    }
                    if (phoneField != null && phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) {
                        currentGuest.setPhone(phoneField.getText().trim());
                    }
                    if (emailField != null && emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
                        currentGuest.setEmail(emailField.getText().trim());
                    }
                    if (addressField != null && addressField.getText() != null && !addressField.getText().trim().isEmpty()) {
                        currentGuest.setAddress(addressField.getText().trim());
                    }
                } else if (guest == null && currentGuest == null && savedGuest != null) {
                    // Preserve saved guest if UI doesn't have one
                    currentGuest = savedGuest;
                }
            },
            (val) -> singleRoomCount = val,
            (val) -> doubleRoomCount = val,
            (val) -> deluxeRoomCount = val,
            (val) -> penthouseRoomCount = val,
            this::updateAddOnTotal,
            logger
        );
        
        // Preserve selectedRooms if they weren't captured from UI (room selection doesn't have UI fields for this)
        if ((selectedRooms == null || selectedRooms.isEmpty()) && !savedSelectedRooms.isEmpty()) {
            selectedRooms = savedSelectedRooms;
            logger.logInfo("Preserved selectedRooms from saved state: " + selectedRooms.size());
        }
        
        // Preserve dates if they weren't captured from UI
        if (checkIn == null && savedCheckIn != null) {
            checkIn = savedCheckIn;
            logger.logInfo("Preserved checkIn from saved state: " + checkIn);
        }
        if (checkOut == null && savedCheckOut != null) {
            checkOut = savedCheckOut;
            logger.logInfo("Preserved checkOut from saved state: " + checkOut);
        }
        
        logger.logInfo("State captured from UI - numAdults: " + numAdults + ", numChildren: " + numChildren +
            ", checkIn: " + checkIn + ", checkOut: " + checkOut +
            ", selectedRooms: " + (selectedRooms != null ? selectedRooms.size() : 0) +
            ", selectedAddons: " + (selectedAddons != null ? selectedAddons.size() : 0));
    }
    
    private String determineCurrentScreen() {
        return KioskNavigationHelper.determineCurrentScreen(
            emailField, nameField, checkInDatePicker, checkOutDatePicker,
            numAdultsField, numChildrenField, singleRoomSpinner, suggestedRoomsTable,
            wifiCheckBox, guestNameLabel, totalAmountLabel, reservationNumberLabel);
    }
    
    //
     // Gets the current stage with complex fallback logic specific to KioskController.
     // Overrides base class method to provide kiosk-specific fallback nodes.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try kiosk-specific fields in order of likelihood
        Node[] kioskNodes = {
            nameField, checkInDatePicker, numAdultsField, checkOutDatePicker,
            phoneField, emailField, wifiCheckBox
        };
        
        try {
            return KioskNavigationHelper.getCurrentStageWithKioskFallbacks(kioskNodes, fallbackNodes);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Unable to determine current stage in KioskController", e);
        }
    }
    
    //
     // Helper method to get stage from ActionEvent (for backward compatibility).
//
    private Stage getCurrentStageFromEvent(ActionEvent event) {
        Node[] kioskNodes = {
            nameField, checkInDatePicker, numAdultsField, checkOutDatePicker,
            phoneField, emailField, wifiCheckBox
        };
        return KioskNavigationHelper.getCurrentStageFromEvent(event, kioskNodes);
    }
    
}
