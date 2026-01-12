package com.hotel.controller.helper;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.ReservationService;
import com.hotel.util.LoggerService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

//
 // Helper class for room selection operations in AdminReservationController.
 // Handles occupancy validation, room suggestions, and room capacity calculations.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminRoomSelectionHelper {
    
    private AdminRoomSelectionHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Calculates total room capacity for a list of rooms.
//
    public static int calculateRoomCapacity(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return 0;
        }
        
        int totalCapacity = 0;
        for (Room room : rooms) {
            if (room != null && room.getType() != null) {
                switch (room.getType()) {
                    case SINGLE:
                    case DELUXE:
                    case PENTHOUSE:
                        totalCapacity += 2;
                        break;
                    case DOUBLE:
                        totalCapacity += 4;
                        break;
                    default:
                        break;
                }
            }
        }
        return totalCapacity;
    }
    
    //
     // Checks if services/addons have changed by comparing addon IDs and quantities.
//
    public static boolean haveServicesChanged(
            List<com.hotel.model.ReservationAddon> pendingServices,
            List<com.hotel.model.ReservationAddon> currentServices) {
        
        if (pendingServices.size() != currentServices.size()) {
            return true;
        }
        
        if (pendingServices.isEmpty()) {
            return false;
        }
        
        // Compare by addon IDs and quantities
        java.util.Map<Long, Integer> pendingAddonMap = new java.util.HashMap<>();
        for (com.hotel.model.ReservationAddon ra : pendingServices) {
            if (ra.getAddon() != null && ra.getAddon().getId() != null) {
                pendingAddonMap.put(ra.getAddon().getId(), ra.getQuantity());
            }
        }
        
        java.util.Map<Long, Integer> currentAddonMap = new java.util.HashMap<>();
        for (com.hotel.model.ReservationAddon ra : currentServices) {
            if (ra.getAddon() != null && ra.getAddon().getId() != null) {
                currentAddonMap.put(ra.getAddon().getId(), ra.getQuantity());
            }
        }
        
        return !pendingAddonMap.equals(currentAddonMap);
    }
    
    //
     // Validates if rooms can accommodate the given occupancy.
//
    public static boolean validateOccupancy(
            List<Room> rooms,
            int adults,
            int children,
            ReservationService reservationService) {
        
        if (rooms == null || rooms.isEmpty()) {
            return false;
        }
        
        return reservationService.validateOccupancy(rooms, adults, children);
    }
    
    //
     // Validates room selection prerequisites (dates and occupancy).
//
    public static boolean validateRoomSelectionPrerequisites(
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            java.util.function.BiConsumer<String, String> showError) {
        
        if (checkIn == null || checkOut == null) {
            if (showError != null) {
                showError.accept("Error", "Please select check-in and check-out dates first.");
            }
            return false;
        }
        if (checkOut.isBefore(checkIn)) {
            if (showError != null) {
                showError.accept("Error", "Check-out date must be after check-in date.");
            }
            return false;
        }
        if (adults <= 0) {
            return false; // Error message handled by caller
        }
        return true;
    }
    
    //
     // Adds suggested rooms to meet capacity requirements.
     // Tries double rooms first (4 capacity), then single rooms (2 capacity).
//
    public static int addSuggestedRooms(
            int additionalCapacityNeeded,
            LocalDate checkIn,
            LocalDate checkOut,
            List<Room> currentRooms,
            List<Room> pendingRooms,
            ReservationService reservationService,
            LoggerService logger) {
        
        try {
            int roomsAdded = 0;
            
            // Try to add double rooms first (most efficient - 4 capacity each)
            int doubleRoomsNeeded = (int) Math.ceil(additionalCapacityNeeded / 4.0);
            List<Room> availableDoubles = reservationService.getAvailableRooms(RoomType.DOUBLE, checkIn, checkOut);
            availableDoubles = availableDoubles.stream()
                .filter(room -> !currentRooms.contains(room) && !pendingRooms.contains(room))
                .toList();
            
            if (availableDoubles.size() >= doubleRoomsNeeded) {
                // Add the needed double rooms
                for (int i = 0; i < doubleRoomsNeeded && i < availableDoubles.size(); i++) {
                    Room room = availableDoubles.get(i);
                    pendingRooms.add(room);
                    roomsAdded++;
                }
            } else {
                // Not enough doubles, try singles (2 capacity each)
                int singleRoomsNeeded = (int) Math.ceil(additionalCapacityNeeded / 2.0);
                List<Room> availableSingles = reservationService.getAvailableRooms(RoomType.SINGLE, checkIn, checkOut);
                availableSingles = availableSingles.stream()
                    .filter(room -> !currentRooms.contains(room) && !pendingRooms.contains(room))
                    .toList();
                
                if (availableSingles.size() >= singleRoomsNeeded) {
                    for (int i = 0; i < singleRoomsNeeded && i < availableSingles.size(); i++) {
                        Room room = availableSingles.get(i);
                        pendingRooms.add(room);
                        roomsAdded++;
                    }
                } else {
                    // Add whatever is available
                    int added = 0;
                    for (Room room : availableDoubles) {
                        if (!pendingRooms.contains(room)) {
                            pendingRooms.add(room);
                            roomsAdded++;
                            added++;
                        }
                    }
                    for (Room room : availableSingles) {
                        if (added < singleRoomsNeeded && !pendingRooms.contains(room)) {
                            pendingRooms.add(room);
                            roomsAdded++;
                            added++;
                        }
                    }
                    
                    if (roomsAdded == 0) {
                        logger.logWarning("No additional rooms available for capacity: " + additionalCapacityNeeded);
                    }
                }
            }
            
            return roomsAdded;
        } catch (Exception e) {
            logger.logError("Failed to add suggested rooms", e);
            throw e;
        }
    }
    
    //
     // Checks occupancy and suggests rooms if needed.
