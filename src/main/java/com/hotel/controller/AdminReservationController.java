package com.hotel.controller;

//
 // Controller for detailed reservation management.
 // Allows admins to view, modify, and manage individual reservations.
 // Handles room changes, add-ons, guest information updates, and reservation status changes.
//

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.controller.helper.RoomSelectionHelper;
import com.hotel.controller.helper.ValidationHelper;
import com.hotel.controller.helper.AdminReservationUIHelper;
import com.hotel.controller.helper.AdminGuestManagementHelper;
import com.hotel.controller.helper.AdminRoomSelectionHelper;
import com.hotel.controller.helper.AdminTableConfigurationHelper;
import com.hotel.controller.helper.AdminRoomTypeSummaryHelper;
import com.hotel.controller.helper.AdminReservationLoaderHelper;
import com.hotel.controller.helper.AdminDialogBuilder;
import com.hotel.controller.helper.AdminFormInitializer;
import com.hotel.controller.helper.AdminReservationEventHandler;
import com.hotel.controller.helper.AdminReservationValidationHelper;
import com.hotel.model.GuestSelectionResult;
import com.hotel.service.AdminReservationService;
import com.hotel.util.ReservationEntityManager;
import com.hotel.util.ReservationStatusParser;
import com.hotel.util.FormFieldParser;
import com.hotel.util.TextFieldListenerHelper;
import com.hotel.model.RoomTypeSummary;
import com.hotel.model.AdminUser;
import com.hotel.model.Billing;
import com.hotel.model.Guest;
import com.hotel.model.PaymentMethod;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.model.PricingModel;
import com.hotel.model.ReservationAddon;
import com.hotel.model.ServiceAddon;
import com.hotel.repository.AddonRepository;
import com.hotel.repository.AdminUserRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.service.ActivityLogService;
import com.hotel.service.BillingService;
import com.hotel.service.PricingService;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import com.hotel.util.Validator;

import javax.persistence.EntityManager;
import com.hotel.security.BCryptPasswordHasher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//
 // Controller dedicated to ReservationDetails.fxml (admin reservation creation/editing).
//
//
 // Controller for admin reservation management.
 // Extends BaseController to inherit common functionality.
//
public class AdminReservationController extends BaseController {

    @FXML private Label reservationSummaryLabel;
    @FXML private Label reservationIdLabel;
    @FXML private Label modeLabel;
    @FXML private TextField guestNameField;
    @FXML private TextField guestPhoneField;
    @FXML private TextField guestEmailField;
    @FXML private VBox loyaltyEnrollmentSection;
    @FXML private HBox loyaltyContainer;
    @FXML private TextField loyaltyNumberField;
    @FXML private Button enrollLoyaltyButton;
    @FXML private VBox billingInformationContainer;
    @FXML private TextField numAdultsField;
    @FXML private TextField numChildrenField;
    @FXML private Label adultsErrorLabel;
    @FXML private Label childrenErrorLabel;
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField numberOfGuestsField;
    @FXML private TableView<RoomTypeSummary> roomsTable;
    @FXML private TableColumn<RoomTypeSummary, String> roomTypeColumn;
    @FXML private TableColumn<RoomTypeSummary, Integer> roomCountColumn;
    @FXML private TableColumn<RoomTypeSummary, Integer> roomCapacityColumn;
    @FXML private TableColumn<RoomTypeSummary, Integer> roomTotalCapacityColumn;
    @FXML private Label roomSelectionErrorLabel;
    @FXML private Label roomTypeSummaryLabel;
    @FXML private TableView<ReservationAddon> servicesTable;
    @FXML private TableColumn<ReservationAddon, String> serviceNameColumn;
    @FXML private TableColumn<ReservationAddon, String> servicePriceColumn;
    @FXML private TableColumn<ReservationAddon, String> servicePricingModelColumn;
    @FXML private TableColumn<ReservationAddon, String> serviceQuantityColumn;
    @FXML private Button addServiceButton;
    @FXML private Button removeServiceButton;
    @FXML private Label serviceErrorLabel;
    @FXML private Button saveReservationButton;
    @FXML private Button processPaymentButton;
    @FXML private Button checkoutButton;
    @FXML private Button cancelReservationButton;
    @FXML private Button deleteReservationButton;
    @FXML private Label subtotalDisplayLabel;
    @FXML private Label taxDisplayLabel;
    @FXML private Label discountDisplayLabel;
    @FXML private Label totalDisplayLabel;
    @FXML private Label paidAmountDisplayLabel;
    @FXML private Label balanceDisplayLabel;

    private final ObservableList<Room> roomTableData = FXCollections.observableArrayList();
    private final ObservableList<ReservationAddon> serviceTableData = FXCollections.observableArrayList();
    private final List<Room> pendingRooms = new ArrayList<>();
    private final List<ReservationAddon> pendingServices = new ArrayList<>();
    
    private final ObservableList<RoomTypeSummary> roomTypeSummaryData = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    private final ReservationService reservationService = AppConfig.createReservationService();
    private final BillingService billingService = AppConfig.createBillingService();
    private final PricingService pricingService = AppConfig.createPricingService();
    private final GuestRepository guestRepository = AppConfig.createGuestRepository();
    private final LoggerService logger = LoggerService.getInstance();
    private final AdminReservationService adminReservationService = new AdminReservationService();
    private ActivityLogService activityLogService;
    private com.hotel.service.LoyaltyService loyaltyService;
    private com.hotel.service.WaitlistService waitlistService;

    private AdminUser currentUser;
    private Reservation currentReservation;
    private Billing currentBilling;
    private boolean creatingNewReservation;

    @FXML
    private void initialize() {
        configureRoomTable();
        configureServiceTable();
        setupOccupancyListeners();
        if (statusComboBox != null) {
            statusComboBox.getItems().addAll("Pending", "Confirmed", "Cancelled", "Checked Out", "Checked In");
        }
        updateGuestCountSummary();
        updateActionButtons();
        
        // Initialize loyalty service
        loyaltyService = AppConfig.createLoyaltyService();
        // Initialize activity log service
        activityLogService = AppConfig.createActivityLogService();
        // Initialize waitlist service (for converting waitlist to reservation)
        waitlistService = AppConfig.createWaitlistService(new com.hotel.events.RoomAvailabilityPublisher());
        
        // Setup loyalty enrollment button visibility
        if (enrollLoyaltyButton != null && guestEmailField != null) {
            guestEmailField.textProperty().addListener((obs, oldVal, newVal) -> {
                updateLoyaltyEnrollmentButton();
            });
        }
    }
    
