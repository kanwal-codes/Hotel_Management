# Hotel Reservation System - Requirements Notes
## Based on First 5 Pages of Project Specification

### Reading Progress Tracker
- ✅ **Page 1**: Introduction & Project Context (READ IN DETAIL)
- ✅ **Pages 2-5**: Scope, Architecture, Functional Requirements (Kiosk & Admin)
- ✅ **Pages 6-9**: Business Rules, Reporting, Logging, Security, Validation (READ IN DETAIL)
- ✅ **Pages 10-14**: Milestones, Submission, Grading, Optional Requirements (READ IN DETAIL)
- ⏳ **Remaining pages**: Any additional content (TO BE READ)

---

## IMPORTANT REMINDERS
- This is a **class project** - must meet all specified requirements
- All functionality listed here **MUST be implemented**
- Use this file as reference when implementing features
- Cross-check implementation against these requirements

---

## 1. INTRODUCTION & PROJECT CONTEXT (PAGE 1 - DETAILED)

### Hotel Background
- Hotel name: **"Choose an appropriate name"** (student to decide)
- Location: **"Choose an appropriate name" city** (student to decide)
- Status: **One of the famous tourist hotels** in the city
- Current system: **Completely manual-based**

### Current Manual System Problems (PAGE 1 DETAILS)

#### Reservation System Issues
- When guests come to make reservations:
  - Their details are **recorded in files**
  - Files are **stored in special cabinets**
- Problems with this approach:
  - ❌ **Data security risks**: Records can easily be destroyed in case of:
    - Fire
    - Disaster
    - Theft
  - ❌ **Storage space**: Requires extra cabinet space
  - ❌ **Search difficulty**: Searching for a record is difficult
  - ❌ **Time consumption**: Results in significant manpower hours

#### Billing System Issues
- Billing system is **manually handled**
- **High error risk**: Having an error in calculation is at high risk
- No automated calculation or validation

#### Missing Features
- ❌ **No customer feedback mechanism**: Management is looking for a way to get customer feedback after the stay

#### New Pandemic Requirement (PAGE 1)
- **Post-pandemic need**: Management is looking to implement **2 kiosks**
- Purpose: So guests can book their room with **no contact or interaction with anyone**
- This is a **critical new requirement** that must be implemented

### Solution Goal (PAGE 1)
- Develop their Reservation system as a **computer-based system**
- Hotel can give **quick service to the guests**
- Replace all manual processes
- Address all security, storage, and efficiency concerns
- Enable contactless booking via kiosks

### Project Due Date
- **Final Submission**: December 3rd, 2025
- **Milestone 1**: November 12th, 2025 (checkpoint for front-end designs, UML, database designs)

---

## 2. SCOPE AND OUTCOMES

### Goal
Build a **desktop-only** reservation and billing system that:
- Replaces manual processes
- Models real-world hotel operations
- Has clear, maintainable architecture

### Architecture Requirements
- **MVC** for presentation logic
- **Service layer** for business rules
- **Repositories** powered by **ORM** for persistence
- **Dependency Injection (DI)** for wiring
- **Design Patterns**: Singleton, Strategy, Observer, Factory, Decorator

### Deliverables
- Project documentation
- Design artifacts
- Working application
- Export files
- Logs
- ORM configuration
- Database scripts
- Reflection on challenges and learnings

### Constraints
- ❌ **NO web components**
- ❌ **NO charts** (all reports as tables)
- ✅ Reports exportable to **CSV, PDF, or TXT**

---

## 3. ARCHITECTURE TIERS (3-Tier Architecture)

### Presentation Tier
- **JavaFX UI** with three modules:
  - Kiosk interface
  - Admin interface
  - Feedback interface
- **Controllers** and **FXML views**
- Responsibilities:
  - Collect input
  - Validate at UI level
  - Display results in tables and forms

### Application/Business Tier
- **Services** implementing business rules
- Apply design patterns
- Orchestrate workflows
- Responsibilities:
  - Enforce occupancy rules
  - Handle pricing
  - Apply discounts
  - Manage loyalty program
  - Handle waitlist notifications

