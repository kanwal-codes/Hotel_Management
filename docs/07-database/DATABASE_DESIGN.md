# Database Design - Hotel Reservation System

## Entity Relationship Diagram (ERD) Overview

### Core Entities and Relationships

```
Hotel (1) ──< (0..*) Room
Guest (1) ──< (0..*) Reservation
Guest (1) ──< (0..*) Feedback
Guest (1) ──< (0..1) Waitlist
Reservation (1) ──< (0..*) Feedback
Reservation (1) ──< (0..1) Billing
Reservation (1) ──< (1..*) ReservationRoom
Reservation (1) ──< (0..*) ReservationAddon
Room (1) ──< (0..*) ReservationRoom
ServiceAddon (1) ──< (0..*) ReservationAddon
Billing (1) ──< (0..*) Payment
AdminUser (1) ──< (0..*) AuditLog
```

---

## Database Schema

### 1. Hotel Table
```sql
CREATE TABLE Hotel (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `name` (VARCHAR(100), NOT NULL)
- `city` (VARCHAR(100), NOT NULL)

**Relationships:**
- One-to-Many with Room

---

### 2. Room Table
```sql
CREATE TABLE Room (
    id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_id INT NOT NULL,
    roomNumber VARCHAR(10) NOT NULL UNIQUE,
    type ENUM('SINGLE', 'DOUBLE', 'DELUXE', 'PENTHOUSE') NOT NULL,
    beds INT NOT NULL,
    basePrice DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (hotel_id) REFERENCES Hotel(id) ON DELETE CASCADE
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `hotel_id` (INT, FK → Hotel.id)
- `roomNumber` (VARCHAR(10), UNIQUE, NOT NULL)
- `type` (ENUM: SINGLE, DOUBLE, DELUXE, PENTHOUSE)
- `beds` (INT, NOT NULL)
- `basePrice` (DECIMAL(10,2), NOT NULL)
- `status` (ENUM: AVAILABLE, OCCUPIED, MAINTENANCE, DEFAULT 'AVAILABLE')

**Relationships:**
- Many-to-One with Hotel
- Many-to-Many with Reservation (via ReservationRoom)

**Constraints:**
- Occupancy limits: SINGLE/DOUBLE/DELUXE/PENTHOUSE (see Business Rules)

---

### 3. Guest Table
```sql
CREATE TABLE Guest (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    loyaltyPoints INT DEFAULT 0,
    loyaltyNumber VARCHAR(50) UNIQUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `name` (VARCHAR(100), NOT NULL)
- `phone` (VARCHAR(20), NOT NULL)
- `email` (VARCHAR(100), NOT NULL)
- `address` (VARCHAR(255))
- `loyaltyPoints` (INT, DEFAULT 0)
- `loyaltyNumber` (VARCHAR(50), UNIQUE)
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Relationships:**
- One-to-Many with Reservation
- One-to-Many with Feedback
- One-to-One with Waitlist

**Validation:**
- Email format validation
- Phone number validation

---

### 4. Reservation Table
```sql
CREATE TABLE Reservation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    checkIn DATE NOT NULL,
    checkOut DATE NOT NULL,
    numAdults INT NOT NULL,
    numChildren INT DEFAULT 0,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_OUT') NOT NULL DEFAULT 'PENDING',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES Guest(id) ON DELETE RESTRICT,
    CHECK (checkOut > checkIn),
    CHECK (numAdults > 0)
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `guest_id` (INT, FK → Guest.id)
- `checkIn` (DATE, NOT NULL)
- `checkOut` (DATE, NOT NULL)
- `numAdults` (INT, NOT NULL)
- `numChildren` (INT, DEFAULT 0)
- `status` (ENUM: PENDING, CONFIRMED, CANCELLED, CHECKED_OUT, DEFAULT 'PENDING')
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Relationships:**
- Many-to-One with Guest
- One-to-Many with Feedback
- One-to-Many with ReservationRoom
- One-to-Many with ReservationAddon
- One-to-One with Billing

**Constraints:**
- checkOut must be after checkIn
- numAdults must be > 0
- Date range validation (no overlaps for same room)

---

### 5. ReservationRoom Table (Junction Table)
```sql
CREATE TABLE ReservationRoom (
    reservation_id INT NOT NULL,
    room_id INT NOT NULL,
    PRIMARY KEY (reservation_id, room_id),
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES Room(id) ON DELETE RESTRICT
);
```

**Fields:**
- `reservation_id` (INT, FK → Reservation.id, PK)
- `room_id` (INT, FK → Room.id, PK)

**Purpose:** Many-to-Many relationship between Reservation and Room

---

### 6. ServiceAddon Table
```sql
CREATE TABLE ServiceAddon (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    pricingModel ENUM('PER_NIGHT', 'PER_RESERVATION') NOT NULL,
    active BOOLEAN DEFAULT TRUE
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `name` (VARCHAR(100), NOT NULL) - e.g., 'Wi-Fi', 'Breakfast', 'Parking', 'Spa'
- `price` (DECIMAL(10,2), NOT NULL)
- `pricingModel` (ENUM: PER_NIGHT, PER_RESERVATION)
- `active` (BOOLEAN, DEFAULT TRUE)

**Relationships:**
- One-to-Many with ReservationAddon

---

### 7. ReservationAddon Table
```sql
CREATE TABLE ReservationAddon (
    reservation_id INT NOT NULL,
    addon_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (reservation_id, addon_id),
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id) ON DELETE CASCADE,
    FOREIGN KEY (addon_id) REFERENCES ServiceAddon(id) ON DELETE RESTRICT,
    CHECK (quantity > 0)
);
```

**Fields:**
- `reservation_id` (INT, FK → Reservation.id, PK)
- `addon_id` (INT, FK → ServiceAddon.id, PK)
- `quantity` (INT, DEFAULT 1, CHECK > 0)

**Purpose:** Links Reservation to ServiceAddon (Decorator Pattern)

---

### 8. Billing Table
```sql
CREATE TABLE Billing (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL UNIQUE,
    subtotal DECIMAL(10, 2) NOT NULL,
    taxRate DECIMAL(5, 4) NOT NULL DEFAULT 0.10,
    taxAmount DECIMAL(10, 2) NOT NULL,
    discountValue DECIMAL(10, 2) DEFAULT 0,
    loyaltyRedeemedPoints INT DEFAULT 0,
    totalAmount DECIMAL(10, 2) NOT NULL,
    paidAmount DECIMAL(10, 2) DEFAULT 0,
    balanceAmount DECIMAL(10, 2) NOT NULL,
    paymentStatus VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    createdBy VARCHAR(100),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id) ON DELETE RESTRICT,
    CHECK (taxRate >= 0 AND taxRate <= 1),
    CHECK (discountValue >= 0),
    CHECK (loyaltyRedeemedPoints >= 0)
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `reservation_id` (INT, FK → Reservation.id, UNIQUE)
- `subtotal` (DECIMAL(10,2), NOT NULL)
- `taxRate` (DECIMAL(5,4), DEFAULT 0.10, CHECK 0-1)
- `taxAmount` (DECIMAL(10,2), NOT NULL)
- `discountValue` (DECIMAL(10,2), DEFAULT 0)
- `loyaltyRedeemedPoints` (INT, DEFAULT 0)
- `totalAmount` (DECIMAL(10,2), NOT NULL)
- `paidAmount` (DECIMAL(10,2), DEFAULT 0)
- `balanceAmount` (DECIMAL(10,2), NOT NULL)
- `paymentStatus` (VARCHAR(50), DEFAULT 'PENDING')
- `createdBy` (VARCHAR(100)) - Admin who created/approved discount
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Relationships:**
- One-to-One with Reservation
- One-to-Many with Payment

