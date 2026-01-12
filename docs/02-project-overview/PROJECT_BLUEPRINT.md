# Project Blueprint & Documentation

## Project Overview
**Hotel Management System** - A Java-based application with multi-layered architecture using JavaFX for UI, JPA for persistence, and BCrypt for security.

---

## Architecture Diagram

### System Architecture (4-Layer Structure)

#### 🏛️ **1. User Desktop Layer (Top Layer)**
Front-end applications built with **JavaFX**:

- **Kiosk UI (JavaFX)**
  - Purpose: Guest self-service operations (check-in, check-out, feedback)
  - Connects to: Controllers in Application Layer

- **Admin UI (JavaFX)**
  - Purpose: Hotel staff/manager operations (rooms, bookings, employees)
  - Connects to: Controllers in Application Layer

- **Feedback UI (JavaFX)**
  - Purpose: Collect customer comments/ratings
  - Connects to: Controllers in Application Layer

#### ⚙️ **2. Application Layer (Main Logic Layer)**
Core business logic and orchestration:

- **Controllers**
  - Purpose: Receive requests from UIs, delegate to Services
  - Connects to: All UI components (Kiosk, Admin, Feedback)
  - Connects to: Services

- **Services**
  - Purpose: Contains business logic, coordinates operations
  - Connects to: Controllers (receives requests)
  - Connects to: Repositories (data operations)
  - Connects to: LoggerService (logging)
  - Connects to: Auth & Security (authentication)
  - Connects to: File System (exports)

- **LoggerService (Singleton)**
  - Pattern: Singleton (single instance app-wide)
  - Purpose: Application-wide logging
  - Connects to: Services (receives log requests)
  - Connects to: Log Files in File System (writes logs)

- **Auth & Security (BCrypt)**
  - Technology: BCrypt password hashing
  - Purpose: Password hashing, authentication, validation
  - Connects to: Services (authentication requests)

- **Repositories (JPA)**
  - Technology: Java Persistence API (JPA/Hibernate)
  - Purpose: Data access operations
  - Connects to: Services (data requests)
  - Connects to: EntityManager in Persistence Layer

#### 🗃️ **3. Persistence Layer (Database Layer)**
Data storage and management:

- **EntityManager (per transaction)**
  - Pattern: Created per transaction
  - Purpose: Query, save, update, delete entities within a transaction
  - Connects to: Repositories (receives data operations)
  - Connects to: EntityManagerFactory (gets instances from factory)

- **EntityManagerFactory (Singleton)**
  - Pattern: Singleton (single instance app-wide)
  - Purpose: Creates EntityManager instances
  - Connects to: EntityManager (provides instances)
  - Connects to: HotelDB (manages database connection)

- **HotelDB (SQL)**
  - Technology: SQL database
  - Purpose: Stores all application data
  - Connects to: EntityManagerFactory (database connection)

#### 📁 **4. File System Layer**
File storage and retrieval:

- **Log Files (rotating)**
  - Purpose: Stores system logs
  - Pattern: Rotating logs (archived/deleted to prevent disk overflow)
  - Connects to: LoggerService (receives log data)

- **Export Files (CSV/PDF/TXT)**
  - Purpose: Exported reports, receipts, invoices
  - Formats: CSV, PDF, TXT
  - Connects to: Services (receives export requests)

---

### Data Flow & Connections

```
User Desktop Layer
    ↓ (requests)
Application Layer
    ├── Controllers → Services
    │                    ├── → Repositories → Persistence Layer
    │                    ├── → LoggerService → Log Files
    │                    ├── → Auth & Security (BCrypt)
    │                    └── → Export Files
    │
Persistence Layer
    Repositories → EntityManager → EntityManagerFactory → HotelDB
```

### Key Design Patterns & Technologies

- **MVC Pattern**: UI (View) → Controllers → Services (Model)
- **Repository Pattern**: Data access abstraction
- **Singleton Pattern**: LoggerService, EntityManagerFactory
- **Technologies**:
  - JavaFX (UI)
  - JPA/Hibernate (Persistence)
  - BCrypt (Security)
  - SQL (Database)
  - CSV/PDF/TXT (File exports)

### System Characteristics

✅ Clean separation of layers  
✅ MVC + Service + Repository patterns  
✅ Strong decoupling between UI and backend logic  
✅ Proper Singleton usage (Logger, EMF)  
✅ Transaction-based data access (EntityManager per transaction)

---

## UML Class Diagram - Package Structure

### Complete Package Architecture (`com.hotel`)

```
com.hotel
 ├── app
 ├── config
 ├── controller
 ├── view
 ├── model
 ├── repository
 ├── service
 ├── security
 ├── util
 └── events
```

---

### 📦 **1. app Package (Entry + Configuration Bootstrapping)**

**Classes:**
- `Main` - Application entry point
- `AppConfig` - Global configuration initialization

**Dependencies:**
- `Main` → `AppConfig`
- `Main` → `config.PricingPolicy`
- `Main` → `config.LoyaltyPolicy`
- `Main` → `controller.KioskController`

**Purpose:** Application entry point and initialization of policies, repositories, services

---

### ⚙️ **2. config Package (Business Logic Policies)**

**Classes:**
- `DiscountPolicy` - Implements discount rules (e.g., long stays)
- `PricingPolicy` - Implements pricing rules (seasonal/dynamic pricing)
- `LoyaltyPolicy` - Implements loyalty reward rules

**Dependencies:**
- Used by: `service.PricingService`, `service.ReservationService`, `service.BillingService`

**Purpose:** Business rules for discounts, pricing, and loyalty programs

---

### 🖥️ **3. controller Package (MVC Controllers)**

**Classes:**
- `KioskController` - Handles guest self-service UI events
- `AdminController` - Handles admin/staff UI events
- `FeedbackController` - Handles feedback UI events
- `ReportController` - Handles reporting UI events
- `LoyaltyController` - Handles loyalty program UI events

**Dependencies:**
- All controllers → `view.FXMLFiles`
- `KioskController` → `service.ReservationService`, `service.FeedbackService`
- `AdminController` → `service.BillingService`, `service.ReservationService`, `service.PricingService`, `service.LoyaltyService`, `service.FeedbackService`, `service.ReportingService`, `service.AuthService`, `service.WaitlistService`, `service.ActivityLogService`
- `FeedbackController` → `service.FeedbackService`
- `ReportController` → `service.ReportingService`
- `LoyaltyController` → `service.LoyaltyService`

**Purpose:** Handle UI events, call services, update views, mediate between JavaFX and Service layer

---

### 🎨 **4. view Package (UI Resources)**

**Classes:**
- `FXMLFiles` - Paths to all FXML screens
- `CSSStyles` - CSS style definitions

**Dependencies:**
- `FXMLFiles` → `CSSStyles`

**Purpose:** Stores UI files, ensures consistent styling, manages FXML paths

---

### 🗃️ **5. model Package (Domain Classes / Entities)**

**Main Entities:**
- `Guest` - Guest information
- `Room` - Room details
- `Reservation` - Booking information
- `Billing` - Billing records
- `Payment` - Payment transactions
- `Feedback` - Customer feedback
- `AdminUser` - Admin/staff user accounts
- `Waitlist` - Waitlist entries
- `AuditLog` - System audit logs

