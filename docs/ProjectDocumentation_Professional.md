HOTEL RESERVATION SYSTEM
PROJECT DOCUMENTATION

Project Name: Hotel Reservation and Billing System
Submission Date: [Current Date]
Author: [Your Name]
Course: Advanced Programming and Design
Instructor: [Instructor Name]

================================================================================
PURPOSE OF THIS DOCUMENTATION
================================================================================

This documentation represents my understanding, design decisions, and implementation
of the Hotel Reservation System. It is not merely a summary of project instructions,
but rather a professional walkthrough of how I brought the system to life.

This document includes:
    • My design decisions and rationale
    • How I applied design patterns and business rules
    • Challenges I encountered and how I solved them
    • Reflections on what worked well and what could be improved
    • My learning journey throughout the project

Note: This document reflects the current state of the codebase as of the latest update.

================================================================================
TABLE OF CONTENTS
================================================================================

1. Project Overview
   1.1 Summary of System and Purpose
   1.2 Key Features
   1.3 Technologies Used

2. Architecture Summary
   2.1 Description of 3-Tier Architecture
   2.2 Cross-Cutting Concerns
   2.3 MVC, DI, ORM Usage

3. Design Artifacts
   3.1 Class Diagram
   3.2 Sequence Diagrams
   3.3 Deployment Diagram
   3.4 Package Diagram
   3.5 Additional Diagrams
   3.6 Optional UI Screenshots

4. Entity and Relationship Mapping
   4.1 List of Entities
   4.2 Relationships and Annotations
   4.3 Cascade/Fetch/Validation Notes

5. Pattern Usage
   5.1 Strategy Pattern (Where and How Used)
   5.2 Observer Pattern (Where and How Used)
   5.3 Factory Pattern (Where and How Used)
   5.4 Decorator Pattern (Where and How Used)
   5.5 Singleton Pattern (Where and How Used)

6. Business Rules
   6.1 Occupancy Rules (Enforcement Logic)
   6.2 Pricing Rules (Enforcement Logic)
   6.3 Discount Rules (Enforcement Logic)
   6.4 Loyalty Rules (Enforcement Logic)
   6.5 Feedback Rules (Enforcement Logic)
   6.6 Waitlist Rules (Enforcement Logic)
   6.7 Payment Rules (Enforcement Logic)

7. Security and Logging
   7.1 Authentication and Roles
   7.2 Logging Configuration
   7.3 Exception Handling

8. Export and Reporting
   8.1 Report Types
   8.2 Export Formats
   8.3 Optional Sample Exports

9. Challenges and Learnings
   9.1 Technical Challenges
   9.2 Personal Reflections
   9.3 Suggestions for Improvement

================================================================================
1. PROJECT OVERVIEW
================================================================================

1.1 Summary of System and Purpose

The Hotel Reservation System is a comprehensive desktop application designed to modernize hotel operations by replacing manual reservation and billing processes. The system serves two primary user groups:

    Guests: Self-service kiosk interface for booking rooms, selecting add-ons, and managing reservations
    
    Administrators: Full-featured dashboard for managing reservations, processing payments, handling checkouts, and generating reports

The system aims to:
    • Streamline hotel operations and reduce manual errors
    • Enhance guest experience through self-service capabilities
    • Provide real-time reporting and analytics
    • Maintain secure, auditable records of all transactions
    • Support dynamic pricing and loyalty programs


1.2 Key Features

Guest-Facing Features (Kiosk)

    Self-Service Booking Flow: Complete reservation process from date selection to payment
    
    Dynamic Room Selection: View available rooms with real-time pricing
    
    Add-On Services: Select additional services (Wi-Fi, breakfast, spa, parking) with dynamic pricing
    
    Booking Summary: Detailed breakdown of charges before confirmation
    
    Payment Processing: Support for Cash, Card, and Loyalty Points payment methods
    
    Booking Management: View existing reservations and check booking status
    
    Feedback Submission: Submit feedback after checkout completion

Administrator Features

    Unified Login: Role-based access control (Admin/Manager) with BCrypt password hashing
    
    Reservation Management: Search, view, modify, and cancel reservations
    
    Payment Processing: Process payments, apply discounts, and handle refunds
    
    Checkout Management: Complete checkout process with billing recalculation for early checkouts
    
    Waitlist Management: Create and manage waitlists with automatic notifications
    
    Discount Application: Apply role-based discounts (Admin: 15%, Manager: 30%)
    
    Loyalty Program Management: View loyalty members, points, and manage redemptions
    
    Feedback Management: View and analyze guest feedback with sentiment analysis
    
    Comprehensive Reporting: Generate revenue, occupancy, activity logs, and feedback reports
    
    PDF Receipt Generation: Download final bills as PDF receipts

System Features

    Dynamic Pricing: Weekend/weekday multipliers and seasonal pricing
    
    Loyalty Program: Points earning (1 point per $10) and redemption (100 points = 1% discount, max 20%)
    
    Observer Pattern: Automatic waitlist notifications when rooms become available
    
    Audit Logging: Complete activity tracking for all administrative actions
    
    Data Export: Export reports to CSV, PDF, and TXT formats


1.3 Technologies Used

    Programming Language: Java 17
    UI Framework: JavaFX 17.0.2 (FXML, CSS)
    Architecture Pattern: MVC (Model-View-Controller)
    Layered Architecture: 3-Tier (Presentation → Business → Data)
    Persistence Framework: JPA (Java Persistence API) with Hibernate 5.6.15
    Database: MySQL 8.0.33
    Security: BCrypt (jbcrypt 0.4) for password hashing
    PDF Generation: Apache PDFBox 2.0.27
    CSV Export: Apache Commons CSV 1.10.0
    Validation: Bean Validation API 2.0.1 with Hibernate Validator 6.2.5
    Build Tool: Maven
    Logging: Java Util Logging with rotating file handlers


================================================================================
2. ARCHITECTURE SUMMARY
================================================================================

2.1 Description of 3-Tier Architecture

The system follows a strict 3-Tier Layered Architecture with clear separation of concerns:

PRESENTATION TIER (JavaFX)
    • Controllers (MVC)
    • FXML Views
    • User Interface Logic
         ↓
BUSINESS/APPLICATION TIER
    • Services (Business Logic)
    • Configuration (Policies)
    • Design Patterns
    • Validation Logic
         ↓
DATA/PERSISTENCE TIER
    • Repositories (Data Access)
    • JPA/Hibernate ORM
    • Entity Models
    • Database (MySQL)


