# KioskController Refactoring Plan

**Date:** 2025-12-03  
**Time Limit:** 1 hour  
**Goal:** Reduce lines in KioskController from 3,123 to ~1,500-2,000 while keeping ALL FXML files working exactly the same

---

## Strategy

### Core Principle
- **ALL @FXML fields stay in KioskController** (FXML binding must work)
- **ALL @FXML methods stay in KioskController** (FXML actions must work)
- **Extract logic into helper classes** (reduce lines)
- **@FXML methods delegate to helpers** (preserve functionality)

### Result
- FXML files: **NO CHANGES** ✅
- Functionality: **EXACTLY THE SAME** ✅
- Code organization: **MUCH BETTER** ✅
- Lines reduced: **~1,000-1,500 lines** ✅

---

## Helper Classes to Create

### 1. **KioskGuestDetailsHelper** (~300-400 lines)
**Purpose:** Extract guest details validation and processing logic

**Methods to Extract:**
- `validateOccupancyFields()` - Occupancy validation
- `validateDateFields()` - Date validation  
- `validateGuestFields()` - Guest info validation
- `processGuestDetails()` - Create/find guest logic
- `lookupLoyalty()` - Loyalty lookup logic
- `enrollInLoyalty()` - Loyalty enrollment logic
- `updateNightsDisplay()` - Nights calculation

**Keep in KioskController:**
- All @FXML fields (nameField, phoneField, emailField, etc.)
- All @FXML methods (validateNameField, validatePhoneField, etc.)
- `initialize()` method (but delegate logic to helper)

**Lines Saved:** ~200-300 lines

---

### 2. **KioskRoomSelectionHelper** (~400-500 lines)
**Purpose:** Extract room selection logic

**Methods to Extract:**
- `setupTableColumns()` - Table column configuration
- `loadRoomSuggestions()` - Load suggestions logic
- `loadAvailableRooms()` - Load available rooms
- `setupRoomSpinner()` - Spinner initialization
- `applyRoomSpinnerLimits()` - Spinner limit updates
- `updateSelectedRoomsSummary()` - Summary updates
- `acceptSuggestion()` - Accept suggestion logic
- `validateRoomSelection()` - Room validation logic
- `refreshCustomSelectionUI()` - UI refresh logic

**Keep in KioskController:**
- All @FXML fields (suggestedRoomsTable, spinners, etc.)
- All @FXML methods (acceptSuggestion, chooseCustom, etc.)
- `initialize()` method (but delegate logic to helper)

**Lines Saved:** ~300-400 lines

---

### 3. **KioskAddOnHelper** (~200-300 lines)
**Purpose:** Extract add-on calculation logic

**Methods to Extract:**
- `updateAddOnTotal()` - Total calculation
- `resetAddOnPriceLabels()` - Label reset
- `updateIndividualAddOnPrice()` - Individual price updates
- `calculateAddOnTotal()` - Calculation logic

**Keep in KioskController:**
- All @FXML fields (wifiCheckBox, addOnTotalLabel, etc.)
- All @FXML methods (updateAddOnTotal - but delegate to helper)
- Checkbox listeners (but delegate calculation to helper)

**Lines Saved:** ~150-200 lines

---

### 4. **KioskBookingSummaryHelper** (~300-400 lines)
**Purpose:** Extract booking summary display logic

**Methods to Extract:**
- `loadBookingSummary()` - Summary loading
- `displayRoomBreakdown()` - Room breakdown display
- `displayAddonBreakdown()` - Add-on breakdown display
- `calculateAndDisplayLoyaltyEffects()` - Loyalty calculations
- `loadConfirmation()` - Confirmation screen logic

**Keep in KioskController:**
- All @FXML fields (guestNameLabel, totalAmountLabel, etc.)
- All @FXML methods (payNow, goToFeedback, etc.)
- `initialize()` method (but delegate logic to helper)

