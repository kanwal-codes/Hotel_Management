package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.repository.ReservationRepository;
import com.hotel.session.CustomerSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;
import java.util.List;

public class CustomerKioskDashboardController {
    
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> confirmationColumn;
    @FXML private TableColumn<Reservation, String> checkInColumn;
    @FXML private TableColumn<Reservation, String> checkOutColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> roomsColumn;
    @FXML private TableColumn<Reservation, String> totalColumn;
    @FXML private TableColumn<Reservation, String> actionsColumn;
    @FXML private Label noBookingsLabel;
    
    private EntityManager entityManager;
    private ReservationRepository reservationRepository;
    
    @FXML
    public void initialize() {
        entityManager = AppConfig.createEntityManager();
        reservationRepository = new ReservationRepository(entityManager);
        
        configureTable();
        loadCustomerReservations();
    }
    
    private void configureTable() {
        // Disable the filler column
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        confirmationColumn.setCellValueFactory(cell -> {
            Reservation r = cell.getValue();
            String conf = r.getConfirmationNumber() != null ? r.getConfirmationNumber() : "ID: " + r.getId();
            return new javafx.beans.property.SimpleStringProperty(conf);
        });
        checkInColumn.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getCheckIn().toString()));
        checkOutColumn.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getCheckOut().toString()));
        statusColumn.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().toString()));
        roomsColumn.setCellValueFactory(cell -> {
            String rooms = cell.getValue().getReservationRooms().stream()
                .map(rr -> rr.getRoom().getRoomNumber())
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");
            return new javafx.beans.property.SimpleStringProperty(rooms);
        });
        totalColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getBilling() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    "$" + String.format("%.2f", cell.getValue().getBilling().getTotalAmount()));
            }
            return new javafx.beans.property.SimpleStringProperty("Pending");
        });
        
        actionsColumn.setCellFactory(param -> new TableCell<Reservation, String>() {
            private final Button cancelButton = new Button("Cancel");
            private final Button viewButton = new Button("View");
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    HBox hbox = new HBox(10, viewButton, cancelButton);
                    
                    viewButton.setOnAction(e -> viewReservation(reservation));
                    cancelButton.setOnAction(e -> cancelReservation(reservation));
                    cancelButton.setDisable(
                        reservation.getStatus() != ReservationStatus.PENDING &&
                        reservation.getStatus() != ReservationStatus.CONFIRMED
                    );
                    
                    setGraphic(hbox);
                }
            }
        });
    }
    
    private void loadCustomerReservations() {
        if (CustomerSession.getAuthenticatedGuest() == null) {
            noBookingsLabel.setVisible(true);
            reservationsTable.setVisible(false);
            return;
        }
        
        List<Reservation> reservations = reservationRepository.findByGuest(
            CustomerSession.getAuthenticatedGuest());
        
        reservationsTable.getItems().setAll(reservations);
        
        if (reservations.isEmpty()) {
            noBookingsLabel.setVisible(true);
            reservationsTable.setVisible(false);
        } else {
            noBookingsLabel.setVisible(false);
            reservationsTable.setVisible(true);
        }
    }
    
    private void viewReservation(Reservation reservation) {
        // Navigate to booking details
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reservation Details");
        alert.setHeaderText("Reservation: " + 
            (reservation.getConfirmationNumber() != null ? reservation.getConfirmationNumber() : "ID: " + reservation.getId()));
        alert.setContentText(
            "Guest: " + reservation.getGuest().getName() + "\n" +
            "Check-in: " + reservation.getCheckIn() + "\n" +
            "Check-out: " + reservation.getCheckOut() + "\n" +
            "Status: " + reservation.getStatus()
        );
        alert.showAndWait();
    }
    
    private void cancelReservation(Reservation reservation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Reservation");
        confirm.setHeaderText("Are you sure you want to cancel this reservation?");
        confirm.setContentText("This action cannot be undone.");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // TODO: Implement cancellation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancellation");
            alert.setContentText("Cancellation functionality coming soon.");
            alert.showAndWait();
            loadCustomerReservations();
        }
    }
    
    @FXML
    private void handleMakeBooking(ActionEvent event) {
        navigate(event, "/view/kiosk/WelcomeScreen.fxml");
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        navigate(event, "/view/kiosk/KioskWelcome.fxml");
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        CustomerSession.clear();
        navigate(event, "/view/kiosk/KioskWelcome.fxml");
    }
    
    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlPath, e);
        }
    }
}

