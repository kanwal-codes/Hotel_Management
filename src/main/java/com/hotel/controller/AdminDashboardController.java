package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;
import com.hotel.service.ActivityLogService;
import com.hotel.util.LoggerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//
 // Controller for the admin dashboard.
 // Displays all reservations with search, filter, and pagination capabilities.
 // Allows admins to view and navigate to detailed reservation management screens.
//
public class AdminDashboardController extends BaseController {

    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> sortByComboBox;
    @FXML private ComboBox<String> itemsPerPageComboBox;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> guestNameColumn;
    @FXML private TableColumn<Reservation, String> phoneColumn;
    @FXML private TableColumn<Reservation, LocalDate> checkInColumn;
    @FXML private TableColumn<Reservation, LocalDate> checkOutColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> balanceColumn;
    @FXML private TableColumn<Reservation, String> actionsColumn;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;

    private final LoggerService logger = LoggerService.getInstance();
    private ActivityLogService activityLogService;
    private ReservationRepository reservationRepository;
    private AdminUser currentUser;
    private List<Reservation> allReservations = new ArrayList<>();
    private int currentPage = 0;
    private int itemsPerPage = 10;

    @FXML
    private void initialize() {
        reservationRepository = AppConfig.createReservationRepository();
        activityLogService = AppConfig.createActivityLogService();
        if (statusFilterComboBox != null) {
            statusFilterComboBox.getItems().addAll("All", "Pending", "Confirmed", "Cancelled", "Checked Out");
            statusFilterComboBox.setValue("All");
        }
        if (sortByComboBox != null) {
            sortByComboBox.getItems().addAll("Check-in Date", "Check-out Date", "Guest Name", "Status");
            sortByComboBox.setValue("Check-in Date");
        }
        if (itemsPerPageComboBox != null) {
            itemsPerPageComboBox.getItems().addAll("10", "25", "50", "100");
            itemsPerPageComboBox.setValue(String.valueOf(itemsPerPage));
        }
        configureTable();
    }

