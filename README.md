# 🏨 Hotel Reservation System

<div align="center">

**A feature-rich JavaFX desktop application for managing hotel reservations and billing**

Built to practice **object-oriented design**, **JavaFX UI development**, and **Hibernate-based persistence** in a real-world domain.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

</div>

---

## 📌 Overview

The **Hotel Reservation System** is a desktop-based Java application designed to model real hotel workflows such as:

* Room reservations and booking management
* Guest self-service kiosk interface
* Billing and checkout processing
* Loyalty programs and rewards
* Waitlist management
* Reporting and analytics

The application provides **two interfaces**:

* **Admin Dashboard** for hotel staff management
* **Customer Kiosk** for guest self-service bookings

This project focuses on **clean architecture**, **design patterns**, and **realistic business logic**, demonstrating enterprise-level Java development practices.

---

## 🧠 Motivation & Learning Goals

This project was built to:

* Apply **OOP principles** in a non-trivial system
* Practice **JavaFX desktop UI development**
* Work with **Hibernate ORM + MySQL**
* Implement **design patterns** (Observer, Strategy, Factory)
* Design a layered architecture (MVC-style)
* Simulate real-world hotel operations and pricing rules
* Demonstrate **JPA/Hibernate** persistence best practices

---

## ✨ Features

### Admin Dashboard

* **Reservation Management**: Create, view, update, and cancel reservations
* **Room Inventory**: Track room availability and status
* **Checkout & Billing**: Process guest checkouts with detailed billing
* **Payment Processing**: Handle payment transactions
* **Waitlist Management**: Manage waitlist entries and convert to reservations
* **Loyalty Program**: Configure loyalty tiers and benefits
* **Feedback Management**: View and manage guest feedback
* **Reports Generation**:
  * Revenue reports
  * Occupancy statistics
  * Guest feedback summaries
  * System activity logs
* **Discount Application**: Apply discounts to reservations
* **CSV and PDF Export**: Export reports in multiple formats

### Customer Kiosk

* **Self-Service Booking**: Step-by-step booking flow
* **Date Selection**: Flexible check-in and check-out date picker
* **Room Selection**: Browse and select available rooms
* **Add-on Services**: Select additional services (WiFi, Breakfast, etc.)
* **Booking Summary**: Review booking details before payment
* **Payment Processing**: Secure payment handling
* **Reservation Lookup**: Check existing reservations
* **Loyalty Dashboard**: View loyalty points and benefits
* **Feedback Submission**: Submit feedback after checkout
* **Help & Support**: Built-in help system

---

## 🎯 Core Functionality

* **Automatic Room Assignment**: Based on availability and preferences
* **Rule-Based Pricing**:
  * Weekend and seasonal multipliers
  * Configurable tax rates
  * Dynamic pricing policies
* **Loyalty System**:
  * Automatic enrollment
  * Points accumulation
  * Tier-based benefits
  * Points redemption
* **Observer Pattern**: Real-time waitlist notifications when rooms become available
* **Add-on Services**: Flexible service selection and pricing
* **Audit Logging**: Complete activity tracking for compliance
* **Session Management**: Secure customer session handling

---

## 🛠️ Technology Stack

### Core

* **Java 17**
* **JavaFX 17**
* **Maven 3.6+**

### Persistence

* **MySQL 8.0+**
* **Hibernate ORM 5.6**
* **JPA (Java Persistence API)**

### Architecture & Patterns

* **MVC-style layered architecture**
* **Repository Pattern**: Data access abstraction
* **Observer Pattern**: Waitlist notifications
* **Strategy Pattern**: Pricing and billing strategies
* **Factory Pattern**: Object creation
* **Service Layer**: Business logic separation

### Utilities

* **BCrypt**: Password hashing
* **Apache PDFBox**: PDF report generation
* **Apache Commons CSV**: CSV export functionality
* **Hibernate Validator**: Input validation
* **Java Util Logging**: Application logging

---

## 💻 System Requirements

* **Java JDK 17+**
* **MySQL 8.0+**
* **4 GB RAM** (8 GB recommended)
* **Windows / macOS / Linux**
* **Maven 3.6+**
* **IntelliJ IDEA** (recommended) or Eclipse

---

## 🚀 Quick Start

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/kanwal-codes/Hotel_Reservation_System.git
cd Hotel_Reservation_System
```

### 2️⃣ Create Database

```sql
CREATE DATABASE hotel_db;
```

### 3️⃣ Set Up Database Schema

```bash
# Run the database setup script
mysql -u root -p hotel_db < database/create_schema.sql
mysql -u root -p hotel_db < database/seed_data.sql
```

Or use the provided setup script:
```bash
chmod +x QUICK_DATABASE_SETUP.sh
./QUICK_DATABASE_SETUP.sh
```

### 4️⃣ Configure Database Connection

Edit `src/main/resources/META-INF/persistence.xml`:

```xml
<property name="javax.persistence.jdbc.user" value="YOUR_DATABASE_USERNAME"/>
<property name="javax.persistence.jdbc.password" value="YOUR_DATABASE_PASSWORD"/>
```

### 5️⃣ Build & Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn javafx:run
```

