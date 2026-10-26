# Project Analysis: CU Daily Schedule (React + Supabase to Java Desktop Swing + JDBC)

## 1. Executive Summary

**CU Daily Schedule** is a smart campus timetable application tailored for Chandigarh University (CU). The original web application was developed using **Next.js 15 (App Router)**, **React 19**, **TypeScript**, **Tailwind CSS v4**, and **Supabase (PostgreSQL)**. 

The goal of this migration is to create a robust, standalone **Java Desktop Application** using **Java Swing**, **Core Java 21**, **JDBC (PostgreSQL driver)**, and **Maven**, connecting to the same underlying Supabase PostgreSQL database while preserving full UI fidelity, business logic, workflows, and database schema compatibility without breaking the existing web application.

---

## 2. Source Code & Architecture Inspection

### 2.1 File Structure Analysis
```
c:/Workspace/Active projects/cu_daily_schedule _java/
├── .env.local
├── cu-daily-schedule/
│   ├── .env.local
│   ├── package.json
│   ├── supabase/
│   │   ├── schema.sql           -- DDL: triggers, tables (timetables, teachers, students, schedule_entries)
│   │   ├── policies.sql         -- RLS policies: public SELECT permissions
│   │   └── seed.sql             -- Sample data for teachers Neeraj, Gitanjali, Jaspreet
│   ├── src/
│   │   ├── app/
│   │   │   ├── page.tsx         -- Landing screen: NFC Student Card & QR Teacher Scanner entry points
│   │   │   ├── layout.tsx       -- Global layout shell
│   │   │   ├── globals.css      -- CUIMS design tokens & color palette
│   │   │   ├── student/
│   │   │   │   └── page.tsx     -- Student schedule view with timeline, live cards, details panel
│   │   │   └── teacher/
│   │   │       ├── page.tsx     -- Teacher ID search input & QR scanner trigger
│   │   │       └── [employeeId]/
│   │   │           └── page.tsx -- Teacher schedule view with profile header & real-time countdown
│   │   ├── components/
│   │   │   ├── Header.tsx           -- CU header with calendar badge and current date
│   │   │   ├── ScheduleCard.tsx     -- Timeline card item for student view
│   │   │   ├── ClassDetails.tsx     -- Active/selected class details with circular progress timer
│   │   │   ├── EmptyState.tsx       -- Weekend / holiday empty state illustration
│   │   │   ├── EndOfDayState.tsx    -- Classes concluded view with "View Tomorrow" action
│   │   │   ├── BottomCard.tsx       -- Last updated timestamp & refresh action
│   │   │   └── teacher/
│   │   │       ├── QRScanner.tsx            -- html5-qrcode camera scanner
│   │   │       ├── TeacherProfileHeader.tsx -- Teacher avatar, ID, department & countdown widget
│   │   │       ├── TeacherScheduleCard.tsx  -- Timeline entry for teacher classes
│   │   │       └── TeacherScheduleView.tsx  -- Split layout with timeline and sticky status panel
│   │   ├── data/
│   │   │   └── timetable.json   -- 258 lines of sample student schedule entries
│   │   ├── lib/
│   │   │   ├── db.ts            -- Supabase query helper functions
│   │   │   ├── supabase.ts      -- Supabase JS client initializer
│   │   │   ├── teacherSchedule.ts -- Kolkata time logic, time parsing, status calculation
│   │   │   └── utils.ts         -- Date-fns formatting, subject mapping dictionary
│   │   └── types/
│   │       └── index.ts         -- ClassSession and Timetable types
```

---

## 3. Existing Features & User Workflows

### 3.1 Device Landing Screen (`/`)
- **Branding**: Displays the Chandigarh University logo (`cu-logo.png`), watermark background (`cu-bg.png`), and uppercase header `"DAILY SCHEDULE - SMART TIMETABLE DEVICE"`.
- **Two Primary Portals**:
  1. **Tap Your NFC Card**: Simulates student NFC swipe; navigates to `/student`.
  2. **Scan Teacher QR Code**: Simulates faculty badge scan; navigates to `/teacher`.
- **Status Indicator**: Displays `"DEVICE READY - Please tap your NFC card or scan QR code"` with red branding accent.

### 3.2 Student Portal Workflow (`/student`)
1. **Clock & Calendar Sync**: Evaluates current system time against Indian Standard Time (IST / Asia/Kolkata).
2. **Weekend & Holiday Detection**: On Saturdays and Sundays, displays `EmptyState` ("No Classes Today! Enjoy your day off!").
3. **Daily Schedule Timeline**:
   - Groups classes by day of week.
   - Highlights the **current active class** (red accent, active pulse, live "Ends In" countdown).
   - Highlights the **next upcoming class**.
   - Marks finished classes with a green checkmark and dimmed styling.
   - Handles "Break" intervals with custom coffee icon badge.
4. **Interactive Class Details Panel**:
   - Displays subject name (mapped from course codes such as `24CSP-305` to `Competitive Coding-II`), faculty name, room/block number, and time bounds.
   - When a class is ongoing, renders an active circular SVG progress countdown showing exact remaining minutes and seconds.
5. **End-of-Day Transition**:
   - When all classes for the current day have concluded, renders `EndOfDayState` ("Classes are Over!").
   - Provides a "View Tomorrow's Schedule" button to preview next-day classes without changing system clock.
6. **Auto & Manual Refresh**: Displays last updated timestamp and a refresh button.