**Business Rules:**
- balanceAmount = totalAmount - paidAmount
- Cannot checkout if balanceAmount > 0

---

### 9. Payment Table
```sql
CREATE TABLE Payment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    billing_id INT NOT NULL,
    method ENUM('CASH', 'CARD', 'POINTS') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (billing_id) REFERENCES Billing(id) ON DELETE CASCADE,
    CHECK (amount != 0)
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `billing_id` (INT, FK → Billing.id)
- `method` (ENUM: CASH, CARD, POINTS)
- `amount` (DECIMAL(10,2), NOT NULL) - Can be negative for refunds
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Relationships:**
- Many-to-One with Billing

**Business Rules:**
- Negative amounts represent refunds
- Sum of payments should not exceed totalAmount (except refunds)

---

### 10. Feedback Table
```sql
CREATE TABLE Feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    reservation_id INT NOT NULL,
    rating INT NOT NULL,
    comments TEXT,
    sentimentTag VARCHAR(50),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES Guest(id) ON DELETE RESTRICT,
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id) ON DELETE RESTRICT,
    CHECK (rating >= 1 AND rating <= 5),
    CHECK (LENGTH(comments) <= 1000)
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `guest_id` (INT, FK → Guest.id)
- `reservation_id` (INT, FK → Reservation.id)
- `rating` (INT, CHECK 1-5)
- `comments` (TEXT, MAX 1000 chars)
- `sentimentTag` (VARCHAR(50)) - e.g., 'POSITIVE', 'NEGATIVE', 'NEUTRAL'
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Relationships:**
- Many-to-One with Guest
- Many-to-One with Reservation

