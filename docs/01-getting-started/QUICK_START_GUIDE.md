# Quick Start Guide - Hotel Reservation System

## 🎯 Goal: Get a Working Foundation in 2 Days

This guide focuses on getting the **core foundation** working so you can build everything else on top.

---

## Day 1: Database & Models

### Step 1: Database Setup (30 minutes)

1. **Install MySQL** (if not already installed)

2. **Create Database:**
   ```sql
   CREATE DATABASE hotel_db;
   USE hotel_db;
   ```

3. **Update `persistence.xml`:**
   - Set your MySQL username/password
   - Verify connection string

4. **Test Connection:**
   Create a simple test class:
   ```java
   EntityManagerFactory emf = Persistence.createEntityManagerFactory("hotelPU");
   EntityManager em = emf.createEntityManager();
   System.out.println("Connected!");
   em.close();
   ```

---

### Step 2: Create Enums (1 hour)

**File: `model/RoomType.java`**
```java
package com.hotel.model;

public enum RoomType {
    SINGLE, DOUBLE, DELUXE, PENTHOUSE
}
```

**File: `model/ReservationStatus.java`**
```java
package com.hotel.model;

public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, CHECKED_OUT
}
```

**File: `model/RoomStatus.java`**
```java
package com.hotel.model;

public enum RoomStatus {
    AVAILABLE, OCCUPIED, MAINTENANCE
}
```

**File: `model/Role.java`**
```java
package com.hotel.model;

public enum Role {
    ADMIN, MANAGER
}
```

**File: `model/PaymentMethod.java`**
```java
package com.hotel.model;

public enum PaymentMethod {
    CASH, CARD, POINTS
}
```

**File: `model/PricingModel.java`**
```java
package com.hotel.model;

public enum PricingModel {
    PER_NIGHT, PER_RESERVATION
}
```

---

### Step 3: Create Simple Entities (2 hours)

**File: `model/Hotel.java`**
```java
package com.hotel.model;

import javax.persistence.*;

@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String city;
    
    // Constructors, getters, setters
}
```

**File: `model/Room.java`**
```java
package com.hotel.model;

import javax.persistence.*;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String roomNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;
    
    @Column(nullable = false)
    private int beds;
    
    @Column(nullable = false)
    private double basePrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;
    
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    
    // Constructors, getters, setters
}
```

**File: `model/Guest.java`**
```java
package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "guest")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String phone;
    
    @Email
    @Column(nullable = false)
    private String email;
    
    private String address;
    
    @Column(name = "loyalty_points")
    private int loyaltyPoints = 0;
    
    // Constructors, getters, setters
}
```

**File: `model/AdminUser.java`**
```java
package com.hotel.model;

import javax.persistence.*;

@Entity
@Table(name = "admin_user")
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(nullable = false)
    private boolean active = true;
    
    // Constructors, getters, setters
}
```

---

### Step 4: Test Models (30 minutes)

Create a simple test:
```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("hotelPU");
EntityManager em = emf.createEntityManager();

em.getTransaction().begin();

Hotel hotel = new Hotel();
hotel.setName("Grand Hotel");
hotel.setCity("New York");
em.persist(hotel);

Room room = new Room();
room.setRoomNumber("101");
room.setType(RoomType.SINGLE);
room.setBeds(1);
room.setBasePrice(100.0);
room.setHotel(hotel);
em.persist(room);

em.getTransaction().commit();
em.close();

System.out.println("Models work!");
```

---

## Day 2: Repositories & First Service

### Step 5: Create Repository Interfaces (2 hours)

**File: `repository/GuestRepository.java`**
```java
package com.hotel.repository;

import com.hotel.model.Guest;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GuestRepository {
    private EntityManager em;
    
    public GuestRepository(EntityManager em) {
        this.em = em;
    }
    
    public Guest save(Guest guest) {
        em.persist(guest);
        return guest;
    }
    
    public Optional<Guest> findById(Long id) {
        return Optional.ofNullable(em.find(Guest.class, id));
    }
    
    public Optional<Guest> findByEmail(String email) {
        return em.createQuery("SELECT g FROM Guest g WHERE g.email = :email", Guest.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
    
    public List<Guest> findAll() {
        return em.createQuery("SELECT g FROM Guest g", Guest.class)
                .getResultList();
    }
}
```

