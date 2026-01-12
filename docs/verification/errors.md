# Error Tracking and Resolution

**Created:** [Date will be updated when errors are added]  
**Status:** 🔴 Active - Errors to be resolved

---

## Error Tracking Template

For each error, please provide:
1. **Error Message** (full stack trace if available)
2. **When it occurs** (what action triggers it)
3. **Expected behavior** (what should happen)
4. **Actual behavior** (what actually happens)
5. **Screenshots/Logs** (if applicable)

---

## Error #1: [Error Title]

**Status:** 🔴 Unresolved | 🟡 In Progress | ✅ Resolved

**Error Message:**
```
[Paste error message here]
```

**When it occurs:**
- [Describe when/where this error happens]

**Expected behavior:**
- [What should happen]

**Actual behavior:**
- [What actually happens]

**Stack Trace:**
```
[Paste full stack trace here if available]
```

**Investigation:**
- [Analysis of the error]

**Fix Applied:**
- [Solution implemented]

**Verification:**
- [ ] Error resolved
- [ ] Tested and working
- [ ] No new errors introduced

**Notes:**
- [Any additional notes]

---

## Error #2: [Error Title]

**Status:** 🔴 Unresolved | 🟡 In Progress | ✅ Resolved

**Error Message:**
```
[Paste error message here]
```

**When it occurs:**
- [Describe when/where this error happens]

**Expected behavior:**
- [What should happen]

**Actual behavior:**
- [What actually happens]

**Stack Trace:**
```
[Paste full stack trace here if available]
```

**Investigation:**
- [Analysis of the error]

**Fix Applied:**
- [Solution implemented]

**Verification:**
- [ ] Error resolved
- [ ] Tested and working
- [ ] No new errors introduced

**Notes:**
- [Any additional notes]

---

## Error #3: [Error Title]

**Status:** 🔴 Unresolved | 🟡 In Progress | ✅ Resolved

**Error Message:**
```
[Paste error message here]
```

**When it occurs:**
- [Describe when/where this error happens]

**Expected behavior:**
- [What should happen]

**Actual behavior:**
- [What actually happens]

**Stack Trace:**
```
[Paste full stack trace here if available]
```

**Investigation:**
- [Analysis of the error]

**Fix Applied:**
- [Solution implemented]

**Verification:**
- [ ] Error resolved
- [ ] Tested and working
- [ ] No new errors introduced

**Notes:**
- [Any additional notes]

---

## Error #4: [Error Title]

**Status:** 🔴 Unresolved | 🟡 In Progress | ✅ Resolved

**Error Message:**
```
[Paste error message here]
```

**When it occurs:**
- [Describe when/where this error happens]

**Expected behavior:**
- [What should happen]

**Actual behavior:**
- [What actually happens]

**Stack Trace:**
```
[Paste full stack trace here if available]
```

**Investigation:**
- [Analysis of the error]

**Fix Applied:**
- [Solution implemented]

**Verification:**
- [ ] Error resolved
- [ ] Tested and working
- [ ] No new errors introduced

**Notes:**
- [Any additional notes]

---

## Error #5: [Error Title]

**Status:** 🔴 Unresolved | 🟡 In Progress | ✅ Resolved

**Error Message:**
```
[Paste error message here]
```

**When it occurs:**
- [Describe when/where this error happens]

**Expected behavior:**
- [What should happen]

**Actual behavior:**
- [What actually happens]

**Stack Trace:**
```
[Paste full stack trace here if available]
```

**Investigation:**
- [Analysis of the error]

**Fix Applied:**
- [Solution implemented]

**Verification:**
- [ ] Error resolved
- [ ] Tested and working
- [ ] No new errors introduced

**Notes:**
- [Any additional notes]

---

## Summary

### Resolved Errors: 0
### In Progress: 0
### Unresolved: 0

