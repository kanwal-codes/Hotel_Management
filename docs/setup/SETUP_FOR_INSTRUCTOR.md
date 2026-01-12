# 🎓 Setup Instructions for Instructor/Professor

**Quick reference guide for running this project**

---

## ⚡ **Quick Start (5 Minutes)**

### **1. Install Requirements**
- ✅ Java 17 (JDK)
- ✅ MySQL 8.0+ 
- ✅ IntelliJ IDEA (or Eclipse with JavaFX)

### **2. Set Up Database**
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE hotel_db;"

# Run scripts
mysql -u root -p hotel_db < database/create_schema.sql
mysql -u root -p hotel_db < database/seed_data.sql
```

### **3. Update Credentials** (if needed)
Edit `src/main/resources/META-INF/persistence.xml`:
- Line 30: MySQL username
- Line 31: MySQL password

### **4. Open in IntelliJ**
- File → Open → Select project folder
- Wait for Maven to sync
- Run `Main.java`

### **5. Login**
- Username: `admin`
- Password: `admin123`

---

## 📋 **What's Included**

✅ **Complete source code**  
✅ **SQL database scripts** (`database/` folder)  
✅ **Maven configuration** (`pom.xml`)  
✅ **JPA configuration** (`persistence.xml`)  
✅ **UI files** (FXML in `src/main/resources/view/`)  
✅ **Documentation** (this file + README.md)

---

## 🔧 **Troubleshooting**

**"Connection refused"** → Start MySQL service  
**"Database doesn't exist"** → Run `create_schema.sql`  
**"Access denied"** → Update password in `persistence.xml`  
**"JavaFX not found"** → Maven will download automatically

---

## ✅ **Verification**

After setup, you should see:
- Welcome screen on application start
- Can login with admin/admin123
- Dashboard shows reservations
- All features functional

---

**Questions?** Check `INSTRUCTOR_SETUP_GUIDE.md` for detailed instructions.

