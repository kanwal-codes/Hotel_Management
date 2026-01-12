package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Billing;
import com.hotel.model.PaymentMethod;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.ServiceAddon;
import com.hotel.service.BillingService;
import com.hotel.service.ReceiptService;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

//
 // Controller for processing guest checkout.
 // Handles final payment verification, receipt generation, and room status updates.
 // Updates reservation status to CHECKED_OUT and marks rooms as available.
//
public class AdminCheckoutController extends BaseController {

    @FXML private Label reservationNumberLabel;
    @FXML private Label guestNameLabel;
    @FXML private Label checkInDateLabel;
    @FXML private Label checkOutDateLabel;
    @FXML private javafx.scene.control.DatePicker actualCheckOutDatePicker;
    @FXML private Label numNightsLabel;
    @FXML private Label roomsLabel;

    @FXML private TableView<ChargeRow> chargesTable;
    @FXML private TableColumn<ChargeRow, String> descriptionColumn;
    @FXML private TableColumn<ChargeRow, String> quantityColumn;
    @FXML private TableColumn<ChargeRow, String> unitPriceColumn;
    @FXML private TableColumn<ChargeRow, String> totalColumn;

    @FXML private VBox addOnsContainer;
    @FXML private VBox addOnsListContainer;

    @FXML private Label roomChargesLabel;
    @FXML private Label addOnsTotalLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxRateDisplayLabel;
    @FXML private Label taxLabel;
    @FXML private VBox discountContainer;
    @FXML private Label discountLabel;
    @FXML private VBox loyaltyContainer;
    @FXML private Label loyaltyLabel;
    @FXML private Label totalAmountLabel;
    @FXML private Label paidAmountLabel;
    @FXML private Label balanceLabel;
    @FXML private Label checkoutSuccessLabel;

    @FXML private TableView<com.hotel.model.Payment> paymentHistoryTable;
    @FXML private TableColumn<com.hotel.model.Payment, String> paymentDateColumn;
    @FXML private TableColumn<com.hotel.model.Payment, String> paymentMethodColumn;
    @FXML private TableColumn<com.hotel.model.Payment, String> paymentAmountColumn;
    @FXML private TableColumn<com.hotel.model.Payment, String> paymentTypeColumn;
    @FXML private TableColumn<com.hotel.model.Payment, String> paymentStatusColumn;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private TextField finalPaymentAmountField;
    @FXML private Button generateFinalBillButton;
    @FXML private Button settleButton;
    @FXML private Button markRoomsButton;
    @FXML private Button feedbackButton;

    private final ReservationService reservationService = AppConfig.createReservationService();
    private final BillingService billingService = AppConfig.createBillingService();
    private final LoggerService logger = LoggerService.getInstance();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private final ObservableList<ChargeRow> chargesData = FXCollections.observableArrayList();
    private final ObservableList<com.hotel.model.Payment> paymentHistoryData = FXCollections.observableArrayList();

    private AdminUser currentUser;
    private Reservation currentReservation;
    private Billing currentBilling;

    @FXML
    private void initialize() {
        if (chargesTable != null) {
            chargesTable.setItems(chargesData);
        }
        if (descriptionColumn != null) {
            descriptionColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        }
        if (quantityColumn != null) {
            quantityColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty());
        }
        if (unitPriceColumn != null) {
            unitPriceColumn.setCellValueFactory(cell -> cell.getValue().unitPriceProperty());
        }
        if (totalColumn != null) {
            totalColumn.setCellValueFactory(cell -> cell.getValue().totalProperty());
        }
        if (paymentMethodComboBox != null) {
            paymentMethodComboBox.getItems().addAll("Cash", "Card", "Points");
            paymentMethodComboBox.setValue("Cash");
        }
        
        // Setup payment history table
        if (paymentHistoryTable != null) {
            paymentHistoryTable.setItems(paymentHistoryData);
        }
        setupPaymentHistoryColumns();
        
