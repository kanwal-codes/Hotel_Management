# 👨‍🏫 Instructor/Professor Setup Guide

**Complete guide for running the Hotel Reservation System on any PC**

---

## 📋 **Prerequisites**

The professor will need:
1. **Java 17** (JDK 17 or higher)
2. **MySQL 8.0+** (or MySQL 5.7+)
3. **Maven** (usually comes with IntelliJ IDEA)
4. **IntelliJ IDEA** (or any Java IDE with JavaFX support)

---

## 🚀 **Quick Setup (5 Steps)**

### **Step 1: Install Java 17**
- Download from: https://adoptium.net/ or Oracle
- Verify: `java -version` (should show 17 or higher)

### **Step 2: Install MySQL**
- **Windows:** Download from https://dev.mysql.com/downloads/mysql/
- **macOS:** `brew install mysql` or download installer
- **Linux:** `sudo apt-get install mysql-server` or `sudo yum install mysql-server`

### **Step 3: Set Up Database**

#### **Option A: Using Command Line**
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE hotel_db;"

# Run schema script
mysql -u root -p hotel_db < database/create_schema.sql

# Run seed data script
mysql -u root -p hotel_db < database/seed_data.sql
```

#### **Option B: Using MySQL Workbench**
1. Open MySQL Workbench
2. Connect to MySQL server
3. Open `database/create_schema.sql` → Execute
4. Open `database/seed_data.sql` → Execute

### **Step 4: Update Database Credentials**

Edit: `src/main/resources/META-INF/persistence.xml`

**Lines 30-31:**
```xml
<property name="javax.persistence.jdbc.user" value="root"/>
<property name="javax.persistence.jdbc.password" value="YOUR_MYSQL_PASSWORD"/>
```

**If MySQL is on different port (line 29):**
```xml
<property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:YOUR_PORT/hotel_db?useSSL=false&amp;serverTimezone=UTC"/>
```

### **Step 5: Run in IntelliJ IDEA**

1. **Open Project:**
   - File → Open → Select project folder
   - IntelliJ will detect Maven project

2. **Wait for Maven Sync:**
   - IntelliJ will download dependencies automatically
   - Check bottom-right corner for progress

3. **Run Application:**
   - Right-click `src/main/java/com/hotel/app/Main.java`
   - Select "Run 'Main.main()'"
   - Or press `Shift+F10`

---

## ✅ **Verification**

### **Check Database:**
```sql
USE hotel_db;
SHOW TABLES;  -- Should show 13 tables
SELECT * FROM admin_users;  -- Should show 2 users
```

### **Test Login:**
- Username: `admin`
- Password: `admin123`

---

## 📁 **Project Structure**

```
Project/
├── src/main/java/          # Source code
├── src/main/resources/     # FXML, CSS, persistence.xml
├── database/               # SQL scripts (REQUIRED)
│   ├── create_schema.sql   # Creates all tables
│   └── seed_data.sql       # Initial data
├── pom.xml                 # Maven configuration
└── README.md               # Project overview
```

---

## 🔧 **Common Issues & Solutions**

### **Issue 1: "MySQL not found"**
**Solution:** Install MySQL and add to PATH

### **Issue 2: "Connection refused"**
**Solution:** 
- Start MySQL service
- Windows: Services → MySQL → Start
- macOS: `brew services start mysql`
- Linux: `sudo systemctl start mysql`

### **Issue 3: "Access denied for user 'root'"**
**Solution:** Update password in `persistence.xml` line 31

### **Issue 4: "Database hotel_db doesn't exist"**
**Solution:** Run `create_schema.sql` script

### **Issue 5: "JavaFX classes not found"**
**Solution:** 
- Maven should handle this automatically
- If not: File → Invalidate Caches / Restart

### **Issue 6: "Port 3306 already in use"**
**Solution:** 
- Change MySQL port, or
- Update `persistence.xml` with correct port

---

## 📝 **What to Submit**

The professor should receive:
1. ✅ **Complete source code** (all `.java` files)
2. ✅ **SQL scripts** (`database/create_schema.sql`, `database/seed_data.sql`)
3. ✅ **Configuration files** (`pom.xml`, `persistence.xml`)
4. ✅ **FXML files** (UI definitions)
5. ✅ **Documentation** (README, setup guides)

---

## 🎯 **Expected Behavior**

After setup, the application should:
1. ✅ Start without errors
2. ✅ Show welcome screen
3. ✅ Allow admin login
4. ✅ Display dashboard
5. ✅ Allow creating/viewing reservations
6. ✅ Process payments
7. ✅ Generate reports

---

## 📞 **Support Information**

**Default Admin Credentials:**
- Username: `admin`
- Password: `admin123`

**Database Name:** `hotel_db`

**Connection:** `localhost:3306`

---

## ⚠️ **Important Notes**

1. **MySQL Must Be Running** - Application requires MySQL to start
2. **Database Must Exist** - Run SQL scripts before running application
3. **Credentials Must Match** - Update `persistence.xml` if MySQL has password
4. **Java 17 Required** - Lower versions won't work (JavaFX 17 requirement)

---

## 🔄 **Alternative: Using Different Database**

If professor wants to use different database name/credentials:

1. Create database with different name
2. Update `persistence.xml`:
   - Line 29: Change `hotel_db` to new database name
   - Line 30: Update username
   - Line 31: Update password
3. Run SQL scripts on new database

---

**Last Updated:** After project completion  
**Compatibility:** Windows, macOS, Linux  
**Difficulty:** Easy (5 steps, ~10 minutes)

