# 🚀 IntelliJ Setup & Run Guide

## ✅ Pre-Run Checklist

### 1. **Database Setup (REQUIRED)**
- [ ] MySQL is installed and running
- [ ] Database `hotel_db` is created
- [ ] SQL scripts are executed:
  - [ ] `database/create_schema.sql` - Creates all tables
  - [ ] `database/seed_data.sql` - Populates initial data
- [ ] Database credentials in `persistence.xml` are correct:
  - Username: `root` (or your MySQL username)
  - Password: (empty or your MySQL password)
  - Port: `3306` (default)

### 2. **Maven Dependencies**
- [ ] IntelliJ has downloaded all Maven dependencies
- [ ] Check: File → Project Structure → Libraries (should show all dependencies)

### 3. **Java Version**
- [ ] Java 17 is configured (required for JavaFX 17)
- [ ] Check: File → Project Structure → Project → SDK: Java 17

### 4. **JavaFX Configuration**
- [ ] JavaFX SDK is configured (if using module path)
- [ ] Or: JavaFX dependencies are in Maven (already done in `pom.xml`)

---

## 📝 Step-by-Step Setup in IntelliJ

### Step 1: Open Project
1. **File → Open** → Select project folder
2. IntelliJ will detect it's a Maven project
3. Wait for Maven to sync (bottom right corner)

### Step 2: Configure Database
1. **Open MySQL Workbench** (or command line)
2. **Run SQL scripts:**
   ```sql
   -- In MySQL:
   source /path/to/database/create_schema.sql
   source /path/to/database/seed_data.sql
   ```
   OR manually:
   - Open `database/create_schema.sql` in MySQL Workbench
   - Execute the script
   - Open `database/seed_data.sql`
   - Execute the script

3. **Verify database:**
   ```sql
   USE hotel_db;
   SHOW TABLES;  -- Should show 13 tables
   SELECT * FROM admin_users;  -- Should show 2 admin users
   ```

### Step 3: Update Database Credentials (if needed)
1. Open: `src/main/resources/META-INF/persistence.xml`
2. Update lines 30-31 if your MySQL credentials differ:
   ```xml
   <property name="javax.persistence.jdbc.user" value="root"/>
   <property name="javax.persistence.jdbc.password" value=""/>
   ```

### Step 4: Configure Run Configuration
1. **Right-click `Main.java`** → **Run 'Main.main()'**
2. OR: **Run → Edit Configurations**
3. **Add New → Application**
   - **Name:** Hotel Reservation System
   - **Main class:** `com.hotel.app.Main`
   - **VM options:** (usually not needed, Maven handles JavaFX)
   - **Working directory:** Project root

### Step 5: Run the Application
1. **Click Run** (green play button) or press `Shift+F10`
2. **Expected:** Welcome screen should appear

---

## ⚠️ Common Issues & Solutions

### Issue 1: "Cannot connect to database"
**Error:** `Communications link failure` or `Access denied`

**Solutions:**
- ✅ Check MySQL is running: `mysql -u root -p`
- ✅ Verify database exists: `SHOW DATABASES;` (should see `hotel_db`)
- ✅ Check credentials in `persistence.xml`
- ✅ Verify MySQL port is 3306

### Issue 2: "ClassNotFoundException: javafx..."
**Error:** JavaFX classes not found

**Solutions:**
- ✅ Maven should handle this automatically
- ✅ Try: **File → Invalidate Caches / Restart**
- ✅ Rebuild: **Build → Rebuild Project**
- ✅ Check Maven dependencies downloaded: **View → Tool Windows → Maven**

### Issue 3: "FXML file not found"
**Error:** `java.lang.NullPointerException` when loading FXML

**Solutions:**
- ✅ Verify FXML files are in `src/main/resources/view/`
- ✅ Check file paths in controllers match actual file locations
- ✅ Mark `src/main/resources` as **Resources Root**: Right-click folder → **Mark Directory as → Resources Root**

### Issue 4: "EntityManagerFactory not found"
**Error:** `No Persistence provider for EntityManager named hotelPU`

**Solutions:**
- ✅ Verify `persistence.xml` is in `src/main/resources/META-INF/`
- ✅ Check persistence unit name matches: `name="hotelPU"`
- ✅ Verify all entity classes are listed in `persistence.xml`

### Issue 5: "Table doesn't exist"
**Error:** `Table 'hotel_db.rooms' doesn't exist`

**Solutions:**
- ✅ Run `create_schema.sql` script
- ✅ Check `hibernate.hbm2ddl.auto` is set to `update` in `persistence.xml`
- ✅ Verify database name is `hotel_db`

---

## 🧪 Testing the Application

### Test 1: Kiosk Booking Flow
1. **Start application** → Should show Welcome screen
2. **Click "Start Booking"** → Should navigate to Date Selection
3. **Select dates** → Should navigate to Guest Details
4. **Fill guest info** → Should navigate to Room Selection
5. **Select room** → Should navigate to Add-ons
6. **Select add-ons** → Should show Booking Summary
7. **Confirm booking** → Should show Confirmation

### Test 2: Admin Login
1. **From Welcome screen** → Click "Admin Login" (if available)
2. **OR navigate to:** `/view/admin/LoginScreen.fxml`
3. **Login credentials:**
   - Username: `admin`
   - Password: `admin123`
   - OR: Username: `manager`, Password: `manager123`

### Test 3: Database Connection
1. **Check logs:** Look for `EntityManagerFactory created successfully`
2. **Check console:** Should see Hibernate SQL queries (if `show_sql=true`)
3. **Try creating a reservation:** Should save to database

---

## 📊 Expected Behavior

### ✅ Application Starts Successfully
- Welcome screen appears
- No errors in console
- Database connection established

### ✅ Kiosk Flow Works
- All screens navigate correctly
- Room selection shows available rooms
- Booking summary calculates prices correctly
- Reservation is saved to database

### ✅ Admin Features Work
- Login successful
- Dashboard shows reservations
- Can process payments
- Can view reports

---

## 🔍 Debugging Tips

### Enable SQL Logging
Already enabled in `persistence.xml`:
```xml
<property name="hibernate.show_sql" value="true"/>
```

### Check Logs
- **Application logs:** `system_logs.0.log` in project root
- **Console output:** Check IntelliJ Run window

### Verify Database
```sql
-- Check if data exists:
SELECT * FROM hotels;
SELECT * FROM rooms;
SELECT * FROM admin_users;
SELECT * FROM service_addons;
```

---

## ✅ Success Indicators

When everything is working:
- ✅ Application launches without errors
- ✅ Welcome screen displays correctly
- ✅ Can navigate through kiosk booking flow
- ✅ Can login as admin
- ✅ Database operations work (create, read, update)
- ✅ Logs show successful operations

---

## 📞 Quick Reference

**Main Class:** `com.hotel.app.Main`  
**Database:** `hotel_db` (MySQL)  
**Default Port:** `3306`  
**Admin Username:** `admin`  
**Admin Password:** `admin123`  
**Persistence Unit:** `hotelPU`  

**Key Files:**
- `src/main/java/com/hotel/app/Main.java` - Entry point
- `src/main/resources/META-INF/persistence.xml` - Database config
- `database/create_schema.sql` - Database schema
- `database/seed_data.sql` - Initial data

