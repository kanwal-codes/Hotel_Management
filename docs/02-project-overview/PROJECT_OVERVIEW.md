# 🏨 Hotel Reservation System - Complete Project Overview

**Last Updated:** [Current Session]  
**Project Type:** Desktop JavaFX Application  
**Architecture:** 3-Tier (Presentation → Business → Data)

---

## 📋 **WHAT IS THIS PROJECT?**

This is a **Hotel Reservation Management System** - a desktop application built with JavaFX that allows:

1. **Guests** to make self-service bookings at a kiosk
2. **Administrators** to manage reservations, process payments, handle checkouts, and generate reports
3. **System** to manage loyalty programs, waitlists, feedback, and billing

### **Key Features:**
- ✅ Self-service kiosk booking flow
- ✅ Admin dashboard for reservation management
- ✅ Payment processing (Cash, Card, Loyalty Points)
- ✅ Dynamic pricing (weekend/weekday/seasonal)
- ✅ Loyalty program with points earning/redeeming
- ✅ Waitlist management with notifications
- ✅ Feedback collection system
- ✅ Comprehensive reporting and exports
- ✅ Role-based access control (Admin/Manager)

---

## 🏗️ **ARCHITECTURE OVERVIEW**

### **3-Tier Architecture:**

```
┌─────────────────────────────────────┐
│   PRESENTATION LAYER (JavaFX)      │
│   - Controllers                     │
│   - FXML Views                      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│   BUSINESS/APPLICATION LAYER        │
│   - Services (Business Logic)       │
│   - Configuration (Policies)        │
│   - Design Patterns                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│   DATA LAYER                        │
│   - Repositories (Data Access)      │
│   - Entities (JPA Models)          │
│   - Database (MySQL)                │
└─────────────────────────────────────┘
```

### **MVC Pattern (Presentation Layer):**
- **Model:** Entities in `com.hotel.model`
- **View:** FXML files in `src/main/resources/view`
- **Controller:** Controllers in `com.hotel.controller`

---

## 📁 **PROJECT STRUCTURE**

```
Project/
├── src/main/java/com/hotel/
│   ├── app/              # Application bootstrap & DI
│   ├── config/           # Business policies
│   ├── controller/       # JavaFX controllers (MVC)
│   ├── events/           # Observer pattern
│   ├── model/            # JPA entities & enums
│   ├── repository/       # Data access layer
│   ├── security/         # Password hashing
│   ├── service/          # Business logic layer
│   │   ├── decorator/    # Decorator pattern
│   │   └── strategy/     # Strategy pattern
│   └── util/             # Utilities
├── src/main/resources/
│   ├── META-INF/         # JPA configuration
│   ├── styles/           # CSS files
│   └── view/             # FXML files
├── database/             # SQL scripts
└── docs/                 # Documentation
```

---

## 🔗 **HOW FILES CONNECT - DETAILED BREAKDOWN**

### **1. APPLICATION BOOTSTRAP (`app/`)**

#### **`AppConfig.java`** - Dependency Injection Container
**Purpose:** Central configuration that wires all dependencies together

**What it does:**
- Creates `EntityManagerFactory` (Singleton)
- Initializes business policies (Pricing, Discount, Loyalty)
- Creates repositories with EntityManager
- Creates services with dependencies
- Manages dependency injection

**Connections:**
- **Uses:** `EntityManagerFactory`, `PricingPolicy`, `DiscountPolicy`, `LoyaltyPolicy`
- **Creates:** All repositories, all services
- **Used by:** All controllers (via factory methods)

**Why this design:**
- Centralized dependency management
- Constructor injection (no field injection)
- Single source of truth for configuration
- Makes testing easier (can mock dependencies)

**How it works:**
```java
// Controllers call:
ReservationService service = AppConfig.createReservationService();

// AppConfig creates:
EntityManager em = createEntityManager();
GuestRepository guestRepo = new GuestRepository(em);
RoomRepository roomRepo = new RoomRepository(em);
// ... creates all dependencies
return new ReservationService(em, guestRepo, roomRepo, ...);
```