**Enums / Models:**
- `RoomType` - Room type enumeration
- `ReservationStatus` - Reservation status enumeration
- `RoomStatus` - Room status enumeration
- `Role` - User role enumeration
- `PaymentMethod` - Payment method enumeration
- `PricingModel` - Pricing model enumeration

**Dependencies:**
- Used by: All repositories, services

**Purpose:** Pure POJOs representing hotel data (entities and enums)

---

### 💾 **6. repository Package (Data Access Layer)**

**Repositories:**
- `GuestRepository` - CRUD for Guest entities
- `RoomRepository` - CRUD for Room entities
- `ReservationRepository` - CRUD for Reservation entities
- `BillingRepository` - CRUD for Billing entities
- `FeedbackRepository` - CRUD for Feedback entities
- `AdminUserRepository` - CRUD for AdminUser entities
- `AddonRepository` - CRUD for Addon entities
- `PaymentRepository` - CRUD for Payment entities
- `WaitlistRepository` - CRUD for Waitlist entities
- `AuditLogRepository` - CRUD for AuditLog entities

**Dependencies:**
- All repositories → `model` package (entities)
- Used by: All services

**Purpose:** Handle CRUD operations in database using JPA

---

### 🔧 **7. service Package (Business Logic Layer)**

**Core Services:**
- `BillingService` - Billing operations, invoice calculations
- `ReservationService` - Booking operations, room reservations
- `PricingService` - Price calculations, applies pricing policies
- `LoyaltyService` - Loyalty program management
- `FeedbackService` - Feedback collection and management
- `ReportingService` - Report generation (CSV/PDF)
- `AuthService` - Authentication and authorization (wraps security.AuthService)
- `WaitlistService` - Waitlist management
- `ActivityLogService` - Activity logging

**Dependencies:**
- `BillingService` → `repository.BillingRepository`, `util.LoggerService`
- `ReservationService` → `repository.ReservationRepository`, `util.LoggerService`, `events.RoomAvailabilityPublisher`
- `PricingService` → `config.PricingPolicy`, `util.LoggerService`
- `LoyaltyService` → `config.LoyaltyPolicy`, `util.LoggerService`
- `FeedbackService` → `repository.FeedbackRepository`, `util.LoggerService`
- `ReportingService` → All repositories (`GuestRepository`, `RoomRepository`, `ReservationRepository`, `BillingRepository`, `FeedbackRepository`, `AdminUserRepository`, `AddonRepository`, `PaymentRepository`, `WaitlistRepository`, `AuditLogRepository`), `util.LoggerService`, `util.CsvExporter`, `util.PdfExporter`
- `AuthService` → `security.AuthService`, `security.BCryptPasswordHasher`, `util.LoggerService`
- `WaitlistService` → `repository.WaitlistRepository`, `util.LoggerService`, `events.WaitlistSubscriber`
- `ActivityLogService` → `repository.AuditLogRepository`, `util.LoggerService`

**Purpose:** Implements all business operations (booking, billing, discounts, auth, logging, reports)

---

### 🔐 **8. security Package (Authentication & Hashing)**

**Classes:**
- `AuthService` - Login validation, role-based access checks
- `BCryptPasswordHasher` - Password hashing using BCrypt

**Dependencies:**
- `AuthService` → `BCryptPasswordHasher`
- Used by: `service.AuthService`

**Purpose:** Handles login validation, password hashing, role-based access checks

---

### 🧰 **9. util Package (Helper Utilities)**

**Classes:**
- `LoggerService` - Logs activity to files (Singleton pattern)
- `CsvExporter` - Generates CSV reports
- `PdfExporter` - Generates PDF reports
- `Validator` - Validates input data

**Dependencies:**
- `LoggerService` → Used by all services
- `CsvExporter`, `PdfExporter` → Used by `service.ReportingService`
- `Validator` → Used by controllers/services

**Purpose:** Provides reusable helper utilities used by services

---

### 🔔 **10. events Package (Observer Pattern)**

**Classes:**
- `Subject` - Observer pattern subject interface
- `Observer` - Observer pattern observer interface
- `RoomAvailabilityPublisher` - Publishes room availability events
- `WaitlistSubscriber` - Subscribes to room availability events

**Dependencies:**
- `RoomAvailabilityPublisher` → `events.Subject`
- `WaitlistSubscriber` → `events.Observer`
- `RoomAvailabilityPublisher` → Used by `service.ReservationService`
- `WaitlistSubscriber` → Used by `service.WaitlistService`

**Purpose:** Implements Observer pattern for event-driven behavior (notifies waitlist when rooms become available)

---

### Class Connections & Dependencies Summary

**Key Dependency Chains:**

1. **UI Flow:**
   ```
   View (FXMLFiles) → Controller → Service → Repository → Model
   ```

2. **Service Dependencies:**
   ```
   Service → Repository (data)
   Service → LoggerService (logging)
   Service → Config Policies (business rules)
   Service → Security (authentication)
   Service → Util (exporters, validators)
   ```

3. **Event Flow:**
   ```
   ReservationService → RoomAvailabilityPublisher → WaitlistSubscriber → WaitlistService
   ```

4. **Security Flow:**
   ```
   Controller → service.AuthService → security.AuthService → BCryptPasswordHasher
   ```

---

## Sequence Diagrams

### Complete Multi-Scenario Sequence Diagram

This sequence diagram shows the **complete customer journey** through 5 key scenarios:

1. Group Booking at Kiosk
2. Deposit Payment at Admin
3. Partial Payment During Stay
4. Checkout with Discount + Loyalty Redemption
5. Feedback Submission

**Participants:**
- **Actors:** Guest, Admin
- **Controllers:** KioskController, AdminController, FeedbackController
- **Services:** ReservationService, PricingService, BillingService, LoyaltyService, FeedbackService
- **Data Store:** Database (ORM)

---

## 🧩 **Scenario 1: Group Booking at Kiosk**

**Flow:** Guest → KioskController → ReservationService → PricingService → Database → Confirmation

### Step-by-Step Sequence:

1. **Guest** → `KioskController`: "Start booking flow"
2. **KioskController** → `ReservationService`: "validate guest details and dates"
3. **ReservationService** → `PricingService`: "room suggestions with dynamic pricing"
4. **PricingService** → `Database (ORM)`: "query available rooms"
5. **Database (ORM)** → `PricingService`: "return available rooms"
6. **PricingService** → `ReservationService`: "return suggested plan"
7. **ReservationService** → `Database (ORM)`: "persist Reservation + ReservationRooms"
8. **Database (ORM)** → `ReservationService`: "confirmation"
9. **ReservationService** → `KioskController`: "booking confirmed"
10. **KioskController** → `Guest`: "show confirmation and deposit reminder"

### Key Operations:
- ✅ Guest validation
- ✅ Date validation
- ✅ Dynamic pricing calculation (uses PricingPolicy)
- ✅ Room availability check
- ✅ Reservation creation
- ✅ Room assignment (ReservationRooms)
- ✅ Database persistence

---

## 🏦 **Scenario 2: Deposit Payment at Admin**

