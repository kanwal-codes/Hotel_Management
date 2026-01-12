# Progress Checklist - Hotel Reservation System

Use this checklist to track your implementation progress.

---

## 📋 Phase 1: Foundation

### Database Setup
- [ ] MySQL database created (`hotel_db`)
- [ ] `persistence.xml` configured correctly
- [ ] Database connection tested

### Enums
- [ ] `RoomType` (SINGLE, DOUBLE, DELUXE, PENTHOUSE)
- [ ] `ReservationStatus` (PENDING, CONFIRMED, CANCELLED, CHECKED_OUT)
- [ ] `RoomStatus` (AVAILABLE, OCCUPIED, MAINTENANCE)
- [ ] `Role` (ADMIN, MANAGER)
- [ ] `PaymentMethod` (CASH, CARD, POINTS)
- [ ] `PricingModel` (PER_NIGHT, PER_RESERVATION)

### Model Entities
- [ ] `Hotel` entity with JPA annotations
- [ ] `Room` entity with relationships
- [ ] `Guest` entity with validation
- [ ] `AdminUser` entity
- [ ] `Reservation` entity
- [ ] `ReservationRoom` (join table)
- [ ] `Billing` entity
- [ ] `Payment` entity
- [ ] `ServiceAddon` entity
- [ ] `ReservationAddon` entity
- [ ] `Feedback` entity
- [ ] `Waitlist` entity
- [ ] `AuditLog` entity

### Repositories
- [ ] `GuestRepository`
- [ ] `RoomRepository`
- [ ] `ReservationRepository`
- [ ] `BillingRepository`
- [ ] `PaymentRepository`
- [ ] `FeedbackRepository`
- [ ] `AdminUserRepository`
- [ ] `WaitlistRepository`
- [ ] `AddonRepository`
- [ ] `AuditLogRepository`

### Utilities
- [ ] `LoggerService` (Singleton pattern)
- [ ] Logger rotation configured (1MB, 10 files)
- [ ] `BCryptPasswordHasher`
- [ ] `EmailValidator`
- [ ] `PhoneValidator`
- [ ] `DateValidator`
- [ ] `CsvExporter`
- [ ] `PdfExporter`

---

## 📋 Phase 2: Business Logic

### Configuration
- [ ] `PricingPolicy` (weekend/weekday multipliers, seasonal)
- [ ] `DiscountPolicy` (Admin 15%, Manager 30%)
- [ ] `LoyaltyPolicy` (earning rate, redemption caps)

### Design Patterns
- [ ] **Factory Pattern**: `RoomFactory` creates Room instances
- [ ] **Strategy Pattern**: `BillingStrategy` interface + implementations
  - [ ] `StandardBillingStrategy`
  - [ ] `DiscountBillingStrategy`
  - [ ] `LoyaltyBillingStrategy`
- [ ] **Decorator Pattern**: `BookingComponent` + `AddOnDecorator`
- [ ] **Observer Pattern**: 
  - [ ] `Subject` interface
  - [ ] `Observer` interface
  - [ ] `RoomAvailabilityPublisher` (implements Subject)
  - [ ] `WaitlistSubscriber` (implements Observer)

### Services
- [ ] `AuthService` (login, role checking)
- [ ] `PricingService` (dynamic pricing calculations)
- [ ] `ReservationService` (booking, availability, group suggestions)
- [ ] `BillingService` (bills, discounts, uses Strategy pattern)
- [ ] `LoyaltyService` (earn/redeem points)
- [ ] `PaymentService` (process payments, refunds)
- [ ] `WaitlistService` (add to waitlist, convert to reservation)
- [ ] `FeedbackService` (submit feedback, validate eligibility)
- [ ] `ReportingService` (revenue, occupancy, activity logs)
- [ ] `ActivityLogService` (log all actions)

---

## 📋 Phase 3: Dependency Injection

### AppConfig
- [ ] `EntityManagerFactory` created as Singleton
- [ ] `LoggerService` created as Singleton
- [ ] Policies initialized
- [ ] Repositories initialized (with EntityManager per transaction)
- [ ] Services initialized (with constructor injection)
- [ ] Controllers wired with services

---

## 📋 Phase 4: Presentation Layer

### Admin Module
- [ ] `LoginScreen.fxml` + controller logic
- [ ] `Dashboard.fxml` (search, pagination, sorting)
- [ ] `ReservationDetails.fxml` (view/edit reservations)
- [ ] `PaymentProcessing.fxml` (process payments)
- [ ] `CheckoutScreen.fxml` (final bill, checkout)
- [ ] `DiscountApplication.fxml` (apply discounts with role check)
- [ ] `WaitlistManagement.fxml` (view waitlist, convert)
- [ ] `LoyaltyProgram.fxml` (loyalty dashboard, enroll guests)
- [ ] `FeedbackManagement.fxml` (view feedback after checkout)
- [ ] `ReportsScreen.fxml` (revenue, occupancy, activity logs)

