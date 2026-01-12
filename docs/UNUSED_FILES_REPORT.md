# Unused Files Report

This report identifies files in the project that are not being used anywhere in the codebase.

## Summary

After thorough verification across the entire project (including documentation, scripts, and database files):

**CONFIRMED UNUSED (Safe to Delete):**
- 2 compiled `.class` files in source directory (should always be removed)
- 1 utility class with no references (`PasswordHashGenerator.java`)
- 4 FXML files (3 stubs + 1 unused dashboard)

**NEEDED (Keep):**
- `UpdateAdminPasswords.java` - Referenced in documentation and database setup scripts
- `SeedData.java` - Referenced extensively in documentation and setup guides

**NEEDS FIX:**
- Missing `CustomerLogin.fxml` reference in code

---

## 1. Utility/Main Classes

### ✅ KEEP: `src/main/java/com/hotel/app/UpdateAdminPasswords.java`
- **Status**: **NEEDED** - Keep this file
- **References Found**:
  - `EMAIL_BASED_ROLE_SETUP.md` (line 25, 82) - Instructions to run this utility
  - `database/seed_data.sql` (lines 11, 14, 19) - Documentation references
  - `database/update_admin_password.sql` (line 5) - Alternative approach mentioned
  - `docs/features/EMAIL_BASED_ROLE_SETUP.md` - Feature documentation
- **Purpose**: Utility to create/update admin users with proper BCrypt password hashes
- **Usage**: Run manually: `java com.hotel.app.UpdateAdminPasswords`
- **Recommendation**: **KEEP** - Required for database setup

### ✅ KEEP: `src/main/java/com/hotel/app/SeedData.java`
- **Status**: **NEEDED** - Keep this file
- **References Found**:
  - `docs/01-getting-started/QUICK_START_GUIDE.md` (lines 502-563) - Full usage instructions
  - `database/seed_data.sql` (lines 11, 18) - Documentation references
  - `QUICK_DATABASE_SETUP.sh` - Setup script mentions it
  - Multiple documentation files reference it
- **Purpose**: Utility to seed database with initial test data
- **Usage**: Run manually or called programmatically
- **Recommendation**: **KEEP** - Required for database initialization

### ❌ DELETE: `src/main/java/com/hotel/util/PasswordHashGenerator.java`
- **Status**: **UNUSED** - Safe to delete
- **References Found**: None (only in this report and the file itself)
- **Purpose**: Utility to generate BCrypt password hashes from command line
- **Recommendation**: **DELETE** - No references found in documentation or code. `UpdateAdminPasswords.java` provides similar functionality.

---

## 2. Compiled Class Files in Source Directory

These `.class` files should not be in the source directory (they belong in `target/classes/`):

### `src/main/java/com/hotel/util/PasswordHashGenerator.class`
- **Status**: Should be removed
- **Description**: Compiled bytecode file
- **Recommendation**: Delete - compiled files should only exist in `target/` directory

### `src/main/java/com/hotel/security/BCryptPasswordHasher.class`
- **Status**: Should be removed
- **Description**: Compiled bytecode file
- **Recommendation**: Delete - compiled files should only exist in `target/` directory

---

## 3. Unused FXML Files

### ❌ DELETE: `src/main/resources/view/kiosk/ManagementKioskDashboard.fxml`
- **Status**: **UNUSED** - Safe to delete
- **Description**: FXML file for management kiosk dashboard
- **Issue**: 
  - No references found in Java code
  - Uses `AdminDashboardController` but is never loaded
  - Appears to be a duplicate/unused variant of admin dashboard
- **Controller**: `com.hotel.controller.AdminDashboardController`
- **Recommendation**: **DELETE** - Never loaded, appears to be unused duplicate

### ❌ DELETE: `src/main/resources/view/kiosk/CustomerKioskDashboard.fxml`
- **Status**: **UNUSED** - Safe to delete (or implement if feature is needed)
- **Description**: FXML file for customer kiosk dashboard
- **Issue**: 
  - Controller exists (`CustomerKioskDashboardController`) with full implementation
  - FXML file is never loaded anywhere in the codebase
  - Controller has methods like `handleMakeBooking()`, `handleBack()`, `handleLogout()`
  - Appears to be a planned feature that was never connected
- **Controller**: `com.hotel.controller.CustomerKioskDashboardController` (fully implemented)
- **Recommendation**: **DELETE** - Or implement navigation to it if this feature is needed. Currently orphaned.

---

## 4. Stub FXML Files (Placeholders)

These FXML files use `KioskStubController` and display "coming soon" messages. They are placeholders for future functionality:

### ❌ DELETE: `src/main/resources/view/kiosk/CheckIn.fxml`
- **Status**: **STUB/PLACEHOLDER** - Safe to delete
- **Controller**: `com.hotel.controller.KioskStubController`
- **Content**: Displays "Check-In functionality coming soon"
- **Issue**: 
  - Only has a back button (via stub controller)
  - No actual check-in functionality
  - Never loaded in the application