### Data Tier
- **ORM-backed repositories**
- **Relational database**
- Responsibilities:
  - Manage persistence
  - Handle queries
  - Manage transactions

### Cross-Cutting Concerns
Must be applied consistently across all tiers:
- **Logging**
- **Security**
- **Configuration**

---

## 4. FUNCTIONAL REQUIREMENTS - KIOSK (Self-Service)

### Welcome Flow
- ✅ Display brief, friendly welcome message
- ✅ Optional short instructional video or GIF
- ✅ **Rules and regulations button** must always remain visible and accessible (like navigation on the side)
- ✅ Interface must guide user through clear, step-by-step journey from arrival to confirmation

### Booking Steps (Sequential Flow)
1. **Number of Guests**
   - Ask for number of **adults** and **children**
   - Must ask before continuing

2. **Date Selection**
   - Ask for **check-in** and **check-out dates**
   - **Validate immediately**

3. **Room Selection**
   - Based on occupancy rules and room availability:
     - **Option A**: Suggest a room plan and allow user to adjust choices
     - **Option B**: Allow user to choose their own type of rooms and quantity
   - ⚠️ **IMPORTANT**: If user chooses their own room types and quantity, indicate they should check the rooms booking policy

4. **Guest Details Collection**
   - Collect guest details
   - **Visible required-field indicators**
   - **Inline validation messages** for each incorrect field

5. **Add-On Services**
   - Let guest select add-on services:
     - Wi-Fi
     - Breakfast
     - Parking
     - Spa
   - **Show price impact** for each selection

6. **Confirmation**
   - Before confirmation: Present **complete estimate** including:
     - Subtotal
     - Tax
     - Add-ons
     - Any loyalty effects
   - After confirmation:
     - Save the reservation
     - Clearly inform guest that **billing will be handled at the front desk**

### Validation Requirements (Kiosk)
- ✅ Enforce **occupancy limits per room type** across all steps
- ✅ Accept **single-person booking** without errors
- ✅ Reject **invalid combinations**
- ✅ Display **clear, actionable error messages** to the user

---

## 5. FUNCTIONAL REQUIREMENTS - ADMIN MODULE

### Authentication
- ✅ Support **multiple administrator accounts**
- ✅ **Role-based access**: Admin and Manager roles
- ✅ All passwords must be **hashed with BCrypt** before storage
- ✅ Login process must provide:
   - Success and failure feedback
   - Log all events

### Dashboard
- ✅ Search for guests and reservations by:
   - Name
   - Phone
   - Date range
   - Status
   - Other relevant filters
- ✅ Show results in **paginated tables** with **sortable columns**
- ✅ Allow opening **detailed views for editing**

### Reservations Management
- ✅ **Create** reservations (via phone)
- ✅ **Modify** reservations
- ✅ **Cancel** reservations
- ✅ Perform **conflict checks** against existing bookings
- ✅ Support **group bookings**:
   - Single reservation can include multiple rooms
   - Maintain **unified bill** for the group

### Payments
- ✅ Process payments using:
   - Cash
   - Card
   - Loyalty points
- ✅ Support **deposits at booking time**
- ✅ Support **partial payments during stay**
- ✅ Support **refunds when required**
- ✅ Track **paid and outstanding balances**
- ✅ **Prevent checkout** while balance remains

### Loyalty Program
- ✅ Offer loyalty program to guests
- ✅ If guest wants to enroll:
   - Use user information already filled
   - Confirm with guest
   - Issue a loyalty number

### Discounts
- ✅ Apply discounts with **role-based caps**:
   - **Admin**: Up to **15%**
   - **Manager**: Up to **30%**
- ✅ Prevent discounts that exceed configured limits
- ✅ Record **who applied each discount**

### Checkout
- ✅ Generate **final bill**
- ✅ **Settle the balance**
- ✅ **Mark rooms as available**
- ✅ Trigger **room availability notifications** after checkout
- ✅ Remind administrator to **invite guest to submit feedback at the kiosk**

### Waitlist
- ✅ When rooms unavailable, add guests to **waitlist** with:
   - Desired room type
   - Date range
- ✅ **Notify subscribed administrators** when availability changes
- ✅ Provide **quick conversion** from waitlist entry to reservation