### Priority Order:
1. [Error #X] - [Brief description]
2. [Error #Y] - [Brief description]
3. [Error #Z] - [Brief description]

---

## Quick Reference

### Common Error Types:
- **Compilation Errors:** Syntax errors, missing imports, type mismatches
- **Runtime Errors:** NullPointerException, ClassNotFoundException, etc.
- **Database Errors:** Connection failures, query errors, transaction issues
- **UI Errors:** FXML loading failures, controller injection issues
- **Service Errors:** Service initialization failures, business logic errors

### Error Resolution Workflow:
1. ✅ Paste error into template above
2. ✅ Analyze error (check logs, stack trace)
3. ✅ Identify root cause
4. ✅ Implement fix
5. ✅ Test fix
6. ✅ Update status to "Resolved"
7. ✅ Verify no new errors introduced

---

**Instructions:**
1. Copy one of the error templates above
2. Fill in the error details
3. Paste it below this line
4. I'll tackle them one by one

---

## Errors to Fix:

---

## ✅ ERROR #1: TransientPropertyValueException - Guest Not Saved

**Status:** ✅ RESOLVED

**Error Message:**
```
org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
```

**Root Cause:**
- `GuestRepository.save()` was not managing transactions
- Guest was persisted but transaction was never committed
- When `ReservationService.createReservation()` started a new transaction, Guest was still transient

**Fix Applied:**
- Modified `GuestRepository.save()` to manage transactions:
  - Begin transaction if not active
  - Commit transaction after save (if we started it)
  - Rollback on error
- This ensures Guest is persisted and committed before Reservation creation

**Files Changed:**
- `src/main/java/com/hotel/repository/GuestRepository.java`

**Verification:**
- [x] Guest is now saved in a committed transaction
- [x] Guest will have an ID before Reservation creation
- [ ] Test reservation creation end-to-end

---

## ✅ ERROR #2: Add-ons Table Empty (0 Addons)

**Status:** ✅ RESOLVED

**Error Message:**
```
[AddonRepository] Found 0 service addons
```

**Root Cause:**
- `seed_data.sql` had wrong table name: `service_addons` (plural)
- Entity maps to `service_addon` (singular)
- Seed data was never inserted

**Fix Applied:**
1. Fixed `seed_data.sql` table name from `service_addons` to `service_addon`
2. Removed `active` column (not in entity)
3. Manually inserted 4 addons:
   - Wi-Fi: $10.00 PER_NIGHT
   - Breakfast: $15.00 PER_NIGHT
   - Parking: $20.00 PER_RESERVATION
   - Spa Access: $50.00 PER_RESERVATION

**Files Changed:**
- `database/seed_data.sql`

**Verification:**
- [x] Addons now exist in database (4 addons)
- [ ] Test add-on selection and total calculation

---

## 🔄 ERROR #3: Table Not Displaying (Data Loads But Not Visible)

**Status:** 🟡 IN PROGRESS

**Evidence:**
- Logs show: "Table items set. Table now has 2 items"
- Logs show: "Table visible: true"
- Logs show: "Table columns count: 4"
- Cell value factories are being called (logs show values)
- But user reports table is empty

**Investigation Needed:**
1. Check FXML for table visibility settings
2. Check container visibility
3. Verify table is in scene graph
4. Check CSS for display:none or visibility:hidden
5. Verify table prefHeight/prefWidth

**Next Steps:**
- Review RoomSelection.fxml
- Check suggestedPlanContainer visibility
- Verify table rendering

---
/Users/kanwal/Library/Java/JavaVirtualMachines/openjdk-25/Contents/Home/bin/java --module-path /Users/kanwal/javafx-sdk-25.0.1/lib --add-modules javafx.controls,javafx.fxml -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=54185 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/kanwal/SEM5/APD/MS1/Project/target/classes:/Users/kanwal/.m2/repository/org/openjfx/javafx-controls/17.0.2/javafx-controls-17.0.2.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-controls/17.0.2/javafx-controls-17.0.2-mac-aarch64.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-graphics/17.0.2/javafx-graphics-17.0.2.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-graphics/17.0.2/javafx-graphics-17.0.2-mac-aarch64.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-base/17.0.2/javafx-base-17.0.2.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-base/17.0.2/javafx-base-17.0.2-mac-aarch64.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-fxml/17.0.2/javafx-fxml-17.0.2.jar:/Users/kanwal/.m2/repository/org/openjfx/javafx-fxml/17.0.2/javafx-fxml-17.0.2-mac-aarch64.jar:/Users/kanwal/.m2/repository/org/hibernate/hibernate-core/5.6.15.Final/hibernate-core-5.6.15.Final.jar:/Users/kanwal/.m2/repository/org/jboss/logging/jboss-logging/3.4.3.Final/jboss-logging-3.4.3.Final.jar:/Users/kanwal/.m2/repository/javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.jar:/Users/kanwal/.m2/repository/net/bytebuddy/byte-buddy/1.12.18/byte-buddy-1.12.18.jar:/Users/kanwal/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:/Users/kanwal/.m2/repository/org/jboss/spec/javax/transaction/jboss-transaction-api_1.2_spec/1.1.1.Final/jboss-transaction-api_1.2_spec-1.1.1.Final.jar:/Users/kanwal/.m2/repository/org/jboss/jandex/2.4.2.Final/jandex-2.4.2.Final.jar:/Users/kanwal/.m2/repository/com/fasterxml/classmate/1.5.1/classmate-1.5.1.jar:/Users/kanwal/.m2/repository/javax/activation/javax.activation-api/1.2.0/javax.activation-api-1.2.0.jar:/Users/kanwal/.m2/repository/org/hibernate/common/hibernate-commons-annotations/5.1.2.Final/hibernate-commons-annotations-5.1.2.Final.jar:/Users/kanwal/.m2/repository/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar:/Users/kanwal/.m2/repository/org/glassfish/jaxb/jaxb-runtime/2.3.1/jaxb-runtime-2.3.1.jar:/Users/kanwal/.m2/repository/org/glassfish/jaxb/txw2/2.3.1/txw2-2.3.1.jar:/Users/kanwal/.m2/repository/com/sun/istack/istack-commons-runtime/3.0.7/istack-commons-runtime-3.0.7.jar:/Users/kanwal/.m2/repository/org/jvnet/staxex/stax-ex/1.8/stax-ex-1.8.jar:/Users/kanwal/.m2/repository/com/sun/xml/fastinfoset/FastInfoset/1.2.15/FastInfoset-1.2.15.jar:/Users/kanwal/.m2/repository/org/hibernate/hibernate-entitymanager/5.6.15.Final/hibernate-entitymanager-5.6.15.Final.jar:/Users/kanwal/.m2/repository/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar:/Users/kanwal/.m2/repository/com/google/protobuf/protobuf-java/3.21.9/protobuf-java-3.21.9.jar:/Users/kanwal/.m2/repository/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar:/Users/kanwal/.m2/repository/org/apache/pdfbox/pdfbox/2.0.27/pdfbox-2.0.27.jar:/Users/kanwal/.m2/repository/org/apache/pdfbox/fontbox/2.0.27/fontbox-2.0.27.jar:/Users/kanwal/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/Users/kanwal/.m2/repository/org/apache/commons/commons-csv/1.10.0/commons-csv-1.10.0.jar:/Users/kanwal/.m2/repository/javax/validation/validation-api/2.0.1.Final/validation-api-2.0.1.Final.jar:/Users/kanwal/.m2/repository/org/hibernate/validator/hibernate-validator/6.2.5.Final/hibernate-validator-6.2.5.Final.jar:/Users/kanwal/.m2/repository/jakarta/validation/jakarta.validation-api/2.0.2/jakarta.validation-api-2.0.2.jar:/Users/kanwal/.m2/repository/org/glassfish/javax.el/3.0.0/javax.el-3.0.0.jar com.hotel.app.Main
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by com.sun.glass.utils.NativeLibLoader in module javafx.graphics (file:/Users/kanwal/javafx-sdk-25.0.1/lib/javafx.graphics.jar)
WARNING: Use --enable-native-access=javafx.graphics to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled

Nov 26, 2025 8:29:51 P.M. com.hotel.util.LoggerService <init>
INFO: LoggerService initialized successfully
Nov 26, 2025 8:29:51 P.M. com.hotel.util.LoggerService <init>
INFO: LoggerService initialized successfully
Nov 26, 2025 8:29:51 P.M. com.hotel.util.LoggerService logInfo
INFO: Initializing application configuration...
Nov 26, 2025 8:29:51 P.M. com.hotel.util.LoggerService logInfo
INFO: Initializing application configuration...
Nov 26, 2025 8:29:51 P.M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
INFO: HHH000204: Processing PersistenceUnitInfo [name: hotelPU]
Nov 26, 2025 8:29:51 P.M. org.hibernate.Version logVersion
INFO: HHH000412: Hibernate ORM core version 5.6.15.Final
Nov 26, 2025 8:29:52 P.M. org.hibernate.annotations.common.reflection.java.JavaReflectionManager <clinit>
INFO: HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
Nov 26, 2025 8:29:52 P.M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl configure
WARN: HHH10001002: Using Hibernate built-in connection pool (not for production use!)
Nov 26, 2025 8:29:52 P.M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl buildCreator
INFO: HHH10001005: using driver [com.mysql.cj.jdbc.Driver] at URL [jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC]
Nov 26, 2025 8:29:52 P.M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl buildCreator
INFO: HHH10001001: Connection properties: {user=root, password=****}
Nov 26, 2025 8:29:52 P.M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl buildCreator
INFO: HHH10001003: Autocommit mode: false
Nov 26, 2025 8:29:52 P.M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl$PooledConnections <init>
INFO: HHH000115: Hibernate connection pool size: 20 (min=1)
Nov 26, 2025 8:29:52 P.M. org.hibernate.dialect.Dialect <init>
INFO: HHH000400: Using dialect: org.hibernate.dialect.MySQL57Dialect
Nov 26, 2025 8:29:52 P.M. org.hibernate.validator.internal.util.Version <clinit>
INFO: HV000001: Hibernate Validator 6.2.5.Final
Nov 26, 2025 8:29:52 P.M. org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl getIsolatedConnection
INFO: HHH10001501: Connection obtained from JdbcConnectionAccess [org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator$ConnectionProviderJdbcConnectionAccess@3091989] for (non-JTA) DDL execution was not in auto-commit mode; the Connection 'local transaction' will be committed and the Connection will be set into auto-commit mode.
Nov 26, 2025 8:29:52 P.M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
INFO: HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: EntityManagerFactory created successfully
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: EntityManagerFactory created successfully
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: Application configuration initialized successfully
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: Application configuration initialized successfully
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:29:52 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Nov 26, 2025 8:29:56 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:29:56 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:29:56 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:29:56 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Nov 26, 2025 8:30:14 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:14 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:14 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:30:14 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Hibernate: 
    select
        guest0_.id as id1_4_,
        guest0_.address as address2_4_,
        guest0_.email as email3_4_,
        guest0_.loyalty_number as loyalty_4_4_,
        guest0_.loyalty_points as loyalty_5_4_,
        guest0_.name as name6_4_,
        guest0_.phone as phone7_4_ 
    from
        guest guest0_ 
    where
        guest0_.email=?
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Nov 26, 2025 8:30:42 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:42 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:42 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:30:42 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms called ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms called ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Parameters: 4 adults, 0 children, 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Parameters: 4 adults, 0 children, 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total people: 4
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total people: 4
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for DOUBLE from 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 20 available DOUBLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 20 available DOUBLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 10 available SINGLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 10 available SINGLE rooms for 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: DOUBLE = true (20 rooms)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: DOUBLE = true (20 rooms)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: SINGLE = true (10 rooms)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: SINGLE = true (10 rooms)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 10 available SINGLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 10 available SINGLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms returning 2 suggestions ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms returning 2 suggestions ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Got 2 room suggestions
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Got 2 room suggestions
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 10 available SINGLE rooms for 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Initializing table columns in initialize()
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Initializing table columns in initialize()
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up table columns...
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up table columns...
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up roomTypeColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up roomTypeColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: roomTypeColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: roomTypeColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up quantityColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up quantityColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: quantityColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: quantityColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up pricePerNightColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up pricePerNightColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: pricePerNightColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: pricePerNightColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up totalPriceColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up totalPriceColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: totalPriceColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: totalPriceColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table columns setup completed
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table columns setup completed
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Booking state set: 4 adults, 0 children, check-in: 2025-11-26, check-out: 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Booking state set: 4 adults, 0 children, check-in: 2025-11-26, check-out: 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === loadRoomSuggestions called ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === loadRoomSuggestions called ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Suggestions count: 2
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Suggestions count: 2
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Suggestion 0: Room=DOUBLE, Quantity=1, Price=150.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Suggestion 0: Room=DOUBLE, Quantity=1, Price=150.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Suggestion 1: Room=SINGLE, Quantity=2, Price=100.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Suggestion 1: Room=SINGLE, Quantity=2, Price=100.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is not null
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is not null
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: roomTypeColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: roomTypeColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: quantityColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: quantityColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: pricePerNightColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: pricePerNightColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: totalPriceColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: totalPriceColumn: exists
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up table columns...
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up table columns...
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up roomTypeColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up roomTypeColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: roomTypeColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: roomTypeColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up quantityColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up quantityColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: quantityColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: quantityColumn factory set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up pricePerNightColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up pricePerNightColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: pricePerNightColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: pricePerNightColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up totalPriceColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up totalPriceColumn
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: totalPriceColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: totalPriceColumn factory and formatter set
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table columns setup completed
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table columns setup completed
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Created ObservableList with 2 items
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Created ObservableList with 2 items
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table items set. Table now has 2 items
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table items set. Table now has 2 items
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table refreshed and layout requested
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table refreshed and layout requested
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table visible: true
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table visible: true
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table managed: true
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table managed: true
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table columns count: 4
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Table columns count: 4
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Showing suggestions table
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Showing suggestions table
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedPlanContainer set to visible
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedPlanContainer set to visible
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === loadRoomSuggestions completed ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: === loadRoomSuggestions completed ===
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 10 available SINGLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 10 available SINGLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DELUXE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DELUXE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 5 available DELUXE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 5 available DELUXE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: PENTHOUSE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: PENTHOUSE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 1 available PENTHOUSE rooms
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 1 available PENTHOUSE rooms
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 20 available DOUBLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 5 available DELUXE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 1 available PENTHOUSE rooms for 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room type cell value: DOUBLE
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room type cell value: DOUBLE
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Quantity cell value: 1
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Quantity cell value: 1
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Price per night cell value: 150.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Price per night cell value: 150.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total price cell value: 150.0 (nights: 1)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total price cell value: 150.0 (nights: 1)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room type cell value: SINGLE
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room type cell value: SINGLE
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Quantity cell value: 2
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Quantity cell value: 2
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Price per night cell value: 100.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Price per night cell value: 100.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total price cell value: 200.0 (nights: 1)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total price cell value: 200.0 (nights: 1)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room type cell value: SINGLE
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Room type cell value: SINGLE
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Quantity cell value: 2
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Quantity cell value: 2
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Price per night cell value: 100.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Price per night cell value: 100.0
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total price cell value: 200.0 (nights: 1)
Nov 26, 2025 8:30:47 P.M. com.hotel.util.LoggerService logInfo
INFO: Total price cell value: 200.0 (nights: 1)
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms called ===
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms called ===
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Parameters: 4 adults, 0 children, 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Parameters: 4 adults, 0 children, 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Total people: 4
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Total people: 4
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: DOUBLE = true (20 rooms)
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: DOUBLE = true (20 rooms)
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Checking availability for SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: SINGLE = true (10 rooms)
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Room availability check: SINGLE = true (10 rooms)
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: SINGLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 10 available SINGLE rooms
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 10 available SINGLE rooms
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms returning 2 suggestions ===
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: === suggestRooms returning 2 suggestions ===
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Getting available rooms: DOUBLE from 2025-11-26 to 2025-11-27
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 20 available DOUBLE rooms
$$ is a deprecated escape sequence. Please use \$ instead.
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 20 available DOUBLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 20 available DOUBLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 10 available SINGLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 10 available SINGLE rooms for 2025-11-26 to 2025-11-27
Hibernate: 
    select
        distinct room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_ 
    where
        room0_.type=? 
        and room0_.status=? 
        and (
            room0_.id not in  (
                select
                    reservatio1_.room_id 
                from
                    reservation_room reservatio1_ 
                inner join
                    reservation reservatio2_ 
                        on reservatio1_.reservation_id=reservatio2_.id 
                where
                    reservatio2_.status<>'CANCELLED' 
                    and reservatio2_.status<>'CHECKED_OUT' 
                    and reservatio2_.check_in<? 
                    and reservatio2_.check_out>?
            )
        )
[RoomRepository] Found 20 available DOUBLE rooms for 2025-11-26 to 2025-11-27
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up add-on checkbox listeners
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Setting up add-on checkbox listeners
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Wi-Fi checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Wi-Fi checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Breakfast checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Breakfast checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Parking checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Parking checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Spa checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Spa checkbox listener added
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Add-on total label initialized to $0.00
Nov 26, 2025 8:30:54 P.M. com.hotel.util.LoggerService logInfo
INFO: Add-on total label initialized to $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Wi-Fi checkbox changed: true
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Wi-Fi checkbox changed: true
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:55 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Breakfast checkbox changed: true
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Breakfast checkbox changed: true
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:56 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Parking checkbox changed: true
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Parking checkbox changed: true
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Spa checkbox changed: true
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Spa checkbox changed: true
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:57 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: updateAddOnTotal called
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Found 0 addons in repository
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Number of nights: 1
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Total addon cost: $0.00
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: Updated addOnTotalLabel to: $0.00
Hibernate: 
    select
        serviceadd0_.id as id1_11_,
        serviceadd0_.name as name2_11_,
        serviceadd0_.price as price3_11_,
        serviceadd0_.pricing_model as pricing_4_11_ 
    from
        service_addon serviceadd0_
[AddonRepository] Found 0 service addons
[KioskController] initialize() called
[KioskController] EntityManager created: true
[KioskController] ReservationService created: true
[KioskController] PricingService created: true
[KioskController] BillingService created: true
[KioskController] GuestRepository created: true
[KioskController] AddonRepository created: true
[KioskController] LoggerService created: true
[KioskController] All services initialized successfully
Hibernate: 
    select
        room0_.id as id1_10_,
        room0_.basePrice as basepric2_10_,
        room0_.beds as beds3_10_,
        room0_.hotel_id as hotel_id7_10_,
        room0_.roomNumber as roomnumb4_10_,
        room0_.status as status5_10_,
        room0_.type as type6_10_ 
    from
        room room0_
[RoomRepository] Found 36 total rooms
[KioskController] Database connection test: Found 36 rooms in database
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: suggestedRoomsTable is null in initialize() - not on room selection screen
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:30:58 P.M. com.hotel.util.LoggerService logInfo
INFO: addOnTotalLabel is null
Nov 26, 2025 8:31:01 P.M. org.hibernate.action.internal.UnresolvedEntityInsertActions logCannotResolveNonNullableTransientDependencies
WARN: HHH000437: Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.
	Unsaved transient entity: ([com.hotel.model.Guest#<null>])
	Dependent entities: ([[com.hotel.model.Reservation#<null>]])
	Non-nullable association(s): ([com.hotel.model.Reservation.guest])
Nov 26, 2025 8:31:01 P.M. com.hotel.util.LoggerService logError
SEVERE: Failed to create reservation
java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:151)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:181)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:188)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:769)
	at org.hibernate.internal.SessionImpl.persist(SessionImpl.java:742)
	at com.hotel.repository.ReservationRepository.save(ReservationRepository.java:24)
	at com.hotel.service.ReservationService.createReservation(ReservationService.java:205)
	at com.hotel.controller.KioskController.confirmBooking(KioskController.java:1441)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at com.sun.javafx.reflect.Trampoline.invoke(MethodUtil.java:65)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at javafx.base@25.0.1/com.sun.javafx.reflect.MethodUtil.invoke(MethodUtil.java:112)
	at javafx.fxml@25.0.1/com.sun.javafx.fxml.MethodHelper.invoke(MethodHelper.java:78)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$MethodHandler.invoke(FXMLLoader.java:1847)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$ControllerMethodEventHandler.handle(FXMLLoader.java:1718)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:86)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:49)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Node.fireEvent(Node.java:9026)
	at javafx.controls@25.0.1/javafx.scene.control.Button.fire(Button.java:203)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.behavior.ButtonBehavior.mouseReleased(ButtonBehavior.java:207)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.inputmap.InputMap.handle(InputMap.java:274)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler$NormalEventHandlerRecord.handleBubblingEvent(CompositeEventHandler.java:247)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:80)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:54)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Scene$MouseHandler.process(Scene.java:4061)
	at javafx.graphics@25.0.1/javafx.scene.Scene.processMouseEvent(Scene.java:1947)
	at javafx.graphics@25.0.1/javafx.scene.Scene$ScenePeerListener.mouseEvent(Scene.java:2784)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:353)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:255)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.QuantumToolkit.runWithoutRenderLock(QuantumToolkit.java:424)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler.handleMouseEvent(GlassViewEventHandler.java:387)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.handleMouseEvent(View.java:573)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.notifyMouse(View.java:975)
	at javafx.graphics@25.0.1/com.sun.glass.ui.mac.MacView.notifyMouse(MacView.java:131)