### 3.3 Teacher Portal Workflow (`/teacher` and `/teacher/[employeeId]`)
1. **Teacher Identification**:
   - Manual search input for Employee ID (e.g., `E19761`, `E16525`, `E10279`).
   - Integrated camera-based QR code scanner decoding employee ID tokens.
2. **Teacher Schedule Dashboard**:
   - If ID is invalid/not found: Displays clean error state ("Teacher Not Found - Please check Employee ID").
   - Profile Header: Teacher avatar initial, full name, employee ID, department, and role ("Assistant Professor").
   - Dynamic Countdown Header Widget: Displays real-time countdown timer ("CLASS ENDS IN" or "NEXT CLASS IN") in hours, minutes, and seconds.
   - Left Column Timeline: Ordered chronological list of classes for the day with start/end time, course name, code, type (Lecture / Tutorial / Practical), room/block (`Block-C3 / 613`), section (`26ADS-513`), and student group.
   - Right Column Dynamic Panel: Sticky detail card showing current/next lecture status, group breakdown, and faculty credentials.
   - Weekend/No Classes State: Displays empty day card ("No classes scheduled today. Enjoy your day!").

---

## 4. Supabase Database & Security Analysis

### 4.1 Schema Tables & Constraints
- **`timetables`**: Parent timetable entity (`id UUID PK`, `timetable_name TEXT`, `timetable_type TEXT CHECK IN ('student', 'teacher')`, timestamps).
- **`teachers`**: Faculty entity (`id UUID PK`, `employee_id TEXT UNIQUE`, `employee_name TEXT`, `department TEXT`, `timetable_id UUID FK -> timetables(id)`).
- **`students`**: Student entity (`id UUID PK`, `student_uid TEXT UNIQUE`, `student_name TEXT`, `timetable_id UUID FK -> timetables(id)`).
- **`schedule_entries`**: Timetable entry items (`id UUID PK`, `timetable_id UUID FK -> timetables(id)`, `day_of_week`, `course_name`, `course_code`, `start_time`, `end_time`, `section`, `course_type`, `student_group`, `block_no`, `room_no`, `partition`).

### 4.2 Row Level Security (RLS) & Auth Dependencies
- `supabase/policies.sql` enables RLS on all 4 tables with:
  ```sql
  CREATE POLICY "Allow public read access on <table_name>" ON <table_name> FOR SELECT USING (true);
  ```
- **Authentication Findings**:
  - The React frontend does NOT use `supabase.auth.signInWithPassword` or Supabase Auth user accounts (`auth.users`).
  - Access is role-based and public read-only via employee ID and student UID identification.
  - Anon key has read-only `SELECT` access.
  - Insert, Update, and Delete operations require direct PostgreSQL database connection or service privileges.

### 4.3 Network & Host Diagnostics
- Environment variable: `NEXT_PUBLIC_SUPABASE_URL=https://hszegowkuthevthdfqym.supabase.co`
- Live DNS check: `hszegowkuthevthdfqym.supabase.co` is currently non-existent/inaccessible (standard free-tier inactivity pause or placeholder).
- **Architectural Requirement**: The Java desktop application must provide:
  1. A standard configurable JDBC connection manager (`DBConnection.java`) supporting standard PostgreSQL host, port, database, user, password, and SSL parameters via `.env` or `config.properties`.
  2. Clear error diagnostics if the remote Supabase database host is unreachable.
  3. A robust offline/demo data provider pre-seeded with the exact project seed data (`seed.sql` and `timetable.json`) so the application remains completely demonstrable and functional even when offline or awaiting fresh Supabase credentials.
  4. Fully parameterized CRUD operations in DAOs ready to execute against the live database the moment credentials are provided.

---

## 5. Technology Stack & Design System Mapping

| Dimension | Existing Web Implementation | Proposed Java Desktop Implementation |
|---|---|---|
| **Language & Runtime** | TypeScript / Node.js 20+ | Core Java 21 (LTS) |
| **UI Framework** | Next.js 15 App Router + React 19 | Java Swing (`JFrame`, `JPanel`, `CardLayout`, `JTable`, custom graphics) |
| **Look & Feel / Design** | Tailwind CSS v4, Glassmorphism, Inter font | Custom Flat/Modern Swing LookAndFeel with anti-aliasing, rounded borders, CUIMS colors |
| **Color Tokens** | `#E30613` (Accent Red), `#1a2b4c` (Navy), `#F7F8FA` (BG), `#FFFFFF` (Card) | Java `Color` constants matching exact hex codes |
| **Database Access** | `@supabase/supabase-js` (PostgREST over HTTP) | PostgreSQL JDBC Driver (`org.postgresql:postgresql:42.7.3`) with `PreparedStatement` |
| **Build & Dependency Management** | `npm` / `package.json` | Apache Maven 3.10+ / `pom.xml` |
| **Timezone Management** | `date-fns` with `Asia/Kolkata` | `java.time.ZonedDateTime`, `ZoneId.of("Asia/Kolkata")`, `LocalTime` |
| **Concurrency & Backgrounding** | React `useEffect`, async/await | `SwingWorker<T, V>`, `ScheduledExecutorService`, Event Dispatch Thread isolation |
| **Data Models** | TypeScript interfaces (`DatabaseScheduleEntry`, `TeacherData`) | Strongly-typed Java POJOs with getters, setters, builder/constructors |