**Flow:** Admin → AdminController → BillingService → Database → Confirmation

### Step-by-Step Sequence:

1. **Admin** → `AdminController`: "pay deposit"
2. **AdminController** → `BillingService`: "create initial bill"
3. **BillingService** → `Database (ORM)`: "persist Billing + Payment (deposit)"
4. **Database (ORM)** → `BillingService`: "confirmation"
5. **BillingService** → `AdminController`: "show updated balance"
6. **AdminController** → `Admin`: Display updated balance

### Key Operations:
- ✅ Initial bill creation
- ✅ Deposit payment recording
- ✅ Balance calculation
- ✅ Database persistence

---

## 💳 **Scenario 3: Partial Payment During Stay**

**Flow:** Admin → AdminController → BillingService → Database → Confirmation

### Step-by-Step Sequence:

1. **Admin** → `AdminController`: "make partial payment"
2. **AdminController** → `BillingService`: "record payment"
3. **BillingService** → `Database (ORM)`: "persist Payment"
4. **Database (ORM)** → `BillingService`: "confirmation"
5. **BillingService** → `AdminController`: "show reduced balance"
6. **AdminController** → `Admin`: Display new balance

### Key Operations:
- ✅ Payment recording
- ✅ Balance update
- ✅ Database persistence
- ✅ Balance display

---

## 🏁 **Scenario 4: Checkout with Discount + Loyalty Redemption**

**Flow:** Admin → AdminController → BillingService → LoyaltyService → ReservationService → Database → Final Receipt

### Step-by-Step Sequence:

1. **Admin** → `AdminController`: "initiate checkout"
2. **AdminController** → `BillingService`: "request final bill"
3. **BillingService** → `LoyaltyService`: "apply loyalty redemption"
4. **LoyaltyService** → `Database (ORM)`: "update Guest loyalty points"
5. **Database (ORM)** → `LoyaltyService`: "confirmation"
6. **LoyaltyService** → `BillingService`: "discount applied"
7. **BillingService** → `Database (ORM)`: "update Billing totals"
8. **Database (ORM)** → `BillingService`: "confirmation"
9. **BillingService** → `AdminController`: "final bill with discount + loyalty applied"
10. **AdminController** → `ReservationService`: "mark Reservation as CHECKED_OUT, free rooms"
11. **ReservationService** → `Database (ORM)`: "update Reservation status, free rooms"
12. **Database (ORM)** → `ReservationService`: "confirmation"
13. **ReservationService** → `AdminController`: "confirmation"
14. **AdminController** → `Admin`: "show final receipt"

### Key Operations:
- ✅ Final bill calculation
- ✅ Loyalty point redemption
- ✅ Discount application
- ✅ Guest loyalty points update
- ✅ Billing totals update
- ✅ Reservation status change (CHECKED_OUT)
- ✅ Room freeing (status change to AVAILABLE)
- ✅ Final receipt generation

---

## ⭐ **Scenario 5: Feedback Submission**

**Flow:** Guest → FeedbackController → FeedbackService → Database → Confirmation

### Step-by-Step Sequence:

1. **Guest** → `FeedbackController`: "submit rating + comments"
2. **FeedbackController** → `FeedbackService`: "validate and save feedback"
3. **FeedbackService** → `Database (ORM)`: "persist Feedback"
4. **Database (ORM)** → `FeedbackService`: "confirmation"
5. **FeedbackService** → `FeedbackController`: "success"
6. **FeedbackController** → `Guest`: "thank you message"

### Key Operations:
- ✅ Feedback validation
- ✅ Sentiment tagging (if implemented)
- ✅ Feedback persistence
- ✅ Confirmation display

---

## 🎯 **Overall System Behavior Summary**

### Complete Hotel Lifecycle Flow:

| Phase | What Happens | Services Involved |
|-------|-------------|-------------------|
| **Booking** | Availability check, dynamic pricing, reservation creation | ReservationService, PricingService |
| **Deposit** | Billing creation + initial payment | BillingService |
| **During Stay** | Additional partial payments | BillingService |
| **Checkout** | Discounts + loyalty redemption + final bill + room freeing | BillingService, LoyaltyService, ReservationService |
| **After Stay** | Feedback collection | FeedbackService |

### Key Characteristics:

✅ **Controller → Service → Repository** communication pattern  
✅ **Multi-service coordination** (Pricing, Billing, Loyalty, Reservation)  
✅ **Database persistence** at each step  
✅ **Synchronous system behavior**  
✅ **Realistic operational flow** for a hotel system  

### Interaction Patterns:

1. **User Action** → Controller receives request
2. **Controller** → Service processes business logic
3. **Service** → Repository/Other Service (if needed)
4. **Repository** → Database persistence
5. **Database** → Confirmation back through chain
6. **Controller** → User receives response

---

## 🎫 **Waitlist Management Sequence Diagram**

### Complete Waitlist Lifecycle Flow

This sequence diagram shows the **Observer Pattern implementation** for waitlist management, demonstrating how the system handles guest waitlists when rooms are unavailable and automatically notifies when rooms become available.

**Participants:**
- **Actors:** Guest, Admin
- **Controllers:** AdminController
- **Services:** WaitlistService
- **Event System:** RoomAvailabilityPublisher (Subject), WaitlistSubscriber (Observer)
- **Data Store:** Database (ORM)

**Three Main Scenarios:**
1. Guest Added to Waitlist
2. Room Becomes Available (Observer Pattern)
3. Waitlist Conversion to Reservation

---

## 🧩 **Scenario 1: Guest Added to Waitlist**

**Flow:** Guest → AdminController → WaitlistService → Database → Confirmation

### Step-by-Step Sequence:

1. **Guest** → `AdminController`: "Request booking (rooms unavailable)"
2. **AdminController** → `WaitlistService`: "Add guest to waitlist"
3. **WaitlistService** → `Database (ORM)`: "Persist Waitlist entry"
4. **Database (ORM)** → `WaitlistService`: "Confirmation"
5. **WaitlistService** → `AdminController`: "Guest added to waitlist"
6. **AdminController** → `Guest`: "Inform guest they are on waitlist"

### Key Operations:
- ✅ Waitlist entry creation
- ✅ Guest information storage
- ✅ Room type and date range stored
- ✅ Database persistence
- ✅ User feedback

---

## 🛏️ **Scenario 2: Room Becomes Available (Observer Pattern)**

**Flow:** Admin → AdminController → RoomAvailabilityPublisher → WaitlistSubscriber → Admin UI Notification

### Step-by-Step Sequence:

1. **Admin** → `AdminController`: "Checkout guest and free room"
2. **AdminController** → `RoomAvailabilityPublisher`: "Publish RoomAvailable event"
3. **RoomAvailabilityPublisher** → `WaitlistSubscriber`: "Notify subscribed admins"
4. **WaitlistSubscriber** → `AdminController`: "Display notification of availability"
5. **AdminController** → `Admin`: Show notification "A room has become available"

### Key Operations:
- ✅ Event-driven architecture (Observer Pattern)
- ✅ Room status change triggers event
- ✅ Publisher → Subscriber notification
- ✅ Reactive waitlist notification
- ✅ No polling or manual checking required

