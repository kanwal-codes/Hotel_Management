package com.hotel.controller.helper;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.ServiceAddon;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//
 // Helper class for state management in KioskController.
 // Handles state transfer, reset, and field population.
//
public final class KioskStateHelper {
    
    private KioskStateHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Booking state container for transfer between controller instances.
//
    public static class BookingState {
        public int numAdults = 0;
        public int numChildren = 0;
        public LocalDate checkIn;
        public LocalDate checkOut;
        public Guest currentGuest;
        public List<Room> selectedRooms = new ArrayList<>();
        public List<ServiceAddon> selectedAddons = new ArrayList<>();
        public Reservation createdReservation;
        public int singleRoomCount = 0;
        public int doubleRoomCount = 0;
        public int deluxeRoomCount = 0;
        public int penthouseRoomCount = 0;
        public boolean customSelectionActive = false;
        public Stack<String> navigationHistory = new Stack<>();
    }
    
    //
     // Creates a state snapshot from current controller state.
//
    public static BookingState createStateSnapshot(
            int numAdults,
            int numChildren,
            LocalDate checkIn,
            LocalDate checkOut,
            Guest currentGuest,
            List<Room> selectedRooms,
            List<ServiceAddon> selectedAddons,
            Reservation createdReservation,
            int singleRoomCount,
            int doubleRoomCount,
            int deluxeRoomCount,
            int penthouseRoomCount,
            boolean customSelectionActive,
            Stack<String> navigationHistory) {
        
        BookingState state = new BookingState();
        state.numAdults = numAdults;
        state.numChildren = numChildren;
        state.checkIn = checkIn;
        state.checkOut = checkOut;
        state.currentGuest = currentGuest;
        state.selectedRooms = new ArrayList<>(selectedRooms != null ? selectedRooms : new ArrayList<>());
        state.selectedAddons = new ArrayList<>(selectedAddons != null ? selectedAddons : new ArrayList<>());
        state.createdReservation = createdReservation;
        state.singleRoomCount = singleRoomCount;
        state.doubleRoomCount = doubleRoomCount;
        state.deluxeRoomCount = deluxeRoomCount;
        state.penthouseRoomCount = penthouseRoomCount;
        state.customSelectionActive = customSelectionActive;
        state.navigationHistory = new Stack<>();
        if (navigationHistory != null) {
            state.navigationHistory.addAll(navigationHistory);
        }
        return state;
    }
    
    //
     // Applies state snapshot to controller fields.
//
    public static void applyStateSnapshot(
            BookingState state,
            KioskStateContainer container) {
        
        if (state == null || container == null) {
            return;
        }
        
        container.setNumAdults(state.numAdults);
        container.setNumChildren(state.numChildren);
        container.setCheckIn(state.checkIn);
        container.setCheckOut(state.checkOut);
        container.setCurrentGuest(state.currentGuest);
        container.setSelectedRooms(new ArrayList<>(state.selectedRooms));
        container.setSelectedAddons(new ArrayList<>(state.selectedAddons));
        container.setCreatedReservation(state.createdReservation);
        container.setSingleRoomCount(state.singleRoomCount);
        container.setDoubleRoomCount(state.doubleRoomCount);
        container.setDeluxeRoomCount(state.deluxeRoomCount);
        container.setPenthouseRoomCount(state.penthouseRoomCount);
        container.setCustomSelectionActive(state.customSelectionActive);
        container.setNavigationHistory(new Stack<>());
        if (state.navigationHistory != null) {
            container.getNavigationHistory().addAll(state.navigationHistory);
        }
    }
    
    //
     // Resets booking state to initial values.
//
    public static BookingState createResetState() {
        return new BookingState();
    }
    
    //
     // Populates UI fields from state.
//
    public static void populateFieldsFromState(
            Guest currentGuest,
            LocalDate checkIn,
            LocalDate checkOut,
            int numAdults,
            int numChildren,
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            TextInputControl addressField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            TextField numAdultsField,
            TextField numChildrenField,
            Label numNightsDisplayLabel,
            VBox nightsInfoContainer) {
        
        if (currentGuest != null) {
            if (nameField != null) nameField.setText(currentGuest.getName() != null ? currentGuest.getName() : "");
            if (phoneField != null) phoneField.setText(currentGuest.getPhone() != null ? currentGuest.getPhone() : "");
            if (emailField != null) emailField.setText(currentGuest.getEmail() != null ? currentGuest.getEmail() : "");
            if (addressField != null && currentGuest.getAddress() != null) {
                addressField.setText(currentGuest.getAddress());
            }
        }
        
        if (checkInDatePicker != null) {
            if (checkIn != null) {
                checkInDatePicker.setValue(checkIn);
            } else if (checkInDatePicker.getValue() == null) {
                checkInDatePicker.setValue(LocalDate.now());
            }
        }
        if (checkOutDatePicker != null) {
            if (checkOut != null) {
                checkOutDatePicker.setValue(checkOut);
            } else if (checkOutDatePicker.getValue() == null) {
                checkOutDatePicker.setValue(LocalDate.now().plusDays(1));
            }
        }
        
        if (numAdultsField != null) {
            if (numAdults > 0) {
                numAdultsField.setText(String.valueOf(numAdults));
            } else {
                numAdultsField.clear();
            }
        }
        if (numChildrenField != null) {
            numChildrenField.setText(String.valueOf(Math.max(numChildren, 0)));
        }
        
        // Update nights display
        if (checkIn != null && checkOut != null && numNightsDisplayLabel != null && nightsInfoContainer != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            numNightsDisplayLabel.setText(nights + " night(s)");
            nightsInfoContainer.setVisible(true);
        }
    }
    
