# Error Resolution Plan - Step by Step

## Overview
Analyzing errors from logs and fixing them systematically, one at a time.

---

## ERROR #1: TransientPropertyValueException - Guest Not Saved

**Status:** 🔴 CRITICAL - Blocking reservation creation

**Error Message:**
```
org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
```

**Location:**
- `ReservationService.createReservation()` line 205
- `KioskController.confirmBooking()` line 1441

**Root Cause:**
- Guest entity is created but NOT saved to database before creating Reservation
- Hibernate requires Guest to be persisted (have an ID) before it can be referenced by Reservation
- The `guest` passed to `createReservation()` is a transient entity (not saved)

**Investigation Steps:**
1. Check where Guest is created in `KioskController.validateGuestDetails()`
2. Check if Guest is saved after creation
3. Verify GuestRepository.save() is called
4. Check if Guest has an ID after save

**Fix Required:**
- Save Guest BEFORE creating Reservation
- Ensure Guest has an ID before passing to createReservation()

---

## ERROR #2: Add-ons Table Empty (0 Addons)

**Status:** 🔴 CRITICAL - Add-ons not working

**Error Message:**
```
[AddonRepository] Found 0 service addons
```

**Location:**
- `AddonRepository.findAll()` returns empty list
- Database query: `SELECT * FROM service_addon` returns 0 rows

**Root Cause:**
- `seed_data.sql` has typo: `INSERT INTO service_addons` (plural)
- Actual table name is `service_addon` (singular)
- Seed data was never inserted

**Investigation Steps:**
1. Check `seed_data.sql` for table name
2. Verify actual table name in database
3. Check if seed script was run
4. Verify addon data exists

**Fix Required:**
- Fix table name in `seed_data.sql` from `service_addons` to `service_addon`
- Re-run seed script OR manually insert addon data

---

## ERROR #3: Table Not Displaying (Data Loads But Not Visible)

**Status:** 🟡 MEDIUM - Data loads but UI doesn't show

**Evidence:**
- Logs show: "Table items set. Table now has 2 items"
- Logs show: "Table visible: true"
- Logs show: "Table columns count: 4"
- Cell value factories are being called (logs show values)
- But user reports table is empty

**Possible Causes:**
1. Table is behind another component (z-order issue)
2. Table size is 0x0 (not visible)
3. CSS hiding table
4. Table not in scene graph
5. Container visibility issue

**Investigation Steps:**
1. Check FXML for table visibility settings
2. Check container visibility
3. Verify table is in scene graph
4. Check CSS for display:none or visibility:hidden
5. Verify table prefHeight/prefWidth

---

## Resolution Steps

### Step 1: Fix Error #1 (Guest Not Saved)
1. Find where Guest is created in KioskController
2. Ensure Guest is saved before creating Reservation
3. Verify Guest has ID after save
4. Test reservation creation

### Step 2: Fix Error #2 (Add-ons Empty)
1. Fix seed_data.sql table name
2. Insert addon data into database
3. Verify addons load correctly
4. Test add-on selection

### Step 3: Fix Error #3 (Table Not Displaying)
1. Check FXML for table settings
2. Verify container visibility
3. Check CSS
4. Add explicit size/visibility settings
5. Test table display

### Step 4: Verify Entire Flow
1. Test complete booking flow
2. Verify all data displays correctly
3. Check for any new errors
4. Verify database updates correctly

---

## Next: Starting with Error #1

