package com.cudailyschedule.service;

import com.cudailyschedule.dao.ScheduleEntryDAO;
import com.cudailyschedule.dao.TeacherDAO;
import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.Teacher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TeacherService handles teacher search and schedule retrieval.
 * Uses JDBC DAOs when database is online, with built-in seed fallback when offline.
 */
public class TeacherService {
    private static final Logger LOGGER = Logger.getLogger(TeacherService.class.getName());

    private final TeacherDAO teacherDAO;
    private final ScheduleEntryDAO scheduleEntryDAO;

    // Built-in seed data fallback for offline / evaluation mode
    private final Map<String, Teacher> seedTeachers = new HashMap<>();
    private final Map<UUID, List<ScheduleEntry>> seedSchedules = new HashMap<>();

    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
        this.scheduleEntryDAO = new ScheduleEntryDAO();
        initSeedData();
    }

    public TeacherService(TeacherDAO teacherDAO, ScheduleEntryDAO scheduleEntryDAO) {
        this.teacherDAO = teacherDAO;
        this.scheduleEntryDAO = scheduleEntryDAO;
        initSeedData();
    }

    private void initSeedData() {
        // Teacher 1: Neeraj
        UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Teacher neeraj = new Teacher(UUID.randomUUID(), "E19761", "Neeraj", "CSE", id1);
        seedTeachers.put("E19761", neeraj);

        List<ScheduleEntry> neerajEntries = new ArrayList<>();
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Tuesday", "Logical Thinking and Problem Solving", "26CSH-101", "09:30 AM", "10:20 AM", "26ADS-513", "Tutorial", "B", "Block-C3", "613", "A"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Tuesday", "Logical Thinking and Problem Solving", "26CSH-101", "01:40 PM", "02:30 PM", "26AML-505", "Tutorial", "B", "Block-C3", "701", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Wednesday", "Logical Thinking and Problem Solving", "26CSH-101", "11:10 AM", "12:00 PM", "26ADS-513", "Practical", "B", "Block-C3", "604", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Wednesday", "Logical Thinking and Problem Solving", "26CSH-101", "12:00 PM", "12:50 PM", "26ADS-513", "Practical", "B", "Block-C3", "604", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Wednesday", "Logical Thinking and Problem Solving", "26CSH-101", "03:20 PM", "04:10 PM", "26ADS-515", "Tutorial", "B", "Block-C3", "702", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Thursday", "Logical Thinking and Problem Solving", "26CSH-101", "11:10 AM", "12:00 PM", "26ADS-515", "Tutorial", "B", "Block-C3", "613", "A"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Thursday", "Logical Thinking and Problem Solving", "26CSH-101", "01:40 PM", "02:30 PM", "26ADS-513", "Tutorial", "B", "Block-C3", "709", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Thursday", "Logical Thinking and Problem Solving", "26CSH-101", "02:30 PM", "03:20 PM", "26AML-505", "Tutorial", "B", "Block-C3", "611", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Saturday", "Logical Thinking and Problem Solving", "26CSH-101", "11:10 AM", "12:00 PM", "26ADS-513", "Practical", "B", "Block-C3", "710", "empty"));
        neerajEntries.add(new ScheduleEntry(UUID.randomUUID(), id1, "Saturday", "Logical Thinking and Problem Solving", "26CSH-101", "12:00 PM", "12:50 PM", "26ADS-513", "Practical", "B", "Block-C3", "710", "empty"));
        seedSchedules.put(id1, neerajEntries);

        // Teacher 2: Gitanjali
        UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        Teacher gitanjali = new Teacher(UUID.randomUUID(), "E16525", "Gitanjali", "CSE", id2);
        seedTeachers.put("E16525", gitanjali);

        List<ScheduleEntry> gitanjaliEntries = new ArrayList<>();
        gitanjaliEntries.add(new ScheduleEntry(UUID.randomUUID(), id2, "Monday", "Database Management System", "25CSH-204", "10:20 AM", "11:10 AM", "25BCS-615", "Lecture", "All", "Block-B1", "511", "empty"));
        gitanjaliEntries.add(new ScheduleEntry(UUID.randomUUID(), id2, "Monday", "Competitive Coding-II", "24CSP-305", "11:10 AM", "12:00 PM", "24BCS_TPP-610", "Practical", "A", "Block-B1", "407", "empty"));
        gitanjaliEntries.add(new ScheduleEntry(UUID.randomUUID(), id2, "Monday", "Competitive Coding-II", "24CSP-305", "12:00 PM", "12:50 PM", "24BCS_TPP-610", "Practical", "A", "Block-B1", "407", "empty"));
        gitanjaliEntries.add(new ScheduleEntry(UUID.randomUUID(), id2, "Tuesday", "Competitive Coding-II", "24CSP-305", "09:30 AM", "10:20 AM", "24BCS_TPP-610", "Practical", "A", "Block-B1", "402", "empty"));
        gitanjaliEntries.add(new ScheduleEntry(UUID.randomUUID(), id2, "Tuesday", "Competitive Coding-II", "24CSP-305", "10:20 AM", "11:10 AM", "24BCS_TPP-610", "Practical", "A", "Block-B1", "402", "empty"));
        seedSchedules.put(id2, gitanjaliEntries);

        // Teacher 3: Jaspreet
        UUID id3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
        Teacher jaspreet = new Teacher(UUID.randomUUID(), "E10279", "Jaspreet Singh Baith", "CSE", id3);
        seedTeachers.put("E10279", jaspreet);

        List<ScheduleEntry> jaspreetEntries = new ArrayList<>();
        jaspreetEntries.add(new ScheduleEntry(UUID.randomUUID(), id3, "Tuesday", "Computer Networks", "24CST-302", "11:10 AM", "12:00 PM", "24BCS_TPP-615", "Lecture", "All", "Block-B1", "501", "empty"));
        jaspreetEntries.add(new ScheduleEntry(UUID.randomUUID(), id3, "Wednesday", "Computer Networks", "24CST-302", "11:10 AM", "12:00 PM", "24BCS_TPP-615", "Lecture", "All", "Block-B1", "501", "empty"));
        jaspreetEntries.add(new ScheduleEntry(UUID.randomUUID(), id3, "Thursday", "Computer Networks", "24CST-302", "11:10 AM", "12:00 PM", "24BCS_TPP-615", "Lecture", "All", "Block-B1", "501", "empty"));
        seedSchedules.put(id3, jaspreetEntries);
    }

    /**
     * Finds a teacher by employee ID.
     */
    public Optional<Teacher> findTeacher(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return Optional.empty();
        }
        String cleanId = employeeId.trim().toUpperCase();

        try {
            Optional<Teacher> dbTeacher = teacherDAO.findByEmployeeId(cleanId);
            if (dbTeacher.isPresent()) {
                return dbTeacher;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database query failed for teacher " + cleanId + ", using seed fallback: " + e.getMessage());
        }

        // Fallback to seed data
        return Optional.ofNullable(seedTeachers.get(cleanId));
    }

    /**
     * Retrieves all schedule entries for a given teacher.
     */
    public List<ScheduleEntry> getTeacherSchedule(Teacher teacher) {
        if (teacher == null || teacher.getTimetableId() == null) {
            return new ArrayList<>();
        }

        try {
            List<ScheduleEntry> dbEntries = scheduleEntryDAO.findByTimetableId(teacher.getTimetableId());
            if (dbEntries != null && !dbEntries.isEmpty()) {
                return dbEntries;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database query failed for schedule of " + teacher.getEmployeeId() + ", using seed fallback: " + e.getMessage());
        }

        // Fallback to seed data
        List<ScheduleEntry> fallback = seedSchedules.get(teacher.getTimetableId());
        return fallback != null ? fallback : new ArrayList<>();
    }

    /**
     * Returns list of sample teachers for quick selection in the UI.
     */
    public List<Teacher> getSampleTeachers() {
        return new ArrayList<>(seedTeachers.values());
    }
}
