package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.Guest;
import com.hotel.model.Payment;
import com.hotel.model.Billing;
import com.hotel.model.Reservation;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.PaymentRepository;
import com.hotel.repository.BillingRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.LoyaltyService;
import com.hotel.util.LoggerService;
import com.hotel.util.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

//
 // Controller for the loyalty program dashboard.
 // Shows guest loyalty points balance, earning history, and redemption history.
 // Allows admins to search for guests and view their loyalty program status.
//
public class LoyaltyController extends BaseController {
    
    private LoyaltyService loyaltyService;
    private GuestRepository guestRepository;
    private PaymentRepository paymentRepository;
    private BillingRepository billingRepository;
    private ReservationRepository reservationRepository;
    private LoggerService logger;
    private EntityManager em;
    private Guest currentGuest;
    
    // ========== LoyaltyProgram.fxml ==========
    @FXML private Button backButton;
    @FXML private TabPane mainTabPane;
    
    // Enrolled Guests Tab
    @FXML private TableView<Guest> enrolledGuestsTable;
    @FXML private TableColumn<Guest, String> enrolledGuestNameColumn;
    @FXML private TableColumn<Guest, String> enrolledGuestEmailColumn;
    @FXML private TableColumn<Guest, String> enrolledGuestPhoneColumn;
    @FXML private TableColumn<Guest, String> enrolledLoyaltyNumberColumn;
    @FXML private TableColumn<Guest, Integer> enrolledPointsColumn;
    @FXML private VBox enrolledGuestDetailsContainer;
    @FXML private VBox enrolledGuestInfoContainer;
    @FXML private Label enrolledGuestInfoLabel;
    @FXML private VBox loyaltyDashboardContainer;
    @FXML private Label currentBalanceLabel;
    @FXML private TableView<Map<String, Object>> earningHistoryTable;
    @FXML private TableView<Map<String, Object>> redemptionHistoryTable;
    
    // Enroll People Tab
    @FXML private TableView<Guest> nonEnrolledGuestsTable;
    @FXML private TableColumn<Guest, String> nonEnrolledGuestNameColumn;
    @FXML private TableColumn<Guest, String> nonEnrolledGuestEmailColumn;
    @FXML private TableColumn<Guest, String> nonEnrolledGuestPhoneColumn;
    @FXML private TableColumn<Guest, String> hasAccountColumn;
    @FXML private VBox enrollGuestInfoContainer;
    @FXML private Label enrollGuestInfoLabel;
    @FXML private Button enrollButton;
    
    private ObservableList<Guest> enrolledGuestsList = FXCollections.observableArrayList();
    private ObservableList<Guest> nonEnrolledGuestsList = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> earningHistoryData = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> redemptionHistoryData = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        // Initialize services
        em = AppConfig.createEntityManager();
        loyaltyService = AppConfig.createLoyaltyService();
        guestRepository = AppConfig.createGuestRepository();
        paymentRepository = AppConfig.createPaymentRepository();
        billingRepository = AppConfig.createBillingRepository();
        reservationRepository = AppConfig.createReservationRepository();
        logger = LoggerService.getInstance();
        
        // Setup tables
        setupEarningHistoryTable();
        setupRedemptionHistoryTable();
        setupEnrolledGuestsTable();
        setupNonEnrolledGuestsTable();
        
        // Load enrolled guests by default (Tab 1)
        loadEnrolledGuests();
        
        // Hide detail containers initially
        if (enrolledGuestDetailsContainer != null) {
            enrolledGuestDetailsContainer.setVisible(false);
            enrolledGuestDetailsContainer.setManaged(false);
        }
        if (enrollGuestInfoContainer != null) {
            enrollGuestInfoContainer.setVisible(false);
            enrollGuestInfoContainer.setManaged(false);
        }
        if (loyaltyDashboardContainer != null) {
            loyaltyDashboardContainer.setVisible(false);
            loyaltyDashboardContainer.setManaged(false);
        }
    }
    
    //
     // Setup enrolled guests table columns
