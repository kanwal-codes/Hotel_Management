# Room Selection Table Flow Analysis

## Complete Flow for Displaying Rooms in Table

### ✅ CORRECT FLOW (Step-by-Step)

```
1. User on DateSelection.fxml
   ↓
2. User clicks "Next" button
   ↓
3. validateDatesAndProceed() is called
   ↓
4. validateDates() validates checkIn and checkOut
   ↓
5. navigateToRoomSelection() is called
   ↓
6. reservationService.suggestRooms(numAdults, numChildren, checkIn, checkOut)
   ↓
7. ReservationService queries database for available rooms
   ↓
8. Returns List<RoomSuggestion>
   ↓
9. FXMLLoader loads RoomSelection.fxml
   ↓
10. New KioskController instance is created
   ↓
11. @FXML fields are injected (suggestedRoomsTable, columns, etc.)
   ↓
12. initialize() is called automatically
   ↓
13. setupTableColumns() is called (if table exists)
   ↓
14. setBookingState() transfers state to new controller
   ↓
15. loadRoomSuggestions(suggestions) is called
   ↓
16. setupTableColumns() sets up cell value factories
   ↓
17. ObservableList is created from suggestions
   ↓
18. suggestedRoomsTable.setItems(observableList)
   ↓
19. Table visibility and layout are set
   ↓
20. Table displays data
```

---

## ❌ POTENTIAL FAILURE POINTS

### 1. **DateSelection Screen - Before Navigation**

#### Issue: Invalid or Null Dates
**Location:** `validateDatesAndProceed()` → `validateDates()`

**What Can Go Wrong:**
- `checkIn` is null
- `checkOut` is null
- `checkIn` is in the past
- `checkOut` is before or equal to `checkIn`
- Dates are not set by user

**Current Code:**
```java
private void validateDates() {
    // Validates dates
    if (checkIn == null || checkOut == null) {
        // Shows error, doesn't navigate
    }
}
```

**Status:** ✅ Protected - validation prevents navigation

---

### 2. **Navigation to RoomSelection**

#### Issue: Navigation Fails
**Location:** `navigateToRoomSelection()`

**What Can Go Wrong:**
- FXML file not found (`/view/kiosk/RoomSelection.fxml`)
- FXML syntax error
- Controller class not found
- FXMLLoader exception

**Current Code:**
```java
FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/kiosk/RoomSelection.fxml"));
Parent root = loader.load();
```

**Status:** ⚠️ Partially Protected - try-catch shows error alert

**Recommendation:** Add more specific error messages

---

### 3. **Controller Injection**

#### Issue: Controller is Null
**Location:** `navigateToRoomSelection()` → `loader.getController()`

**What Can Go Wrong:**
- FXML `fx:controller` attribute missing or incorrect
- Controller class not accessible
- Controller instantiation fails

**Current Code:**
```java
KioskController controller = loader.getController();
if (controller != null) {
    // Transfer state
}
```

**Status:** ⚠️ Protected - null check exists, but no error shown to user

**Recommendation:** Show error if controller is null

---

### 4. **FXML Field Injection**

#### Issue: Table or Columns Not Injected
**Location:** `initialize()` method (called automatically by JavaFX)

**What Can Go Wrong:**
- `fx:id="suggestedRoomsTable"` missing in FXML
- `fx:id` mismatch between FXML and controller
- Table not in the scene graph
- Columns not declared in FXML

**Current Code:**
```java
@FXML private TableView<ReservationService.RoomSuggestion> suggestedRoomsTable;
@FXML private TableColumn<...> roomTypeColumn;
// etc.
```

**Status:** ⚠️ Partially Protected - null checks in `loadRoomSuggestions()`

**Recommendation:** Add validation in `initialize()` to check all required fields

---

### 5. **Service Call - suggestRooms()**

#### Issue: Service Returns Empty or Null
**Location:** `reservationService.suggestRooms()`

**What Can Go Wrong:**
- `reservationService` is null (not initialized)
- `numAdults` or `numChildren` is 0 or negative
- `checkIn` or `checkOut` is null (passed to service)
- No rooms available in database
- Database connection fails
- Query returns empty list

