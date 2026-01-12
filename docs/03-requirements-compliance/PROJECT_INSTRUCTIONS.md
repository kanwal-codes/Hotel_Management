# Project Instructions

This document contains the complete project instructions and requirements.

## 🚀 Getting Started

**New to this project? Start here:**

1. **Read this file** (`PROJECT_INSTRUCTIONS.md`) - Complete requirements
2. **Check `QUICK_START_GUIDE.md`** - Get foundation working in 2 days
3. **Follow `IMPLEMENTATION_ROADMAP.md`** - Step-by-step implementation plan
4. **Track progress** with `PROGRESS_CHECKLIST.md` - Stay organized

**Recommended Reading Order:**
- Pages 1-4: Requirements overview
- `QUICK_START_GUIDE.md`: Get started immediately
- `IMPLEMENTATION_ROADMAP.md`: Full implementation strategy
- Pages 5-12: Detailed requirements and diagrams
- `PROGRESS_CHECKLIST.md`: Track your progress

---

## Instructions

*Please paste your 12 pages of instructions below. You can organize them by page or section as needed.*

---

## Page 1

**Project : Hotel Reservation System**

**Due Date: December 3rd, 2025**

### Introduction

Hotel (Choose an appropriate name) is one of the famous tourist hotels in (Choose an appropriate name) city. But this hotel's current reservation is based on a manual system. When guests come in to make a reservation, their details are recorded in a file and then those files are stored in special cabinets. Also, the billing system is manual too.

As the current system is manual based, the management of the hotel must put extra efforts to keep the data secured. Records can easily be destroyed in case of fire, disaster or even possible to be stolen. Additionally, storing files requires extra cabinet space, and searching for a record is difficult, resulting in significant manpower hours. As well as the billing system, the system is manually handled so having an error in calculation is also at high risk. The management is also looking for a way to get the customer feedback after the stay. Since the pandemic hits now the management is also looking to implement 2 kiosks as well, so the guests can book their room with no contact or interaction with anyone.

We have decided to develop their Reservation system as a computer-based system and hotel can give quick service to the guests.

### Scope and outcomes

• **Goal:** You will build a desktop-only reservation and billing system that replaces manual processes and models real-world hotel operations with clear, maintainable architecture.

• **Architecture:** You will use MVC for presentation logic, a service layer for business rules, repositories powered by an ORM for persistence, and DI for wiring. You will apply Singleton, Strategy, Observer, Factory, and Decorator patterns where appropriate.

• **Deliverables:** You will submit project documentation, design artifacts, a working application, export files, logs, ORM configuration, database scripts, and a reflection on your challenges and learnings.

• **Constraints:** You will not build any web components or use charts. All reports must be shown as tables and exportable to CSV, PDF, or TXT.

---

## Page 2

### Architecture Tiers

This project must follow a 3-tier architecture:

• **Presentation tier:**
  - JavaFX UI (kiosk, admin, feedback) with controllers and FXML views.
  - This tier collects input, validates at the UI level, and displays results in tables and forms.

• **Application/Business tier:**
  - Services implementing business rules, applying patterns, and orchestrating workflows.
  - This tier enforces occupancy rules, pricing, discounts, loyalty, and waitlist notifications.

• **Data tier:**
  - ORM-backed repositories and the relational database.
  - This tier manages persistence, queries, and transactions.

In addition, cross-cutting concerns such as logging, security, and configuration must be applied consistently across all tiers.

### Functional requirements

#### Kiosk (self-service)

**Welcome flow:**
- The kiosk must display a brief, friendly welcome message and an optional short instructional video/ or gif.
- The rules and regulations button must always remain visible and accessible during the flow (like a navigation on the side).
- The interface must guide the user through a clear, step-by-step journey from arrival to confirmation.

**Booking steps:**
- The kiosk must ask for the number of adults and children before continuing.
- It must then ask for check-in and check-out dates and validate them immediately.
- Based on occupancy rules and room availability, the kiosk must either suggest a room plan and allow the user to adjust choices OR allow the user to choose their own type of rooms and quantity.
- Indicate the guest to check the rooms booking policy if user decide to choose their own type of room and quantity.
- The kiosk must collect guest details with visible required-field indicators and inline validation messages for each incorrect field.
- The kiosk must let the guest select add-on services such as Wi-Fi, breakfast, parking, and spa and must show the price impact for each selection.
- Before confirmation, the kiosk must present a complete estimate including subtotal, tax, add-ons and any loyalty effects.
- After confirmation, the kiosk must save the reservation and clearly inform the guest that billing will be handled at the front desk.

**Validation:**
- The kiosk must enforce occupancy limits per room type across all steps.
- It must accept a single-person booking without errors.
- It must reject any invalid combinations and must display clear, actionable error messages to the user.

---

## Page 3

### Admin module

#### Authentication:
- The system must support multiple administrator accounts with role-based access (Admin and Manager).
- All passwords must be hashed with BCrypt before storage.
- The login process must provide success and failure feedback and must log all events.