**File: `repository/RoomRepository.java`**
```java
package com.hotel.repository;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.model.RoomStatus;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class RoomRepository {
    private EntityManager em;
    
    public RoomRepository(EntityManager em) {
        this.em = em;
    }
    
    public Room save(Room room) {
        em.persist(room);
        return room;
    }
    
    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(em.find(Room.class, id));
    }
    
    public List<Room> findAvailableByType(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        // Query for rooms that are available and not booked in date range
        String query = "SELECT r FROM Room r WHERE r.type = :type " +
                      "AND r.status = :status " +
                      "AND r.id NOT IN (" +
                      "SELECT rr.room.id FROM ReservationRoom rr " +
                      "JOIN rr.reservation res " +
                      "WHERE (res.checkIn <= :checkOut AND res.checkOut >= :checkIn)" +
                      ")";
        
        return em.createQuery(query, Room.class)
                .setParameter("type", type)
                .setParameter("status", RoomStatus.AVAILABLE)
                .setParameter("checkIn", checkIn)
                .setParameter("checkOut", checkOut)
                .getResultList();
    }
}
```

**File: `repository/AdminUserRepository.java`**
```java
package com.hotel.repository;

import com.hotel.model.AdminUser;
import javax.persistence.EntityManager;
import java.util.Optional;

public class AdminUserRepository {
    private EntityManager em;
    
    public AdminUserRepository(EntityManager em) {
        this.em = em;
    }
    
    public AdminUser save(AdminUser admin) {
        em.persist(admin);
        return admin;
    }
    
    public Optional<AdminUser> findByUsername(String username) {
        return em.createQuery("SELECT a FROM AdminUser a WHERE a.username = :username", AdminUser.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
```

---

### Step 6: Create LoggerService (Singleton) (1 hour)

**File: `util/LoggerService.java`**
```java
package com.hotel.util;

import java.io.IOException;
import java.util.logging.*;

public class LoggerService {
    private static LoggerService instance;
    private Logger logger;
    
    private LoggerService() {
        logger = Logger.getLogger("HotelSystem");
        logger.setLevel(Level.ALL);
        
        try {
            FileHandler fileHandler = new FileHandler("system_logs.%g.log", 1024 * 1024, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            
            // Also log to console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize logger", e);
        }
    }
    
    public static LoggerService getInstance() {
        if (instance == null) {
            instance = new LoggerService();
        }
        return instance;
    }
    
    public void logInfo(String message) {
        logger.info(message);
    }
    
    public void logError(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
    
    public void logActivity(String actor, String action, String entityType, Long entityId, String message) {
        String logMessage = String.format("[%s] %s - %s (ID: %d): %s", 
            actor, action, entityType, entityId, message);
        logger.info(logMessage);
    }
}
```

---

### Step 7: Create BCryptPasswordHasher (30 minutes)

**File: `security/BCryptPasswordHasher.java`**
```java
package com.hotel.security;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordHasher {
    
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
```

---

### Step 8: Create AuthService (1 hour)

