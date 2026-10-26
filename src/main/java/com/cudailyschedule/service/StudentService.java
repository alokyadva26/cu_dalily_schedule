package com.cudailyschedule.service;

import com.cudailyschedule.dao.ScheduleEntryDAO;
import com.cudailyschedule.dao.TimetableDAO;
import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.Timetable;
import com.cudailyschedule.util.SubjectRegistry;
import com.cudailyschedule.util.TimeUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * StudentService manages student schedule data retrieval.
 * Connects to PostgreSQL database with seamless fallback to timetable.json.
 */
public class StudentService {
    private static final Logger LOGGER = Logger.getLogger(StudentService.class.getName());

    private final TimetableDAO timetableDAO;
    private final ScheduleEntryDAO scheduleEntryDAO;
    private final List<ClassSession> bundledSessions = new ArrayList<>();

    public StudentService() {
        this.timetableDAO = new TimetableDAO();
        this.scheduleEntryDAO = new ScheduleEntryDAO();
        loadBundledTimetable();
    }

    public StudentService(TimetableDAO timetableDAO, ScheduleEntryDAO scheduleEntryDAO) {
        this.timetableDAO = timetableDAO;
        this.scheduleEntryDAO = scheduleEntryDAO;
        loadBundledTimetable();
    }

    private void loadBundledTimetable() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 1. Try classpath resource
            InputStream is = getClass().getResourceAsStream("/data/timetable.json");
            if (is != null) {
                List<ClassSession> list = mapper.readValue(is, new TypeReference<List<ClassSession>>() {});
                bundledSessions.addAll(list);
                return;
            }

            // 2. Try file system
            File file = new File("src/main/resources/data/timetable.json");
            if (file.exists()) {
                List<ClassSession> list = mapper.readValue(file, new TypeReference<List<ClassSession>>() {});
                bundledSessions.addAll(list);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load bundled timetable.json: " + e.getMessage());
        }
    }

    /**
     * Gets classes for a specific day of week (e.g. "Monday", "Tuesday").
     */
    public List<ClassSession> getClassesForDay(String dayName) {
        if (dayName == null) return new ArrayList<>();

        // First attempt to load from database if student timetable exists
        try {
            List<Timetable> studentTimetables = timetableDAO.findByType("student");
            if (!studentTimetables.isEmpty()) {
                List<ScheduleEntry> entries = scheduleEntryDAO.findByTimetableIdAndDay(
                        studentTimetables.get(0).getId(), dayName
                );
                if (entries != null && !entries.isEmpty()) {
                    return entries.stream().map(this::convertEntryToSession).collect(Collectors.toList());
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.FINE, "Could not fetch student timetable from DB, using bundled data: " + e.getMessage());
        }

        // Fallback to bundled timetable.json
        List<ClassSession> filtered = bundledSessions.stream()
                .filter(session -> dayName.equalsIgnoreCase(session.getDay()))
                .sorted(Comparator.comparing(s -> TimeUtils.parseTime(s.getStartTime())))
                .collect(Collectors.toList());

        return filtered;
    }

    /**
     * Retrieves today's classes based on Asia/Kolkata timezone.
     */
    public List<ClassSession> getTodayClasses() {
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        String dayName = now.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return getClassesForDay(dayName);
    }

    /**
     * Retrieves tomorrow's classes based on Asia/Kolkata timezone.
     */
    public List<ClassSession> getTomorrowClasses() {
        ZonedDateTime tomorrow = TimeUtils.getNowInKolkata().plusDays(1);
        String dayName = tomorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return getClassesForDay(dayName);
    }

    public List<ClassSession> getAllBundledSessions() {
        return new ArrayList<>(bundledSessions);
    }

    private ClassSession convertEntryToSession(ScheduleEntry entry) {
        String subject = entry.getCourseName();
        if (subject == null || subject.isEmpty()) {
            subject = entry.getCourseCode();
        }
        return new ClassSession(
                entry.getDayOfWeek(),
                entry.getStartTime(),
                entry.getEndTime(),
                subject,
                "Faculty",
                entry.getFormattedRoom()
        );
    }
}
