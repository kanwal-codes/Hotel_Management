package com.hotel.controller.helper;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

//
 // Helper class for room selection screen logic in KioskController.
 // Extracts table setup, spinner management, and room selection logic.
//
public final class KioskRoomSelectionHelper {
    
    private KioskRoomSelectionHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Sets up table columns for room suggestions table.
//
    public static void setupTableColumns(
            TableView<ReservationService.RoomSuggestion> suggestedRoomsTable,
            TableColumn<ReservationService.RoomSuggestion, String> roomTypeColumn,
            TableColumn<ReservationService.RoomSuggestion, String> capacityColumn,
            TableColumn<ReservationService.RoomSuggestion, Integer> quantityColumn,
            TableColumn<ReservationService.RoomSuggestion, Double> pricePerNightColumn,
            LoggerService logger) {
        
        if (suggestedRoomsTable == null) {
            logger.logWarning("suggestedRoomsTable is null in setupTableColumns");
            return;
        }
        
        logger.logInfo("Setting up table columns...");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        
        try {
            // Set up cell value factories for each column
            if (roomTypeColumn != null) {
                roomTypeColumn.setCellValueFactory(cellData -> {
                    ReservationService.RoomSuggestion suggestion = cellData.getValue();
                    if (suggestion != null && suggestion.getRoom() != null) {
                        // For combination suggestions, show the description
                        if (suggestion.isCombination()) {
                            return new SimpleStringProperty(suggestion.getDescription());
                        }
                        return new SimpleStringProperty(suggestion.getRoom().getType().toString());
                    }
                    return new SimpleStringProperty("");
                });
            }
            
            if (capacityColumn != null) {
                capacityColumn.setCellValueFactory(cellData -> {
                    ReservationService.RoomSuggestion suggestion = cellData.getValue();
                    if (suggestion != null && suggestion.getRoom() != null) {
                        // For combination suggestions, calculate total capacity
                        if (suggestion.isCombination()) {
                            RoomType primaryType = suggestion.getRoom().getType();
                            int primaryCapacity = (primaryType == RoomType.DOUBLE) ? 4 : 2;
                            int primaryTotal = primaryCapacity * suggestion.getQuantity();
                            
                            RoomType secondaryType = suggestion.getSecondaryRoomType();
                            int secondaryCapacity = (secondaryType == RoomType.DOUBLE) ? 4 : 2;
                            int secondaryTotal = secondaryCapacity * suggestion.getSecondaryQuantity();
                            
                            return new SimpleStringProperty(String.valueOf(primaryTotal + secondaryTotal));
                        }
                        // Single room type
                        RoomType type = suggestion.getRoom().getType();
                        int capacity = (type == RoomType.DOUBLE) ? 4 : 2;
                        int totalCapacity = capacity * suggestion.getQuantity();
                        return new SimpleStringProperty(String.valueOf(totalCapacity));
                    }
                    return new SimpleStringProperty("");
                });
            }
            
            if (quantityColumn != null) {
                quantityColumn.setCellValueFactory(cellData -> {
                    ReservationService.RoomSuggestion suggestion = cellData.getValue();
                    if (suggestion != null) {
                        // For combination suggestions, show total room count
                        if (suggestion.isCombination()) {
                            int totalRooms = suggestion.getQuantity() + suggestion.getSecondaryQuantity();
                            return new SimpleIntegerProperty(totalRooms).asObject();
                        }
                        return new SimpleIntegerProperty(suggestion.getQuantity()).asObject();
                    }
                    return new SimpleIntegerProperty(0).asObject();
                });
            }
            
            if (pricePerNightColumn != null) {
                pricePerNightColumn.setCellValueFactory(cellData -> {
                    ReservationService.RoomSuggestion suggestion = cellData.getValue();
                    if (suggestion != null && suggestion.getRoom() != null) {
                        return new SimpleDoubleProperty(suggestion.getRoom().getBasePrice()).asObject();
                    }
                    return new SimpleDoubleProperty(0.0).asObject();
                });
                // Format as currency
                pricePerNightColumn.setCellFactory(column -> new TableCell<ReservationService.RoomSuggestion, Double>() {
                    @Override
                    protected void updateItem(Double price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty || price == null) {
                            setText(null);
                        } else {
                            setText(currencyFormat.format(price));
                        }
                    }
                });
            }
            
            // Set column resize policy
            suggestedRoomsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            suggestedRoomsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            
            logger.logInfo("Table columns setup completed");
        } catch (Exception e) {
            logger.logError("Error setting up table columns", e);
        }
    }
    
    //
     // Loads room suggestions into the table.