Caused by: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.action.internal.UnresolvedEntityInsertActions.checkNoUnresolvedActionsAfterOperation(UnresolvedEntityInsertActions.java:122)
	at org.hibernate.engine.spi.ActionQueue.checkNoUnresolvedActionsAfterOperation(ActionQueue.java:436)
	at org.hibernate.internal.SessionImpl.checkNoUnresolvedActionsAfterOperation(SessionImpl.java:623)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:766)
	... 62 more

Nov 26, 2025 8:31:01 P.M. com.hotel.util.LoggerService logError
SEVERE: Failed to create reservation
java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:151)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:181)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:188)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:769)
	at org.hibernate.internal.SessionImpl.persist(SessionImpl.java:742)
	at com.hotel.repository.ReservationRepository.save(ReservationRepository.java:24)
	at com.hotel.service.ReservationService.createReservation(ReservationService.java:205)
	at com.hotel.controller.KioskController.confirmBooking(KioskController.java:1441)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at com.sun.javafx.reflect.Trampoline.invoke(MethodUtil.java:65)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at javafx.base@25.0.1/com.sun.javafx.reflect.MethodUtil.invoke(MethodUtil.java:112)
	at javafx.fxml@25.0.1/com.sun.javafx.fxml.MethodHelper.invoke(MethodHelper.java:78)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$MethodHandler.invoke(FXMLLoader.java:1847)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$ControllerMethodEventHandler.handle(FXMLLoader.java:1718)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:86)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:49)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Node.fireEvent(Node.java:9026)
	at javafx.controls@25.0.1/javafx.scene.control.Button.fire(Button.java:203)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.behavior.ButtonBehavior.mouseReleased(ButtonBehavior.java:207)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.inputmap.InputMap.handle(InputMap.java:274)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler$NormalEventHandlerRecord.handleBubblingEvent(CompositeEventHandler.java:247)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:80)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:54)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Scene$MouseHandler.process(Scene.java:4061)
	at javafx.graphics@25.0.1/javafx.scene.Scene.processMouseEvent(Scene.java:1947)
	at javafx.graphics@25.0.1/javafx.scene.Scene$ScenePeerListener.mouseEvent(Scene.java:2784)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:353)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:255)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.QuantumToolkit.runWithoutRenderLock(QuantumToolkit.java:424)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler.handleMouseEvent(GlassViewEventHandler.java:387)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.handleMouseEvent(View.java:573)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.notifyMouse(View.java:975)
	at javafx.graphics@25.0.1/com.sun.glass.ui.mac.MacView.notifyMouse(MacView.java:131)