---

#### **`Main.java`** - Application Entry Point
**Purpose:** Starts the JavaFX application

**What it does:**
- Initializes `AppConfig` on startup
- Loads the initial FXML (Login or Welcome screen)
- Sets up the JavaFX stage

**Connections:**
- **Calls:** `AppConfig.initialize()`
- **Loads:** FXML files from `resources/view`
- **Uses:** Controllers specified in FXML

---

### **2. MODEL LAYER (`model/`)**

#### **Entities (13 files)**
**Purpose:** Represent database tables and business objects

**Key Entities:**
- `Guest.java` - Guest information
- `Room.java` - Room details
- `Reservation.java` - Booking information
- `Billing.java` - Billing details
- `Payment.java` - Payment transactions
- `AdminUser.java` - Admin accounts
- `Feedback.java` - Guest feedback
- `Waitlist.java` - Waitlist entries
- `ServiceAddon.java` - Add-on services
- `Hotel.java` - Hotel information
- `AuditLog.java` - System logs
- `ReservationRoom.java` - Join table (Reservation ↔ Room)
- `ReservationAddon.java` - Join table (Reservation ↔ Addon)

**Connections:**
- **Annotated with:** JPA annotations (`@Entity`, `@Table`, `@Id`, `@ManyToOne`, `@OneToMany`)
- **Used by:** Repositories (to query), Services (business logic), Controllers (display)
- **Relationships:** Entities reference each other (e.g., `Reservation` has `@ManyToOne Guest`)

**Why JPA:**
- Object-relational mapping (no raw SQL)
- Automatic relationship management
- Lazy/eager loading control
- Transaction management

**Example Connection:**
```java
// Reservation.java
@ManyToOne
private Guest guest;  // Links to Guest entity

// Guest.java
@OneToMany(mappedBy = "guest")
private List<Reservation> reservations;  // Reverse relationship
```

---

#### **Enums (6 files)**
**Purpose:** Type-safe constants

**Enums:**
- `RoomType.java` - SINGLE, DOUBLE, DELUXE, PENTHOUSE
- `ReservationStatus.java` - PENDING, CONFIRMED, CANCELLED, CHECKED_OUT
- `RoomStatus.java` - AVAILABLE, OCCUPIED, MAINTENANCE
- `Role.java` - ADMIN, MANAGER
- `PaymentMethod.java` - CASH, CARD, POINTS
- `PricingModel.java` - PER_NIGHT, PER_RESERVATION

**Connections:**
- **Used by:** Entities (as fields), Services (business logic), Controllers (UI dropdowns)

---

### **3. REPOSITORY LAYER (`repository/`)**

#### **Repositories (10 files)**
**Purpose:** Data access layer - abstracts database operations

**Key Repositories:**
- `GuestRepository.java` - CRUD + search by email/phone/name
- `RoomRepository.java` - CRUD + availability queries
- `ReservationRepository.java` - CRUD + search by guest/date/status
- `BillingRepository.java` - CRUD + find by reservation
- `PaymentRepository.java` - CRUD operations
- `AdminUserRepository.java` - CRUD + findByUsername
- `AddonRepository.java` - CRUD for service addons
- `FeedbackRepository.java` - CRUD + filtering
- `WaitlistRepository.java` - CRUD + findByRoomType
- `AuditLogRepository.java` - CRUD for logs

**Connections:**
- **Uses:** `EntityManager` (injected via constructor)
- **Uses:** Entity classes (to query)
- **Used by:** Services (business logic layer)
- **Implements:** JPA repository pattern

**Why this design:**
- Separation of concerns (data access separate from business logic)
- Easy to test (mock repositories)
- Reusable queries
- Type-safe operations

**Example Connection:**
```java
// ReservationService.java
private ReservationRepository reservationRepository;

public ReservationService(EntityManager em) {
    this.reservationRepository = new ReservationRepository(em);
}

// Service uses repository:
Reservation reservation = reservationRepository.findById(id);
```