Presentation Tier

    Location: com.hotel.controller package
    
    Components:
        • JavaFX Controllers (KioskController, AdminDashboardController, etc.)
        • FXML Views (in src/main/resources/view/)
        • Helper classes for UI logic (com.hotel.controller.helper) - 30+ helper classes:
            Kiosk Helpers:
            - KioskGuestDetailsHelper - Guest validation and processing
            - KioskRoomSelectionHelper - Room selection logic
            - KioskAddOnHelper - Add-on calculations
            - KioskBookingSummaryHelper - Summary display
            - KioskStateHelper - State management
            - KioskValidationHelper - Validation logic
            - KioskPaymentHelper - Payment preparation
            - KioskNavigationHelper - Navigation and screen detection
            - KioskInitializationHelper - Initialization logic
            - KioskConfirmationHelper - Confirmation screen logic
            - KioskLoyaltyHelper - Loyalty program operations
            - KioskRoomSpinnerHelper - Room spinner operations
            - KioskUIHelper - UI-related operations
            - KioskValidationService - Validation service
            - KioskNavigationService - Navigation service
            
            Admin Helpers:
            - AdminGuestManagementHelper - Guest management UI
            - AdminRoomSelectionHelper - Admin room selection
            - AdminReservationUIHelper - UI updates
            - AdminReservationLoaderHelper - Data loading
            - AdminReservationValidationHelper - Validation
            - AdminReservationEventHandler - Event handling
            - AdminTableConfigurationHelper - Table configuration
            - AdminRoomTypeSummaryHelper - Room type summaries
            - AdminFormInitializer - Form initialization
            - AdminDialogBuilder - Dialog building
            - AdminNavigationHelper - Navigation
            
            Shared Helpers:
            - AlertHelper - Unified alert dialogs
            - NavigationHelper - Unified navigation
            - ValidationHelper - Centralized validation
            - RoomSelectionHelper - Room selection utilities
        • Base controllers (BaseController, BasePaymentController) for shared functionality
    
    Responsibilities:
        • Handle user input and events
        • Display data to users
        • Navigate between screens
        • Validate user input (presentation-level)
        • Delegate business logic to services
        • Manage UI state and navigation flow


Business/Application Tier

    Location: com.hotel.service, com.hotel.config, and com.hotel.util packages
    
    Components:
        • Service classes (11 services):
            - ReservationService - Core reservation business logic
            - AdminReservationService - Admin-specific reservation operations
            - BillingService - Billing calculations and payment processing
            - LoyaltyService - Loyalty points management
            - WaitlistService - Waitlist management with observer pattern
            - ReportingService - Report generation
            - FeedbackService - Feedback collection and analysis
            - PricingService - Dynamic pricing calculations
            - AuthService - Authentication and authorization
            - ActivityLogService - Audit logging
            - ReceiptService - PDF receipt generation
        • Policy classes (PricingPolicy, DiscountPolicy, LoyaltyPolicy)
        • Design pattern implementations (Strategy, Observer, Decorator, Factory)
        • Utility classes:
            - EmailDetector - Email-based role detection
            - CsvExporter - CSV export with proper escaping
            - PdfExporter - PDF export functionality
            - TxtExporter - Text export functionality
            - LoggerService - Singleton logging service
            - RoomFactory - Factory pattern for room creation
            - FormFieldParser - Form field parsing
            - ReservationStatusParser - Status parsing
            - Validator - Input validation
            - TextFieldListenerHelper - Text field event handling
            - ReservationEntityManager - Entity manager utilities
    
    Responsibilities:
        • Implement business rules and logic
        • Coordinate between controllers and repositories
        • Enforce business constraints
        • Handle transactions
        • Apply design patterns (Strategy, Observer, Decorator, Factory)
        • Provide utility functions for common operations


Data/Persistence Tier

    Location: com.hotel.repository and com.hotel.model packages
    
    Components:
        • Repository classes (ReservationRepository, GuestRepository, etc.)
        • Entity models (Reservation, Guest, Room, etc.)
        • JPA annotations and relationships
    
    Responsibilities:
        • Abstract database access
        • Map entities to database tables
        • Handle CRUD operations
        • Manage entity relationships
        • Execute queries
        • Support complex queries with JPQL


2.2 Cross-Cutting Concerns

Dependency Injection (DI)

    Implementation: AppConfig class centralizes dependency creation
    
    Pattern: Factory methods for creating services and repositories
    
    Benefits:
        • Loose coupling between components
        • Easy testing and maintenance
        • Centralized configuration
    
    Usage: AppConfig provides factory methods for creating services and repositories with proper dependencies.
    
    Components:
        • EntityManagerFactory (Singleton) - Created once at application startup
        • EntityManager - Created per transaction
        • All services and repositories created via factory methods
        • Centralized configuration management


Logging

    Implementation: LoggerService (Singleton pattern)
    
    Configuration:
        • Rotating file handler: 1MB per file, 10 files max
        • Logs to both file (system_logs.%g.log) and console
        • Activity logging with actor, action, entity type, and message
    
    Usage: All services use LoggerService.getInstance() for logging


Security

    Authentication: AuthService validates credentials using BCrypt
    
    Password Hashing: BCryptPasswordHasher uses BCrypt algorithm
    
    Role-Based Access: Admin and Manager roles with different permissions
    
    Session Management: CustomerSession for guest sessions


Exception Handling

    Strategy: Try-catch blocks in service methods with transaction rollback
    
    Logging: All exceptions logged with context
    
    User Feedback: User-friendly error messages via AlertHelper


2.3 MVC, DI, ORM Usage

MVC (Model-View-Controller)

    Model: Entity classes in com.hotel.model package
    
    View: FXML files in src/main/resources/view/ directory
    
    Controller: Controller classes in com.hotel.controller package
    
    Separation: Controllers handle events, delegate to services, and update views


Dependency Injection

    Central Configuration: AppConfig class
    
    Factory Methods: Create services and repositories with proper dependencies
    
    EntityManager Management: Created per transaction, not shared
    
    Singleton Services: LoggerService, EntityManagerFactory


ORM (Object-Relational Mapping)

    Framework: JPA/Hibernate 5.6.15
    
    Entity Annotations: @Entity, @Table, @Id, @GeneratedValue
    
    Relationship Annotations: @OneToMany, @ManyToOne, @OneToOne
    
    Fetch Strategies: Lazy loading by default, eager loading where needed
    
    Cascade Operations: CascadeType.ALL for dependent entities
    
    Query Methods: JPQL queries in repositories


================================================================================
3. DESIGN ARTIFACTS
================================================================================

3.1 Class Diagram

PlantUML Source: docs/diagrams/ClassDiagram.puml
Generated Image: docs/diagrams/images/ClassDiagram.png

[Class Diagram Image: docs/diagrams/images/ClassDiagram.png]

The class diagram shows the complete structure of the system including:
    • Entity models with their attributes and relationships
    • Service classes and their dependencies
    • Repository classes for data access
    • Controller classes for UI handling
    • Design pattern implementations (Strategy, Observer, Decorator)

Key Classes:

Controllers:
    • KioskController - Handles guest self-service booking flow
    • AdminDashboardController - Main admin interface
    • AdminReservationController - Reservation management
    • AdminPaymentController - Payment processing
    • AdminCheckoutController - Checkout management
    • AdminDiscountController - Discount application
    • AdminFeedbackController - Feedback management
    • AdminWaitlistController - Waitlist management
    • ReportController - Report generation and export
    • UnifiedLoginController - Role-based authentication
    • KioskPaymentController - Guest payment processing
    • KioskWelcomeController - Kiosk welcome screen
    • KioskHelpController - Kiosk help screen
    • CheckBookingController - Booking status checking
    • FeedbackController - Guest feedback submission
    • LoyaltyController - Loyalty program management
    • CustomerRegistrationController - Guest registration
    • CustomerKioskDashboardController - Customer dashboard
    • BrowseServicesController - Service browsing
    • BaseController - Base controller with common functionality
    • BasePaymentController - Base payment controller