#### Dashboard:
- Administrators must be able to search for guests and reservations by name, phone, date range, status, and other relevant filters.
- The dashboard must show results in paginated tables with sortable columns and must allow opening detailed views for editing.
- **Reservations:** Administrators must be able to create (via phone), modify, and cancel reservations while performing conflict checks against existing bookings.
- The system must support group bookings where a single reservation can include multiple rooms, and it must maintain a unified bill for the group.
- **Payments:** Administrators must be able to process payments using cash, card, or loyalty points.
- The system must support deposits at booking time, partial payments during the stay, and refunds when required.
- The system must track paid and outstanding balances and must prevent checkout while a balance remains.
- The system must offer loyalty program to the guest and if guest wants to enrolled for it then use the user information which is filled already confirm it with the guest and issue a loyalty number.

#### Discounts:
- Administrators must be able to apply discounts with role-based caps, where Admin can apply up to 15% and Manager can apply up to 30%.
- The system must prevent discounts that exceed configured limits and must record who applied each discount.

#### Checkout:
- Administrators must be able to generate the final bill, settle the balance, and mark the rooms as available.
- The system must trigger room availability notifications after checkout and must remind the administrator to invite the guest to submit feedback at the kiosk.

#### Waitlist:
- When rooms are unavailable, administrators must be able to add guests to a waitlist with their desired room type and date range.
- The system must notify subscribed administrators when availability changes and must provide a quick conversion from a waitlist entry to a reservation.

#### Feedback management:
- Administrators must be able to view feedback entries only after the guest has checked out.
- The system must provide filters by rating, date, sentiment tag, and guest.
- Administrators must be able to export feedback summaries for analysis.

#### Loyalty:
- Guests must earn loyalty points based on payment amounts, using a configurable earning rate.
- Administrators must be able to redeem points for discounts under defined caps.
- The system must provide a loyalty dashboard showing balances, earning history, and redemption activity.

---

## Page 4

### Feedback (guest)

#### Submission:
- Guests must be able to submit a star rating from one to five along with comments after checkout.
- The system must store the feedback linked to both the reservation and the guest and must show a confirmation after submission.

### Reporting (tabular only)

#### Revenue reports:
- The system must provide revenue summaries by day, week, and month.
- Each summary must include reservation counts, subtotal, tax, discounts, and total amounts.
- Reports must be displayed in tables and must be exportable to CSV or PDF.

#### Occupancy reports:
- The system must provide occupancy tables for daily, weekly, and monthly views.
- Each table must include rooms available, rooms occupied, and occupancy percentage as a numeric value only. Reports must be exportable to CSV or PDF.

#### Activity logs:
- The system must provide a table of administrative activity that includes timestamp, actor, action, entity type, entity identifier, and message.
- The system must support export to CSV and TXT.

### Business rules

#### Occupancy limits:
- A single room must allow up to two people.
- A double room must allow up to four people.
- Deluxe and penthouse rooms must allow up to two people with higher base prices.
- The system must validate occupancy both per room and across group bookings.

#### Group booking suggestions:
- For groups of three or four adults, the system must suggest either one double room or two single rooms Or let them choose their own type of rooms and quantity.
- For groups larger than four adults, the system must suggest multiple double rooms or a combination of double and single rooms until capacity is satisfied.
- In the case of group booking choosing their own room types and quantity, system must validate the rules of occupancy.

#### Dynamic pricing:
- The system must apply a configurable multiplier for weekends and a separate multiplier for weekdays.
- The system must apply seasonal multipliers for defined date ranges such as peak season.
- The system must price add-ons either per night or per reservation, based on their pricing model.

#### Payments:
- The system must support cash, card, and loyalty point payments.
- The system must allow deposits at booking and must track partial payments during the stay.
- The system must allow refunds as negative payment entries and must adjust totals and logs accordingly.

#### Discounts:
- The system must enforce role-based discount caps and must prevent exceeding the configured limits.
- The system must cap loyalty points redemption per reservation and must apply discounts before loyalty redemption where required by the strategy.

#### Loyalty:
- The system must earn points per paid amount using a configurable rate.
- The system must redeem points into discounts using the loyalty strategy and must respect configured redemption caps.
- The system must maintain accurate point balances with audit trails for earning and redemption.

#### Feedback eligibility:
- The system must allow feedback submission only after the reservation has been checked out and any balances have been fully settled.

---

## Page 5

### Architecture and patterns

#### Layers and packages:
- The project must organize code into app (bootstrap and DI), config (pricing and policy), controller (JavaFX), view (FXML and CSS), model (entities and enums), service (business logic), repository (ORM-backed persistence), security (authentication and roles), util (logging and exporters), and events (observer components).

#### ORM and lifecycle:
- Entities must be annotated with JPA, including identifiers and relationships.
- The EntityManagerFactory must be created once and treated as a singleton or DI-managed singleton.
- The EntityManager must be created per transaction or unit of work and must not be shared across threads.

#### Repository abstraction:
- Repositories must expose clear interfaces and use JPA queries or criteria for persistence operations.
- Services must depend on repositories, not on ORM APIs directly.

#### Dependency injection:
- Controllers, services, and repositories must use constructor injection.
- A central configuration class must wire dependencies and provide singletons where appropriate.

