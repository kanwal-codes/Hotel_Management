# Hotel Reservation System - Project Documentation

**Project Name:** Hotel Reservation and Billing System  
**Submission Date:** December 3, 2025  
**Author:** [Your Name]  
**Course:** Advanced Programming and Design  
**Instructor:** [Instructor Name]

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture Summary](#2-architecture-summary)
3. [Design Artifacts](#3-design-artifacts)
4. [Entity and Relationship Mapping](#4-entity-and-relationship-mapping)
5. [Pattern Usage](#5-pattern-usage)
6. [Business Rules](#6-business-rules)
7. [Security and Logging](#7-security-and-logging)
8. [Export and Reporting](#8-export-and-reporting)
9. [Challenges and Learnings](#9-challenges-and-learnings)

---

## 1. Project Overview

### 1.1 Summary of System and Purpose

The Hotel Reservation System is a comprehensive desktop application designed to modernize hotel operations by replacing manual reservation and billing processes. The system serves two primary user groups:

- **Guests**: Self-service kiosk interface for booking rooms, selecting add-ons, and managing reservations
- **Administrators**: Full-featured dashboard for managing reservations, processing payments, handling checkouts, and generating reports

The system aims to:
- Streamline hotel operations and reduce manual errors
- Enhance guest experience through self-service capabilities
- Provide real-time reporting and analytics
- Maintain secure, auditable records of all transactions
- Support dynamic pricing and loyalty programs

### 1.2 Key Features

#### Guest-Facing Features (Kiosk)
- **Self-Service Booking Flow**: Complete reservation process from date selection to payment
- **Dynamic Room Selection**: View available rooms with real-time pricing
- **Add-On Services**: Select additional services (Wi-Fi, breakfast, spa, parking) with dynamic pricing
- **Booking Summary**: Detailed breakdown of charges before confirmation
- **Payment Processing**: Support for Cash, Card, and Loyalty Points payment methods
- **Booking Management**: View existing reservations and check booking status
- **Feedback Submission**: Submit feedback after checkout completion

#### Administrator Features
- **Unified Login**: Role-based access control (Admin/Manager) with BCrypt password hashing
- **Reservation Management**: Search, view, modify, and cancel reservations
- **Payment Processing**: Process payments, apply discounts, and handle refunds
- **Checkout Management**: Complete checkout process with billing recalculation for early checkouts
- **Waitlist Management**: Create and manage waitlists with automatic notifications
- **Discount Application**: Apply role-based discounts (Admin: 15%, Manager: 30%)
- **Loyalty Program Management**: View loyalty members, points, and manage redemptions
- **Feedback Management**: View and analyze guest feedback with sentiment analysis
- **Comprehensive Reporting**: Generate revenue, occupancy, activity logs, and feedback reports
- **PDF Receipt Generation**: Download final bills as PDF receipts

#### System Features
- **Dynamic Pricing**: Weekend/weekday multipliers and seasonal pricing
- **Loyalty Program**: Points earning (1 point per $10) and redemption (100 points = 1% discount, max 20%)
- **Observer Pattern**: Automatic waitlist notifications when rooms become available
- **Audit Logging**: Complete activity tracking for all administrative actions
- **Data Export**: Export reports to CSV, PDF, and TXT formats

### 1.3 Technologies Used

- **Programming Language**: Java 17
- **UI Framework**: JavaFX 17.0.2 (FXML, CSS)
- **Architecture Pattern**: MVC (Model-View-Controller)
- **Layered Architecture**: 3-Tier (Presentation → Business → Data)
- **Persistence Framework**: JPA (Java Persistence API) with Hibernate 5.6.15
- **Database**: MySQL 8.0.33
- **Security**: BCrypt (jbcrypt 0.4) for password hashing
- **PDF Generation**: Apache PDFBox 2.0.27
- **CSV Export**: Apache Commons CSV 1.10.0
- **Validation**: Bean Validation API 2.0.1 with Hibernate Validator 6.2.5
- **Build Tool**: Maven
- **Logging**: Java Util Logging with rotating file handlers

---

## 2. Architecture Summary

### 2.1 Description of 3-Tier Architecture

The system follows a strict **3-Tier Layered Architecture** with clear separation of concerns:

#### Presentation Tier
- **Location**: `com.hotel.controller` package
- **Components**: 
  - JavaFX Controllers (KioskController, AdminDashboardController, etc.)
  - FXML Views (in `src/main/resources/view/`)
  - Helper classes for UI logic (`com.hotel.controller.helper`)
- **Responsibilities**:
  - Handle user input and events
  - Display data to users
  - Navigate between screens
  - Validate user input (presentation-level)
  - Delegate business logic to services

#### Business/Application Tier
- **Location**: `com.hotel.service` and `com.hotel.config` packages
- **Components**:
  - Service classes (ReservationService, BillingService, etc.)
  - Policy classes (PricingPolicy, DiscountPolicy, LoyaltyPolicy)
  - Design pattern implementations
- **Responsibilities**:
  - Implement business rules and logic
  - Coordinate between controllers and repositories
  - Enforce business constraints
  - Handle transactions
  - Apply design patterns (Strategy, Observer, Decorator, Factory)

#### Data/Persistence Tier
- **Location**: `com.hotel.repository` and `com.hotel.model` packages
- **Components**:
  - Repository classes (ReservationRepository, GuestRepository, etc.)
  - Entity models (Reservation, Guest, Room, etc.)
  - JPA annotations and relationships
- **Responsibilities**:
  - Abstract database access
  - Map entities to database tables
  - Handle CRUD operations
  - Manage entity relationships
  - Execute queries

### 2.2 Cross-Cutting Concerns

#### Dependency Injection (DI)
- **Implementation**: `AppConfig` class centralizes dependency creation
- **Pattern**: Factory methods for creating services and repositories
- **Benefits**: 
  - Loose coupling between components
  - Easy testing and maintenance
  - Centralized configuration
- **Example**:

#### Logging
- **Implementation**: `LoggerService` (Singleton pattern)
- **Configuration**: 
  - Rotating file handler: 1MB per file, 10 files max
  - Logs to both file (`system_logs.%g.log`) and console
  - Activity logging with actor, action, entity type, and message
- **Usage**: All services use `LoggerService.getInstance()` for logging

#### Security
- **Authentication**: `AuthService` validates credentials using BCrypt
- **Password Hashing**: `BCryptPasswordHasher` uses BCrypt algorithm
- **Role-Based Access**: Admin and Manager roles with different permissions
- **Session Management**: `CustomerSession` for guest sessions

#### Exception Handling
- **Strategy**: Try-catch blocks in service methods with transaction rollback
- **Logging**: All exceptions logged with context
- **User Feedback**: User-friendly error messages via AlertHelper

### 2.3 MVC, DI, ORM Usage

#### MVC (Model-View-Controller)
- **Model**: Entity classes in `com.hotel.model` package
- **View**: FXML files in `src/main/resources/view/` directory
- **Controller**: Controller classes in `com.hotel.controller` package
- **Separation**: Controllers handle events, delegate to services, and update views

#### Dependency Injection
- **Central Configuration**: `AppConfig` class
- **Factory Methods**: Create services and repositories with proper dependencies
- **EntityManager Management**: Created per transaction, not shared
- **Singleton Services**: LoggerService, EntityManagerFactory

#### ORM (Object-Relational Mapping)
- **Framework**: JPA/Hibernate 5.6.15
- **Entity Annotations**: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- **Relationship Annotations**: `@OneToMany`, `@ManyToOne`, `@OneToOne`
- **Fetch Strategies**: Lazy loading by default, eager loading where needed
- **Cascade Operations**: `CascadeType.ALL` for dependent entities
- **Query Methods**: JPQL queries in repositories

---

## 3. Design Artifacts

### 3.1 Class Diagram

**PlantUML Source**: `docs/diagrams/ClassDiagram.puml`  
**Generated Image**: `docs/diagrams/images/ClassDiagram.png`

![Class Diagram](docs/diagrams/images/ClassDiagram.png)

The class diagram shows the complete structure of the system including:
- Entity models with their attributes and relationships
- Service classes and their dependencies
- Repository classes for data access
- Controller classes for UI handling
- Design pattern implementations (Strategy, Observer, Decorator)

**To generate the diagram:**
1. Visit https://plantuml.com/ or use PlantUML locally
2. Open `docs/diagrams/ClassDiagram.puml`
3. Generate PNG, SVG, or PDF output

**Key Classes:**

**Controllers:**
- `KioskController` - Handles guest self-service booking flow
- `AdminDashboardController` - Main admin interface
- `AdminReservationController` - Reservation management
- `AdminPaymentController` - Payment processing
- `AdminCheckoutController` - Checkout management
- `ReportController` - Report generation and export

**Services:**
- `ReservationService` - Core reservation business logic
- `BillingService` - Billing calculations and payment processing
- `LoyaltyService` - Loyalty points management
- `WaitlistService` - Waitlist management with observer pattern
- `ReportingService` - Report generation
- `FeedbackService` - Feedback collection and analysis

**Repositories:**
- `ReservationRepository` - Reservation data access
- `GuestRepository` - Guest data access
- `RoomRepository` - Room data access
- `BillingRepository` - Billing data access
- `PaymentRepository` - Payment data access

**Models:**
- `Reservation` - Core reservation entity
- `Guest` - Guest information
- `Room` - Room details
- `Billing` - Billing information
- `Payment` - Payment transactions
- `ServiceAddon` - Add-on services
- `Waitlist` - Waitlist entries
- `Feedback` - Guest feedback

### 3.2 Sequence Diagrams

**PlantUML Sources**: 
- `docs/diagrams/SequenceDiagram_Booking.puml` - Complete booking flow
- `docs/diagrams/SequenceDiagram_Payment.puml` - Payment processing
- `docs/diagrams/SequenceDiagram_Observer.puml` - Observer pattern for waitlist

#### Booking Flow Sequence

![Booking Sequence Diagram](docs/diagrams/images/SequenceDiagram_Booking.png)

Shows the complete interaction from guest selecting dates through confirmation, including:
- Guest input validation
- Guest lookup/creation
- Room availability checking
- Reservation creation
- Billing generation

#### Payment Processing Sequence

![Payment Sequence Diagram](docs/diagrams/images/SequenceDiagram_Payment.png)

Demonstrates payment processing including:
- Payment validation
- Payment entity creation
- Billing update
- Loyalty points redemption (if applicable)
- Transaction management

#### Observer Pattern Sequence (Waitlist Notification)

![Observer Sequence Diagram](docs/diagrams/images/SequenceDiagram_Observer.png)

Illustrates the Observer pattern implementation:
- Room availability publication
- Observer notification
- Waitlist service processing
- Admin notification display

### 3.3 Deployment Diagram

**PlantUML Source**: `docs/diagrams/DeploymentDiagram.puml`  
**Generated Image**: `docs/diagrams/images/DeploymentDiagram.png`

![Deployment Diagram](docs/diagrams/images/DeploymentDiagram.png)

The deployment diagram shows:
- Desktop application components (Kiosk UI, Admin UI, Feedback UI)
- Application layer (Controllers, Services, Repositories)
- ORM layer (JPA/Hibernate)
- Database (MySQL)
- File system (logs, PDFs, CSVs)

**To generate**: Use PlantUML with `docs/diagrams/DeploymentDiagram.puml`

### 3.4 Package Diagram

**PlantUML Source**: `docs/diagrams/PackageDiagram.puml`  
**Generated Image**: `docs/diagrams/images/PackageDiagram.png`

![Package Diagram](docs/diagrams/images/PackageDiagram.png)

The package diagram illustrates:
- Complete package structure of `com.hotel`
- Dependencies between packages
- Organization of classes by layer and responsibility

### 3.5 Additional Diagrams

#### Component Diagram

**PlantUML Source**: `docs/diagrams/ComponentDiagram.puml`  
**Generated Image**: `docs/diagrams/images/ComponentDiagram.png`

![Component Diagram](docs/diagrams/images/ComponentDiagram.png)

Shows system architecture components organized by layers.

#### Activity Diagram

**PlantUML Source**: `docs/diagrams/ActivityDiagram_Booking.puml`  
**Generated Image**: `docs/diagrams/images/ActivityDiagram_Booking.png`

![Activity Diagram](docs/diagrams/images/ActivityDiagram_Booking.png)

Illustrates the booking process flow with decision points and activities.

#### Use Case Diagram

**PlantUML Source**: `docs/diagrams/UseCaseDiagram.puml`  
**Generated Image**: `docs/diagrams/images/UseCaseDiagram.png`

![Use Case Diagram](docs/diagrams/images/UseCaseDiagram.png)

Shows all system use cases for Guest, Admin, and Manager actors.

### 3.5 UI Screenshots (Optional)

*Note: Screenshots would be included here showing:*
- Kiosk welcome screen
- Room selection interface
- Booking summary
- Admin dashboard
- Reservation management
- Payment processing
- Reports screen

---

## 4. Entity and Relationship Mapping

### 4.1 List of Entities

1. **Reservation** - Core reservation entity
2. **Guest** - Guest information
3. **Room** - Room details and availability
4. **Billing** - Billing information for reservations
5. **Payment** - Payment transactions
6. **ServiceAddon** - Add-on services (Wi-Fi, breakfast, etc.)
7. **ReservationRoom** - Join entity for Reservation-Room many-to-many
8. **ReservationAddon** - Join entity for Reservation-Addon many-to-many
9. **Waitlist** - Waitlist entries
10. **Feedback** - Guest feedback
11. **AdminUser** - Administrator accounts
12. **AuditLog** - Activity logging
13. **Hotel** - Hotel information

### 4.2 Relationships and Annotations

#### Reservation Entity Relationships

#### Key Relationships Summary

| Relationship | Type | Entities | Cascade | Fetch Type |
|-------------|------|----------|---------|------------|
| Reservation → Guest | Many-to-One | Reservation, Guest | None | LAZY |
| Reservation → ReservationRooms | One-to-Many | Reservation, ReservationRoom | ALL | LAZY |
| Reservation → ReservationAddons | One-to-Many | Reservation, ReservationAddon | ALL | LAZY |
| Reservation ↔ Billing | One-to-One | Reservation, Billing | ALL | LAZY |
| Reservation → Feedbacks | One-to-Many | Reservation, Feedback | ALL | LAZY |
| Billing → Payments | One-to-Many | Billing, Payment | ALL | LAZY |
| Guest → Reservations | One-to-Many | Guest, Reservation | ALL | LAZY |
| Guest → Waitlist | One-to-One | Guest, Waitlist | ALL | LAZY |
| Room → ReservationRooms | One-to-Many | Room, ReservationRoom | None | LAZY |
| ServiceAddon → ReservationAddons | One-to-Many | ServiceAddon, ReservationAddon | None | LAZY |

### 4.3 Cascade/Fetch/Validation Notes

#### Cascade Types
- **CascadeType.ALL**: Used for dependent entities (ReservationRoom, ReservationAddon, Billing, Payment, Feedback)
  - When parent is deleted, children are deleted
  - When parent is persisted, children are persisted
  - Ensures data consistency

#### Fetch Strategies
- **LAZY (Default)**: Used for most relationships to avoid N+1 query problems
  - Collections loaded only when accessed
  - Better performance for large datasets
- **EAGER**: Used sparingly, only when immediate access is always needed
  - Example: `findByIdWithRooms()` uses JOIN FETCH for specific queries

#### Validation Annotations
- **@NotNull**: Required fields (checkIn, checkOut, guest, etc.)
- **@Positive**: Numeric fields must be positive (numAdults, amounts)
- **@Min(0)**: Non-negative values (numChildren)
- **@Column(nullable = false)**: Database-level constraints

#### Special Query Methods
- **findByIdWithRooms()**: Eagerly fetches rooms and addons separately to avoid MultipleBagFetchException
- Uses two separate queries to fetch collections safely

---

## 5. Pattern Usage

### 5.1 Strategy Pattern

**Purpose**: Different billing calculation strategies without changing service code

**Location**: `com.hotel.service.strategy`

**Implementation**: The Strategy pattern is implemented through a `BillingStrategy` interface with three concrete implementations: `StandardBillingStrategy` for basic billing calculations, `DiscountBillingStrategy` for applying discounts, and `LoyaltyBillingStrategy` for loyalty point redemptions.

**Usage in BillingService**: The service selects the appropriate strategy based on billing attributes (loyalty points redeemed, discount value, or standard billing) and uses it to calculate the total amount.

**Benefits**:
- Open/Closed Principle: Add new strategies without modifying existing code
- Single Responsibility: Each strategy handles one calculation method
- Easy testing: Test each strategy independently

### 5.2 Observer Pattern

**Purpose**: Notify waitlist when rooms become available

**Location**: `com.hotel.events`

**Implementation**: The Observer pattern is implemented through `Subject` and `Observer` interfaces. `RoomAvailabilityPublisher` acts as the concrete subject that maintains a list of observers and notifies them when rooms become available. `WaitlistSubscriber` is the concrete observer that receives notifications and updates the waitlist service.

**Usage Flow**:
1. `ReservationService.checkoutReservation()` calls `roomAvailabilityPublisher.publishRoomAvailable(room)`
2. `RoomAvailabilityPublisher` notifies all subscribers
3. `WaitlistSubscriber` receives notification and updates waitlist service
4. Admin sees notification in waitlist management screen

**Benefits**:
- Decoupling: Room availability logic separate from waitlist logic
- Extensibility: Easy to add new observers (e.g., email notifications)
- Loose coupling: Publisher doesn't know about specific subscribers

### 5.3 Factory Pattern

**Purpose**: Create Room instances with configured attributes

**Location**: `com.hotel.util.RoomFactory`

**Implementation**: The Factory pattern is implemented through a static factory method `createRoom()` that takes room type, room number, and base price as parameters, creates a new Room instance, sets all attributes including status to AVAILABLE, and returns the configured room object.

**Usage**: Used in `SeedData` to create test rooms with consistent configuration

**Benefits**:
- Centralized creation logic
- Consistent object initialization
- Easy to modify creation process

### 5.4 Decorator Pattern

**Purpose**: Dynamically add services (add-ons) to booking pricing

**Location**: `com.hotel.service.decorator`

**Implementation**: The Decorator pattern is implemented through an abstract `BookingComponent` class with concrete implementations. `RoomBookingComponent` represents the base room booking, and `AddOnDecorator` wraps it to add additional services. Each decorator adds its price to the component's price, allowing dynamic composition of services.

**Usage**: In booking summary calculation, decorators wrap room booking with add-ons

**Benefits**:
- Dynamic composition: Add services at runtime
- Flexible pricing: Each add-on can have different pricing model
- Single Responsibility: Each decorator handles one add-on type

### 5.5 Singleton Pattern

**Purpose**: Ensure single instance of critical services

**Location**: 
- `com.hotel.util.LoggerService`
- `EntityManagerFactory` in `AppConfig`

**Implementation**: The Singleton pattern is implemented using double-checked locking. The `LoggerService` class has a private static instance variable and a public static `getInstance()` method that ensures only one instance is created. The constructor is private to prevent direct instantiation.

**Usage**: 
- `LoggerService.getInstance()` used throughout application
- `EntityManagerFactory` created once in `AppConfig.initialize()`

**Benefits**:
- Resource efficiency: Single logger instance, single EMF
- Consistency: Same logger configuration everywhere
- Thread-safe: Double-checked locking for singleton creation

---

## 6. Business Rules

### 6.1 Occupancy Rules

**Rule**: Rooms can only be reserved if they are AVAILABLE

**Enforcement**:
- `RoomRepository.findAvailableRooms()` filters by `RoomStatus.AVAILABLE`
- `ReservationService.createReservation()` checks room availability before booking
- Room status updated to `OCCUPIED` when reservation is confirmed
- Room status updated to `AVAILABLE` when reservation is checked out or cancelled

**Implementation**: The system validates room availability by checking the room status before allowing reservations. If a room is not available, an exception is thrown. Upon successful reservation, the room status is updated to OCCUPIED, and when the reservation is completed or cancelled, the status is reset to AVAILABLE.

### 6.2 Pricing Rules

**Dynamic Pricing**:
- **Weekend Multiplier**: 1.2x (20% increase) for Saturday and Sunday
- **Weekday Multiplier**: 1.0x (normal price) for Monday-Friday
- **Seasonal Pricing**: Configurable multipliers for specific date ranges

**Implementation**: `PricingPolicy.calculatePriceForDateRange()`
- Iterates through each night
- Applies appropriate multiplier based on day of week
- Checks seasonal multipliers first, then falls back to weekend/weekday

**Example**:
- Base price: $100/night
- Weekend (Sat-Sun): $120/night
- Weekday (Mon-Fri): $100/night
- 3-night stay (Fri-Sun): $100 + $120 + $120 = $340

### 6.3 Discount Rules

**Role-Based Discount Caps**:
- **Admin**: Maximum 15% discount
- **Manager**: Maximum 30% discount

**Enforcement**: `DiscountPolicy.validateAndCapDiscount()` validates the discount percentage against the role-based maximum and caps it if it exceeds the limit.

**Application**:
- Discounts applied to subtotal before tax calculation
- Discount amount calculated: `subtotal * (discountPercent / 100)`
- Discounts logged in audit log with actor information

### 6.4 Loyalty Rules

**Points Earning**:
- **Rate**: 1 point per $10 spent
- **Calculation**: `pointsEarned = paymentAmount / 10`
- **Accumulation**: Points added to guest's loyalty balance after payment

**Points Redemption**:
- **Conversion**: 100 points = 1% discount
- **Maximum Redemption**: 1000 points per reservation
- **Maximum Discount**: 20% discount from loyalty points
- **Calculation**: `discountPercent = min(pointsRedeemed / 100, 20)`

**Enforcement**: `LoyaltyPolicy.calculateDiscountFromPoints()` calculates the discount percentage from redeemed points, ensuring it does not exceed the maximum allowed discount of 20%.

### 6.5 Feedback Rules

**Eligibility**:
- Feedback can only be submitted after reservation is CHECKED_OUT
- All balances must be fully settled (balanceAmount = 0)
- One feedback per reservation

**Enforcement**: `FeedbackService.submitFeedback()` validates that the reservation is checked out and the balance is zero before allowing feedback submission.

**Sentiment Analysis**:
- Automatic sentiment tagging based on rating and keywords
- Tags: POSITIVE, NEGATIVE, NEUTRAL
- Used in feedback reports and analytics

### 6.6 Waitlist Rules

**Creation**:
- Guest can be added to waitlist if no rooms available
- One waitlist entry per guest
- Waitlist entries linked to preferred room type and dates

**Notification**:
- When room becomes available, Observer pattern notifies waitlist
- Admin sees notification in waitlist management screen
- Admin can contact guest to confirm availability

**Implementation**: Observer pattern with `RoomAvailabilityPublisher` and `WaitlistSubscriber`

### 6.7 Payment Rules

**Payment Methods**:
- **Cash**: Direct payment, no additional processing
- **Card**: Credit/debit card payment
- **Points**: Loyalty points redemption

**Payment Processing**:
- Payments reduce billing balance
- Payment status updated: PENDING → PARTIAL → PAID
- Multiple payments allowed (partial payments)
- Payment history tracked for audit

**Refunds**:
- Refunds increase balance amount
- Refund payments logged with negative amount
- Payment history shows all transactions

---

## 7. Security and Logging

### 7.1 Authentication and Roles

**Authentication Flow**:
1. User enters username and password in `UnifiedLoginController`
2. `AuthService.authenticate()` validates credentials
3. BCrypt verifies password hash
4. User role retrieved from database
5. Session created with user information

**Password Hashing**:
- **Algorithm**: BCrypt (via jbcrypt library)
- **Implementation**: `BCryptPasswordHasher.hash()` and `BCryptPasswordHasher.verify()`
- **Security**: Salt automatically generated, one-way hashing

**Role-Based Access**:
- **Admin Role**: 
  - Full access to all features
  - Discount cap: 15%
  - Can manage all reservations
- **Manager Role**:
  - Full access to all features
  - Discount cap: 30%
  - Can manage all reservations

**Role Enforcement**:
- Controllers check user role before allowing actions
- Discount application validates role-based caps
- Audit logs record actor role for all actions

### 7.2 Logging Configuration

**Logger Service**:
- **Pattern**: Singleton
- **Implementation**: `LoggerService` class
- **Configuration**: Java Util Logging with FileHandler

**Log Rotation**:
- **File Size**: 1MB per file
- **File Count**: 10 files maximum
- **File Pattern**: `system_logs.%g.log` (where %g is generation number)
- **Append Mode**: true (append to existing files)

**Log Levels**:
- **INFO**: General information, successful operations
- **WARNING**: Potential issues, validation failures
- **SEVERE**: Errors, exceptions, critical failures

**Activity Logging**:
- **Format**: `[actor] action - entityType (ID: entityId): message`
- **Example**: `[admin] CHECKOUT - Reservation (ID: 123): Guest checked out successfully`
- **Storage**: Both file and database (`AuditLog` entity)

**Logging Examples**: The system logs information messages for successful operations, error messages with exception details for failures, and activity logs with actor, action, entity type, and descriptive messages for all administrative actions.

### 7.3 Exception Handling

**Strategy**:
- Try-catch blocks in all service methods
- Transaction rollback on exceptions
- User-friendly error messages via AlertHelper
- Detailed error logging for debugging

**Transaction Management**:

**Exception Types**:
- **IllegalStateException**: Business rule violations (e.g., room not available)
- **IllegalArgumentException**: Invalid input parameters
- **RuntimeException**: Unexpected errors, wrapped and logged

**User Feedback**:
- Errors displayed via `AlertHelper.showError()`
- Success messages via `AlertHelper.showInfo()`
- Validation errors shown inline on forms

---

## 8. Export and Reporting

### 8.1 Report Types

#### Revenue Report
- **Purpose**: Track revenue by date range
- **Data**: Reservation count, subtotal, tax, discounts, total
- **Filters**: Date range (start date, end date)
- **Export Formats**: CSV, PDF

#### Occupancy Report
- **Purpose**: Track room occupancy by room type
- **Data**: Date, available rooms, occupied rooms, occupancy percentage
- **Filters**: Date range, room type
- **Export Formats**: CSV, PDF

#### Activity Logs Report
- **Purpose**: Audit trail of administrative actions
- **Data**: Timestamp, actor, action, entity type, entity ID, message
- **Filters**: Date range
- **Export Formats**: CSV, PDF, TXT

#### Feedback Summary Report
- **Purpose**: Analyze guest feedback
- **Data**: Reservation ID, guest name, rating, comment, date, sentiment tag
- **Metrics**: Total feedback count, average rating
- **Export Formats**: CSV, PDF

### 8.2 Export Formats

#### CSV Export
- **Library**: Apache Commons CSV 1.10.0
- **Implementation**: `CsvExporter` class
- **Features**: 
  - Headers in first row
  - Comma-separated values
  - Proper escaping of special characters
- **Usage**: All report types support CSV export

#### PDF Export
- **Library**: Apache PDFBox 2.0.27
- **Implementation**: `PdfExporter` class
- **Features**:
  - Formatted tables
  - Headers and titles
  - Page breaks for long reports
  - Professional layout
- **Usage**: Revenue, Occupancy, Activity Logs, Feedback Summary reports

#### TXT Export
- **Implementation**: `TxtExporter` class
- **Features**: Plain text format, tab-separated values
- **Usage**: Activity Logs report (primary format)

#### PDF Receipt
- **Purpose**: Final bill receipt for guests
- **Implementation**: `ReceiptService.generateReceipt()`
- **Content**:
  - Reservation details
  - Guest information
  - Room charges
  - Add-on charges
  - Tax and discounts
  - Payment history
  - Payment summary
- **Usage**: Generated from checkout screen

### 8.3 Sample Exports

*Note: Sample export files would be included here showing:*
- Sample revenue report CSV
- Sample occupancy report PDF
- Sample activity log TXT
- Sample receipt PDF

---

## 9. Challenges and Learnings

### 9.1 Technical Challenges

#### Challenge 1: MultipleBagFetchException in Hibernate

**Problem**: 
When trying to eagerly fetch multiple `@OneToMany` collections (reservationRooms and reservationAddons) in a single query, Hibernate threw `MultipleBagFetchException`.

**Solution**:
Split the query into two separate queries:
1. First query fetches reservation with rooms
2. Second query fetches reservation with addons
3. Hibernate merges the results in the same persistence context

**Learning**: 
- Hibernate has limitations on fetching multiple collections
- Understanding ORM behavior is crucial for performance
- Sometimes multiple queries are better than one complex query

#### Challenge 2: State Management in Kiosk Flow

**Problem**: 
KioskController was over 3000 lines, making it difficult to maintain and test. State needed to be preserved across multiple screens.

**Solution**:
- Extracted logic into helper classes:
  - `KioskGuestDetailsHelper`: Guest validation and processing
  - `KioskRoomSelectionHelper`: Room selection logic
  - `KioskAddOnHelper`: Add-on calculations
  - `KioskBookingSummaryHelper`: Summary display
  - `KioskStateHelper`: State management
  - `KioskValidationHelper`: Validation logic
- Created `BookingState` class to transfer state between screens
- Reduced controller to ~1882 lines

**Learning**:
- Helper classes improve code organization
- State management is critical for multi-screen flows
- Refactoring large classes improves maintainability

#### Challenge 3: PDF Generation Font API Compatibility

**Problem**: 
PDFBox 2.0.27 doesn't have public `Standard14Fonts` class, causing compilation errors.

**Solution**:
Used the older font API: `PDType1Font.HELVETICA_BOLD` instead of `new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)`.

**Learning**:
- Library version compatibility is important
- Older APIs may be more stable
- Always check library documentation for correct usage

#### Challenge 4: Checkout Status Validation

**Problem**: 
System prevented checkout if reservation was already CHECKED_OUT, but users needed to recalculate billing for already-checked-out reservations.

**Solution**:
Modified `checkoutReservation()` to allow billing recalculation even if already checked out:
- Detect if this is a billing recalculation scenario
- Skip status change and room freeing if already checked out
- Still allow billing recalculation

**Learning**:
- Business rules need to accommodate edge cases
- User workflow should drive technical decisions
- Flexibility in status transitions is important

### 9.2 Personal Reflections

#### What Worked Well

1. **3-Tier Architecture**: Clear separation made development and debugging easier
2. **Design Patterns**: Strategy, Observer, Decorator patterns made code flexible and extensible
3. **Helper Classes**: Breaking down large controllers improved code organization
4. **Comprehensive Logging**: Made debugging and auditing much easier
5. **JPA/Hibernate**: ORM simplified database operations significantly

#### What Could Be Improved

1. **Error Handling**: Could implement more specific exception types
2. **Testing**: Would benefit from unit tests for services and repositories
3. **UI/UX**: Some screens could be more intuitive with better visual feedback
4. **Performance**: Some queries could be optimized further
5. **Documentation**: More inline documentation would help future maintenance

#### Key Learnings

1. **ORM Understanding**: Deep understanding of JPA/Hibernate is essential for performance
2. **Design Patterns**: Proper pattern usage makes code more maintainable
3. **State Management**: Critical for multi-screen applications
4. **Refactoring**: Regular refactoring prevents technical debt
5. **User Experience**: Business rules should align with user workflows

### 9.3 Suggestions for Improvement

#### Short-Term Improvements

1. **Add Unit Tests**: Implement JUnit tests for services and repositories
2. **Improve Error Messages**: More specific, user-friendly error messages
3. **Add Input Validation**: Client-side validation for better UX
4. **Optimize Queries**: Review and optimize slow queries
5. **Add Loading Indicators**: Show progress for long operations

#### Long-Term Improvements

1. **Web Application**: Convert to web-based application for remote access
2. **Mobile App**: Develop mobile app for guest bookings
3. **Real-Time Notifications**: WebSocket-based real-time updates
4. **Advanced Analytics**: Machine learning for pricing optimization
5. **Integration**: Integrate with payment gateways and booking platforms

#### Architecture Improvements

1. **Microservices**: Break into smaller, independent services
2. **Caching**: Implement caching layer for frequently accessed data
3. **Message Queue**: Use message queue for async operations
4. **API Layer**: Create REST API for external integrations
5. **Database Optimization**: Index optimization, query tuning

---

## Conclusion

This Hotel Reservation System demonstrates a comprehensive understanding of:
- 3-tier layered architecture
- Design patterns (Strategy, Observer, Factory, Decorator, Singleton)
- JPA/Hibernate ORM with proper relationship mapping
- Business rule enforcement
- Security with BCrypt password hashing
- Comprehensive logging and auditing
- Report generation and export functionality

The system successfully addresses all requirements while maintaining clean, maintainable code through proper separation of concerns, design patterns, and best practices.

---

**End of Documentation**

