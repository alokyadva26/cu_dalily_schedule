# CU Daily Schedule — Java Desktop Application
## Windows Run Guide & Technical Manual

> A high-performance, modern desktop timetable application built with **Java 21 (LTS)**, **Java Swing (FlatLaf)**, **JDBC**, and **Maven**, integrated with the **Supabase PostgreSQL** database.

---

## 1. Prerequisites

Before running the application on Windows, verify that your development environment meets the following requirements:

1. **Operating System**: Windows 10 or Windows 11 (64-bit).
2. **Java Development Kit (JDK)**: **JDK 21** or later (Oracle JDK 21, Eclipse Temurin 21, or OpenJDK 21).
   - Ensure the `JAVA_HOME` environment variable points to your JDK 21 directory.
   - Ensure `%JAVA_HOME%\bin` is included in your system `Path`.
3. **Apache Maven**: **Maven 3.8.0** or later (Maven 3.10+ recommended).
   - Ensure the Maven `bin` directory is in your system `Path`.
4. **Network Access (Optional for Live DB)**:
   - Outbound internet access on TCP port `5432` or `6543` if connecting to a live remote Supabase PostgreSQL database.
   - *Note*: If offline or no network connection is available, the application automatically launches in **Offline Fallback Mode** with zero crashes.

---

## 2. Project Directory Identification

The complete Java desktop application is located in the `cu-daily-schedule-java` directory.

- **Workspace Root**: `c:\Workspace\Active projects\cu_daily_schedule _java`
- **Exact Java Project Root**: `c:\Workspace\Active projects\cu_daily_schedule _java\cu-daily-schedule-java`
- **Original React Project Root**: `c:\Workspace\Active projects\cu_daily_schedule _java\cu-daily-schedule` *(Preserved, untouched reference codebase)*

> **Important**: Always navigate into the `cu-daily-schedule-java` directory before running Maven or Java commands:
> ```powershell
> cd "c:\Workspace\Active projects\cu_daily_schedule _java\cu-daily-schedule-java"
> ```

---

## 3. Verify Java & Maven Installation

Open **PowerShell** or **Command Prompt** and run the following commands to verify your tools:

```powershell
java -version
javac -version
mvn -version
```

### Expected Output
- `java -version`: Must report version `21` (e.g. `java version "21.0.10"`)
- `javac -version`: Must report `javac 21.x.x`
- `mvn -version`: Must report `Apache Maven 3.x.x`