//
    public static void loadRoomSuggestionsIntoTable(
            List<ReservationService.RoomSuggestion> suggestions,
            TableView<ReservationService.RoomSuggestion> suggestedRoomsTable,
            VBox suggestedPlanContainer,
            VBox customSelectionContainer,
            LocalDate checkIn,
            LocalDate checkOut,
            ReservationService reservationService,
            LoggerService logger) {
        
        if (suggestedRoomsTable == null) {
            logger.logError("suggestedRoomsTable is NULL! Table not initialized from FXML.");
            return;
        }
        
        // Get ALL available rooms by type and create suggestions for each type
        List<ReservationService.RoomSuggestion> allRoomSuggestions = new ArrayList<>();
        
        if (checkIn != null && checkOut != null) {
            // Get all available rooms by type
            List<Room> singleRooms = reservationService.getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
            List<Room> doubleRooms = reservationService.getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut);
            List<Room> deluxeRooms = reservationService.getAvailableRooms(RoomType.DELUXE, checkIn, checkOut);
            List<Room> penthouseRooms = reservationService.getAvailableRooms(RoomType.PENTHOUSE, checkIn, checkOut);
            
            // Create a suggestion entry for each room type with quantity = 1
            if (!singleRooms.isEmpty()) {
                allRoomSuggestions.add(new ReservationService.RoomSuggestion(singleRooms.get(0), 1));
            }
            if (!doubleRooms.isEmpty()) {
                allRoomSuggestions.add(new ReservationService.RoomSuggestion(doubleRooms.get(0), 1));
            }
            if (!deluxeRooms.isEmpty()) {
                allRoomSuggestions.add(new ReservationService.RoomSuggestion(deluxeRooms.get(0), 1));
            }
            if (!penthouseRooms.isEmpty()) {
                allRoomSuggestions.add(new ReservationService.RoomSuggestion(penthouseRooms.get(0), 1));
            }
        }
        
        // Populate the table
        ObservableList<ReservationService.RoomSuggestion> suggestionList = 
            FXCollections.observableArrayList(allRoomSuggestions);
        
        // Use Platform.runLater to ensure table update happens after scene is rendered
        Platform.runLater(() -> {
            if (suggestedRoomsTable != null) {
                suggestedRoomsTable.setItems(suggestionList);
                suggestedRoomsTable.setVisible(true);
                suggestedRoomsTable.setManaged(true);
                suggestedRoomsTable.setMinHeight(200.0);
                suggestedRoomsTable.setPrefHeight(200.0);
                suggestedRoomsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                
                // Add row click handler to ensure selection works when clicking on rows
                suggestedRoomsTable.setRowFactory(tv -> {
                    TableRow<ReservationService.RoomSuggestion> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (!row.isEmpty() && event.getClickCount() == 1) {
                            // Single click - select the row
                            suggestedRoomsTable.getSelectionModel().select(row.getItem());
                            logger.logInfo("Row clicked - selected: " + 
                                (row.getItem() != null ? row.getItem().getRoom().getType() : "null"));
                        }
                    });
                    return row;
                });
                
                suggestedRoomsTable.refresh();
                suggestedRoomsTable.requestLayout();
            }
        });
        
        // Show containers
        if (suggestedPlanContainer != null) {
            suggestedPlanContainer.setVisible(true);
        }
        if (customSelectionContainer != null) {
            customSelectionContainer.setVisible(true);
            customSelectionContainer.setManaged(true);
        }
    }
    
    //
     // Gets available room counts by type.