    @FXML
    private void updateLoyaltyEnrollmentButton() {
        AdminGuestManagementHelper.updateLoyaltyEnrollmentButton(
            guestEmailField != null ? guestEmailField.getText() : null,
            guestRepository,
            enrollLoyaltyButton,
            loyaltyContainer,
            loyaltyNumberField
        );
    }
    
    @FXML
    private void enrollGuestInLoyalty() {
        if (guestEmailField == null || guestEmailField.getText() == null || guestEmailField.getText().trim().isEmpty()) {
            AlertHelper.showError("Error", "Please enter guest email first.");
            return;
        }
        
        try {
            String email = guestEmailField.getText().trim();
            String name = guestNameField != null ? guestNameField.getText() : null;
            String phone = guestPhoneField != null ? guestPhoneField.getText() : null;
            
            String loyaltyNumber = AdminGuestManagementHelper.enrollGuestInLoyalty(
                email,
                name,
                phone,
                guestRepository,
                activityLogService,
                logger,
                currentUser != null ? currentUser.getUsername() : null
            );
            
            // Check if guest was already enrolled (loyaltyNumber would be existing one)
            Optional<Guest> guestOpt = guestRepository.findByEmail(email);
            if (guestOpt.isPresent()) {
                Guest guest = guestOpt.get();
                if (guest.getLoyaltyNumber() != null && !guest.getLoyaltyNumber().isEmpty()) {
                    // Check if this is existing enrollment
                    if (!loyaltyNumber.equals(guest.getLoyaltyNumber())) {
                        // This shouldn't happen, but handle gracefully
                        loyaltyNumber = guest.getLoyaltyNumber();
                    }
                }
            }
            
            // Show loyalty number field
            if (loyaltyContainer != null) {
                loyaltyContainer.setVisible(true);
                loyaltyContainer.setManaged(true);
            }
            if (loyaltyNumberField != null) {
                loyaltyNumberField.setText(loyaltyNumber);
                loyaltyNumberField.setEditable(false);
            }
            if (enrollLoyaltyButton != null) {
                enrollLoyaltyButton.setVisible(false);
                enrollLoyaltyButton.setManaged(false);
            }
            
            AlertHelper.showInfo("Success", 
                "Guest enrolled successfully!\nLoyalty Number: " + loyaltyNumber);
                
        } catch (IllegalArgumentException e) {
            AlertHelper.showError("Error", e.getMessage());
        } catch (Exception e) {
            logger.logError("Failed to enroll guest in loyalty program", e);
            AlertHelper.showError("Error", "Failed to enroll guest: " + e.getMessage());
        }
    }
    
    private void configureServiceTable() {
        AdminTableConfigurationHelper.configureServiceTable(
            servicesTable,
            serviceTableData,
            serviceNameColumn,
            servicePriceColumn,
            servicePricingModelColumn,
            serviceQuantityColumn
        );
    }

    public void initForCreate(AdminUser user) {
        this.currentUser = user;
        // Show customer selection dialog first
        Optional<GuestSelectionResult> result = AdminDialogBuilder.showCustomerSelectionDialog(
            guestRepository, logger);
        if (result.isPresent()) {
            GuestSelectionResult selection = result.get();
            if (selection.guest != null) {
                fillGuestDetails(selection.guest);
            }
            if (selection.createAccount) {
                // Handle account creation if needed
            }
            if (selection.proceedAsGuest) {
                // Handle guest proceeding
            }
        }
    }
    
    
    //
     // Fills guest details in the form from a selected guest
//
    private void fillGuestDetails(Guest guest) {
        AdminGuestManagementHelper.fillGuestDetails(
            guest,
            guestNameField,
            guestPhoneField,
            guestEmailField,
            this::updateLoyaltyEnrollmentButton
        );
    }

    public void initForExisting(AdminUser user, Long reservationId) {
        this.currentUser = user;
        loadReservation(reservationId);
    }
    
    //
     // Initialize reservation form from waitlist entry.
     // Pre-fills all fields with waitlist data so admin can review and make changes.
//
    public void initFromWaitlist(AdminUser user, com.hotel.model.Waitlist waitlist) {
        this.currentUser = user;
        this.creatingNewReservation = true;
        this.currentReservation = null;
        this.currentBilling = null;
        this.pendingRooms.clear();
        this.roomTableData.clear();
        
        // Pre-fill form with waitlist data
        if (waitlist.getGuest() != null) {
            Guest guest = waitlist.getGuest();
            if (guestNameField != null) guestNameField.setText(guest.getName() != null ? guest.getName() : "");
            if (guestPhoneField != null) guestPhoneField.setText(guest.getPhone() != null ? guest.getPhone() : "");
            if (guestEmailField != null) guestEmailField.setText(guest.getEmail() != null ? guest.getEmail() : "");
            // Note: Address field is not present in reservation details form
        }
        
        // Pre-fill dates
        if (checkInDatePicker != null) checkInDatePicker.setValue(waitlist.getDateRangeStart());
        if (checkOutDatePicker != null) checkOutDatePicker.setValue(waitlist.getDateRangeEnd());
        
        // Pre-fill adults and children from waitlist
        if (numAdultsField != null) {
            numAdultsField.setText(waitlist.getNumAdults() != null ? String.valueOf(waitlist.getNumAdults()) : "1");
        }
        if (numChildrenField != null) {
            numChildrenField.setText(waitlist.getNumChildren() != null ? String.valueOf(waitlist.getNumChildren()) : "0");
        }
        
        // Pre-select the requested room type
        // Get available rooms for the requested type
        List<Room> availableRooms = reservationService.getAvailableRooms(
            waitlist.getRequestedType(),
            waitlist.getDateRangeStart(),
            waitlist.getDateRangeEnd()
        );
        
        if (!availableRooms.isEmpty()) {
            // Add first available room to pending rooms
            pendingRooms.add(availableRooms.get(0));
            updateRoomTypeSummary(); // Update room type summary display
        }
        
        // Hide billing information section (will show after reservation is created)
        if (billingInformationContainer != null) {
            billingInformationContainer.setVisible(false);
            billingInformationContainer.setManaged(false);
        }
        
        // Hide loyalty container initially
        if (loyaltyContainer != null) {
            loyaltyContainer.setVisible(false);
            loyaltyContainer.setManaged(false);
        }
        
        // Update loyalty enrollment button based on email
        updateLoyaltyEnrollmentButton();
        
        if (statusComboBox != null) {
            statusComboBox.setValue("Pending");
            statusComboBox.setDisable(true);
        }
        
        if (modeLabel != null) {
            modeLabel.setVisible(true);
            modeLabel.setText("Create Mode (from Waitlist)");
        }
        
        updateActionButtons();
        updateRoomSelectionError(null);
        updateGuestCountSummary();
        
        // Store waitlist reference so we can remove it after reservation is created
        // We'll use a field to track this
        this.waitlistToRemove = waitlist;
    }
    