    public void init(AdminUser user) {
        this.currentUser = user;
        if (welcomeLabel != null && currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        }
        loadAllReservations();
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = getCurrentStage();
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/kiosk/KioskWelcome.fxml"));
            stage.setScene(new Scene(root, 1200, 800));
            if (activityLogService != null) {
                activityLogService.logActivity(currentUser != null ? currentUser.getUsername() : "SYSTEM",
                    "LOGOUT", "AdminUser", null, "User logged out");
            }
        } catch (Exception e) {
            logger.logError("Failed to logout", e);
            AlertHelper.showError("Error", "Failed to logout: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBackToKiosk() {
        try {
            Stage stage = getCurrentStage();
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/kiosk/KioskWelcome.fxml"));
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            logger.logError("Failed to navigate back", e);
            AlertHelper.showError("Error", "Failed to navigate: " + e.getMessage());
        }
    }

    @FXML
    private void createReservation() {
        openReservationScreen(null);
    }

    @FXML
    private void viewReports() {
        try {
            Stage stage = getCurrentStage();
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/admin/ReportsScreen.fxml"));
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            logger.logError("Failed to navigate to reports", e);
            AlertHelper.showError("Error", "Failed to load reports: " + e.getMessage());
        }
    }


    @FXML
    private void manageWaitlist() {
        switchScene("/view/admin/WaitlistManagement.fxml", controller -> {
            if (controller instanceof AdminWaitlistController waitlistController) {
                waitlistController.init(currentUser);
            }
        });
    }

    @FXML
    private void loyaltyDashboard() {
        switchScene("/view/admin/LoyaltyProgram.fxml", controller -> {});
    }

    @FXML
    private void manageFeedback() {
        switchScene("/view/admin/FeedbackManagement.fxml", controller -> {
            if (controller instanceof AdminFeedbackController feedbackController) {
                feedbackController.init(currentUser);
            }
        });
    }

    @FXML
    private void searchReservations() {
        try {
            // Start with all reservations
            List<Reservation> results = reservationRepository.findAll();
            
            // Apply search filter (name/phone)
            String searchQuery = searchField != null ? searchField.getText().trim() : "";
            if (!searchQuery.isEmpty()) {
                results = results.stream()
                    .filter(r -> {
                        if (r.getGuest() == null) return false;
                        String name = r.getGuest().getName() != null ? r.getGuest().getName().toLowerCase() : "";
                        String phone = r.getGuest().getPhone() != null ? r.getGuest().getPhone().toLowerCase() : "";
                        String queryLower = searchQuery.toLowerCase();
                        return name.contains(queryLower) || phone.contains(queryLower);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply date range filter
            LocalDate startDate = startDatePicker != null ? startDatePicker.getValue() : null;
            LocalDate endDate = endDatePicker != null ? endDatePicker.getValue() : null;
            
            if (startDate != null || endDate != null) {
                // If only one date is provided, use it for both start and end
                if (startDate == null) startDate = endDate;
                if (endDate == null) endDate = startDate;
                
                // Filter reservations that overlap with the date range
                // A reservation overlaps if: checkIn <= endDate AND checkOut >= startDate
                LocalDate finalStartDate = startDate;
                LocalDate finalEndDate = endDate;
                results = results.stream()
                    .filter(r -> {
                        if (r.getCheckIn() == null || r.getCheckOut() == null) return false;
                        // Reservation overlaps with date range if:
                        // reservation check-in is before or on the end date AND
                        // reservation check-out is on or after the start date
                        return !r.getCheckIn().isAfter(finalEndDate) && !r.getCheckOut().isBefore(finalStartDate);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Apply status filter
            String statusFilter = statusFilterComboBox != null && statusFilterComboBox.getValue() != null 
                ? statusFilterComboBox.getValue() : "All";
            if (!statusFilter.equals("All")) {
                final String finalStatusFilter = statusFilter;
                results = results.stream()
                    .filter(r -> {
                        if (r.getStatus() == null) return false;
                        String statusName = r.getStatus().name();
                        // Map UI status names to enum values
                        switch (finalStatusFilter) {
                            case "Pending":
                                return statusName.equals("PENDING");
                            case "Confirmed":
                                return statusName.equals("CONFIRMED");
                            case "Cancelled":
                                return statusName.equals("CANCELLED");
                            case "Checked Out":
                                return statusName.equals("CHECKED_OUT");
                            default:
                                return true;
                        }
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            displayReservations(results);
        } catch (Exception e) {
            logger.logError("Failed to search/filter reservations", e);
            AlertHelper.showError("Error", "Failed to filter reservations: " + e.getMessage());
        }
    }

    @FXML
    private void clearSearch() {
        if (searchField != null) searchField.clear();
        if (statusFilterComboBox != null) statusFilterComboBox.setValue("All");
        if (startDatePicker != null) startDatePicker.setValue(null);
        if (endDatePicker != null) endDatePicker.setValue(null);
        loadAllReservations();
    }

    @FXML
    private void sortReservations() {
        if (sortByComboBox == null || sortByComboBox.getValue() == null) {
            return;
        }
        String sortBy = sortByComboBox.getValue();
        List<Reservation> sorted = new ArrayList<>(allReservations);
        switch (sortBy) {
            case "Check-in Date":
                sorted.sort(Comparator.comparing(Reservation::getCheckIn));
                break;
            case "Check-out Date":
                sorted.sort(Comparator.comparing(Reservation::getCheckOut));
                break;
            case "Guest Name":
                sorted.sort(Comparator.comparing(r -> r.getGuest() != null ? r.getGuest().getName() : "",
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
                break;
            case "Status":
                sorted.sort(Comparator.comparing(Reservation::getStatus));
                break;
            default:
                break;
        }
        displayReservations(sorted);
    }

    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePagination();
        }
    }

    @FXML
    private void nextPage() {
        int totalPages = (int) Math.ceil((double) allReservations.size() / itemsPerPage);
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePagination();
        }
    }

    @FXML
    private void changeItemsPerPage() {
        if (itemsPerPageComboBox != null && itemsPerPageComboBox.getValue() != null) {
            try {
                itemsPerPage = Integer.parseInt(itemsPerPageComboBox.getValue());
                currentPage = 0;
                updatePagination();
            } catch (NumberFormatException e) {
                logger.logError("Invalid items per page", e);
            }
        }
    }

    private void loadAllReservations() {
        try {
            allReservations = reservationRepository.findAll();
            displayReservations(allReservations);
        } catch (Exception e) {
            logger.logError("Failed to load reservations", e);
            AlertHelper.showError("Error", "Failed to load reservations: " + e.getMessage());
        }
    }

    private void configureTable() {
        if (reservationsTable == null) return;
        // Disable the filler column
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (guestNameColumn != null) {
            guestNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGuest() != null ? cell.getValue().getGuest().getName() : ""));
        }
        if (phoneColumn != null) {
            phoneColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGuest() != null ? cell.getValue().getGuest().getPhone() : ""));
        }
        if (checkInColumn != null) {
            checkInColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("checkIn"));
        }
        if (checkOutColumn != null) {
            checkOutColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("checkOut"));
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStatus() != null ? cell.getValue().getStatus().name() : ""));
        }
        if (balanceColumn != null) {
            balanceColumn.setCellValueFactory(cell -> {
                if (cell.getValue().getBilling() != null) {
                    return new SimpleStringProperty(
                        String.format("$%.2f", cell.getValue().getBilling().getBalanceAmount()));
                }
                return new SimpleStringProperty("$0.00");
            });
        }
        if (actionsColumn != null) {
            actionsColumn.setCellFactory(column -> new TableCell<>() {
                private final Button viewBtn = new Button("View");
                private final Button paymentBtn = new Button("Payment");
                {
                    viewBtn.setOnAction(e -> {
                        Reservation res = getTableView().getItems().get(getIndex());
                        openReservationScreen(res.getId());
                    });
                    paymentBtn.setOnAction(e -> {
                        Reservation res = getTableView().getItems().get(getIndex());
                        openPaymentScreen(res);
                    });
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox hbox = new HBox(5, viewBtn, paymentBtn);
                        setGraphic(hbox);
                    }
                }
            });
        }
    }