//
    public static RoomCounts getAvailableRoomCounts(
            LocalDate checkIn,
            LocalDate checkOut,
            ReservationService reservationService) {
        
        RoomCounts counts = new RoomCounts();
        
        if (checkIn == null || checkOut == null) {
            return counts;
        }
        
        List<Room> allAvailableRooms = new ArrayList<>();
        allAvailableRooms.addAll(reservationService.getAvailableRooms(RoomType.SINGLE, checkIn, checkOut));
        allAvailableRooms.addAll(reservationService.getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut));
        allAvailableRooms.addAll(reservationService.getAvailableRooms(RoomType.DELUXE, checkIn, checkOut));
        allAvailableRooms.addAll(reservationService.getAvailableRooms(RoomType.PENTHOUSE, checkIn, checkOut));
        
        counts.singleCount = (int) allAvailableRooms.stream().filter(r -> r.getType() == RoomType.SINGLE).count();
        counts.doubleCount = (int) allAvailableRooms.stream().filter(r -> r.getType() == RoomType.DOUBLE).count();
        counts.deluxeCount = (int) allAvailableRooms.stream().filter(r -> r.getType() == RoomType.DELUXE).count();
        counts.penthouseCount = (int) allAvailableRooms.stream().filter(r -> r.getType() == RoomType.PENTHOUSE).count();
        
        return counts;
    }
    
    //
     // Sets up a room spinner with value factory and listeners.
//
    public static void setupRoomSpinner(
            Spinner<Integer> spinner,
            HBox row,
            IntConsumer valueConsumer,
            Runnable onUpdateSummary) {
        
        if (spinner == null) {
            return;
        }
        
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        spinner.setValueFactory(factory);
        spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal != null ? newVal : 0;
            valueConsumer.accept(value);
            updateRoomRowHighlight(row, value);
            if (onUpdateSummary != null) {
                onUpdateSummary.run();
            }
        });
    }
    
    //
     // Applies limits to a room spinner based on available rooms.
//
    public static void applyRoomSpinnerLimits(
            Spinner<Integer> spinner,
            HBox row,
            int available,
            int storedValue,
            LoggerService logger) {
        
        if (spinner == null) {
            return;
        }
        
        SpinnerValueFactory<Integer> vf = spinner.getValueFactory();
        SpinnerValueFactory.IntegerSpinnerValueFactory factory;
        if (vf instanceof SpinnerValueFactory.IntegerSpinnerValueFactory) {
            factory = (SpinnerValueFactory.IntegerSpinnerValueFactory) vf;
        } else {
            int maxValue = available > 0 ? available : 10;
            factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxValue, 0);
            spinner.setValueFactory(factory);
        }
        factory.setMin(0);
        int maxValue = available > 0 ? available : Math.max(1, factory.getMax());
        factory.setMax(maxValue);
        int clamped = Math.min(Math.max(0, storedValue), maxValue);
        factory.setValue(clamped);
        updateRoomRowHighlight(row, clamped);
        logger.logInfo("Updated spinner limits: available=" + available + ", max=" + maxValue + ", value=" + clamped);
    }
    
    //
     // Updates room row highlight based on selection.
//
    public static void updateRoomRowHighlight(HBox row, int count, boolean customSelectionActive) {
        if (row == null) {
            return;
        }
        ObservableList<String> classes = row.getStyleClass();
        if (customSelectionActive && count > 0) {
            if (!classes.contains("selected-room-row")) {
                classes.add("selected-room-row");
            }
        } else {
            classes.remove("selected-room-row");
        }
    }
    
    //
     // Updates room row highlight (overload for backward compatibility).
//
    private static void updateRoomRowHighlight(HBox row, int count) {
        updateRoomRowHighlight(row, count, true);
    }
    
    //
     // Updates selected rooms summary label.
//
    public static void updateSelectedRoomsSummary(
            Label selectedRoomsSummaryLabel,
            boolean customSelectionActive,
            int singleCount,
            int doubleCount,
            int deluxeCount,
            int penthouseCount) {
        
        if (selectedRoomsSummaryLabel == null) {
            return;
        }
        
        if (!customSelectionActive) {
            selectedRoomsSummaryLabel.setVisible(false);
            selectedRoomsSummaryLabel.setManaged(false);
            selectedRoomsSummaryLabel.getStyleClass().remove("active");
            return;
        }
        
        if (singleCount == 0 && doubleCount == 0 && deluxeCount == 0 && penthouseCount == 0) {
            selectedRoomsSummaryLabel.setVisible(false);
            selectedRoomsSummaryLabel.setManaged(false);
            selectedRoomsSummaryLabel.getStyleClass().remove("active");
            return;
        }
        
        StringBuilder summary = new StringBuilder("Selected Rooms: ");
        List<String> selections = new ArrayList<>();
        if (singleCount > 0) selections.add(singleCount + " Single");
        if (doubleCount > 0) selections.add(doubleCount + " Double");
        if (deluxeCount > 0) selections.add(deluxeCount + " Deluxe");
        if (penthouseCount > 0) selections.add(penthouseCount + " Penthouse");
        
        summary.append(String.join(", ", selections));
        selectedRoomsSummaryLabel.setText(summary.toString());
        selectedRoomsSummaryLabel.setVisible(true);
        selectedRoomsSummaryLabel.setManaged(true);
        ObservableList<String> classes = selectedRoomsSummaryLabel.getStyleClass();
        if (!classes.contains("active")) {
            classes.add("active");
        }
    }
    
    //
     // Validates and processes custom room selection.