### Observer Pattern Implementation:
- **Subject:** `RoomAvailabilityPublisher` (implements `Subject` interface)
- **Observer:** `WaitlistSubscriber` (implements `Observer` interface)
- **Event:** Room becomes available
- **Action:** Notify all subscribed observers (admins)

---

## 🔄 **Scenario 3: Waitlist Conversion to Reservation**

**Flow:** Admin → AdminController → WaitlistService → Database → Reservation Created

### Step-by-Step Sequence:

1. **Admin** → `AdminController`: "Open waitlist notification"
2. **AdminController** → `WaitlistService`: "Convert waitlist entry to reservation"
3. **WaitlistService** → `Database (ORM)`: "Create Reservation and remove Waitlist entry"
4. **Database (ORM)** → `WaitlistService`: "Confirmation"
5. **WaitlistService** → `AdminController`: "Reservation created"
6. **AdminController** → `Guest`: "Show new reservation details"

### Key Operations:
- ✅ Automatic room allocation for waitlisted guests
- ✅ Waitlist entry → Reservation conversion
- ✅ Waitlist entry removal
- ✅ Reservation creation
- ✅ Prevents overbooking
- ✅ Clean lifecycle management

---

## 🌟 **Waitlist System Overview**

### Complete Waitlist Lifecycle:

| Phase | What Happens | Components Involved |
|-------|-------------|-------------------|
| **No Rooms Available** | Guest added to waitlist | WaitlistService, Database |
| **Room Becomes Available** | Event published, admins notified | RoomAvailabilityPublisher, WaitlistSubscriber (Observer Pattern) |
| **Admin Converts** | Waitlist entry → Reservation | WaitlistService, ReservationService |

### Key Characteristics:

✅ **Observer Pattern** for event-driven notifications  
✅ **Automatic notification** when rooms become available  
✅ **Clean conversion** from waitlist to reservation  
✅ **Prevents overbooking** through proper lifecycle management  
✅ **Reactive system** - no manual polling required  

### Observer Pattern Flow:

```
Room Status Change (Checkout)
    ↓
RoomAvailabilityPublisher.publish()
    ↓
WaitlistSubscriber.notify()
    ↓
Admin UI Notification
    ↓
Admin Action
    ↓
WaitlistService.convertToReservation()
    ↓
Reservation Created
```

---

## Project Instructions

### Project: Hotel Reservation System

**Due Date:** December 3rd, 2025

---

## Introduction

Hotel (Choose an appropriate name) is one of the famous tourist hotels in (Choose an appropriate name) city. But this hotel's current reservation is based on a manual system.

When guests come in to make a reservation, their details are recorded in a file and then those files are stored in special cabinets. Also, the billing system is manual too.

**Problems with Current System:**
- Records can easily be destroyed in case of fire, disaster or even possible to be stolen
- Storing files requires extra cabinet space
- Searching for a record is difficult, resulting in significant manpower hours
- Billing system is manually handled so having an error in calculation is also at high risk
- No way to get customer feedback after the stay
- Since the pandemic hits, management is looking to implement 2 kiosks so guests can book their room with no contact or interaction with anyone

**Solution:** Develop a computer-based Reservation system so hotel can give quick service to the guests.

---

## Scope and Outcomes

### Goal
Build a **desktop-only reservation and billing system** that:
- Replaces manual processes
- Models real-world hotel operations
- Has clear, maintainable architecture

### Architecture Requirements
- **MVC** for presentation logic
- **Service layer** for business rules
- **Repositories** powered by an ORM for persistence
- **Dependency Injection (DI)** for wiring
- **Design Patterns** to apply:
  - Singleton
  - Strategy
  - Observer
  - Factory
  - Decorator

### Deliverables
1. Project documentation
2. Design artifacts
3. Working application
4. Export files (CSV, PDF, TXT)
5. Logs
6. ORM configuration
7. Database scripts
8. Reflection on challenges and learnings

### Constraints
- ❌ **No web components**
- ❌ **No charts** - All reports must be shown as tables
- ✅ Reports must be exportable to **CSV, PDF, or TXT**

---

## Architecture Tiers

This project must follow a **3-tier architecture**:

### 1. Presentation Tier
- **JavaFX UI** (kiosk, admin, feedback) with controllers and FXML views
- Collects input
- Validates at UI level
- Displays results in tables and forms

### 2. Application/Business Tier
- **Services** implementing business rules
- Applying design patterns
- Orchestrating workflows
- Enforces:
  - Occupancy rules
  - Pricing
  - Discounts
  - Loyalty
  - Waitlist notifications

### 3. Data Tier
- **ORM-backed repositories**
- **Relational database**
- Manages:
  - Persistence
  - Queries
  - Transactions

### Cross-Cutting Concerns
Applied consistently across all tiers:
- **Logging**
- **Security**
- **Configuration**

---

## Functional Requirements

### Kiosk (Self-Service)

#### Welcome Flow
- ✅ The kiosk must display a brief, friendly welcome message
- ✅ Optional short instructional video or GIF
- ✅ Rules and regulations button must always remain visible and accessible during the flow (like a navigation on the side)
- ✅ Interface must guide the user through a clear, step-by-step journey from arrival to confirmation

#### Booking Steps
- ✅ The kiosk must ask for the number of adults and children before continuing
- ✅ It must then ask for check-in and check-out dates and validate them immediately
- ✅ Based on occupancy rules and room availability, the kiosk must either:
  - Suggest a room plan and allow the user to adjust choices
  - OR allow the user to choose their own type of rooms and quantity
- ✅ If user chooses their own type of room and quantity, indicate the guest to check the rooms booking policy
- ✅ The kiosk must collect guest details with visible required-field indicators and inline validation messages for each incorrect field
- ✅ The kiosk must let the guest select add-on services such as:
  - Wi-Fi
  - Breakfast
  - Parking
  - Spa
- ✅ Must show the price impact for each add-on selection
- ✅ Before confirmation, the kiosk must present a complete estimate including:
  - Subtotal
  - Tax
  - Add-ons
  - Any loyalty effects
- ✅ After confirmation, the kiosk must save the reservation and clearly inform the guest that billing will be handled at the front desk

#### Kiosk Validation
- ✅ The kiosk must enforce occupancy limits per room type across all steps
- ✅ It must accept a single-person booking without errors
- ✅ It must reject any invalid combinations and must display clear, actionable error messages to the user

---

### Admin Module

#### Authentication
- ✅ The system must support multiple administrator accounts with role-based access:
  - **Admin** role
  - **Manager** role
- ✅ All passwords must be hashed with **BCrypt** before storage
- ✅ The login process must provide success and failure feedback
- ✅ Must log all authentication events

#### Dashboard
- ✅ Administrators must be able to search for guests and reservations by:
  - Name
  - Phone
  - Date range
  - Status
  - Other relevant filters
- ✅ The dashboard must show results in **paginated tables** with **sortable columns**
- ✅ Must allow opening detailed views for editing

#### Reservations Management
- ✅ Administrators must be able to:
  - Create reservations (via phone)
  - Modify reservations
  - Cancel reservations
