# Refactoring Plan: Reducing Controller File Sizes

## Goal
Reduce all controller files to under 1000 lines by extracting code into:
- **Services** (business logic)
- **Helpers** (UI/controller helpers)
- **Utils** (utility functions)
- **Repositories** (data access - already exists)
- **Strategy Pattern** (algorithm variations)
- **Decorator Pattern** (enhanced functionality)
- **Builder Pattern** (complex object construction)

---

## 1. AdminReservationController (2,413 lines → Target: <1000)

### Current Responsibilities:
1. Guest Management (loyalty enrollment, selection)
2. Room Selection & Validation
3. Billing Calculations & Display
4. Reservation CRUD Operations
5. UI Updates & Form Handling
6. Service/Addon Management
7. Entity Management (JPA transactions)

### Extraction Plan:

#### A. Extract to Service: `AdminReservationService` (~400 lines)
**Location:** `src/main/java/com/hotel/service/AdminReservationService.java`

**Methods to Extract:**
- `saveReservationChanges()` - Complex reservation update logic (lines ~1400-1900)
- `updateReservationRooms()` - Room update with entity management (lines ~1554-1650)
- `updateReservationAddons()` - Addon update with entity management (lines ~1680-1749)
- `calculateSubtotal()` - Billing calculation (lines ~2037-2066)
- `resolveGuestFromForm()` - Guest resolution logic (lines ~2027-2035)
- `validateReservationData()` - Validation logic (lines ~1990-2025)
- `createReservationFromForm()` - New reservation creation (lines ~1200-1400)

**Benefits:**
- Removes ~600-800 lines from controller
- Centralizes business logic
- Makes testing easier
- Reusable across admin modules

#### B. Extract to Helper: `AdminReservationUIHelper` (~300 lines)
**Location:** `src/main/java/com/hotel/controller/helper/AdminReservationUIHelper.java`

**Methods to Extract:**
- `updateReservationDisplay()` - UI updates (lines ~2119-2151)
- `updateBillingDisplay()` - Billing UI updates (lines ~2157-2185)
- `updateEstimatedBillingDisplay()` - Estimated billing UI (lines ~2187-2210)
- `updateBalanceDisplay()` - Balance UI (lines ~2153-2155)
- `updateActionButtons()` - Button state management (lines ~1136-1152)
- `updateRoomTypeSummary()` - Room summary display (lines ~700-800)
- `updateRoomTypeSummaryLabel()` - Label updates
- `updateGuestCountSummary()` - Guest count display (lines ~1129-1134)
- `updateRoomSelectionError()` - Error message display (lines ~1154-1163)
- `configureServiceTable()` - Table configuration (lines ~341-378)
- `configureRoomTable()` - Room table setup

**Benefits:**
- Removes ~300-400 lines
- Separates UI concerns from business logic
- Easier to maintain UI updates

#### C. Extract to Helper: `AdminGuestManagementHelper` (~200 lines)
**Location:** `src/main/java/com/hotel/controller/helper/AdminGuestManagementHelper.java`

**Methods to Extract:**
- `showCustomerSelectionDialog()` - Customer selection UI (lines ~389-500)
- `showNewUserOptionsDialog()` - New user dialog (lines ~500-600)
- `showAccountCreationDialog()` - Account creation dialog (lines ~600-700)
- `fillGuestDetails()` - Form population (lines ~700-750)
- `updateLoyaltyEnrollmentButton()` - Loyalty UI updates (lines ~201-247)
- `enrollGuestInLoyalty()` - Loyalty enrollment (lines ~249-339)

**Benefits:**
- Removes ~200-250 lines
- Isolates guest management UI logic
- Reusable for other admin screens

#### D. Extract to Helper: `AdminRoomSelectionHelper` (~250 lines)
**Location:** `src/main/java/com/hotel/controller/helper/AdminRoomSelectionHelper.java`

**Methods to Extract:**
- `checkOccupancyAndSuggest()` - Occupancy validation (lines ~900-1000)
- `showRoomSuggestionDialog()` - Suggestion dialog (lines ~980-1033)
- `addSuggestedRooms()` - Auto-add rooms (lines ~1035-1116)
- `validateOccupancy()` - Occupancy validation (lines ~1194-1200)
- `validateRoomSelectionPrerequisites()` - Prerequisites check (lines ~1176-1192)
- `calculateRoomCapacity()` - Capacity calculation
- `setupOccupancyListeners()` - Event listeners

**Benefits:**
- Removes ~250-300 lines
- Centralizes room selection logic
- Reusable validation