    // Field to track waitlist being converted (will be removed after reservation creation)
    private com.hotel.model.Waitlist waitlistToRemove;

    private void startCreateMode() {
        AdminFormInitializer.startCreateMode(
            (creating) -> creatingNewReservation = creating,
            (v) -> { currentReservation = null; currentBilling = null; pendingRooms.clear(); roomTableData.clear(); },
            (v) -> roomTableData.clear(),
            this::clearReservationForm,
            billingInformationContainer,
            loyaltyContainer,
            enrollLoyaltyButton,
            statusComboBox,
            modeLabel,
            this::updateActionButtons,
            this::updateRoomSelectionError
        );
    }

    private void clearReservationForm() {
        AdminFormInitializer.clearReservationForm(
            guestNameField,
            guestPhoneField,
            guestEmailField,
            numAdultsField,
            numChildrenField,
            checkInDatePicker,
            checkOutDatePicker,
            numberOfGuestsField,
            this::updateGuestCountSummary
        );
    }

    private void configureRoomTable() {
        AdminTableConfigurationHelper.configureRoomTable(
            roomsTable,
            roomTypeSummaryData,
            roomTypeColumn,
            roomCountColumn,
            roomCapacityColumn,
            roomTotalCapacityColumn
        );
    }
    
    //
     // Convert list of rooms to room type summary (grouped by type)
//
    private void updateRoomTypeSummary() {
        AdminRoomTypeSummaryHelper.updateRoomTypeSummary(
            pendingRooms,
            roomTypeSummaryData,
            this::updateRoomTypeSummaryLabel
        );
    }
    
    //
     // Update the room type summary label to show current room assignments
//
    private void updateRoomTypeSummaryLabel() {
        AdminRoomTypeSummaryHelper.updateRoomTypeSummaryLabel(
            pendingRooms,
            roomTypeSummaryLabel
        );
    }