//
    public static RoomSelectionResult validateCustomRoomSelection(
            Spinner<Integer> singleRoomSpinner,
            Spinner<Integer> doubleRoomSpinner,
            Spinner<Integer> deluxeRoomSpinner,
            Spinner<Integer> penthouseSpinner,
            LocalDate checkIn,
            LocalDate checkOut,
            ReservationService reservationService,
            int numAdults,
            int numChildren,
            LoggerService logger) {
        
        RoomSelectionResult result = new RoomSelectionResult();
        
        if (checkIn == null || checkOut == null) {
            result.errorMessage = "Please go back and set check-in and check-out dates";
            return result;
        }
        
        // Get room counts
        int singleCount = singleRoomSpinner != null ? singleRoomSpinner.getValue() : 0;
        int doubleCount = doubleRoomSpinner != null ? doubleRoomSpinner.getValue() : 0;
        int deluxeCount = deluxeRoomSpinner != null ? deluxeRoomSpinner.getValue() : 0;
        int penthouseCount = penthouseSpinner != null ? penthouseSpinner.getValue() : 0;
        
        if (singleCount == 0 && doubleCount == 0 && deluxeCount == 0 && penthouseCount == 0) {
            result.errorMessage = "Please select at least one room";
            return result;
        }
        
        List<Room> selectedRooms = new ArrayList<>();
        
        // Add rooms by type
        if (singleCount > 0) {
            List<Room> singles = reservationService.getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
            if (singles.size() < singleCount) {
                result.errorMessage = "Not enough single rooms available. Only " + singles.size() + " available.";
                return result;
            }
            for (int i = 0; i < singleCount; i++) {
                selectedRooms.add(singles.get(i));
            }
        }
        
        if (doubleCount > 0) {
            List<Room> doubles = reservationService.getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut);
            if (doubles.size() < doubleCount) {
                result.errorMessage = "Not enough double rooms available. Only " + doubles.size() + " available.";
                return result;
            }
            for (int i = 0; i < doubleCount; i++) {
                selectedRooms.add(doubles.get(i));
            }
        }
        
        if (deluxeCount > 0) {
            List<Room> deluxes = reservationService.getAvailableRooms(RoomType.DELUXE, checkIn, checkOut);
            if (deluxes.size() < deluxeCount) {
                result.errorMessage = "Not enough deluxe rooms available. Only " + deluxes.size() + " available.";
                return result;
            }
            for (int i = 0; i < deluxeCount; i++) {
                selectedRooms.add(deluxes.get(i));
            }
        }
        
        if (penthouseCount > 0) {
            List<Room> penthouses = reservationService.getAvailableRooms(RoomType.PENTHOUSE, checkIn, checkOut);
            if (penthouses.size() < penthouseCount) {
                result.errorMessage = "Not enough penthouse rooms available. Only " + penthouses.size() + " available.";
                return result;
            }
            for (int i = 0; i < penthouseCount; i++) {
                selectedRooms.add(penthouses.get(i));
            }
        }
        
        // Validate occupancy
        if (!reservationService.validateOccupancy(selectedRooms, numAdults, numChildren)) {
            result.errorMessage = "Selected rooms cannot accommodate all guests";
            return result;
        }
        
        result.isValid = true;
        result.selectedRooms = selectedRooms;
        return result;
    }
    
    //
     // Processes accepted suggestion and returns selected rooms.
     // Handles both single room type and combination suggestions.
