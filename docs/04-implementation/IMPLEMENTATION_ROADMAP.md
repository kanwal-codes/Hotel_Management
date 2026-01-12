# Implementation Roadmap - Hotel Reservation System

## 🎯 Strategy: Bottom-Up Approach

**Why Bottom-Up?**
- Start with data layer (Model + Repository) → Business logic (Service) → UI (Controller)
- Each layer depends on the one below it
- Test each layer before moving up
- Simpler debugging and development

---

## 📋 Phase 1: Foundation (Week 1)

### Step 1.1: Database Setup
**Priority: CRITICAL - Everything depends on this**

1. **Create MySQL Database**
   ```sql
   CREATE DATABASE hotel_db;
   ```

2. **Update persistence.xml**
   - Set correct database URL, username, password
   - Verify Hibernate configuration

3. **Test Database Connection**
   - Create simple test to verify connection works

**Time Estimate:** 1-2 hours

---

### Step 1.2: Model Layer (Entities + Enums)
**Priority: CRITICAL - Foundation for everything**

**Order of Creation:**
1. **Enums First** (no dependencies)
   - `RoomType` (SINGLE, DOUBLE, DELUXE, PENTHOUSE)
   - `ReservationStatus` (PENDING, CONFIRMED, CANCELLED, CHECKED_OUT)
   - `RoomStatus` (AVAILABLE, OCCUPIED, MAINTENANCE)
   - `Role` (ADMIN, MANAGER)
   - `PaymentMethod` (CASH, CARD, POINTS)
   - `PricingModel` (PER_NIGHT, PER_RESERVATION)

2. **Simple Entities** (depend only on enums)
   - `Hotel` (id, name, city)
   - `Room` (id, roomNumber, type, beds, basePrice, status, hotel)
   - `ServiceAddon` (id, name, price, pricingModel)
   - `AdminUser` (id, username, passwordHash, role, active)

3. **Guest Entity**
   - `Guest` (id, name, phone, email, address, loyaltyPoints)

4. **Reservation Entities** (depend on Guest, Room)
   - `Reservation` (id, checkIn, checkOut, numAdults, numChildren, status, guest)
   - `ReservationRoom` (reservation, room) - Join table
   - `ReservationAddon` (reservation, addon, quantity)

5. **Billing Entities** (depend on Reservation)
   - `Billing` (id, reservation, subtotal, taxRate, taxAmount, discountValue, loyaltyRedeemedPoints, totalAmount, paidAmount, balanceAmount, paymentStatus)
   - `Payment` (id, billing, method, amount, createdAt)

6. **Other Entities**
   - `Feedback` (id, guest, reservation, rating, comments, sentimentTag, createdAt)
   - `Waitlist` (id, guest, requestedType, dateRangeStart, dateRangeEnd, status)
   - `AuditLog` (id, timestamp, actor, action, entityType, entityId, message)

**Key Points:**
- Use JPA annotations: `@Entity`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany`, `@OneToOne`
- Add validation annotations: `@NotNull`, `@Email`, `@Min`, `@Max`
- Use `@JoinColumn` for foreign keys
- Set cascade types appropriately

**Time Estimate:** 4-6 hours

---

### Step 1.3: Repository Layer
**Priority: HIGH - Needed by services**

**Create Repository Interfaces:**
1. `GuestRepository`
2. `RoomRepository`
3. `ReservationRepository`
4. `BillingRepository`
5. `PaymentRepository`
6. `FeedbackRepository`
7. `AdminUserRepository`
8. `WaitlistRepository`
9. `AddonRepository`
10. `AuditLogRepository`

**For each repository:**
- Extend `JpaRepository<Entity, Long>` or create custom interface
- Add custom query methods as needed:
  - `findByEmail(String email)`
  - `findAvailableRoomsByTypeAndDateRange(...)`
  - `findReservationsByGuest(...)`
  - etc.

**Key Points:**
- Keep repositories simple - just data access
- Use `@Repository` annotation
- Use `@Query` for complex queries

**Time Estimate:** 3-4 hours

---

### Step 1.4: Utility Layer (Cross-Cutting)
**Priority: HIGH - Used by all services**

1. **LoggerService (Singleton)**
   - Implement Singleton pattern
   - Configure FileHandler with rotation (1MB, 10 files)
   - Methods: `logInfo()`, `logError()`, `logActivity()`

2. **BCryptPasswordHasher**
   - Simple wrapper around BCrypt
   - Methods: `hash()`, `verify()`

3. **Validators**
   - `EmailValidator`
   - `PhoneValidator`
   - `DateValidator`

4. **Exporters** (can be done later)
   - `CsvExporter`
   - `PdfExporter`

**Time Estimate:** 2-3 hours

---

## 📋 Phase 2: Business Logic (Week 2)

### Step 2.1: Configuration Layer
**Priority: HIGH - Business rules**

1. **PricingPolicy**
   - Weekend multiplier
   - Weekday multiplier
   - Seasonal multipliers (date ranges)

2. **DiscountPolicy**
   - Admin cap: 15%
   - Manager cap: 30%

3. **LoyaltyPolicy**
   - Earning rate (e.g., 1 point per $10)
   - Redemption caps

**Time Estimate:** 2 hours

---

### Step 2.2: Design Patterns Implementation
**Priority: HIGH - Required patterns**

#### 2.2.1: Factory Pattern - RoomFactory
```java
public class RoomFactory {
    public static Room createRoom(RoomType type, String roomNumber, double basePrice) {
        // Create room based on type
        // Set appropriate beds, basePrice
    }
}
```

#### 2.2.2: Strategy Pattern - Billing Strategies
```java
public interface BillingStrategy {
    double calculateTotal(Billing billing);
}