Services:
    • ReservationService - Core reservation business logic
    • AdminReservationService - Admin-specific reservation operations
    • BillingService - Billing calculations and payment processing
    • LoyaltyService - Loyalty points management
    • WaitlistService - Waitlist management with observer pattern
    • ReportingService - Report generation
    • FeedbackService - Feedback collection and analysis
    • PricingService - Dynamic pricing calculations
    • AuthService - Authentication and authorization
    • ActivityLogService - Audit logging
    • ReceiptService - PDF receipt generation

Repositories:
    • ReservationRepository - Reservation data access
    • GuestRepository - Guest data access
    • RoomRepository - Room data access
    • BillingRepository - Billing data access
    • PaymentRepository - Payment data access
    • AddonRepository - Service addon data access
    • FeedbackRepository - Feedback data access
    • WaitlistRepository - Waitlist data access
    • AdminUserRepository - Admin user data access
    • AuditLogRepository - Audit log data access
    • AmenityBookingRepository - Amenity booking data access

Models:
    • Reservation - Core reservation entity
    • Guest - Guest information
    • Room - Room details
    • Billing - Billing information
    • Payment - Payment transactions
    • ServiceAddon - Add-on services
    • ReservationAddon - Join entity for reservation-addon relationship
    • ReservationRoom - Join entity for reservation-room relationship
    • Waitlist - Waitlist entries
    • Feedback - Guest feedback
    • AdminUser - Administrator accounts
    • AuditLog - Activity logging
    • Hotel - Hotel information
    • AmenityBooking - Amenity booking records
    • GuestSelectionResult - Guest selection helper model
    • RoomTypeSummary - Room type summary helper model
    • Enums: PaymentMethod, PricingModel, ReservationStatus, Role, RoomStatus, RoomType


3.2 Sequence Diagrams

PlantUML Sources:
    • docs/diagrams/SequenceDiagram_Booking.puml - Complete booking flow
    • docs/diagrams/SequenceDiagram_Payment.puml - Payment processing
    • docs/diagrams/SequenceDiagram_Observer.puml - Observer pattern for waitlist

Booking Flow Sequence

[Booking Sequence Diagram: docs/diagrams/images/SequenceDiagram_Booking.png]

Shows the complete interaction from guest selecting dates through confirmation, including:
    • Guest input validation
    • Guest lookup/creation
    • Room availability checking
    • Reservation creation
    • Billing generation

Payment Processing Sequence

[Payment Sequence Diagram: docs/diagrams/images/SequenceDiagram_Payment.png]

Demonstrates payment processing including:
    • Payment validation
    • Payment entity creation
    • Billing update
    • Loyalty points redemption (if applicable)
    • Transaction management

Observer Pattern Sequence (Waitlist Notification)

[Observer Sequence Diagram: docs/diagrams/images/SequenceDiagram_Observer.png]

Illustrates the Observer pattern implementation:
    • Room availability publication
    • Observer notification
    • Waitlist service processing
    • Admin notification display


3.3 Deployment Diagram

PlantUML Source: docs/diagrams/DeploymentDiagram.puml
Generated Image: docs/diagrams/images/DeploymentDiagram.png

[Deployment Diagram: docs/diagrams/images/DeploymentDiagram.png]

The deployment diagram shows:
    • Desktop application components (Kiosk UI, Admin UI, Feedback UI)
    • Application layer (Controllers, Services, Repositories)
    • ORM layer (JPA/Hibernate)
    • Database (MySQL)
    • File system (logs, PDFs, CSVs)


3.4 Package Diagram

PlantUML Source: docs/diagrams/PackageDiagram.puml
Generated Image: docs/diagrams/images/PackageDiagram.png

[Package Diagram: docs/diagrams/images/PackageDiagram.png]

The package diagram illustrates:
    • Complete package structure of com.hotel
    • Dependencies between packages
    • Organization of classes by layer and responsibility


3.5 Additional Diagrams

Component Diagram

PlantUML Source: docs/diagrams/ComponentDiagram.puml
Generated Image: docs/diagrams/images/ComponentDiagram.png

[Component Diagram: docs/diagrams/images/ComponentDiagram.png]

Shows system architecture components organized by layers.

Activity Diagram

PlantUML Source: docs/diagrams/ActivityDiagram_Booking.puml
Generated Image: docs/diagrams/images/ActivityDiagram_Booking.png

[Activity Diagram: docs/diagrams/images/ActivityDiagram_Booking.png]

Illustrates the booking process flow with decision points and activities.

Use Case Diagram

PlantUML Source: docs/diagrams/UseCaseDiagram.puml
Generated Image: docs/diagrams/images/UseCaseDiagram.png

[Use Case Diagram: docs/diagrams/images/UseCaseDiagram.png]

Shows all system use cases for Guest, Admin, and Manager actors.


3.6 Optional UI Screenshots

Note: UI screenshots can be included here to demonstrate the user interface.
Screenshots would show:
    • Kiosk welcome screen
    • Guest booking flow screens
    • Admin dashboard
    • Reservation management screens
    • Payment processing screens
    • Report generation screens

[UI Screenshots would be inserted here if available]


================================================================================
4. ENTITY AND RELATIONSHIP MAPPING
================================================================================

4.1 List of Entities

1. Reservation - Core reservation entity
2. Guest - Guest information
3. Room - Room details and availability
4. Billing - Billing information for reservations
5. Payment - Payment transactions
6. ServiceAddon - Add-on services (Wi-Fi, breakfast, etc.)
7. ReservationRoom - Join entity for Reservation-Room many-to-many
8. ReservationAddon - Join entity for Reservation-Addon many-to-many
9. Waitlist - Waitlist entries
10. Feedback - Guest feedback
11. AdminUser - Administrator accounts
12. AuditLog - Activity logging
13. Hotel - Hotel information
14. AmenityBooking - Amenity booking records

Enums:
- PaymentMethod - Payment method types (CASH, CARD, POINTS)
- PricingModel - Pricing model types (PER_NIGHT, PER_RESERVATION)
- ReservationStatus - Reservation status (PENDING, CONFIRMED, CANCELLED, CHECKED_OUT)
- Role - User roles (ADMIN, MANAGER)
- RoomStatus - Room status (AVAILABLE, OCCUPIED, MAINTENANCE)
- RoomType - Room types (SINGLE, DOUBLE, DELUXE, PENTHOUSE)


4.2 Relationships and Annotations

Reservation Entity Relationships

The Reservation entity maintains the following relationships:
    • Many-to-One with Guest: Each reservation belongs to one guest
    • One-to-Many with ReservationRooms: A reservation can have multiple rooms
    • One-to-Many with ReservationAddons: A reservation can include multiple add-ons
    • One-to-One with Billing: Each reservation has one billing record
    • One-to-Many with Feedbacks: A reservation can have multiple feedback entries

Key Relationships Summary

Relationship Type: Many-to-One
    Entities: Reservation, Guest
    Cascade: None
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: Reservation, ReservationRoom
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: Reservation, ReservationAddon
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-One
    Entities: Reservation, Billing
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: Reservation, Feedback
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: Billing, Payment
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: Guest, Reservation
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-One
    Entities: Guest, Waitlist
    Cascade: ALL
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: Room, ReservationRoom
    Cascade: None
    Fetch Type: LAZY

