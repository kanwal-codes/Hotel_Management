package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Guest;
import com.hotel.model.RoomType;
import com.hotel.model.Waitlist;
import com.hotel.repository.GuestRepository;
import com.hotel.service.ReservationService;
import com.hotel.service.WaitlistService;
import com.hotel.util.LoggerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//
 // Controller for managing the waitlist.
 // Displays all waitlist entries with filtering and search capabilities.
 // Allows admins to convert waitlist entries to reservations when rooms become available.
//
public class AdminWaitlistController extends BaseController {

    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private ComboBox<String> roomTypeFilterComboBox;
    @FXML private TextField guestSearchField;
    @FXML private Label totalWaitlistLabel;
    @FXML private Label pendingLabel;
    @FXML private Label notifiedLabel;
    @FXML private TableView<Waitlist> waitlistTable;
    @FXML private TableColumn<Waitlist, String> guestNameColumn;
    @FXML private TableColumn<Waitlist, String> guestPhoneColumn;
    @FXML private TableColumn<Waitlist, String> roomTypeColumn;
    @FXML private TableColumn<Waitlist, LocalDate> startDateColumn;
    @FXML private TableColumn<Waitlist, LocalDate> endDateColumn;
    @FXML private TableColumn<Waitlist, String> statusColumn;
    @FXML private Button convertButton;
    @FXML private Button removeButton;

    private final LoggerService logger = LoggerService.getInstance();
    private WaitlistService waitlistService;
    private GuestRepository guestRepository;
    private ReservationService reservationService;
    private AdminUser currentUser;
    private List<Waitlist> allWaitlist = new ArrayList<>();

    @FXML
    private void initialize() {
        waitlistService = AppConfig.createWaitlistService(new com.hotel.events.RoomAvailabilityPublisher());
        guestRepository = AppConfig.createGuestRepository();
        reservationService = AppConfig.createReservationService();
        
        if (statusFilterComboBox != null) {
            statusFilterComboBox.getItems().addAll("All", "PENDING", "NOTIFIED", "CONVERTED");
            statusFilterComboBox.setValue("All");
        }
        if (roomTypeFilterComboBox != null) {
            roomTypeFilterComboBox.getItems().addAll("All", "SINGLE", "DOUBLE", "DELUXE", "PENTHOUSE");
            roomTypeFilterComboBox.setValue("All");
        }
    }

    public void init(AdminUser user) {
        this.currentUser = user;
        loadWaitlistData();
        checkNotifications();
    }

    private void loadWaitlistData() {
        try {
            allWaitlist = waitlistService.getAllWaitlist();
            displayWaitlist(allWaitlist);
            updateWaitlistStats();
        } catch (Exception e) {
            logger.logError("Failed to load waitlist data", e);
            AlertHelper.showError("Error", "Failed to load waitlist: " + e.getMessage());
        }
    }

    private void checkNotifications() {
        try {
            List<String> notifications = waitlistService.getNotifications();
            if (!notifications.isEmpty()) {
                StringBuilder message = new StringBuilder("Room Availability Notifications:\n\n");
                for (String notification : notifications) {
                    message.append("• ").append(notification).append("\n");
                }
                AlertHelper.showInfo("Room Available", message.toString());
                waitlistService.clearNotifications();
            }
        } catch (Exception e) {
            logger.logError("Failed to check notifications", e);
        }
    }

    @FXML
    private void filterWaitlist() {
        try {
            List<Waitlist> filtered = new ArrayList<>(allWaitlist);
            filtered = applyStatusFilter(filtered);
            filtered = applyRoomTypeFilter(filtered);
            filtered = applyGuestFilter(filtered);
            displayWaitlist(filtered);
            updateWaitlistStats(filtered);
        } catch (Exception e) {
            logger.logError("Failed to filter waitlist", e);
            AlertHelper.showError("Error", "Failed to filter waitlist: " + e.getMessage());
        }
    }

    @FXML
    private void clearWaitlistFilters() {
        if (statusFilterComboBox != null) statusFilterComboBox.setValue("All");
        if (roomTypeFilterComboBox != null) roomTypeFilterComboBox.setValue("All");
        if (guestSearchField != null) guestSearchField.clear();
        displayWaitlist(allWaitlist);
        updateWaitlistStats();
    }