---

### **4. SERVICE LAYER (`service/`)**

#### **Services (8 files)**
**Purpose:** Business logic layer - implements all business rules

**Key Services:**

##### **`ReservationService.java`**
**What it does:**
- Creates reservations
- Checks room availability
- Suggests rooms for group bookings
- Validates dates and occupancy
- Cancels/checks out reservations
- Publishes room availability events (Observer pattern)

**Connections:**
- **Uses:** `GuestRepository`, `RoomRepository`, `ReservationRepository`, `AddonRepository`
- **Uses:** `RoomAvailabilityPublisher` (Observer pattern)
- **Used by:** `KioskController`, `AdminController`
- **Uses:** `LoggerService` (for logging)

**Why this design:**
- All reservation business logic in one place
- Reusable by multiple controllers
- Easy to test business rules
- Observer pattern for notifications

---

##### **`BillingService.java`**
**What it does:**
- Creates billing for reservations
- Processes payments (Cash, Card, Points)
- Applies discounts (with role validation)
- Calculates totals using Strategy pattern
- Earns loyalty points on payment

**Connections:**
- **Uses:** `BillingRepository`, `PaymentRepository`, `ReservationRepository`
- **Uses:** `DiscountPolicy` (business rules)
- **Uses:** `LoyaltyService` (to earn points)
- **Uses:** Strategy pattern (`BillingStrategy`, `StandardBillingStrategy`, `DiscountBillingStrategy`, `LoyaltyBillingStrategy`)
- **Used by:** `AdminController`, `KioskController`

**Why Strategy Pattern:**
- Different billing calculations (standard, discount, loyalty)
- Easy to add new billing strategies
- Open/Closed Principle (open for extension, closed for modification)

---

##### **`PricingService.java`**
**What it does:**
- Calculates dynamic pricing
- Applies weekend/weekday multipliers
- Applies seasonal multipliers
- Uses `PricingPolicy` for rules

**Connections:**
- **Uses:** `PricingPolicy` (from `config/`)
- **Uses:** `RoomRepository` (to get base prices)
- **Used by:** `KioskController` (for booking summary)

---

##### **`LoyaltyService.java`**
**What it does:**
- Enrolls guests in loyalty program
- Earns points on payments
- Redeems points for discounts
- Manages loyalty numbers

**Connections:**
- **Uses:** `GuestRepository`, `BillingRepository`, `PaymentRepository`
- **Uses:** `LoyaltyPolicy` (business rules)
- **Used by:** `BillingService` (to earn points), `LoyaltyController`, `AdminController`

---

##### **`WaitlistService.java`**
**What it does:**
- Adds guests to waitlist
- Manages waitlist entries
- Subscribes to room availability notifications (Observer pattern)

**Connections:**
- **Uses:** `WaitlistRepository`
- **Uses:** `RoomAvailabilityPublisher` (Observer pattern - subscribes)
- **Uses:** `WaitlistSubscriber` (Observer pattern - receives notifications)
- **Used by:** `AdminController`

**Why Observer Pattern:**
- When room becomes available, waitlist is automatically notified
- Decouples room availability from waitlist management
- Easy to add more observers (e.g., email notifications)

---

##### **`FeedbackService.java`**
**What it does:**
- Submits guest feedback
- Validates feedback eligibility (checked out + balance = 0)
- Retrieves feedback for reports

**Connections:**
- **Uses:** `FeedbackRepository`, `ReservationRepository`, `BillingRepository`
- **Used by:** `FeedbackController`, `AdminController`, `ReportingService`

---

##### **`ReportingService.java`**
**What it does:**
- Generates revenue reports
- Generates occupancy reports
- Retrieves activity logs
- Generates feedback summaries

**Connections:**
- **Uses:** `ReservationRepository`, `RoomRepository`, `AuditLogRepository`, `FeedbackRepository`
- **Used by:** `ReportController`