### Feedback Management
- ✅ View feedback entries **only after guest has checked out**
- ✅ Provide filters by:
   - Rating
   - Date
   - Sentiment tag
   - Guest
- ✅ Export feedback summaries for analysis

### Loyalty Dashboard
- ✅ Show:
   - Balances
   - Earning history
   - Redemption activity

---

## 6. FUNCTIONAL REQUIREMENTS - FEEDBACK (Guest)

### Submission
- ✅ Submit **star rating** (1 to 5)
- ✅ Submit **comments** after checkout
- ✅ Store feedback linked to:
   - Reservation
   - Guest
- ✅ Show **confirmation after submission**

---

## KEY TAKEAWAYS FROM FIRST 5 PAGES

### Critical Requirements Checklist
- [ ] Desktop-only application (NO web)
- [ ] 3-tier architecture (Presentation, Business, Data)
- [ ] MVC pattern for presentation
- [ ] ORM for persistence
- [ ] Dependency Injection
- [ ] Design Patterns: Singleton, Strategy, Observer, Factory, Decorator
- [ ] JavaFX UI (Kiosk, Admin, Feedback modules)
- [ ] Kiosk with welcome flow, booking steps, validation
- [ ] Admin module with authentication, dashboard, reservations, payments, discounts, checkout, waitlist, feedback management
- [ ] Guest feedback submission
- [ ] BCrypt password hashing
- [ ] Role-based access (Admin/Manager)
- [ ] Group bookings support
- [ ] Loyalty program
- [ ] Waitlist with notifications
- [ ] All reports as tables (NO charts)
- [ ] Export to CSV, PDF, or TXT

### Architecture Must-Haves
1. **MVC separation** - Controllers, Models, Views (FXML)
2. **Service layer** - Business logic separate from UI
3. **Repository pattern** - Data access layer
4. **ORM** - JPA/Hibernate for database operations
5. **DI** - Constructor injection, central configuration

### Design Patterns Required
1. **Strategy** - For billing calculations (standard, discount, loyalty)
2. **Observer** - For room availability notifications
3. **Factory** - For creating room instances
4. **Decorator** - For adding services (spa, breakfast, Wi-Fi, parking) to booking pricing
5. **Singleton** - Where appropriate

---

---

## 7. BUSINESS RULES (PAGES 6-7)

### Occupancy Limits (CRITICAL RULES)
- ✅ **Single room**: Must allow up to **2 people**
- ✅ **Double room**: Must allow up to **4 people**
- ✅ **Deluxe room**: Must allow up to **2 people** (with higher base price)
- ✅ **Penthouse room**: Must allow up to **2 people** (with higher base price)
- ✅ System must validate occupancy **both per room and across group bookings**

### Group Booking Suggestions (PAGE 6)
- ✅ For groups of **3 or 4 adults**:
  - System must suggest either:
    - **One double room**, OR
    - **Two single rooms**
  - OR let them choose their own type of rooms and quantity
- ✅ For groups **larger than 4 adults**:
  - System must suggest:
    - **Multiple double rooms**, OR
    - **Combination of double and single rooms** until capacity is satisfied
- ⚠️ **IMPORTANT**: In case of group booking choosing their own room types and quantity, system **must validate the rules of occupancy**

### Dynamic Pricing (PAGE 6)
- ✅ System must apply **configurable multiplier for weekends**
- ✅ System must apply **separate multiplier for weekdays**
- ✅ System must apply **seasonal multipliers** for defined date ranges (e.g., peak season)
- ✅ System must price add-ons either:
  - **Per night**, OR
  - **Per reservation**
  - Based on their pricing model

### Payments (PAGE 6)
- ✅ System must support:
  - **Cash**
  - **Card**
  - **Loyalty point payments**
- ✅ System must allow **deposits at booking**
- ✅ System must allow **partial payments during stay**
- ✅ System must allow **refunds when required**
- ✅ System must track **paid and outstanding balances**
- ✅ System must **prevent checkout while balance remains**

### Loyalty Points (PAGE 6)
- ✅ Guests must **earn loyalty points** based on payment amounts
- ✅ Use a **configurable earning rate**
- ✅ Administrators must be able to **redeem points for discounts** under defined caps
- ✅ System must provide **loyalty dashboard** showing:
  - Balances
  - Earning history
  - Redemption activity