#### Required patterns:
- **Strategy** must be used for billing calculations including standard, discount, and loyalty strategies.
- **Observer** must be used to notify administrators when room availability changes.
- **Factory** must be used to create room instances with configured attributes.
- **Decorator** must be used to add services such as spa, breakfast, Wi-Fi, and parking to booking pricing.

### Logging, security, and validation

#### Activity logging:
- The application must record administrator actions including logins, searches, reservation changes, checkouts, cancellations, discounts, payments, refunds, and feedback submissions.
- Each log entry must include a timestamp, actor, action, entity type, entity identifier, and a descriptive message.

#### Logger configuration:
- The application must use java.util.logging or Log4j with a rotating file handler.
- Each log file must be limited to approximately one megabyte, and the system must retain up to ten files before rotating.
- Students should configure logging to store logs in a separate log file.
- Consider implementing log rotation to avoid excessive file size growth.

**Example configuration and rotation (using FileHandler):**
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

- "system_logs.%g.log" is a pattern where %g is a placeholder for the generation number of the log file.
- 1024 * 1024 sets the limit to 1MB for each log file.
- 10 specifies the maximum number of log files to keep.
- This setup will create a new log file after the current file reaches 1MB and will maintain up to 10 log files. Anything beyond that will overwrite the oldest log file.

#### Exception logging:
- The application must log validation failures, persistence errors, and unexpected exceptions at appropriate levels.
- Severe issues must include stack traces for troubleshooting.

---

## Page 6

### Authentication and authorization:
- The application must store only BCrypt-hashed passwords and must perform role checks for sensitive actions such as discounts, refunds, reporting, and user management.

### Validation rules:
- The application must validate guest names, phone numbers, and email addresses with clear messages.
- The application must validate date ranges with minimums and must check for overlaps.
- The application must validate occupancy distribution across group bookings.
- The application must validate payment amounts and must prevent negative balances.
- The application must validate discounts within configured caps and must enforce non-negative values.
- The application must validate feedback ratings and must cap comment length.

### Reporting specifications

#### Revenue reports:
- The system must show period, reservation count, subtotal, tax, discounts, and total for day, week, and month views.
- The system must support filtering by date range and room type and must export to CSV or PDF.

#### Occupancy reports:
- The system must show date, rooms available, rooms occupied, and occupancy percentage in numeric form.
- The system must support filtering by date range and room type and must export to CSV and PDF.

#### Feedback summary:
- The system must show reservation identifier, guest, rating, comment, date, and sentiment tag.
- The system must display the average rating and counts for common issue tags.
- The system must export to CSV.

#### Activity logs:
- The system must show timestamp, actor, action, entity type, entity identifier, and message.
- The system must read from the log file or the audit table and must export to CSV or TXT.

---

## Page 7

#### Activity logs:
- The system must show timestamp, actor, action, entity type, entity identifier, and message.
- The system must read from the log file or the audit table and must export to CSV or TXT.

### Milestone 1: November 12th, 2025 (5%)

- The first deadline is a check point in which everyone must participate. It will be a short 3 to 5 minutes meeting in which you will present your final front-end designs, class diagrams, UML etc. or any other diagram that can support your project structure, data flow (Final Screen Shots of all the front ends).
- Separate submission will be open where you need to submit all UML, front end screen shots, database designs, or any other design diagrams that you have shown in the lab. (Only those submissions will be accepted which are approved during the lab time).
- Attendance is mandatory for this milestone.

**Note:** Students can show their design in earlier labs as well.

### Final Submission: December 3rd, 2025 (13%)

- The second deadline is your final submission in which you will record a video to show the full working project that you have prepared with an explanation of your challenges only faced. The video must be minimum 7 - 10 minutes but you can use more time to explain if you want. There is no deduction for extra minutes of video.
- Submit your full project.
- Submit your database scripts (if you are using sql, mysql - must).
- **Reflection must include:**
  - Challenges you faced and finding the solutions.
  - Learnings during the project.

---

## Page 8

### Project Documentation

#### Purpose of Your Documentation

This is not just a summary of the project instructions — it's your opportunity to explain how you understood, designed, and implemented the system. Your documentation should reflect your decisions, challenges, and learning journey. It should include your own diagrams, explanations of how you applied patterns and business rules, and reflections on what worked well and what you'd improve. Think of it as a professional walkthrough of your work, showing how you brought the project to life.

#### Documentation Checklist

1. **Project Overview**
   - [ ] Summary of system and purpose
   - [ ] Key features
   - [ ] Technologies used

2. **Architecture Summary**
   - [ ] Description of 3-tier architecture
   - [ ] Cross-cutting concerns
   - [ ] MVC, DI, ORM usage

3. **Design Artifacts**
   - [ ] Class diagram
   - [ ] Sequence diagrams
   - [ ] Deployment diagram
   - [ ] Package diagram
   - [ ] Optional UI screenshots

4. **Entity and Relationship Mapping**
   - [ ] List of entities
   - [ ] Relationships and annotations
   - [ ] Cascade/fetch/validation notes