- ✅ Must perform conflict checks against existing bookings
- ✅ The system must support **group bookings** where a single reservation can include multiple rooms
- ✅ Must maintain a **unified bill** for the group

#### Payments
- ✅ Administrators must be able to process payments using:
  - Cash
  - Card
  - Loyalty points
- ✅ The system must support:
  - **Deposits** at booking time
  - **Partial payments** during the stay
  - **Refunds** when required
- ✅ The system must track:
  - Paid amounts
  - Outstanding balances
- ✅ Must **prevent checkout** while a balance remains

#### Loyalty Program Enrollment
- ✅ The system must offer loyalty program to guests
- ✅ If guest wants to enroll:
  - Use the user information which is already filled
  - Confirm it with the guest
  - Issue a loyalty number

#### Discounts
- ✅ Administrators must be able to apply discounts with **role-based caps**:
  - **Admin** can apply up to **15%**
  - **Manager** can apply up to **30%**
- ✅ The system must prevent discounts that exceed configured limits
- ✅ Must record who applied each discount

#### Checkout
- ✅ Administrators must be able to:
  - Generate the final bill
  - Settle the balance
  - Mark the rooms as available
- ✅ The system must trigger **room availability notifications** after checkout
- ✅ Must remind the administrator to invite the guest to submit feedback at the kiosk

#### Waitlist
- ✅ When rooms are unavailable, administrators must be able to add guests to a waitlist with:
  - Desired room type
  - Date range
- ✅ The system must notify subscribed administrators when availability changes
- ✅ Must provide a quick conversion from a waitlist entry to a reservation

#### Feedback Management
- ✅ Administrators must be able to view feedback entries **only after the guest has checked out**
- ✅ The system must provide filters by:
  - Rating
  - Date
  - Sentiment tag
  - Guest
- ✅ Administrators must be able to export feedback summaries for analysis

#### Loyalty Dashboard
- ✅ Guests must earn loyalty points based on payment amounts, using a configurable earning rate
- ✅ Administrators must be able to redeem points for discounts under defined caps
- ✅ The system must provide a loyalty dashboard showing:
  - Balances
  - Earning history
  - Redemption activity

---

### Feedback Module (Guest)

#### Submission
- ✅ Guests must be able to submit:
  - Star rating from **one to five**
  - Comments
- ✅ Submission allowed **after checkout**
- ✅ The system must store the feedback linked to both:
  - The reservation
  - The guest
- ✅ Must show a confirmation after submission

---

### Reporting (Tabular Only)

#### Revenue Reports
- ✅ The system must provide revenue summaries by:
  - Day
  - Week
  - Month
- ✅ Each summary must include:
  - Reservation counts
  - Subtotal
  - Tax
  - Discounts
  - Total amounts
- ✅ Reports must be displayed in **tables**
- ✅ Must be exportable to **CSV or PDF**

#### Occupancy Reports
- ✅ The system must provide occupancy tables for:
  - Daily view
  - Weekly view
  - Monthly view
- ✅ Each table must include:
  - Rooms available
  - Rooms occupied
  - Occupancy percentage (as numeric value only)
- ✅ Reports must be exportable to **CSV or PDF**

#### Activity Logs
- ✅ The system must provide a table of administrative activity that includes:
  - Timestamp
  - Actor
  - Action
  - Entity type
  - Entity identifier
  - Message
- ✅ The system must support export to **CSV and TXT**

---

## Business Rules

### Occupancy Limits
- ✅ **Single room**: Must allow up to **2 people**
- ✅ **Double room**: Must allow up to **4 people**
- ✅ **Deluxe room**: Must allow up to **2 people** with higher base prices
- ✅ **Penthouse room**: Must allow up to **2 people** with higher base prices
- ✅ The system must validate occupancy both:
  - Per room
  - Across group bookings

### Group Booking Suggestions
- ✅ For groups of **3 or 4 adults**, the system must suggest:
  - One double room
  - OR two single rooms
  - OR let them choose their own type of rooms and quantity
- ✅ For groups **larger than 4 adults**, the system must suggest:
  - Multiple double rooms
  - OR a combination of double and single rooms
  - Until capacity is satisfied
- ✅ In the case of group booking choosing their own room types and quantity, system must validate the rules of occupancy

### Dynamic Pricing
- ✅ The system must apply a configurable multiplier for **weekends** and a separate multiplier for **weekdays**
- ✅ The system must apply **seasonal multipliers** for defined date ranges such as peak season
- ✅ The system must price add-ons either:
  - Per night
  - Per reservation
- ✅ Pricing based on their pricing model (`PricingModel` enum: PER_NIGHT, PER_RESERVATION)

### Payments Business Rules
- ✅ The system must support:
  - Cash
  - Card
  - Loyalty point payments
- ✅ The system must allow **deposits at booking** and must track **partial payments** during the stay
- ✅ The system must allow **refunds** as negative payment entries and must adjust totals and logs accordingly

### Discounts Business Rules
- ✅ The system must enforce **role-based discount caps** and must prevent exceeding the configured limits
- ✅ The system must cap **loyalty points redemption per reservation**
- ✅ Must apply discounts before loyalty redemption where required by the strategy

### Loyalty Business Rules
- ✅ The system must earn points per paid amount using a **configurable rate**
- ✅ The system must redeem points into discounts using the loyalty strategy and must respect configured redemption caps
- ✅ The system must maintain accurate point balances with **audit trails** for earning and redemption

### Feedback Eligibility
- ✅ The system must allow feedback submission **only after**:
  - The reservation has been checked out
  - Any balances have been fully settled

---

## Architecture and Patterns

### Layers and Packages
The project must organize code into the following packages:

- ✅ **app** - Bootstrap and Dependency Injection
- ✅ **config** - Pricing and policy configurations
- ✅ **controller** - JavaFX controllers
- ✅ **view** - FXML and CSS files
- ✅ **model** - Entities and enums
- ✅ **service** - Business logic
- ✅ **repository** - ORM-backed persistence
- ✅ **security** - Authentication and roles
- ✅ **util** - Logging and exporters
- ✅ **events** - Observer pattern components

### ORM and Lifecycle
- ✅ Entities must be annotated with **JPA**, including identifiers and relationships
- ✅ The **EntityManagerFactory** must be created once and treated as a **singleton** or DI-managed singleton
- ✅ The **EntityManager** must be created **per transaction** or unit of work
- ✅ EntityManager must **not be shared across threads**

### Repository Abstraction
- ✅ Repositories must expose **clear interfaces**
- ✅ Must use **JPA queries or criteria** for persistence operations
- ✅ Services must depend on repositories, **not on ORM APIs directly**

### Dependency Injection
- ✅ Controllers, services, and repositories must use **constructor injection**
- ✅ A central configuration class must wire dependencies and provide singletons where appropriate

### Required Patterns Implementation

#### Strategy Pattern
- ✅ Must be used for billing calculations including:
  - Standard billing strategy
  - Discount billing strategy
  - Loyalty billing strategy

#### Observer Pattern
- ✅ Must be used to notify administrators when room availability changes
- ✅ Implementation: `RoomAvailabilityPublisher` (Subject) → `WaitlistSubscriber` (Observer)