//
    public static void checkOccupancyAndSuggest(
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            List<Room> pendingRooms,
            List<Room> currentReservationRooms,
            ReservationService reservationService,
            Runnable calculateRoomCapacity,
            Consumer<String> showRoomSuggestionDialog,
            Consumer<String> updateRoomSelectionError) {
        
        if (checkIn == null || checkOut == null) {
            return;
        }
        
        int totalGuests = adults + children;
        if (totalGuests <= 0) {
            return;
        }
        
        // Get current rooms (from pendingRooms or currentReservation)
        List<Room> currentRooms;
        if (pendingRooms != null && !pendingRooms.isEmpty()) {
            currentRooms = new ArrayList<>(pendingRooms);
        } else if (currentReservationRooms != null && !currentReservationRooms.isEmpty()) {
            currentRooms = new ArrayList<>(currentReservationRooms);
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
            showRoomSuggestionDialog.accept(buildRoomSuggestionMessage(
                currentRooms.size(),
                currentCapacity,
                totalGuests,
                adults,
                children,
                checkIn,
                checkOut,
                reservationService,
                currentRooms
            ));
        } else {
            // Clear any error messages if capacity is sufficient
            updateRoomSelectionError.accept(null);
        }
    }
    
    //
     // Builds room suggestion message for dialog.
//
    private static String buildRoomSuggestionMessage(
            int currentRoomCount,
            int currentCapacity,
            int requiredGuests,
            int adults,
            int children,
            LocalDate checkIn,
            LocalDate checkOut,
            ReservationService reservationService,
            List<Room> currentRooms) {
        
        int additionalCapacityNeeded = requiredGuests - currentCapacity;
        
        // Calculate how many additional rooms are needed
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
        message.append("Current Rooms: ").append(currentRoomCount).append(" room(s)\n");
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
        return message.toString();
    }
    
    //
     // Shows room suggestion dialog and handles user response.
//
    public static void showRoomSuggestionDialog(
            String message,
            int additionalCapacityNeeded,
            LocalDate checkIn,
            LocalDate checkOut,
            List<Room> currentRooms,
            Runnable addSuggestedRooms,
            Consumer<String> updateRoomSelectionError) {
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Insufficient Room Capacity");
        alert.setHeaderText("Selected rooms cannot accommodate all guests");
        alert.setContentText(message);
        
        javafx.scene.control.ButtonType addRoomsButton = new javafx.scene.control.ButtonType("Add Rooms", 
            javafx.scene.control.ButtonBar.ButtonData.YES);
        javafx.scene.control.ButtonType skipButton = new javafx.scene.control.ButtonType("Skip", 
            javafx.scene.control.ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(addRoomsButton, skipButton);
        
        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == addRoomsButton) {
            // User wants to add rooms - add suggested rooms
            addSuggestedRooms.run();
        } else {
            // User chose to skip - show warning but allow
            updateRoomSelectionError.accept("Warning: Selected rooms may not accommodate all guests comfortably.");
        }
    }
    
    //
     // Validates occupancy in real-time with alert option to bypass.
//
    public static void validateOccupancyRealtime(
            int adults,
            int children,
            List<Room> pendingRooms,
            ReservationService reservationService,
            Runnable calculateRoomCapacity,
            Consumer<String> updateRoomSelectionError,
            Runnable showOccupancyAlert,
            Runnable addSuggestedRooms) {
        
        int totalGuests = adults + children;
        
        if (totalGuests <= 0) {
            updateRoomSelectionError.accept(null);
            return;
        }
        
        if (pendingRooms == null || pendingRooms.isEmpty()) {
            updateRoomSelectionError.accept("No rooms selected. Please add rooms to accommodate " + totalGuests + " guests.");
            return;
        }
        
        boolean valid = reservationService.validateOccupancy(pendingRooms, adults, children);
        if (!valid) {
            int currentCapacity = calculateRoomCapacity(pendingRooms);
            showOccupancyAlert.run();
        } else {
            updateRoomSelectionError.accept(null);
        }
    }
}