5. **Pattern Usage**
   - [ ] Strategy, Observer, Factory, Decorator, Singleton
   - [ ] Where and how each is used

6. **Business Rules**
   - [ ] Occupancy, pricing, discounts, loyalty, feedback
   - [ ] Enforcement logic

7. **Security and Logging**
   - [ ] Authentication and roles
   - [ ] Logging configuration and samples
   - [ ] Exception handling

8. **Export and Reporting**
   - [ ] Report types and formats
   - [ ] Optional sample exports

9. **Challenges and Learnings**
   - [ ] Technical challenges
   - [ ] Personal reflections
   - [ ] Suggestions for improvement

**Submit one PDF or DOCX file named `ProjectDocumentation_.pdf`**

### Grading Rubric

• **Design & Architecture (20%)**
  - MVC separation, DI, package structure, UML diagrams

• **Patterns & Principles (15%)**
  - Strategy, Observer, Factory, Decorator, Singleton; OO principles

• **ORM & Persistence (15%)**
  - JPA annotations, relationships, EMF/EM usage, queries

• **Functionality – Kiosk (10%)**
  - Booking flow, validation, dynamic pricing

• **Functionality – Admin (15%)**
  - Login, search, modify, payments, checkout, notifications

• **Functionality – Waitlist & Loyalty (10%)**
  - Waitlist creation, observer notifications, loyalty dashboard

• **Reporting & Feedback (10%)**
  - Tabular reports, export formats, feedback flow

• **Logging & Security (5%)**
  - Logger rotation, audit logs, exception handling, BCrypt

---

## Page 9

**Passing threshold:** Minimum 50% overall, with working kiosk booking, admin login, and ORM persistence

**Note:** Every Wednesday lab students can approach and discuss the design of the project, their progress and if there are any issues.

### Optional Requirement: Implementing a Multithreaded Server for Admin Access

In a real-world hotel reservation system, multiple admins might need to log in and manage bookings at the same time. To simulate this, you must implement a multithreaded server that allows multiple admin sessions to run simultaneously.

#### What This Means:

• The system should allow at least two admins to log in and use the system at the same time via separate command prompt windows.

• Each admin should be able to search guests, modify reservations, process checkouts, and apply discounts independently.

• The server should handle multiple admin requests without conflicts or crashes.

#### How to Achieve This?

1. **Use a Multi-Threaded Server Approach**
   - The system should have a server-side application that listens for admin connections.
   - When an admin logs in, the server should create a new thread to handle that admin's session.
   - **Example:** This ensures that multiple admins can work independently without affecting each other.

2. **Client-Server Communication**
   - The admin interface should act as a client, sending requests to the server.
   - The server will process requests and respond to each admin individually.

3. **Testing the Multithreaded Functionality**
   - Open two command prompt windows and start an admin session in each.
   - Verify that both admins can log in, search for guests, and process reservations simultaneously.

---

## Page 10

# ✅ **What This Diagram Represents**

This is a **3-tier layered architecture diagram** for your **Hotel Reservation System**.

It visually shows how the entire system is divided across:

### ✔ User Desktop Layer

### ✔ Application Layer

### ✔ Persistence Layer

### ✔ File System Layer

And how these layers communicate.

---

# 🧍‍♂️ **1. User Desktop Layer (Top Layer)**

This layer represents the **JavaFX interfaces** the user interacts with:

* **Kiosk UI (JavaFX)** – for bookings

* **Admin UI (JavaFX)** – for admin operations

* **Feedback UI (JavaFX)** – for feedback after checkout

Each UI sends user requests down into the *Application Layer*.

---

# ⚙️ **2. Application Layer (Middle / Business Logic Layer)**

This is the **core logic layer** of the system.

### Includes:

#### ✔ **Controllers**

* These are your JavaFX controllers linked to FXML.

* They receive UI events and pass them to services.

#### ✔ **Services**

* Contain all **business rules**:

  reservations, pricing, billing, loyalty, feedback, waitlist, reporting, etc.

### Services depend on:

* **Repositories** (for database access)

* **LoggerService (Singleton)**

* **Auth & Security (BCrypt)**

This layer enforces:

* MVC

* DI

* Business rules

* Pricing, discounts, loyalty

* Logging and authentication flows

Think of it as:

**The "brains" of your application.**

---

# 🗃️ **3. Persistence Layer (Bottom Left)**

This is the **database layer**.

### It includes:

* **EntityManager (per transaction)**

  * created for each service operation

  * ensures safe DB transactions

* **EntityManagerFactory (singleton)**

  * created once at startup by AppConfig

  * supplies EntityManager

* **HotelDB (SQL)**

  * your relational database

Repositories connect **services → ORM → SQL database**.

---

# 📁 **4. File System Layer (Bottom Right)**

This is where the application writes files outside the DB.

### Contains:

#### ✔ Log Files (rotating)

* Generated by `LoggerService`

* Using FileHandler rotation (1 MB, 10 files)

#### ✔ Export Files (CSV/PDF/TXT)

* Revenue reports

* Occupancy reports

* Feedback summaries

* Activity logs

Services generate these files, not the UI.

