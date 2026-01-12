# 🗄️ MySQL Database Setup Guide

Complete step-by-step guide to set up MySQL for the Hotel Reservation System.

---

## 📋 **Step 1: Install MySQL**

### **Option A: Install via Homebrew (Recommended for macOS)**

```bash
# Install Homebrew (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install MySQL
brew install mysql

# Start MySQL service
brew services start mysql
```

### **Option B: Download MySQL Installer**

1. Go to: https://dev.mysql.com/downloads/mysql/
2. Download MySQL Community Server for macOS
3. Run the installer
4. Follow the installation wizard
5. **Note the root password** you set during installation!

---

## 📋 **Step 2: Verify MySQL Installation**

```bash
# Check if MySQL is running
mysql --version

# Try to connect (if no password set)
mysql -u root

# Or if password was set during installation
mysql -u root -p
```

---

## 📋 **Step 3: Create the Database**

### **Method 1: Using Command Line**

```bash
# Connect to MySQL
mysql -u root -p

# Then run these SQL commands:
```

```sql
CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;
```

### **Method 2: Using MySQL Workbench**

1. Open MySQL Workbench
2. Connect to your MySQL server
3. Click "Create a new schema"
4. Name it: `hotel_db`
5. Click "Apply"

---

## 📋 **Step 4: Run the SQL Scripts**

### **Method 1: Using Command Line**

From your project directory:

```bash
# Navigate to project root
cd /Users/kanwal/SEM5/APD/MS1/Project

# Run schema creation script
mysql -u root -p hotel_db < database/create_schema.sql

# Run seed data script
mysql -u root -p hotel_db < database/seed_data.sql
```

### **Method 2: Using MySQL Workbench**

1. Open MySQL Workbench
2. Connect to your MySQL server
3. Select `hotel_db` database
4. **File → Open SQL Script**
5. Select `database/create_schema.sql`
6. Click the "Execute" button (⚡)
7. Repeat for `database/seed_data.sql`

### **Method 3: Copy-Paste in MySQL Command Line**

```bash
mysql -u root -p hotel_db
```

Then copy and paste the entire contents of:
- `database/create_schema.sql`
- `database/seed_data.sql`

---

## 📋 **Step 5: Verify Database Setup**

```bash
mysql -u root -p hotel_db -e "SHOW TABLES;"
```

You should see 13 tables:
- hotels
- admin_users
- guests
- rooms
- reservations
- reservation_rooms
- service_addons
- reservation_addons
- billings
- payments
- feedbacks
- waitlists
- audit_log

---

## 📋 **Step 6: Update Database Credentials (if needed)**

### **Check Current Settings**

File: `src/main/resources/META-INF/persistence.xml`

Current settings:
- **Username:** `root`
- **Password:** (empty)
- **Port:** `3306`
- **Database:** `hotel_db`

### **If You Set a Password During Installation**

Edit `persistence.xml` line 31:
```xml
<property name="javax.persistence.jdbc.password" value="your_mysql_password"/>
```

### **If MySQL is on a Different Port**

Edit `persistence.xml` line 29:
```xml
<property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:YOUR_PORT/hotel_db?useSSL=false&amp;serverTimezone=UTC"/>
```

---

## 📋 **Step 7: Test the Connection**

### **Quick Test**

```bash
# Test connection
mysql -u root -p -e "USE hotel_db; SELECT COUNT(*) FROM hotels;"
```

Should return a number (0 or more).

### **Test from Application**

1. Start MySQL (if not running):
   ```bash
   brew services start mysql
   ```

2. Run the application in IntelliJ
3. Check the console output - should see:
   ```
   EntityManagerFactory created successfully
   ```

---

## 🔧 **Troubleshooting**

### **Issue: "Command not found: mysql"**

**Solution:** MySQL is not in your PATH
- Add MySQL to PATH, or
- Use full path: `/usr/local/mysql/bin/mysql`

### **Issue: "Access denied for user 'root'"**

**Solution:** Wrong password
- Try: `mysql -u root -p` and enter your password
- Or reset MySQL root password

### **Issue: "Can't connect to MySQL server"**

**Solution:** MySQL is not running
```bash
# Start MySQL
brew services start mysql

# Or
sudo /usr/local/mysql/support-files/mysql.server start
```

### **Issue: "Unknown database 'hotel_db'"**

**Solution:** Database not created
```sql
CREATE DATABASE hotel_db;
```

---

## ✅ **Verification Checklist**

- [ ] MySQL is installed
- [ ] MySQL is running
- [ ] Can connect with `mysql -u root -p`
- [ ] Database `hotel_db` exists
- [ ] Schema script executed successfully
- [ ] Seed data script executed successfully
- [ ] 13 tables exist in database
- [ ] Credentials in `persistence.xml` are correct
- [ ] Application starts without connection errors

---

## 🚀 **Quick Start Commands**

```bash
# 1. Install MySQL (if not installed)
brew install mysql

# 2. Start MySQL
brew services start mysql

# 3. Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS hotel_db;"

# 4. Run scripts
mysql -u root -p hotel_db < database/create_schema.sql
mysql -u root -p hotel_db < database/seed_data.sql

# 5. Verify
mysql -u root -p hotel_db -e "SHOW TABLES;"
```

---

## 📝 **Default Admin Credentials**

After running `seed_data.sql`, you can login with:

- **Username:** `admin`
- **Password:** `admin123`

OR

- **Username:** `manager`
- **Password:** `manager123`

---

**Need Help?** Check the error message and refer to the troubleshooting section above.