//
    public static List<Room> processAcceptedSuggestion(
            ReservationService.RoomSuggestion selectedSuggestion,
            LocalDate checkIn,
            LocalDate checkOut,
            ReservationService reservationService,
            LoggerService logger) {
        
        if (selectedSuggestion == null) {
            return new ArrayList<>();
        }
        
        List<Room> selectedRooms = new ArrayList<>();
        
        // Handle combination suggestions (e.g., double + single rooms)
        if (selectedSuggestion.isCombination()) {
            // Add primary room type
            List<Room> primaryRooms = reservationService.getAvailableRooms(
                selectedSuggestion.getRoom().getType(), checkIn, checkOut);
            int primaryQuantity = selectedSuggestion.getQuantity();
            int primaryToAdd = Math.min(primaryQuantity, primaryRooms.size());
            for (int i = 0; i < primaryToAdd; i++) {
                selectedRooms.add(primaryRooms.get(i));
            }
            
            // Add secondary room type
            RoomType secondaryType = selectedSuggestion.getSecondaryRoomType();
            int secondaryQuantity = selectedSuggestion.getSecondaryQuantity();
            if (secondaryType != null && secondaryQuantity > 0) {
                List<Room> secondaryRooms = reservationService.getAvailableRooms(
                    secondaryType, checkIn, checkOut);
                int secondaryToAdd = Math.min(secondaryQuantity, secondaryRooms.size());
                for (int i = 0; i < secondaryToAdd; i++) {
                    selectedRooms.add(secondaryRooms.get(i));
                }
            }
            
            logger.logInfo("acceptSuggestion (combination): Added " + primaryToAdd + " " + 
                selectedSuggestion.getRoom().getType() + " room(s) + " + 
                (secondaryQuantity > 0 ? secondaryQuantity : 0) + " " + 
                (secondaryType != null ? secondaryType : "") + " room(s)");
        } else {
            // Handle single room type suggestion
            List<Room> rooms = reservationService.getAvailableRooms(
                selectedSuggestion.getRoom().getType(), checkIn, checkOut);
            
            int quantityNeeded = selectedSuggestion.getQuantity();
            int roomsToAdd = Math.min(quantityNeeded, rooms.size());
            
            for (int i = 0; i < roomsToAdd; i++) {
                selectedRooms.add(rooms.get(i));
            }
            
            logger.logInfo("acceptSuggestion: Added " + selectedRooms.size() + " " + 
                selectedSuggestion.getRoom().getType() + " room(s)");
        }
        
        return selectedRooms;
    }
    
    // ========== Inner Classes for Return Values ==========
    
    //
     // Available room counts by type.
//
    public static class RoomCounts {
        public int singleCount = 0;
        public int doubleCount = 0;
        public int deluxeCount = 0;
        public int penthouseCount = 0;
    }
    
    //
     // Result of room selection validation.
//
    public static class RoomSelectionResult {
        public boolean isValid = false;
        public List<Room> selectedRooms = new ArrayList<>();
        public String errorMessage = null;
    }
    
    //
     // Logs room suggestions for debugging.
//
    public static void logRoomSuggestions(
            List<ReservationService.RoomSuggestion> suggestions,
            TableView<ReservationService.RoomSuggestion> suggestedRoomsTable,
            TableColumn<?, ?> roomTypeColumn,
            TableColumn<?, ?> quantityColumn,
            TableColumn<?, ?> pricePerNightColumn,
            LoggerService logger) {
        
        logger.logInfo("=== loadRoomSuggestions called ===");
        
        // Null check
        if (suggestions == null) {
            logger.logError("Suggestions list is null, using empty list");
            return;
        }
        
        logger.logInfo("Suggestions count: " + suggestions.size());
        
        // Debug: Log each suggestion
        for (int i = 0; i < suggestions.size(); i++) {
            ReservationService.RoomSuggestion s = suggestions.get(i);
            logger.logInfo("Suggestion " + i + ": Room=" + s.getRoom().getType() + 
                         ", Quantity=" + s.getQuantity() + 
                         ", Price=" + s.getRoom().getBasePrice());
        }
        
        // Check if table exists
        if (suggestedRoomsTable == null) {
            logger.logError("suggestedRoomsTable is NULL! Table not initialized from FXML.");
            return;
        }
        logger.logInfo("suggestedRoomsTable is not null");
        
        // Check if columns exist
        logger.logInfo("roomTypeColumn: " + (roomTypeColumn != null ? "exists" : "NULL"));
        logger.logInfo("quantityColumn: " + (quantityColumn != null ? "exists" : "NULL"));
        logger.logInfo("pricePerNightColumn: " + (pricePerNightColumn != null ? "exists" : "NULL"));
    }
}