//
    private void setupEnrolledGuestsTable() {
        if (enrolledGuestsTable == null) return;
        
        if (enrolledGuestNameColumn != null) {
            enrolledGuestNameColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        }
        if (enrolledGuestEmailColumn != null) {
            enrolledGuestEmailColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : ""));
        }
        if (enrolledGuestPhoneColumn != null) {
            enrolledGuestPhoneColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getPhone() != null ? cellData.getValue().getPhone() : ""));
        }
        if (enrolledLoyaltyNumberColumn != null) {
            enrolledLoyaltyNumberColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getLoyaltyNumber() != null ? cellData.getValue().getLoyaltyNumber() : ""));
        }
        if (enrolledPointsColumn != null) {
            enrolledPointsColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getLoyaltyPoints()).asObject());
        }
        
        enrolledGuestsTable.setItems(enrolledGuestsList);
    }
    
    //
     // Setup non-enrolled guests table columns
//
    private void setupNonEnrolledGuestsTable() {
        if (nonEnrolledGuestsTable == null) return;
        
        if (nonEnrolledGuestNameColumn != null) {
            nonEnrolledGuestNameColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        }
        if (nonEnrolledGuestEmailColumn != null) {
            nonEnrolledGuestEmailColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEmail() != null ? cellData.getValue().getEmail() : ""));
        }
        if (nonEnrolledGuestPhoneColumn != null) {
            nonEnrolledGuestPhoneColumn.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getPhone() != null ? cellData.getValue().getPhone() : ""));
        }
        if (hasAccountColumn != null) {
            hasAccountColumn.setCellValueFactory(cellData -> {
                Guest guest = cellData.getValue();
                boolean hasAccount = guest.getEmail() != null && !guest.getEmail().isEmpty() &&
                                     guest.getCustomerPasswordHash() != null && !guest.getCustomerPasswordHash().isEmpty();
                return new javafx.beans.property.SimpleStringProperty(hasAccount ? "Yes" : "No");
            });
        }
        
        nonEnrolledGuestsTable.setItems(nonEnrolledGuestsList);
    }
    
    //
     // Load enrolled guests when Enrolled Guests tab is selected
//
    @FXML
    private void onEnrolledGuestsTabSelected() {
        loadEnrolledGuests();
        // Don't clear currentGuest - keep it so Guest Details tab can still show it
    }
    
    //
     // Load non-enrolled guests with accounts when Enroll People tab is selected
//
    @FXML
    private void onEnrollPeopleTabSelected() {
        loadNonEnrolledGuestsWithAccounts();
        // Hide enrollment info when switching tabs
        if (enrollGuestInfoContainer != null) {
            enrollGuestInfoContainer.setVisible(false);
            enrollGuestInfoContainer.setManaged(false);
        }
    }
    
    //
     // Load all enrolled guests
//
    private void loadEnrolledGuests() {
        try {
            List<Guest> enrolled = guestRepository.findEnrolledGuests();
            enrolledGuestsList.clear();
            enrolledGuestsList.addAll(enrolled);
            logger.logInfo("Loaded " + enrolled.size() + " enrolled guests");
        } catch (Exception e) {
            logger.logError("Failed to load enrolled guests", e);
            showAlert("Error", "Failed to load enrolled guests: " + e.getMessage());
        }
    }
    
    //
     // Load non-enrolled guests who have accounts (email and password)
//
    private void loadNonEnrolledGuestsWithAccounts() {
        try {
            List<Guest> nonEnrolled = guestRepository.findNonEnrolledGuestsWithAccounts();
            nonEnrolledGuestsList.clear();
            nonEnrolledGuestsList.addAll(nonEnrolled);
            logger.logInfo("Loaded " + nonEnrolled.size() + " non-enrolled guests with accounts");
        } catch (Exception e) {
            logger.logError("Failed to load non-enrolled guests", e);
            showAlert("Error", "Failed to load guests: " + e.getMessage());
        }
    }
    
    //
     // Handle click on enrolled guest - switch to Guest Details tab and show their details