#### Factory Pattern
- ✅ Must be used to create room instances with configured attributes
- ✅ Implementation: `RoomFactory`

#### Decorator Pattern
- ✅ Must be used to add services such as:
  - Spa
  - Breakfast
  - Wi-Fi
  - Parking
- ✅ Implementation: `BookingComponent` (abstract) → `AddOnDecorator`

---

## Logging, Security, and Validation

### Activity Logging
The application must record administrator actions including:
- ✅ Logins
- ✅ Searches
- ✅ Reservation changes
- ✅ Checkouts
- ✅ Cancellations
- ✅ Discounts
- ✅ Payments
- ✅ Refunds
- ✅ Feedback submissions

#### Log Entry Requirements
Each log entry must include:
- ✅ **Timestamp**
- ✅ **Actor** (who performed the action)
- ✅ **Action** (what was done)
- ✅ **Entity type** (what entity was affected)
- ✅ **Entity identifier** (which specific entity)
- ✅ **Descriptive message**

### Logger Configuration
- ✅ The application must use **java.util.logging** or **Log4j** with a rotating file handler
- ✅ Each log file must be limited to approximately **1 megabyte**
- ✅ The system must retain up to **10 files** before rotating
- ✅ Logs must be stored in a **separate log file**
- ✅ Must implement log rotation to avoid excessive file size growth

#### Example Configuration (FileHandler)
```java
try {
    FileHandler fileHandler = new FileHandler("system_logs.%g.log", 1024 * 1024, 10, true);
    logger.addHandler(fileHandler);
    SimpleFormatter formatter = new SimpleFormatter();
    fileHandler.setFormatter(formatter);
} catch (IOException e) {
    logger.log(Level.SEVERE, "Failed to initialize logger", e);
}
```

**Configuration Details:**
- `"system_logs.%g.log"` - Pattern where `%g` is a placeholder for the generation number
- `1024 * 1024` - Sets the limit to 1MB for each log file
- `10` - Maximum number of log files to keep
- Creates a new log file after current file reaches 1MB
- Maintains up to 10 log files, overwrites oldest beyond that

### Exception Logging
- ✅ The application must log:
  - Validation failures
  - Persistence errors
  - Unexpected exceptions
- ✅ Must log at appropriate levels
- ✅ Severe issues must include **stack traces** for troubleshooting

### Authentication and Authorization
- ✅ The application must store **only BCrypt-hashed passwords**
- ✅ Must perform **role checks** for sensitive actions such as:
  - Discounts
  - Refunds
  - Reporting
  - User management

### Validation Rules
- ✅ The application must validate:
  - **Guest names** - with clear messages
  - **Phone numbers** - with clear messages
  - **Email addresses** - with clear messages
- ✅ Must provide clear validation error messages
- ✅ The application must validate **date ranges** with minimums and must check for overlaps
- ✅ The application must validate **occupancy distribution** across group bookings
- ✅ The application must validate **payment amounts** and must prevent negative balances
- ✅ The application must validate **discounts** within configured caps and must enforce non-negative values
- ✅ The application must validate **feedback ratings** and must cap comment length

---

## Reporting Specifications

### Revenue Reports
- ✅ The system must show:
  - Period
  - Reservation count
  - Subtotal
  - Tax
  - Discounts
  - Total
- ✅ Views: **Day, Week, and Month**
- ✅ The system must support filtering by:
  - Date range
  - Room type
- ✅ Must export to **CSV or PDF**

### Occupancy Reports
- ✅ The system must show:
  - Date
  - Rooms available
  - Rooms occupied
  - Occupancy percentage (in numeric form)
- ✅ The system must support filtering by:
  - Date range
  - Room type
- ✅ Must export to **CSV and PDF**

### Feedback Summary
- ✅ The system must show:
  - Reservation identifier
  - Guest
  - Rating
  - Comment
  - Date
  - Sentiment tag
- ✅ The system must display:
  - Average rating
  - Counts for common issue tags
- ✅ Must export to **CSV**

### Activity Logs
- ✅ The system must show:
  - Timestamp
  - Actor
  - Action
  - Entity type
  - Entity identifier
  - Message
- ✅ The system must read from:
  - The log file
  - OR the audit table
- ✅ Must export to **CSV or TXT**

---

## Project Milestones

### Milestone 1: November 12th, 2025 (5%)

**Requirements:**
- ✅ Short 3 to 5 minutes meeting presentation
- ✅ Present final front-end designs
- ✅ Present class diagrams, UML, etc.
- ✅ Present any other diagram that can support project structure and data flow
- ✅ **Final Screen Shots of all the front ends**
- ✅ Separate submission for:
  - All UML diagrams
  - Front end screen shots
  - Database designs
  - Any other design diagrams shown in lab
- ⚠️ **Only submissions approved during lab time will be accepted**
- ⚠️ **Attendance is mandatory for this milestone**

**Note:** Students can show their design in earlier labs as well.

---

### Final Submission: December 3rd, 2025 (13%)

**Requirements:**
- ✅ Record a video showing the full working project
- ✅ Video must include explanation of challenges faced
- ✅ Video must be **minimum 7-10 minutes** (can use more time, no deduction)
- ✅ Submit full project
- ✅ Submit database scripts (if using SQL, MySQL - must)

**Reflection Must Include:**
- ✅ Challenges you faced and finding the solutions
- ✅ Learnings during the project

---

## Project Documentation

### Purpose of Documentation
This is not just a summary of the project instructions — it's your opportunity to explain how you understood, designed, and implemented the system. Your documentation should reflect your decisions, challenges, and learning journey. It should include your own diagrams, explanations of how you applied patterns and business rules, and reflections on what worked well and what you'd improve. Think of it as a professional walkthrough of your work, showing how you brought the project to life.

### Documentation Checklist

#### 1. Project Overview
- [ ] Summary of system and purpose
- [ ] Key features
- [ ] Technologies used

#### 2. Architecture Summary
- [ ] Description of 3-tier architecture
- [ ] Cross-cutting concerns
- [ ] MVC, DI, ORM usage

#### 3. Design Artifacts
- [ ] Class diagram
- [ ] Sequence diagrams
- [ ] Deployment diagram
- [ ] Package diagram
- [ ] Optional UI screenshots

#### 4. Entity and Relationship Mapping
- [ ] List of entities
- [ ] Relationships and annotations
- [ ] Cascade/fetch/validation notes

#### 5. Pattern Usage
- [ ] Strategy, Observer, Factory, Decorator, Singleton
- [ ] Where and how each is used

#### 6. Business Rules
- [ ] Occupancy, pricing, discounts, loyalty, feedback
- [ ] Enforcement logic

#### 7. Security and Logging
- [ ] Authentication and roles
- [ ] Logging configuration and samples
- [ ] Exception handling

#### 8. Export and Reporting
- [ ] Report types and formats
- [ ] Optional sample exports

#### 9. Challenges and Learnings
- [ ] Technical challenges
- [ ] Personal reflections
- [ ] Suggestions for improvement

**Submission Format:**
- Submit one PDF or DOCX file
- Named: `ProjectDocumentation_[YourName].pdf`

---

## Grading Rubric

### Total: 100%