**File: `service/AuthService.java`**
```java
package com.hotel.service;

import com.hotel.model.AdminUser;
import com.hotel.repository.AdminUserRepository;
import com.hotel.security.BCryptPasswordHasher;
import com.hotel.util.LoggerService;
import javax.persistence.EntityManager;
import java.util.Optional;

public class AuthService {
    private AdminUserRepository adminUserRepository;
    private LoggerService logger;
    
    public AuthService(EntityManager em) {
        this.adminUserRepository = new AdminUserRepository(em);
        this.logger = LoggerService.getInstance();
    }
    
    public Optional<AdminUser> login(String username, String password) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            if (BCryptPasswordHasher.verify(password, admin.getPasswordHash())) {
                logger.logActivity(admin.getUsername(), "LOGIN", "AdminUser", admin.getId(), "Successful login");
                return Optional.of(admin);
            } else {
                logger.logActivity(username, "LOGIN_FAILED", "AdminUser", null, "Invalid password");
            }
        } else {
            logger.logActivity(username, "LOGIN_FAILED", "AdminUser", null, "User not found");
        }
        
        return Optional.empty();
    }
    
    public boolean hasRole(AdminUser admin, String role) {
        return admin.getRole().name().equals(role);
    }
}
```

---

### Step 9: Create Seed Data (30 minutes)

**File: `app/SeedData.java`**
```java
package com.hotel.app;

import com.hotel.model.*;
import com.hotel.security.BCryptPasswordHasher;
import javax.persistence.EntityManager;

public class SeedData {
    
    public static void seed(EntityManager em) {
        em.getTransaction().begin();
        
        // Create admin users
        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setPasswordHash(BCryptPasswordHasher.hash("admin123"));
        admin.setRole(Role.ADMIN);
        em.persist(admin);
        
        AdminUser manager = new AdminUser();
        manager.setUsername("manager");
        manager.setPasswordHash(BCryptPasswordHasher.hash("manager123"));
        manager.setRole(Role.MANAGER);
        em.persist(manager);
        
        // Create hotel
        Hotel hotel = new Hotel();
        hotel.setName("Grand Hotel");
        hotel.setCity("New York");
        em.persist(hotel);
        
        // Create rooms
        for (int i = 1; i <= 10; i++) {
            Room room = new Room();
            room.setRoomNumber("10" + i);
            room.setType(i <= 4 ? RoomType.SINGLE : RoomType.DOUBLE);
            room.setBeds(i <= 4 ? 1 : 2);
            room.setBasePrice(i <= 4 ? 100.0 : 150.0);
            room.setHotel(hotel);
            em.persist(room);
        }
        
        em.getTransaction().commit();
        System.out.println("Seed data created!");
    }
}
```

---

### Step 10: Test Everything (1 hour)

Create a test class:
```java
public class TestFoundation {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hotelPU");
        EntityManager em = emf.createEntityManager();
        
        // Seed data
        SeedData.seed(em);
        
        // Test AuthService
        AuthService authService = new AuthService(em);
        Optional<AdminUser> admin = authService.login("admin", "admin123");
        System.out.println("Login successful: " + admin.isPresent());
        
        // Test LoggerService
        LoggerService logger = LoggerService.getInstance();
        logger.logInfo("Test log message");
        
        em.close();
        emf.close();
        
        System.out.println("Foundation test complete!");
    }
}
```

---

## ✅ After Day 2, You Should Have:

- [x] Database connected
- [x] All enums created
- [x] Basic entities (Hotel, Room, Guest, AdminUser)
- [x] Repository interfaces working
- [x] LoggerService (Singleton) working
- [x] BCrypt password hashing working
- [x] AuthService working
- [x] Seed data for testing

---

## 🚀 Next Steps

Once foundation is working:

1. **Complete remaining entities** (Reservation, Billing, Payment, etc.)
2. **Complete remaining repositories**
3. **Create other services** (ReservationService, BillingService, etc.)
4. **Implement design patterns** (Strategy, Observer, Factory, Decorator)
5. **Build UI** (Admin first, then Kiosk)

**See `IMPLEMENTATION_ROADMAP.md` for the full plan!**

---

## 💡 Tips

1. **Test frequently** - Don't wait until everything is done
2. **Use simple examples first** - Get it working, then improve
3. **Check logs** - LoggerService will help debug
4. **Keep it simple** - Don't over-engineer early on
5. **Follow the order** - Each step builds on the previous

**You've got this! Start with Day 1, Step 1! 🎯**