### Kiosk Module
- [ ] `WelcomeScreen.fxml` + controller logic
- [ ] `DateSelection.fxml` (date validation)
- [ ] `GuestDetails.fxml` (guest info with validation)
- [ ] `RoomSelection.fxml` (suggestions or custom selection)
- [ ] `AddOnServices.fxml` (Wi-Fi, breakfast, parking, spa)
- [ ] `BookingSummary.fxml` (complete estimate)
- [ ] `ConfirmationScreen.fxml` (confirmation message)

### Feedback Module
- [ ] `FeedbackSubmission.fxml` (rating + comments)
- [ ] `FeedbackConfirmation.fxml` (thank you message)

---

## 📋 Phase 5: Business Rules Implementation

### Occupancy Rules
- [ ] Single room: max 2 people
- [ ] Double room: max 4 people
- [ ] Deluxe/Penthouse: max 2 people
- [ ] Validation per room and across group bookings

### Group Booking Suggestions
- [ ] 3-4 adults: suggest 1 double OR 2 singles
- [ ] >4 adults: suggest multiple rooms
- [ ] Custom selection validation

### Dynamic Pricing
- [ ] Weekend multiplier
- [ ] Weekday multiplier
- [ ] Seasonal multipliers (date ranges)
- [ ] Add-on pricing (per night or per reservation)

### Payment Rules
- [ ] Cash, card, loyalty points supported
- [ ] Deposits at booking
- [ ] Partial payments during stay
- [ ] Refunds as negative payments
- [ ] Balance tracking
- [ ] Prevent checkout with balance

### Discount Rules
- [ ] Admin: max 15%
- [ ] Manager: max 30%
- [ ] Prevent exceeding caps
- [ ] Record who applied discount

### Loyalty Rules
- [ ] Points earned per payment (configurable rate)
- [ ] Points redeemed for discounts
- [ ] Redemption caps enforced
- [ ] Balance tracking with audit trail

### Feedback Rules
- [ ] Only after checkout
- [ ] Only when balance = 0
- [ ] Rating 1-5 stars
- [ ] Comment length validation

---

## 📋 Phase 6: Reporting

### Revenue Reports
- [ ] Daily view
- [ ] Weekly view
- [ ] Monthly view
- [ ] Filter by date range
- [ ] Filter by room type
- [ ] Export to CSV
- [ ] Export to PDF

### Occupancy Reports
- [ ] Daily view
- [ ] Weekly view
- [ ] Monthly view
- [ ] Shows: available, occupied, percentage (numeric)
- [ ] Filter by date range
- [ ] Filter by room type
- [ ] Export to CSV
- [ ] Export to PDF

### Activity Logs
- [ ] Shows: timestamp, actor, action, entity type, entity ID, message
- [ ] Read from log file or audit table
- [ ] Export to CSV
- [ ] Export to TXT

### Feedback Summary
- [ ] Shows: reservation ID, guest, rating, comment, date, sentiment
- [ ] Average rating displayed
- [ ] Common issue tag counts
- [ ] Export to CSV

---

## 📋 Phase 7: Testing

### Unit Tests
- [ ] Service tests
- [ ] Repository tests
- [ ] Business rule tests

### Integration Tests
- [ ] Full booking flow (kiosk → payment → checkout)
- [ ] Waitlist → notification → conversion
- [ ] Reporting flows
- [ ] Authentication flows

### Bug Fixes
- [ ] Validation issues fixed
- [ ] UI bugs fixed
- [ ] Database issues fixed

---

## 📋 Phase 8: Documentation

### Code Documentation
- [ ] JavaDoc comments on all classes
- [ ] Design pattern documentation
- [ ] Business rule documentation

### Project Documentation
- [ ] Project overview
- [ ] Architecture summary
- [ ] Class diagram
- [ ] Sequence diagrams
- [ ] Package diagram
- [ ] Deployment diagram
- [ ] Entity relationship mapping
- [ ] Pattern usage explanation
- [ ] Business rules documentation
- [ ] Security and logging documentation
- [ ] Export and reporting documentation
- [ ] Challenges and learnings reflection

### Submission Materials
- [ ] Video recorded (7-10 minutes)
- [ ] Database scripts included
- [ ] All export files included
- [ ] Log files included
- [ ] Project documentation PDF

---

## 📊 Progress Tracking

**Current Phase:** _______________

**Completion %:** _______________

**Last Updated:** _______________

---

## 🎯 Milestone Checkpoints

### Milestone 1 (November 12th, 2025)
- [ ] Front-end designs complete
- [ ] Class diagrams complete
- [ ] UML diagrams complete
- [ ] Database design complete
- [ ] Screenshots of all UIs
- [ ] Presentation prepared (3-5 minutes)

### Final Submission (December 3rd, 2025)
- [ ] All functionality working
- [ ] All tests passing
- [ ] Documentation complete
- [ ] Video recorded
- [ ] Reflection written
- [ ] All files submitted

---

**Keep this checklist updated as you progress! ✅**