| Category | Weight | Description |
|----------|--------|-------------|
| **Design & Architecture** | 20% | MVC separation, DI, package structure, UML diagrams |
| **Patterns & Principles** | 15% | Strategy, Observer, Factory, Decorator, Singleton; OO principles |
| **ORM & Persistence** | 15% | JPA annotations, relationships, EMF/EM usage, queries |
| **Functionality – Kiosk** | 10% | Booking flow, validation, dynamic pricing |
| **Functionality – Admin** | 15% | Login, search, modify, payments, checkout, notifications |
| **Functionality – Waitlist & Loyalty** | 10% | Waitlist creation, observer notifications, loyalty dashboard |
| **Reporting & Feedback** | 10% | Tabular reports, export formats, feedback flow |
| **Logging & Security** | 5% | Logger rotation, audit logs, exception handling, BCrypt |

**Passing Threshold:**
- Minimum **50% overall**
- Must have:
  - ✅ Working kiosk booking
  - ✅ Admin login
  - ✅ ORM persistence

**Note:** Every Wednesday lab students can approach and discuss the design of the project, their progress and if there are any issues.

---

## Optional Requirement: Multithreaded Server for Admin Access

### Overview
In a real-world hotel reservation system, multiple admins might need to log in and manage bookings at the same time. To simulate this, you must implement a multithreaded server that allows multiple admin sessions to run simultaneously.

### Requirements

#### What This Means:
- ✅ The system should allow at least **two admins** to log in and use the system at the same time via separate command prompt windows
- ✅ Each admin should be able to:
  - Search guests
  - Modify reservations
  - Process checkouts
  - Apply discounts
- ✅ All actions should be **independent** (no conflicts or crashes)

### Implementation Approach

#### 1. Multi-Threaded Server Approach
- ✅ The system should have a **server-side application** that listens for admin connections
- ✅ When an admin logs in, the server should create a **new thread** to handle that admin's session
- ✅ This ensures that multiple admins can work independently without affecting each other

#### 2. Client-Server Communication
- ✅ The admin interface should act as a **client**, sending requests to the server
- ✅ The server will process requests and respond to each admin individually

#### 3. Testing the Multithreaded Functionality
- ✅ Open **two command prompt windows** and start an admin session in each
- ✅ Verify that both admins can:
  - Log in
  - Search for guests
  - Process reservations
- ✅ All operations should work **simultaneously**

---

## UML Class Diagram - Detailed Class Structure

### Complete Class Structure with Attributes, Relationships, and Design Patterns

---

## 📋 **1. Enumerations (E)**

### **RoomType**
- `SINGLE`
- `DOUBLE`
- `DELUXE`
- `PENTHOUSE`

### **ReservationStatus**
- `PENDING`
- `CONFIRMED`
- `CANCELLED`
- `CHECKED_OUT`

### **RoomStatus**
- `AVAILABLE`
- `OCCUPIED`
- `MAINTENANCE`

### **Role**
- `ADMIN`
- `MANAGER`

### **PaymentMethod**
- `CASH`
- `CARD`
- `POINTS`

### **PricingModel**
- `PER_NIGHT`
- `PER_RESERVATION`

---

## 🏗️ **2. Core Entity Classes (C)**

### **Hotel**
**Attributes:**
- `id` (int)
- `name` (String)
- `city` (String)

**Relationships:**
- Has 0..* `Room`s (1-to-0..*)

---

### **Room**
**Attributes:**
- `id` (int)
- `roomNumber` (String)
- `type` (RoomType)
- `beds` (int)
- `basePrice` (double)
- `status` (RoomStatus)

**Relationships:**
- Created by `RoomFactory` (Factory Pattern)
- Linked to multiple `Reservation`s via `ReservationRoom` (many-to-many)

---

### **Guest**
**Attributes:**
- `id` (int)
- `name` (String)
- `phone` (String)
- `email` (String)
- `address` (String)
- `loyaltyPoints` (int)

**Relationships:**
- Has 0..* `Reservation`s (1-to-0..*)
- Has 0..* `Feedback`s (1-to-0..*)
- Has 1 `Waitlist` entry (1-to-1)

---

### **Reservation**
**Attributes:**
- `id` (int)
- `checkIn` (Date)
- `checkOut` (Date)
- `numAdults` (int)
- `numChildren` (int)
- `status` (ReservationStatus)

**Relationships:**
- Belongs to 1 `Guest` (many-to-1)
- Has 0..* `Feedback`s (1-to-0..*)
- Has 0..* `ReservationAddon`s (1-to-0..*)
- Has 0..1 `Billing` (1-to-0..1)
- Has 1..* `ReservationRoom`s (1-to-1..*)

---

### **ReservationRoom**
**Attributes:**
- `reservation` (Reservation)
- `room` (Room)

**Purpose:** Links Reservation to Room (many-to-many relationship)

---

### **Billing**
**Attributes:**
- `id` (int)
- `reservation` (Reservation)
- `subtotal` (double)
- `taxRate` (double)
- `taxAmount` (double)
- `discountValue` (double)
- `loyaltyRedeemedPoints` (int)
- `totalAmount` (double)
- `paidAmount` (double)
- `balanceAmount` (double)
- `paymentStatus` (String)

**Relationships:**
- Linked 1:1 with `Reservation`
- Has 0..* `Payment`s (1-to-0..*)

---

### **Payment**
**Attributes:**
- `id` (int)
- `billing` (Billing)
- `method` (PaymentMethod)
- `amount` (double)
- `createdAt` (Date)

**Relationships:**
- Belongs to 1 `Billing` (many-to-1)

---

### **ServiceAddon**
**Attributes:**
- `id` (int)
- `name` (String)
- `price` (double)
- `pricingModel` (PricingModel)

**Relationships:**
- Used in `ReservationAddon` (Decorator Pattern)

---

### **ReservationAddon**
**Attributes:**
- `reservation` (Reservation)
- `addon` (ServiceAddon)
- `quantity` (int)

**Relationships:**
- Links `Reservation` to `ServiceAddon` (1-to-1)
- Part of Decorator Pattern implementation

---

### **Feedback**
**Attributes:**
- `id` (int)
- `guest` (Guest)
- `reservation` (Reservation)
- `rating` (int)
- `comments` (String)
- `sentimentTag` (String)
- `createdAt` (Date)

**Relationships:**
- Belongs to 1 `Guest` (many-to-1)
- Belongs to 1 `Reservation` (many-to-1)

---

### **Waitlist**
**Attributes:**
- `id` (int)
- `guest` (Guest)
- `requestedType` (RoomType)
- `dateRangeStart` (Date)
- `dateRangeEnd` (Date)
- `status` (String)

**Relationships:**
- Belongs to 1 `Guest` (1-to-1)
- Used by Observer Pattern for room availability notifications

---

### **AdminUser**
**Attributes:**
- `id` (int)
- `username` (String)
- `passwordHash` (String)
- `role` (Role)
- `active` (boolean)

---

### **AuditLog**
**Attributes:**
- `id` (int)
- `timestamp` (Date)
- `actor` (String)
- `action` (String)
- `entityType` (String)
- `entityId` (int)
- `message` (String)