    private void displayReservations(List<Reservation> reservations) {
        if (reservationsTable == null) return;
        allReservations = new ArrayList<>(reservations);
        updatePagination();
    }

    private void updatePagination() {
        if (reservationsTable == null) return;
        if (allReservations.isEmpty()) {
            reservationsTable.setItems(FXCollections.observableArrayList());
            if (pageLabel != null) pageLabel.setText("Page 1 of 1");
            if (prevButton != null) prevButton.setDisable(true);
            if (nextButton != null) nextButton.setDisable(true);
            return;
        }
        int totalPages = (int) Math.ceil((double) allReservations.size() / itemsPerPage);
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allReservations.size());
        ObservableList<Reservation> pageData = FXCollections.observableArrayList(allReservations.subList(start, end));
        reservationsTable.setItems(pageData);
        if (pageLabel != null) {
            pageLabel.setText("Page " + (currentPage + 1) + " of " + Math.max(totalPages, 1));
        }
        if (prevButton != null) prevButton.setDisable(currentPage == 0);
        if (nextButton != null) nextButton.setDisable(currentPage >= totalPages - 1);
    }

    private void openReservationScreen(Long reservationId) {
        switchScene("/view/admin/ReservationDetails.fxml", controller -> {
            if (controller instanceof AdminReservationController reservationController) {
                if (reservationId == null) {
                    reservationController.initForCreate(currentUser);
                } else {
                    reservationController.initForExisting(currentUser, reservationId);
                }
            }
        });
    }

    private void openPaymentScreen(Reservation reservation) {
        switchScene("/view/admin/PaymentProcessing.fxml", controller -> {
            if (controller instanceof AdminPaymentController paymentController) {
                paymentController.initPaymentScreen(currentUser, reservation);
            }
        });
    }

    private void switchScene(String fxmlPath, java.util.function.Consumer<Object> consumer) {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, fxmlPath, consumer);
        } catch (Exception e) {
            logger.logError("Navigation failure", e);
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
        if (welcomeLabel != null && welcomeLabel.getScene() != null) {
            return getCurrentStageFromNode(welcomeLabel);
        }
        if (reservationsTable != null && reservationsTable.getScene() != null) {
            return getCurrentStageFromNode(reservationsTable);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}

