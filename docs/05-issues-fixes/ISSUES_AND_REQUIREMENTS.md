# 🚨 ISSUES AND REQUIREMENTS CHECK

**Date:** [Current Session]  
**Status:** ⚠️ **1 CRITICAL ISSUE + 8 IMPORTANT ISSUES FOUND**

---

## ❌ **CRITICAL ISSUE #1: EntityManager Lifecycle Violation**

### **Requirement (Page 5, Line 214):**
> "The EntityManager must be created per transaction or unit of work and must not be shared across threads."

### **Current Implementation - WRONG:**
```java
// ❌ WRONG: EntityManager stored as field, reused across transactions
public class ReservationService {
    private EntityManager em;  // ❌ Stored as instance field
    
    public ReservationService(EntityManager em) {
        this.em = em;  // ❌ Same EntityManager reused
    }
    
    public Reservation createReservation(...) {
        em.getTransaction().begin();  // ❌ Using same EntityManager
        // ... transaction code ...
        em.getTransaction().commit();
    }
}
```

**Problem:**
- EntityManager is created once per service instance in `AppConfig.createReservationService()`
- Same EntityManager is reused for multiple transactions
- EntityManager is stored as a field in services
- EntityManager is also stored in controllers (AdminController, KioskController, etc.)

**Required Fix:**
```java
// ✅ CORRECT: EntityManager created per transaction
public class ReservationService {
    // NO EntityManager field!
    
    public Reservation createReservation(...) {
        EntityManager em = AppConfig.createEntityManager();  // ✅ New for each transaction
        try {
            em.getTransaction().begin();
            // ... transaction code ...
            em.getTransaction().commit();
        } finally {
            em.close();  // ✅ Close after transaction
        }
    }
}
```

**Impact:** ⚠️ **CRITICAL** - Violates ORM lifecycle requirements, can cause:
- Transaction isolation issues
- Memory leaks
- Thread safety problems
- Data consistency issues

**Files Affected:**
- All Services: `ReservationService`, `BillingService`, `FeedbackService`, `WaitlistService`, `LoyaltyService`, `ReportingService`, `ActivityLogService`
- All Controllers: `AdminController`, `KioskController`, `FeedbackController`, `LoyaltyController`

---

## ⚠️ **IMPORTANT ISSUES**

### Issue #2: Missing Checkout Feedback Reminder

**Requirement (Page 3, Line 126):**
> "The system must trigger room availability notifications after checkout and must remind the administrator to invite the guest to submit feedback at the kiosk."

**Current Implementation:**
- ✅ Room availability notifications work (Observer pattern)
- ❌ **MISSING:** Reminder to invite guest for feedback

**Location:** `AdminController.handleCheckout()` - Line 537

**Required Fix:**
```java
showAlert("Success", 
    "Checkout completed successfully! Rooms are now available.\n\n" +
    "Please remind the guest to submit feedback at the kiosk.");
```

**Impact:** ⚠️ **MEDIUM** - Requirement not fully met

---

### Issue #3: Waitlist Conversion Not Implemented

**Requirement (Page 3, Line 130):**
> "The system must provide a quick conversion from a waitlist entry to a reservation."

**Current Implementation:**
- ❌ `convertToReservation()` is just a stub (shows alert)

**Location:** `AdminController.convertToReservation()` - Line 1429

**Required Fix:**
- Implement full conversion logic:
  1. Get selected waitlist entry
  2. Check room availability for requested type and date range
  3. Create reservation from waitlist data
  4. Remove from waitlist
  5. Notify admin of success

**Impact:** ⚠️ **MEDIUM** - Core functionality missing

---

### Issue #4: Revenue Report Room Type Filtering Missing

**Requirement (Page 6, Line 282):**
> "The system must support filtering by date range and room type and must export to CSV or PDF."

**Current Implementation:**
- ✅ Date range filtering exists
- ❌ **MISSING:** Room type filtering

**Location:** `ReportingService.generateRevenueReport()` - Line 42

**Required Fix:**
- Add `roomType` parameter to `generateRevenueReport()`
- Filter reservations by room type
- Update `ReportController` to pass room type filter

**Impact:** ⚠️ **MEDIUM** - Requirement not fully met

---