//
    @FXML
    private void selectEnrolledGuest() {
        if (enrolledGuestsTable == null) return;
        
        Guest selectedGuest = enrolledGuestsTable.getSelectionModel().getSelectedItem();
        if (selectedGuest != null) {
            currentGuest = selectedGuest;
            // Switch to Guest Details tab (index 1)
            if (mainTabPane != null && mainTabPane.getTabs().size() > 1) {
                mainTabPane.getSelectionModel().select(1); // Guest Details tab
            }
            displayEnrolledGuestDetails(selectedGuest);
            logger.logInfo("Selected enrolled guest: " + selectedGuest.getName());
        }
    }
    
    //
     // Called when Guest Details tab is selected
//
    @FXML
    private void onGuestDetailsTabSelected() {
        // If a guest is already selected, refresh their details
        if (currentGuest != null) {
            displayEnrolledGuestDetails(currentGuest);
        } else {
            // No guest selected, hide details
            if (enrolledGuestDetailsContainer != null) {
                enrolledGuestDetailsContainer.setVisible(false);
                enrolledGuestDetailsContainer.setManaged(false);
        }
        if (loyaltyDashboardContainer != null) {
            loyaltyDashboardContainer.setVisible(false);
                loyaltyDashboardContainer.setManaged(false);
            }
        }
    }
    
    //
     // Handle click on non-enrolled guest - show their info for enrollment
//
    @FXML
    private void selectNonEnrolledGuest() {
        if (nonEnrolledGuestsTable == null) return;
        
        Guest selectedGuest = nonEnrolledGuestsTable.getSelectionModel().getSelectedItem();
        if (selectedGuest != null) {
            currentGuest = selectedGuest;
            displayNonEnrolledGuestInfo(selectedGuest);
            logger.logInfo("Selected non-enrolled guest: " + selectedGuest.getName());
        }
    }
    
    //
     // Display details for enrolled guest
//
    private void displayEnrolledGuestDetails(Guest guest) {
        if (enrolledGuestDetailsContainer != null) {
            enrolledGuestDetailsContainer.setVisible(true);
            enrolledGuestDetailsContainer.setManaged(true);
        }
        
        // Reload guest from database to ensure it's a managed entity
        // This is important for lazy loading relationships
        Guest managedGuest = null;
        if (guest != null && guest.getId() != null) {
            try {
                Optional<Guest> guestOpt = guestRepository.findById(guest.getId());
                if (guestOpt.isPresent()) {
                    managedGuest = guestOpt.get();
                    currentGuest = managedGuest; // Update current guest reference
                } else {
                    managedGuest = guest; // Fallback to original if not found
                }
            } catch (Exception e) {
                logger.logError("Failed to reload guest from database", e);
                managedGuest = guest; // Fallback to original
            }
        } else {
            managedGuest = guest;
        }
        
        if (enrolledGuestInfoLabel != null && managedGuest != null) {
            enrolledGuestInfoLabel.setText(
                "Guest Information:\n\n" +
                "Name: " + managedGuest.getName() + "\n" +
                "Email: " + (managedGuest.getEmail() != null ? managedGuest.getEmail() : "N/A") + "\n" +
                "Phone: " + (managedGuest.getPhone() != null ? managedGuest.getPhone() : "N/A") + "\n" +
                "Address: " + (managedGuest.getAddress() != null ? managedGuest.getAddress() : "N/A") + "\n" +
                "Loyalty Number: " + (managedGuest.getLoyaltyNumber() != null ? managedGuest.getLoyaltyNumber() : "N/A") + "\n" +
                "Points: " + managedGuest.getLoyaltyPoints()
            );
        }
        
        // Show loyalty dashboard with managed guest
        if (managedGuest != null) {
            displayLoyaltyDashboard(managedGuest);
        }
    }
    
    //
     // Display info for non-enrolled guest (for enrollment)