**Business Rules:**
- Can only submit after checkout and balance settled

---

### 11. Waitlist Table
```sql
CREATE TABLE Waitlist (
    id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL UNIQUE,
    requestedType ENUM('SINGLE', 'DOUBLE', 'DELUXE', 'PENTHOUSE') NOT NULL,
    dateRangeStart DATE NOT NULL,
    dateRangeEnd DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES Guest(id) ON DELETE CASCADE,
    CHECK (dateRangeEnd > dateRangeStart)
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `guest_id` (INT, FK → Guest.id, UNIQUE)
- `requestedType` (ENUM: SINGLE, DOUBLE, DELUXE, PENTHOUSE)
- `dateRangeStart` (DATE, NOT NULL)
- `dateRangeEnd` (DATE, NOT NULL)
- `status` (VARCHAR(50), DEFAULT 'PENDING')
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Relationships:**
- One-to-One with Guest

**Business Rules:**
- Used by Observer Pattern for room availability notifications

---

### 12. AdminUser Table
```sql
CREATE TABLE AdminUser (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MANAGER') NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `username` (VARCHAR(50), UNIQUE, NOT NULL)
- `passwordHash` (VARCHAR(255), NOT NULL) - BCrypt hashed
- `role` (ENUM: ADMIN, MANAGER)
- `active` (BOOLEAN, DEFAULT TRUE)
- `createdAt` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**Security:**
- Passwords must be hashed with BCrypt
- Role-based access control

---

### 13. AuditLog Table
```sql
CREATE TABLE AuditLog (
    id INT PRIMARY KEY AUTO_INCREMENT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actor VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entityType VARCHAR(50) NOT NULL,
    entityId INT,
    message TEXT NOT NULL
);
```

**Fields:**
- `id` (INT, PK, AUTO_INCREMENT)
- `timestamp` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- `actor` (VARCHAR(100), NOT NULL) - Username or system
- `action` (VARCHAR(100), NOT NULL) - e.g., 'LOGIN', 'CREATE_RESERVATION', 'APPLY_DISCOUNT'
- `entityType` (VARCHAR(50), NOT NULL) - e.g., 'RESERVATION', 'BILLING', 'GUEST'
- `entityId` (INT) - ID of affected entity
- `message` (TEXT, NOT NULL) - Descriptive message

**Purpose:**
- Tracks all administrative actions
- Used for activity logs reporting

---

## Indexes

```sql
-- Performance indexes
CREATE INDEX idx_room_status ON Room(status);
CREATE INDEX idx_room_type ON Room(type);
CREATE INDEX idx_reservation_status ON Reservation(status);
CREATE INDEX idx_reservation_dates ON Reservation(checkIn, checkOut);
CREATE INDEX idx_guest_email ON Guest(email);
CREATE INDEX idx_guest_phone ON Guest(phone);
CREATE INDEX idx_billing_reservation ON Billing(reservation_id);
CREATE INDEX idx_payment_billing ON Payment(billing_id);
CREATE INDEX idx_feedback_reservation ON Feedback(reservation_id);
CREATE INDEX idx_auditlog_timestamp ON AuditLog(timestamp);
CREATE INDEX idx_auditlog_actor ON AuditLog(actor);
```

---

## Relationships Summary

| Relationship | Type | Description |
|-------------|------|-------------|
| Hotel → Room | 1:N | One hotel has many rooms |
| Guest → Reservation | 1:N | One guest can have many reservations |
| Guest → Feedback | 1:N | One guest can submit multiple feedbacks |
| Guest → Waitlist | 1:1 | One guest can have one waitlist entry |
| Reservation → Feedback | 1:N | One reservation can have multiple feedbacks |
| Reservation → ReservationRoom | 1:N | One reservation can have multiple rooms |
| Reservation → ReservationAddon | 1:N | One reservation can have multiple addons |
| Reservation → Billing | 1:1 | One reservation has one billing |
| Room → ReservationRoom | 1:N | One room can be in multiple reservations |
| ServiceAddon → ReservationAddon | 1:N | One addon can be in multiple reservations |
| Billing → Payment | 1:N | One billing can have multiple payments |
| AdminUser → AuditLog | 1:N | One admin can have many audit logs |

---

## Cascade Rules

- **ON DELETE CASCADE**: ReservationRoom, ReservationAddon, Payment (when parent deleted)
- **ON DELETE RESTRICT**: Reservation, Billing, Feedback (prevent deletion if referenced)
- **ON DELETE CASCADE**: Waitlist (when guest deleted)

---

## Validation Rules (Database Level)

1. **Date Validation**: checkOut > checkIn
2. **Occupancy**: numAdults > 0
3. **Rating**: 1 <= rating <= 5
4. **Comments**: LENGTH(comments) <= 1000
5. **Tax Rate**: 0 <= taxRate <= 1
6. **Discounts**: discountValue >= 0
7. **Loyalty Points**: loyaltyRedeemedPoints >= 0
8. **Payment Amount**: amount != 0

---

## Sample Data (Optional for Testing)

```sql
-- Sample Hotel
INSERT INTO Hotel (name, city) VALUES ('Grand Paradise Hotel', 'Mumbai');

-- Sample Rooms
INSERT INTO Room (hotel_id, roomNumber, type, beds, basePrice, status) VALUES
(1, '101', 'SINGLE', 1, 2000.00, 'AVAILABLE'),
(1, '102', 'DOUBLE', 2, 3500.00, 'AVAILABLE'),
(1, '201', 'DELUXE', 1, 5000.00, 'AVAILABLE'),
(1, '301', 'PENTHOUSE', 1, 10000.00, 'AVAILABLE');

-- Sample Service Addons
INSERT INTO ServiceAddon (name, price, pricingModel) VALUES
('Wi-Fi', 200.00, 'PER_NIGHT'),
('Breakfast', 500.00, 'PER_NIGHT'),
('Parking', 300.00, 'PER_RESERVATION'),
('Spa', 1500.00, 'PER_RESERVATION');

-- Sample Admin User (password: admin123 - hash this with BCrypt)
INSERT INTO AdminUser (username, passwordHash, role) VALUES
('admin', '$2a$10$...', 'ADMIN'),
('manager', '$2a$10$...', 'MANAGER');
```

---

## Database Script File

Save this as `hotel_reservation_schema.sql` for submission.