---

##### **`ActivityLogService.java`**
**What it does:**
- Logs all system activities
- Creates audit trail
- Used by all services for logging

**Connections:**
- **Uses:** `AuditLogRepository`
- **Used by:** All services (via `LoggerService`)

---

##### **`AuthService.java`**
**What it does:**
- Authenticates admin users
- Validates passwords (BCrypt)
- Checks role permissions
- Validates discount permissions

**Connections:**
- **Uses:** `AdminUserRepository`
- **Uses:** `BCryptPasswordHasher` (from `security/`)
- **Used by:** `AdminController`

---

### **5. CONFIGURATION LAYER (`config/`)**

#### **Policy Classes (3 files)**
**Purpose:** Encapsulate business rules

##### **`PricingPolicy.java`**
**What it does:**
- Defines weekend/weekday multipliers
- Defines seasonal multipliers
- Calculates prices for date ranges

**Connections:**
- **Used by:** `PricingService`
- **Created by:** `AppConfig`

**Why separate policy:**
- Business rules can change without changing service code
- Easy to configure different pricing strategies
- Single Responsibility Principle

---

##### **`DiscountPolicy.java`**
**What it does:**
- Defines role-based discount caps (Admin 15%, Manager 30%)
- Validates discount amounts

**Connections:**
- **Used by:** `BillingService`
- **Created by:** `AppConfig`

---

##### **`LoyaltyPolicy.java`**
**What it does:**
- Defines points earning rate (1 point per $10)
- Defines redemption caps (max 1000 points per redemption)

**Connections:**
- **Used by:** `LoyaltyService`
- **Created by:** `AppConfig`

---

### **6. DESIGN PATTERNS**

#### **Strategy Pattern (`service/strategy/`)**
**Files:**
- `BillingStrategy.java` (interface)
- `StandardBillingStrategy.java`
- `DiscountBillingStrategy.java`
- `LoyaltyBillingStrategy.java`

**Purpose:** Different billing calculation strategies

**Connections:**
- **Used by:** `BillingService`
- **Why:** Allows different billing calculations without changing service code

**How it works:**
```java
// BillingService selects strategy based on context
BillingStrategy strategy = new DiscountBillingStrategy(discountPolicy);
double total = strategy.calculateTotal(subtotal, tax);
```

---

#### **Observer Pattern (`events/`)**
**Files:**
- `Subject.java` (interface)
- `Observer.java` (interface)
- `RoomAvailabilityPublisher.java` (Subject)
- `WaitlistSubscriber.java` (Observer)

**Purpose:** Notify waitlist when rooms become available

**Connections:**
- **Publisher:** `ReservationService` (publishes when room available)
- **Subscriber:** `WaitlistService` (subscribes to notifications)
- **Why:** Decouples room availability from waitlist management

**How it works:**
```java
// ReservationService (on checkout):
roomAvailabilityPublisher.publishRoomAvailable(room);

// WaitlistSubscriber (automatically notified):
public void update(String message) {
    notifications.add(message);  // Admin sees notification
}
```

---

#### **Factory Pattern (`util/`)**
**Files:**
- `RoomFactory.java`

**Purpose:** Creates Room instances with configured attributes

**Connections:**
- **Used by:** `SeedData` (for test data)
- **Why:** Centralized room creation logic

---

#### **Decorator Pattern (`service/decorator/`)**
**Files:**
- `BookingComponent.java` (abstract base)
- `AddOnDecorator.java` (abstract decorator)
- `RoomBookingComponent.java` (concrete component)
- `CombinedBookingComponent.java` (concrete component)

**Purpose:** Dynamically add services (Wi-Fi, breakfast) to booking pricing

**Connections:**
- **Used by:** `KioskController` (for booking summary)
- **Why:** Allows adding services without modifying base booking class