        // Add listener to actual check-out date picker to recalculate when changed
        if (actualCheckOutDatePicker != null) {
            actualCheckOutDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && currentReservation != null) {
                    generateFinalBill(); // Recalculate bill with new check-out date
                }
            });
        }
    }

    public void initCheckoutScreen(AdminUser user, Reservation reservation) {
        this.currentUser = user;
        // Reload reservation to get latest status from database
        try {
            com.hotel.repository.ReservationRepository reservationRepo = AppConfig.createReservationRepository();
            Optional<Reservation> refreshedReservation = reservationRepo.findById(reservation.getId());
            this.currentReservation = refreshedReservation.orElse(reservation);
        } catch (Exception e) {
            logger.logError("Failed to reload reservation", e);
            this.currentReservation = reservation;
        }
        
        this.currentBilling = billingService.getBillingForReservation(this.currentReservation).orElse(null);
        
        // Initialize basic reservation info (but don't generate bill yet)
        if (reservationNumberLabel != null) {
            reservationNumberLabel.setText("Reservation #" + currentReservation.getId());
        }
        if (guestNameLabel != null && currentReservation.getGuest() != null) {
            guestNameLabel.setText(currentReservation.getGuest().getName());
        }
        if (checkInDateLabel != null) {
            checkInDateLabel.setText(currentReservation.getCheckIn().toString());
        }
        if (checkOutDateLabel != null) {
            checkOutDateLabel.setText("Scheduled: " + currentReservation.getCheckOut().toString());
        }
        if (actualCheckOutDatePicker != null) {
            // Set default to reservation's check-out date, or today if check-out is in the past, or check-in if check-in is in the future
            java.time.LocalDate defaultDate = currentReservation.getCheckOut();
            java.time.LocalDate today = java.time.LocalDate.now();
            if (defaultDate.isBefore(today)) {
                defaultDate = today;
            }
            if (defaultDate.isBefore(currentReservation.getCheckIn())) {
                defaultDate = currentReservation.getCheckIn();
            }
            actualCheckOutDatePicker.setValue(defaultDate);
            actualCheckOutDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                @Override
                public void updateItem(java.time.LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    // Allow all dates - no restrictions
                }
            });
        }
        if (roomsLabel != null) {
            String rooms = currentReservation.getReservationRooms().stream()
                .map(rr -> rr.getRoom().getRoomNumber() + " (" + rr.getRoom().getType() + ")")
                .collect(Collectors.joining(", "));
            roomsLabel.setText(rooms);
        }
        
        // Check if billing already exists and has data - if so, bill is already generated
        boolean billAlreadyGenerated = (currentBilling != null && 
            (currentBilling.getTotalAmount() > 0 || !chargesData.isEmpty()));
        
        // Initially disable all buttons except Generate Final Bill (unless bill already generated)
        if (generateFinalBillButton != null) {
            generateFinalBillButton.setDisable(billAlreadyGenerated);
        }
        if (settleButton != null) {
            double balanceAmount = currentBilling != null ? currentBilling.getBalanceAmount() : 0.0;
            boolean hasBalance = Math.abs(balanceAmount) > 0.01; // Check for both positive and negative balance
            settleButton.setDisable(!hasBalance);
            // Set initial button text and style
            if (hasBalance) {
                if (balanceAmount < 0) {
                    settleButton.setText("Settle Balance (Refund: " + currencyFormat.format(Math.abs(balanceAmount)) + ")");
                    settleButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                } else {
                    settleButton.setText("Settle Balance (Pay: " + currencyFormat.format(balanceAmount) + ")");
                    settleButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                }
            }
        }
        if (markRoomsButton != null) {
            double balanceAmount = currentBilling != null ? currentBilling.getBalanceAmount() : 0.0;
            boolean hasBalance = Math.abs(balanceAmount) > 0.01;
            markRoomsButton.setDisable(hasBalance);
        }
        
        // If bill is already generated, populate the display
        if (billAlreadyGenerated && currentBilling != null) {
            // Populate payment history immediately
            populatePaymentHistory();
            // Don't auto-generate bill, just show what's there
        } else {
            // Clear tables initially
            chargesData.clear();
            paymentHistoryData.clear();
        }
        
        if (chargesTable != null) {
            chargesTable.refresh();
        }
        if (paymentHistoryTable != null) {
            paymentHistoryTable.refresh();
        }
    }

    @FXML
    private void generateFinalBill() {
        if (currentReservation == null) {
            AlertHelper.showError("Error", "Missing reservation information");
            return;
        }
        
        // Recalculate billing if check-out date changed
        if (actualCheckOutDatePicker != null && actualCheckOutDatePicker.getValue() != null) {
            java.time.LocalDate actualCheckOut = actualCheckOutDatePicker.getValue();
            // Always allow checkout regardless of date - system will handle date adjustments
            try {
                reservationService.checkoutReservation(currentReservation.getId(), actualCheckOut);
                // Reload billing after recalculation
                currentBilling = billingService.getBillingForReservation(currentReservation).orElse(null);
                if (currentBilling == null) {
                    AlertHelper.showError("Error", "Failed to recalculate billing");
                    return;
                }
            } catch (Exception e) {
                logger.logError("Failed to recalculate billing for checkout", e);
                AlertHelper.showError("Error", "Failed to recalculate billing: " + e.getMessage());
                return;
            }
        }
        
        if (currentBilling == null) {
            // Try to get or create billing
            currentBilling = billingService.getBillingForReservation(currentReservation).orElse(null);
            if (currentBilling == null) {
                AlertHelper.showError("Error", "Billing information not available. Please process payment first.");
                return;
            }
        }
        
        // Calculate nights based on actual check-out if set, otherwise scheduled
        java.time.LocalDate checkOutDate = (actualCheckOutDatePicker != null && actualCheckOutDatePicker.getValue() != null) 
            ? actualCheckOutDatePicker.getValue() 
            : currentReservation.getCheckOut();
        long nights = ChronoUnit.DAYS.between(currentReservation.getCheckIn(), checkOutDate);
        if (numNightsLabel != null) {
            numNightsLabel.setText(nights + " nights" + 
                (checkOutDate.isBefore(currentReservation.getCheckOut()) ? " (early checkout)" : ""));
        }
        if (roomsLabel != null) {
            String rooms = currentReservation.getReservationRooms().stream()
                .map(rr -> rr.getRoom().getRoomNumber() + " (" + rr.getRoom().getType() + ")")
                .collect(Collectors.joining(", "));
            roomsLabel.setText(rooms);
        }

        // Basic charge summary
        double roomCharges = currentReservation.getReservationRooms().stream()
            .mapToDouble(rr -> rr.getRoom().getBasePrice() * nights)
            .sum();
        double addonTotal = currentReservation.getReservationAddons().stream()
            .mapToDouble(resAddon -> {
                ServiceAddon addon = resAddon.getAddon();
                if (addon == null) {
                    return 0.0;
                }
                double basePrice = addon.getPrice();
                return basePrice * Math.max(1, resAddon.getQuantity());
            })
            .sum();
        if (roomChargesLabel != null) roomChargesLabel.setText(currencyFormat.format(roomCharges));
        if (addOnsTotalLabel != null) addOnsTotalLabel.setText(currencyFormat.format(addonTotal));
        if (subtotalLabel != null) subtotalLabel.setText(currencyFormat.format(currentBilling.getSubtotal()));
        if (taxRateDisplayLabel != null) taxRateDisplayLabel.setText("Included");
        if (taxLabel != null) taxLabel.setText(currencyFormat.format(currentBilling.getTaxAmount()));
        if (totalAmountLabel != null) totalAmountLabel.setText(currencyFormat.format(currentBilling.getTotalAmount()));
        if (paidAmountLabel != null) paidAmountLabel.setText(currencyFormat.format(currentBilling.getPaidAmount()));
        if (balanceLabel != null) {
            double balanceAmount = currentBilling.getBalanceAmount();
            if (balanceAmount < 0) {
                // Negative balance (refund needed) - show in warning color
                balanceLabel.setText(currencyFormat.format(balanceAmount) + " (Refund)");
                balanceLabel.setStyle("-fx-text-fill: #856404; -fx-font-weight: bold;"); // Warning color
            } else if (balanceAmount > 0) {
                // Positive balance (payment needed) - show in error color
                balanceLabel.setText(currencyFormat.format(balanceAmount));
                balanceLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;"); // Error color
            } else {
                // Zero balance - show in success color
                balanceLabel.setText(currencyFormat.format(balanceAmount));
                balanceLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;"); // Success color
            }
        }

        if (currentBilling.getDiscountValue() > 0 && discountContainer != null) {
            discountContainer.setVisible(true);
            if (discountLabel != null) {
                discountLabel.setText(currencyFormat.format(currentBilling.getDiscountValue()));
            }
        }
        if (loyaltyContainer != null) {
            loyaltyContainer.setVisible(false);
        }

        // Button logic: After generating bill, enable Settle Balance if there's a balance (positive or negative)
        // Disable Generate Final Bill after it's been generated (until balance is settled)
        double balanceAmount = currentBilling.getBalanceAmount();
        boolean hasBalance = Math.abs(balanceAmount) > 0.01; // Use small epsilon for floating point comparison
        boolean isRefundNeeded = balanceAmount < 0; // Negative balance means refund needed
        
        if (settleButton != null) {
            settleButton.setDisable(!hasBalance);
            // Update button text based on whether it's a payment or refund
            if (isRefundNeeded) {
                settleButton.setText("Settle Balance (Refund: " + currencyFormat.format(Math.abs(balanceAmount)) + ")");
                settleButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"); // Red for refund
            } else if (balanceAmount > 0) {
                settleButton.setText("Settle Balance (Pay: " + currencyFormat.format(balanceAmount) + ")");
                settleButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"); // Green for payment
            } else {
                settleButton.setText("Settle Balance");
                settleButton.setStyle(""); // Default style
            }
        }
        if (generateFinalBillButton != null) {
            // Disable Generate Final Bill after it's been generated
            generateFinalBillButton.setDisable(true);
        }
        if (markRoomsButton != null) {
            markRoomsButton.setDisable(hasBalance);
        }
        
        // Show warning message if early checkout results in refund
        if (isRefundNeeded && actualCheckOutDatePicker != null && actualCheckOutDatePicker.getValue() != null) {
            java.time.LocalDate actualCheckOut = actualCheckOutDatePicker.getValue();
            if (actualCheckOut.isBefore(currentReservation.getCheckOut())) {
                if (checkoutSuccessLabel != null) {
                    checkoutSuccessLabel.setText("Early checkout detected. Refund of " + currencyFormat.format(Math.abs(balanceAmount)) + " will be processed.");
                    checkoutSuccessLabel.setVisible(true);
                    checkoutSuccessLabel.setStyle("-fx-text-fill: #856404; -fx-font-size: 14px;"); // Warning color
                }
            }
        }
        
        // Populate itemized charges table
        populateChargesTable();
        
        // Populate payment history table
        populatePaymentHistory();
        
        // Refresh the tables to ensure they display
        if (chargesTable != null) {
            chargesTable.refresh();
        }
        if (paymentHistoryTable != null) {
            paymentHistoryTable.refresh();
        }
        
        AlertHelper.showInfo("Bill Generated", "Final bill has been generated and itemized charges updated.");
    }
    
    private void setupPaymentHistoryColumns() {
        if (paymentDateColumn != null) {
            paymentDateColumn.setCellValueFactory(cell -> {
                com.hotel.model.Payment payment = cell.getValue();
                if (payment != null && payment.getCreatedAt() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        payment.getCreatedAt().toLocalDate().toString() + " " + 
                        payment.getCreatedAt().toLocalTime().toString().substring(0, 5)
                    );
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
        }
        if (paymentMethodColumn != null) {
            paymentMethodColumn.setCellValueFactory(cell -> {
                com.hotel.model.Payment payment = cell.getValue();
                if (payment != null && payment.getMethod() != null) {
                    return new javafx.beans.property.SimpleStringProperty(payment.getMethod().toString());
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
        }
        if (paymentAmountColumn != null) {
            paymentAmountColumn.setCellValueFactory(cell -> {
                com.hotel.model.Payment payment = cell.getValue();
                if (payment != null) {
                    String formattedAmount = currencyFormat.format(payment.getAmount());
                    // Negative amounts (refunds) will automatically show with minus sign
                    return new javafx.beans.property.SimpleStringProperty(formattedAmount);
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            // Set cell factory to style refunds differently
            paymentAmountColumn.setCellFactory(column -> new javafx.scene.control.TableCell<com.hotel.model.Payment, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // Get the payment object to check if it's a refund
                        com.hotel.model.Payment payment = getTableView().getItems().get(getIndex());
                        if (payment != null && payment.getAmount() < 0) {
                            setStyle("-fx-text-fill: #856404; -fx-font-weight: bold;"); // Warning color for refunds
                        } else {
                            setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;"); // Success color for payments
                        }
                    }
                }
            });
        }
        if (paymentTypeColumn != null) {
            // Payment type (deposit, partial, full, refund)
            paymentTypeColumn.setCellValueFactory(cell -> {
                com.hotel.model.Payment payment = cell.getValue();
                if (payment != null && payment.getBilling() != null) {
                    double total = payment.getBilling().getTotalAmount();
                    double paid = payment.getBilling().getPaidAmount();
                    double amount = payment.getAmount();
                    
                    if (amount < 0) {
                        return new javafx.beans.property.SimpleStringProperty("Refund");
                    } else if (paid >= total) {
                        return new javafx.beans.property.SimpleStringProperty("Full Payment");
                    } else if (paid > 0) {
                        return new javafx.beans.property.SimpleStringProperty("Partial Payment");
                    } else {
                        return new javafx.beans.property.SimpleStringProperty("Deposit");
                    }
                }
                return new javafx.beans.property.SimpleStringProperty("Payment");
            });
        }
        if (paymentStatusColumn != null) {
            paymentStatusColumn.setCellValueFactory(cell -> {
                com.hotel.model.Payment payment = cell.getValue();
                if (payment != null && payment.getBilling() != null) {
                    return new javafx.beans.property.SimpleStringProperty("Completed");
                }
                return new javafx.beans.property.SimpleStringProperty("Pending");
            });
        }
    }
    
    private void populatePaymentHistory() {
        if (paymentHistoryTable == null || currentBilling == null) return;
        
        paymentHistoryData.clear();
        
        try {
            // Reload billing to ensure we have the latest data
            Optional<Billing> refreshedBilling = billingService.getBillingForReservation(currentReservation);
            if (refreshedBilling.isPresent()) {
                currentBilling = refreshedBilling.get();
            }
            
            // Get payments for this billing - this should get ALL payments ever made
            com.hotel.repository.PaymentRepository paymentRepository = AppConfig.createPaymentRepository();
            java.util.List<com.hotel.model.Payment> payments = paymentRepository.findByBilling(currentBilling);
            
            // Sort by date, newest first
            payments.sort((p1, p2) -> {
                if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) return 0;
                return p2.getCreatedAt().compareTo(p1.getCreatedAt());
            });
            
            paymentHistoryData.addAll(payments);
            
            logger.logInfo("Loaded " + payments.size() + " payment(s) for billing #" + currentBilling.getId() + 
                " (reservation #" + currentReservation.getId() + ")");
        } catch (Exception e) {
            logger.logError("Failed to load payment history", e);
            AlertHelper.showError("Error", "Failed to load payment history: " + e.getMessage());
        }
    }
    
    private void populateChargesTable() {
        if (chargesTable == null || currentReservation == null) return;
        
        chargesData.clear();
        // Use actual check-out date if set (for early checkout), otherwise use scheduled check-out
        java.time.LocalDate checkOutDate = (actualCheckOutDatePicker != null && actualCheckOutDatePicker.getValue() != null) 
            ? actualCheckOutDatePicker.getValue() 
            : currentReservation.getCheckOut();
        long nights = ChronoUnit.DAYS.between(currentReservation.getCheckIn(), checkOutDate);
        
        // Add room charges
        for (var rr : currentReservation.getReservationRooms()) {
            Room room = rr.getRoom();
            double unitPrice = room.getBasePrice();
            double total = unitPrice * nights;
            chargesData.add(new ChargeRow(
                "Room " + room.getRoomNumber() + " (" + room.getType() + ")",
                String.valueOf(nights) + " nights",
                currencyFormat.format(unitPrice),
                currencyFormat.format(total)
            ));
        }
        
        // Add service/addon charges
        for (var ra : currentReservation.getReservationAddons()) {
            ServiceAddon addon = ra.getAddon();
            if (addon == null) continue;
            
            int quantity = ra.getQuantity();
            double unitPrice = addon.getPrice();
            double total;
            
            if (addon.getPricingModel() == com.hotel.model.PricingModel.PER_NIGHT) {
                total = unitPrice * quantity * nights;
            } else {
                total = unitPrice * quantity;
            }
            
            chargesData.add(new ChargeRow(
                addon.getName(),
                String.valueOf(quantity),
                currencyFormat.format(unitPrice),
                currencyFormat.format(total)
            ));
        }
        
        // Add tax if applicable
        if (currentBilling != null && currentBilling.getTaxAmount() > 0) {
            chargesData.add(new ChargeRow(
                "Tax (10%)",
                "1",
                currencyFormat.format(currentBilling.getTaxAmount()),
                currencyFormat.format(currentBilling.getTaxAmount())
            ));
        }
        
        // Add discount if applicable
        if (currentBilling != null && currentBilling.getDiscountValue() > 0) {
            chargesData.add(new ChargeRow(
                "Discount",
                "1",
                "-" + currencyFormat.format(currentBilling.getDiscountValue()),
                "-" + currencyFormat.format(currentBilling.getDiscountValue())
            ));
        }
    }

    @FXML
    private void processFinalPayment() {
        processBalancePayment();
    }

    @FXML
    private void settleBalance() {
        processBalancePayment();
    }

    private void processBalancePayment() {
        if (currentBilling == null) {
            AlertHelper.showError("Error", "No billing selected");
            return;
        }
        double balanceAmount = currentBilling.getBalanceAmount();
        double amountToProcess = Math.abs(balanceAmount); // Use absolute value for processing
        
        // Check if user specified a different amount
        if (finalPaymentAmountField != null && !finalPaymentAmountField.getText().isBlank()) {
            try {
                double userAmount = Double.parseDouble(finalPaymentAmountField.getText());
                if (userAmount > 0) {
                    amountToProcess = userAmount;
                }
            } catch (NumberFormatException e) {
                AlertHelper.showError("Error", "Invalid amount");
                return;
            }
        }
        
        if (amountToProcess <= 0) {
            AlertHelper.showInfo("Info", "Balance is already zero");
            return;
        }

        boolean isRefund = balanceAmount < 0;
        PaymentMethod method = PaymentMethod.CASH;
        if (paymentMethodComboBox != null) {
            String methodStr = paymentMethodComboBox.getValue();
            if ("Card".equalsIgnoreCase(methodStr)) {
                method = PaymentMethod.CARD;
            } else if ("Points".equalsIgnoreCase(methodStr)) {
                method = PaymentMethod.POINTS;
            }
        }
        
        String actor = currentUser != null ? currentUser.getUsername() : "ADMIN";
        
        // Process payment or refund based on balance
        if (isRefund) {
            // Process refund (negative payment)
            billingService.processRefund(currentBilling, amountToProcess, actor);
            logger.logActivity(actor, "PROCESS_REFUND", "Billing", currentBilling.getId(),
                "Early checkout refund of " + currencyFormat.format(amountToProcess) + " processed");
        } else {
            // Process payment (positive)
            billingService.processPayment(currentBilling, method, amountToProcess, actor);
        }
        
        // Reload billing to get updated balance
        currentBilling = billingService.getBillingForReservation(currentReservation).orElse(currentBilling);
        
        // Update button states: after payment/refund, disable Settle Balance if balance is zero
        double newBalance = currentBilling.getBalanceAmount();
        boolean hasBalance = Math.abs(newBalance) > 0.01;
        
        if (settleButton != null) {
            settleButton.setDisable(!hasBalance);
            if (!hasBalance) {
                settleButton.setText("Settle Balance");
                settleButton.setStyle(""); // Reset to default style
            }
        }
        if (generateFinalBillButton != null) {
            generateFinalBillButton.setDisable(hasBalance);
        }
        if (markRoomsButton != null) {
            markRoomsButton.setDisable(hasBalance);
        }
        
        // Refresh payment history and bill
        populatePaymentHistory();
        if (paymentHistoryTable != null) {
            paymentHistoryTable.refresh();
        }
        generateFinalBill();
        
        // Show appropriate success message
        if (isRefund) {
            AlertHelper.showInfo("Refund Processed", 
                "Refund of " + currencyFormat.format(amountToProcess) + " has been processed successfully.\n" +
                "The transaction has been recorded as a negative payment in the system.");
        } else {
            AlertHelper.showInfo("Success", "Payment processed successfully. Payment history updated.");
        }
    }

    @FXML
    private void markRoomsAvailable() {
        completeCheckout();
    }

    private void completeCheckout() {
        if (currentReservation == null) {
            AlertHelper.showError("Error", "No reservation selected");
            return;
        }
        
        // Reload reservation to get latest status from database
        try {
            com.hotel.repository.ReservationRepository reservationRepo = AppConfig.createReservationRepository();
            Optional<Reservation> refreshedReservation = reservationRepo.findById(currentReservation.getId());
            if (refreshedReservation.isPresent()) {
                currentReservation = refreshedReservation.get();
            }
        } catch (Exception e) {
            logger.logError("Failed to reload reservation for checkout", e);
        }
        
        // Allow checkout regardless of status - clicking checkout will automatically set status to CHECKED_OUT
        // Check if balance is settled (must be zero, can be negative for refunds)
        if (currentBilling != null) {
            double balanceAmount = currentBilling.getBalanceAmount();
            if (Math.abs(balanceAmount) > 0.01) { // Use small epsilon for floating point comparison
                if (balanceAmount > 0) {
                    AlertHelper.showError("Error", "Balance must be zero before checkout. Please settle the balance first.");
                } else {
                    // Negative balance means refund is needed - this is OK, but should be processed
                    AlertHelper.showWarning("Warning", 
                        "Early checkout detected. A refund of " + currencyFormat.format(Math.abs(balanceAmount)) + 
                        " is pending. Please click 'Settle Balance' to process the refund before completing checkout.");
                }
                return;
            }
        }
        
        try {
            // Get actual check-out date if set (for early checkout)
            java.time.LocalDate actualCheckOut = null;
            if (actualCheckOutDatePicker != null && actualCheckOutDatePicker.getValue() != null) {
                actualCheckOut = actualCheckOutDatePicker.getValue();
                // Validate actual check-out date
                if (actualCheckOut.isBefore(currentReservation.getCheckIn())) {
                    AlertHelper.showError("Error", "Actual check-out date cannot be before check-in date");
                    return;
                }
            }
            
            // Perform checkout with optional early check-out date
            reservationService.checkoutReservation(currentReservation.getId(), actualCheckOut);
            
            if (checkoutSuccessLabel != null) {
                checkoutSuccessLabel.setText("Checkout successful! Guest can now submit feedback.");
                checkoutSuccessLabel.setVisible(true);
            }
            
            String message = "Checkout completed successfully! Rooms are now available.";
            if (actualCheckOut != null && actualCheckOut.isBefore(currentReservation.getCheckOut())) {
                message += "\nEarly checkout processed. Billing recalculated for actual nights stayed.";
            }
            message += "\n\nPlease remind the guest to submit feedback at the kiosk.";
            AlertHelper.showInfo("Success", message);
            markRoomsButton.setDisable(true);
        } catch (Exception e) {
            logger.logError("Failed to checkout", e);
            AlertHelper.showError("Error", "Failed to checkout: " + e.getMessage());
        }
    }

    @FXML
    private void printReceipt() {
        if (currentReservation == null || currentBilling == null) {
            AlertHelper.showError("Error", "No reservation or billing information available for receipt.");
            return;
        }
        
        try {
            // Use JavaFX FileChooser to let user choose where to save the PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Receipt as PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            // Set default filename
            String defaultFileName = "Receipt_Reservation_" + currentReservation.getId() + "_" + 
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
            fileChooser.setInitialFileName(defaultFileName);
            
            // Get the stage
            Stage stage = getCurrentStage();
            if (stage == null) {
                AlertHelper.showError("Error", "Could not access window for file dialog.");
                return;
            }
            
            java.io.File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                // Ensure billing and reservation data is loaded
                if (currentReservation.getReservationRooms() == null || currentReservation.getReservationRooms().isEmpty()) {
                    // Reload reservation with rooms
                    com.hotel.repository.ReservationRepository reservationRepo = AppConfig.createReservationRepository();
                    Optional<Reservation> refreshedReservation = reservationRepo.findByIdWithRooms(currentReservation.getId());
                    if (refreshedReservation.isPresent()) {
                        currentReservation = refreshedReservation.get();
                    }
                }
                
                // Generate PDF receipt
                ReceiptService.generateReceipt(currentReservation, currentBilling, file.getAbsolutePath());
                
                AlertHelper.showInfo("Success", "Receipt saved successfully to:\n" + file.getAbsolutePath());
                logger.logInfo("Receipt generated: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.logError("Failed to generate receipt", e);
            AlertHelper.showError("Error", "Failed to generate receipt: " + e.getMessage());
        }
    }

    @FXML
    private void openFeedbackScreen() {
        try {
            if (currentReservation == null || currentReservation.getId() == null) {
                AlertHelper.showError("Error", "No reservation selected for feedback.");
                return;
            }
            
            // Navigate to feedback screen
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/feedback/FeedbackSubmission.fxml"));
            javafx.scene.Parent root = loader.load();
            FeedbackController controller = loader.getController();
            
            // Set the reservation for feedback
            controller.setReservation(currentReservation.getId());
            
            // Get current stage and set scene
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(new javafx.scene.Scene(root, 1200, 800));
            } else {
                // Fallback: create new stage
                Stage newStage = new Stage();
                newStage.setScene(new javafx.scene.Scene(root, 1200, 800));
                newStage.setTitle("Submit Feedback");
                newStage.show();
            }
        } catch (Exception e) {
            logger.logError("Failed to navigate to feedback screen", e);
            AlertHelper.showError("Error", "Failed to load feedback screen: " + e.getMessage());
        }
    }

    @FXML
    @Override
    protected void goBack() {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/ReservationDetails.fxml", controller -> {
                if (controller instanceof AdminReservationController reservationController) {
                    reservationController.initForExisting(currentUser, currentReservation.getId());
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back", e);
            AlertHelper.showError("Navigation Error", "Failed to load reservation screen: " + e.getMessage());
        }
    }

    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (reservationNumberLabel != null && reservationNumberLabel.getScene() != null) {
            return getCurrentStageFromNode(reservationNumberLabel);
        }
        if (roomsLabel != null && roomsLabel.getScene() != null) {
            return getCurrentStageFromNode(roomsLabel);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }

    //
     // Simple DTO for displaying charge rows.
//
    public static class ChargeRow {
        private final javafx.beans.property.SimpleStringProperty description;
        private final javafx.beans.property.SimpleStringProperty quantity;
        private final javafx.beans.property.SimpleStringProperty unitPrice;
        private final javafx.beans.property.SimpleStringProperty total;

        public ChargeRow(String description, String quantity, String unitPrice, String total) {
            this.description = new javafx.beans.property.SimpleStringProperty(description);
            this.quantity = new javafx.beans.property.SimpleStringProperty(quantity);
            this.unitPrice = new javafx.beans.property.SimpleStringProperty(unitPrice);
            this.total = new javafx.beans.property.SimpleStringProperty(total);
        }

        public javafx.beans.property.StringProperty descriptionProperty() { return description; }
        public javafx.beans.property.StringProperty quantityProperty() { return quantity; }
        public javafx.beans.property.StringProperty unitPriceProperty() { return unitPrice; }
        public javafx.beans.property.StringProperty totalProperty() { return total; }
    }
}

