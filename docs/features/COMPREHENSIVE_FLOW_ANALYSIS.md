# Comprehensive Application Flow Analysis

## Table of Contents
1. [Application Entry Point](#application-entry-point)
2. [Kiosk Module Flow](#kiosk-module-flow)
3. [FXML Files and Controllers Mapping](#fxml-files-and-controllers-mapping)
4. [Database Interactions](#database-interactions)
5. [Services and Responsibilities](#services-and-responsibilities)
6. [Data Flow Diagrams](#data-flow-diagrams)
7. [Issues and Recommendations](#issues-and-recommendations)

---

## Application Entry Point

### Main.java
- **File:** `src/main/java/com/hotel/app/Main.java`
- **Entry Point:** `main(String[] args)`
- **Initialization:**
  1. Calls `AppConfig.initialize()` - Sets up EntityManagerFactory, LoggerService, Policies
  2. Loads `WelcomeScreen.fxml` as initial screen
  3. Sets stage size: 1200x800
- **Shutdown:** Calls `AppConfig.shutdown()` on application close

---

## Kiosk Module Flow

### Complete User Journey

```
1. WelcomeScreen.fxml
   ↓ [Start Booking Button]
2. BookingDetails.fxml (Occupancy Only)
   ↓ [Next Button - validateOccupancyAndProceed()]
3. GuestDetails.fxml
   ↓ [Next Button - validateGuestDetails()]
4. DateSelection.fxml (Dates Only)
   ↓ [Next Button - validateDatesAndProceed()]
5. RoomSelection.fxml
   ↓ [Accept Suggestion OR Validate Room Selection]
6. AddOnServices.fxml
   ↓ [Next Button - proceedToSummary()]
7. BookingSummary.fxml
   ↓ [Confirm Booking Button - confirmBooking()]
8. ConfirmationScreen.fxml
```

---

## FXML Files and Controllers Mapping

### 1. WelcomeScreen.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/WelcomeScreen.fxml`

**UI Elements:**
- No fx:id fields (static content)
- **Button:** `onAction="#showRules"` → `showRules()` method
- **Button:** `onAction="#startBooking"` → `startBooking(ActionEvent)` method

**Controller Methods:**
- `startBooking(ActionEvent event)` - Navigates to BookingDetails.fxml
- `showRules()` - Shows rules dialog

**Navigation:**
- `startBooking()` → `/view/kiosk/BookingDetails.fxml`

---

### 2. BookingDetails.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/BookingDetails.fxml`

**UI Elements:**
- `fx:id="numAdultsField"` - TextField for adults count
- `fx:id="adultsErrorLabel"` - Error label for adults
- `fx:id="numChildrenField"` - TextField for children count
- `fx:id="childrenErrorLabel"` - Error label for children
- `fx:id="nextButtonOccupancy"` - Next button (disabled by default)
- **Button:** `onAction="#goBack"` → `goBack()` method
- **Button:** `onAction="#validateOccupancyAndProceed"` → `validateOccupancyAndProceed()` method
- **TextField:** `onKeyReleased="#checkOccupancyFields"` → `checkOccupancyFields()` method

**Controller Methods:**
- `checkOccupancyFields()` - Validates and updates button state
- `validateOccupancyAndProceed()` - Validates occupancy and navigates to GuestDetails
- `validateOccupancyFields()` - Returns boolean if fields are valid
- `updateNextButtonState()` - Enables/disables nextButtonOccupancy

**Data Stored:**
- `numAdults` (int)
- `numChildren` (int)

**Navigation:**
- `goBack()` → `/view/kiosk/WelcomeScreen.fxml`
- `validateOccupancyAndProceed()` → `/view/kiosk/GuestDetails.fxml`

---

### 3. GuestDetails.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/GuestDetails.fxml`

**UI Elements:**
- `fx:id="nameField"` - TextField for guest name
- `fx:id="nameErrorLabel"` - Error label for name
- `fx:id="phoneField"` - TextField for phone
- `fx:id="phoneErrorLabel"` - Error label for phone
- `fx:id="emailField"` - TextField for email
- `fx:id="emailErrorLabel"` - Error label for email
- `fx:id="addressField"` - TextArea for address (optional)
- `fx:id="nextButtonGuest"` - Next button (disabled by default)
- **Button:** `onAction="#goBack"` → `goBack()` method
- **Button:** `onAction="#validateGuestDetails"` → `validateGuestDetails()` method

**Controller Methods:**
- `validateNameField()` - Validates name on focus lost
- `validatePhoneField()` - Validates phone on focus lost
- `validateEmailField()` - Validates email on focus lost
- `validateGuestDetails()` - Full validation and navigation
- `validateGuestFields()` - Returns boolean if all fields valid
- `updateNextButtonState()` - Enables/disables nextButtonGuest

**Focus Listeners (in initialize()):**
- `nameField.focusedProperty()` → calls `validateNameField()` on blur
- `phoneField.focusedProperty()` → calls `validatePhoneField()` on blur
- `emailField.focusedProperty()` → calls `validateEmailField()` on blur

**Data Stored:**
- `currentGuest` (Guest object) - Created or found by email

**Database Operations:**
- `guestRepository.findByEmail(email)` - Check if guest exists
- `guestRepository.save(guest)` - Save new or update existing guest

**Navigation:**
- `goBack()` → `/view/kiosk/BookingDetails.fxml`
- `validateGuestDetails()` → `/view/kiosk/DateSelection.fxml`

---

### 4. DateSelection.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/DateSelection.fxml`

**UI Elements:**
- `fx:id="checkInDatePicker"` - DatePicker for check-in
- `fx:id="checkInErrorLabel"` - Error label for check-in
- `fx:id="checkOutDatePicker"` - DatePicker for check-out
- `fx:id="checkOutErrorLabel"` - Error label for check-out
- `fx:id="numNightsDisplayLabel"` - Label showing number of nights
- `fx:id="nightsInfoContainer"` - VBox container for nights display
- `fx:id="nextButtonDates"` - Next button (disabled by default)
- **Button:** `onAction="#goBack"` → `goBack()` method
- **Button:** `onAction="#validateDatesAndProceed"` → `validateDatesAndProceed()` method
- **DatePicker:** `onAction="#validateCheckInDate"` → `validateCheckInDate()` method
- **DatePicker:** `onAction="#validateCheckOutDate"` → `validateCheckOutDate()` method

**Controller Methods:**
- `validateCheckInDate()` - Validates check-in date individually
- `validateCheckOutDate()` - Validates check-out date individually
- `validateDates()` - Full date validation
- `validateDatesAndProceed()` - Validates dates and navigates
- `validateDateFields()` - Returns boolean if dates are valid
- `updateNextButtonState()` - Enables/disables nextButtonDates

**Data Stored:**
- `checkIn` (LocalDate)
- `checkOut` (LocalDate)

**Validation:**
- Check-in cannot be in the past
- Check-out must be after check-in
- Uses `reservationService.validateDates()` for service-level validation

**Navigation:**
- `goBack()` → `/view/kiosk/GuestDetails.fxml`
- `validateDatesAndProceed()` → `/view/kiosk/RoomSelection.fxml` (via `navigateToRoomSelection()`)

---

### 5. RoomSelection.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/RoomSelection.fxml`

**UI Elements:**
- `fx:id="suggestedPlanContainer"` - VBox for suggested room plan (visible by default)
- `fx:id="suggestedRoomsTable"` - TableView for suggested rooms
  - `fx:id="roomTypeColumn"` - TableColumn for room type
  - `fx:id="quantityColumn"` - TableColumn for quantity
  - `fx:id="pricePerNightColumn"` - TableColumn for price per night
  - `fx:id="totalPriceColumn"` - TableColumn for total price
- `fx:id="customSelectionContainer"` - VBox for custom selection (hidden by default)
- `fx:id="singleRoomSpinner"` - Spinner for single rooms
- `fx:id="doubleRoomSpinner"` - Spinner for double rooms
- `fx:id="deluxeRoomSpinner"` - Spinner for deluxe rooms
- `fx:id="penthouseSpinner"` - Spinner for penthouse
- `fx:id="occupancyValidationLabel"` - Error label for room selection
- **Button:** `onAction="#goBack"` → `goBack()` method
- **Button:** `onAction="#validateRoomSelection"` → `validateRoomSelection()` method
- **Button:** `onAction="#acceptSuggestion"` → `acceptSuggestion()` method
- **Button:** Removed `onAction="#adjustChoices"` (redundant with "Choose My Own")
- **Button:** `onAction="#chooseCustom"` → `chooseCustom()` method
- **Button:** `onAction="#showBookingPolicy"` → `showBookingPolicy()` method

**Controller Methods:**
- `setBookingState(int, int, LocalDate, LocalDate, Guest)` - Sets booking state from previous screen
- `loadRoomSuggestions(List<RoomSuggestion>)` - Populates suggested rooms table
- `loadAvailableRooms()` - Updates spinner max values based on availability
- `setupTableColumns()` - Configures table column cell factories
- `acceptSuggestion()` - Accepts suggested room plan
- `chooseCustom()` - Shows custom selection container
- `chooseCustom()` - Shows custom selection (replaced `adjustChoices()`)
- `validateRoomSelection()` - Validates custom room selection
- `showBookingPolicy()` - Shows booking policy

**Data Flow:**
1. `navigateToRoomSelection()` is called from DateSelection
2. Calls `reservationService.suggestRooms(numAdults, numChildren, checkIn, checkOut)`
3. Gets list of `RoomSuggestion` objects
4. Calls `loadRoomSuggestions(suggestions)` to populate table
5. Calls `loadAvailableRooms()` to update spinner limits

**Database Operations:**
- `reservationService.suggestRooms()` → `roomRepository.findAvailableByTypeAndDateRange()`
- `reservationService.getAvailableRooms()` → Queries available rooms by type

**Data Stored:**
- `selectedRooms` (List<Room>) - Selected rooms for booking

**Navigation:**
- `goBack()` → `/view/kiosk/DateSelection.fxml`
- `acceptSuggestion()` → `/view/kiosk/AddOnServices.fxml` (via `navigateToAddOns()`)
- `validateRoomSelection()` → `/view/kiosk/AddOnServices.fxml` (via `navigateToAddOns()`)

---

### 6. AddOnServices.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/AddOnServices.fxml`

**UI Elements:**
- `fx:id="wifiCheckBox"` - CheckBox for Wi-Fi
- `fx:id="breakfastCheckBox"` - CheckBox for Breakfast
- `fx:id="parkingCheckBox"` - CheckBox for Parking
- `fx:id="spaCheckBox"` - CheckBox for Spa
- `fx:id="addOnTotalLabel"` - Label showing add-on total
- **Button:** `onAction="#goBack"` → `goBack()` method
- **Button:** `onAction="#proceedToSummary"` → `proceedToSummary()` method
- **CheckBox:** `onAction="#updateAddOnTotal"` → `updateAddOnTotal()` method (all checkboxes)

**Controller Methods:**
- `updateAddOnTotal()` - Calculates and updates add-on total
- `proceedToSummary()` - Navigates to booking summary

**Focus Listeners (in initialize()):**
- `wifiCheckBox.selectedProperty()` → calls `updateAddOnTotal()`
- `breakfastCheckBox.selectedProperty()` → calls `updateAddOnTotal()`
- `parkingCheckBox.selectedProperty()` → calls `updateAddOnTotal()`
- `spaCheckBox.selectedProperty()` → calls `updateAddOnTotal()`

**Data Flow:**
1. User checks/unchecks add-on
2. `updateAddOnTotal()` is called
3. Queries `addonRepository.findAll()` to get all addons
4. Filters selected addons by checkbox state
5. Calculates total based on pricing model (PER_NIGHT vs PER_RESERVATION)
6. Updates `addOnTotalLabel` with formatted price

**Database Operations:**
- `addonRepository.findAll()` - Gets all service addons

**Data Stored:**
- `selectedAddons` (List<ServiceAddon>) - Selected add-ons

**Pricing Calculation:**
- PER_NIGHT: `price * number_of_nights`
- PER_RESERVATION: `price` (one-time)

**Navigation:**
- `goBack()` → `/view/kiosk/RoomSelection.fxml`
- `proceedToSummary()` → `/view/kiosk/BookingSummary.fxml`

---

### 7. BookingSummary.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/BookingSummary.fxml`

**UI Elements:**
- `fx:id="guestNameLabel"` - Guest name display
- `fx:id="guestPhoneLabel"` - Guest phone display
- `fx:id="guestEmailLabel"` - Guest email display
- `fx:id="checkInLabel"` - Check-in date display
- `fx:id="checkOutLabel"` - Check-out date display
- `fx:id="numNightsLabel"` - Number of nights display
- `fx:id="roomDetailsLabel"` - Room details display
- `fx:id="roomSubtotalLabel"` - Room subtotal display
- `fx:id="addOnSubtotalLabel"` - Add-on subtotal display
- `fx:id="taxRateLabel"` - Tax rate display
- `fx:id="taxAmountLabel"` - Tax amount display
- `fx:id="discountContainer"` - VBox for discount (hidden by default)
- `fx:id="discountLabel"` - Discount amount display
- `fx:id="loyaltyContainer"` - VBox for loyalty (hidden by default)
- `fx:id="loyaltyLabel"` - Loyalty points effect display
- `fx:id="totalAmountLabel"` - Total amount display
- **Button:** `onAction="#goBack"` → `goBack()` method
- **Button:** `onAction="#confirmBooking"` → `confirmBooking()` method

**Controller Methods:**
- `loadBookingSummary()` - Populates all summary fields
- `confirmBooking()` - Creates reservation and billing

**Data Flow:**
1. `loadBookingSummary()` is called when screen loads
2. Calculates room subtotal using `pricingService.calculateRoomPrice()`
3. Calculates add-on subtotal using Decorator Pattern
4. Calculates tax (10% of subtotal)
5. Checks for discounts and loyalty points
6. Updates all labels with calculated values

**Services Used:**
- `PricingService` - Room price calculation
- `BillingService` - Billing creation (after confirmation)
- Decorator Pattern - Add-on price calculation

**Navigation:**
- `goBack()` → `/view/kiosk/AddOnServices.fxml`
- `confirmBooking()` → `/view/kiosk/ConfirmationScreen.fxml`

---

### 8. ConfirmationScreen.fxml
**Controller:** `KioskController`
**Location:** `src/main/resources/view/kiosk/ConfirmationScreen.fxml`

**UI Elements:**
- `fx:id="reservationNumberLabel"` - Reservation number display
- `fx:id="bookingDetailsLabel"` - Booking details display
- `fx:id="feedbackButton"` - Button to submit feedback (hidden by default)
- **Button:** `onAction="#goToFeedback"` → `goToFeedback()` method
- **Button:** `onAction="#startNewBooking"` → `startNewBooking()` method

**Controller Methods:**
- `loadConfirmation()` - Populates confirmation details
- `goToFeedback()` - Navigates to feedback screen
- `startNewBooking()` - Resets state and goes to welcome screen

**Data Flow:**
1. `confirmBooking()` creates reservation
2. Gets reservation ID
3. Calls `loadConfirmation()` to display details
4. Shows reservation number and booking summary

**Database Operations:**
- `reservationService.createReservation()` - Creates reservation
- `billingService.createBilling()` - Creates billing record

**Navigation:**
- `startNewBooking()` → `/view/kiosk/WelcomeScreen.fxml` (resets state)
- `goToFeedback()` → `/view/feedback/FeedbackSubmission.fxml`

---

## Database Interactions

### Entities and Tables

#### Guest Entity
- **Table:** `guest`
- **Fields:** id, name, phone, email, address, loyalty_number, loyalty_points
- **Operations:**
  - `GuestRepository.findByEmail(String)` - Find existing guest
  - `GuestRepository.save(Guest)` - Create or update guest

#### Room Entity
- **Table:** `room`
- **Fields:** id, hotel_id, roomNumber, type, beds, basePrice, status
- **Operations:**
  - `RoomRepository.findAvailableByTypeAndDateRange(RoomType, LocalDate, LocalDate)` - Find available rooms
  - `RoomRepository.findByStatus(RoomStatus)` - Find rooms by status

#### Reservation Entity
- **Table:** `reservation`
- **Fields:** id, guest_id, check_in, check_out, num_adults, num_children, status
- **Operations:**
  - `ReservationService.createReservation()` - Creates reservation with rooms and addons
  - `ReservationRepository.save(Reservation)` - Persists reservation

#### ReservationRoom Entity (Junction Table)
- **Table:** `reservation_room`
- **Fields:** reservation_id, room_id (composite primary key)
- **Purpose:** Many-to-many relationship between reservations and rooms

#### ReservationAddon Entity (Junction Table)
- **Table:** `reservation_addon`
- **Fields:** reservation_id, addon_id, quantity (composite primary key)
- **Purpose:** Many-to-many relationship between reservations and addons

#### ServiceAddon Entity
- **Table:** `service_addon`
- **Fields:** id, name, price, pricing_model, active
- **Operations:**
  - `AddonRepository.findAll()` - Get all active addons

#### Billing Entity
- **Table:** `billing`
- **Fields:** id, reservation_id, subtotal, tax, discounts, loyalty_points_used, total
- **Operations:**
  - `BillingService.createBilling(Reservation, double)` - Creates billing record

---

## Services and Responsibilities

### ReservationService
**Location:** `src/main/java/com/hotel/service/ReservationService.java`

**Responsibilities:**
1. **Room Availability:**
   - `isRoomAvailable(RoomType, LocalDate, LocalDate)` - Check if room type is available
   - `getAvailableRooms(RoomType, LocalDate, LocalDate)` - Get list of available rooms
   - `suggestRooms(int, int, LocalDate, LocalDate)` - Suggest rooms based on occupancy

2. **Reservation Management:**
   - `createReservation(Guest, List<Room>, LocalDate, LocalDate, int, int, List<ServiceAddon>)` - Create reservation
   - `cancelReservation(Long)` - Cancel reservation
   - `validateDates(LocalDate, LocalDate)` - Validate date range
   - `validateOccupancy(List<Room>, int, int)` - Validate room capacity

3. **Business Rules:**
   - Occupancy limits: Single/Deluxe/Penthouse = 2 people, Double = 4 people
   - Room suggestions based on total people count

**Dependencies:**
- `RoomRepository` - Room queries
- `ReservationRepository` - Reservation persistence
- `GuestRepository` - Guest operations
- `AddonRepository` - Addon queries
- `RoomAvailabilityPublisher` - Event publishing
- `LoggerService` - Logging

---

### PricingService
**Location:** `src/main/java/com/hotel/service/PricingService.java`

**Responsibilities:**
1. **Price Calculation:**
   - `calculateRoomPrice(Room, LocalDate, LocalDate)` - Calculate room price for date range
   - Applies pricing policies (seasonal, dynamic pricing)

**Dependencies:**
- `PricingPolicy` - Business rules for pricing
- `LoggerService` - Logging

---

### BillingService
**Location:** `src/main/java/com/hotel/service/BillingService.java`

**Responsibilities:**
1. **Billing Creation:**
   - `createBilling(Reservation, double)` - Create billing record
   - Calculates tax, applies discounts, loyalty points

**Dependencies:**
- `BillingRepository` - Billing persistence
- `PricingService` - Price calculations
- `LoggerService` - Logging

---

## Data Flow Diagrams

### Complete Booking Flow

```
User Input → Controller → Service → Repository → Database
                ↓
            Validation
                ↓
            Business Logic
                ↓
            Persistence
                ↓
            UI Update
```

### Room Selection Flow

```
DateSelection Screen
    ↓
validateDatesAndProceed()
    ↓
navigateToRoomSelection()
    ↓
reservationService.suggestRooms()
    ↓
roomRepository.findAvailableByTypeAndDateRange()
    ↓
Database Query (excludes overlapping reservations)
    ↓
Returns List<Room>
    ↓
ReservationService.suggestRooms() creates RoomSuggestion objects
    ↓
loadRoomSuggestions() populates table
    ↓
User selects rooms
    ↓
validateRoomSelection()
    ↓
selectedRooms list populated
```

### Add-On Selection Flow

```
AddOnServices Screen
    ↓
User checks/unchecks checkbox
    ↓
updateAddOnTotal() called
    ↓
addonRepository.findAll()
    ↓
Filter by selected checkboxes
    ↓
Calculate total:
  - PER_NIGHT: price * nights
  - PER_RESERVATION: price
    ↓
Update addOnTotalLabel
```

---

## Issues and Recommendations

### Current Issues Found

1. **Table Not Displaying:**
   - **Status:** Logs show 2 items loaded, but table not rendering
   - **Possible Causes:**
     - Cell value factories not being called
     - Table visibility issue
     - CSS styling hiding content
   - **Fix Applied:** Added `setVisible(true)`, `setManaged(true)`, `requestLayout()`

2. **Add-On Total Not Updating:**
   - **Status:** Fixed - Added listeners in `initialize()`
   - **Fix Applied:** Added `selectedProperty()` listeners to all checkboxes

3. **Back Button Navigation:**
   - **Status:** Fixed - Added `previousScreen` tracking
   - **Fix Applied:** `goBack()` now uses navigation history

4. **Date Selection Flow:**
   - **Status:** Fixed - Separated occupancy and dates
   - **Fix Applied:** Created separate BookingDetails.fxml and DateSelection.fxml

### Recommendations

1. **Error Handling:**
   - Add try-catch blocks around all database operations
   - Show user-friendly error messages
   - Log all errors for debugging

2. **Validation:**
   - Add client-side validation before service calls
   - Show inline error messages immediately
   - Disable Next buttons until validation passes

3. **State Management:**
   - Consider using a state object to track booking progress
   - Persist state in case of application crash
   - Add ability to resume booking

4. **Testing:**
   - Add unit tests for all service methods
   - Add integration tests for complete flow
   - Test edge cases (no rooms available, invalid dates, etc.)

5. **Performance:**
   - Cache room availability queries
   - Optimize database queries
   - Add pagination for large result sets

---

## Complete Method Mapping

### KioskController Methods by Screen

#### WelcomeScreen
- `startBooking(ActionEvent)` → Navigate to BookingDetails
- `showRules()` → Show rules dialog

#### BookingDetails
- `checkOccupancyFields()` → Validate occupancy fields
- `validateOccupancyAndProceed()` → Validate and navigate
- `validateOccupancyFields()` → Return validation status
- `updateNextButtonState()` → Enable/disable Next button

#### GuestDetails
- `validateNameField()` → Validate name on blur
- `validatePhoneField()` → Validate phone on blur
- `validateEmailField()` → Validate email on blur
- `validateGuestDetails()` → Full validation and navigation
- `validateGuestFields()` → Return validation status

#### DateSelection
- `validateCheckInDate()` → Validate check-in date
- `validateCheckOutDate()` → Validate check-out date
- `validateDates()` → Full date validation
- `validateDatesAndProceed()` → Validate and navigate

#### RoomSelection
- `setBookingState()` → Set booking state
- `loadRoomSuggestions()` → Populate suggestions table
- `loadAvailableRooms()` → Update spinner limits
- `setupTableColumns()` → Configure table columns
- `acceptSuggestion()` → Accept suggested plan
- `chooseCustom()` → Show custom selection
- `chooseCustom()` → Show custom selection (replaced `adjustChoices()`)
- `validateRoomSelection()` → Validate custom selection

#### AddOnServices
- `updateAddOnTotal()` → Calculate and update total
- `proceedToSummary()` → Navigate to summary

#### BookingSummary
- `loadBookingSummary()` → Populate summary fields
- `confirmBooking()` → Create reservation and billing

#### ConfirmationScreen
- `loadConfirmation()` → Populate confirmation details
- `goToFeedback()` → Navigate to feedback
- `startNewBooking()` → Reset and start new booking

---

## Database Schema Reference

### Key Tables

```sql
guest (id, name, phone, email, address, loyalty_number, loyalty_points)
room (id, hotel_id, room_number, type, beds, base_price, status)
reservation (id, guest_id, check_in, check_out, num_adults, num_children, status)
reservation_room (reservation_id, room_id) -- Composite PK
reservation_addon (reservation_id, addon_id, quantity) -- Composite PK
service_addon (id, name, price, pricing_model, active)
billing (id, reservation_id, subtotal, tax, discounts, loyalty_points_used, total)
```

---

## Service Dependencies Graph

```
KioskController
    ├── ReservationService
    │   ├── RoomRepository
    │   ├── ReservationRepository
    │   ├── GuestRepository
    │   ├── AddonRepository
    │   └── RoomAvailabilityPublisher
    ├── PricingService
    │   └── PricingPolicy
    ├── BillingService
    │   ├── BillingRepository
    │   └── PricingService
    ├── GuestRepository
    └── AddonRepository
```

---

## Critical Issues Found

### 1. Orphaned FXML File
- **File:** `src/main/resources/view/kiosk/OccupancySelection.fxml`
- **Status:** EXISTS but NOT USED
- **Issue:** This file was replaced by `BookingDetails.fxml` but still exists
- **Recommendation:** DELETE this file to avoid confusion

### 2. Flow Verification
The actual flow is:
1. WelcomeScreen → `startBooking()` → BookingDetails (occupancy)
2. BookingDetails → `validateOccupancyAndProceed()` → GuestDetails
3. GuestDetails → `validateGuestDetails()` → DateSelection
4. DateSelection → `validateDatesAndProceed()` → RoomSelection
5. RoomSelection → `acceptSuggestion()` or `validateRoomSelection()` → AddOnServices
6. AddOnServices → `proceedToSummary()` → BookingSummary
7. BookingSummary → `confirmBooking()` → ConfirmationScreen

### 3. Navigation State Transfer
- **Current Implementation:** State is transferred via `navigateToScreen()` method
- **State Variables Transferred:**
  - `numAdults`, `numChildren`
  - `checkIn`, `checkOut`
  - `currentGuest`
  - `selectedRooms`
  - `selectedAddons`
  - `previousScreen` (for back button)

### 4. Missing Connections
- **loadBookingSummary()** is called AFTER navigation in `proceedToSummary()` - this may cause timing issues
- **loadConfirmation()** is called AFTER navigation in `confirmBooking()` - same issue
- **Recommendation:** Call these methods in `initialize()` when on the respective screens

---

## End of Analysis

This document provides a complete mapping of the application flow, FXML files, controllers, database interactions, and services. Use this as a reference for debugging and understanding the complete system architecture.

### Quick Reference Checklist

- [x] All FXML files mapped to controllers
- [x] All fx:id fields verified
- [x] All onAction handlers verified
- [x] Complete flow documented
- [x] Database interactions documented
- [x] Services and responsibilities documented
- [x] Issues identified
- [ ] Orphaned file removed (OccupancySelection.fxml)
- [ ] Timing issues fixed (loadBookingSummary, loadConfirmation)

