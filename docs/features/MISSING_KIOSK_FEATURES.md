# Missing Kiosk Features - Requirements vs Implementation

**Date:** November 26, 2025  
**Status:** Analysis Complete

---

## 📋 Requirements Analysis

Based on `PROJECT_INSTRUCTIONS.md`, here are the **required features** for the Kiosk (self-service) module and what's **currently missing**:

---

## ❌ MISSING FEATURES

### 1. **Welcome Screen - Instructional Video/GIF** ❌

**Requirement:**
> "The kiosk must display a brief, friendly welcome message and an **optional short instructional video or GIF**."

**Current Status:**
- ✅ Welcome message: **Present**
- ❌ Instructional video/GIF: **MISSING**

**What to Add:**
- Add a `MediaView` or `ImageView` component to `WelcomeScreen.fxml`
- Support for video (MP4) or animated GIF
- Optional: Add play/pause controls for video
- Place it in the "How It Works" section or as a separate card

**File to Modify:** `src/main/resources/view/kiosk/WelcomeScreen.fxml`

---

### 2. **Add-On Services - Individual Price Impact Display** ❌

**Requirement:**
> "The kiosk must let the guest select add-on services such as Wi-Fi, breakfast, parking, and spa and **must show the price impact for each selection**."

**Current Status:**
- ✅ Add-on selection checkboxes: **Present**
- ✅ Total add-on price: **Present**
- ❌ **Individual price impact per add-on: MISSING**

**What to Add:**
- Show individual price for each add-on when selected
- Display price next to each checkbox (e.g., "Wi-Fi - $10/night - Selected: $30.00")
- Update individual prices in real-time as checkboxes are selected/deselected
- Show pricing model (PER_NIGHT vs PER_RESERVATION) clearly

**File to Modify:** `src/main/resources/view/kiosk/AddOnServices.fxml`

**Example Enhancement:**
```xml
<HBox spacing="10.0">
    <CheckBox fx:id="wifiCheckBox" text="Wi-Fi" />
    <Label text="$10.00/night" />
    <Label fx:id="wifiPriceLabel" text="Selected: $0.00" visible="false" />
</HBox>
```

---

### 3. **Room Selection - Booking Policy Reminder** ⚠️ PARTIALLY MISSING

**Requirement:**
> "Indicate the guest to check the rooms booking policy **if user decide to choose their own type of room and quantity**."

**Current Status:**
- ✅ "View Booking Policy" button: **Present** (in custom selection)
- ⚠️ **Warning/reminder message: MISSING** - Should be more prominent

**What to Add:**
- Add a prominent warning/reminder label when "Choose My Own" is clicked
- Display message like: "⚠️ Please check the booking policy before selecting rooms. Occupancy limits apply."
- Make it visible and styled as a warning (yellow/orange background)
- Show it automatically when custom selection is enabled

**File to Modify:** `src/main/resources/view/kiosk/RoomSelection.fxml`

---

### 4. **Booking Summary - Loyalty Effects Display** ❌

**Requirement:**
> "Before confirmation, the kiosk must present a complete estimate including subtotal, tax, add-ons **and any loyalty effects**."

**Current Status:**
- ✅ Loyalty container in FXML: **Present** (`loyaltyContainer`, `loyaltyLabel`)
- ❌ **Loyalty calculation and display: NOT IMPLEMENTED**
- ❌ **Loyalty points check: NOT IMPLEMENTED**
- ❌ **Loyalty redemption preview: NOT IMPLEMENTED**

**What to Add:**
- Check if guest has loyalty account and points
- Calculate potential loyalty redemption discount
- Display loyalty points available
- Show loyalty discount effect on total
- Make `loyaltyContainer` visible when loyalty applies
- Update `loyaltyLabel` with points info and discount amount

**Files to Modify:**
- `src/main/resources/view/kiosk/BookingSummary.fxml` (already has containers)
- `src/main/java/com/hotel/controller/KioskController.java` (implement logic)

**Required Logic:**
```java
// In loadBookingSummary()
if (currentGuest != null && currentGuest.getLoyaltyNumber() != null) {
    int availablePoints = currentGuest.getLoyaltyPoints();
    // Calculate potential discount from loyalty points
    // Display in loyaltyContainer
    loyaltyContainer.setVisible(true);
    loyaltyLabel.setText("Available Points: " + availablePoints + " (Potential discount: $X.XX)");
}
```