- **Note**: Check-in functionality exists in `AdminCheckoutController` for admin users, but not for kiosk
- **Recommendation**: **DELETE** - Placeholder with no implementation. Implement properly or remove.

### ❌ DELETE: `src/main/resources/view/kiosk/CheckOut.fxml`
- **Status**: **STUB/PLACEHOLDER** - Safe to delete
- **Controller**: `com.hotel.controller.KioskStubController`
- **Content**: Displays "Check-Out functionality coming soon"
- **Issue**: 
  - Only has a back button (via stub controller)
  - No actual check-out functionality
  - Never loaded in the application
- **Note**: Check-out functionality exists in `AdminCheckoutController` for admin users, but not for kiosk
- **Recommendation**: **DELETE** - Placeholder with no implementation. Implement properly or remove.

### ❌ DELETE: `src/main/resources/view/kiosk/ViewAvailability.fxml`
- **Status**: **STUB/PLACEHOLDER** - Safe to delete
- **Controller**: `com.hotel.controller.KioskStubController`
- **Content**: Displays "View Room Availability functionality coming soon"
- **Issue**: 
  - Only has a back button (via stub controller)
  - No actual availability viewing functionality
  - Never loaded in the application
- **Note**: Room availability is shown in `RoomSelection.fxml` during booking, but no standalone view exists
- **Recommendation**: **DELETE** - Placeholder with no implementation. Implement properly or remove.

---

## 5. Missing FXML File Reference (Code Bug)

### ⚠️ FIX NEEDED: Missing `CustomerLogin.fxml`
- **Status**: **CODE BUG** - Needs to be fixed
- **Location**: Referenced in `CustomerRegistrationController.java` line 104
- **Issue**: 
  - Code tries to navigate to `/view/main/CustomerLogin.fxml` but file doesn't exist
  - `CustomerLoginController.class` exists in `target/` but source file is missing
  - Documentation (`docs/improvements/CONTROLLER_ANALYSIS.md`) indicates `CustomerLoginController` should be deprecated/removed in favor of `UnifiedLoginController`
- **Current Code**:
  ```java
  navigate(event, "/view/main/CustomerLogin.fxml");
  ```
- **Recommendation**: **FIX** - Change to use `UnifiedLogin.fxml`:
  ```java
  navigate(event, "/view/main/UnifiedLogin.fxml");
  ```
- **Note**: `UnifiedLoginController` handles both admin and customer login, making `CustomerLogin.fxml` redundant

---

## CSS Files Status

All CSS files are being used:
- ✅ `resources/styles/kiosk.css` - Used by multiple kiosk FXML files
- ✅ `resources/styles/admin.css` - Used by all admin FXML files
- ✅ `resources/styles/feedback.css` - Used by feedback FXML files

---

## Summary of Actions

### ✅ Files to KEEP (Required)
- `src/main/java/com/hotel/app/UpdateAdminPasswords.java` - Required for database setup
- `src/main/java/com/hotel/app/SeedData.java` - Required for database initialization

### ❌ Files to DELETE (Confirmed Unused)

```bash
# Compiled class files (should always be removed from source)
src/main/java/com/hotel/util/PasswordHashGenerator.class
src/main/java/com/hotel/security/BCryptPasswordHasher.class

# Unused utility (no references found)
src/main/java/com/hotel/util/PasswordHashGenerator.java

# Unused FXML files
src/main/resources/view/kiosk/ManagementKioskDashboard.fxml
src/main/resources/view/kiosk/CustomerKioskDashboard.fxml

# Stub/placeholder FXML files
src/main/resources/view/kiosk/CheckIn.fxml
src/main/resources/view/kiosk/CheckOut.fxml
src/main/resources/view/kiosk/ViewAvailability.fxml
```

### ⚠️ Code Fix Required

**File**: `src/main/java/com/hotel/controller/CustomerRegistrationController.java` (line 104)

**Current Code**:
```java
navigate(event, "/view/main/CustomerLogin.fxml");
```

**Fix**:
```java
navigate(event, "/view/main/UnifiedLogin.fxml");
```

**Reason**: `CustomerLogin.fxml` doesn't exist. `UnifiedLoginController` handles both admin and customer login.

---

## Verification Summary

✅ **Verified**: Checked entire project including:
- All Java source files
- All FXML files
- Documentation files (`.md` files)
- Database setup scripts (`.sql` files)
- Shell scripts (`.sh` files)
- Configuration files

**Result**: 
- 2 utility classes (`UpdateAdminPasswords`, `SeedData`) are **REQUIRED** and referenced in documentation
- 1 utility class (`PasswordHashGenerator`) has **NO REFERENCES** - safe to delete
- 4 FXML files are **CONFIRMED UNUSED** - safe to delete
- 2 compiled `.class` files should **ALWAYS be removed** from source
- 1 code reference needs to be **FIXED** (missing FXML file)

