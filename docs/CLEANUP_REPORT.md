# Project Cleanup & File Audit Report

**Date & Time**: 2026-10-26  
**Auditor**: Senior Java Desktop Architect & Build Engineer  
**Workspace Root**: `c:\Workspace\Active projects\cu_daily_schedule _java`  
**Java Project Root**: `c:\Workspace\Active projects\cu_daily_schedule _java\cu-daily-schedule-java`  
**Original React Project**: `c:\Workspace\Active projects\cu_daily_schedule _java\cu-daily-schedule` *(Preserved & Untouched)*  

---

## 1. Safety Rules & Workspace Boundary Audit

Prior to performing any cleanup, the physical directory boundaries were inspected and verified:
1. **Original React Project Directory**: `cu-daily-schedule/`  
   - Status: Git repository on `main` branch.
   - Git Status: `nothing to commit, working tree clean`.
   - Decision: **Strictly untouched**. No files within this directory have been modified or deleted.
2. **Java Migration Project Directory**: `cu-daily-schedule-java/`  
   - Status: Independent Maven project containing complete Java Swing, JDBC, service, and DAO code.
   - Decision: All verification, builds, testing, and file classifications operate exclusively inside this project tree.
3. **Workspace Root**: Contains global navigation docs, root README, and screenshots for convenient evaluation.

---

## 2. File Classification & Audit Inventory

Every file within the Java migration workspace was audited and classified into categories A through G:

### Category A: Essential Java Source Files (`src/main/java`)

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| `Main.java` | Main application entry point; initializes FlatLaf look-and-feel and launches `MainFrame` on EDT | Yes | Retain | Primary application bootstrap |
| `config/DBConfig.java` | Configuration manager; loads host, port, database, credentials, and SSL settings from environment or local `.env` | Yes | Retain | Essential database configuration layer |
| `config/DBConnection.java` | Pure JDBC connection manager; provides parameterized queries, connection testing, and transactions | Yes | Retain | Reusable database connectivity provider |
| `model/Timetable.java` | Entity POJO matching the `timetables` PostgreSQL table | Yes | Retain | Core domain model |
| `model/Teacher.java` | Entity POJO matching the `teachers` PostgreSQL table | Yes | Retain | Core domain model |
| `model/Student.java` | Entity POJO matching the `students` PostgreSQL table | Yes | Retain | Core domain model |
| `model/ScheduleEntry.java` | Entity POJO matching the `schedule_entries` PostgreSQL table | Yes | Retain | Core domain model |
| `model/ClassSession.java` | Timetable session model matching the JSON/student schedule representation | Yes | Retain | Domain model for student timetable |
| `model/ScheduleStatus.java` | Data holder for real-time status (`BEFORE`, `ONGOING`, `COMPLETED`), countdown seconds, and active slots | Yes | Retain | Status calculation representation |
| `dao/TimetableDAO.java` | Data Access Object for `timetables` CRUD operations | Yes | Retain | Persistence layer |
| `dao/TeacherDAO.java` | Data Access Object for `teachers` lookup by employee ID and CRUD | Yes | Retain | Persistence layer |
| `dao/StudentDAO.java` | Data Access Object for `students` lookup by UID and CRUD | Yes | Retain | Persistence layer |
| `dao/ScheduleEntryDAO.java` | Data Access Object for `schedule_entries` CRUD and multi-row batch insert transactions | Yes | Retain | Persistence layer with transaction management |
| `service/ScheduleTimingService.java` | Calculates active class, next class, and countdown timers in `Asia/Kolkata` time | Yes | Retain | Core business logic layer |
| `service/TeacherService.java` | Retrieves teacher schedules via JDBC with seed data fallback | Yes | Retain | Core business service layer |
| `service/StudentService.java` | Retrieves student schedules via JDBC with bundled fallback | Yes | Retain | Core business service layer |
| `service/AdminScheduleService.java` | Provides timetable management and CRUD validation | Yes | Retain | Business validation & admin service |
| `service/DatabaseService.java` | Coordinates connection diagnostics and online/offline status reporting | Yes | Retain | Database health monitoring |
| `ui/MainFrame.java` | Top-level `JFrame` window with CardLayout screen switcher and top toolbar | Yes | Retain | Main Swing window controller |
| `ui/LandingScreen.java` | Smart device landing screen with Student NFC and Teacher QR cards | Yes | Retain | Primary application screen |
| `ui/StudentScheduleScreen.java` | Student timetable dashboard with timeline, details panel, and end-of-day states | Yes | Retain | Primary student screen |
| `ui/TeacherSearchScreen.java` | Teacher Mode screen with Employee ID search and simulated QR badge scanner | Yes | Retain | Primary teacher search screen |
| `ui/TeacherScheduleScreen.java` | Teacher dashboard with profile header, live countdown widget, and dynamic panel | Yes | Retain | Primary teacher schedule screen |
| `ui/AdminScheduleDialog.java` | Management dialog with `JTable`, CRUD operations, and DB test | Yes | Retain | Academic requirement demonstrating Swing CRUD & SwingWorker |
| `ui/components/CircularProgressIndicator.java` | Anti-aliased circular progress countdown component | Yes | Retain | Recreates React SVG countdown ring |
| `ui/components/HeaderPanel.java` | Header bar with calendar badge, title, and live date/day labels | Yes | Retain | Common UI header component |
| `ui/components/ScheduleCardPanel.java` | Interactive timeline card with start/end time and status node | Yes | Retain | Timeline card component |
| `ui/components/ClassDetailsPanel.java` | Detailed class specifications panel with countdown meter | Yes | Retain | Details side panel component |
| `ui/components/TeacherProfileHeaderPanel.java` | Teacher avatar with initial and live countdown widget | Yes | Retain | Teacher profile header component |
| `ui/components/TeacherScheduleCardPanel.java` | Teacher timeline card matching the React design | Yes | Retain | Teacher timeline card component |
| `ui/components/TeacherDynamicPanel.java` | Sticky right panel showing current lecture status | Yes | Retain | Teacher dynamic status component |
| `ui/components/EmptyStatePanel.java` | Weekend / holiday empty state ("🎉 No Classes Today!") | Yes | Retain | Empty state component |
| `ui/components/EndOfDayPanel.java` | Classes concluded panel ("🎉 Classes are Over!") with tomorrow preview | Yes | Retain | End-of-day component |
| `ui/components/FooterPanel.java` | Auto-update indicator and manual Refresh button | Yes | Retain | Bottom footer component |
| `ui/theme/UITheme.java` | Design system tokens (CU Red `#E30613`, Navy `#1A2B4C`, typography, borders) | Yes | Retain | Central UI styling |
| `util/SubjectRegistry.java` | Maps course codes to readable subject titles | Yes | Retain | Course mapping utility |
| `util/TimeUtils.java` | Time parsing, Kolkata timezone conversions, and countdown formatting | Yes | Retain | Time calculation utility |
| `util/ImageUtils.java` | Image loading, caching, and smooth scaling | Yes | Retain | Graphic resource utility |