### Issue #5: Occupancy Report Room Type Filtering Missing

**Requirement (Page 6, Line 286):**
> "The system must support filtering by date range and room type and must export to CSV and PDF."

**Current Implementation:**
- ✅ Date filtering exists
- ❌ **MISSING:** Room type filtering

**Location:** `ReportingService.generateOccupancyReport()` - Line 70

**Required Fix:**
- Add `roomType` parameter to `generateOccupancyReport()`
- Filter rooms by type when calculating occupancy
- Update `ReportController` to pass room type filter

**Impact:** ⚠️ **MEDIUM** - Requirement not fully met

---

### Issue #6: Seasonal Pricing Not Configured

**Requirement (Page 4, Line 182):**
> "The system must apply seasonal multipliers for defined date ranges such as peak season."

**Current Implementation:**
- ✅ `PricingPolicy` has seasonal multiplier support (code exists)
- ❌ **MISSING:** No seasonal multipliers configured in `AppConfig`

**Location:** `AppConfig.initialize()` - Line 48

**Required Fix:**
```java
pricingPolicy = new PricingPolicy(1.2, 1.0);
// Add peak season multiplier
LocalDate peakStart = LocalDate.of(2025, 12, 15);
LocalDate peakEnd = LocalDate.of(2026, 1, 15);
PricingPolicy.Season peakSeason = new PricingPolicy.Season("Peak Season", peakStart, peakEnd);
pricingPolicy.addSeasonalMultiplier(peakSeason, 1.5); // 50% increase
```

**Impact:** ⚠️ **MEDIUM** - Business rule not fully implemented

---

## 📊 **DATABASE CONFIGURATION**

### **Question: "Should SQL data set be on the server?"**

### **Answer: NO - For Desktop Application, Localhost is CORRECT**

**Current Setup:**
```xml
<!-- persistence.xml -->
<property name="javax.persistence.jdbc.url" 
    value="jdbc:mysql://localhost:3306/hotel_db"/>
```

**Analysis:**
1. **Project Type:** Desktop-only application (JavaFX)
2. **No Server Requirement:** Instructions don't mention server (except optional multithreaded server)
3. **Standard Practice:** Desktop apps use localhost database
4. **SQL Scripts:** ✅ Created (`create_schema.sql`, `seed_data.sql`) - This is what's required for submission

**Conclusion:**
- ✅ **Current setup is CORRECT** for desktop application
- ✅ **SQL scripts are provided** (required for submission)
- ✅ **Database can be localhost** (standard for desktop apps)

**Only if implementing multithreaded server (optional):**
- Then database should be on server
- But this is an optional requirement

---

## 🎯 **PRIORITY FIX ORDER**

### **Priority 1: CRITICAL - EntityManager Lifecycle (MUST FIX)**
**Time:** 4-6 hours  
**Impact:** Architecture violation, can cause bugs

**Fix Required:**
- Remove EntityManager fields from all services
- Create EntityManager per transaction in each service method
- Close EntityManager after transaction
- Remove EntityManager fields from controllers (or create per operation)

---

### **Priority 2: IMPORTANT - Business Rules (SHOULD FIX)**
1. **Checkout feedback reminder** - 5 minutes
2. **Waitlist conversion** - 1-2 hours
3. **Revenue report room type filter** - 1 hour
4. **Occupancy report room type filter** - 1 hour
5. **Seasonal pricing configuration** - 15 minutes

**Total Time:** ~4-5 hours

---

## ✅ **WHAT'S WORKING CORRECTLY**

1. ✅ All models and entities
2. ✅ All repositories
3. ✅ All business logic (except EntityManager lifecycle)
4. ✅ All design patterns
5. ✅ Dependency injection
6. ✅ Kiosk booking flow
7. ✅ Admin login and dashboard
8. ✅ Payment processing
9. ✅ Checkout flow (except reminder)
10. ✅ Discount application
11. ✅ Waitlist management (except conversion)
12. ✅ Feedback submission
13. ✅ Reports (except room type filtering)
14. ✅ Exports
15. ✅ Loyalty program
16. ✅ Database scripts
17. ✅ Database configuration (localhost is correct)

---

**The most critical issue is EntityManager lifecycle management!** This violates the project requirements and can cause serious bugs. ⚠️