#### E. Extract to Utils: `ReservationEntityManager` (~150 lines)
**Location:** `src/main/java/com/hotel/util/ReservationEntityManager.java`

**Methods to Extract:**
- `updateReservationRoomsTransaction()` - Room update transaction (lines ~1554-1650)
- `updateReservationAddonsTransaction()` - Addon update transaction (lines ~1680-1749)
- `reloadReservationWithAssociations()` - Eager loading helper
- `mergeReservationSafely()` - Safe merge operation

**Benefits:**
- Removes ~150-200 lines
- Centralizes JPA transaction management
- Reduces code duplication
- Better error handling

#### F. Extract to Strategy: `ReservationStatusStrategy` (~100 lines)
**Location:** `src/main/java/com/hotel/service/strategy/ReservationStatusStrategy.java`

**Strategy Pattern for Status Determination:**
- `AutoStatusStrategy` - Automatic status updates
- `ManualStatusStrategy` - User-selected status
- `StatusDeterminer` - Context class

**Benefits:**
- Removes ~100 lines
- Makes status logic extensible
- Easier to add new status rules

**Estimated Reduction:** ~1,500-1,800 lines → **Target: ~600-900 lines**

---

## 2. KioskController (2,156 lines → Target: <1000)

### Current Responsibilities:
1. State Management (across multiple screens)
2. Navigation (screen transitions)
3. Guest Details Handling
4. Room Selection
5. Add-ons Selection
6. Booking Summary
7. Payment Processing
8. Form Validation
9. Data Persistence

### Extraction Plan:

#### A. Extract to Service: `KioskBookingService` (~400 lines)
**Location:** `src/main/java/com/hotel/service/KioskBookingService.java`

**Methods to Extract:**
- `createReservationFromKiosk()` - Complete reservation creation (lines ~1400-1600)
- `validateBookingState()` - State validation
- `calculateBookingTotals()` - Price calculations
- `processBookingPayment()` - Payment processing
- `finalizeBooking()` - Booking completion

**Benefits:**
- Removes ~400-500 lines
- Centralizes booking business logic
- Testable independently

#### B. Extract to Helper: `KioskStateManager` (~300 lines)
**Location:** `src/main/java/com/hotel/controller/helper/KioskStateManager.java`

**Methods to Extract:**
- `captureCurrentStateFromUI()` - State capture (already exists but can be enhanced)
- `populateFieldsFromState()` - State restoration (already exists)
- `transferStateToController()` - State transfer
- `clearState()` - State cleanup
- `validateState()` - State validation

**Note:** `KioskStateHelper` already exists - enhance it or merge functionality.

**Benefits:**
- Removes ~300-350 lines
- Centralizes state management
- Reduces controller complexity

#### C. Extract to Helper: `KioskNavigationManager` (~200 lines)
**Location:** `src/main/java/com/hotel/controller/helper/KioskNavigationManager.java`

**Methods to Extract:**
- `navigateToScreen()` - Navigation logic (lines ~800-900)
- `navigateToScreenWithoutHistory()` - Direct navigation
- `navigateToKioskWelcome()` - Welcome screen navigation
- `goBack()` - Back button logic
- `handleNavigationHistory()` - History stack management

**Note:** `KioskNavigationHelper` already exists - enhance it.

**Benefits:**
- Removes ~200-250 lines
- Centralizes navigation logic
- Easier to modify navigation flow

#### D. Extract to Helper: `KioskFormValidator` (~250 lines)
**Location:** `src/main/java/com/hotel/controller/helper/KioskFormValidator.java`

**Methods to Extract:**
- `validateGuestDetails()` - Guest validation (lines ~600-700)
- `validateDateFields()` - Date validation
- `validateOccupancyFields()` - Occupancy validation
- `validateRoomSelection()` - Room validation
- `validateBeforeRoomSelection()` - Pre-room validation
- `validateCheckInDate()` - Check-in validation
- `validateCheckOutDate()` - Check-out validation
- `validateNameField()` - Name validation
- `validatePhoneField()` - Phone validation
- `validateEmailField()` - Email validation

**Note:** `KioskValidationHelper` already exists - enhance it.

**Benefits:**
- Removes ~250-300 lines
- Centralizes validation logic
- Reusable validation methods

#### E. Extract to Helper: `KioskUIUpdater` (~200 lines)
**Location:** `src/main/java/com/hotel/controller/helper/KioskUIUpdater.java`