**How it works:**
```java
// Base booking
BookingComponent booking = new RoomBookingComponent(roomPrice);

// Add Wi-Fi
booking = new AddOnDecorator(booking, wifiAddon);

// Add breakfast
booking = new AddOnDecorator(booking, breakfastAddon);

// Total includes room + Wi-Fi + breakfast
double total = booking.getPrice();
```

---

#### **Singleton Pattern**
**Files:**
- `LoggerService.java` (in `util/`)
- `EntityManagerFactory` (in `AppConfig`)

**Purpose:** Single instance shared across application

**Connections:**
- **LoggerService:** Used by all services/controllers
- **EntityManagerFactory:** Used by `AppConfig` to create EntityManagers

---

### **7. CONTROLLER LAYER (`controller/`)**

#### **Controllers (5 files)**
**Purpose:** Handle UI interactions, coordinate with services

##### **`AdminController.java`**
**What it does:**
- Handles admin login
- Manages reservations (search, filter, view, edit, cancel)
- Processes payments
- Handles checkouts
- Applies discounts
- Manages waitlist
- Manages feedback
- Navigates to reports

**Connections:**
- **Uses:** `AuthService`, `ReservationService`, `BillingService`, `WaitlistService`, `FeedbackService`
- **Uses:** FXML files: `LoginScreen.fxml`, `Dashboard.fxml`, `PaymentProcessing.fxml`, etc.
- **Uses:** `AppConfig` (to create services)

**Data Flow:**
```
User clicks button → FXML calls controller method → 
Controller calls service → Service uses repository → 
Repository queries database → Results flow back → 
Controller updates UI
```

---

##### **`KioskController.java`**
**What it does:**
- Handles self-service booking flow
- Validates guest details
- Validates dates
- Suggests/selects rooms
- Selects add-ons
- Creates reservations
- Creates billing
- Shows confirmation

**Connections:**
- **Uses:** `ReservationService`, `PricingService`, `BillingService`
- **Uses:** FXML files: `WelcomeScreen.fxml`, `DateSelection.fxml`, `GuestDetails.fxml`, `RoomSelection.fxml`, `AddOnServices.fxml`, `BookingSummary.fxml`, `ConfirmationScreen.fxml`
- **Uses:** Decorator pattern (for add-on pricing)

**Flow:**
```
Welcome → Date Selection → Guest Details → Room Selection → 
Add-Ons → Booking Summary → Confirmation
```

---

##### **`FeedbackController.java`**
**What it does:**
- Handles feedback submission
- Validates eligibility
- Submits feedback

**Connections:**
- **Uses:** `FeedbackService`
- **Uses:** FXML files: `FeedbackSubmission.fxml`, `FeedbackConfirmation.fxml`

---

##### **`ReportController.java`**
**What it does:**
- Generates reports (Revenue, Occupancy, Activity Logs, Feedback)
- Exports to CSV, PDF, TXT
- Displays reports in tables

**Connections:**
- **Uses:** `ReportingService`
- **Uses:** Export utilities (`CsvExporter`, `PdfExporter`, `TxtExporter`)
- **Uses:** FXML: `ReportsScreen.fxml`

---

##### **`LoyaltyController.java`**
**What it does:**
- Searches guests
- Enrolls guests in loyalty program
- Displays loyalty dashboard
- Shows earning/redemption history

**Connections:**
- **Uses:** `LoyaltyService`, `GuestRepository`
- **Uses:** FXML: `LoyaltyProgram.fxml`

---

### **8. UTILITY LAYER (`util/`)**

#### **Utilities (6 files)**

##### **`LoggerService.java`** (Singleton)
**What it does:**
- Logs all activities to file
- Rotating file handler (1MB, 10 files)
- Used by all services/controllers

**Connections:**
- **Used by:** All services, all controllers
- **Uses:** `ActivityLogService` (to persist logs)

---

##### **`RoomFactory.java`** (Factory Pattern)
**What it does:**
- Creates Room instances with default values

**Connections:**
- **Used by:** `SeedData` (for test data)

---

##### **`CsvExporter.java`**
**What it does:**
- Exports data to CSV format

