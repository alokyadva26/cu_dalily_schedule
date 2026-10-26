# Migration Plan: CU Daily Schedule (React to Java Desktop Swing)

## 1. Overview and Objectives

The objective is to migrate the **CU Daily Schedule** application from a web-based React/Next.js stack to a desktop-based **Java Swing + JDBC** architecture. Both applications must operate against the same underlying PostgreSQL schema and data model.

---

## 2. Implementation Stages

### Stage A: Analysis & Documentation (Completed)
- Inspect existing codebase, components, routes, database schemas, and seed files.
- Produce `PROJECT_ANALYSIS.md`, `FEATURE_MAPPING.md`, `DATABASE_MAPPING.md`, and `MIGRATION_PLAN.md`.
- Document security, RLS policies, and DNS/network findings.

### Stage B: Maven Project Foundation & Configuration
- Create clean Maven project directory: `cu-daily-schedule-java`.
- Configure `pom.xml`:
  - Java version: JDK 21.
  - Dependencies:
    - PostgreSQL JDBC Driver (`org.postgresql:postgresql:42.7.3`)
    - FlatLaf Modern Look and Feel (`com.formdev:flatlaf:3.4.4`) for clean, anti-aliased UI aesthetics
    - Testing framework (`org.junit.jupiter:junit-jupiter:5.10.2`)
- Create `.env.example` and `config/DBConnection.java`:
  - Read database connection parameters from `.env` or system environment.
  - Support SSL (`ssl=true`, `sslmode=require`).
  - Provide safe diagnostic connection tests.

### Stage C: Data Layer & Core Business Logic
- Create POJO Models in `com.cudailyschedule.model`:
  - `Timetable.java`
  - `Teacher.java`
  - `Student.java`
  - `ScheduleEntry.java`
  - `ScheduleStatus.java`
- Implement DAOs in `com.cudailyschedule.dao`:
  - `TimetableDAO.java` (CRUD)
  - `TeacherDAO.java` (Lookup by employee ID, list all, create, update)
  - `StudentDAO.java` (Lookup by student UID, list all, create, update)
  - `ScheduleEntryDAO.java` (Lookup by timetable and day of week, insert, update, delete)
- Implement Services in `com.cudailyschedule.service`:
  - `ScheduleTimingService.java` (Active/next/completed classification, countdown math in `Asia/Kolkata` time)
  - `TeacherService.java` (Teacher lookup & schedule retrieval)
  - `StudentService.java` (Student schedule retrieval & fallback to default student timetable)
  - `AdminScheduleService.java` (CRUD management with transaction handling)
- Provide dual-mode repository provider:
  - Direct JDBC when live Supabase PostgreSQL connection is active.
  - Built-in in-memory / JSON fallback provider when offline or awaiting fresh Supabase credentials.

### Stage D: Desktop User Interface (Java Swing)
- Implement UI Design System in `com.cudailyschedule.ui.theme`:
  - `UITheme.java` (Colors: Navy `#1a2b4c`, Accent Red `#E30613`, Background `#F7F8FA`, Card `#FFFFFF`, fonts, borders)
- Build Primary Screens in `com.cudailyschedule.ui`:
  - `MainFrame.java` (Main application window, CardLayout navigation controller, header branding)
  - `LandingScreen.java` (CU logo, "DAILY SCHEDULE - SMART TIMETABLE DEVICE", Student NFC Card, Teacher QR Code Card)
  - `StudentScheduleScreen.java` (Header, timeline with class cards, empty weekend state, end-of-day state, class details panel with circular countdown ring, refresh button)
  - `TeacherSearchScreen.java` (Manual ID input, sample teacher quick-picks, simulated QR badge scanner)
  - `TeacherScheduleScreen.java` (Profile header with avatar, live timer widget, timeline of classes, sticky status panel)
  - `AdminManagementDialog.java` (CRUD dialog for viewing and adding timetable schedule entries to test live database updates)

### Stage E: Authentication, Threading & Error Handling
- Use `SwingWorker` for all database calls to ensure the Event Dispatch Thread (EDT) never freezes.
- Handle connection timeouts and SQL errors with user-friendly dialogs.
- Preserve student/teacher role boundaries and validation.

### Stage F: Testing & Cross-Platform Validation
- Automated JUnit tests:
  - Database connection configuration parsing.
  - Time calculation and countdown tests.
  - Schedule status classification (ongoing, next, completed, finished).
  - Subject registry mapping.
  - DAO SQL query correctness and parameterization.
- Document manual verification steps in `docs/TESTING.md`.

### Stage G: Documentation, Deliverables & Final Report
- `docs/TESTING.md`
- `docs/LIMITATIONS.md`
- `README.md` with build and run instructions.
- Screenshots and academic project report structure for CSE submission.