---

## 8. ARCHITECTURE & DESIGN PATTERNS (PAGE 7)

### Dependency Injection (DI) Requirements
- ✅ Services and repositories must use **constructor injection**
- ✅ A **central configuration class** must wire dependencies
- ✅ Central configuration must provide **singletons where appropriate**

### Required Design Patterns (PAGE 7 - DETAILED)

#### 1. Strategy Pattern
- **Purpose**: Must be used for **billing calculations**
- **Implementations needed**:
  - Standard billing strategy
  - Discount strategy
  - Loyalty strategy

#### 2. Observer Pattern
- **Purpose**: Must be used to **notify administrators when room availability changes**
- **Use case**: After checkout, when rooms become available, notify subscribed administrators

#### 3. Factory Pattern
- **Purpose**: Must be used to **create room instances with configured attributes**
- **Use case**: Creating different room types (Single, Double, Deluxe, Penthouse) with their specific attributes

#### 4. Decorator Pattern
- **Purpose**: Must be used to **add services to booking pricing**
- **Services to decorate**:
  - Spa
  - Breakfast
  - Wi-Fi
  - Parking
- **Use case**: Dynamically add service costs to base room pricing

#### 5. Singleton Pattern
- **Purpose**: Use where appropriate
- **Likely use cases**: Configuration, logging, database connection management

---

## 9. LOGGING, SECURITY, AND VALIDATION (PAGES 7-8)

### Activity Logging (PAGE 7)
- ✅ Application must record **administrator actions** including:
  - Logins
  - Searches
  - Reservation changes
  - Checkouts
  - Cancellations
  - Discounts
  - Payments
  - Refunds
  - Feedback submissions
- ✅ Each log entry must include:
  - **Timestamp**
  - **Actor** (who performed the action)
  - **Action** (what was done)
  - **Entity type** (what entity was affected)
  - **Entity identifier** (which specific entity)
  - **Descriptive message**

### Logger Configuration (PAGE 7-8 - CRITICAL)
- ✅ Application must use **java.util.logging** or **Log4j**
- ✅ Must use a **rotating file handler**
- ✅ Each log file must be limited to approximately **1 megabyte**
- ✅ System must retain up to **10 files** before rotating
- ✅ Students should configure logging to store logs in a **separate log file**
- ✅ Consider implementing log rotation to avoid excessive file size growth

#### Example Configuration (PAGE 8)
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

**Configuration Details**:
- `"system_logs.%g.log"` - Pattern where `%g` is placeholder for generation number
- `1024 * 1024` - Sets limit to 1 MB for each log file
- `10` - Maximum number of log files to keep
- This setup creates new log file after current file reaches 1 MB
- Maintains up to 10 log files, overwrites oldest beyond that

### Exception Logging (PAGE 8)
- ✅ Application must log:
  - **Validation failures**
  - **Persistence errors**
  - **Unexpected exceptions**
- ✅ Log at **appropriate levels**
- ✅ **Severe issues** must include **stack traces** for troubleshooting

### Authentication and Authorization (PAGE 8)
- ✅ Application must store **only BCrypt-hashed passwords**
- ✅ Must perform **role checks** for sensitive actions:
  - Discounts
  - Refunds
  - Reporting
  - User management

### Validation Rules (PAGE 8)
- ✅ **Guest names**: Validate with clear messages
- ✅ **Phone numbers**: Validate with clear messages
- ✅ **Email addresses**: Validate with clear messages
- ✅ **Date ranges**: 
  - Validate with minimums
  - Check for overlaps
- ✅ **Occupancy distribution**: Validate across group bookings
- ✅ **Payment amounts**: 
  - Validate amounts
  - Prevent negative balances
- ✅ **Discounts**: 
  - Validate within configured caps
  - Enforce non-negative values
- ✅ **Feedback ratings**: Validate ratings (1-5)
- ✅ **Feedback comments**: Cap comment length

---

## 10. REPORTING SPECIFICATIONS (PAGE 8-9)