**Connections:**
- **Used by:** `ReportController`

---

##### **`PdfExporter.java`**
**What it does:**
- Exports data to PDF format

**Connections:**
- **Used by:** `ReportController`

---

##### **`TxtExporter.java`**
**What it does:**
- Exports data to TXT format

**Connections:**
- **Used by:** `ReportController`

---

##### **`Validator.java`**
**What it does:**
- Validates email, phone, names, numbers, ratings

**Connections:**
- **Used by:** Controllers (for input validation)

---

### **9. SECURITY LAYER (`security/`)**

#### **`BCryptPasswordHasher.java`**
**What it does:**
- Hashes passwords using BCrypt
- Verifies passwords

**Connections:**
- **Used by:** `AuthService`, `SeedData`

---

### **10. RESOURCES (`src/main/resources/`)**

#### **FXML Files (`view/`)**
**Purpose:** Define UI layout (JavaFX Scene Builder format)

**Structure:**
- `admin/` - Admin screens (Login, Dashboard, Payment, etc.)
- `kiosk/` - Kiosk screens (Welcome, Date Selection, etc.)
- `feedback/` - Feedback screens

**Connections:**
- **Linked to:** Controllers via `fx:controller` attribute
- **UI Components:** Linked to controller fields via `fx:id`
- **Actions:** Linked to controller methods via `onAction`

**Example:**
```xml
<!-- Dashboard.fxml -->
<Button fx:id="searchButton" onAction="#searchReservations" />
<!-- Links to AdminController.searchReservations() method -->
```

---

#### **CSS Files (`styles/`)**
**Purpose:** Styling for UI components

**Connections:**
- **Linked in:** FXML files via `stylesheets` attribute

---

#### **Persistence Configuration (`META-INF/persistence.xml`)**
**Purpose:** JPA configuration

**What it defines:**
- Database connection (MySQL)
- Entity classes
- Hibernate properties

**Connections:**
- **Used by:** `EntityManagerFactory` (created in `AppConfig`)

---

### **11. DATABASE (`database/`)**

#### **SQL Scripts**
- `create_schema.sql` - Creates all tables
- `seed_data.sql` - Populates initial test data

**Connections:**
- **Used by:** Database setup (run before application)
- **Maps to:** Entity classes (tables match entities)

---

## 🔄 **DATA FLOW EXAMPLES**

### **Example 1: Guest Makes a Booking (Kiosk)**

```
1. User fills form (KioskController)
   ↓
2. KioskController.validateGuestDetails()
   ↓
3. KioskController.selectRooms()
   ↓
4. KioskController calls ReservationService.getAvailableRooms()
   ↓
5. ReservationService calls RoomRepository.findAvailableByTypeAndDateRange()
   ↓
6. RoomRepository queries database (via EntityManager)
   ↓
7. Results flow back: Database → Repository → Service → Controller
   ↓
8. KioskController displays available rooms
   ↓
9. User selects rooms and add-ons
   ↓
10. KioskController calls PricingService (for pricing)
    ↓
11. KioskController uses Decorator pattern (for add-on pricing)
    ↓
12. KioskController calls ReservationService.createReservation()
    ↓
13. ReservationService:
    - Validates dates/occupancy
    - Creates Reservation entity
    - Creates ReservationRoom entities
    - Creates ReservationAddon entities
    - Saves via ReservationRepository
    ↓
14. KioskController calls BillingService.createBilling()
    ↓
15. BillingService creates Billing entity and saves
    ↓
16. KioskController shows confirmation screen
```

---

### **Example 2: Admin Processes Payment**

```
1. Admin clicks "Process Payment" (AdminController)
   ↓
2. AdminController.processPayment()
   ↓
3. AdminController calls BillingService.processPayment()
   ↓
4. BillingService:
    - Creates Payment entity
    - Updates Billing (paidAmount, balance)
    - Uses Strategy pattern (for calculation)
    - Calls LoyaltyService.earnPoints() (if not POINTS payment)
    ↓
5. LoyaltyService:
    - Updates Guest (loyaltyPoints)
    - Saves via GuestRepository
    ↓
6. BillingService saves Payment and Billing
   ↓
7. Results flow back to AdminController
   ↓
8. AdminController updates UI (shows new balance)
```