Caused by: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.action.internal.UnresolvedEntityInsertActions.checkNoUnresolvedActionsAfterOperation(UnresolvedEntityInsertActions.java:122)
	at org.hibernate.engine.spi.ActionQueue.checkNoUnresolvedActionsAfterOperation(ActionQueue.java:436)
	at org.hibernate.internal.SessionImpl.checkNoUnresolvedActionsAfterOperation(SessionImpl.java:623)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:766)
	... 62 more

Nov 26, 2025 8:31:01 P.M. com.hotel.util.LoggerService logError
SEVERE: Failed to create reservation
java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:151)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:181)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:188)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:769)
	at org.hibernate.internal.SessionImpl.persist(SessionImpl.java:742)
	at com.hotel.repository.ReservationRepository.save(ReservationRepository.java:24)
	at com.hotel.service.ReservationService.createReservation(ReservationService.java:205)
	at com.hotel.controller.KioskController.confirmBooking(KioskController.java:1441)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at com.sun.javafx.reflect.Trampoline.invoke(MethodUtil.java:65)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at javafx.base@25.0.1/com.sun.javafx.reflect.MethodUtil.invoke(MethodUtil.java:112)
	at javafx.fxml@25.0.1/com.sun.javafx.fxml.MethodHelper.invoke(MethodHelper.java:78)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$MethodHandler.invoke(FXMLLoader.java:1847)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$ControllerMethodEventHandler.handle(FXMLLoader.java:1718)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:86)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:49)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Node.fireEvent(Node.java:9026)
	at javafx.controls@25.0.1/javafx.scene.control.Button.fire(Button.java:203)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.behavior.ButtonBehavior.mouseReleased(ButtonBehavior.java:207)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.inputmap.InputMap.handle(InputMap.java:274)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler$NormalEventHandlerRecord.handleBubblingEvent(CompositeEventHandler.java:247)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:80)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:54)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Scene$MouseHandler.process(Scene.java:4061)
	at javafx.graphics@25.0.1/javafx.scene.Scene.processMouseEvent(Scene.java:1947)
	at javafx.graphics@25.0.1/javafx.scene.Scene$ScenePeerListener.mouseEvent(Scene.java:2784)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:353)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:255)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.QuantumToolkit.runWithoutRenderLock(QuantumToolkit.java:424)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler.handleMouseEvent(GlassViewEventHandler.java:387)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.handleMouseEvent(View.java:573)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.notifyMouse(View.java:975)
	at javafx.graphics@25.0.1/com.sun.glass.ui.mac.MacView.notifyMouse(MacView.java:131)