### Category B: Runtime Resources (`src/main/resources`)

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| `resources/data/timetable.json` | Complete student cohort timetable dataset (258 lines) | Yes | Retain | Offline fallback dataset packaged inside JAR |
| `resources/images/cu-logo.png` | Chandigarh University official logo asset | Yes | Retain | UI branding image |
| `resources/images/cu-bg.png` | Chandigarh University campus watermark background | Yes | Retain | UI background watermark image |

### Category C: Build Configuration

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| `pom.xml` | Maven build descriptor specifying Java 21, FlatLaf, PostgreSQL driver, Jackson, and Shade plugin | Yes | Retain | Primary project build descriptor |
| `.gitignore` | Excludes `target/`, `.env`, and IDE artifacts from version control | Yes | Retain | Clean repository management |
| `.env.example` | Sanitized template for database connection parameters | Yes | Retain | Configuration documentation |

### Category D: Automated Tests (`src/test/java`)

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| `AdminScheduleServiceTest.java` | Tests validation logic for timetable entries | Yes | Retain | Unit test |
| `DatabaseConnectionTest.java` | Tests connection diagnostic behavior without crashing | Yes | Retain | Integration test |
| `DBConfigTest.java` | Tests configuration parsing, SSL options, and defaults | Yes | Retain | Unit test |
| `ScheduleTimingServiceTest.java` | Tests active class, next class, and countdown calculations | Yes | Retain | Unit test |
| `StudentServiceTest.java` | Tests loading bundled timetable and day-of-week filtering | Yes | Retain | Unit test |
| `SubjectRegistryTest.java` | Tests course code mapping and break handling | Yes | Retain | Unit test |
| `TeacherServiceTest.java` | Tests employee ID lookup, case insensitivity, and seed fallback | Yes | Retain | Unit test |
| `TimeUtilsTest.java` | Tests AM/PM time parsing, duration math, and MM:SS formatting | Yes | Retain | Unit test |
| `ScreenshotGenerator.java` | Renders and captures high-resolution screenshots of all 4 screens | Yes | Retain | Test utility for submission deliverables |