**Methods to Extract:**
- `updateNightsDisplay()` - Nights calculation display
- `updateNextButtonState()` - Button state updates
- `updateAssignedRoomsLabel()` - Room display
- `displayAddOnBreakdown()` - Addon display
- `loadBookingSummary()` - Summary display (delegate to existing helper)
- `updateLoyaltyDisplay()` - Loyalty UI updates

**Benefits:**
- Removes ~200-250 lines
- Separates UI updates from logic
- Easier to maintain

#### F. Extract to Builder: `KioskReservationBuilder` (~150 lines)
**Location:** `src/main/java/com/hotel/service/builder/KioskReservationBuilder.java`

**Builder Pattern for Complex Reservation Creation:**
```java
public class KioskReservationBuilder {
    public KioskReservationBuilder withGuest(Guest guest)
    public KioskReservationBuilder withDates(LocalDate checkIn, LocalDate checkOut)
    public KioskReservationBuilder withOccupancy(int adults, int children)
    public KioskReservationBuilder withRooms(List<Room> rooms)
    public KioskReservationBuilder withAddons(List<ServiceAddon> addons)
    public Reservation build()
}
```

**Benefits:**
- Removes ~150-200 lines
- Makes reservation creation more readable
- Easier to extend with new fields

**Estimated Reduction:** ~1,500-1,700 lines → **Target: ~450-650 lines**

---

## 3. AdminCheckoutController (888 lines → Target: <1000)

### Status: ✅ Already under 1000 lines
**Action:** Monitor and refactor if it grows

---

## 4. ReportController (859 lines → Target: <1000)

### Status: ✅ Already under 1000 lines
**Action:** Monitor and refactor if it grows

---

## Implementation Priority

### Phase 1: High Impact (AdminReservationController)
1. Extract `AdminReservationService` (~400 lines)
2. Extract `AdminReservationUIHelper` (~300 lines)
3. Extract `ReservationEntityManager` utils (~150 lines)

**Expected Result:** 2,413 → ~1,563 lines (still need more)

### Phase 2: Medium Impact (AdminReservationController)
4. Extract `AdminGuestManagementHelper` (~200 lines)
5. Extract `AdminRoomSelectionHelper` (~250 lines)
6. Extract `ReservationStatusStrategy` (~100 lines)

**Expected Result:** ~1,563 → ~1,013 lines (close to target)

### Phase 3: KioskController Refactoring
7. Extract `KioskBookingService` (~400 lines)
8. Enhance `KioskStateManager` (~300 lines)
9. Enhance `KioskNavigationManager` (~200 lines)
10. Enhance `KioskFormValidator` (~250 lines)
11. Extract `KioskUIUpdater` (~200 lines)
12. Extract `KioskReservationBuilder` (~150 lines)

**Expected Result:** 2,156 → ~656 lines ✅

### Phase 4: Fine-tuning
13. Review and optimize extracted code
14. Add unit tests for services
15. Document new architecture

---

## File Structure After Refactoring

```
src/main/java/com/hotel/
├── controller/
│   ├── AdminReservationController.java (~600-900 lines) ✅
│   ├── KioskController.java (~450-650 lines) ✅
│   └── ...
├── service/
│   ├── AdminReservationService.java (~400 lines) NEW
│   ├── KioskBookingService.java (~400 lines) NEW
│   └── strategy/
│       └── ReservationStatusStrategy.java (~100 lines) NEW
├── controller/helper/
│   ├── AdminReservationUIHelper.java (~300 lines) NEW
│   ├── AdminGuestManagementHelper.java (~200 lines) NEW
│   ├── AdminRoomSelectionHelper.java (~250 lines) NEW
│   ├── KioskStateManager.java (~300 lines) ENHANCED
│   ├── KioskNavigationManager.java (~200 lines) ENHANCED
│   ├── KioskFormValidator.java (~250 lines) ENHANCED
│   └── KioskUIUpdater.java (~200 lines) NEW
├── util/
│   └── ReservationEntityManager.java (~150 lines) NEW
└── service/builder/
    └── KioskReservationBuilder.java (~150 lines) NEW
```

---

## Benefits Summary

1. **Maintainability:** Smaller, focused files are easier to understand and modify
2. **Testability:** Services and helpers can be unit tested independently
3. **Reusability:** Extracted code can be reused across controllers
4. **Separation of Concerns:** Clear boundaries between UI, business logic, and data access
5. **Code Quality:** Follows SOLID principles (Single Responsibility, Open/Closed, etc.)

---

## Next Steps

1. Review this plan with the team
2. Start with Phase 1 (AdminReservationController - Service extraction)
3. Test thoroughly after each extraction
4. Update documentation as we go
5. Monitor file sizes to prevent future bloat