If any of these commands return `'mvn' is not recognized` or an older Java version, refer to the [Troubleshooting](#8-troubleshooting) section below.

---

## 4. Run Automated Tests

To run the complete JUnit 5 test suite:

```powershell
mvn clean test
```

### Expected Result
- **18 tests executed across 8 test suites** (0 failures, 0 errors, 0 skipped).
- Tests validate:
  - Time parsing, MM:SS countdown formatting, and Indian Standard Time (`Asia/Kolkata`) math.
  - Active lecture detection and circular progress logic.
  - Subject code lookups and break intervals.
  - Teacher lookup by Employee ID (with offline seed fallback).
  - Student cohort schedule parsing from packaged resources.
  - Safe database connection diagnostics without crashes.

---

## 5. Build the Executable Application

To compile the source code and assemble the standalone executable "fat JAR" with all dependencies shaded:

```powershell
mvn clean package
```

### Build Artifacts
Upon a successful build (`BUILD SUCCESS`), Maven outputs the executable JAR to:
```
cu-daily-schedule-java/target/cu-daily-schedule-java-1.0.0.jar
```
- **File Size**: ~9.7 MB (contains FlatLaf, PostgreSQL driver, Jackson Databind, and bundled runtime assets).
- **Pre-configured Manifest**: Automatically specifies `Main-Class: com.cudailyschedule.Main`.

---

## 6. How to Run the Application

You can run the application using either of the following two methods:

### Method A: Run via Executable Fat JAR (Recommended)

From the `cu-daily-schedule-java` directory, execute:

```powershell
java -jar target/cu-daily-schedule-java-1.0.0.jar
```

### Method B: Run via Maven

From the `cu-daily-schedule-java` directory, execute:

```powershell
mvn compile exec:java
```

---

## 7. Database Configuration (Supabase & Local PostgreSQL)

The application supports both **Live Database Mode** and **Offline Seed Fallback Mode**.

### 7.1 Configuration Priority
The application reads database configuration in the following order:
1. System Environment Variables (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SSL`, `DB_TIMEOUT`)
2. Local `.env` file in the project root
3. Local `.env.local` file
4. Local `config.properties` file
5. Fallback defaults (`localhost:5432`, `postgres`, offline fallback)

### 7.2 Setting Up Your `.env` File

1. In the `cu-daily-schedule-java` folder, copy the template `.env.example` to `.env`:
   ```powershell
   Copy-Item .env.example .env
   ```
2. Open `.env` in any text editor and fill in your database credentials:
   ```env
   # PostgreSQL / Supabase Database Configuration
   DB_HOST=aws-0-ap-south-1.pooler.supabase.com
   DB_PORT=6543
   DB_NAME=postgres
   DB_USER=postgres.your_project_ref
   DB_PASSWORD=your_actual_db_password
   DB_SSL=true
   DB_TIMEOUT=5
   ```

> **Security Note**: Never commit your `.env` file to version control. The repository's `.gitignore` automatically excludes `.env` and `.env.local`.

### 7.3 How to Obtain Connection Parameters from Supabase
1. Log in to your [Supabase Dashboard](https://supabase.com/dashboard).
2. Select your project.
3. Click the **Project Settings** (gear icon) in the left sidebar.
4. Navigate to **Database** → **Connection String**.
5. Select the **URI** or **Parameters** tab:
   - **Host**: e.g., `db.yourprojectref.supabase.co` or `aws-0-ap-south-1.pooler.supabase.com`
   - **Port**: `5432` (Direct) or `6543` (Session Pooler)
   - **Database**: `postgres`
   - **User**: `postgres` (or `postgres.yourprojectref` for pooling)
   - **Password**: The password you set when creating the Supabase project
   - **SSL**: Set `DB_SSL=true` (Supabase requires SSL encryption)

### 7.4 Running in Offline Mode (No Database Required)
If you do not have an active Supabase database or are developing offline:
- You do **not** need to configure `.env`.
- Simply launch the application via `java -jar target/cu-daily-schedule-java-1.0.0.jar`.
- The application will attempt to connect, detect that the database is unreachable within 5 seconds, and automatically engage **Offline Fallback Mode**.
- The top header will display the indicator: `○ Offline (Local Seed)`.
- Full timetable schedules, countdown calculations, teacher search (e.g., `E19761`), and UI interactions operate smoothly using the packaged seed dataset.

---

## 8. Troubleshooting

### Problem 1: `'mvn' is not recognized as an internal or external command`
- **Cause**: Apache Maven is either not installed or its `bin` folder is not added to the Windows system `Path`.
- **Solution**:
  1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi).
  2. Extract it to `C:\Program Files\apache-maven-3.x.x`.
  3. Open Windows Start menu, search for **Edit the system environment variables**, click **Environment Variables**.
  4. Under System variables, edit `Path` and append `C:\Program Files\apache-maven-3.x.x\bin`.
  5. Restart your PowerShell or Command Prompt terminal.

### Problem 2: `javac: invalid flag: --release` or Java Version Mismatch
- **Cause**: An older JDK (e.g. Java 8, 11, or 17) is currently active in your path.
- **Solution**:
  1. Verify the active Java version with `java -version`.
  2. Set `JAVA_HOME` to your JDK 21 installation path:
     ```powershell
     $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
     $env:Path = "$env:JAVA_HOME\bin;$env:Path"
     ```
  3. Verify with `javac -version` that version 21 is reported.

### Problem 3: `java.net.UnknownHostException` or Supabase DNS Errors
- **Cause**: The Supabase domain hostname (e.g., `hszegowkuthevthdfqym.supabase.co`) cannot be resolved. This occurs when a free-tier Supabase project has been paused after inactivity or the host URL is mistyped.
- **Solution**:
  1. Check your project status on the Supabase Dashboard. If paused, click **Restore Project**.
  2. Verify your hostname using PowerShell:
     ```powershell
     Resolve-DnsName db.yourprojectref.supabase.co
     ```
  3. In the meantime, the application seamlessly runs in Offline Fallback Mode.

### Problem 4: `Connection refused: connect` (localhost:5432)
- **Cause**: No `.env` file was found, so the application defaulted to `localhost:5432`, but no local PostgreSQL server is running.
- **Solution**:
  - This is expected behavior when running without a database; the application automatically engages offline fallback.
  - If you intended to connect to a local PostgreSQL server, ensure the PostgreSQL Windows service is running via Windows Services (`services.msc`).

### Problem 5: `FATAL: password authentication failed for user`
- **Cause**: The password supplied in `.env` or `DB_PASSWORD` does not match your database credentials.
- **Solution**:
  - Double check your password in `.env`.
  - If using Supabase, you can reset your database password under **Project Settings** → **Database** → **Database Password**.

### Problem 6: Error: `Unable to access jarfile target\cu-daily-schedule-java-1.0.0.jar`
- **Cause**: The JAR has not been built yet, or the command is being executed from the wrong directory.
- **Solution**:
  1. Confirm your working directory is `cu-daily-schedule-java`:
     ```powershell
     pwd
     ```
  2. Run `mvn clean package` to generate the JAR file.

---

## 9. Architecture & File Structure

```
cu-daily-schedule-java/
├── pom.xml                               # Maven project definition (Java 21, FlatLaf, PostgreSQL)
├── README.md                             # This comprehensive user & run guide
├── .gitignore                            # Excludes target/, .env, and IDE files
├── .env.example                          # Database configuration template
├── docs/                                 # Academic reports and architecture specifications
│   ├── CLEANUP_REPORT.md                 # Full file inventory, classification & audit decisions
│   ├── FINAL_VERIFICATION.md             # Execution proof, test logs & build verification
│   ├── COLLEGE_REPORT.md                 # Complete B.Tech CSE academic project report
│   ├── PROJECT_ANALYSIS.md               # Analysis of original React codebase
│   ├── FEATURE_MAPPING.md                # 1-to-1 React to Java desktop feature mapping
│   ├── DATABASE_MAPPING.md               # PostgreSQL schema & entity mappings
│   ├── MIGRATION_PLAN.md                 # Implementation architecture & phase plan
│   ├── TESTING.md                        # Detailed JUnit test suites & manual procedures
│   └── LIMITATIONS.md                    # Technical boundaries & hardware notes
├── database/
│   └── README.md                         # SQL DDL schemas, triggers, and seed records
├── screenshots/                          # Authentic UI screen captures
│   ├── 01_landing_screen.png             # Landing Screen (NFC & QR cards)
│   ├── 02_student_schedule_screen.png    # Student Timetable & Circular Countdown
│   ├── 03_teacher_search_screen.png      # Teacher ID Search & QR Scanner Simulator
│   └── 04_teacher_schedule_screen.png    # Teacher Profile Header & Lecture Timeline
└── src/
    ├── main/
    │   ├── java/com/cudailyschedule/
    │   │   ├── Main.java                 # Entry point: FlatLaf setup & MainFrame launch
    │   │   ├── config/                   # DBConfig, DBConnection
    │   │   ├── model/                    # Domain POJOs (Timetable, Teacher, Student, ScheduleEntry, ClassSession, ScheduleStatus)
    │   │   ├── dao/                      # DAO layer (PreparedStatement CRUD & transactions)
    │   │   ├── service/                  # Business services (ScheduleTimingService, TeacherService, StudentService, AdminScheduleService)
    │   │   ├── ui/                       # Swing UI screens (MainFrame, LandingScreen, StudentScheduleScreen, TeacherSearchScreen, TeacherScheduleScreen, AdminScheduleDialog)
    │   │   │   ├── components/           # Reusable widgets (CircularProgressIndicator, HeaderPanel, ScheduleCardPanel, ClassDetailsPanel, etc.)
    │   │   │   └── theme/                # UITheme design tokens (CU Red #E30613, Navy #1A2B4C)
    │   │   └── util/                     # Utilities (TimeUtils, SubjectRegistry, ImageUtils)
    │   └── resources/
    │       ├── data/timetable.json       # Packaged offline fallback timetable dataset
    │       └── images/                   # CU official logo & campus background watermark
    └── test/java/com/cudailyschedule/    # 8 automated JUnit 5 test classes (18 tests)
```

---

## 10. Summary Verification Commands

To perform a complete verification cycle from scratch on Windows:

```powershell
# 1. Navigate to Java project root
cd "c:\Workspace\Active projects\cu_daily_schedule _java\cu-daily-schedule-java"

# 2. Run all unit tests
mvn clean test

# 3. Build executable fat JAR
mvn clean package

# 4. Run the desktop application
java -jar target/cu-daily-schedule-java-1.0.0.jar
```