Relationship Type: One-to-Many
    Entities: ServiceAddon, ReservationAddon
    Cascade: None
    Fetch Type: LAZY


4.3 Cascade/Fetch/Validation Notes

Cascade Types

    CascadeType.ALL: Used for dependent entities (ReservationRoom, ReservationAddon, Billing, Payment, Feedback)
        • When parent is deleted, children are deleted
        • When parent is persisted, children are persisted
        • Ensures data consistency


Fetch Strategies

    LAZY (Default): Used for most relationships to avoid N+1 query problems
        • Collections loaded only when accessed
        • Better performance for large datasets
    
    EAGER: Used sparingly, only when immediate access is always needed
        • Example: findByIdWithRooms() uses JOIN FETCH for specific queries


Validation Annotations

    @NotNull: Required fields (checkIn, checkOut, guest, etc.)
    
    @Positive: Numeric fields must be positive (numAdults, amounts)
    
    @Min(0): Non-negative values (numChildren)
    
    @Column(nullable = false): Database-level constraints


Special Query Methods

    findByIdWithRooms(): Eagerly fetches rooms and addons separately to avoid MultipleBagFetchException
        • Uses two separate queries to fetch collections safely
        • First query fetches reservation with rooms
        • Second query fetches reservation with addons
        • Hibernate merges the results in the same persistence context


================================================================================
5. PATTERN USAGE
================================================================================

5.1 Strategy Pattern (Where and How Used)

Purpose: Different billing calculation strategies without changing service code

Location: com.hotel.service.strategy

Where It's Used:
    • BillingService.java - Main service that uses billing strategies
    • Strategy selection based on billing context (standard, discount, or loyalty)

How It's Implemented:
    The Strategy pattern is implemented through a BillingStrategy interface with three concrete implementations:
    • StandardBillingStrategy - Basic billing calculations without discounts or loyalty points
    • DiscountBillingStrategy - Applies role-based discounts (Admin: 15%, Manager: 30%)
    • LoyaltyBillingStrategy - Handles loyalty point redemptions (100 points = 1% discount, max 20%)

How It Works:
    1. BillingService receives a billing request with context (discount, loyalty points, etc.)
    2. Service selects the appropriate strategy based on billing attributes:
       - If loyalty points redeemed > 0 → LoyaltyBillingStrategy
       - If discount value > 0 → DiscountBillingStrategy
       - Otherwise → StandardBillingStrategy
    3. Selected strategy calculates the total amount using its specific algorithm
    4. Result is returned to the service for persistence

Code Example:
```java
// In BillingService
BillingStrategy strategy;
if (loyaltyRedeemedPoints > 0) {
    strategy = new LoyaltyBillingStrategy(loyaltyPolicy);
} else if (discountValue > 0) {
    strategy = new DiscountBillingStrategy(discountPolicy);
} else {
    strategy = new StandardBillingStrategy();
}
double total = strategy.calculateTotal(subtotal, tax);
```

Why I Chose This Pattern:
    • Open/Closed Principle: Can add new billing strategies without modifying existing code
    • Single Responsibility: Each strategy handles one calculation method
    • Easy testing: Test each strategy independently
    • Flexible: Easy to add new pricing models in the future


5.2 Observer Pattern (Where and How Used)

Purpose: Notify waitlist when rooms become available

Location: com.hotel.events

Where It's Used:
    • ReservationService.checkoutReservation() - Publishes room availability events
    • WaitlistService - Subscribes to room availability notifications
    • AdminWaitlistController - Displays notifications to administrators

How It's Implemented:
    The Observer pattern is implemented through:
    • Subject interface - Defines methods for attaching, detaching, and notifying observers
    • Observer interface - Defines the update method that observers must implement
    • RoomAvailabilityPublisher (Concrete Subject) - Maintains list of observers and publishes events
    • WaitlistSubscriber (Concrete Observer) - Receives notifications and updates waitlist service

How It Works:
    1. When a reservation is checked out, ReservationService.checkoutReservation() is called
    2. After freeing the room, the service calls: roomAvailabilityPublisher.publishRoomAvailable(room)
    3. RoomAvailabilityPublisher iterates through all registered observers
    4. WaitlistSubscriber.update() is called with room availability information
    5. WaitlistSubscriber checks if any waitlist entries match the available room
    6. If matches found, notification is added to the waitlist notification queue
    7. AdminWaitlistController displays the notification when admin views waitlist screen

Code Example:
```java
// In ReservationService
roomAvailabilityPublisher.publishRoomAvailable(room);

// In RoomAvailabilityPublisher
public void publishRoomAvailable(Room room) {
    for (Observer observer : observers) {
        observer.update("Room " + room.getRoomNumber() + " is now available");
    }
}

// In WaitlistSubscriber
public void update(String message) {
    // Check waitlist and notify admin
    waitlistService.checkWaitlistForAvailableRooms();
}
```

Why I Chose This Pattern:
    • Decoupling: Room availability logic completely separate from waitlist logic
    • Extensibility: Easy to add new observers (e.g., email notifications, SMS alerts)
    • Loose coupling: Publisher doesn't know about specific subscribers
    • Follows Open/Closed Principle: Can add new notification types without modifying existing code


5.3 Factory Pattern (Where and How Used)

Purpose: Create Room instances with configured attributes

Location: com.hotel.util.RoomFactory

Where It's Used:
    • SeedData.java - Creating initial test data for the database
    • Room creation with consistent configuration

How It's Implemented:
    The Factory pattern is implemented through a static factory method createRoom() that:
    • Takes room type (RoomType enum), room number (String), and base price (double) as parameters
    • Creates a new Room instance
    • Sets all attributes including status to AVAILABLE
    • Returns the fully configured room object

How It Works:
    1. Factory method receives room creation parameters
    2. Creates new Room instance
    3. Sets room type, room number, base price
    4. Initializes status to AVAILABLE
    5. Returns configured room ready for persistence

Code Example:
```java
// In RoomFactory
public static Room createRoom(RoomType roomType, String roomNumber, double basePrice) {
    Room room = new Room();
    room.setRoomType(roomType);
    room.setRoomNumber(roomNumber);
    room.setBasePrice(basePrice);
    room.setStatus(RoomStatus.AVAILABLE);
    return room;
}

// Usage in SeedData
Room room101 = RoomFactory.createRoom(RoomType.SINGLE, "101", 100.0);
```

Why I Chose This Pattern:
    • Centralized creation logic - All room creation in one place
    • Consistent object initialization - Ensures all rooms are created with proper defaults
    • Easy to modify creation process - Changes to room initialization only need to be made in one place
    • Reduces code duplication - Avoids repeating room setup code throughout the application


5.4 Decorator Pattern (Where and How Used)

Purpose: Dynamically add services (add-ons) to booking pricing

Location: com.hotel.service.decorator

Where It's Used:
    • KioskController - Booking summary calculation
    • KioskBookingSummaryHelper - Displaying booking breakdown
    • Dynamic pricing calculation for reservations with add-ons