//
    private void displayNonEnrolledGuestInfo(Guest guest) {
        if (enrollGuestInfoContainer != null) {
            enrollGuestInfoContainer.setVisible(true);
            enrollGuestInfoContainer.setManaged(true);
        }
        
        if (enrollGuestInfoLabel != null) {
            boolean hasAccount = guest.getEmail() != null && !guest.getEmail().isEmpty() &&
                                guest.getCustomerPasswordHash() != null && !guest.getCustomerPasswordHash().isEmpty();
            
            enrollGuestInfoLabel.setText(
                "Guest Information:\n\n" +
                "Name: " + guest.getName() + "\n" +
                "Email: " + (guest.getEmail() != null ? guest.getEmail() : "N/A") + "\n" +
                "Phone: " + (guest.getPhone() != null ? guest.getPhone() : "N/A") + "\n" +
                "Address: " + (guest.getAddress() != null ? guest.getAddress() : "N/A") + "\n" +
                "Has Account: " + (hasAccount ? "Yes (Eligible for enrollment)" : "No (Account required)")
            );
        }
        
        // Enable/disable enroll button based on account status
        if (enrollButton != null) {
            boolean hasAccount = guest.getEmail() != null && !guest.getEmail().isEmpty() &&
                                guest.getCustomerPasswordHash() != null && !guest.getCustomerPasswordHash().isEmpty();
            enrollButton.setDisable(!hasAccount);
            if (!hasAccount) {
                enrollButton.setTooltip(new Tooltip("Guest must have an account (email and password) to enroll"));
            }
        }
    }
    
    // ========== Navigation Methods ==========
    @FXML
    @Override
    protected void goBack() {
        try {
            javafx.stage.Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", controller -> {
                if (controller instanceof AdminDashboardController dashboardController) {
                    // Get current user from context if available
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back to dashboard", e);
            showAlert("Error", "Failed to navigate back: " + e.getMessage());
        }
    }
    
    //
     // Gets the current stage for navigation.
     // Overrides base method to use loyalty-specific fields with complex fallback logic.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try fallback nodes first (if provided)
        for (Node node : fallbackNodes) {
            if (node != null && node.getScene() != null) {
                return getCurrentStageFromNode(node);
            }
        }
        
        // Try the back button first (always visible in header)
        if (backButton != null && backButton.getScene() != null) {
            return getCurrentStageFromNode(backButton);
        }
        
        // Try multiple nodes from both tabs to find the stage
        Node node = null;
        
        // Try TabPane first
        if (mainTabPane != null && mainTabPane.getScene() != null) {
            node = mainTabPane;
        }
        
        // Try nodes from Enrolled Guests tab
        if (node == null) {
            if (enrolledGuestsTable != null && enrolledGuestsTable.getScene() != null) {
                node = enrolledGuestsTable;
            } else if (enrolledGuestInfoLabel != null && enrolledGuestInfoLabel.getScene() != null) {
                node = enrolledGuestInfoLabel;
            } else if (enrolledGuestDetailsContainer != null && enrolledGuestDetailsContainer.getScene() != null) {
                node = enrolledGuestDetailsContainer;
            }
        }
        
        // Try nodes from Enroll People tab
        if (node == null) {
            if (nonEnrolledGuestsTable != null && nonEnrolledGuestsTable.getScene() != null) {
                node = nonEnrolledGuestsTable;
            } else if (enrollGuestInfoLabel != null && enrollGuestInfoLabel.getScene() != null) {
                node = enrollGuestInfoLabel;
            } else if (enrollGuestInfoContainer != null && enrollGuestInfoContainer.getScene() != null) {
                node = enrollGuestInfoContainer;
            }
        }
        
        // Try nodes from Loyalty Dashboard
        if (node == null) {
            if (currentBalanceLabel != null && currentBalanceLabel.getScene() != null) {
                node = currentBalanceLabel;
            } else if (earningHistoryTable != null && earningHistoryTable.getScene() != null) {
                node = earningHistoryTable;
            } else if (redemptionHistoryTable != null && redemptionHistoryTable.getScene() != null) {
                node = redemptionHistoryTable;
            } else if (loyaltyDashboardContainer != null && loyaltyDashboardContainer.getScene() != null) {
                node = loyaltyDashboardContainer;
            }
        }
        
        // If still not found, try to get from any button
        if (node == null && enrollButton != null && enrollButton.getScene() != null) {
                node = enrollButton;
        }
        
        if (node != null && node.getScene() != null) {
            return getCurrentStageFromNode(node);
        }
        
        throw new IllegalStateException("Unable to determine current stage");
    }
    
    // ========== Guest Enrollment Methods ==========
    
    @FXML
    private void enrollGuest() {
        if (currentGuest == null) {
            showAlert("Error", "Please select a guest first");
            return;
        }
        
        try {
            // Check if already enrolled (has loyalty number)
            if (currentGuest.getLoyaltyNumber() != null && !currentGuest.getLoyaltyNumber().isEmpty()) {
                showAlert("Already Enrolled", 
                    "Guest is already enrolled in loyalty program.\n" +
                    "Loyalty Number: " + currentGuest.getLoyaltyNumber() + "\n" +
                    "Current Points: " + currentGuest.getLoyaltyPoints());
                return;
            }
            
            // Verify guest has account (email and password)
            boolean hasAccount = currentGuest.getEmail() != null && !currentGuest.getEmail().isEmpty() &&
                               currentGuest.getCustomerPasswordHash() != null && !currentGuest.getCustomerPasswordHash().isEmpty();
            
            if (!hasAccount) {
                showAlert("Account Required", 
                    "Guest must have an account (email and password) to enroll in the loyalty program.\n\n" +
                    "Please ensure the guest has registered with an email and password.");
                return;
            }
            
            // Generate loyalty number (simple format: L + guest ID)
            String loyaltyNumber = "L" + String.format("%06d", currentGuest.getId());
            currentGuest.setLoyaltyNumber(loyaltyNumber);
            currentGuest.setLoyaltyPoints(0); // Start with 0 points
            
            guestRepository.save(currentGuest);
            
            showAlert("Success", 
                "Guest enrolled successfully!\n\n" +
                "Name: " + currentGuest.getName() + "\n" +
                "Loyalty Number: " + loyaltyNumber + "\n" +
                "Initial Points: 0");
            
            // Refresh lists
            loadNonEnrolledGuestsWithAccounts();
            loadEnrolledGuests();
            
            // Hide enrollment info
            if (enrollGuestInfoContainer != null) {
                enrollGuestInfoContainer.setVisible(false);
                enrollGuestInfoContainer.setManaged(false);
            }
            
            logger.logActivity("SYSTEM", "ENROLL_GUEST", "Guest", currentGuest.getId(), 
                "Guest enrolled with loyalty number: " + loyaltyNumber);
            
        } catch (Exception e) {
            logger.logError("Failed to enroll guest", e);
            showAlert("Error", "Failed to enroll guest: " + e.getMessage());
        }
    }
    
    private void displayLoyaltyDashboard(Guest guest) {
        if (loyaltyDashboardContainer != null) {
            loyaltyDashboardContainer.setVisible(true);
            loyaltyDashboardContainer.setManaged(true);
        }
        
        if (currentBalanceLabel != null) {
            int points = loyaltyService.getBalance(guest);
            currentBalanceLabel.setText("Current Points Balance: " + points);
        }
        
        // Populate earning history table
        populateEarningHistory(guest);
        
        // Populate redemption history table
        populateRedemptionHistory(guest);
        
        logger.logInfo("Displayed loyalty dashboard for: " + guest.getName());
    }
    
    private void setupEarningHistoryTable() {
        if (earningHistoryTable == null) return;
        
        earningHistoryTable.getColumns().clear();
        
        TableColumn<Map<String, Object>, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().get("date").toString()));
        
        TableColumn<Map<String, Object>, String> amountCol = new TableColumn<>("Payment Amount");
        amountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                "$" + String.format("%.2f", cellData.getValue().get("amount"))));
        
        TableColumn<Map<String, Object>, String> pointsCol = new TableColumn<>("Points Earned");
        pointsCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().get("points").toString()));
        
        earningHistoryTable.getColumns().addAll(dateCol, amountCol, pointsCol);
        earningHistoryTable.setItems(earningHistoryData);
    }
    
    private void setupRedemptionHistoryTable() {
        if (redemptionHistoryTable == null) return;
        
        redemptionHistoryTable.getColumns().clear();
        
        TableColumn<Map<String, Object>, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().get("date").toString()));
        
        TableColumn<Map<String, Object>, String> pointsCol = new TableColumn<>("Points Redeemed");
        pointsCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().get("points").toString()));
        
        TableColumn<Map<String, Object>, String> discountCol = new TableColumn<>("Discount Amount");
        discountCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                "$" + String.format("%.2f", cellData.getValue().get("discount"))));
        
        TableColumn<Map<String, Object>, String> reservationCol = new TableColumn<>("Reservation ID");
        reservationCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().get("reservationId").toString()));
        
        redemptionHistoryTable.getColumns().addAll(dateCol, pointsCol, discountCol, reservationCol);
        redemptionHistoryTable.setItems(redemptionHistoryData);
    }
    
    private void populateEarningHistory(Guest guest) {
        earningHistoryData.clear();
        
        if (guest == null || guest.getId() == null) {
            logger.logWarning("Cannot populate earning history: guest is null or has no ID");
            return;
        }
        
        try {
            // Use guest ID to find reservations (more reliable than using detached guest entity)
            List<Reservation> reservations = reservationRepository.findByGuest(guest);
            
            logger.logInfo("Found " + reservations.size() + " reservations for guest " + guest.getId());
            
            for (Reservation reservation : reservations) {
                // Get billing for reservation
                Optional<Billing> billingOpt = billingRepository.findByReservation(reservation);
                if (billingOpt.isPresent()) {
                    Billing billing = billingOpt.get();
                    // Get all payments for this billing
                    List<Payment> payments = paymentRepository.findByBilling(billing);
                    
                    logger.logInfo("Found " + payments.size() + " payments for reservation " + reservation.getId());
                    
                    for (Payment payment : payments) {
                        // Only count non-points payments for earning
                        if (payment.getMethod() != com.hotel.model.PaymentMethod.POINTS && payment.getAmount() > 0) {
                            // Calculate points earned (1 point per $10)
                            int pointsEarned = (int) (payment.getAmount() / 10.0);
                            
                            Map<String, Object> row = new HashMap<>();
                            row.put("date", payment.getCreatedAt() != null ? payment.getCreatedAt().toString() : "N/A");
                            row.put("amount", payment.getAmount());
                            row.put("points", pointsEarned);
                            earningHistoryData.add(row);
                        }
                    }
                } else {
                    logger.logInfo("No billing found for reservation " + reservation.getId());
                }
            }
            
            logger.logInfo("Populated " + earningHistoryData.size() + " earning history entries");
        } catch (Exception e) {
            logger.logError("Failed to populate earning history", e);
            e.printStackTrace(); // Add stack trace for debugging
        }
    }
    
    private void populateRedemptionHistory(Guest guest) {
        redemptionHistoryData.clear();
        
        if (guest == null || guest.getId() == null) {
            logger.logWarning("Cannot populate redemption history: guest is null or has no ID");
            return;
        }
        
        try {
            // Use guest ID to find reservations (more reliable than using detached guest entity)
            List<Reservation> reservations = reservationRepository.findByGuest(guest);
            
            logger.logInfo("Found " + reservations.size() + " reservations for guest " + guest.getId() + " (redemption history)");
            
            for (Reservation reservation : reservations) {
                // Get billing for reservation
                Optional<Billing> billingOpt = billingRepository.findByReservation(reservation);
                if (billingOpt.isPresent()) {
                    Billing billing = billingOpt.get();
                    
                    // Check if loyalty points were redeemed
                    if (billing.getLoyaltyRedeemedPoints() > 0) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("date", reservation.getCheckIn() != null ? reservation.getCheckIn().toString() : "N/A");
                        row.put("points", billing.getLoyaltyRedeemedPoints());
                        
                        // Calculate discount amount (100 points = 1% discount)
                        double discountPercent = Math.min(billing.getLoyaltyRedeemedPoints() / 100.0, 20.0);
                        double discountAmount = billing.getSubtotal() * (discountPercent / 100.0);
                        row.put("discount", discountAmount);
                        row.put("reservationId", reservation.getId() != null ? reservation.getId() : "N/A");
                        
                        redemptionHistoryData.add(row);
                    }
                } else {
                    logger.logInfo("No billing found for reservation " + reservation.getId() + " (redemption history)");
                }
            }
            
            logger.logInfo("Populated " + redemptionHistoryData.size() + " redemption history entries");
        } catch (Exception e) {
            logger.logError("Failed to populate redemption history", e);
            e.printStackTrace(); // Add stack trace for debugging
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