    //
     // Populates additional fields (spinners, checkboxes) from state.
//
    public static void populateAdditionalFieldsFromState(
            int singleRoomCount,
            int doubleRoomCount,
            int deluxeRoomCount,
            int penthouseRoomCount,
            java.util.List<com.hotel.model.ServiceAddon> selectedAddons,
            Guest currentGuest,
            javafx.scene.control.Spinner<Integer> singleRoomSpinner,
            javafx.scene.control.Spinner<Integer> doubleRoomSpinner,
            javafx.scene.control.Spinner<Integer> deluxeRoomSpinner,
            javafx.scene.control.Spinner<Integer> penthouseSpinner,
            javafx.scene.control.CheckBox wifiCheckBox,
            javafx.scene.control.CheckBox breakfastCheckBox,
            javafx.scene.control.CheckBox parkingCheckBox,
            javafx.scene.control.CheckBox spaCheckBox,
            javafx.scene.control.TextField loyaltyNumberField,
            java.util.function.Supplier<Void> lookupLoyaltyCallback) {
        
        // Populate room spinners
        if (singleRoomSpinner != null && singleRoomSpinner.getValueFactory() != null) {
            singleRoomSpinner.getValueFactory().setValue(singleRoomCount);
        }
        if (doubleRoomSpinner != null && doubleRoomSpinner.getValueFactory() != null) {
            doubleRoomSpinner.getValueFactory().setValue(doubleRoomCount);
        }
        if (deluxeRoomSpinner != null && deluxeRoomSpinner.getValueFactory() != null) {
            deluxeRoomSpinner.getValueFactory().setValue(deluxeRoomCount);
        }
        if (penthouseSpinner != null && penthouseSpinner.getValueFactory() != null) {
            penthouseSpinner.getValueFactory().setValue(penthouseRoomCount);
        }
        
        // Populate add-on checkboxes
        if (wifiCheckBox != null) {
            wifiCheckBox.setSelected(isAddonSelected(selectedAddons, "Wi-Fi"));
        }
        if (breakfastCheckBox != null) {
            breakfastCheckBox.setSelected(isAddonSelected(selectedAddons, "Breakfast"));
        }
        if (parkingCheckBox != null) {
            parkingCheckBox.setSelected(isAddonSelected(selectedAddons, "Parking"));
        }
        if (spaCheckBox != null) {
            spaCheckBox.setSelected(isAddonSelected(selectedAddons, "Spa Access"));
        }
        
        // Populate loyalty number field
        if (loyaltyNumberField != null && currentGuest != null) {
            String loyaltyNumber = currentGuest.getLoyaltyNumber();
            if (loyaltyNumber != null) {
                loyaltyNumberField.setText(loyaltyNumber);
                // Automatically perform lookup when loyalty number is found
                if (lookupLoyaltyCallback != null) {
                    lookupLoyaltyCallback.get();
                }
            }
        }
    }
    
    private static boolean isAddonSelected(java.util.List<com.hotel.model.ServiceAddon> selectedAddons, String addonName) {
        if (selectedAddons == null || selectedAddons.isEmpty() || addonName == null) {
            return false;
        }
        return selectedAddons.stream()
            .anyMatch(addon -> addon.getName() != null && addon.getName().equalsIgnoreCase(addonName));
    }
    