---

### 5. **Booking Summary - Discount Display** ❌

**Requirement:**
> "Before confirmation, the kiosk must present a complete estimate including **subtotal, tax, add-ons and any loyalty effects**."

**Note:** Discounts are typically applied by admins, but the kiosk should show if any discounts are pre-applied.

**Current Status:**
- ✅ Discount container in FXML: **Present** (`discountContainer`, `discountLabel`)
- ❌ **Discount calculation and display: NOT IMPLEMENTED**

**What to Add:**
- Check if any discounts apply to the booking
- Display discount amount if applicable
- Make `discountContainer` visible when discount applies

**Files to Modify:**
- `src/main/java/com/hotel/controller/KioskController.java`

---

### 6. **Add-On Services - Dynamic Pricing Display** ❌

**Requirement:**
> "Must show the price impact for each add-on selection."

**Current Status:**
- ❌ **Dynamic pricing per add-on: NOT SHOWN**
- ❌ **PER_NIGHT vs PER_RESERVATION distinction: NOT CLEAR**

**What to Add:**
- Display individual price for each add-on
- Show calculated total for each add-on based on:
  - PER_NIGHT: `price × number of nights`
  - PER_RESERVATION: `price` (one-time)
- Update in real-time as user selects/deselects

**File to Modify:** `src/main/resources/view/kiosk/AddOnServices.fxml`

**Example:**
```xml
<VBox spacing="10.0">
    <HBox spacing="15.0">
        <CheckBox fx:id="wifiCheckBox" text="Wi-Fi" />
        <Label text="($10.00/night)" styleClass="price-info" />
        <Label fx:id="wifiTotalLabel" text="Total: $0.00" styleClass="addon-total" />
    </HBox>
    <Label text="For 3 nights: $30.00" fx:id="wifiCalculationLabel" visible="false" />
</VBox>
```

---

### 7. **Confirmation Screen - Loyalty Enrollment Option** ❌

**Requirement:**
> "The system must offer loyalty program to the guest and if guest wants to enrolled for it then use the user information which is filled already confirm it with the guest and issue a loyalty number."

**Current Status:**
- ❌ **Loyalty enrollment option: MISSING** from kiosk flow
- ✅ Loyalty enrollment exists in Admin module

**What to Add:**
- Add loyalty enrollment option on `ConfirmationScreen.fxml`
- Show checkbox: "Enroll in Loyalty Program"
- If checked, enroll guest automatically using existing guest info
- Display loyalty number after enrollment
- Show confirmation message

**Files to Modify:**
- `src/main/resources/view/kiosk/ConfirmationScreen.fxml`
- `src/main/java/com/hotel/controller/KioskController.java`

---

### 8. **Guest Details - Loyalty Number Input/Check** ❌

**Requirement:**
> "The system must offer loyalty program to the guest..."

**Current Status:**
- ❌ **Loyalty number lookup: MISSING**
- ❌ **Option to enter existing loyalty number: MISSING**

**What to Add:**
- Optional field: "Loyalty Number (if you have one)"
- If entered, lookup guest by loyalty number
- Pre-fill guest information if found
- Show loyalty points balance if applicable

**File to Modify:** `src/main/resources/view/kiosk/GuestDetails.fxml`

---

### 9. **Room Selection - Occupancy Validation Display** ⚠️ PARTIALLY MISSING

**Requirement:**
> "The kiosk must enforce occupancy limits per room type across all steps."

**Current Status:**
- ✅ Occupancy validation: **Implemented in controller**
- ⚠️ **Visual occupancy limit display: NOT CLEAR ENOUGH**

**What to Add:**
- Show current occupancy vs. limits more prominently
- Display warning when approaching limits
- Show "Total Guests: X / Max Capacity: Y" in real-time
- Highlight when limits are exceeded

**File to Modify:** `src/main/resources/view/kiosk/RoomSelection.fxml`

---

### 10. **Booking Summary - Detailed Room Breakdown** ⚠️ PARTIALLY MISSING

**Requirement:**
> "Before confirmation, the kiosk must present a complete estimate..."

**Current Status:**
- ✅ Room subtotal: **Present**
- ⚠️ **Individual room details: MISSING** (only shows count)