How It's Implemented:
    The Decorator pattern is implemented through:
    • BookingComponent (abstract base class) - Defines the interface with getPrice() method
    • RoomBookingComponent (concrete component) - Represents the base room booking price
    • AddOnDecorator (abstract decorator) - Wraps BookingComponent and adds add-on price
    • CombinedBookingComponent - Handles multiple add-ons by wrapping decorators

How It Works:
    1. Start with RoomBookingComponent containing base room price
    2. For each selected add-on, wrap the current component with AddOnDecorator
    3. Each decorator adds its price to the wrapped component's price
    4. Final price is calculated by calling getPrice() on the outermost decorator
    5. Decorators can handle different pricing models (PER_NIGHT vs PER_RESERVATION)

Code Example:
```java
// Base component
BookingComponent booking = new RoomBookingComponent(roomPrice);

// Add decorators for each add-on
if (wifiSelected) {
    booking = new AddOnDecorator(booking, wifiAddon);
}
if (breakfastSelected) {
    booking = new AddOnDecorator(booking, breakfastAddon);
}

// Calculate total
double totalPrice = booking.getPrice();
```

Why I Chose This Pattern:
    • Dynamic composition: Can add services at runtime based on user selection
    • Flexible pricing: Each add-on can have different pricing model (per night vs per reservation)
    • Single Responsibility: Each decorator handles one add-on type
    • Open/Closed Principle: Can add new add-on types without modifying existing code
    • Maintains object composition over inheritance


5.5 Singleton Pattern (Where and How Used)

Purpose: Ensure single instance of critical services

Location:
    • com.hotel.util.LoggerService - Logging service singleton
    • EntityManagerFactory in AppConfig - Database connection factory singleton

Where It's Used:
    • LoggerService.getInstance() - Called from all services and controllers for logging
    • AppConfig.initialize() - Creates single EntityManagerFactory for the application
    • Throughout the application - Any class that needs logging or database access

How It's Implemented:
    LoggerService uses double-checked locking pattern:
    • Private static instance variable
    • Private constructor to prevent direct instantiation
    • Public static getInstance() method with synchronized block
    • Double-checked locking ensures thread safety and efficiency

    EntityManagerFactory:
    • Created once in AppConfig.initialize() static method
    • Stored as private static variable
    • Accessed via getter method throughout application lifecycle

How It Works:
    1. First call to getInstance() creates the singleton instance
    2. Subsequent calls return the same instance
    3. Thread-safe: Multiple threads can safely access the singleton
    4. Lazy initialization: Instance created only when first needed

Code Example:
```java
// LoggerService Singleton
public class LoggerService {
    private static volatile LoggerService instance;
    
    private LoggerService() {
        // Initialize logger configuration
    }
    
    public static LoggerService getInstance() {
        if (instance == null) {
            synchronized (LoggerService.class) {
                if (instance == null) {
                    instance = new LoggerService();
                }
            }
        }
        return instance;
    }
}

// Usage throughout application
LoggerService.getInstance().logInfo("Operation completed");
```

Why I Chose This Pattern:
    • Resource efficiency: Single logger instance, single EntityManagerFactory
    • Consistency: Same logger configuration everywhere, same database connection pool
    • Thread-safe: Double-checked locking ensures safe concurrent access
    • Memory efficiency: Prevents multiple instances of expensive resources
    • Global access point: Easy to access from anywhere in the application


================================================================================
6. BUSINESS RULES
================================================================================

6.1 Occupancy Rules (Enforcement Logic)

Rule: Rooms can only be reserved if they are AVAILABLE

Enforcement Logic:
    • RoomRepository.findAvailableRooms() filters by RoomStatus.AVAILABLE
    • ReservationService.createReservation() checks room availability before booking
    • Room status updated to OCCUPIED when reservation is confirmed
    • Room status updated to AVAILABLE when reservation is checked out or cancelled

How I Enforced This:
    The system validates room availability by checking the room status before allowing reservations.
    Implementation details:
    1. When creating a reservation, ReservationService calls RoomRepository.findAvailableRooms()
    2. This query filters rooms where status = 'AVAILABLE'
    3. If selected room is not in the available list, IllegalStateException is thrown
    4. Upon successful reservation creation, room status is updated to OCCUPIED via RoomRepository.update()
    5. When reservation is checked out or cancelled, ReservationService updates room status back to AVAILABLE
    6. This ensures data integrity - rooms cannot be double-booked

Code Location:
    • ReservationService.createReservation() - Lines checking room availability
    • RoomRepository.findAvailableRooms() - Query filtering by status
    • ReservationService.checkoutReservation() - Room status update on checkout


6.2 Pricing Rules (Enforcement Logic)

Dynamic Pricing:
    • Weekend Multiplier: 1.2x (20% increase) for Saturday and Sunday
    • Weekday Multiplier: 1.0x (normal price) for Monday-Friday
    • Seasonal Pricing: Configurable multipliers for specific date ranges

Enforcement Logic:
    PricingPolicy.calculatePriceForDateRange() enforces pricing rules:
    1. Iterates through each night in the date range
    2. For each night, checks if it falls within a seasonal pricing period
    3. If seasonal period found, applies seasonal multiplier
    4. Otherwise, checks day of week:
       - Saturday or Sunday → applies 1.2x multiplier
       - Monday through Friday → applies 1.0x multiplier
    5. Calculates: nightPrice = basePrice * multiplier
    6. Sums all nightly prices for total reservation price

How I Implemented This:
    • PricingPolicy class contains the pricing calculation logic
    • PricingService uses PricingPolicy to calculate prices
    • Seasonal multipliers are configurable and checked first (highest priority)
    • Weekend/weekday multipliers are fallback defaults
    • All pricing calculations are logged for audit purposes

Example Calculation:
    • Base price: $100/night
    • Weekend (Sat-Sun): $120/night (100 * 1.2)
    • Weekday (Mon-Fri): $100/night (100 * 1.0)
    • 3-night stay (Fri-Sun): $100 + $120 + $120 = $340

Code Location:
    • PricingPolicy.calculatePriceForDateRange() - Main pricing calculation
    • PricingService - Service layer that uses PricingPolicy


6.3 Discount Rules (Enforcement Logic)

Role-Based Discount Caps:
    • Admin: Maximum 15% discount
    • Manager: Maximum 30% discount

Enforcement Logic:
    DiscountPolicy.validateAndCapDiscount() enforces discount limits:
    1. Receives discount percentage and user role as parameters
    2. Determines maximum allowed discount based on role:
       - ADMIN → max 15%
       - MANAGER → max 30%
    3. If requested discount exceeds maximum, caps it to the maximum
    4. Returns validated/capped discount percentage
    5. Discount amount calculated: subtotal * (discountPercent / 100)
    6. Applied to subtotal before tax calculation
    7. All discount applications logged in audit log with actor information

How I Implemented This:
    • DiscountPolicy class contains validation and capping logic
    • BillingService calls DiscountPolicy before applying discounts
    • AdminDiscountController validates role before allowing discount input
    • Discount applications are logged via ActivityLogService
    • Prevents unauthorized discount amounts from being applied

Code Location:
    • DiscountPolicy.validateAndCapDiscount() - Discount validation and capping
    • BillingService.applyDiscount() - Discount application logic
    • AdminDiscountController - UI validation for discount input