    private void setupOccupancyListeners() {
        attachNumericListener(numAdultsField);
        attachNumericListener(numChildrenField);
        
        // Add listeners to check occupancy when values change
        if (numAdultsField != null) {
            numAdultsField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    checkOccupancyAndSuggest();
                }
            });
        }
        if (numChildrenField != null) {
            numChildrenField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    checkOccupancyAndSuggest();
                }
            });
        }
    }
    
    private void checkOccupancyAndSuggest() {
        // Only check if we have dates set
        if (checkInDatePicker == null || checkInDatePicker.getValue() == null ||
            checkOutDatePicker == null || checkOutDatePicker.getValue() == null) {
            return;
        }
        
        int adults = parseInteger(numAdultsField, 0);
        int children = parseInteger(numChildrenField, 0);
        int totalGuests = adults + children;
        
        if (totalGuests <= 0) {
            return;
        }
        
        // Get current rooms (from pendingRooms or currentReservation)
        List<Room> currentRooms;
        if (!pendingRooms.isEmpty()) {
            currentRooms = new ArrayList<>(pendingRooms);
        } else if (currentReservation != null && !currentReservation.getReservationRooms().isEmpty()) {
            currentRooms = currentReservation.getReservationRooms().stream()
                .map(rr -> rr.getRoom())
                .toList();
        } else {
            // No rooms selected yet - don't show suggestion
            return;
        }
        
        if (currentRooms.isEmpty()) {
            return;
        }
        
        // Calculate current room capacity
        int currentCapacity = calculateRoomCapacity(currentRooms);
        
        // Check if rooms can accommodate
        if (currentCapacity < totalGuests) {
            showRoomSuggestionDialog(currentCapacity, totalGuests, adults, children, currentRooms);
        } else {
            // Clear any error messages if capacity is sufficient
            updateRoomSelectionError(null);
        }
    }
    
    private void showRoomSuggestionDialog(int currentCapacity, int requiredGuests, int adults, int children, List<Room> currentRooms) {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        
        if (checkIn == null || checkOut == null) {
            return;
        }
        
        int additionalCapacityNeeded = requiredGuests - currentCapacity;
        
        // Calculate how many additional rooms are needed
        // Try double rooms first (4 capacity each), then singles (2 capacity each)
        int additionalDoubleRoomsNeeded = (int) Math.ceil(additionalCapacityNeeded / 4.0);
        int additionalSingleRoomsNeeded = (int) Math.ceil(additionalCapacityNeeded / 2.0);
        
        // Get available rooms for suggestions (exclude already selected rooms)
        List<Room> availableDoubles = reservationService.getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut);
        List<Room> availableSingles = reservationService.getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
        
        // Filter out already selected rooms
        availableDoubles = availableDoubles.stream()
            .filter(room -> !currentRooms.contains(room))
            .toList();
        availableSingles = availableSingles.stream()
            .filter(room -> !currentRooms.contains(room))
            .toList();
        
        StringBuilder message = new StringBuilder();
        message.append("Occupancy Alert\n\n");
        message.append("Current Rooms: ").append(currentRooms.size()).append(" room(s)\n");
        message.append("Current Room Capacity: ").append(currentCapacity).append(" guests\n");
        message.append("Required Capacity: ").append(requiredGuests).append(" guests (").append(adults).append(" adults, ").append(children).append(" children)\n");
        message.append("Additional Capacity Needed: ").append(additionalCapacityNeeded).append(" guests\n\n");
        
        message.append("Suggestions:\n");
        if (availableDoubles.size() >= additionalDoubleRoomsNeeded) {
            message.append("• Add ").append(additionalDoubleRoomsNeeded).append(" double room(s) (each accommodates 4 guests)\n");
        }
        if (availableSingles.size() >= additionalSingleRoomsNeeded) {
            message.append("• Add ").append(additionalSingleRoomsNeeded).append(" single room(s) (each accommodates 2 guests)\n");
        }
        if (availableDoubles.size() < additionalDoubleRoomsNeeded && availableSingles.size() < additionalSingleRoomsNeeded) {
            message.append("• Limited availability. Please check room availability manually.\n");
        }
        
        message.append("\nWould you like to add more rooms automatically?");
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Insufficient Room Capacity");
        alert.setHeaderText("Selected rooms cannot accommodate all guests");
        alert.setContentText(message.toString());
        
        javafx.scene.control.ButtonType addRoomsButton = new javafx.scene.control.ButtonType("Add Rooms", 
            javafx.scene.control.ButtonBar.ButtonData.YES);
        javafx.scene.control.ButtonType skipButton = new javafx.scene.control.ButtonType("Skip", 
            javafx.scene.control.ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(addRoomsButton, skipButton);
        
        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == addRoomsButton) {
            // User wants to add rooms - add suggested rooms
            addSuggestedRooms(additionalCapacityNeeded, checkIn, checkOut, currentRooms);
        } else {
            // User chose to skip - show warning but allow
            updateRoomSelectionError("Warning: Selected rooms may not accommodate all guests comfortably.");
        }
    }
    
    private void addSuggestedRooms(int additionalCapacityNeeded, LocalDate checkIn, LocalDate checkOut, List<Room> currentRooms) {
        try {
            List<Room> currentRoomsList = currentRooms != null ? currentRooms : new ArrayList<>();
            int roomsAdded = AdminRoomSelectionHelper.addSuggestedRooms(
                additionalCapacityNeeded,
                checkIn,
                checkOut,
                currentRoomsList,
                pendingRooms,
                reservationService,
                logger
            );
            
            if (roomsAdded == 0) {
                AlertHelper.showWarning("Limited Availability", 
                    "No additional rooms available. Please manually select rooms.");
                return;
            }
            
            // Update room table display
            updateRoomTypeSummary();
            
            // Recalculate billing when rooms are added
            recalculateBillingIfNeeded();
            updateBillingDisplay();
            
            // Re-validate occupancy to check if we now have enough capacity
            int newCapacity = calculateRoomCapacity(pendingRooms);
            int totalGuests = parseInteger(numAdultsField, 0) + parseInteger(numChildrenField, 0);
            
            if (newCapacity >= totalGuests) {
                updateRoomSelectionError(null);
                AlertHelper.showInfo("Success", "Added " + roomsAdded + " room(s). Capacity now sufficient.");
            } else {
                updateRoomSelectionError("Still need more rooms. Current capacity: " + newCapacity + ", Required: " + totalGuests);
                AlertHelper.showWarning("Partial Addition", 
                    "Added " + roomsAdded + " room(s), but more may be needed. Current capacity: " + newCapacity + " guests.");
            }
        } catch (Exception e) {
            logger.logError("Failed to add suggested rooms", e);
            AlertHelper.showError("Error", "Failed to add rooms: " + e.getMessage());
        }
    }

    private void attachNumericListener(TextField field) {
        TextFieldListenerHelper.attachNumericListener(field, this::updateGuestCountSummary);
    }

    private void updateGuestCountSummary() {
        AdminReservationUIHelper.updateGuestCountSummary(
            numAdultsField,
            numChildrenField,
            numberOfGuestsField
        );
    }

    private void updateActionButtons() {
        boolean hasReservation = currentReservation != null && !creatingNewReservation;
        AdminReservationUIHelper.updateActionButtons(
            hasReservation,
            saveReservationButton,
            processPaymentButton,
            checkoutButton,
            cancelReservationButton,
            deleteReservationButton,
            statusComboBox
        );
    }

    private void updateRoomSelectionError(String message) {
        AdminReservationUIHelper.updateRoomSelectionError(message, roomSelectionErrorLabel);
    }

    private int parseInteger(TextField field, int defaultValue) {
        return FormFieldParser.parseInteger(field, defaultValue);
    }

    private boolean validateRoomSelectionPrerequisites() {
        LocalDate checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        LocalDate checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        int adults = parseInteger(numAdultsField, 0);
        
        if (!AdminRoomSelectionHelper.validateRoomSelectionPrerequisites(checkIn, checkOut, adults, 
            AlertHelper::showError)) {
            if (adults <= 0) {
                updateRoomSelectionError("At least one adult is required for the reservation.");
            }
            return false;
        }
        return true;
    }

    private boolean validateOccupancy(List<Room> rooms) {
        int adults = parseInteger(numAdultsField, 0);
        int children = parseInteger(numChildrenField, 0);
        boolean valid = reservationService.validateOccupancy(rooms, adults, children);
        if (!valid) {
            updateRoomSelectionError("Selected rooms cannot accommodate " + (adults + children) + " guests. Please add more rooms.");
        } else {
            updateRoomSelectionError(null);
        }
        return valid;
    }
    
    //
     // Real-time occupancy validation with alert option to bypass
