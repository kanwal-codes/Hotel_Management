package com.hotel.controller.helper;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.ReservationService;
import javafx.scene.control.ChoiceDialog;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

//
 // Unified helper for room selection dialogs used across admin and kiosk flows.
 // Provides methods for selecting room types and specific rooms.
//
public final class RoomSelectionHelper {

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private RoomSelectionHelper() {
        // Utility class - prevent instantiation
    }

    //
     // Presents dialogs to choose a room type and concrete room for the given date range.
//
     // @param reservationService reservation service used to query availability
     // @param checkIn            check-in date
     // @param checkOut           check-out date
     // @param alreadySelected    rooms already selected that should be excluded
     // @return optional selected room
//
    public static Optional<Room> selectRoom(ReservationService reservationService,
                                            LocalDate checkIn,
                                            LocalDate checkOut,
                                            List<Room> alreadySelected) {
        RoomType type = promptRoomTypeSelection().orElse(null);
        if (type == null) {
            return Optional.empty();
        }

        List<Room> available = reservationService.getAvailableRooms(type, checkIn, checkOut);
        if (alreadySelected != null && !alreadySelected.isEmpty()) {
            List<Long> takenIds = alreadySelected.stream()
                .map(Room::getId)
                .collect(Collectors.toList());
            available.removeIf(room -> takenIds.contains(room.getId()));
        }
        if (available.isEmpty()) {
            return Optional.empty();
        }

        return promptSpecificRoomSelection(available);
    }

    //
     // Prompts user to select a room type with optional default value.
//
     // @param defaultType optional default room type (can be null)
     // @return selected room type or null if cancelled
//
    public static RoomType promptRoomTypeSelection(RoomType defaultType) {
        RoomType defaultVal = defaultType != null ? defaultType : RoomType.SINGLE;
        ChoiceDialog<RoomType> dialog = new ChoiceDialog<>(defaultVal, Arrays.asList(RoomType.values()));
        dialog.setTitle("Select Room Type");
        dialog.setHeaderText("Choose the room type to add");
        dialog.setContentText("Room Type:");
        Optional<RoomType> result = dialog.showAndWait();
        return result.orElse(null);
    }

    //
     // Prompts user to select a room type with default SINGLE.
//
     // @return optional selected room type
//
    public static Optional<RoomType> promptRoomTypeSelection() {
        ChoiceDialog<RoomType> dialog = new ChoiceDialog<>(RoomType.SINGLE, Arrays.asList(RoomType.values()));
        dialog.setTitle("Select Room Type");
        dialog.setHeaderText("Choose the room type to add");
        dialog.setContentText("Room Type:");
        return dialog.showAndWait();
    }

    //
     // Prompts user to select a specific room from a list.
     // Returns Optional for better null handling.
//
     // @param rooms list of available rooms
     // @return optional selected room
//
    public static Optional<Room> promptSpecificRoomSelection(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return Optional.empty();
        }
        List<RoomOption> options = rooms.stream()
            .map(RoomOption::new)
            .collect(Collectors.toList());
        ChoiceDialog<RoomOption> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Select Room");
        dialog.setHeaderText("Choose a specific room to assign");
        dialog.setContentText("Available Rooms:");
        Optional<RoomOption> selection = dialog.showAndWait();
        return selection.map(RoomOption::room);
    }

    //
     // Prompts user to select a specific room from a list.
     // Returns Room directly (for backward compatibility).
//
     // @param rooms list of available rooms
     // @return selected room or null if cancelled
//
    public static Room promptSpecificRoomSelectionDirect(List<Room> rooms) {
        Optional<Room> result = promptSpecificRoomSelection(rooms);
        return result.orElse(null);
    }

    private static final class RoomOption {
        private final Room room;

        RoomOption(Room room) {
            this.room = room;
        }

        Room room() {
            return room;
        }

        @Override
        public String toString() {
            return room.getRoomNumber() + " - " + room.getType() + " (" +
                CURRENCY.format(room.getBasePrice()) + " / night)";
        }
    }
}