---

# 🔀 **5. Data Flow Overview (Arrows)**

The arrows show dependencies:

### UI → Controllers → Services

**(User input flows downward into business logic)**

### Services → Repositories → EntityManager → Database

**(Services perform queries & persistence via ORM)**

### Services → LoggerService → Log Files

**(Every action is logged)**

### Services → Export Files

**(Reports are generated)**

### Services ↔ Auth & Security

**(BCrypt, role checks)**

---

# 🌟 **High-Level Summary**

This diagram shows:

* A well-structured **3-tier enterprise architecture**

* UI (JavaFX) layer at the top

* Business logic layer in the middle

* Database & filesystem at the bottom

* Dependency injection, singletons, security, and ORM usage

* Proper separation of concerns

It is **clean, correct, and matches your project requirements exactly**.

---

## Page 11

# ✅ **This is a PACKAGE DIAGRAM for your entire project**

It represents:

### ✔ The full package structure

### ✔ All classes grouped into their packages

### ✔ High-level dependencies between the packages

### ✔ Mapping to your 3-tier architecture

No behaviors, attributes, or methods appear — only class names and arrows showing package-to-package dependencies.

---

# 🗂️ **1. TOP-LEVEL PACKAGE = `com.hotel`**

Inside `com.hotel`, I see multiple sub-packages:

* `app`

* `config`

* `controller`

* `view`

* `model`

* `repository`

* `service`

* `security`

* `util`

* `events`

This matches exactly the architecture requirement of your assignment.

---

# 🧩 **2. `app` package**

Contains:

* `AppConfig`

* `Main`

### What it represents:

Your dependency injection configuration and JavaFX application startup.

---

# ⚙️ **3. `config` package**

Contains:

* `DiscountPolicy`

* `PricingPolicy`

* `LoyaltyPolicy`

### What it represents:

Centralized business rule configuration (dynamic pricing, discount caps, loyalty).

---

# 🎮 **4. `controller` package**

Contains:

* `KioskController`

* `AdminController`

* `FeedbackController`

* `ReportController`

* `LoyaltyController`

### Purpose:

JavaFX controllers (presentation layer).

They connect UI → Services.

---

# 🖼️ **5. `view` package**

Contains:

* `FXMLFiles`

* `CSSStyles`

### Purpose:

References to UI resources (FXML + CSS).

---

# 🧱 **6. `model` package**

Contains all JPA entities and enums:

* `Guest`

* `Room`

* `Reservation`

* `Billing`

* `Payment`

* `Feedback`

* `AdminUser`

* `Waitlist`

* `AuditLog`

* Enums: `RoomType`, `ReservationStatus`, `RoomStatus`, `Role`, `PaymentMethod`, `PricingModel`

### Purpose:

Your domain model & database persistence classes.

---

# 🗄️ **7. `repository` package**

Contains:

* `GuestRepository`

* `RoomRepository`

* `ReservationRepository`

* `BillingRepository`

* `FeedbackRepository`

* `AdminUserRepository`

* `AddonRepository`

* `PaymentRepository`

* `WaitlistRepository`

* `AuditLogRepository`

### Purpose:

ORM (JPA) database access layer.

---

# 🔧 **8. `service` package**

Contains your business layer services:

* `BillingService`

* `ReservationService`

* `PricingService`

* `LoyaltyService`

* `FeedbackService`

* `ReportingService`

* `AuthService`

* `WaitlistService`

* `ActivityLogService`

### Purpose:

Business logic, rules, validation, workflows.

---

# 🔐 **9. `security` package**

Contains:

* `AuthService`

* `BCryptPasswordHasher`

### Purpose:

Handles authentication & password hashing.

---

# 📦 **10. `util` package**

Contains:

* `LoggerService`

* `CsvExporter`

* `PdfExporter`

* `Validator`

### Purpose:

Cross-cutting utilities (logging, exports, validation).

---

# 🔔 **11. `events` package**

Contains:

* `Subject`

* `Observer`

* `RoomAvailabilityPublisher`

* `WaitlistSubscriber`

### Purpose:

Observer pattern for room availability → waitlist notifications.

---

# 🔀 **12. Dependencies shown by arrows**

The arrows show:

* Controllers → Services

* Services → Repositories

* Services → Security

* Services → Util (Logger, Exporters, Validator)

* Services ↔ Events

* AppConfig wiring everything together

This is your clean **3-tier + patterns** architecture exactly as required.

---

# 🧠 **In short:**

This diagram shows a **full architecture view of your system**, perfectly aligned with your assignment's expectations:

* 3-tier architecture

* Clear separation of concerns

* All subsystems visible

* All packages properly grouped

* Uses MVC, DI, ORM, Strategy, Observer, Decorator, Factory as required

---

## Page 12

# ✅ **This image is your COMPLETE CLASS DIAGRAM**

Unlike the package diagram earlier, THIS ONE shows:

### ✔ Classes

### ✔ Attributes

### ✔ Relationships (1..*, 0..1, etc.)

### ✔ Services + their repository dependencies

### ✔ Entities + their associations

### ✔ Strategy Pattern