6.4 Loyalty Rules (Enforcement Logic)

Points Earning:
    • Rate: 1 point per $10 spent
    • Calculation: pointsEarned = paymentAmount / 10
    • Accumulation: Points added to guest's loyalty balance after payment

Enforcement Logic for Points Earning:
    1. When payment is processed, BillingService calls LoyaltyService.earnPoints()
    2. LoyaltyService calculates: pointsEarned = Math.floor(paymentAmount / 10)
    3. Points are added to guest's loyaltyPoints balance
    4. Guest's loyalty balance is updated in database
    5. Points earning is logged for audit purposes

Points Redemption:
    • Conversion: 100 points = 1% discount
    • Maximum Redemption: 1000 points per reservation (10% discount)
    • Maximum Discount: 20% discount from loyalty points
    • Calculation: discountPercent = min(pointsRedeemed / 100, 20)

Enforcement Logic for Points Redemption:
    1. When loyalty points are redeemed, LoyaltyPolicy.calculateDiscountFromPoints() is called
    2. Calculates discount percentage: discountPercent = pointsRedeemed / 100
    3. Caps discount at maximum: discountPercent = min(discountPercent, 20)
    4. Validates guest has sufficient points balance
    5. Deducts redeemed points from guest's balance
    6. Applies discount to billing using LoyaltyBillingStrategy
    7. Redemption is logged in audit log

How I Implemented This:
    • LoyaltyPolicy contains all loyalty calculation logic
    • LoyaltyService manages points earning and redemption
    • BillingService integrates loyalty redemption into billing
    • Guest entity tracks loyalty points balance
    • All loyalty transactions are logged

Code Location:
    • LoyaltyPolicy.calculateDiscountFromPoints() - Discount calculation from points
    • LoyaltyService.earnPoints() - Points earning logic
    • LoyaltyService.redeemPoints() - Points redemption logic
    • BillingService - Integration of loyalty into billing


6.5 Feedback Rules (Enforcement Logic)

Eligibility:
    • Feedback can only be submitted after reservation is CHECKED_OUT
    • All balances must be fully settled (balanceAmount = 0)
    • One feedback per reservation

Enforcement Logic:
    FeedbackService.submitFeedback() enforces feedback eligibility:
    1. Validates reservation status is CHECKED_OUT
       - If status is not CHECKED_OUT, throws IllegalStateException
    2. Validates billing balance is zero
       - Checks billing.balanceAmount == 0
       - If balance exists, throws IllegalStateException with message
    3. Checks if feedback already exists for reservation
       - Queries FeedbackRepository for existing feedback
       - If found, throws IllegalStateException (one feedback per reservation)
    4. If all validations pass, creates and persists Feedback entity
    5. Performs sentiment analysis on feedback content
    6. Sets sentiment tag (POSITIVE, NEGATIVE, NEUTRAL)
    7. Logs feedback submission in audit log

Sentiment Analysis:
    • Automatic sentiment tagging based on rating and keywords
    • Tags: POSITIVE, NEGATIVE, NEUTRAL
    • Used in feedback reports and analytics

How I Implemented This:
    • FeedbackService contains all validation and submission logic
    • Sentiment analysis performed in FeedbackService based on:
      - Rating value (1-2 = NEGATIVE, 3 = NEUTRAL, 4-5 = POSITIVE)
      - Keyword analysis of comment text
    • Feedback entity stores sentiment tag for reporting
    • All feedback submissions are logged

Code Location:
    • FeedbackService.submitFeedback() - Main feedback submission and validation
    • FeedbackService.analyzeSentiment() - Sentiment analysis logic
    • FeedbackController - UI layer for feedback submission


6.6 Waitlist Rules (Enforcement Logic)

Creation:
    • Guest can be added to waitlist if no rooms available
    • One waitlist entry per guest
    • Waitlist entries linked to preferred room type and dates

Enforcement Logic for Waitlist Creation:
    1. WaitlistService.createWaitlist() validates:
       - Guest does not already have an active waitlist entry
       - If existing waitlist found, throws IllegalStateException
    2. Validates requested dates are in the future
    3. Creates Waitlist entity with guest, room type, and date range
    4. Sets status to PENDING
    5. Persists waitlist entry to database
    6. Logs waitlist creation in audit log

Notification:
    • When room becomes available, Observer pattern notifies waitlist
    • Admin sees notification in waitlist management screen
    • Admin can contact guest to confirm availability

Enforcement Logic for Waitlist Notification:
    1. When ReservationService.checkoutReservation() completes:
       - Room status updated to AVAILABLE
       - RoomAvailabilityPublisher.publishRoomAvailable(room) is called
    2. RoomAvailabilityPublisher notifies all registered observers
    3. WaitlistSubscriber receives notification
    4. WaitlistSubscriber queries WaitlistRepository for matching entries:
       - Room type matches available room
       - Date range overlaps with available dates
    5. If matches found, notification added to waitlist notification queue
    6. AdminWaitlistController displays notifications when admin views screen
    7. Admin can convert waitlist entry to reservation

How I Implemented This:
    • Observer pattern decouples room availability from waitlist management
    • WaitlistService manages waitlist CRUD operations
    • RoomAvailabilityPublisher and WaitlistSubscriber handle notifications
    • AdminWaitlistController displays notifications to administrators
    • All waitlist operations are logged

Code Location:
    • WaitlistService.createWaitlist() - Waitlist creation logic
    • WaitlistService.checkWaitlistForAvailableRooms() - Notification checking
    • RoomAvailabilityPublisher - Observer pattern subject
    • WaitlistSubscriber - Observer pattern observer
    • AdminWaitlistController - UI for waitlist management


6.7 Payment Rules (Enforcement Logic)

Payment Methods:
    • Cash: Direct payment, no additional processing
    • Card: Credit/debit card payment
    • Points: Loyalty points redemption

Enforcement Logic for Payment Processing:
    1. BillingService.processPayment() validates:
       - Payment amount is positive
       - Payment amount does not exceed balance amount
       - Payment method is valid (CASH, CARD, or POINTS)
    2. Creates Payment entity with:
       - Payment method
       - Payment amount
       - Timestamp
       - Link to billing
    3. Updates billing:
       - paidAmount += paymentAmount
       - balanceAmount -= paymentAmount
    4. Updates payment status:
       - If balanceAmount == 0 → status = "PAID"
       - If balanceAmount > 0 and paidAmount > 0 → status = "PARTIAL"
       - If paidAmount == 0 → status = "PENDING"
    5. If payment method is POINTS:
       - Validates guest has sufficient loyalty points
       - Deducts points from guest balance
       - Applies loyalty discount to billing
    6. Persists payment and updated billing
    7. Logs payment in audit log
    8. If payment completes billing, earns loyalty points for guest

Multiple Payments:
    • System allows multiple payments (partial payments)
    • Each payment reduces balance incrementally
    • Payment history tracks all transactions
    • Checkout only allowed when balanceAmount == 0

Refunds:
    • Refunds increase balance amount
    • Refund payments logged with negative amount
    • Payment history shows all transactions

