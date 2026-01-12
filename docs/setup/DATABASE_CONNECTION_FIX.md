# 🔧 Database Connection Error - Fix Guide

## ❌ **Error Message**
```
Caused by: java.net.ConnectException: Connection refused
Communications link failure
```

## 🔍 **Problem**
MySQL server is **not running** or **not accessible** on `localhost:3306`.

---

## ✅ **Solution Steps**

### **Step 1: Check if MySQL is Running**

**On macOS:**
```bash
# Check if MySQL is running
brew services list | grep mysql

# Or check process
ps aux | grep mysql
```

**Start MySQL:**
```bash
# If installed via Homebrew
brew services start mysql

# Or if using MySQL directly
sudo /usr/local/mysql/support-files/mysql.server start
```

**On Windows:**
- Open Services (services.msc)
- Find "MySQL" service
- Right-click → Start

**On Linux:**
```bash
sudo systemctl start mysql
# or
sudo service mysql start
```

---

### **Step 2: Verify MySQL is Running**

Test the connection:
```bash
mysql -u root -p
```

If this works, MySQL is running. If not, you need to start it.

---

### **Step 3: Create the Database**

Once MySQL is running, create the database:

```bash
mysql -u root -p
```

Then in MySQL:
```sql
CREATE DATABASE IF NOT EXISTS hotel_db;
```

---

### **Step 4: Run the SQL Scripts**

```bash
# From project root
mysql -u root -p hotel_db < database/create_schema.sql
mysql -u root -p hotel_db < database/seed_data.sql
```

Or in MySQL Workbench:
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open `database/create_schema.sql`
4. Execute the script
5. Open `database/seed_data.sql`
6. Execute the script

---

### **Step 5: Verify Database Credentials**

Check `src/main/resources/META-INF/persistence.xml`:
- **Username:** `root` (or your MySQL username)
- **Password:** (empty or your MySQL password)
- **Port:** `3306` (default)
- **Database:** `hotel_db`

Update if needed:
```xml
<property name="javax.persistence.jdbc.user" value="your_username"/>
<property name="javax.persistence.jdbc.password" value="your_password"/>
```

---

## 🚨 **Common Issues**

### **Issue 1: MySQL Not Installed**
**Solution:** Install MySQL
- macOS: `brew install mysql`
- Windows: Download from mysql.com
- Linux: `sudo apt-get install mysql-server`

### **Issue 2: Wrong Port**
**Solution:** Check if MySQL is on a different port
```bash
# Check MySQL port
mysql -u root -p -e "SHOW VARIABLES LIKE 'port';"
```

Update `persistence.xml` if port is different.

### **Issue 3: Wrong Password**
**Solution:** Update password in `persistence.xml` or reset MySQL password.

### **Issue 4: Database Doesn't Exist**
**Solution:** Create the database:
```sql
CREATE DATABASE hotel_db;
```

---

## ✅ **Quick Test**

After fixing, test the connection:
```bash
mysql -u root -p -e "USE hotel_db; SHOW TABLES;"
```

Should show 13 tables if database is set up correctly.

---

## 🎯 **After Fixing**

1. **Restart the application** in IntelliJ
2. **Check logs** - should see "EntityManagerFactory created successfully"
3. **Application should start** without connection errors

---

## 📝 **Note**

The application **requires MySQL to be running** before it can start. This is expected behavior - the application needs the database to function.

**If you want to test the UI without database:**
- You would need to modify `AppConfig.initialize()` to handle connection failures gracefully
- But for normal operation, MySQL must be running

---

**Last Updated:** After database connection error  
**Status:** Configuration issue, not code error