**Purpose:** Logs all system actions for audit trail

---

## 🔧 **3. Service Classes (C)**

### **ReservationService**
**Dependencies:**
- `RoomRepository`
- `AddonRepository`
- `GuestRepository`
- `ReservationRepository`

**Responsibilities:**
- Creating/modifying reservations
- Assigning rooms
- Managing addons
- Availability checks

---

### **BillingService**
**Dependencies:**
- `BillingRepository`
- `PaymentRepository`
- `BillingStrategy` (interface - Strategy Pattern)

**Responsibilities:**
- Invoice creation
- Tax calculations
- Discounts
- Loyalty redemption
- Payment processing

**Uses Strategy Pattern:**
- `StandardBillingStrategy`
- `DiscountBillingStrategy`
- `LoyaltyBillingStrategy`

---

### **PricingService**
**Dependencies:**
- Uses `PricingModel` enum

**Responsibilities:**
- Calculates cost using selected pricing model (PER_NIGHT or PER_RESERVATION)

---

### **LoyaltyService**
**Dependencies:**
- `GuestRepository`
- `ReservationRepository`

**Responsibilities:**
- Loyalty point earning
- Loyalty point redemption

---

### **FeedbackService**
**Dependencies:**
- `FeedbackRepository`

**Responsibilities:**
- Customer feedback collection and management
- Sentiment analysis

---

### **ReportingService**
**Dependencies:**
- `BillingRepository`
- `FeedbackRepository`
- `AuditLogRepository`

**Responsibilities:**
- Revenue reports
- Feedback analytics
- Audit reports

---

### **ActivityLogService**
**Dependencies:**
- `AuditLogRepository`

**Responsibilities:**
- Writes audit records to database

---

### **AuthService**
**Dependencies:**
- `AdminUserRepository`

**Responsibilities:**
- Login validation
- Password validation
- Role checking

---

### **WaitlistService**
**Dependencies:**
- `WaitlistRepository`
- `RoomAvailabilityPublisher` (Subject - Observer Pattern)

**Responsibilities:**
- Adding to waitlist
- Notifying when rooms become available

---

### **RoomFactory**
**Dependencies:**
- `Room` (creates Room objects)

**Pattern:** Factory Pattern

**Responsibilities:**
- Creates Room objects based on type

---

## 🎨 **4. Design Pattern Implementations**

### **Strategy Pattern - Billing Strategies**

**Interface:**
- `BillingStrategy` (or `IBillingStrategy`)

**Implementations:**
- `StandardBillingStrategy` - Standard billing calculation
- `DiscountBillingStrategy` - Applies discounts
- `LoyaltyBillingStrategy` - Applies loyalty point redemption

**Used by:** `BillingService`

---

### **Decorator Pattern - Add-ons**

**Abstract Class:**
- `BookingComponent` (abstract base)

**Concrete Implementation:**
- `AddOnDecorator` (extends `BookingComponent`)

**Relationships:**
- `AddOnDecorator` associated with `ServiceAddon`
- `ReservationAddon` links `Reservation` to `ServiceAddon`

**Purpose:** Allows dynamically adding services/features to reservations

---

### **Observer Pattern - Room Availability & Waitlist**

**Interfaces:**
- `Subject` (or `ISubject`)
- `Observer` (or `IObserver`)

**Implementations:**
- `RoomAvailabilityPublisher` (implements `Subject`)
- `WaitlistSubscriber` (implements `Observer`)

**Flow:**
```
Room becomes available → RoomAvailabilityPublisher → WaitlistSubscriber → WaitlistService
```

**Purpose:** Notifies waitlist guests when rooms become available

---

### **Factory Pattern - Room Creation**

**Class:**
- `RoomFactory`

**Purpose:** Creates Room objects based on type

---

## 📦 **5. Repository Interfaces (I)**

All repositories follow Repository Pattern:

- `GuestRepository` - CRUD for Guest
- `RoomRepository` - CRUD for Room
- `ReservationRepository` - CRUD for Reservation
- `BillingRepository` - CRUD for Billing
- `AddonRepository` - CRUD for ServiceAddon
- `PaymentRepository` - CRUD for Payment
- `FeedbackRepository` - CRUD for Feedback
- `AdminUserRepository` - CRUD for AdminUser
- `WaitlistRepository` - CRUD for Waitlist
- `AuditLogRepository` - CRUD for AuditLog

**Pattern:** Repository Pattern - Abstracts data access layer

**Used by:** All Service classes

---

## 🔗 **6. Entity Relationships Summary**

### **One-to-Many Relationships:**
- `Hotel` → `Room` (1-to-0..*)
- `Guest` → `Reservation` (1-to-0..*)
- `Guest` → `Feedback` (1-to-0..*)
- `Reservation` → `Feedback` (1-to-0..*)
- `Reservation` → `ReservationAddon` (1-to-0..*)
- `Reservation` → `ReservationRoom` (1-to-1..*)
- `Billing` → `Payment` (1-to-0..*)

### **One-to-One Relationships:**
- `Guest` → `Waitlist` (1-to-1)
- `Reservation` → `Billing` (1-to-0..1)

### **Many-to-Many Relationships:**
- `Reservation` ↔ `Room` (via `ReservationRoom`)

### **Composition Relationships:**
- `ReservationAddon` links `Reservation` and `ServiceAddon`
- `ReservationRoom` links `Reservation` and `Room`

---

## 🎯 **7. Complete Dependency Graph**

### **Service Dependencies:**

```
ReservationService
 ├── RoomRepository
 ├── AddonRepository
 ├── GuestRepository
 └── ReservationRepository

BillingService
 ├── BillingRepository
 ├── PaymentRepository
 └── BillingStrategy (Strategy Pattern)

LoyaltyService
 ├── GuestRepository
 └── ReservationRepository

FeedbackService
 └── FeedbackRepository

ReportingService
 ├── BillingRepository
 ├── FeedbackRepository
 └── AuditLogRepository

ActivityLogService
 └── AuditLogRepository

AuthService
 └── AdminUserRepository

WaitlistService
 ├── WaitlistRepository
 └── RoomAvailabilityPublisher (Observer Pattern)
```

---

## 📊 **8. Design Patterns Summary**

| Pattern | Implementation | Purpose |
|---------|---------------|---------|
| **Repository Pattern** | All Repository interfaces | Abstracts data access layer |
| **Strategy Pattern** | `BillingStrategy` + implementations | Flexible billing calculations |
| **Decorator Pattern** | `BookingComponent` + `AddOnDecorator` | Dynamic add-on features |
| **Observer Pattern** | `Subject`/`Observer` + Publisher/Subscriber | Event-driven waitlist notifications |
| **Factory Pattern** | `RoomFactory` | Creates Room objects |
| **Service Layer** | All Service classes | Separates business logic |

---

### Package Dependencies Map

```
app
 ├── config
 ├── controller
 │     ├── view
 │     └── service
 │           ├── repository → model
 │           ├── config
 │           ├── security
 │           ├── util
 │           └── events
```

---

## Implementation Details

### Changes Log
*[Track all changes made during development]*

### Design Decisions
*[Key design decisions and rationale]*

---

## Notes
*[Additional notes and observations]*