public class StandardBillingStrategy implements BillingStrategy { }
public class DiscountBillingStrategy implements BillingStrategy { }
public class LoyaltyBillingStrategy implements BillingStrategy { }
```

#### 2.2.3: Decorator Pattern - Add-ons
```java
public abstract class BookingComponent {
    public abstract double getPrice();
}

public class AddOnDecorator extends BookingComponent {
    private BookingComponent component;
    private ServiceAddon addon;
    // Decorate with addon pricing
}
```

#### 2.2.4: Observer Pattern - Waitlist Notifications
```java
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

public interface Observer {
    void update(String message);
}

public class RoomAvailabilityPublisher implements Subject { }
public class WaitlistSubscriber implements Observer { }
```

**Time Estimate:** 4-5 hours

---

### Step 2.3: Service Layer (Core Business Logic)
**Priority: CRITICAL - Heart of the application**

**Order of Implementation:**

1. **AuthService** (simplest, needed first)
   - `login(username, password)` → returns AdminUser or null
   - `hasRole(adminUser, role)` → check permissions
   - Uses BCryptPasswordHasher

2. **PricingService**
   - `calculateRoomPrice(room, checkIn, checkOut)` → applies multipliers
   - `calculateAddonPrice(addon, nights)` → per night or per reservation
   - Uses PricingPolicy

3. **ReservationService**
   - `validateDates(checkIn, checkOut)`
   - `checkAvailability(roomType, checkIn, checkOut)`
   - `suggestRooms(numAdults, numChildren)` → group booking logic
   - `createReservation(guest, rooms, dates, addons)`
   - `cancelReservation(reservationId)`
   - Uses RoomFactory, PricingService

4. **BillingService**
   - `createBilling(reservation)` → initial bill
   - `applyDiscount(billing, discountPercent, appliedBy)` → role-based caps
   - `calculateTotal(billing)` → uses BillingStrategy
   - Uses Strategy pattern for billing calculations

5. **LoyaltyService**
   - `earnPoints(guest, amount)` → configurable rate
   - `redeemPoints(guest, points)` → apply discount
   - `getBalance(guest)` → current points

6. **PaymentService** (or part of BillingService)
   - `processPayment(billing, method, amount)`
   - `processRefund(billing, amount)`
   - `getBalance(billing)` → outstanding amount

7. **WaitlistService**
   - `addToWaitlist(guest, roomType, dateRange)`
   - `convertToReservation(waitlistEntry)`
   - Uses Observer pattern for notifications

8. **FeedbackService**
   - `submitFeedback(guest, reservation, rating, comments)`
   - `validateEligibility(reservation)` → must be checked out, balance = 0

9. **ReportingService**
   - `generateRevenueReport(startDate, endDate, period)`
   - `generateOccupancyReport(startDate, endDate, period)`
   - `generateActivityLogs()`
   - `generateFeedbackSummary()`
   - Uses Exporters

10. **ActivityLogService**
    - `logAction(actor, action, entityType, entityId, message)`
    - Uses LoggerService

**Key Points:**
- All services use constructor injection for dependencies
- Services call repositories, not ORM directly
- Services use LoggerService for logging
- Services validate business rules

**Time Estimate:** 12-15 hours

---

## 📋 Phase 3: Dependency Injection & Configuration (Week 2-3)

### Step 3.1: AppConfig (Dependency Injection)
**Priority: HIGH - Wires everything together**

```java
public class AppConfig {
    private static EntityManagerFactory emf; // Singleton
    private static LoggerService logger; // Singleton
    
