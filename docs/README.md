# Hotel Reservation System

A JavaFX-based desktop application for hotel reservation and billing management.

## Project Structure

```
hotel-reservation-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hotel/
│   │   │       ├── app/              # Application entry point
│   │   │       ├── config/           # Business policies
│   │   │       ├── controller/      # JavaFX controllers
│   │   │       ├── model/            # Entity classes
│   │   │       ├── repository/      # Data access layer
│   │   │       ├── service/          # Business logic
│   │   │       ├── security/         # Authentication & hashing
│   │   │       ├── util/             # Utilities (logging, exporters)
│   │   │       └── events/           # Observer pattern
│   │   └── resources/
│   │       ├── view/                 # FXML files
│   │       │   ├── kiosk/
│   │       │   ├── admin/
│   │       │   └── feedback/
│   │       ├── styles/               # CSS files
│   │       └── META-INF/
│   │           └── persistence.xml   # JPA configuration
│   └── test/
│       └── java/                     # Test classes
├── docs/                              # Project documentation
├── pom.xml                            # Maven configuration
└── README.md
```

## Setup Instructions

### **For Students/Developers:**
See `DATABASE_SETUP_GUIDE.md` for detailed setup instructions.

### **For Instructors/Professors:**
See `SETUP_FOR_INSTRUCTOR.md` for quick setup guide.

### **Quick Setup:**
1. **Install Requirements:**
   - Java 17 (JDK)
   - MySQL 8.0+
   - IntelliJ IDEA

2. **Set Up Database:**
   ```bash
   mysql -u root -p -e "CREATE DATABASE hotel_db;"
   mysql -u root -p hotel_db < database/create_schema.sql
   mysql -u root -p hotel_db < database/seed_data.sql
   ```

3. **Update Credentials** (if needed):
   - Edit `src/main/resources/META-INF/persistence.xml`
   - Update MySQL username/password (lines 30-31)

4. **Open in IntelliJ:**
   - File → Open → Select project folder
   - Wait for Maven sync
   - Run `Main.java`

5. **Login:**
   - Username: `admin`
   - Password: `admin123`

## Technologies

- **JavaFX** - UI Framework
- **JPA/Hibernate** - ORM
- **MySQL** - Database
- **BCrypt** - Password Hashing
- **Maven** - Build Tool

## Documentation

- **For Students:** See `docs/` folder for complete project documentation
- **For Instructors:** See `SETUP_FOR_INSTRUCTOR.md` or `PROFESSOR_CHECKLIST.md` for quick setup

## Quick Setup for Instructors

1. Install Java 17, MySQL, and IntelliJ IDEA
2. Run SQL scripts: `database/create_schema.sql` and `database/seed_data.sql`
3. Update MySQL credentials in `src/main/resources/META-INF/persistence.xml` (if needed)
4. Open project in IntelliJ → Run `Main.java`
5. Login with: `admin` / `admin123`

**See `SETUP_FOR_INSTRUCTOR.md` for detailed instructions.**