### Revenue Reports (PAGE 8)
- ✅ System must provide revenue summaries for:
  - **Day** view
  - **Week** view
  - **Month** view
- ✅ Each summary must include:
  - **Period**
  - **Reservation count**
  - **Subtotal**
  - **Tax**
  - **Discounts**
  - **Total amounts**
- ✅ Reports must be displayed in **tables**
- ✅ Must support **filtering by**:
  - Date range
  - Room type
- ✅ Must be **exportable to CSV or PDF**

### Occupancy Reports (PAGE 8)
- ✅ System must provide occupancy tables for:
  - **Daily** view
  - **Weekly** view
  - **Monthly** view
- ✅ Each table must include:
  - **Date**
  - **Rooms available**
  - **Rooms occupied**
  - **Occupancy percentage** (as numeric value only)
- ✅ Must support **filtering by**:
  - Date range
  - Room type
- ✅ Must be **exportable to CSV and PDF**

### Feedback Summary (PAGE 8)
- ✅ System must show:
  - **Reservation identifier**
  - **Guest**
  - **Rating**
  - **Comment**
  - **Date**
  - **Sentiment tag**
- ✅ System must display:
  - **Average rating**
  - **Counts for common issue tags**
- ✅ Must **export to CSV**

### Activity Logs (PAGE 8)
- ✅ System must show:
  - **Timestamp**
  - **Actor**
  - **Action**
  - **Entity type**
  - **Entity identifier**
  - **Message**
- ✅ System must read from:
  - **Log file**, OR
  - **Audit table**
- ✅ Must **export to CSV and TXT**

---

## KEY TAKEAWAYS FROM PAGES 6-9

### Business Rules Checklist
- [ ] Occupancy limits: Single (2), Double (4), Deluxe (2), Penthouse (2)
- [ ] Group booking suggestions for 3-4 adults and larger groups
- [ ] Dynamic pricing: Weekend/weekday multipliers, seasonal multipliers
- [ ] Payment support: Cash, Card, Loyalty points
- [ ] Deposits, partial payments, refunds
- [ ] Balance tracking and checkout prevention
- [ ] Loyalty points earning and redemption

### Design Patterns Implementation
- [ ] Strategy pattern for billing calculations
- [ ] Observer pattern for room availability notifications
- [ ] Factory pattern for room instance creation
- [ ] Decorator pattern for add-on services pricing
- [ ] Singleton pattern where appropriate
- [ ] Constructor injection for DI
- [ ] Central configuration class

### Logging Requirements
- [ ] java.util.logging or Log4j
- [ ] Rotating file handler
- [ ] 1 MB file size limit
- [ ] 10 file rotation
- [ ] Separate log file
- [ ] Log all administrator actions with full details
- [ ] Exception logging with stack traces for severe issues

### Security Requirements
- [ ] BCrypt password hashing only
- [ ] Role-based access control
- [ ] Role checks for sensitive actions

### Validation Requirements
- [ ] Guest name, phone, email validation
- [ ] Date range validation with overlap checks
- [ ] Occupancy validation across group bookings
- [ ] Payment amount validation
- [ ] Discount validation within caps
- [ ] Feedback rating and comment validation

### Reporting Requirements
- [ ] Revenue reports (day/week/month) with filtering
- [ ] Occupancy reports (daily/weekly/monthly) with filtering
- [ ] Feedback summary with average rating and issue tags
- [ ] Activity logs from log file or audit table
- [ ] All reports as tables (NO charts)
- [ ] Export to CSV, PDF, or TXT

---

---

## 11. MILESTONE 1 REQUIREMENTS (PAGE 9-10)

### Deadline
- **Date**: November 12th, 2025
- **Type**: Checkpoint milestone
- **Attendance**: **MANDATORY**

### What to Present
- **Duration**: 3 to 5 minutes meeting
- Must present:
  - ✅ **Final front-end designs**
  - ✅ **Class diagrams**
  - ✅ **UML diagrams** (or any other diagram that can support project structure)
  - ✅ **Data flow diagrams**
  - ✅ **Final screenshots of all front ends**

### Submission Requirements
- ✅ Separate submission will be open
- ✅ Must submit:
  - All UML diagrams
  - Front-end screenshots
  - Database designs
  - Any other design diagrams shown in lab