    // Policies
    private static PricingPolicy pricingPolicy;
    private static DiscountPolicy discountPolicy;
    private static LoyaltyPolicy loyaltyPolicy;
    
    // Repositories (created with EntityManager per transaction)
    // Services (created with repositories)
    
    public static void initialize() {
        // 1. Create EntityManagerFactory (Singleton)
        // 2. Create LoggerService (Singleton)
        // 3. Create Policies
        // 4. Create Repositories (with EntityManager per operation)
        // 5. Create Services (with repositories)
        // 6. Wire Controllers (with services)
    }
    
    public static EntityManager createEntityManager() {
        // Create new EntityManager per transaction
        return emf.createEntityManager();
    }
}
```

**Time Estimate:** 3-4 hours

---

## 📋 Phase 4: Presentation Layer (Week 3-4)

### Step 4.1: Admin Module (Start Here - Simpler)
**Priority: HIGH - Core functionality**

**Why Admin First?**
- No complex UI flows
- Can test services directly
- Needed for testing other features

**Screens to Implement:**
1. **LoginScreen.fxml**
   - Username, password fields
   - Login button
   - Error messages

2. **Dashboard.fxml**
   - Search bar (name, phone, date range)
   - Results table (paginated, sortable)
   - Action buttons (View, Edit, Cancel)

3. **ReservationDetails.fxml**
   - View/edit reservation
   - Add rooms, modify dates
   - Conflict checking

4. **PaymentProcessing.fxml**
   - Process payments (cash, card, points)
   - View balance
   - Apply discounts (with role check)

5. **CheckoutScreen.fxml**
   - Final bill
   - Apply loyalty redemption
   - Mark as checked out
   - Free rooms

6. **WaitlistManagement.fxml**
   - View waitlist
   - Convert to reservation

7. **LoyaltyProgram.fxml**
   - View loyalty dashboard
   - Enroll guests

8. **FeedbackManagement.fxml**
   - View feedback (after checkout)
   - Filter by rating, date, sentiment

9. **ReportsScreen.fxml**
   - Revenue reports
   - Occupancy reports
   - Activity logs
   - Export buttons

**AdminController Implementation:**
- Wire services via AppConfig
- Handle button clicks
- Validate inputs
- Show error messages
- Update UI

**Time Estimate:** 15-20 hours

---

### Step 4.2: Kiosk Module
**Priority: HIGH - Core booking flow**

**Screens (Already have FXML structure):**
1. **WelcomeScreen.fxml** ✅
2. **DateSelection.fxml** ✅
3. **GuestDetails.fxml** ✅
4. **RoomSelection.fxml** ✅
5. **AddOnServices.fxml** ✅
6. **BookingSummary.fxml** ✅
7. **ConfirmationScreen.fxml** ✅

**KioskController Implementation:**
- Step-by-step flow
- Validate at each step
- Show room suggestions
- Calculate pricing dynamically
- Save reservation at end

**Key Methods:**
- `startBooking()` → go to occupancy selection
- `validateOccupancy()` → check adults/children
- `validateDates()` → check-in/out validation
- `validateGuestDetails()` → name, email, phone
- `suggestRooms()` → call ReservationService
- `updateAddOnTotal()` → calculate addon prices
- `confirmBooking()` → save reservation

**Time Estimate:** 12-15 hours

---

### Step 4.3: Feedback Module
**Priority: MEDIUM - Simple module**

**Screens:**
1. **FeedbackSubmission.fxml**
   - Rating (1-5 stars)
   - Comments text area
   - Submit button

2. **FeedbackConfirmation.fxml**
   - Thank you message

**FeedbackController Implementation:**
- Validate reservation is checked out
- Validate balance is zero
- Submit feedback
- Show confirmation

**Time Estimate:** 3-4 hours

---

## 📋 Phase 5: Testing & Refinement (Week 4-5)

### Step 5.1: Unit Testing
- Test services in isolation
- Test repositories
- Test business rules

### Step 5.2: Integration Testing
- Test full flows (booking → payment → checkout)
- Test waitlist → notification → conversion
- Test reporting

### Step 5.3: Bug Fixes
- Fix validation issues
- Fix UI bugs
- Fix database issues

### Step 5.4: Polish
- Improve error messages
- Add loading indicators
- Improve UI/UX

**Time Estimate:** 8-10 hours

---

## 📋 Phase 6: Documentation & Submission (Week 5)

### Step 6.1: Code Documentation
- Add JavaDoc comments
- Document design patterns
- Document business rules

### Step 6.2: Project Documentation
- Follow documentation checklist
- Create UML diagrams
- Write reflection

### Step 6.3: Video Recording
- Record 7-10 minute demo
- Show all features
- Explain challenges

**Time Estimate:** 6-8 hours

---

## 🎯 Quick Start Guide (First 2 Days)

### Day 1 Morning: Database & Models
1. Set up MySQL database
2. Create all enums
3. Create Hotel, Room, Guest entities
4. Test with simple save/retrieve

### Day 1 Afternoon: More Models
1. Create Reservation, Billing, Payment entities
2. Create remaining entities
3. Test relationships

### Day 2 Morning: Repositories & Utilities
1. Create all repository interfaces
2. Implement LoggerService (Singleton)
3. Implement BCryptPasswordHasher

### Day 2 Afternoon: First Service
1. Implement AuthService
2. Create AppConfig skeleton
3. Test admin login

---

## 🔑 Key Principles

### 1. **Keep It Simple**
- Don't over-engineer
- Use standard JPA patterns
- Follow JavaFX best practices

### 2. **Test As You Go**
- Test each layer before moving up
- Use simple test cases
- Verify database operations

### 3. **Follow the Architecture**
- Controllers → Services → Repositories → Database
- No shortcuts or direct database access from controllers

### 4. **Implement Patterns Correctly**
- Singleton: LoggerService, EntityManagerFactory
- Strategy: Billing calculations
- Observer: Waitlist notifications
- Factory: Room creation
- Decorator: Add-on pricing

### 5. **Handle Errors Gracefully**
- Validate inputs
- Show user-friendly error messages
- Log all errors

---

## 📊 Estimated Total Time

- **Phase 1 (Foundation):** 10-15 hours
- **Phase 2 (Business Logic):** 18-22 hours
- **Phase 3 (DI & Config):** 3-4 hours
- **Phase 4 (UI):** 30-39 hours
- **Phase 5 (Testing):** 8-10 hours
- **Phase 6 (Documentation):** 6-8 hours

**Total: 75-98 hours** (approximately 2-3 weeks of full-time work)

---

## 🚨 Common Pitfalls to Avoid

1. **Don't skip the model layer** - Everything depends on it
2. **Don't forget validation** - At both UI and service level
3. **Don't ignore logging** - Required for grading
4. **Don't hardcode values** - Use configuration classes
5. **Don't mix layers** - Keep separation clean
6. **Don't forget transactions** - Use EntityManager properly
7. **Don't skip error handling** - Users need feedback

---

## ✅ Success Checklist

- [ ] Database created and connected
- [ ] All entities created with JPA annotations
- [ ] All repositories implemented
- [ ] All services implemented with business logic
- [ ] All design patterns implemented
- [ ] Admin module fully functional
- [ ] Kiosk module fully functional
- [ ] Feedback module functional
- [ ] Reporting working with exports
- [ ] Logging configured with rotation
- [ ] Authentication with BCrypt working
- [ ] All business rules enforced
- [ ] Documentation complete
- [ ] Video recorded

---

## 🆘 When Stuck

1. **Check the instructions file** - Requirements are there
2. **Check existing documentation** - Blueprint has examples
3. **Test in isolation** - Break down the problem
4. **Use simple examples first** - Get it working, then improve
5. **Check JavaFX/JPA documentation** - Standard patterns

---

**Good luck! Start with Phase 1 and work systematically. You've got this! 🚀**