**Lines Saved:** ~200-300 lines

---

### 5. **KioskNavigationHelper** (~200-300 lines)
**Purpose:** Extract navigation and state transfer logic

**Methods to Extract:**
- `navigateToScreen()` - Navigation logic
- `navigateToScreenWithoutHistory()` - Back navigation
- `transferStateToController()` - State transfer
- `determineCurrentScreen()` - Screen detection
- `validateBeforeRoomSelection()` - Pre-navigation validation

**Keep in KioskController:**
- Navigation history stack
- State variables
- But delegate navigation logic to helper

**Lines Saved:** ~150-200 lines

---

## Execution Plan

### Phase 1: Guest Details Helper (15 min)
1. Create `KioskGuestDetailsHelper.java`
2. Extract validation methods
3. Extract guest processing methods
4. Update `KioskController` to use helper
5. Test guest details screen

### Phase 2: Room Selection Helper (20 min)
1. Create `KioskRoomSelectionHelper.java`
2. Extract table setup methods
3. Extract room selection methods
4. Update `KioskController` to use helper
5. Test room selection screen

### Phase 3: Add-On Helper (10 min)
1. Create `KioskAddOnHelper.java`
2. Extract calculation methods
3. Update `KioskController` to use helper
4. Test add-on screen

### Phase 4: Booking Summary Helper (10 min)
1. Create `KioskBookingSummaryHelper.java`
2. Extract summary display methods
3. Update `KioskController` to use helper
4. Test booking summary screen

### Phase 5: Navigation Helper (5 min)
1. Create `KioskNavigationHelper.java`
2. Extract navigation methods
3. Update `KioskController` to use helper
4. Test navigation flow

---

## Testing Checklist

After each phase:
- [ ] Compile successfully
- [ ] Screen loads correctly
- [ ] All fields bind correctly
- [ ] All buttons work
- [ ] Validation works
- [ ] Navigation works
- [ ] State transfer works

Final testing:
- [ ] Complete booking flow works
- [ ] All screens accessible
- [ ] All functionality preserved
- [ ] No errors in logs

---

## Expected Results

### Before
- `KioskController.java`: 3,123 lines
- All logic in one file
- Hard to maintain

### After
- `KioskController.java`: ~1,500-2,000 lines
- `KioskGuestDetailsHelper.java`: ~300-400 lines
- `KioskRoomSelectionHelper.java`: ~400-500 lines
- `KioskAddOnHelper.java`: ~200-300 lines
- `KioskBookingSummaryHelper.java`: ~300-400 lines
- `KioskNavigationHelper.java`: ~200-300 lines
- **Total:** ~3,000-3,900 lines (organized better)
- **Net reduction in main controller:** ~1,000-1,500 lines

---

## Important Notes

1. **Never change @FXML fields** - They must stay in KioskController
2. **Never change @FXML method signatures** - FXML actions depend on them
3. **Always test after each phase** - Catch issues early
4. **Preserve all logic exactly** - No behavior changes
5. **Keep state variables in KioskController** - Helpers access via parameters

---

## Helper Class Pattern

```java
public final class KioskGuestDetailsHelper {
    private KioskGuestDetailsHelper() {}
    
    // Static methods that take KioskController fields as parameters
    public static boolean validateGuestDetails(
        TextField nameField, TextField phoneField, TextField emailField,
        Label nameErrorLabel, Label phoneErrorLabel, Label emailErrorLabel,
        // ... other parameters
    ) {
        // Logic here
    }
}
```

**Usage in KioskController:**
```java
@FXML
private void validateGuestDetails() {
    boolean isValid = KioskGuestDetailsHelper.validateGuestDetails(
        nameField, phoneField, emailField,
        nameErrorLabel, phoneErrorLabel, emailErrorLabel,
        // ... other fields
    );
    // Handle result
}
```

---

**Status:** Ready to execute  
**Next Step:** Phase 1 - Guest Details Helper

