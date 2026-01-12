# 🎓 Understanding the Hotel Reservation System - From Scratch

**A Beginner-Friendly Guide to Navigating and Understanding This Project**

---

## 📖 Table of Contents

1. [What Is This Project?](#what-is-this-project)
2. [The Big Picture](#the-big-picture)
3. [How to Read This Codebase](#how-to-read-this-codebase)
4. [Learning Paths](#learning-paths)
5. [Key Concepts Explained](#key-concepts-explained)
6. [Following a Feature End-to-End](#following-a-feature-end-to-end)
7. [Common Questions](#common-questions)
8. [Next Steps](#next-steps)

---

## 🏨 What Is This Project?

### In Simple Terms

This is a **desktop application** (like a desktop app you'd install on Windows/Mac) that helps manage a hotel. Think of it like a hotel management system you'd see at a hotel front desk, but built as a Java application.

### What It Does

1. **For Guests:** A self-service kiosk where guests can book rooms themselves (like an ATM for hotel bookings)
2. **For Staff:** An admin dashboard where hotel staff can:
   - View and manage reservations
   - Process payments
   - Check guests in/out
   - Generate reports
   - Manage waitlists
   - View feedback

### Technology Stack

- **Language:** Java
- **UI Framework:** JavaFX (for creating windows, buttons, forms)
- **Database:** MySQL (stores all data - guests, rooms, reservations, etc.)
- **ORM:** JPA/Hibernate (connects Java objects to database tables)
- **Build Tool:** Maven (manages dependencies and builds the project)

---

## 🗺️ The Big Picture

### Architecture Overview

Think of the project like a **3-story building**:

```
┌─────────────────────────────────────────┐
│  FLOOR 3: PRESENTATION (What You See)  │
│  - JavaFX Windows/Screens               │
│  - Controllers (handle button clicks)   │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│  FLOOR 2: BUSINESS LOGIC (The Rules)    │
│  - Services (business rules)             │
│  - Policies (pricing, discounts, etc.)  │
│  - Design Patterns                      │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│  FLOOR 1: DATA (The Storage)            │
│  - Repositories (database access)       │
│  - Entities (Java objects = DB tables)  │
│  - MySQL Database                       │
└─────────────────────────────────────────┘
```

### The Flow of Information

When a guest books a room, here's what happens:

```
1. Guest clicks "Book Room" button (UI)
   ↓
2. Controller receives the click
   ↓
3. Controller calls a Service method
   ↓
4. Service applies business rules (check availability, calculate price)
   ↓
5. Service calls Repository to save data
   ↓
6. Repository saves to Database
   ↓
7. Success message flows back up: Database → Repository → Service → Controller → UI
```

---

## 📚 How to Read This Codebase

### Step 1: Start with the Entry Point

**File:** `src/main/java/com/hotel/app/Main.java`

This is where the application starts. It's like the "ON" button.

```java
public class Main extends Application {
    public void start(Stage primaryStage) {
        AppConfig.initialize();  // Sets up everything
        // Loads the first screen (Welcome or Login)
    }
}
```

**What to understand:**
- This is a JavaFX application (extends `Application`)
- It initializes `AppConfig` (which sets up the whole system)
- It loads the first screen (FXML file)

---

### Step 2: Understand the Configuration

**File:** `src/main/java/com/hotel/app/AppConfig.java`

This is the **central hub** that creates and wires everything together. Think of it as the "factory" that builds all the parts.

**Key Concepts:**
- **Dependency Injection:** Instead of classes creating their own dependencies, `AppConfig` creates them and passes them in
- **Singleton Pattern:** Some things (like `EntityManagerFactory`, `LoggerService`) are created once and reused

**Example:**
```java
// Instead of doing this in a controller:
ReservationService service = new ReservationService(new ReservationRepository(...));

// AppConfig does this:
public static ReservationService createReservationService() {
    EntityManager em = createEntityManager();
    return new ReservationService(em);
}

// Controllers just call:
ReservationService service = AppConfig.createReservationService();
```

**Why this is good:**
- Centralized configuration
- Easy to test (can swap in mock objects)
- No tight coupling between classes

---

### Step 3: Explore the Model Layer (Data Structures)

**Location:** `src/main/java/com/hotel/model/`

These are the **data structures** that represent real-world things:
- `Guest.java` = A guest (person)
- `Room.java` = A hotel room
- `Reservation.java` = A booking
- `Billing.java` = A bill/invoice
- `Payment.java` = A payment transaction

**How to read an entity:**

```java
@Entity
@Table(name = "guests")
public class Guest {
    @Id
    @GeneratedValue
    private Long id;  // Primary key
    
    private String name;
    private String email;
    
    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations;  // One guest has many reservations
}
```

**Key annotations:**
- `@Entity` = This is a database table
- `@Id` = This is the primary key
- `@OneToMany` = One guest can have many reservations
- `@ManyToOne` = Many reservations belong to one guest

**Exercise:** Open `Guest.java` and `Reservation.java` and see how they're connected.

---

### Step 4: Understand Repositories (Data Access)

**Location:** `src/main/java/com/hotel/repository/`

Repositories are like **librarians** - they know how to find and store data in the database.

**Example:**
```java
public class GuestRepository {
    private EntityManager em;
    
    public Guest findById(Long id) {
        return em.find(Guest.class, id);  // Find guest by ID
    }
    
    public Guest findByEmail(String email) {
        // Query database for guest with this email
        return em.createQuery("SELECT g FROM Guest g WHERE g.email = :email", Guest.class)
                 .setParameter("email", email)
                 .getSingleResult();
    }
    
    public void save(Guest guest) {
        em.getTransaction().begin();
        em.persist(guest);  // Save to database
        em.getTransaction().commit();
    }
}
```

**What repositories do:**
- **CRUD operations:** Create, Read, Update, Delete
- **Custom queries:** Find by email, find available rooms, etc.
- **Abstract database details:** Services don't need to know SQL

---

### Step 5: Explore Services (Business Logic)

**Location:** `src/main/java/com/hotel/service/`

Services contain the **business rules** and **workflows**. This is where the "magic" happens.

**Example: `ReservationService.java`**

```java
public class ReservationService {
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;
    
    public Reservation createReservation(Guest guest, List<Room> rooms, LocalDate checkIn, LocalDate checkOut) {
        // 1. Validate dates
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in must be before check-out");
        }
        
        // 2. Check room availability
        for (Room room : rooms) {
            if (!isRoomAvailable(room, checkIn, checkOut)) {
                throw new IllegalStateException("Room " + room.getNumber() + " is not available");
            }
        }
        
        // 3. Create reservation
        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setStatus(ReservationStatus.PENDING);
        
        // 4. Save to database
        reservationRepository.save(reservation);
        
        return reservation;
    }
}
```

**What services do:**
- **Validate:** Check if data is valid
- **Apply business rules:** Calculate prices, check availability, etc.
- **Coordinate:** Call multiple repositories
- **Handle transactions:** Ensure data consistency

---

### Step 6: Understand Controllers (UI Logic)

**Location:** `src/main/java/com/hotel/controller/`

Controllers are the **bridge** between the UI (what users see) and the services (business logic).

**Example: `KioskController.java`**

```java
public class KioskController {
    @FXML
    private TextField guestNameField;  // UI element
    
    @FXML
    private Button bookButton;  // UI button
    
    @FXML
    private void handleBookButton() {
        // 1. Get data from UI
        String name = guestNameField.getText();
        
        // 2. Validate input
        if (name.isEmpty()) {
            showError("Name is required");
            return;
        }
        
        // 3. Call service
        ReservationService service = AppConfig.createReservationService();
        Reservation reservation = service.createReservation(...);
        
        // 4. Update UI
        showSuccess("Reservation created!");
    }
}
```

**What controllers do:**
- **Handle events:** Button clicks, form submissions
- **Validate input:** Check if user entered valid data
- **Call services:** Delegate business logic to services
- **Update UI:** Show results, errors, success messages

---

## 🛤️ Learning Paths

### Path 1: Understanding the Data Flow

**Goal:** See how data flows from UI to database and back.

**Steps:**
1. Open `KioskController.java`
2. Find a method like `handleBookRoom()`
3. Follow the method calls:
   - Controller → Service → Repository → Database
4. See how results flow back

**Files to read in order:**
1. `controller/KioskController.java` (UI handling)
2. `service/ReservationService.java` (business logic)
3. `repository/ReservationRepository.java` (data access)
4. `model/Reservation.java` (data structure)

---

### Path 2: Understanding Design Patterns

**Goal:** Learn how design patterns are used in this project.

#### Pattern 1: Strategy Pattern

**Location:** `service/strategy/`

**What it does:** Allows different billing calculations without changing the service code.

**Files:**
- `BillingStrategy.java` (interface)
- `StandardBillingStrategy.java` (standard calculation)
- `DiscountBillingStrategy.java` (with discount)
- `LoyaltyBillingStrategy.java` (with loyalty points)

**How to understand:**
1. Read `BillingStrategy.java` - this is the contract
2. Read `StandardBillingStrategy.java` - see one implementation
3. Read `BillingService.java` - see how it uses strategies

**Example:**
```java
// BillingService can switch strategies:
BillingStrategy strategy = new DiscountBillingStrategy(discountPolicy);
double total = strategy.calculateTotal(subtotal, tax);
```

---

#### Pattern 2: Observer Pattern

**Location:** `events/`

**What it does:** Notifies waitlist when rooms become available.

**Files:**
- `Subject.java` (interface - the publisher)
- `Observer.java` (interface - the subscriber)
- `RoomAvailabilityPublisher.java` (publishes events)
- `WaitlistSubscriber.java` (receives events)

**How to understand:**
1. Read `Subject.java` and `Observer.java` - these are the contracts
2. Read `RoomAvailabilityPublisher.java` - see how it publishes
3. Read `WaitlistSubscriber.java` - see how it receives
4. Read `ReservationService.java` - see where it publishes (on checkout)
5. Read `WaitlistService.java` - see where it subscribes

**Example:**
```java
// When a room becomes available:
roomAvailabilityPublisher.publishRoomAvailable(room);

// WaitlistSubscriber automatically receives notification:
public void update(String message) {
    // Admin sees notification
}
```

---

#### Pattern 3: Decorator Pattern

**Location:** `service/decorator/`

**What it does:** Dynamically adds services (Wi-Fi, breakfast) to a booking.

**Files:**
- `BookingComponent.java` (base interface)
- `RoomBookingComponent.java` (base booking)
- `AddOnDecorator.java` (adds services)
- `CombinedBookingComponent.java` (combines multiple)

**How to understand:**
1. Read `BookingComponent.java` - the base contract
2. Read `RoomBookingComponent.java` - basic room booking
3. Read `AddOnDecorator.java` - how services are added
4. See it used in `KioskController.java`

**Example:**
```java
// Start with base booking
BookingComponent booking = new RoomBookingComponent(roomPrice);

// Add Wi-Fi
booking = new AddOnDecorator(booking, wifiAddon);

// Add breakfast
booking = new AddOnDecorator(booking, breakfastAddon);

// Total includes room + Wi-Fi + breakfast
double total = booking.getPrice();
```

---

### Path 3: Understanding Business Rules

**Goal:** Learn how business rules are implemented.

**Location:** `config/` and `service/`

**Key Business Rules:**

1. **Pricing Rules** (`PricingPolicy.java`)
   - Weekend prices are 20% higher
   - Seasonal multipliers apply
   - Base price comes from room type

2. **Discount Rules** (`DiscountPolicy.java`)
   - Admin can apply up to 15% discount
   - Manager can apply up to 30% discount
   - Discounts must be validated

3. **Loyalty Rules** (`LoyaltyPolicy.java`)
   - Earn 1 point per $10 spent
   - Max 1000 points per redemption
   - Points can be redeemed for discounts

**How to understand:**
1. Read `PricingPolicy.java` - see how prices are calculated
2. Read `PricingService.java` - see how it uses the policy
3. Read `DiscountPolicy.java` - see discount rules
4. Read `BillingService.java` - see how discounts are applied

---

## 🔑 Key Concepts Explained

### 1. Dependency Injection

**What it is:** Instead of classes creating their own dependencies, they receive them from outside.

**Why it's good:**
- Easier to test (can inject mock objects)
- Loose coupling (classes don't depend on specific implementations)
- Centralized configuration

**Example:**
```java
// BAD (tight coupling):
public class ReservationService {
    private ReservationRepository repo = new ReservationRepository();
}

// GOOD (dependency injection):
public class ReservationService {
    private ReservationRepository repo;
    
    public ReservationService(EntityManager em) {
        this.repo = new ReservationRepository(em);  // Injected
    }
}
```

---

### 2. Repository Pattern

**What it is:** A layer that abstracts database operations.

**Why it's good:**
- Services don't need to know SQL
- Easy to test (can mock repositories)
- Reusable queries

**Example:**
```java
// Service doesn't know about SQL:
Reservation reservation = reservationRepository.findById(id);

// Repository handles SQL:
public Reservation findById(Long id) {
    return em.find(Reservation.class, id);
}
```

---

### 3. Service Layer

**What it is:** A layer that contains business logic.

**Why it's good:**
- All business rules in one place
- Reusable by multiple controllers
- Easy to test

**Example:**
```java
// Business rule: Check-in must be before check-out
public Reservation createReservation(...) {
    if (checkIn.isAfter(checkOut)) {
        throw new IllegalArgumentException("Invalid dates");
    }
    // ... rest of logic
}
```

---

### 4. MVC Pattern

**What it is:** Model-View-Controller separation.

- **Model:** Entities (data structures)
- **View:** FXML files (UI layout)
- **Controller:** Controllers (handle UI events)

**Why it's good:**
- Separation of concerns
- Easy to change UI without changing logic
- Easy to test

---

## 🔍 Following a Feature End-to-End

### Example: Guest Books a Room

Let's trace through what happens when a guest books a room:

#### Step 1: User Interaction (UI)
**File:** `view/kiosk/RoomSelection.fxml`

User selects a room and clicks "Book Now" button.

---

#### Step 2: Controller Receives Event
**File:** `controller/KioskController.java`

```java
@FXML
private void handleBookRoom() {
    // Get selected room
    Room selectedRoom = roomTableView.getSelectionModel().getSelectedItem();
    
    // Get guest details
    Guest guest = getGuestFromForm();
    
    // Call service
    ReservationService service = AppConfig.createReservationService();
    Reservation reservation = service.createReservation(
        guest, 
        Arrays.asList(selectedRoom), 
        checkInDate, 
        checkOutDate
    );
}
```

---

#### Step 3: Service Applies Business Logic
**File:** `service/ReservationService.java`

```java
public Reservation createReservation(Guest guest, List<Room> rooms, LocalDate checkIn, LocalDate checkOut) {
    // 1. Validate
    validateDates(checkIn, checkOut);
    
    // 2. Check availability
    for (Room room : rooms) {
        if (!isRoomAvailable(room, checkIn, checkOut)) {
            throw new IllegalStateException("Room not available");
        }
    }
    
    // 3. Create reservation
    Reservation reservation = new Reservation();
    reservation.setGuest(guest);
    reservation.setCheckInDate(checkIn);
    reservation.setCheckOutDate(checkOut);
    
    // 4. Save
    reservationRepository.save(reservation);
    
    // 5. Update room status
    for (Room room : rooms) {
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);
    }
    
    return reservation;
}
```

---

#### Step 4: Repository Saves to Database
**File:** `repository/ReservationRepository.java`

```java
public void save(Reservation reservation) {
    EntityManager em = this.em;
    em.getTransaction().begin();
    em.persist(reservation);  // Saves to database
    em.getTransaction().commit();
}
```

---

#### Step 5: Results Flow Back

```
Database → Repository → Service → Controller → UI
```

Controller receives the reservation and shows a success message.

---

## ❓ Common Questions

### Q1: Where do I start if I want to add a new feature?

**Answer:**
1. **Model:** Add entity if needed (`model/`)
2. **Repository:** Add data access methods (`repository/`)
3. **Service:** Add business logic (`service/`)
4. **Controller:** Add UI handling (`controller/`)
5. **FXML:** Create UI if needed (`view/`)

---

### Q2: How do I find where a specific feature is implemented?

**Answer:**
1. **UI Feature:** Look in `controller/` for the controller that handles that screen
2. **Business Logic:** Look in `service/` for the service that handles that domain
3. **Data Access:** Look in `repository/` for the repository that handles that entity

**Example:** To find payment processing:
- UI: `controller/AdminController.java` (has `processPayment()` method)
- Business Logic: `service/BillingService.java` (has `processPayment()` method)
- Data: `repository/PaymentRepository.java` (saves payments)

---

### Q3: How are entities related to each other?

**Answer:**
Look at the `@OneToMany` and `@ManyToOne` annotations in entity classes.

**Example:**
```java
// Guest.java
@OneToMany(mappedBy = "guest")
private List<Reservation> reservations;  // One guest has many reservations

// Reservation.java
@ManyToOne
private Guest guest;  // Many reservations belong to one guest
```

---

### Q4: How do I understand the database structure?

**Answer:**
1. **Entities:** Look at `model/` - each entity = one table
2. **Schema:** Look at `database/create_schema.sql` - see actual SQL
3. **Relationships:** Look at `@OneToMany`, `@ManyToOne` annotations

---

### Q5: What's the difference between a Service and a Repository?

**Answer:**
- **Repository:** Handles data access (save, find, delete)
- **Service:** Handles business logic (validate, calculate, coordinate)

**Example:**
```java
// Repository (data access):
public Guest findById(Long id) {
    return em.find(Guest.class, id);
}

// Service (business logic):
public void enrollInLoyalty(Long guestId) {
    Guest guest = guestRepository.findById(guestId);
    if (guest.getLoyaltyNumber() != null) {
        throw new IllegalStateException("Already enrolled");
    }
    guest.setLoyaltyNumber(generateLoyaltyNumber());
    guestRepository.save(guest);
}
```

---

## 🎯 Next Steps

### For Beginners:

1. **Read the code in this order:**
   - `Main.java` (entry point)
   - `AppConfig.java` (configuration)
   - `model/Guest.java` (simple entity)
   - `repository/GuestRepository.java` (simple repository)
   - `service/ReservationService.java` (business logic)
   - `controller/KioskController.java` (UI handling)

2. **Run the application:**
   - Set up database (see `database/create_schema.sql`)
   - Run `Main.java`
   - Try booking a room
   - Put breakpoints and step through the code

3. **Modify something small:**
   - Change a validation message
   - Add a new field to an entity
   - Add a new query to a repository

### For Intermediate Developers:

1. **Understand design patterns:**
   - Study Strategy pattern in `service/strategy/`
   - Study Observer pattern in `events/`
   - Study Decorator pattern in `service/decorator/`

2. **Follow a complete feature:**
   - Trace payment processing end-to-end
   - Trace waitlist notification end-to-end

3. **Add a new feature:**
   - Add a new report type
   - Add a new add-on service
   - Add a new discount type

### For Advanced Developers:

1. **Refactor:**
   - Improve error handling
   - Add more design patterns
   - Optimize database queries

2. **Extend:**
   - Add email notifications
   - Add more payment methods
   - Add room recommendations

---

## 📚 Additional Resources

- **PROJECT_OVERVIEW.md** - Detailed technical reference
- **QUICK_START_GUIDE.md** - Step-by-step implementation guide
- **IMPLEMENTATION_ROADMAP.md** - Complete implementation strategy
- **PROJECT_INSTRUCTIONS.md** - Original requirements

---

## 💡 Tips for Understanding Code

1. **Start small:** Don't try to understand everything at once
2. **Use breakpoints:** Step through code with a debugger
3. **Read in layers:** Understand one layer before moving to the next
4. **Follow the data:** Trace how data flows through the system
5. **Ask questions:** If something doesn't make sense, dig deeper

---

**Remember:** Understanding a codebase takes time. Start with the basics, build your understanding layer by layer, and don't be afraid to experiment!

**Good luck! 🚀**