### Category E: Documentation & Submission Deliverables

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| `README.md` | Comprehensive user manual, prerequisites, run guide, and troubleshooting | Yes | Retain | Primary run and submission guide |
| `database/README.md` | Schema DDL, triggers, RLS policies, and seed data guide | Yes | Retain | Database documentation |
| `docs/PROJECT_ANALYSIS.md` | Source inspection report of the original React application | Yes | Retain | Submission deliverable |
| `docs/FEATURE_MAPPING.md` | 1-to-1 feature comparison matrix between React and Java | Yes | Retain | Submission deliverable |
| `docs/DATABASE_MAPPING.md` | PostgreSQL schema mapping, types, and Mermaid ER diagram | Yes | Retain | Submission deliverable |
| `docs/MIGRATION_PLAN.md` | Architecture and migration phase plan | Yes | Retain | Submission deliverable |
| `docs/TESTING.md` | Detailed automated test results and manual test verification procedures | Yes | Retain | Submission deliverable |
| `docs/LIMITATIONS.md` | Technical boundaries, DNS diagnostics, and security notes | Yes | Retain | Submission deliverable |
| `docs/COLLEGE_REPORT.md` | Academic project report content for B.Tech CSE submission | Yes | Retain | Submission deliverable |
| `screenshots/01_landing_screen.png` | Authentic screenshot of the Landing Hub | Yes | Retain | Visual deliverable |
| `screenshots/02_student_schedule_screen.png` | Authentic screenshot of Student Timetable & Countdown | Yes | Retain | Visual deliverable |
| `screenshots/03_teacher_search_screen.png` | Authentic screenshot of Teacher Search & QR Simulator | Yes | Retain | Visual deliverable |
| `screenshots/04_teacher_schedule_screen.png` | Authentic screenshot of Teacher Schedule Dashboard | Yes | Retain | Visual deliverable |

### Category F: Generated Build Artifacts (`target/`)

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| `target/cu-daily-schedule-java-1.0.0.jar` | Standalone executable Fat JAR | Yes | Retain / Rebuild | Shaded binary containing all dependencies |
| `target/original-cu-daily-schedule-java-1.0.0.jar` | Pre-shaded intermediate build artifact | No | Transient | Automatically cleaned during `mvn clean` |
| `target/classes/`, `target/test-classes/` | Compiled bytecode classes | No | Transient | Automatically managed by Maven |

### Category G: Unnecessary or Obsolete Files Identified

| File Path | Current Purpose | Required? | Decision | Reason |
|---|---|---|---|---|
| Reference to `cu-daily-schedule/src/data/timetable.json` in `StudentService.java` | Redundant third fallback to external React directory | No | **Removed** | Java project must be completely decoupled from the React directory |
| Reference to `cu-daily-schedule/.env.local` in `DBConfig.java` | Search path pointing to external React folder | No | **Removed** | Java application must be self-contained and read its own configuration |

---

## 3. Summary of Cleanup Actions

1. **Decoupling from External React Folder**:
   - `DBConfig.java`: Removed search path `new File("cu-daily-schedule/.env.local")` and `new File("../.env.local")`. The Java application now strictly looks for `.env`, `.env.local`, `config.properties`, or system environment variables in its own runtime directory.
   - `StudentService.java`: Removed fallback `new File("cu-daily-schedule/src/data/timetable.json")`. The service now relies exclusively on `getClass().getResourceAsStream("/data/timetable.json")` (packaged directly in the JAR) and local `src/main/resources/data/timetable.json`.
2. **React Project Boundary Protection**:
   - Confirmed zero modifications or deletions inside `cu-daily-schedule/`.
   - Verified that no JavaScript, TypeScript, node_modules, or Next.js build artifacts exist in `cu-daily-schedule-java/`.