Caused by: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.action.internal.UnresolvedEntityInsertActions.checkNoUnresolvedActionsAfterOperation(UnresolvedEntityInsertActions.java:122)
	at org.hibernate.engine.spi.ActionQueue.checkNoUnresolvedActionsAfterOperation(ActionQueue.java:436)
	at org.hibernate.internal.SessionImpl.checkNoUnresolvedActionsAfterOperation(SessionImpl.java:623)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:766)
	... 62 more

Nov 26, 2025 8:31:01 P.M. com.hotel.util.LoggerService logError
SEVERE: Failed to create reservation
java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:151)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:181)
	at org.hibernate.internal.ExceptionConverterImpl.convert(ExceptionConverterImpl.java:188)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:769)
	at org.hibernate.internal.SessionImpl.persist(SessionImpl.java:742)
	at com.hotel.repository.ReservationRepository.save(ReservationRepository.java:24)
	at com.hotel.service.ReservationService.createReservation(ReservationService.java:205)
	at com.hotel.controller.KioskController.confirmBooking(KioskController.java:1441)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at com.sun.javafx.reflect.Trampoline.invoke(MethodUtil.java:65)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at javafx.base@25.0.1/com.sun.javafx.reflect.MethodUtil.invoke(MethodUtil.java:112)
	at javafx.fxml@25.0.1/com.sun.javafx.fxml.MethodHelper.invoke(MethodHelper.java:78)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$MethodHandler.invoke(FXMLLoader.java:1847)
	at javafx.fxml@25.0.1/javafx.fxml.FXMLLoader$ControllerMethodEventHandler.handle(FXMLLoader.java:1718)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:86)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:49)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Node.fireEvent(Node.java:9026)
	at javafx.controls@25.0.1/javafx.scene.control.Button.fire(Button.java:203)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.behavior.ButtonBehavior.mouseReleased(ButtonBehavior.java:207)
	at javafx.controls@25.0.1/com.sun.javafx.scene.control.inputmap.InputMap.handle(InputMap.java:274)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler$NormalEventHandlerRecord.handleBubblingEvent(CompositeEventHandler.java:247)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventHandler.dispatchBubblingEvent(CompositeEventHandler.java:80)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:232)
	at javafx.base@25.0.1/com.sun.javafx.event.EventHandlerManager.dispatchBubblingEvent(EventHandlerManager.java:189)
	at javafx.base@25.0.1/com.sun.javafx.event.CompositeEventDispatcher.dispatchBubblingEvent(CompositeEventDispatcher.java:59)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:58)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.BasicEventDispatcher.dispatchEvent(BasicEventDispatcher.java:56)
	at javafx.base@25.0.1/com.sun.javafx.event.EventDispatchChainImpl.dispatchEvent(EventDispatchChainImpl.java:114)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEventImpl(EventUtil.java:74)
	at javafx.base@25.0.1/com.sun.javafx.event.EventUtil.fireEvent(EventUtil.java:54)
	at javafx.base@25.0.1/javafx.event.Event.fireEvent(Event.java:199)
	at javafx.graphics@25.0.1/javafx.scene.Scene$MouseHandler.process(Scene.java:4061)
	at javafx.graphics@25.0.1/javafx.scene.Scene.processMouseEvent(Scene.java:1947)
	at javafx.graphics@25.0.1/javafx.scene.Scene$ScenePeerListener.mouseEvent(Scene.java:2784)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:353)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler$MouseEventNotification.get(GlassViewEventHandler.java:255)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.QuantumToolkit.runWithoutRenderLock(QuantumToolkit.java:424)
	at javafx.graphics@25.0.1/com.sun.javafx.tk.quantum.GlassViewEventHandler.handleMouseEvent(GlassViewEventHandler.java:387)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.handleMouseEvent(View.java:573)
	at javafx.graphics@25.0.1/com.sun.glass.ui.View.notifyMouse(View.java:975)
	at javafx.graphics@25.0.1/com.sun.glass.ui.mac.MacView.notifyMouse(MacView.java:131)
Caused by: org.hibernate.TransientPropertyValueException: Not-null property references a transient value - transient instance must be saved before current operation : com.hotel.model.Reservation.guest -> com.hotel.model.Guest
	at org.hibernate.action.internal.UnresolvedEntityInsertActions.checkNoUnresolvedActionsAfterOperation(UnresolvedEntityInsertActions.java:122)
	at org.hibernate.engine.spi.ActionQueue.checkNoUnresolvedActionsAfterOperation(ActionQueue.java:436)
	at org.hibernate.internal.SessionImpl.checkNoUnresolvedActionsAfterOperation(SessionImpl.java:623)
	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:766)
	... 62 more