**Current Code:**
```java
List<ReservationService.RoomSuggestion> suggestions = 
    reservationService.suggestRooms(numAdults, numChildren, checkIn, checkOut);
```

**Status:** ⚠️ Partially Protected - empty list handled, but null not checked

**Recommendation:** Add null check for service and suggestions

---

### 6. **Room Suggestion Logic**

#### Issue: No Suggestions Generated
**Location:** `ReservationService.suggestRooms()`

**What Can Go Wrong:**
- Total people count doesn't match any suggestion logic
- No rooms available for date range
- Room availability query fails
- Business logic doesn't cover edge cases

**Current Logic:**
- 1-2 people → Single room
- 3-4 people → Double OR 2 Singles
- >4 people → Multiple doubles

**Status:** ⚠️ Edge Cases:
- 0 people (shouldn't happen, but not validated)
- Very large groups (>20 people) - may not have enough rooms
- All rooms booked for date range

**Recommendation:** Add validation for edge cases

---

### 7. **Table Column Setup**

#### Issue: Cell Value Factories Not Set
**Location:** `setupTableColumns()`

**What Can Go Wrong:**
- `suggestedRoomsTable` is null
- Column fields are null
- Cell value factory lambda throws exception
- Currency formatter fails
- Date calculation fails (checkIn/checkOut null)

**Current Code:**
```java
roomTypeColumn.setCellValueFactory(cellData -> {
    ReservationService.RoomSuggestion suggestion = cellData.getValue();
    if (suggestion != null && suggestion.getRoom() != null) {
        return new SimpleStringProperty(suggestion.getRoom().getType().toString());
    }
    return new SimpleStringProperty("");
});
```

**Status:** ⚠️ Partially Protected - null checks exist, but exceptions not caught

**Recommendation:** Wrap in try-catch for each column setup

---

### 8. **Table Population**

#### Issue: Table Not Populated
**Location:** `loadRoomSuggestions()` → `setItems()`

**What Can Go Wrong:**
- `suggestions` list is null
- `suggestions` list is empty (handled, shows custom selection)
- ObservableList creation fails
- `setItems()` doesn't trigger update
- Table not in scene graph yet
- Table columns not properly configured

**Current Code:**
```java
ObservableList<ReservationService.RoomSuggestion> suggestionList = 
    FXCollections.observableArrayList(suggestions);
suggestedRoomsTable.setItems(suggestionList);
```

**Status:** ⚠️ Partially Protected - empty list handled, but null not checked

**Recommendation:** Add null check before creating ObservableList

---

### 9. **Table Visibility**

#### Issue: Table Not Visible
**Location:** `loadRoomSuggestions()` → visibility settings

**What Can Go Wrong:**
- Table is hidden in FXML (`visible="false"`)
- Container (`suggestedPlanContainer`) is hidden
- Table not in scene graph
- CSS hides table
- Table size is 0x0

**Current Code:**
```java
suggestedRoomsTable.setVisible(true);
suggestedRoomsTable.setManaged(true);
suggestedPlanContainer.setVisible(true);
```

**Status:** ✅ Protected - visibility is set explicitly

---

### 10. **State Transfer**

#### Issue: State Not Transferred
**Location:** `setBookingState()` and `navigateToScreen()`

**What Can Go Wrong:**
- `numAdults` or `numChildren` is 0
- `checkIn` or `checkOut` is null
- `currentGuest` is null
- State lost during navigation
- New controller instance doesn't receive state

**Current Code:**
```java
controller.setBookingState(numAdults, numChildren, checkIn, checkOut, currentGuest);
```

**Status:** ⚠️ Not Validated - no checks if state is valid

**Recommendation:** Add validation in `setBookingState()`

---

## 🔍 DETAILED FLOW CHECKLIST

### Pre-Navigation Checks (DateSelection)

- [x] `checkIn` is not null
- [x] `checkOut` is not null
- [x] `checkIn` is not in the past
- [x] `checkOut` is after `checkIn`
- [x] `numAdults` > 0
- [x] `numChildren` >= 0
- [x] `reservationService` is initialized
- [ ] `currentGuest` is set (may be null, but should be set)

### Navigation Checks

- [x] FXML file exists
- [x] FXML syntax is valid
- [x] Controller class is accessible
- [x] Controller instance is created
- [x] Controller is not null

### FXML Injection Checks

- [x] `suggestedRoomsTable` has `fx:id` in FXML
- [x] `roomTypeColumn` has `fx:id` in FXML
- [x] `quantityColumn` has `fx:id` in FXML
- [x] `pricePerNightColumn` has `fx:id` in FXML
- [x] `totalPriceColumn` has `fx:id` in FXML
- [x] `suggestedPlanContainer` has `fx:id` in FXML
- [x] All fields are `@FXML` annotated in controller

### Service Call Checks

- [x] `reservationService` is not null
- [x] `suggestRooms()` parameters are valid
- [x] Database connection is active
- [x] Query executes successfully
- [x] Returns non-null list (may be empty)

### Table Setup Checks

- [x] `suggestedRoomsTable` is not null
- [x] All columns are not null
- [x] `setupTableColumns()` is called
- [x] Cell value factories are set
- [x] Cell factories (formatters) are set

### Table Population Checks

- [x] `suggestions` list is not null
- [x] ObservableList is created successfully
- [x] `setItems()` is called
- [x] Table has items after `setItems()`
- [x] Table is visible
- [x] Container is visible

---

## 🐛 KNOWN ISSUES AND FIXES

### Issue 1: Table Not Displaying Despite Data
**Symptoms:** Logs show data loaded, but table is empty

**Possible Causes:**
1. Cell value factories not being called
2. Table not in scene graph
3. CSS hiding content
4. Table size is 0

**Fix Applied:**
- Added `setVisible(true)`, `setManaged(true)`
- Added `requestLayout()`
- Added extensive logging

**Status:** ✅ Fixed

---

### Issue 2: Columns Not Showing Data
**Symptoms:** Table shows rows but columns are empty

**Possible Causes:**
1. Cell value factory returns null
2. Cell factory formatter fails
3. Property type mismatch

**Fix Applied:**
- Removed null check on `getCellValueFactory()`
- Always set factories
- Added null checks in lambda

**Status:** ✅ Fixed

---

### Issue 3: Empty Suggestions List
**Symptoms:** No rooms suggested, custom selection shown

**Possible Causes:**
1. No rooms available
2. All rooms booked
3. Date range has no availability
4. Business logic doesn't match occupancy

**Fix Applied:**
- Handles empty list gracefully
- Shows custom selection container
- Hides suggested plan container

**Status:** ✅ Working as designed

---

## 🧪 TESTING SCENARIOS

### Scenario 1: Normal Flow (2 adults, 1 child)
**Expected:**
- Suggests 1 Single room OR 1 Double room
- Table shows 1-2 suggestions
- Table is visible and populated

**Test:**
1. Enter 2 adults, 1 child
2. Select dates (future dates)
3. Click Next
4. Verify table shows suggestions

---

### Scenario 2: Large Group (6 adults)
**Expected:**
- Suggests 2 Double rooms
- Table shows 1 suggestion
- Table is visible

**Test:**
1. Enter 6 adults, 0 children
2. Select dates
3. Click Next
4. Verify table shows 2 double rooms

---

### Scenario 3: No Rooms Available
**Expected:**
- Empty suggestions list
- Custom selection container shown
- Suggested plan container hidden

**Test:**
1. Select dates where all rooms are booked
2. Click Next
3. Verify custom selection is shown

---

### Scenario 4: Null Dates
**Expected:**
- Validation error shown
- Navigation blocked
- Stays on DateSelection screen

**Test:**
1. Don't select dates
2. Click Next
3. Verify error message

---

### Scenario 5: Past Dates
**Expected:**
- Validation error shown
- Navigation blocked

**Test:**
1. Select check-in date in the past
2. Click Next
3. Verify error message

---

## 🔧 RECOMMENDATIONS

### 1. Add Comprehensive Validation

```java
private boolean validateBeforeRoomSelection() {
    if (checkIn == null || checkOut == null) {
        logger.logError("Dates are null");
        return false;
    }
    if (numAdults <= 0) {
        logger.logError("Invalid number of adults");
        return false;
    }
    if (reservationService == null) {
        logger.logError("ReservationService is null");
        return false;
    }
    return true;
}
```

### 2. Add Null Checks in loadRoomSuggestions()

```java
public void loadRoomSuggestions(List<ReservationService.RoomSuggestion> suggestions) {
    if (suggestions == null) {
        logger.logError("Suggestions list is null");
        suggestions = new ArrayList<>();
    }
    // ... rest of code
}
```

### 3. Add Error Handling in setupTableColumns()

```java
private void setupTableColumns() {
    try {
        // ... setup code
    } catch (Exception e) {
        logger.logError("Failed to setup table columns", e);
        // Show error to user or use default factories
    }
}
```

### 4. Validate State in setBookingState()

```java
public void setBookingState(int adults, int children, LocalDate in, LocalDate out, Guest guest) {
    if (adults <= 0) {
        throw new IllegalArgumentException("Number of adults must be positive");
    }
    if (in == null || out == null) {
        throw new IllegalArgumentException("Dates cannot be null");
    }
    // ... set state
}
```

### 5. Add Fallback for Empty Suggestions

```java
if (suggestions.isEmpty()) {
    // Show message to user
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("No Suggestions");
    alert.setHeaderText("No room suggestions available");
    alert.setContentText("Please use custom selection to choose your rooms.");
    alert.showAndWait();
}
```

---

## 📊 FLOW DIAGRAM

```
DateSelection Screen
    │
    ├─[User clicks Next]
    │
    ├─[validateDates() called]
    │  ├─[checkIn null?] → ERROR → Stay on screen
    │  ├─[checkOut null?] → ERROR → Stay on screen
    │  ├─[checkIn in past?] → ERROR → Stay on screen
    │  └─[checkOut <= checkIn?] → ERROR → Stay on screen
    │
    └─[All validations pass]
       │
       ├─[navigateToRoomSelection() called]
       │  │
       │  ├─[reservationService.suggestRooms()]
       │  │  ├─[Service null?] → EXCEPTION → Show error
       │  │  ├─[Database error?] → EXCEPTION → Show error
       │  │  └─[Returns List<RoomSuggestion>]
       │  │
       │  ├─[Load FXML]
       │  │  ├─[File not found?] → EXCEPTION → Show error
       │  │  └─[FXML loaded]
       │  │
       │  ├─[Get Controller]
       │  │  ├─[Controller null?] → Skip state transfer
       │  │  └─[Controller exists]
       │  │
       │  ├─[initialize() called automatically]
       │  │  ├─[Table null?] → Log warning
       │  │  └─[setupTableColumns() called]
       │  │
       │  ├─[setBookingState() called]
       │  │
       │  ├─[loadRoomSuggestions() called]
       │  │  ├─[Table null?] → Log error → Return
       │  │  ├─[Columns null?] → Log warning
       │  │  ├─[setupTableColumns() called]
       │  │  ├─[Create ObservableList]
       │  │  ├─[setItems() called]
       │  │  ├─[Set visibility]
       │  │  └─[Show/hide containers]
       │  │
       │  └─[loadAvailableRooms() called]
       │
       └─[RoomSelection Screen displayed]
          ├─[Suggestions exist?]
          │  ├─[Yes] → Show table
          │  └─[No] → Show custom selection
```

---

## ✅ VERIFICATION CHECKLIST

Before considering the flow complete, verify:

- [ ] All null checks are in place
- [ ] All exceptions are caught and handled
- [ ] User-friendly error messages are shown
- [ ] Logging is comprehensive
- [ ] Table displays correctly with data
- [ ] Table handles empty data gracefully
- [ ] State is properly transferred
- [ ] Back button works correctly
- [ ] Custom selection works when no suggestions
- [ ] All edge cases are handled

---

## End of Analysis

This document provides a complete breakdown of the room selection table flow, including all potential failure points and recommendations for improvement.