- ⚠️ **IMPORTANT**: Only submissions approved during lab time will be accepted
- ✅ Students can show their design in earlier labs as well

---

## 12. FINAL SUBMISSION REQUIREMENTS (PAGE 10)

### Deadline
- **Date**: December 3rd, 2025
- **Weight**: 13% of course grade

### Video Demo Requirements
- ✅ Record a video to show the **full working project**
- ✅ Include **explanation of challenges faced**
- ✅ **Minimum duration**: 7-10 minutes
- ✅ **No deduction** for extra minutes (can use more time if needed)
- ✅ Video must demonstrate complete functionality

### Submission Items
1. ✅ **Full project** (complete codebase)
2. ✅ **Database scripts** (if using SQL, MySQL - **must submit**)
3. ✅ **Reflection document** must include:
   - Challenges faced and finding solutions
   - Learnings during the project

---

## 13. PROJECT DOCUMENTATION REQUIREMENTS (PAGES 10-11)

### Purpose of Documentation
- ⚠️ **NOT just a summary** of project instructions
- ✅ **Your opportunity** to explain:
  - How you understood the system
  - How you designed the system
  - How you implemented the system
- ✅ Should reflect:
  - Your decisions
  - Your challenges
  - Your learning journey
- ✅ Should include:
  - Your own diagrams
  - Explanations of how you applied patterns
  - Explanations of how you applied business rules
  - Reflections on what worked well
  - Reflections on what you'd improve
- ✅ Think of it as a **professional walkthrough** of your work

### Documentation Checklist (PAGE 10-11)

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

### Documentation Submission
- ✅ Submit **one PDF or DOCX file**
- ✅ File name: `ProjectDocumentation_[YourName].pdf` (or .docx)

---

## 14. GRADING BREAKDOWN (PAGE 11)

### Total Weight: 100%

#### 1. Design & Architecture (20%)
- MVC separation
- DI (Dependency Injection)
- Package structure
- UML diagrams

#### 2. Patterns & Principles (15%)
- Strategy, Observer, Factory, Decorator, Singleton patterns
- OO (Object-Oriented) principles

#### 3. ORM & Persistence (15%)
- JPA annotations
- Relationships
- EMF/EM (EntityManagerFactory/EntityManager) usage
- Queries

#### 4. Functionality - Kiosk (10%)
- Booking flow
- Validation
- Dynamic pricing

#### 5. Functionality - Admin (15%)
- Login
- Search
- Modify
- Payments
- Checkout
- Notifications

#### 6. Functionality - Waitlist & Loyalty (10%)
- Waitlist creation
- Observer notifications
- Loyalty dashboard

#### 7. Reporting & Feedback (10%)
- Tabular reports
- Export formats
- Feedback flow

#### 8. Logging & Security (5%)
- Logger rotation
- Audit logs
- Exception handling
- BCrypt

### Passing Threshold
- ✅ **Minimum 50% overall**
- ✅ **Must have working**:
  - Kiosk booking
  - Admin login
  - ORM persistence

### Lab Support
- ✅ **Every Wednesday lab**: Students can approach and discuss:
  - Design of the project
  - Their progress
  - Any issues they're facing

---

## 15. OPTIONAL REQUIREMENT: MULTITHREADED SERVER (PAGES 11-12)

### Purpose
- In real-world hotel reservation system, **multiple admins** might need to log in and manage bookings **at the same time**
- To simulate this, you **must implement** a multithreaded server that allows multiple admin sessions to run simultaneously

### What This Means
- ✅ System should allow **at least two admins** to log in and use the system at the same time
- ✅ Each admin should use **separate command prompt windows**
- ✅ Each admin should be able to:
  - Search guests
  - Modify reservations
  - Process checkouts
  - Apply discounts
  - **Independently** (without conflicts or crashes)
- ✅ Server should handle **multiple admin requests** without conflicts or crashes

### How to Achieve This

#### 1. Use a Multi-Threaded Server Approach
- ✅ System should have a **server-side application** that listens for admin connections
- ✅ When an admin logs in, the server should **create a new thread** to handle that admin's session

