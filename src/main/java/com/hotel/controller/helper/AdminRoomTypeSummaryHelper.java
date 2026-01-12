package com.hotel.controller.helper;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.model.RoomTypeSummary;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
 // Helper class for room type summary operations in AdminReservationController.
 // Handles room type grouping, counting, and label updates.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminRoomTypeSummaryHelper {
    
    private AdminRoomTypeSummaryHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Updates room type summary from a list of rooms.
//
    public static void updateRoomTypeSummary(
            List<Room> pendingRooms,
            ObservableList<RoomTypeSummary> roomTypeSummaryData,
            Runnable updateRoomTypeSummaryLabel) {
        
        Map<RoomType, Integer> typeCounts = new HashMap<>();
        
        // Count rooms by type
        for (Room room : pendingRooms) {
            typeCounts.put(room.getType(), typeCounts.getOrDefault(room.getType(), 0) + 1);
        }
        
        // Create summary list
        roomTypeSummaryData.clear();
        for (RoomType type : RoomType.values()) {
            int count = typeCounts.getOrDefault(type, 0);
            if (count > 0) {
                roomTypeSummaryData.add(new RoomTypeSummary(type, count));
            }
        }
        
        // Update the summary label above the table
        if (updateRoomTypeSummaryLabel != null) {
            updateRoomTypeSummaryLabel.run();
        }
    }
    
    //
     // Updates the room type summary label to show current room assignments.
//
    public static void updateRoomTypeSummaryLabel(
            List<Room> pendingRooms,
            Label roomTypeSummaryLabel) {
        
        if (roomTypeSummaryLabel == null) return;
        
        if (pendingRooms.isEmpty()) {
            roomTypeSummaryLabel.setText("No rooms assigned");
            return;
        }
        
        // Count rooms by type
        Map<RoomType, Integer> typeCounts = new HashMap<>();
        for (Room room : pendingRooms) {
            typeCounts.put(room.getType(), typeCounts.getOrDefault(room.getType(), 0) + 1);
        }
        
        // Build summary text
        List<String> summaryParts = new ArrayList<>();
        for (RoomType type : RoomType.values()) {
            int count = typeCounts.getOrDefault(type, 0);
            if (count > 0) {
                summaryParts.add(count + " " + type.name());
            }
        }
        
        String labelText = summaryParts.isEmpty() 
            ? "No rooms assigned" 
            : "Assigned: " + String.join(", ", summaryParts);
        roomTypeSummaryLabel.setText(labelText);
    }
}