### ✔ Decorator Pattern

### ✔ Observer Pattern

### ✔ Factory Pattern

### ✔ Enum types

This is your **final, fully detailed class-level model** of the system.

---

# 🧱 1. **MODEL LAYER — Entities with attributes & relationships**

### **RoomType (enum)**

* SINGLE, DOUBLE, DELUXE, PENTHOUSE

### **ReservationStatus (enum)**

* PENDING, CONFIRMED, CANCELLED, CHECKED_OUT

### **RoomStatus (enum)**

* AVAILABLE, OCCUPIED, MAINTENANCE

### **Role (enum)**

* ADMIN, MANAGER

### **PaymentMethod (enum)**

* CASH, CARD, POINTS

### **PricingModel (enum)**

* PER_NIGHT, PER_RESERVATION

---

### **Guest**

Attributes:

* id, name, phone, email, address, loyaltyPoints

Relationships:

* **1 guest → many reservations**

* **1 guest → many feedback entries**

---

### **Reservation**

Attributes:

* id, checkIn, checkOut, numAdults, numChildren, status (ReservationStatus)

Relationships:

* **Reservation → Guest (many-to-one)**

* **Reservation → Feedback (one-to-many)**

* **Reservation → ReservationRoom (1..*)**

* **Reservation → Billing (1 → 1)**

* **Reservation → ReservationAddOn (0..*)**

---

### **Room**

Attributes:

* id, roomNumber, type (RoomType), bedCount, basePrice, status (RoomStatus)

Relationships:

* **Room ↔ ReservationRoom**

* Created through **RoomFactory**

---

### **Billing**

Attributes:

* id, subtotal, taxRate, taxAmount, discountValue, loyaltyRedeemedPoints, totalAmount, paidAmount, balanceAmount, paymentStatus

Relationships:

* **1 Reservation → 1 Billing**

* **Billing → Payments (1..*)**

---

### **Payment**

Attributes:

* id, amount, method (PaymentMethod), createdAt

Relationships:

* **Payment → Billing (many-to-one)**

---

### **Feedback**

Attributes:

* id, rating, comments, sentimentTag, createdAt

Relationships:

* **Feedback → Guest**

* **Feedback → Reservation**

---

### **Waitlist**

Attributes:

* requestedType, dateRangeStart, dateRangeEnd, status

Relationships:

* **Waitlist → Guest**

---

### **AdminUser**

Attributes:

* username, password, role, active

---

### **AuditLog**

Attributes:

* timestamp, actor, action, entityType, entityId, message

---

### **ServiceAddOn**

Attributes:

* name, price, pricingModel

### **ReservationAddOn**

Attributes:

* quantity

Relationships:

* **ReservationAddOn → ServiceAddOn**

* **ReservationAddOn → Reservation**

### **BookingComponent / AddOnDecorator**

This is the **Decorator Pattern**.

---

# ⚙️ 2. **SERVICE LAYER — Services & business logic**

### I see the following services:

* **ReservationService**

* **LoyaltyService**

* **FeedbackService**

* **ReportingService**

* **ActivityLogService**

* **BillingService**

* **AuthService**

* **WaitlistService**

### Every service has arrows pointing to repositories (dependencies):

Example:

* ReservationService → RoomRepository, AddonRepository, GuestRepository, ReservationRepository

* BillingService → BillingRepository, PaymentRepository, BillingStrategy

* ReportingService → FeedbackRepository, AuditLogRepository, BillingRepository

This shows **constructor injection and dependency flow**.

---

# 🧩 3. **STRATEGY PATTERN — Billing strategies**

The diagram shows:

```
BillingStrategy (interface)

   ↑

   ├── StandardBillingStrategy

   ├── DiscountBillingStrategy

   └── LoyaltyBillingStrategy
```

BillingService depends on **BillingStrategy**.

This is exactly what your assignment required.

---

# 🧱 4. **DECORATOR PATTERN — Add-on pricing**

The diagram shows:

```
BookingComponent (interface/abstract)

       ↑

       AddOnDecorator
```

This indicates the pricing decorator for:

* breakfast

* spa

* Wi-Fi

* parking

---

# 🏭 5. **FACTORY PATTERN — RoomFactory**

The diagram shows:

* `RoomFactory` creates `Room` based on type, price, bed count.

This matches your requirement.

---

# 🔔 6. **OBSERVER PATTERN — Waitlist notifications**

I see:

```
Subject (interface)

Observer (interface)

RoomAvailabilityPublisher  → implements Subject

WaitlistSubscriber         → implements Observer
```

Relationships:

* WaitlistService → WaitlistRepository

* RoomAvailabilityPublisher notifies WaitlistSubscriber

This implements:

* "Notify admins when a room becomes available"

---

# 🗄️ 7. **REPOSITORY LAYER**

Repositories visible:

* GuestRepository

* RoomRepository

* ReservationRepository

* AddonRepository

* PaymentRepository

* AuditLogRepository

* WaitlistRepository

* FeedbackRepository

* AdminUserRepository

* BillingRepository

Every repository is connected to exactly the correct services.

---

# ⭐ **FINAL SUMMARY — What I see**