Enforcement Logic for Refunds:
    1. BillingService.processRefund() validates:
       - Refund amount is positive
       - Total refunds do not exceed total payments
    2. Creates Payment entity with negative amount
    3. Updates billing:
       - paidAmount -= refundAmount
       - balanceAmount += refundAmount
    4. Updates payment status based on new balance
    5. If original payment was POINTS, refunds points to guest
    6. Persists refund payment and updated billing
    7. Logs refund in audit log

How I Implemented This:
    • BillingService contains all payment and refund logic
    • Payment entity tracks all transactions (positive and negative)
    • Billing entity maintains running totals (paidAmount, balanceAmount)
    • Payment status automatically calculated based on balance
    • All payment operations are logged for audit
    • Checkout validation ensures balance is zero before allowing checkout

Code Location:
    • BillingService.processPayment() - Payment processing logic
    • BillingService.processRefund() - Refund processing logic
    • BillingService.updatePaymentStatus() - Status calculation
    • AdminPaymentController - UI for payment processing
    • KioskPaymentController - UI for guest payments


================================================================================
7. SECURITY AND LOGGING
================================================================================

7.1 Authentication and Roles

Authentication Flow:
    1. User enters username and password in UnifiedLoginController
    2. AuthService.authenticate() validates credentials
    3. BCrypt verifies password hash
    4. User role retrieved from database
    5. Session created with user information

Password Hashing:
    • Algorithm: BCrypt (via jbcrypt library)
    • Implementation: BCryptPasswordHasher.hash() and BCryptPasswordHasher.verify()
    • Security: Salt automatically generated, one-way hashing

Role-Based Access:
    Admin Role:
        • Full access to all features
        • Discount cap: 15%
        • Can manage all reservations
    
    Manager Role:
        • Full access to all features
        • Discount cap: 30%
        • Can manage all reservations

Email-Based Role Detection:
    • EmailDetector utility class identifies management users by email domain
    • Management domains: @hotel.com, @management.hotel.com, @admin.hotel.com
    • Used for automatic role assignment and access control

Role Enforcement:
    • Controllers check user role before allowing actions
    • Discount application validates role-based caps
    • Audit logs record actor role for all actions


7.2 Logging Configuration

Logger Service:
    • Pattern: Singleton
    • Implementation: LoggerService class
    • Configuration: Java Util Logging with FileHandler

Log Rotation:
    • File Size: 1MB per file
    • File Count: 10 files maximum
    • File Pattern: system_logs.%g.log (where %g is generation number)
    • Append Mode: true (append to existing files)

Log Levels:
    • INFO: General information, successful operations
    • WARNING: Potential issues, validation failures
    • SEVERE: Errors, exceptions, critical failures

Activity Logging:
    • Format: [actor] action - entityType (ID: entityId): message
    • Example: [admin] CHECKOUT - Reservation (ID: 123): Guest checked out successfully
    • Storage: Both file and database (AuditLog entity)

Logging Examples:

Sample Log Entries:

INFO Level - Successful Operations:
```
INFO: [admin] CREATE_RESERVATION - Reservation (ID: 45): Created reservation for guest John Doe, Check-in: 2025-01-15, Check-out: 2025-01-18
INFO: [manager] APPLY_DISCOUNT - Billing (ID: 45): Applied 20% discount to reservation 45
INFO: [admin] PROCESS_PAYMENT - Payment (ID: 67): Processed payment of $450.00 for reservation 45
INFO: [admin] CHECKOUT - Reservation (ID: 45): Guest checked out successfully, Room 101 freed
```

WARNING Level - Validation Failures:
```
WARNING: [admin] CREATE_RESERVATION - Reservation: Room 101 is not available for selected dates
WARNING: [manager] APPLY_DISCOUNT - Billing: Discount of 35% exceeds manager maximum of 30%, capped to 30%
WARNING: [admin] PROCESS_PAYMENT - Payment: Payment amount $500.00 exceeds balance of $450.00
```

SEVERE Level - Errors and Exceptions:
```
SEVERE: [admin] CREATE_RESERVATION - Reservation: Failed to create reservation - Room not available
SEVERE: Exception in BillingService.processPayment(): java.sql.SQLException: Connection timeout
SEVERE: [admin] CHECKOUT - Reservation (ID: 45): Checkout failed - Balance not fully paid
```

Activity Log Database Entries:
```
Timestamp: 2025-01-15 14:30:25
Actor: admin
Action: CREATE_RESERVATION
Entity Type: Reservation
Entity ID: 45
Message: Created reservation for guest John Doe, Check-in: 2025-01-15, Check-out: 2025-01-18

Timestamp: 2025-01-15 15:45:10
Actor: manager
Action: APPLY_DISCOUNT
Entity Type: Billing
Entity ID: 45
Message: Applied 20% discount to reservation 45, Discount amount: $90.00
```

The system logs information messages for successful operations, error messages with exception details for failures, and activity logs with actor, action, entity type, and descriptive messages for all administrative actions.


7.3 Exception Handling

Strategy:
    • Try-catch blocks in all service methods
    • Transaction rollback on exceptions
    • User-friendly error messages via AlertHelper
    • Detailed error logging for debugging

Transaction Management: All service methods use transaction management with try-catch blocks. Transactions are begun at the start, business logic is executed, and if successful, the transaction is committed. If any exception occurs, the transaction is rolled back, the error is logged, and the exception is re-thrown.

Exception Types:
    • IllegalStateException: Business rule violations (e.g., room not available)
    • IllegalArgumentException: Invalid input parameters
    • RuntimeException: Unexpected errors, wrapped and logged

User Feedback:
    • Errors displayed via AlertHelper.showError()
    • Success messages via AlertHelper.showInfo()
    • Validation errors shown inline on forms


================================================================================
8. EXPORT AND REPORTING
================================================================================

8.1 Report Types

Revenue Report
    Purpose: Track revenue by date range
    Data: Reservation count, subtotal, tax, discounts, total
    Filters: Date range (start date, end date)
    Export Formats: CSV, PDF

Occupancy Report
    Purpose: Track room occupancy by room type
    Data: Date, available rooms, occupied rooms, occupancy percentage
    Filters: Date range, room type
    Export Formats: CSV, PDF

Activity Logs Report
    Purpose: Audit trail of administrative actions
    Data: Timestamp, actor, action, entity type, entity ID, message
    Filters: Date range
    Export Formats: CSV, PDF, TXT

Feedback Summary Report
    Purpose: Analyze guest feedback
    Data: Reservation ID, guest name, rating, comment, date, sentiment tag
    Metrics: Total feedback count, average rating
    Export Formats: CSV, PDF


8.2 Export Formats

CSV Export
    Library: Apache Commons CSV 1.10.0
    Implementation: CsvExporter class (com.hotel.util.CsvExporter)
    Features:
        • Headers in first row
        • Comma-separated values
        • Proper escaping of special characters (commas, quotes, newlines)
        • Support for revenue, occupancy, feedback, and activity log exports
    Usage: All report types support CSV export

PDF Export
    Library: Apache PDFBox 2.0.27
    Implementation: PdfExporter class
    Features:
        • Formatted tables
        • Headers and titles
        • Page breaks for long reports
        • Professional layout
    Usage: Revenue, Occupancy, Activity Logs, Feedback Summary reports

