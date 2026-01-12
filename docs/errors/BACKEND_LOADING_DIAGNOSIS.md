# Backend Loading Diagnosis - Critical Issues Found

## Problem Statement
**Nothing from backend is loading on views - nothing shows up ever**

## Critical Issues Identified

### Issue #1: EntityManager Not in Transaction for Read Operations
**Location:** All Repository classes

**Problem:**
- Repositories are created with EntityManager
- Read queries (SELECT) are executed WITHOUT transactions
- Hibernate requires transactions even for read operations in some configurations
- Queries may be failing silently

**Current Code:**
```java
public List<Room> findAvailableByTypeAndDateRange(RoomType type, LocalDate checkIn, LocalDate checkOut) {
    // NO TRANSACTION!
    TypedQuery<Room> query = em.createQuery(queryStr, Room.class);
    return query.getResultList(); // May fail silently
}
```

**Fix Required:**
- Wrap read operations in transactions
- OR configure Hibernate to allow read-only transactions
- OR use `em.getTransaction().begin()` for read operations

---

### Issue #2: Services Creating New EntityManagers Per Call
**Location:** AppConfig.java

**Problem:**
- Each service/repository gets a NEW EntityManager
- EntityManagers are not shared or managed
- No transaction management
- EntityManagers may not be properly initialized

**Current Code:**
```java
public static ReservationService createReservationService() {
    return new ReservationService(createEntityManager()); // NEW EM every time
}
```

**Impact:**
- Each service call creates a new EntityManager
- No connection pooling benefits
- Potential connection leaks
- Transactions not managed properly

---

### Issue #3: No Error Handling in Repository Queries
**Location:** All Repository classes

**Problem:**
- Queries may throw exceptions
- Exceptions are not caught
- No logging of query failures
- UI doesn't know if query failed

**Example:**
```java
public List<Room> findAvailableByTypeAndDateRange(...) {
    TypedQuery<Room> query = em.createQuery(queryStr, Room.class);
    return query.getResultList(); // No try-catch!
}
```

---

### Issue #4: Services May Be Null
**Location:** KioskController.initialize()

**Problem:**
- Services are initialized in `initialize()`
- If initialization fails, services remain null
- No validation that services are not null before use
- Methods may throw NullPointerException

**Current Code:**
```java
@FXML
private void initialize() {
    reservationService = AppConfig.createReservationService();
    // What if this fails? No error handling!
}
```

---

### Issue #5: Database Connection May Not Be Active
**Location:** EntityManager creation

**Problem:**
- EntityManagerFactory may not be properly initialized
- Database connection may be closed
- No verification that connection is active
- Queries fail silently

---

### Issue #6: No Logging of Query Results
**Location:** All Repository and Service methods

**Problem:**
- Queries execute but results not logged
- Can't verify if data is being retrieved
- No debugging information

---

## Diagnostic Steps

### Step 1: Verify Database Connection
```java
// Add to AppConfig.initialize()
EntityManager testEm = emf.createEntityManager();
try {
    testEm.getTransaction().begin();
    Long count = testEm.createQuery("SELECT COUNT(r) FROM Room r", Long.class).getSingleResult();
    logger.logInfo("Database connection test: Found " + count + " rooms");
    testEm.getTransaction().commit();
} catch (Exception e) {
    logger.logError("Database connection test failed", e);
} finally {
    testEm.close();
}
```

### Step 2: Add Transaction to Read Operations
```java
public List<Room> findAvailableByTypeAndDateRange(RoomType type, LocalDate checkIn, LocalDate checkOut) {
    em.getTransaction().begin();
    try {
        TypedQuery<Room> query = em.createQuery(queryStr, Room.class);
        List<Room> results = query.getResultList();
        em.getTransaction().commit();
        logger.logInfo("Found " + results.size() + " available rooms");
        return results;
    } catch (Exception e) {
        em.getTransaction().rollback();
        logger.logError("Failed to find available rooms", e);
        return new ArrayList<>();
    }
}
```

### Step 3: Add Service Validation
```java
@FXML
private void initialize() {
    try {
        em = AppConfig.createEntityManager();
        reservationService = AppConfig.createReservationService();
        pricingService = AppConfig.createPricingService();
        billingService = AppConfig.createBillingService();
        guestRepository = AppConfig.createGuestRepository();
        addonRepository = AppConfig.createAddonRepository();
        logger = LoggerService.getInstance();
        
        // VALIDATE SERVICES
        if (reservationService == null) {
            logger.logError("ReservationService is NULL!");
            throw new RuntimeException("ReservationService initialization failed");
        }
        if (pricingService == null) {
            logger.logError("PricingService is NULL!");
            throw new RuntimeException("PricingService initialization failed");
        }
        // ... validate all services
        
        logger.logInfo("All services initialized successfully");
    } catch (Exception e) {
        logger.logError("Failed to initialize services", e);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Initialization Error");
        alert.setHeaderText("Failed to initialize application");
        alert.setContentText("Please restart the application. Error: " + e.getMessage());
        alert.showAndWait();
    }
}
```

### Step 4: Add Query Result Logging
```java
public List<Room> findAvailableByTypeAndDateRange(...) {
    logger.logInfo("Querying available rooms: type=" + type + ", checkIn=" + checkIn + ", checkOut=" + checkOut);
    // ... query code ...
    logger.logInfo("Query returned " + results.size() + " rooms");
    return results;
}
```

---

## Immediate Fixes Required

1. **Add transactions to all read operations**
2. **Add try-catch to all repository methods**
3. **Add logging to all queries**
4. **Validate services are not null**
5. **Add error handling in controller methods**
6. **Test database connection on startup**

---

## Testing Checklist

- [ ] Database connection is active
- [ ] EntityManagerFactory is initialized
- [ ] Services are not null
- [ ] Queries return data (check logs)
- [ ] Transactions are working
- [ ] Errors are logged
- [ ] UI shows error messages if backend fails

---

## Next Steps

1. Implement transaction management for read operations
2. Add comprehensive error handling
3. Add logging to all database operations
4. Validate service initialization
5. Test with actual database queries
6. Verify data flows from database → repository → service → controller → UI