**What to Add:**
- List each selected room with:
  - Room number
  - Room type
  - Price per night
  - Total for stay
- Show add-on breakdown (which add-ons selected and their individual costs)

**File to Modify:** `src/main/resources/view/kiosk/BookingSummary.fxml`

---

## ✅ FEATURES THAT ARE PRESENT

1. ✅ Welcome message
2. ✅ Rules and regulations button (always visible)
3. ✅ Step-by-step journey
4. ✅ Number of adults and children input
5. ✅ Check-in and check-out dates with validation
6. ✅ Room suggestions OR choose own
7. ✅ Guest details collection with validation
8. ✅ Add-on service selection (Wi-Fi, Breakfast, Parking, Spa)
9. ✅ Add-on total calculation
10. ✅ Complete estimate (subtotal, tax, add-ons)
11. ✅ Reservation saving
12. ✅ Billing reminder message
13. ✅ Occupancy limits enforcement

---

## 📊 Summary Table

| Feature | Required | Status | Priority |
|---------|----------|--------|----------|
| Instructional video/GIF | ✅ | ❌ Missing | Medium |
| Individual add-on price impact | ✅ | ❌ Missing | **HIGH** |
| Booking policy reminder (prominent) | ✅ | ⚠️ Partial | Medium |
| Loyalty effects display | ✅ | ❌ Missing | **HIGH** |
| Discount display | ✅ | ❌ Missing | Low |
| Dynamic add-on pricing | ✅ | ❌ Missing | **HIGH** |
| Loyalty enrollment option | ✅ | ❌ Missing | **HIGH** |
| Loyalty number lookup | ✅ | ❌ Missing | Medium |
| Occupancy validation display | ✅ | ⚠️ Partial | Medium |
| Detailed room breakdown | ✅ | ⚠️ Partial | Medium |

---

## 🎯 Priority Implementation Order

### **HIGH PRIORITY** (Required for full compliance):

1. **Individual Add-On Price Impact** - Show price for each add-on when selected
2. **Loyalty Effects Display** - Calculate and show loyalty points/discounts
3. **Loyalty Enrollment Option** - Allow guests to enroll at confirmation
4. **Dynamic Add-On Pricing** - Show PER_NIGHT vs PER_RESERVATION clearly

### **MEDIUM PRIORITY** (Enhancement):

5. **Booking Policy Reminder** - Make warning more prominent
6. **Loyalty Number Lookup** - Allow entering existing loyalty number
7. **Occupancy Validation Display** - Show limits more clearly
8. **Detailed Room Breakdown** - List individual rooms and prices

### **LOW PRIORITY** (Nice to have):

9. **Instructional Video/GIF** - Optional feature
10. **Discount Display** - Usually admin-only, but good to show if pre-applied

---

## 📝 Implementation Notes

### For Add-On Price Impact:
- Need to calculate price per add-on based on:
  - Number of nights (for PER_NIGHT)
  - One-time price (for PER_RESERVATION)
- Update labels in real-time as checkboxes change
- Show both unit price and total price

### For Loyalty Effects:
- Check `currentGuest.getLoyaltyNumber()` and `currentGuest.getLoyaltyPoints()`
- Use `LoyaltyService` to calculate potential discount
- Display in `loyaltyContainer` on BookingSummary
- Show both available points and potential discount amount

### For Loyalty Enrollment:
- Add checkbox on ConfirmationScreen
- Call `LoyaltyService` or use existing enrollment logic
- Generate loyalty number automatically
- Display confirmation with loyalty number

---

## 🔍 Files That Need Modification

1. **`WelcomeScreen.fxml`** - Add video/GIF support
2. **`AddOnServices.fxml`** - Add individual price displays
3. **`RoomSelection.fxml`** - Enhance policy reminder
4. **`BookingSummary.fxml`** - Already has containers, need logic
5. **`ConfirmationScreen.fxml`** - Add loyalty enrollment
6. **`GuestDetails.fxml`** - Add loyalty number lookup
7. **`KioskController.java`** - Implement all missing logic

---

## ✅ Next Steps

1. Implement HIGH PRIORITY features first
2. Test each feature thoroughly
3. Update documentation
4. Verify compliance with requirements

---

**Last Updated:** November 26, 2025