TXT Export
    Implementation: TxtExporter class
    Features: Plain text format, tab-separated values
    Usage: Activity Logs report (primary format)

PDF Receipt
    Purpose: Final bill receipt for guests
    Implementation: ReceiptService.generateReceipt()
    Content:
        • Reservation details
        • Guest information
        • Room charges
        • Add-on charges
        • Tax and discounts
        • Payment history
        • Payment summary
    Usage: Generated from checkout screen


8.3 Optional Sample Exports

Note: Sample export files can be included here to demonstrate the export functionality.
Sample exports would show:

    • Sample revenue report CSV - Showing revenue breakdown by period
    • Sample occupancy report PDF - Showing room occupancy statistics
    • Sample activity log TXT - Showing audit trail of administrative actions
    • Sample receipt PDF - Showing final bill for a completed reservation
    • Sample feedback summary CSV - Showing guest feedback with sentiment analysis

[Sample export files would be inserted here if available]


================================================================================
9. CHALLENGES AND LEARNINGS
================================================================================

9.1 Technical Challenges

Challenge 1: MultipleBagFetchException in Hibernate

Problem: When trying to eagerly fetch multiple @OneToMany collections (reservationRooms and reservationAddons) in a single query, Hibernate threw MultipleBagFetchException.

Solution: Split the query into two separate queries:
    1. First query fetches reservation with rooms
    2. Second query fetches reservation with addons
    3. Hibernate merges the results in the same persistence context

Learning:
    • Hibernate has limitations on fetching multiple collections
    • Understanding ORM behavior is crucial for performance
    • Sometimes multiple queries are better than one complex query


Challenge 2: State Management in Kiosk Flow

Problem: KioskController was over 3000 lines, making it difficult to maintain and test. State needed to be preserved across multiple screens.

Solution:
    • Extracted logic into helper classes:
        - KioskGuestDetailsHelper: Guest validation and processing
        - KioskRoomSelectionHelper: Room selection logic
        - KioskAddOnHelper: Add-on calculations
        - KioskBookingSummaryHelper: Summary display
        - KioskStateHelper: State management
        - KioskValidationHelper: Validation logic
    • Created BookingState class to transfer state between screens
    • Reduced controller to ~1882 lines

Learning:
    • Helper classes improve code organization
    • State management is critical for multi-screen flows
    • Refactoring large classes improves maintainability


Challenge 3: PDF Generation Font API Compatibility

Problem: PDFBox 2.0.27 doesn't have public Standard14Fonts class, causing compilation errors.

Solution: Used the older font API: PDType1Font.HELVETICA_BOLD instead of new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD).

Learning:
    • Library version compatibility is important
    • Older APIs may be more stable
    • Always check library documentation for correct usage


Challenge 4: Checkout Status Validation

Problem: System prevented checkout if reservation was already CHECKED_OUT, but users needed to recalculate billing for already-checked-out reservations.

Solution: Modified checkoutReservation() to allow billing recalculation even if already checked out:
    • Detect if this is a billing recalculation scenario
    • Skip status change and room freeing if already checked out
    • Still allow billing recalculation

Learning:
    • Business rules need to accommodate edge cases
    • User workflow should drive technical decisions
    • Flexibility in status transitions is important


9.2 Personal Reflections

What Worked Well

    1. 3-Tier Architecture: Clear separation made development and debugging easier
    
    2. Design Patterns: Strategy, Observer, Decorator patterns made code flexible and extensible
    
    3. Helper Classes: Breaking down large controllers improved code organization
    
    4. Comprehensive Logging: Made debugging and auditing much easier
    
    5. JPA/Hibernate: ORM simplified database operations significantly


What Could Be Improved

    1. Error Handling: Could implement more specific exception types
    
    2. Testing: Would benefit from unit tests for services and repositories
    
    3. UI/UX: Some screens could be more intuitive with better visual feedback
    
    4. Performance: Some queries could be optimized further
    
    5. Documentation: More inline documentation would help future maintenance


Key Learnings

    1. ORM Understanding: Deep understanding of JPA/Hibernate is essential for performance
    
    2. Design Patterns: Proper pattern usage makes code more maintainable
    
    3. State Management: Critical for multi-screen applications
    
    4. Refactoring: Regular refactoring prevents technical debt
    
    5. User Experience: Business rules should align with user workflows


9.3 Suggestions for Improvement

Short-Term Improvements

    1. Add Unit Tests: Implement JUnit tests for services and repositories
    
    2. Improve Error Messages: More specific, user-friendly error messages
    
    3. Add Input Validation: Client-side validation for better UX
    
    4. Optimize Queries: Review and optimize slow queries
    
    5. Add Loading Indicators: Show progress for long operations


Long-Term Improvements

    1. Web Application: Convert to web-based application for remote access
    
    2. Mobile App: Develop mobile app for guest bookings
    
    3. Real-Time Notifications: WebSocket-based real-time updates
    
    4. Advanced Analytics: Machine learning for pricing optimization
    
    5. Integration: Integrate with payment gateways and booking platforms


Architecture Improvements

    1. Microservices: Break into smaller, independent services
    
    2. Caching: Implement caching layer for frequently accessed data
    
    3. Message Queue: Use message queue for async operations
    
    4. API Layer: Create REST API for external integrations
    
    5. Database Optimization: Index optimization, query tuning


================================================================================
CONCLUSION
================================================================================

This Hotel Reservation System represents my journey from understanding requirements
to implementing a complete, production-ready application. Through this project, I
demonstrated a comprehensive understanding of:

    • 3-tier layered architecture - Applied clear separation of concerns across
      presentation, business, and data layers
    • Design patterns - Successfully implemented Strategy, Observer, Factory,
      Decorator, and Singleton patterns to solve real-world problems
    • JPA/Hibernate ORM - Mapped complex entity relationships with proper
      cascade, fetch, and validation strategies
    • Business rule enforcement - Implemented and enforced all business rules
      with clear validation logic
    • Security - Integrated BCrypt password hashing and role-based access control
    • Comprehensive logging and auditing - Created a complete audit trail for
      all administrative actions
    • Report generation and export - Built flexible reporting with multiple
      export formats (CSV, PDF, TXT)

My Design Decisions:
    • Chose helper classes to reduce controller complexity and improve maintainability
    • Implemented Observer pattern for waitlist notifications to achieve loose coupling
    • Used Strategy pattern for billing calculations to support future extensibility
    • Applied Decorator pattern for dynamic add-on pricing composition
    • Split Hibernate queries to avoid MultipleBagFetchException, demonstrating
      understanding of ORM limitations

Key Learnings:
    • Architecture matters - Proper layering made debugging and maintenance much easier
    • Design patterns solve real problems - Each pattern addressed a specific need
    • ORM requires deep understanding - Performance and correctness depend on
      proper relationship mapping
    • Refactoring is essential - Breaking down large classes improved code quality
    • User experience drives technical decisions - Business rules must align with
      user workflows

The system successfully addresses all requirements while maintaining clean,
maintainable code through proper separation of concerns, design patterns, and
best practices. This project has been a valuable learning experience in building
enterprise-grade applications with Java, JavaFX, and JPA/Hibernate.


End of Documentation





