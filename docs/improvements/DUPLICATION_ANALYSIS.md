# Code Duplication Analysis - Services vs KioskController

**Date:** 2025-12-03  
**Goal:** Identify duplicated logic in KioskController that already exists in services/repositories

---

## ✅ Already Using Services (Good!)

### ReservationService
- ✅ `reservationService.suggestRooms()` - Used correctly
- ✅ `reservationService.getAvailableRooms()` - Used correctly
- ✅ `reservationService.createReservation()` - Used correctly
- ✅ `reservationService.validateDates()` - **USED BUT ALSO DUPLICATED!**
- ✅ `reservationService.validateOccupancy()` - **USED BUT ALSO DUPLICATED!**

### PricingService
- ✅ `pricingService.calculateRoomPrice()` - Used correctly

### BillingService
- ✅ `billingService.createBilling()` - Used correctly
- ✅ `billingService.processPayment()` - Used correctly

---

## ❌ DUPLICATION FOUND!

### 1. **Date Validation** - DUPLICATED
**Service Method:**
- `ReservationService.validateDates(LocalDate checkIn, LocalDate checkOut)` ✅

**KioskController Duplication:**
- Lines ~1073-1095: Manual date validation logic
- Lines ~970-1002: Individual date validation
- **Status:** KioskController calls `reservationService.validateDates()` but ALSO has its own validation logic

**Fix:** Remove manual validation, use service method only

**Lines Saved:** ~50-80 lines

---

### 2. **Occupancy Validation** - DUPLICATED
**Service Method:**
- `ReservationService.validateOccupancy(List<Room> rooms, int numAdults, int numChildren)` ✅

**KioskController Duplication:**
- Lines ~1976-1980: Manual occupancy validation
- **Status:** KioskController calls `reservationService.validateOccupancy()` correctly, but validation logic might be duplicated in helper

**Fix:** Ensure we're using service method, not duplicating logic

**Lines Saved:** ~10-20 lines

---

### 3. **Loyalty Enrollment** - PARTIALLY DUPLICATED
**Service:** 
- `LoyaltyService` exists but doesn't have enrollment method
- KioskController manually generates loyalty number: `"L" + String.format("%06d", guest.getId())`

**KioskController Duplication:**
- Lines ~809-855: `enrollInLoyaltyFromGuestDetails()` - Manual enrollment
- Lines ~2763-2820: `enrollInLoyalty()` - Manual enrollment (duplicate method!)

**Fix:** 
- Option 1: Add `enrollGuest()` method to `LoyaltyService`
- Option 2: Keep in helper but remove duplication between two methods

**Lines Saved:** ~50-100 lines (if we remove duplicate method)

---

### 4. **Loyalty Lookup** - COULD USE REPOSITORY
**Repository Method:**
- `GuestRepository.findByLoyaltyNumber()` - EXISTS ✅

**KioskController:**
- Lines ~2708-2758: `lookupLoyalty()` - Uses `findAll()` and streams instead of `findByLoyaltyNumber()`

**Fix:** Use `guestRepository.findByLoyaltyNumber()` directly

**Lines Saved:** ~20-30 lines

---

### 5. **Guest Processing** - COULD BE IN SERVICE
**KioskController:**
- Lines ~1113-1136: Manual guest create/find logic
- Uses `guestRepository.findByEmail()` and `guestRepository.save()`

**Service:** No `GuestService` exists

**Fix:** 
- Option 1: Create `GuestService` with `createOrUpdateGuest()` method
- Option 2: Keep in helper (current approach is fine)

**Lines Saved:** ~20-30 lines (if we create service)

---

## 📊 Summary

### Duplications Found:
1. ✅ Date validation - **DUPLICATED** (~50-80 lines)
2. ✅ Occupancy validation - **USED CORRECTLY** (but check for duplication)
3. ✅ Loyalty enrollment - **DUPLICATE METHOD** (~50-100 lines)
4. ✅ Loyalty lookup - **INEFFICIENT** (~20-30 lines)
5. ⚠️ Guest processing - **OK** (but could be in service)

### Total Potential Savings:
- **~140-240 lines** by removing duplications
- **~50-80 lines** by using services more efficiently

### Recommended Actions:

1. **HIGH PRIORITY:**
   - Remove duplicate date validation logic (use service only)
   - Remove duplicate `enrollInLoyalty()` method
   - Fix `lookupLoyalty()` to use `findByLoyaltyNumber()`

2. **MEDIUM PRIORITY:**
   - Consider adding `enrollGuest()` to `LoyaltyService`
   - Consider creating `GuestService` for guest operations

3. **LOW PRIORITY:**
   - Current usage of other services is good

---

## Implementation Plan

### Phase 1: Fix Duplications (15 min)
1. Remove manual date validation, use `reservationService.validateDates()` only
2. Remove duplicate `enrollInLoyalty()` method
3. Fix `lookupLoyalty()` to use `guestRepository.findByLoyaltyNumber()`

### Phase 2: Service Enhancements (Optional - 10 min)
1. Add `enrollGuest()` to `LoyaltyService`
2. Update KioskController to use it

---

**Status:** Ready to fix duplications  
**Estimated Lines Saved:** ~140-240 lines