    @FXML
    private void convertToReservation() {
        Waitlist selected = waitlistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a waitlist entry to convert.");
            return;
        }

        try {
            // Get available rooms for the requested type
            List<com.hotel.model.Room> availableRooms = reservationService.getAvailableRooms(
                selected.getRequestedType(),
                selected.getDateRangeStart(),
                selected.getDateRangeEnd()
            );

            if (availableRooms.isEmpty()) {
                AlertHelper.showWarning("No Availability", 
                    "No rooms of type " + selected.getRequestedType() + 
                    " are available for the requested date range.");
                return;
            }

            // Navigate to reservation details page with pre-filled data from waitlist
            // This allows admin to review and make changes before creating reservation
            com.hotel.controller.helper.AdminNavigationHelper.switchScene(
                getCurrentStage(), 
                "/view/admin/ReservationDetails.fxml", 
                controller -> {
                    if (controller instanceof com.hotel.controller.AdminReservationController reservationController) {
                        reservationController.initFromWaitlist(currentUser, selected);
                    }
                }
            );
            
            logger.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                "CONVERT_WAITLIST", "Waitlist", selected.getId(),
                "Navigated to reservation creation from waitlist entry");

        } catch (Exception e) {
            logger.logError("Failed to convert waitlist to reservation", e);
            AlertHelper.showError("Error", "Failed to convert waitlist: " + e.getMessage());
        }
    }

    @FXML
    private void addToWaitlist() {
        try {
            // Create a dialog to collect guest and waitlist information
            javafx.scene.control.Dialog<WaitlistEntryData> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Add Guest to Waitlist");
            dialog.setHeaderText("Enter guest and waitlist information");

            // Set button types
            javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Add to Waitlist", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);

            // Create form fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField guestNameField = new TextField();
            guestNameField.setPromptText("Guest Name");
            TextField guestPhoneField = new TextField();
            guestPhoneField.setPromptText("Phone Number");
            TextField guestEmailField = new TextField();
            guestEmailField.setPromptText("Email");
            ComboBox<RoomType> roomTypeCombo = new ComboBox<>();
            roomTypeCombo.getItems().addAll(RoomType.values());
            roomTypeCombo.setPromptText("Select Room Type");
            DatePicker startDatePicker = new DatePicker();
            startDatePicker.setPromptText("Start Date");
            DatePicker endDatePicker = new DatePicker();
            endDatePicker.setPromptText("End Date");
            Spinner<Integer> numAdultsSpinner = new Spinner<>(1, 20, 1);
            numAdultsSpinner.setEditable(true);
            Spinner<Integer> numChildrenSpinner = new Spinner<>(0, 20, 0);
            numChildrenSpinner.setEditable(true);

            grid.add(new Label("Guest Name:"), 0, 0);
            grid.add(guestNameField, 1, 0);
            grid.add(new Label("Phone:"), 0, 1);
            grid.add(guestPhoneField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(guestEmailField, 1, 2);
            grid.add(new Label("Room Type:"), 0, 3);
            grid.add(roomTypeCombo, 1, 3);
            grid.add(new Label("Start Date:"), 0, 4);
            grid.add(startDatePicker, 1, 4);
            grid.add(new Label("End Date:"), 0, 5);
            grid.add(endDatePicker, 1, 5);
            grid.add(new Label("Number of Adults:"), 0, 6);
            grid.add(numAdultsSpinner, 1, 6);
            grid.add(new Label("Number of Children:"), 0, 7);
            grid.add(numChildrenSpinner, 1, 7);

            dialog.getDialogPane().setContent(grid);

            // Enable/disable add button based on field completion
            javafx.scene.control.Button addButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(addButtonType);
            addButton.setDisable(true);

            // Validation
            Runnable validateFields = () -> {
                boolean valid = !guestNameField.getText().trim().isEmpty() &&
                               !guestPhoneField.getText().trim().isEmpty() &&
                               !guestEmailField.getText().trim().isEmpty() &&
                               roomTypeCombo.getValue() != null &&
                               startDatePicker.getValue() != null &&
                               endDatePicker.getValue() != null &&
                               endDatePicker.getValue().isAfter(startDatePicker.getValue());
                addButton.setDisable(!valid);
            };

            guestNameField.textProperty().addListener((obs, old, val) -> validateFields.run());
            guestPhoneField.textProperty().addListener((obs, old, val) -> validateFields.run());
            guestEmailField.textProperty().addListener((obs, old, val) -> validateFields.run());
            roomTypeCombo.valueProperty().addListener((obs, old, val) -> validateFields.run());
            startDatePicker.valueProperty().addListener((obs, old, val) -> validateFields.run());
            endDatePicker.valueProperty().addListener((obs, old, val) -> validateFields.run());

            // Convert result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    WaitlistEntryData data = new WaitlistEntryData();
                    data.guestName = guestNameField.getText().trim();
                    data.guestPhone = guestPhoneField.getText().trim();
                    data.guestEmail = guestEmailField.getText().trim();
                    data.roomType = roomTypeCombo.getValue();
                    data.startDate = startDatePicker.getValue();
                    data.endDate = endDatePicker.getValue();
                    data.numAdults = numAdultsSpinner.getValue();
                    data.numChildren = numChildrenSpinner.getValue();
                    return data;
                }
                return null;
            });

            Optional<WaitlistEntryData> result = dialog.showAndWait();
            if (result.isPresent()) {
                WaitlistEntryData data = result.get();
                
                // Check if guest already exists
                Optional<Guest> existingGuest = guestRepository.findByEmail(data.guestEmail);
                Guest guest;
                
                if (existingGuest.isPresent()) {
                    guest = existingGuest.get();
                    // Check if guest already has a waitlist entry
                    if (guest.getWaitlist() != null) {
                        AlertHelper.showWarning("Already on Waitlist", 
                            "This guest is already on the waitlist.");
                        return;
                    }
                    // Update guest info if needed
                    if (!guest.getName().equals(data.guestName) || !guest.getPhone().equals(data.guestPhone)) {
                        guest.setName(data.guestName);
                        guest.setPhone(data.guestPhone);
                        guestRepository.save(guest);
                    }
                } else {
                    // Create new guest - need to save it first before adding to waitlist
                    guest = new Guest(data.guestName, data.guestPhone, data.guestEmail, null);
                    guest = guestRepository.save(guest);
                }
                
                // Add to waitlist (WaitlistService will handle the transaction)
                Waitlist waitlist = waitlistService.addToWaitlist(
                    guest, data.roomType, data.startDate, data.endDate, 
                    data.numAdults, data.numChildren);
                
                // Reload data
                loadWaitlistData();
                
                AlertHelper.showInfo("Success", 
                    "Guest " + guest.getName() + " added to waitlist successfully!");
                
                logger.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                    "ADD_TO_WAITLIST", "Waitlist", waitlist.getId(),
                    "Added guest to waitlist: " + guest.getName());
            }
        } catch (Exception e) {
            logger.logError("Failed to add guest to waitlist", e);
            AlertHelper.showError("Error", "Failed to add to waitlist: " + e.getMessage());
        }
    }

    // Helper class for dialog data
    private static class WaitlistEntryData {
        String guestName;
        String guestPhone;
        String guestEmail;
        RoomType roomType;
        LocalDate startDate;
        LocalDate endDate;
        Integer numAdults;
        Integer numChildren;
    }

    @FXML
    private void removeFromWaitlist() {
        Waitlist selected = waitlistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a waitlist entry to remove.");
            return;
        }

        try {
            boolean confirmed = AlertHelper.showConfirmation("Remove Waitlist Entry",
                "Are you sure you want to remove this waitlist entry?\n\n" +
                "Guest: " + selected.getGuest().getName());

            if (!confirmed) return;

            waitlistService.removeFromWaitlist(selected.getId());
            loadWaitlistData();

            AlertHelper.showInfo("Success", "Waitlist entry removed successfully.");
            
            logger.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                "REMOVE_WAITLIST", "Waitlist", selected.getId(),
                "Removed waitlist entry");

        } catch (Exception e) {
            logger.logError("Failed to remove waitlist entry", e);
            AlertHelper.showError("Error", "Failed to remove waitlist entry: " + e.getMessage());
        }
    }

    @FXML
    @Override
    protected void goBack() {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", controller -> {
                if (controller instanceof AdminDashboardController dashboardController) {
                    dashboardController.init(currentUser);
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back to dashboard", e);
            AlertHelper.showError("Navigation Error", "Failed to load dashboard: " + e.getMessage());
        }
    }

    private List<Waitlist> applyStatusFilter(List<Waitlist> waitlist) {
        if (statusFilterComboBox != null && statusFilterComboBox.getValue() != null &&
            !"All".equals(statusFilterComboBox.getValue())) {
            String status = statusFilterComboBox.getValue();
            return waitlist.stream()
                .filter(w -> status.equalsIgnoreCase(w.getStatus()))
                .collect(Collectors.toList());
        }
        return waitlist;
    }

    private List<Waitlist> applyRoomTypeFilter(List<Waitlist> waitlist) {
        if (roomTypeFilterComboBox != null && roomTypeFilterComboBox.getValue() != null &&
            !"All".equals(roomTypeFilterComboBox.getValue())) {
            try {
                RoomType roomType = RoomType.valueOf(roomTypeFilterComboBox.getValue());
                return waitlist.stream()
                    .filter(w -> w.getRequestedType() == roomType)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException ignore) {
            }
        }
        return waitlist;
    }

    private List<Waitlist> applyGuestFilter(List<Waitlist> waitlist) {
        if (guestSearchField != null && !guestSearchField.getText().isBlank()) {
            String guestQuery = guestSearchField.getText().toLowerCase();
            return waitlist.stream()
                .filter(w -> w.getGuest() != null &&
                    (w.getGuest().getName().toLowerCase().contains(guestQuery) ||
                     w.getGuest().getPhone().contains(guestQuery)))
                .collect(Collectors.toList());
        }
        return waitlist;
    }

    private void displayWaitlist(List<Waitlist> waitlist) {
        if (waitlistTable == null) return;
        waitlistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        if (guestNameColumn != null) {
            guestNameColumn.setCellValueFactory(cell -> {
                Waitlist w = cell.getValue();
                return new SimpleStringProperty(
                    w.getGuest() != null ? w.getGuest().getName() : "N/A");
            });
        }
        if (guestPhoneColumn != null) {
            guestPhoneColumn.setCellValueFactory(cell -> {
                Waitlist w = cell.getValue();
                return new SimpleStringProperty(
                    w.getGuest() != null ? w.getGuest().getPhone() : "N/A");
            });
        }
        if (roomTypeColumn != null) {
            roomTypeColumn.setCellValueFactory(cell -> {
                Waitlist w = cell.getValue();
                return new SimpleStringProperty(
                    w.getRequestedType() != null ? w.getRequestedType().toString() : "N/A");
            });
        }
        if (startDateColumn != null) {
            startDateColumn.setCellValueFactory(cell -> {
                Waitlist w = cell.getValue();
                return new SimpleObjectProperty<>(w.getDateRangeStart());
            });
        }
        if (endDateColumn != null) {
            endDateColumn.setCellValueFactory(cell -> {
                Waitlist w = cell.getValue();
                return new SimpleObjectProperty<>(w.getDateRangeEnd());
            });
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cell -> {
                Waitlist w = cell.getValue();
                return new SimpleStringProperty(w.getStatus() != null ? w.getStatus() : "PENDING");
            });
        }
        
        ObservableList<Waitlist> data = FXCollections.observableArrayList(waitlist);
        waitlistTable.setItems(data);
    }

    private void updateWaitlistStats() {
        updateWaitlistStats(allWaitlist);
    }

    private void updateWaitlistStats(List<Waitlist> waitlist) {
        if (totalWaitlistLabel != null) {
            totalWaitlistLabel.setText(String.valueOf(waitlist.size()));
        }
        if (pendingLabel != null) {
            long pending = waitlist.stream().filter(w -> "PENDING".equals(w.getStatus())).count();
            pendingLabel.setText(String.valueOf(pending));
        }
        if (notifiedLabel != null) {
            long notified = waitlist.stream().filter(w -> "NOTIFIED".equals(w.getStatus())).count();
            notifiedLabel.setText(String.valueOf(notified));
        }
    }

    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (waitlistTable != null && waitlistTable.getScene() != null) {
            return getCurrentStageFromNode(waitlistTable);
        }
        if (statusFilterComboBox != null && statusFilterComboBox.getScene() != null) {
            return getCurrentStageFromNode(statusFilterComboBox);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}