Or run from IDE:
- Open project in IntelliJ IDEA
- Navigate to `src/main/java/com/hotel/app/Main.java`
- Run the `main()` method

---

## 🌱 Database Seeding

The project includes database seeding utilities:

### **Using Seed Scripts**

Run the SQL scripts in order:
```bash
mysql -u root -p hotel_db < database/create_schema.sql
mysql -u root -p hotel_db < database/seed_data.sql
```

### **Using Java Seed Classes**

Run `SeedData.java` from your IDE:
```
src/main/java/com/hotel/app/SeedData.java
```

This creates:
* Admin and manager accounts
* Sample guest users
* Room inventory
* Service add-ons

### Default Credentials

| Role    | Username | Password   |
| ------- | -------- | ---------- |
| Admin   | `admin`  | `admin123` |
| Manager | `manager` | `manager123` |
| Guest   | Use seeded guest email | `guest123` |

---

## 📁 Project Structure

```
hotel-reservation-system/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/
│   │   │   ├── app/              # Application entry point
│   │   │   ├── config/           # Business policies (Pricing, Loyalty, Discount)
│   │   │   ├── controller/       # JavaFX controllers (MVC)
│   │   │   │   ├── admin/        # Admin panel controllers
│   │   │   │   ├── kiosk/        # Customer kiosk controllers
│   │   │   │   └── base/         # Base controller classes
│   │   │   ├── model/            # JPA entity classes
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── service/          # Business logic layer
│   │   │   ├── security/         # Authentication & password hashing
│   │   │   ├── session/          # Session management
│   │   │   ├── util/             # Utilities (logging, exporters)
│   │   │   └── events/           # Observer pattern implementation
│   │   └── resources/
│   │       ├── view/             # FXML files
│   │       │   ├── kiosk/        # Customer kiosk views
│   │       │   ├── admin/        # Admin panel views
│   │       │   └── feedback/     # Feedback views
│   │       ├── styles/           # CSS files
│   │       └── META-INF/
│   │           └── persistence.xml  # JPA configuration
│   └── test/                     # Test classes
├── database/                      # SQL scripts
│   ├── create_schema.sql
│   ├── seed_data.sql
│   └── ...
├── docs/                          # Project documentation
├── pom.xml                        # Maven configuration
└── README.md
```

---

## 🎨 Design Highlights

### Dynamic Pricing

* Weekend and seasonal multipliers
* Configurable tax rules
* Discount policies
* Add-on service pricing

### Loyalty Program

* Automatic guest enrollment
* Points-based rewards system
* Tiered benefits structure
* Points redemption support

### Observer Pattern

* Real-time waitlist notifications
* Room availability updates
* Event-driven architecture

### Reporting

* Revenue and occupancy analysis
* Feedback summaries
* CSV and PDF export capabilities
* In-app report preview

---

## 🚧 Limitations & Future Improvements

### Current Limitations

* Desktop-only application (JavaFX)
* Single-hotel support
* Simulated payment processing
* Limited automated testing

### Planned Improvements

* Web version (Spring Boot backend)
* REST API for mobile integration
* Comprehensive test suite (JUnit)
* Enhanced role-based access control
* Dockerized setup for easy deployment
* Multi-hotel support

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

Licensed under the **MIT License**.

---

## 👤 Author

**Kanwaljot Singh**

* GitHub: [@kanwal-codes](https://github.com/kanwal-codes)
* Repository: [Hotel_Reservation_System](https://github.com/kanwal-codes/Hotel_Reservation_System)

---

## 📚 Documentation

For detailed documentation, see the `docs/` folder:

* **Getting Started**: `docs/01-getting-started/`
* **Project Overview**: `docs/02-project-overview/`
* **Requirements**: `docs/03-requirements-compliance/`
* **Implementation Details**: `docs/04-implementation/`

---

## ⚠️ Important Notes

* **Database Credentials**: Update `persistence.xml` with your MySQL credentials before running
* **Schema Creation**: Run `create_schema.sql` before `seed_data.sql`
* **Java Version**: Requires Java 17 or higher
* **MySQL Version**: Requires MySQL 8.0 or higher

---

⭐ If you find this project useful, consider starring the repository!

---

<div align="center">

**Made with ❤️ using Java and JavaFX**

</div>
