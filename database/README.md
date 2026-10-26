# Supabase PostgreSQL Database Setup & Schema Guide

This directory documents the database schema, security model, and seed data for the **CU Daily Schedule** application.

## 1. Schema Architecture

The database runs on **PostgreSQL** hosted on **Supabase**. The schema contains four primary tables:

1. `timetables`: Parent timetable records for both teachers and student groups.
2. `teachers`: Faculty records linked to a timetable.
3. `students`: Student cohort records linked to a timetable.
4. `schedule_entries`: Individual class / lab / tutorial slots belonging to a timetable.

## 2. DDL SQL (Tables & Triggers)

The schema definitions are located in:
`cu-daily-schedule/supabase/schema.sql`

```sql
-- Create updated_at trigger function
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Timetables Table
CREATE TABLE timetables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timetable_name TEXT NOT NULL,
    timetable_type TEXT NOT NULL CHECK (timetable_type IN ('student', 'teacher')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER update_timetables_modtime
BEFORE UPDATE ON timetables FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

-- Teachers Table
CREATE TABLE teachers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id TEXT NOT NULL UNIQUE,
    employee_name TEXT NOT NULL,
    department TEXT,
    timetable_id UUID REFERENCES timetables(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_teachers_employee_id ON teachers(employee_id);
CREATE INDEX idx_teachers_timetable_id ON teachers(timetable_id);

CREATE TRIGGER update_teachers_modtime
BEFORE UPDATE ON teachers FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

-- Students Table
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_uid TEXT NOT NULL UNIQUE,
    student_name TEXT NOT NULL,
    timetable_id UUID REFERENCES timetables(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_students_student_uid ON students(student_uid);
CREATE INDEX idx_students_timetable_id ON students(timetable_id);

CREATE TRIGGER update_students_modtime
BEFORE UPDATE ON students FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

-- Schedule Entries Table
CREATE TABLE schedule_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timetable_id UUID NOT NULL REFERENCES timetables(id) ON DELETE CASCADE,
    day_of_week TEXT NOT NULL,
    course_name TEXT,
    course_code TEXT,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    section TEXT,
    course_type TEXT,
    student_group TEXT,
    block_no TEXT,
    room_no TEXT,
    partition TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_schedule_entries_timetable_id ON schedule_entries(timetable_id);
CREATE INDEX idx_schedule_entries_day_of_week ON schedule_entries(day_of_week);

CREATE TRIGGER update_schedule_entries_modtime
BEFORE UPDATE ON schedule_entries FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
```

## 3. Row Level Security Policies

Policies are defined in `cu-daily-schedule/supabase/policies.sql`:

```sql
ALTER TABLE timetables ENABLE ROW LEVEL SECURITY;
ALTER TABLE teachers ENABLE ROW LEVEL SECURITY;
ALTER TABLE students ENABLE ROW LEVEL SECURITY;
ALTER TABLE schedule_entries ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow public read access on timetables" ON timetables FOR SELECT USING (true);
CREATE POLICY "Allow public read access on teachers" ON teachers FOR SELECT USING (true);
CREATE POLICY "Allow public read access on students" ON students FOR SELECT USING (true);
CREATE POLICY "Allow public read access on schedule_entries" ON schedule_entries FOR SELECT USING (true);
```

## 4. Seed Data Verification

The database includes seed records for:
- Faculty `E19761` (Neeraj)
- Faculty `E16525` (Gitanjali)
- Faculty `E10279` (Jaspreet Singh Baith)
- Associated schedule entries spanning Monday through Saturday across various blocks and lecture rooms.
