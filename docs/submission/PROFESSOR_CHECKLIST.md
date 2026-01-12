# ✅ Professor/Instructor - Quick Checklist

**Time Required:** ~10 minutes  
**Difficulty:** Easy

---

## ✅ **Pre-Setup Checklist**

- [ ] Java 17 installed (`java -version`)
- [ ] MySQL installed and running
- [ ] IntelliJ IDEA (or compatible IDE)
- [ ] Maven (usually bundled with IntelliJ)

---

## 📋 **Setup Steps (5 Steps)**

### **Step 1: Extract Project**
- [ ] Extract project folder
- [ ] Note the location

### **Step 2: Set Up Database** (2 minutes)
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE hotel_db;"

# Run scripts (from project root)
mysql -u root -p hotel_db < database/create_schema.sql
mysql -u root -p hotel_db < database/seed_data.sql
```

**OR** use MySQL Workbench:
- [ ] Open `database/create_schema.sql` → Execute
- [ ] Open `database/seed_data.sql` → Execute

### **Step 3: Update Credentials** (1 minute)
- [ ] Open `src/main/resources/META-INF/persistence.xml`
- [ ] Update line 30: MySQL username (if not `root`)
- [ ] Update line 31: MySQL password (if you have one)

### **Step 4: Open in IntelliJ** (2 minutes)
- [ ] File → Open → Select project folder
- [ ] Wait for Maven sync (automatic)
- [ ] Check for any errors (should be none)

### **Step 5: Run Application** (1 minute)
- [ ] Right-click `src/main/java/com/hotel/app/Main.java`
- [ ] Select "Run 'Main.main()'"
- [ ] Application should start

---

## ✅ **Verification**

- [ ] Application starts without errors
- [ ] Welcome screen appears
- [ ] Can login with: `admin` / `admin123`
- [ ] Dashboard loads
- [ ] Can view/create reservations

---

## 🔑 **Login Credentials**

**Admin:**
- Username: `admin`
- Password: `admin123`

**Manager:**
- Username: `manager`
- Password: `manager123`

---

## ⚠️ **Common Issues**

| Issue | Quick Fix |
|-------|-----------|
| "Connection refused" | Start MySQL service |
| "Database doesn't exist" | Run `create_schema.sql` |
| "Access denied" | Update password in `persistence.xml` |
| "JavaFX not found" | Wait for Maven to download dependencies |

---

## 📞 **If Something Goes Wrong**

1. Check MySQL is running: `mysql -u root -p`
2. Verify database exists: `SHOW DATABASES;`
3. Check tables: `USE hotel_db; SHOW TABLES;`
4. Review `INSTRUCTOR_SETUP_GUIDE.md` for detailed help

---

## ✅ **What's Included**

- ✅ Complete source code
- ✅ SQL scripts (ready to run)
- ✅ Maven configuration
- ✅ All dependencies (auto-downloaded)
- ✅ Documentation

---

**Estimated Setup Time:** 10 minutes  
**Platform:** Windows, macOS, Linux  
**Difficulty:** ⭐⭐ Easy