//
    private void validateOccupancyRealtime() {
        int adults = parseInteger(numAdultsField, 0);
        int children = parseInteger(numChildrenField, 0);
        int totalGuests = adults + children;
        
        if (totalGuests <= 0) {
            updateRoomSelectionError(null);
            return;
        }
        
        if (pendingRooms.isEmpty()) {
            updateRoomSelectionError("No rooms selected. Please add rooms to accommodate " + totalGuests + " guests.");
            return;
        }
        
        boolean valid = reservationService.validateOccupancy(pendingRooms, adults, children);
        if (!valid) {
            int currentCapacity = calculateRoomCapacity(pendingRooms);
            String message = "Selected rooms can accommodate " + currentCapacity + " guests, but " + totalGuests + " guests are required.\n\n" +
                           "Would you like to add more rooms?";
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Occupancy Warning");
            alert.setHeaderText("Insufficient Room Capacity");
            alert.setContentText(message);
            
            javafx.scene.control.ButtonType addRoomsButton = new javafx.scene.control.ButtonType("Add Rooms");
            javafx.scene.control.ButtonType bypassButton = new javafx.scene.control.ButtonType("Bypass", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(addRoomsButton, bypassButton);
            
            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == addRoomsButton) {
                // User wants to add rooms - suggest rooms
                int additionalCapacityNeeded = totalGuests - currentCapacity;
                LocalDate checkIn = checkInDatePicker.getValue();
                LocalDate checkOut = checkOutDatePicker.getValue();
                if (checkIn != null && checkOut != null) {
                    addSuggestedRooms(additionalCapacityNeeded, checkIn, checkOut, pendingRooms);
                }
            } else {
                // User chose to bypass - just show warning
                updateRoomSelectionError("Warning: Selected rooms may not accommodate all guests comfortably.");
            }
        } else {
            updateRoomSelectionError(null);
        }
    }

    @FXML
    private void addRoom() {
        if (!isCreateMode() && currentReservation == null) {
            AlertHelper.showError("Error", "No reservation selected.");
            return;
        }
        if (!validateRoomSelectionPrerequisites()) return;

        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        Optional<Room> selectedRoom = RoomSelectionHelper.selectRoom(
            reservationService, checkIn, checkOut, pendingRooms);
        if (selectedRoom.isEmpty()) {
            updateRoomSelectionError("No rooms available for the selected criteria.");
            return;
        }

        pendingRooms.add(selectedRoom.get());
        updateRoomTypeSummary(); // Update room type summary display (also updates label)
        updateRoomSelectionError(null);
        validateOccupancyRealtime(); // Real-time validation
        // Recalculate billing when rooms change
        recalculateBillingIfNeeded();
    }

    @FXML
    private void removeSelectedRoom() {
        if (roomsTable == null) return;
        RoomTypeSummary selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Error", "Please select a room type to remove");
            return;
        }
        // Remove one room of the selected type
        Room roomToRemove = pendingRooms.stream()
            .filter(room -> room.getType() == selected.getType())
            .findFirst()
            .orElse(null);
        if (roomToRemove != null) {
            pendingRooms.remove(roomToRemove);
            updateRoomTypeSummary(); // Update room type summary display (also updates label)
            updateRoomSelectionError(null);
            validateOccupancyRealtime(); // Real-time validation
            // Recalculate billing when rooms change
            recalculateBillingIfNeeded();
        }
    }
    
    @FXML
    private void addService() {
        if (!isCreateMode() && currentReservation == null) {
            AlertHelper.showError("Error", "No reservation selected.");
            return;
        }
        if (checkInDatePicker == null || checkInDatePicker.getValue() == null ||
            checkOutDatePicker == null || checkOutDatePicker.getValue() == null) {
            AlertHelper.showError("Error", "Please select check-in and check-out dates first.");
            return;
        }
        
        try {
            AddonRepository addonRepo = AppConfig.createAddonRepository();
            List<ServiceAddon> allAddons = addonRepo.findAll();
            
            Optional<ServiceAddon> selectedAddon = AdminDialogBuilder.showServiceSelectionDialog(
                allAddons, currencyFormat);
            
            if (selectedAddon.isEmpty()) {
                return;
            }
            
            ServiceAddon addon = selectedAddon.get();
            
            // Check if already added
            boolean alreadyExists = pendingServices.stream()
                .anyMatch(ra -> ra.getAddon() != null && ra.getAddon().getId().equals(addon.getId()));
            
            if (alreadyExists) {
                // Increase quantity
                pendingServices.stream()
                    .filter(ra -> ra.getAddon() != null && ra.getAddon().getId().equals(addon.getId()))
                    .findFirst()
                    .ifPresent(ra -> ra.setQuantity(ra.getQuantity() + 1));
            } else {
                // Create new ReservationAddon
                ReservationAddon reservationAddon = new ReservationAddon();
                reservationAddon.setAddon(addon);
                reservationAddon.setQuantity(1);
                if (currentReservation != null) {
                    reservationAddon.setReservation(currentReservation);
                }
                pendingServices.add(reservationAddon);
            }
            
            serviceTableData.setAll(pendingServices);
            updateServiceError(null);
            
            // Recalculate billing when services change
            recalculateBillingIfNeeded();
            
        } catch (Exception e) {
            logger.logError("Failed to add service", e);
            AlertHelper.showError("Error", "Failed to add service: " + e.getMessage());
        }
    }
    
    @FXML
    private void removeSelectedService() {
        if (servicesTable == null) return;
        ReservationAddon selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showError("Error", "Please select a service to remove");
            return;
        }
        
        if (selected.getQuantity() > 1) {
            // Decrease quantity
            selected.setQuantity(selected.getQuantity() - 1);
        } else {
            // Remove completely
            pendingServices.remove(selected);
        }
        
        serviceTableData.setAll(pendingServices);
        updateServiceError(null);
        
        // Recalculate billing when services change
        recalculateBillingIfNeeded();
        updateBillingDisplay();
    }
    
    private void updateServiceError(String message) {
        AdminReservationUIHelper.updateServiceError(message, serviceErrorLabel);
    }
    
    private void recalculateBillingIfNeeded() {
        LocalDate checkIn = checkInDatePicker != null ? checkInDatePicker.getValue() : null;
        LocalDate checkOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : null;
        
        Billing updatedBilling = adminReservationService.recalculateBillingIfNeeded(
            currentReservation,
            pendingRooms,
            checkIn,
            checkOut,
            billingService,
            (billing) -> {
                currentBilling = billing;
                updateBillingDisplay();
            },
            () -> updateEstimatedBillingDisplay(adminReservationService.calculateSubtotal(
                pendingRooms.isEmpty() && currentReservation != null
                    ? currentReservation.getReservationRooms().stream().map(rr -> rr.getRoom()).toList()
                    : pendingRooms,
                checkIn,
                checkOut,
                pendingServices,
                currentReservation
            ))
        );
        
        if (updatedBilling != null) {
            currentBilling = updatedBilling;
        }
    }

    @FXML
    private void saveReservationChanges() {
        if (isCreateMode()) {
            createReservationFromForm();
            return;
        }
        if (currentReservation == null) {
            AlertHelper.showError("Error", "No reservation selected");
            return;
        }
        
        try {
            // Get new values from form
            int newAdults = parseInteger(numAdultsField, currentReservation.getNumAdults());
            int newChildren = parseInteger(numChildrenField, currentReservation.getNumChildren());
            LocalDate newCheckIn = checkInDatePicker != null ? checkInDatePicker.getValue() : currentReservation.getCheckIn();
            LocalDate newCheckOut = checkOutDatePicker != null ? checkOutDatePicker.getValue() : currentReservation.getCheckOut();
            
            // Get current rooms
            List<Room> currentRooms = currentReservation.getReservationRooms().stream()
                .map(rr -> rr.getRoom())
                .toList();
            
            // Check if guest count changed
            boolean guestCountChanged = (newAdults != currentReservation.getNumAdults()) || 
                                       (newChildren != currentReservation.getNumChildren());
            
            // Check if rooms changed
            boolean roomsChanged = !pendingRooms.isEmpty() && !pendingRooms.equals(currentRooms);
            
            // Validate occupancy if guest count or rooms changed
            List<Room> roomsToValidate = roomsChanged ? pendingRooms : currentRooms;
            if (guestCountChanged || roomsChanged) {
                if (!reservationService.validateOccupancy(roomsToValidate, newAdults, newChildren)) {
                    // Show dialog with options
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Occupancy Warning");
                    alert.setHeaderText("Selected rooms cannot accommodate all guests");
                    alert.setContentText(
                        "The selected rooms can accommodate " + calculateRoomCapacity(roomsToValidate) + 
                        " guests, but you have " + (newAdults + newChildren) + " guests.\n\n" +
                        "Choose an option:\n" +
                        "• Cancel: Go back and change rooms\n" +
                        "• OK: Proceed with customer's choice (rooms may be overcrowded)");
                    
                    javafx.scene.control.ButtonType cancelButton = new javafx.scene.control.ButtonType("Cancel", 
                        javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                    javafx.scene.control.ButtonType proceedButton = new javafx.scene.control.ButtonType("Proceed", 
                        javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
                    alert.getButtonTypes().setAll(cancelButton, proceedButton);
                    
                    Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                    if (result.isEmpty() || result.get() == cancelButton) {
                        // User chose to cancel - don't save
                        AlertHelper.showInfo("Info", "Please change rooms to accommodate all guests");
                        return;
                    }
                    // User chose to proceed - continue with save
                }
            }
            
            // Update guest information
            adminReservationService.updateReservationGuestInfo(
                currentReservation,
                guestNameField != null ? guestNameField.getText() : null,
                guestPhoneField != null ? guestPhoneField.getText() : null,
                guestEmailField != null ? guestEmailField.getText() : null
            );
            
            // Update dates
            if (newCheckIn != null) {
                currentReservation.setCheckIn(newCheckIn);
            }
            if (newCheckOut != null) {
                currentReservation.setCheckOut(newCheckOut);
            }
            
            // Update occupancy
            currentReservation.setNumAdults(newAdults);
            currentReservation.setNumChildren(newChildren);
            
            // Update rooms if changed - handle this separately to ensure proper entity management
            if (roomsChanged && !pendingRooms.isEmpty()) {
                // Validate all rooms exist and are not null
                for (Room room : pendingRooms) {
                    if (room == null || room.getId() == null) {
                        logger.logError("Invalid room in pendingRooms: " + room, new IllegalArgumentException("Room cannot be null or have null ID"));
                        AlertHelper.showError("Error", "Invalid room selected. Please try again.");
                        return;
                    }
                }
                
                // Use ReservationEntityManager to update rooms properly
                try {
                    Long reservationId = currentReservation.getId();
                    currentReservation = ReservationEntityManager.updateReservationRooms(
                        reservationId,
                        pendingRooms,
                        logger
                    );
                } catch (Exception e) {
                    logger.logError("Failed to update rooms for reservation", e);
                    AlertHelper.showError("Error", "Failed to update rooms: " + e.getMessage());
                    return;
                }
            }
            
            // Check if services changed - now safe because reservation is reloaded with services
            List<ReservationAddon> currentServices = currentReservation.getReservationAddons() != null 
                ? new ArrayList<>(currentReservation.getReservationAddons()) 
                : new ArrayList<>();
            
            // Compare by size and content to determine if services changed
            boolean servicesChanged = AdminRoomSelectionHelper.haveServicesChanged(pendingServices, currentServices);
            
            // Update services/add-ons if changed
            if (servicesChanged) {
                try {
                    Long reservationId = currentReservation.getId();
                    currentReservation = ReservationEntityManager.updateReservationAddons(
                        reservationId,
                        pendingServices,
                        logger
                    );
                } catch (Exception e) {
                    logger.logError("Failed to update addons for reservation", e);
                    AlertHelper.showError("Error", "Failed to update addons: " + e.getMessage());
                    return;
                }
            }
            
            // Get status from ComboBox - ALWAYS check this first (user's manual selection)
            ReservationStatus selectedStatus = null;
            ReservationStatus originalStatus = currentReservation.getStatus();
            
            if (statusComboBox != null && !statusComboBox.isDisabled()) {
                String comboBoxValue = statusComboBox.getValue();
                if (comboBoxValue != null && !comboBoxValue.isEmpty()) {
                    selectedStatus = ReservationStatusParser.parseReservationStatus(comboBoxValue);
                    logger.logInfo("Status ComboBox value: '" + comboBoxValue + "' -> parsed: " + selectedStatus);
                }
            }
            
            // Determine and set status
            ReservationStatus statusToSave = adminReservationService.determineReservationStatus(
                selectedStatus,
                guestCountChanged,
                roomsChanged,
                servicesChanged,
                newCheckIn,
                newCheckOut,
                currentReservation
            );
            
            currentReservation.setStatus(statusToSave);
            logger.logInfo("Saving reservation with status: " + statusToSave + " (was: " + originalStatus + ")");
            
            // Save reservation with the determined status
            currentReservation = adminReservationService.saveReservationWithStatus(
                currentReservation,
                statusToSave,
                logger
            );
            
            // Store original paid amount before recalculation
            double originalPaidAmount = currentBilling != null ? currentBilling.getPaidAmount() : 0.0;
            
            // Recalculate billing if rooms, services, or dates changed
            if (roomsChanged || servicesChanged || 
                (newCheckIn != null && !newCheckIn.equals(currentReservation.getCheckIn())) ||
                (newCheckOut != null && !newCheckOut.equals(currentReservation.getCheckOut()))) {
                recalculateBillingAfterChange();
            }
            
            // Refresh display - but preserve the status ComboBox value if user selected it
            String userSelectedStatus = statusComboBox != null ? statusComboBox.getValue() : null;
            loadReservation(currentReservation.getId());
            // Restore user's selection if they had manually changed it
            if (userSelectedStatus != null && selectedStatus != null) {
                statusComboBox.setValue(userSelectedStatus);
            }
            updateBillingDisplay();
            
            // Show payment breakdown if billing changed
            if (roomsChanged || servicesChanged || 
                (newCheckIn != null && !newCheckIn.equals(currentReservation.getCheckIn())) ||
                (newCheckOut != null && !newCheckOut.equals(currentReservation.getCheckOut()))) {
                showPaymentBreakdown(originalPaidAmount);
            }
            
            String statusMessage = selectedStatus != null 
                ? "Reservation changes saved successfully. Status: " + formatReservationStatus(selectedStatus.name())
                : "Reservation changes saved successfully.";
            AlertHelper.showInfo("Success", statusMessage);
            if (activityLogService != null) {
                activityLogService.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                    "UPDATE_RESERVATION", "Reservation", currentReservation.getId(),
                    "Updated reservation details - status: " + currentReservation.getStatus());
            }
        } catch (Exception e) {
            logger.logError("Failed to save reservation changes", e);
            AlertHelper.showError("Error", "Failed to save changes: " + e.getMessage());
        }
    }
    
    private int calculateRoomCapacity(List<Room> rooms) {
        return AdminRoomSelectionHelper.calculateRoomCapacity(rooms);
    }
    
    private void recalculateBillingAfterChange() {
        currentBilling = adminReservationService.recalculateBillingAfterChange(
            currentReservation,
            billingService
        );
    }

    private void createReservationFromForm() {
        if (!validateNewReservationForm()) return;
        try {
            Guest guest = resolveGuestFromForm();
            LocalDate checkIn = checkInDatePicker.getValue();
            LocalDate checkOut = checkOutDatePicker.getValue();
            int adults = parseInteger(numAdultsField, 0);
            int children = parseInteger(numChildrenField, 0);
            List<Room> roomsToBook = new ArrayList<>(pendingRooms);

            // Convert pending services to ServiceAddon list
            List<ServiceAddon> addonsToCreate = pendingServices.stream()
                .map(ra -> ra.getAddon())
                .filter(addon -> addon != null)
                .toList();

            AdminReservationService.ReservationCreationResult result = adminReservationService.createReservationFromForm(
                guest,
                roomsToBook,
                checkIn,
                checkOut,
                adults,
                children,
                addonsToCreate,
                reservationService,
                billingService
            );

            currentReservation = result.reservation;
            currentBilling = result.billing;
            creatingNewReservation = false;
            
            // Show billing information section after reservation is created
            if (billingInformationContainer != null) {
                billingInformationContainer.setVisible(true);
                billingInformationContainer.setManaged(true);
            }
            
            pendingRooms.clear();
            roomTableData.clear();
            loadReservation(result.reservation.getId());
            updateActionButtons();
            
            // If this reservation was created from a waitlist, remove the waitlist entry
            Long waitlistIdToRemove = null;
            if (waitlistToRemove != null && waitlistService != null) {
                try {
                    waitlistIdToRemove = waitlistToRemove.getId();
                    waitlistService.removeFromWaitlist(waitlistIdToRemove);
                    logger.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                        "CONVERT_WAITLIST", "Waitlist", waitlistIdToRemove,
                        "Converted waitlist entry to reservation " + result.reservation.getId());
                    waitlistToRemove = null; // Clear reference
                } catch (Exception e) {
                    logger.logError("Failed to remove waitlist entry after conversion", e);
                    // Don't fail the reservation creation if waitlist removal fails
                }
            }

            String reservationSource = waitlistIdToRemove != null ? " (from waitlist)" : "";
            AlertHelper.showInfo("Success", "Reservation created successfully." + 
                (waitlistIdToRemove != null ? "\nWaitlist entry has been removed." : ""));
            if (activityLogService != null) {
                activityLogService.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                    "CREATE_RESERVATION", "Reservation", result.reservation.getId(),
                    "Created reservation via admin portal" + reservationSource);
            }
        } catch (Exception e) {
            logger.logError("Failed to create reservation", e);
            AlertHelper.showError("Error", "Failed to create reservation: " + e.getMessage());
        }
    }

    private boolean validateNewReservationForm() {
        return AdminReservationValidationHelper.validateNewReservationForm(
            guestNameField,
            guestPhoneField,
            guestEmailField,
            numAdultsField,
            numChildrenField,
            checkInDatePicker,
            checkOutDatePicker,
            pendingRooms,
            reservationService,
            this::validateRoomSelectionPrerequisites,
            this::updateRoomSelectionError,
            () -> validateOccupancy(pendingRooms)
        );
    }

    private Guest resolveGuestFromForm() {
        return adminReservationService.resolveGuestFromForm(
            guestNameField.getText(),
            guestPhoneField.getText(),
            guestEmailField.getText()
        );
    }

    private double calculateSubtotal(List<Room> rooms, LocalDate checkIn, LocalDate checkOut) {
        return adminReservationService.calculateSubtotal(
            rooms, 
            checkIn, 
            checkOut, 
            pendingServices, 
            currentReservation
        );
    }

    private void loadReservation(Long reservationId) {
        try {
            AdminReservationLoaderHelper.ReservationLoadResult result = 
                AdminReservationLoaderHelper.loadReservationData(
                    reservationId,
                    AppConfig.createReservationRepository(),
                    billingService
                );
            
            if (result == null) {
                AlertHelper.showError("Error", "Reservation not found");
                return;
            }
            
            currentReservation = result.reservation;
            creatingNewReservation = false;
            
            // Show billing information section for existing reservations
            if (billingInformationContainer != null) {
                billingInformationContainer.setVisible(true);
                billingInformationContainer.setManaged(true);
            }
            
            pendingRooms.clear();
            pendingRooms.addAll(result.rooms);
            updateRoomTypeSummary();
            
            pendingServices.clear();
            pendingServices.addAll(result.addons);
            serviceTableData.setAll(pendingServices);

            currentBilling = result.billing;

            updateReservationDisplay();
            updateBalanceDisplay();
            updateActionButtons();
            updateRoomSelectionError(null);
        } catch (Exception e) {
            logger.logError("Failed to load reservation", e);
        }
    }

    private void updateReservationDisplay() {
        AdminReservationUIHelper.updateReservationDisplay(
            currentReservation,
            reservationSummaryLabel,
            reservationIdLabel,
            modeLabel,
            guestNameField,
            guestPhoneField,
            guestEmailField,
            numAdultsField,
            numChildrenField,
            checkInDatePicker,
            checkOutDatePicker,
            statusComboBox,
            this::updateGuestCountSummary
        );
    }

    private void updateBalanceDisplay() {
        updateBillingDisplay();
    }
    
    private void updateBillingDisplay() {
        AdminReservationUIHelper.updateBillingDisplay(
            currentBilling,
            subtotalDisplayLabel,
            taxDisplayLabel,
            discountDisplayLabel,
            totalDisplayLabel,
            paidAmountDisplayLabel,
            balanceDisplayLabel
        );
    }
    
    private void updateEstimatedBillingDisplay(double subtotal) {
        AdminReservationUIHelper.updateEstimatedBillingDisplay(
            subtotal,
            subtotalDisplayLabel,
            taxDisplayLabel,
            discountDisplayLabel,
            totalDisplayLabel,
            paidAmountDisplayLabel,
            balanceDisplayLabel
        );
    }
    
    private void showPaymentBreakdown(double originalPaidAmount) {
        AdminReservationUIHelper.showPaymentBreakdown(currentBilling, originalPaidAmount);
    }

    
    private String formatReservationStatus(String status) {
        return AdminReservationUIHelper.formatReservationStatus(status);
    }

    private boolean isCreateMode() {
        return creatingNewReservation || currentReservation == null;
    }

    @FXML
    private void cancelReservation() {
        if (currentReservation == null) {
            AlertHelper.showError("Error", "No reservation selected");
            return;
        }
        try {
            reservationService.cancelReservation(currentReservation.getId());
            AlertHelper.showInfo("Success", "Reservation cancelled successfully");
            backToDashboard();
        } catch (Exception e) {
            logger.logError("Failed to cancel reservation", e);
            AlertHelper.showError("Error", "Failed to cancel reservation: " + e.getMessage());
        }
    }

    @FXML
    private void deleteReservation() {
        if (AdminReservationEventHandler.deleteReservation(
                currentReservation,
                AppConfig.createReservationRepository(),
                logger,
                this::backToDashboard)) {
            // Success - backToDashboard already called
        }
    }

    @FXML
    private void openPaymentScreen() {
        // Get user (with fallback if currentUser is null)
        AdminUser userToUse = currentUser;
        if (userToUse == null) {
            // Try to get default admin user
            try {
                EntityManager em = AppConfig.createEntityManager();
                try {
                    AdminUserRepository adminRepo = new AdminUserRepository(em);
                    Optional<AdminUser> defaultAdmin = adminRepo.findByEmail("admin@hotel.com");
                    if (defaultAdmin.isPresent()) {
                        userToUse = defaultAdmin.get();
                        currentUser = userToUse;
                    }
                } finally {
                    em.close();
                }
            } catch (Exception e) {
                logger.logError("Failed to retrieve default admin user", e);
            }
        }
        
        final AdminUser finalUser = userToUse;
        AdminReservationEventHandler.openPaymentScreen(
            currentReservation,
            currentUser,
            logger,
            (fxmlPath) -> navigateToAdminScreen(fxmlPath, controller -> {
                if (controller instanceof AdminPaymentController paymentController) {
                    paymentController.initPaymentScreen(finalUser, currentReservation);
                }
            }),
            null
        );
    }

    @FXML
    private void openCheckoutScreen() {
        AdminReservationEventHandler.openCheckoutScreen(
            currentReservation,
            currentUser,
            (fxmlPath) -> navigateToAdminScreen(fxmlPath, controller -> {
                if (controller instanceof AdminCheckoutController checkoutController) {
                    checkoutController.initCheckoutScreen(currentUser, currentReservation);
                }
            })
        );
    }

    @FXML
    private void backToDashboard() {
        AdminReservationEventHandler.backToDashboard(
            currentUser,
            getCurrentStage(),
            controller -> {
                if (controller instanceof AdminDashboardController dashboardController) {
                    dashboardController.init(currentUser);
                }
            }
        );
    }

    private void navigateToAdminScreen(String fxmlPath, java.util.function.Consumer<Object> consumer) {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, fxmlPath, consumer);
        } catch (Exception e) {
            logger.logError("Navigation failed", e);
            AlertHelper.showError("Navigation Error", "Failed to load screen: " + e.getMessage());
        }
    }

    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (reservationSummaryLabel != null && reservationSummaryLabel.getScene() != null) {
            return getCurrentStageFromNode(reservationSummaryLabel);
        }
        if (guestNameField != null && guestNameField.getScene() != null) {
            return getCurrentStageFromNode(guestNameField);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}