#### Example Code (PAGE 12)
```java
class AdminHandler extends Thread {
    private Socket socket;
    public AdminHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        // Handle admin requests here
    }
}
```
- This ensures that multiple admins can work **independently** without affecting each other

#### 2. Client-Server Communication
- ✅ The admin interface should act as a **client**, sending requests to the server
- ✅ The server will process requests and **respond to each admin individually**

#### 3. Testing the Multithreaded Functionality
- ✅ Open **two command prompt windows**
- ✅ Start an admin session in each
- ✅ Verify that both admins can:
  - Log in
  - Search for guests
  - Process reservations
  - **Simultaneously**

### Important Notes
- ⚠️ This is an **optional requirement** but recommended for real-world simulation
- ✅ Demonstrates understanding of:
  - Multithreading
  - Client-server architecture
  - Concurrent access handling

---

## KEY TAKEAWAYS FROM PAGES 10-14

### Milestone 1 Checklist
- [ ] Final front-end designs ready
- [ ] Class diagrams complete
- [ ] UML diagrams complete
- [ ] Database designs complete
- [ ] Screenshots of all front ends
- [ ] Present in 3-5 minute meeting
- [ ] Get approval during lab time
- [ ] Submit approved designs

### Final Submission Checklist
- [ ] Record 7-10 minute video demo
- [ ] Show full working project
- [ ] Explain challenges faced
- [ ] Submit full project code
- [ ] Submit database scripts (SQL/MySQL)
- [ ] Submit reflection document

### Documentation Checklist
- [ ] Project overview
- [ ] Architecture summary
- [ ] Design artifacts (class, sequence, deployment, package diagrams)
- [ ] Entity and relationship mapping
- [ ] Pattern usage documentation
- [ ] Business rules documentation
- [ ] Security and logging documentation
- [ ] Export and reporting documentation
- [ ] Challenges and learnings
- [ ] Submit as single PDF or DOCX file

### Grading Focus Areas
- [ ] Design & Architecture (20%) - MVC, DI, package structure, UML
- [ ] Patterns & Principles (15%) - All 5 patterns, OO principles
- [ ] ORM & Persistence (15%) - JPA, relationships, queries
- [ ] Kiosk Functionality (10%) - Booking flow, validation, pricing
- [ ] Admin Functionality (15%) - Login, search, modify, payments, checkout
- [ ] Waitlist & Loyalty (10%) - Waitlist, observer, loyalty dashboard
- [ ] Reporting & Feedback (10%) - Tabular reports, exports, feedback
- [ ] Logging & Security (5%) - Logger rotation, audit logs, BCrypt

### Optional Requirements
- [ ] Multithreaded server implementation
- [ ] Multiple admin sessions simultaneously
- [ ] Client-server architecture
- [ ] Testing with two command prompt windows

---

## COMPLETE PROJECT REQUIREMENTS SUMMARY

### Critical Path to Passing (Minimum 50%)
1. ✅ **Working kiosk booking** - Must be functional
2. ✅ **Working admin login** - Must be functional
3. ✅ **ORM persistence** - Must be functional

### Architecture Must-Haves
- 3-tier architecture (Presentation, Business, Data)
- MVC pattern
- Dependency Injection (constructor injection)
- ORM (JPA/Hibernate)
- All 5 design patterns (Strategy, Observer, Factory, Decorator, Singleton)

### Functional Must-Haves
- Kiosk: Welcome flow, booking steps, validation, add-ons, confirmation
- Admin: Authentication, dashboard, reservations, payments, discounts, checkout, waitlist, feedback management
- Feedback: Guest submission after checkout
- Reports: Revenue, occupancy, activity logs, feedback summary (all as tables)
- Exports: CSV, PDF, TXT formats

### Technical Must-Haves
- BCrypt password hashing
- Role-based access (Admin/Manager)
- Logging with rotation (1 MB files, 10 file limit)
- Validation at all levels
- Group bookings support
- Loyalty program
- Dynamic pricing
- Waitlist with observer notifications

---

## NEXT STEPS
- Review complete requirements
- Cross-check implementation against all requirements
- Ensure all critical path items are working
- Prepare for Milestone 1 presentation
- Plan final submission video and documentation