---

### **Example 3: Room Becomes Available (Observer Pattern)**

```
1. Admin checks out guest (AdminController)
   ↓
2. AdminController calls ReservationService.checkoutReservation()
   ↓
3. ReservationService:
    - Sets reservation status to CHECKED_OUT
    - Sets rooms status to AVAILABLE
    - Calls roomAvailabilityPublisher.publishRoomAvailable(room)
    ↓
4. RoomAvailabilityPublisher notifies all observers
    ↓
5. WaitlistSubscriber receives notification
    ↓
6. WaitlistSubscriber adds notification to list
    ↓
7. AdminController displays notification in UI
    ↓
8. Admin can convert waitlist entry to reservation
```

---

## 🎯 **KEY DESIGN DECISIONS**

### **Why 3-Tier Architecture?**
- **Separation of Concerns:** Each layer has a single responsibility
- **Maintainability:** Changes in one layer don't affect others
- **Testability:** Can test each layer independently
- **Scalability:** Easy to add new features

### **Why Dependency Injection?**
- **Loose Coupling:** Controllers don't create services directly
- **Testability:** Can inject mocks for testing
- **Flexibility:** Easy to swap implementations
- **Single Responsibility:** AppConfig manages all dependencies

### **Why Repository Pattern?**
- **Abstraction:** Services don't know about database details
- **Testability:** Can mock repositories
- **Reusability:** Same queries used by multiple services

### **Why Service Layer?**
- **Business Logic:** All rules in one place
- **Reusability:** Same logic used by multiple controllers
- **Transaction Management:** Services manage database transactions

### **Why Design Patterns?**
- **Strategy:** Flexible billing calculations
- **Observer:** Decoupled notifications
- **Factory:** Centralized object creation
- **Decorator:** Dynamic service addition
- **Singleton:** Shared resources (logger, EntityManagerFactory)

---

## 📊 **DEPENDENCY GRAPH**

```
Controllers
    ↓ (uses)
Services
    ↓ (uses)
Repositories
    ↓ (uses)
Entities
    ↓ (maps to)
Database Tables

AppConfig
    ↓ (creates)
All Services & Repositories

Policies (config/)
    ↓ (used by)
Services

Design Patterns
    ↓ (used by)
Services & Controllers
```

---

## 🔍 **HOW TO TRACE A FEATURE**

### **Example: "How does room availability checking work?"**

1. **Start at Controller:** `KioskController.getAvailableRooms()`
2. **Go to Service:** `ReservationService.getAvailableRooms()`
3. **Go to Repository:** `RoomRepository.findAvailableByTypeAndDateRange()`
4. **See Query:** JPA query in repository method
5. **Check Entity:** `Room.java` (see fields and relationships)
6. **Check Database:** `create_schema.sql` (see table structure)

---

## 📝 **SUMMARY**

This project follows **clean architecture principles**:

1. **Layered Structure:** Clear separation between presentation, business, and data
2. **Dependency Injection:** Centralized configuration in `AppConfig`
3. **Design Patterns:** Strategy, Observer, Factory, Decorator, Singleton
4. **Repository Pattern:** Abstracted data access
5. **Service Layer:** Business logic encapsulation
6. **MVC Pattern:** Separation of UI, logic, and data

**Every file has a purpose, every connection has a reason, and the architecture ensures maintainability, testability, and scalability.**

---

**For specific implementation details, see:**
- `PROJECT_INSTRUCTIONS.md` - Requirements
- `IMPLEMENTATION_ROADMAP.md` - Implementation strategy
- `PROJECT_STATUS.md` - Current status
- `ISSUES_AND_REQUIREMENTS.md` - Known issues