    //
     // Captures current state from UI fields before navigation.
//
    public static void captureCurrentStateFromUI(
            TextField numAdultsField,
            TextField numChildrenField,
            DatePicker checkInDatePicker,
            DatePicker checkOutDatePicker,
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            javafx.scene.control.TextArea addressField,
            javafx.scene.control.Spinner<Integer> singleRoomSpinner,
            javafx.scene.control.Spinner<Integer> doubleRoomSpinner,
            javafx.scene.control.Spinner<Integer> deluxeRoomSpinner,
            javafx.scene.control.Spinner<Integer> penthouseSpinner,
            javafx.scene.control.CheckBox wifiCheckBox,
            javafx.scene.control.CheckBox breakfastCheckBox,
            javafx.scene.control.CheckBox parkingCheckBox,
            javafx.scene.control.CheckBox spaCheckBox,
            java.util.function.IntConsumer setNumAdults,
            java.util.function.IntConsumer setNumChildren,
            java.util.function.Consumer<LocalDate> setCheckIn,
            java.util.function.Consumer<LocalDate> setCheckOut,
            java.util.function.Consumer<Guest> setCurrentGuest,
            java.util.function.IntConsumer setSingleRoomCount,
            java.util.function.IntConsumer setDoubleRoomCount,
            java.util.function.IntConsumer setDeluxeRoomCount,
            java.util.function.IntConsumer setPenthouseRoomCount,
            Runnable updateAddOnTotal,
            com.hotel.util.LoggerService logger) {
        
        // Capture occupancy
        if (numAdultsField != null) {
            try {
                String adultsText = numAdultsField.getText().trim();
                if (!adultsText.isEmpty()) {
                    int adults = Integer.parseInt(adultsText);
                    if (setNumAdults != null) setNumAdults.accept(adults);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
        if (numChildrenField != null) {
            try {
                String childrenText = numChildrenField.getText().trim();
                if (!childrenText.isEmpty()) {
                    int children = Integer.parseInt(childrenText);
                    if (setNumChildren != null) setNumChildren.accept(children);
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
        
        // Capture dates
        if (checkInDatePicker != null && checkInDatePicker.getValue() != null) {
            if (setCheckIn != null) setCheckIn.accept(checkInDatePicker.getValue());
        }
        if (checkOutDatePicker != null && checkOutDatePicker.getValue() != null) {
            if (setCheckOut != null) setCheckOut.accept(checkOutDatePicker.getValue());
        }
        
        // Capture guest info (if fields are filled but guest is null, create/update guest)
        if (setCurrentGuest != null) {
            Guest guest = null;
            if (nameField != null && nameField.getText() != null && !nameField.getText().trim().isEmpty()) {
                guest = new Guest();
                guest.setName(nameField.getText().trim());
                if (phoneField != null && phoneField.getText() != null && !phoneField.getText().trim().isEmpty()) {
                    guest.setPhone(phoneField.getText().trim());
                }
                if (emailField != null && emailField.getText() != null && !emailField.getText().trim().isEmpty()) {
                    guest.setEmail(emailField.getText().trim());
                }
                if (addressField != null && addressField.getText() != null && !addressField.getText().trim().isEmpty()) {
                    guest.setAddress(addressField.getText().trim());
                }
            }
            setCurrentGuest.accept(guest);
        }
        
        // Capture room spinner values
        if (singleRoomSpinner != null && singleRoomSpinner.getValue() != null) {
            if (setSingleRoomCount != null) setSingleRoomCount.accept(singleRoomSpinner.getValue());
        }
        if (doubleRoomSpinner != null && doubleRoomSpinner.getValue() != null) {
            if (setDoubleRoomCount != null) setDoubleRoomCount.accept(doubleRoomSpinner.getValue());
        }
        if (deluxeRoomSpinner != null && deluxeRoomSpinner.getValue() != null) {
            if (setDeluxeRoomCount != null) setDeluxeRoomCount.accept(deluxeRoomSpinner.getValue());
        }
        if (penthouseSpinner != null && penthouseSpinner.getValue() != null) {
            if (setPenthouseRoomCount != null) setPenthouseRoomCount.accept(penthouseSpinner.getValue());
        }
        
        // Capture add-on selections
        if (updateAddOnTotal != null) {
            updateAddOnTotal.run();
        }
    }
    
    //
     // Interface for state container (allows KioskController to implement).
//
    public interface KioskStateContainer {
        void setNumAdults(int numAdults);
        void setNumChildren(int numChildren);
        void setCheckIn(LocalDate checkIn);
        void setCheckOut(LocalDate checkOut);
        void setCurrentGuest(Guest currentGuest);
        void setSelectedRooms(List<Room> selectedRooms);
        void setSelectedAddons(List<ServiceAddon> selectedAddons);
        void setCreatedReservation(Reservation createdReservation);
        void setSingleRoomCount(int count);
        void setDoubleRoomCount(int count);
        void setDeluxeRoomCount(int count);
        void setPenthouseRoomCount(int count);
        void setCustomSelectionActive(boolean active);
        void setNavigationHistory(Stack<String> history);
        Stack<String> getNavigationHistory();
    }
}

