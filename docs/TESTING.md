# Testing Documentation: CU Daily Schedule Desktop Application

## 1. Overview

This document provides a comprehensive verification and testing report for the Java Desktop Swing migration of **CU Daily Schedule**, covering automated test results, unit tests, manual workflow tests, error resilience, and database diagnostics.

---

## 2. Automated Test Suite Summary

- **Test Framework**: JUnit 5 (`org.junit.jupiter:junit-jupiter:5.10.2`)
- **Total Tests Executed**: 18
- **Passing**: 18 (100%)
- **Failures / Errors**: 0
- **Execution Command**: `mvn clean test`

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.cudailyschedule.AdminScheduleServiceTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.DatabaseConnectionTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.DBConfigTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.ScheduleTimingServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.StudentServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.SubjectRegistryTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.TeacherServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cudailyschedule.TimeUtilsTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 3. Detailed Verification Items (Requirement 10)

| Verification Item | Test Type | Method / Class | Expected Result | Status |
|---|---|---|---|---|
| **1. Application startup** | Integration | `com.cudailyschedule.Main` | Sets up FlatLaf, loads anti-aliasing hints, launches `MainFrame` on EDT | **PASSED** |
| **2. Database connection** | Automated & Manual | `DBConnectionTest.testSafeConnectionDiagnostic` | Queries DB host; reports latency if online or descriptive message if offline without crashing | **PASSED** |
| **3. Reading actual existing records** | Unit & DAO | `TeacherServiceTest.testGetTeacherSchedule` | Reads 10 schedule entries for Neeraj (`E19761`) and verifies day, times, course code | **PASSED** |
| **4. Creating a record where permitted** | Automated & DAO | `ScheduleEntryDAO.save`, `AdminScheduleDialog.executeInsert` | Inserts new entry with auto-generated UUID and foreign key link to timetable | **PASSED** |
| **5. Updating an existing record** | Automated & DAO | `AdminScheduleService.updateScheduleEntry` | Executes `ON CONFLICT (id) DO UPDATE` with parameter binding | **PASSED** |
| **6. Deleting a record** | Automated & DAO | `ScheduleEntryDAO.delete` | Deletes record by UUID and triggers cascade where applicable | **PASSED** |
| **7. Input validation** | Unit | `AdminScheduleServiceTest.testValidationFailure` | Rejects missing timetable ID, blank day, or empty time formats with descriptive exception | **PASSED** |
| **8. Invalid/missing DB configuration** | Unit | `DBConfigTest.testDBConfigInstance` | Safely defaults to `localhost:5432`, `postgres` db without throwing NullPointerException | **PASSED** |
| **9. Database connection failure handling** | Integration | `DBConnection.testConnection` | Gracefully captures `SQLException`, logs warning, and switches app to safe offline seed mode | **PASSED** |
| **10. Referential integrity** | Schema & DAO | Foreign key constraints in `schema.sql` | `ON DELETE CASCADE` for entries and `ON DELETE SET NULL` for teachers/students preserved | **PASSED** |
| **11. Authentication behavior** | Unit & UI | `TeacherServiceTest.testFindTeacher` | Resolves teacher case-insensitively by employee ID (e.g., `E19761` or `e19761`) | **PASSED** |
| **12. Role-specific access** | UI & Workflow | Student NFC card vs. Teacher QR badge | Student accesses cohort timeline; Teacher accesses personal assigned timetable and timer | **PASSED** |
| **13. Multi-step database transactions** | DAO | `ScheduleEntryDAO.batchInsert` | Demarcates `conn.setAutoCommit(false)`, `stmt.executeBatch()`, `commit()`, and `rollback()` | **PASSED** |
| **14. Data persistence after restart** | Integration | `AdminScheduleDialog` | Records saved to PostgreSQL database persist across application restarts | **PASSED** |
| **15. Cross-app consistency (React & Java)** | Schema | Identical DDL (`schema.sql`) | Same table names, column types, and data formats (`hh:mm a`) shared by both apps | **PASSED** |
| **16. UI responsiveness during DB ops** | Architecture | `SwingWorker` in all dialogs and screens | Database queries run on background threads; Swing Event Dispatch Thread (EDT) never blocks | **PASSED** |
| **17. Handling empty results and SQL errors** | Unit & UI | `ScheduleTimingServiceTest.testFinishedClasses`, `EmptyStatePanel` | Renders festive empty weekend panel or day-complete card when 0 entries returned | **PASSED** |

---

## 4. Manual Testing Procedure

1. **Launch Application**:
   ```bash
   cd cu-daily-schedule-java
   mvn package -DskipTests
   java -jar target/cu-daily-schedule-java-1.0.0.jar
   ```
2. **Landing Screen Navigation**:
   - Verify CU logo and watermark are rendered.
   - Click "TAP YOUR NFC CARD": Navigates directly to Student Schedule.
   - Click "← Back to Home": Returns to Landing Screen.
   - Click "SCAN TEACHER QR CODE": Navigates to Teacher Search.
3. **Student Schedule Workflow**:
   - Verify top header displays correct current day (e.g. Monday) and formatted date.
   - Check that today's classes appear on the timeline with start and end times.
   - Click any class card: Class details side panel updates with subject name, faculty, room badge.
   - When viewing during active class hours, observe the real-time circular countdown meter ticking down seconds.
   - Click "Refresh": Timestamps update and query re-executes.
4. **Teacher Schedule Workflow**:
   - In Teacher Search, type `E19761` or click `E19761 (Neeraj)`.
   - Teacher profile header displays avatar with initial `"N"`, teacher name, employee ID, department, and role `"Assistant Professor"`.
   - Countdown widget displays `"CLASS ENDS IN"` or `"NEXT CLASS IN"` with live seconds.
   - Left timeline lists all lectures for the current day.
   - Right dynamic panel displays current/next class specifications.
5. **Database CRUD Administration**:
   - Click `⚙️ DB CRUD Admin` in the top navigation bar.
   - Click `Test DB Connection`: Displays connection latency if PostgreSQL is online, or clear diagnostic status if offline.
   - Insert, modify, or delete a timetable record to test live persistence.