This image is your **full class diagram**, containing:

### ✔ All entities + attributes

### ✔ All relationships (multiplicity + associations)

### ✔ All services & repository dependencies

### ✔ Strategy pattern

### ✔ Decorator pattern

### ✔ Observer pattern

### ✔ Factory pattern

### ✔ All enums

### ✔ Perfect alignment to assignment requirements

There is **nothing missing** at the modeling level.

---

## Sequence Diagram - Multi-Stage Reservation Lifecycle

# ✅ **This is a Multi-Stage Sequence Diagram Showing:**

### ✔ 1) Group Booking at Kiosk

### ✔ 2) Deposit Payment at Admin

### ✔ 3) Partial Payment During Stay

### ✔ 4) Checkout with Discount & Loyalty Redemption

It shows the full lifecycle of a reservation from start → finish.

This is one of your **main sequence diagrams** for your final submission.

---

# 🧍‍♂️🧍‍♀️ **Actors in the Diagram**

* **Guest**

* **Admin**

---

# 🧩 **Objects / Lifelines in the Diagram**

From left to right:

1. **KioskController**

2. **AdminController**

3. **ReservationService**

4. **PricingService**

5. **BillingService**

6. **LoyaltyService**

7. **FeedbackController**

8. **FeedbackService**

9. **Database (ORM)**

Each of these is a lifeline.

---

# 🧪 **1. Group Booking at Kiosk — Sequence**

### Guest → KioskController

* Start booking flow

### KioskController → ReservationService

* Validate guest details & dates

### KioskController → PricingService

* Request room suggestions with dynamic pricing

### PricingService → ReservationService

* Query available rooms

* Return available rooms

### ReservationService → Database (ORM)

* Persist Reservation + ReservationRooms

### ReservationService → KioskController

* Confirmation returned

### KioskController → Guest

* Show confirmation and deposit reminder

---

# 💵 **2. Deposit Payment at Admin**

### Guest → AdminController

* Pay deposit

### AdminController → BillingService

* Create initial bill

### BillingService → Database

* Persist Billing + Payment

### BillingService → AdminController

* Confirmation + updated balance

---

# 💳 **3. Partial Payment During Stay**

### Guest → AdminController

* Make partial payment

### AdminController → BillingService

* Record payment

### BillingService → Database

* Persist payment

### BillingService → AdminController

* Show reduced balance

---

# 🧾 **4. Checkout with Discount & Loyalty Redemption**

### Guest → AdminController

* Initiate checkout

### AdminController → BillingService

* Request final bill

### BillingService → LoyaltyService

* Apply loyalty redemption

### LoyaltyService → Database

* Update guest loyalty points

### BillingService → Database

* Persist bill changes (discount, loyalty applied)

### BillingService → AdminController

* Confirmation

### AdminController → Guest

* Show final receipt

---

# ⭐ **Overall Summary of This Diagram**

This image describes the **entire operational workflow** of your system:

* **Booking** (kiosk)

* **Billing creation**

* **Payments**

* **Dynamic pricing**

* **Loyalty points**

* **Discount application**

* **Checkout process**

* **Database interactions**

This is a **textbook-perfect sequence diagram** for a hotel reservation system.

---

## Sequence Diagram - Waitlist System (Observer Pattern)

# ✅ **This is a Sequence Diagram for the WAITLIST SYSTEM**

It shows the three major stages:

### 1️⃣ Guest Added to Waitlist

### 2️⃣ Room Becomes Available (Observer Pattern)

### 3️⃣ Waitlist Conversion (Auto-create reservation)

This is a required workflow from your assignment under **Waitlist + Observer pattern**.

---

# 🧍‍♂️🧍‍♀️ **Actors**

* **Guest**

* **Admin**

---

# 🧩 **Objects / Lifelines**

From left to right:

1. **AdminController**

2. **WaitlistService**

3. **RoomAvailabilityPublisher**

4. **WaitlistSubscriber**

5. **Database (ORM)**

This matches the observer pattern:

* **Subject = RoomAvailabilityPublisher**

* **Observers = WaitlistSubscriber(s)**

* **Business Logic = WaitlistService, AdminController**

---

# 🟦 **1️⃣ Guest Added to Waitlist — Flow**

Guest tries to book but rooms are unavailable.

### Guest → AdminController

**Request booking (rooms unavailable)**

### AdminController → WaitlistService

**Add guest to waitlist**

### WaitlistService → Database

**Persist Waitlist entry**

### Database → WaitlistService

**Confirmation**

### WaitlistService → AdminController

**Guest added to waitlist**

### AdminController → Guest

**Inform guest they are on waitlist**

---

# 🟧 **2️⃣ Room Becomes Available (Observer Pattern Trigger)**

### Admin → AdminController

**Checkout guest and free room**

### AdminController → RoomAvailabilityPublisher

**Publish RoomAvailable event**

### RoomAvailabilityPublisher → WaitlistSubscriber

**Notify subscribed admins**

### WaitlistSubscriber → AdminController

**Display notification of availability**

This shows the exact implementation of the Observer Pattern:

* **Publisher pushes event**

* **Subscribers are notified**

---

# 🟩 **3️⃣ Waitlist Conversion — Auto-create reservation**

### Admin → AdminController

**Open waitlist notification**

### AdminController → WaitlistService

**Convert waitlist entry to reservation**

### WaitlistService → Database

**Create Reservation & remove Waitlist entry**

### Database → WaitlistService

**Confirmation**

### WaitlistService → AdminController

**Reservation created**

### AdminController → Admin

**Show new reservation details**

---

# ⭐ **What This Diagram Represents Overall**

### ✔ How waitlist entries are created

### ✔ How room-availability events are triggered

### ✔ How admins get auto-notified (Observer Pattern)

### ✔ How waitlist entries convert to actual reservations

### ✔ How the system interacts with the database throughout

This is a **complete end-to-end sequence diagram for the Waitlist Feature**.

It includes:

* AdminController

* WaitlistService

* Publisher & Subscriber

* ORM interactions

* All required communication arrows

* State transitions

This is **exactly what instructors expect** for this scenario.

---

## Notes & Implementation Plan

*Use this section to track implementation progress, decisions, and notes as you work through the project.*

---

## Quick Reference Summary

### Project Overview
- **Project Name:** Hotel Reservation System
- **Due Date:** December 3rd, 2025
- **Milestone 1:** November 12th, 2025 (5%)
- **Final Submission:** December 3rd, 2025 (13%)
- **Passing Threshold:** Minimum 50% overall

### Key Technologies
- **JavaFX** - UI Framework
- **JPA/Hibernate** - ORM for persistence
- **MySQL** - Database
- **BCrypt** - Password hashing
- **Maven** - Build tool
- **Java 17** - Programming language

### Architecture Requirements
- **3-Tier Architecture:** Presentation → Application/Business → Data
- **MVC Pattern:** Controllers, Views, Services
- **Dependency Injection:** Constructor injection
- **ORM:** JPA with EntityManagerFactory (Singleton), EntityManager (per transaction)

### Required Design Patterns
1. **Singleton** - LoggerService, EntityManagerFactory
2. **Strategy** - Billing calculations (Standard, Discount, Loyalty)
3. **Observer** - Room availability notifications → Waitlist
4. **Factory** - Room creation
5. **Decorator** - Add-on services (Wi-Fi, Breakfast, Parking, Spa)

### Package Structure
```
com.hotel
├── app          - Bootstrap & DI (AppConfig, Main)
├── config       - Business policies (PricingPolicy, DiscountPolicy, LoyaltyPolicy)
├── controller   - JavaFX controllers (Kiosk, Admin, Feedback, Report, Loyalty)
├── view         - FXML & CSS files
├── model        - JPA entities & enums
├── repository   - ORM data access layer
├── service      - Business logic layer
├── security     - Authentication & BCrypt
├── util         - Logging, Exporters, Validators
└── events       - Observer pattern components
```

### Core Entities
- Guest, Room, Reservation, Billing, Payment
- Feedback, Waitlist, AdminUser, AuditLog
- ServiceAddon, ReservationAddon, ReservationRoom, Hotel

### Key Business Rules
- **Occupancy:** Single (2), Double (4), Deluxe/Penthouse (2)
- **Discounts:** Admin (15%), Manager (30%)
- **Loyalty:** Configurable earning rate, redemption caps
- **Pricing:** Weekend/weekday multipliers, seasonal pricing
- **Feedback:** Only after checkout and balance settled

### Reporting Requirements
- **Revenue Reports:** Day/Week/Month (CSV/PDF)
- **Occupancy Reports:** Daily/Weekly/Monthly (CSV/PDF)
- **Activity Logs:** Administrative actions (CSV/TXT)
- **Feedback Summary:** Ratings and comments (CSV)

### Logging Configuration
- **Logger:** java.util.logging or Log4j
- **File Size:** 1 MB per file
- **Rotation:** Up to 10 files
- **Format:** system_logs.%g.log

### Related Documentation Files
- `docs/project-overview/PROJECT_BLUEPRINT.md` - Complete architecture and sequence diagrams
- `docs/database/DATABASE_DESIGN.md` - Database schema and ERD
- `docs/ui-design/UI_DESIGN_SPECIFICATIONS.md` - UI requirements
- `docs/milestones/MILESTONE1_SUBMISSION_GUIDE.md` - Milestone 1 requirements

### Implementation Checklist
- [ ] Set up Maven project structure
- [ ] Configure persistence.xml
- [ ] Create database schema
- [ ] Implement model entities with JPA annotations
- [ ] Create repository interfaces
- [ ] Implement service layer with business logic
- [ ] Create JavaFX controllers
- [ ] Design FXML views for Kiosk, Admin, Feedback
- [ ] Implement authentication with BCrypt
- [ ] Set up logging with rotation
- [ ] Implement design patterns (Strategy, Observer, Factory, Decorator, Singleton)
- [ ] Create reporting functionality
- [ ] Implement export functionality (CSV, PDF, TXT)
- [ ] Test all functionality
- [ ] Create documentation
- [ ] Record video demonstration

