# ReservationAddon EntityExistsException Fix

**Date:** November 26, 2025  
**Status:** ✅ Fixed

---

## 🔴 Error

```
javax.persistence.EntityExistsException: A different object with the same identifier value was already associated with the session : [com.hotel.model.ReservationAddon#com.hotel.model.ReservationAddon@3c1]
```

**Location:** `ReservationService.createReservation()` when saving reservation with add-ons

---

## 🔍 Root Cause

The error occurred because:

1. **Detached Entities:** `ServiceAddon` entities passed to `createReservation()` were detached (loaded from a different EntityManager/session)

2. **Cascade Merge Conflict:** When `reservationRepository.save(reservation)` was called, it used `em.merge()` (since reservation already had an ID). Hibernate tried to cascade merge the `ReservationAddon` entities in the collection.

3. **Duplicate Composite Keys:** If the same `ServiceAddon` was used multiple times, or if a `ReservationAddon` with the same composite key (reservation_id + addon_id) already existed in the session, Hibernate threw `EntityExistsException`.

4. **Session State:** The session might have contained a `ReservationAddon` entity with the same composite key from a previous operation, causing a conflict when trying to merge.

---

## ✅ Solution

### Changes Made:

1. **Ensure Addons are Managed:**
   ```java
   ServiceAddon managedAddon = em.merge(addon);
   ```
   - Merge each `ServiceAddon` to ensure it's managed in the current session

2. **Check for Duplicates:**
   ```java
   boolean alreadyExists = reservation.getReservationAddons().stream()
       .anyMatch(ra -> ra.getAddon().getId().equals(managedAddon.getId()));
   ```
   - Check if the addon is already associated with the reservation to avoid duplicate composite keys

3. **Explicit Persistence:**
   ```java
   em.persist(reservationAddon);
   ```
   - Explicitly persist `ReservationAddon` entities instead of relying on cascade merge
   - This prevents Hibernate from trying to merge entities that might conflict with session state

4. **Flush Before Commit:**
   ```java
   em.flush();
   ```
   - Flush to ensure all entities are persisted before commit

5. **Same Fix for Rooms:**
   - Applied the same pattern to `Room` entities to ensure they're managed:
   ```java
   Room managedRoom = em.merge(room);
   ```

---

## 📝 Code Changes

**File:** `src/main/java/com/hotel/service/ReservationService.java`

**Before:**
```java
// Add addons
for (ServiceAddon addon : addons) {
    ReservationAddon reservationAddon = new ReservationAddon(reservation, addon, 1);
    reservation.getReservationAddons().add(reservationAddon);
}

reservation = reservationRepository.save(reservation);
```

**After:**
```java
// Add addons - ensure addons are managed entities and explicitly persist ReservationAddon
for (ServiceAddon addon : addons) {
    // Merge the addon to ensure it's managed in the current session
    ServiceAddon managedAddon = em.merge(addon);
    
    // Check if this addon is already associated with this reservation
    // (to avoid duplicate composite keys)
    boolean alreadyExists = reservation.getReservationAddons().stream()
        .anyMatch(ra -> ra.getAddon().getId().equals(managedAddon.getId()));
    
    if (!alreadyExists) {
        ReservationAddon reservationAddon = new ReservationAddon(reservation, managedAddon, 1);
        // Explicitly persist ReservationAddon instead of relying on cascade
        em.persist(reservationAddon);
        reservation.getReservationAddons().add(reservationAddon);
    }
}

// Flush to ensure all entities are persisted before commit
em.flush();
```

---

## 🎯 Why This Works

1. **Managed Entities:** By merging `ServiceAddon` entities, we ensure they're managed in the current session, preventing detached entity issues.

2. **Duplicate Prevention:** Checking for existing associations prevents adding duplicate `ReservationAddon` entities with the same composite key.

3. **Explicit Persistence:** Using `em.persist()` for new `ReservationAddon` entities instead of relying on cascade merge avoids conflicts with existing session state.

4. **Flush:** Flushing ensures all persisted entities are written to the database before commit, making the transaction atomic.

---

## 🧪 Testing

**Test Cases:**
- [x] Create reservation with single addon
- [x] Create reservation with multiple addons
- [x] Create reservation with same addon selected multiple times (should only add once)
- [x] Create reservation with no addons
- [x] Verify addons are correctly persisted in database

---

## 📚 Related Concepts

### Entity States in Hibernate:
- **Transient:** New entity, not associated with session
- **Persistent/Managed:** Entity associated with session, tracked by Hibernate
- **Detached:** Entity was persistent but session is closed
- **Removed:** Entity marked for deletion

### Composite Primary Keys:
- `ReservationAddon` uses a composite key: `(reservation_id, addon_id)`
- Hibernate requires entities with composite keys to implement `Serializable`, `equals()`, and `hashCode()`
- Duplicate composite keys in the same session cause `EntityExistsException`

### Cascade Operations:
- `CascadeType.ALL` on `@OneToMany` means all operations cascade to children
- `merge()` cascades to children, which can cause conflicts if children are already in session
- Explicit persistence avoids cascade conflicts

---

## ✅ Summary

**Problem:** `EntityExistsException` when creating reservations with add-ons due to detached entities and cascade merge conflicts.

**Solution:** 
- Merge entities to ensure they're managed
- Check for duplicates before adding
- Explicitly persist `ReservationAddon` entities
- Flush before commit

**Result:** ✅ Reservations with add-ons can now be created successfully without session conflicts.

