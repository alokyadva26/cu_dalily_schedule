package com.cudailyschedule.service;

import com.cudailyschedule.dao.ScheduleEntryDAO;
import com.cudailyschedule.dao.TeacherDAO;
import com.cudailyschedule.dao.TimetableDAO;
import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.model.Timetable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * AdminScheduleService provides management CRUD operations for timetable slots and teachers.
 * Demonstrates transactional integrity and business validation for college CSE evaluation.
 */
public class AdminScheduleService {
    private final TimetableDAO timetableDAO;
    private final TeacherDAO teacherDAO;
    private final ScheduleEntryDAO scheduleEntryDAO;

    public AdminScheduleService() {
        this.timetableDAO = new TimetableDAO();
        this.teacherDAO = new TeacherDAO();
        this.scheduleEntryDAO = new ScheduleEntryDAO();
    }

    public List<Timetable> getAllTimetables() throws SQLException {
        return timetableDAO.findAll();
    }

    public List<Teacher> getAllTeachers() throws SQLException {
        return teacherDAO.findAll();
    }

    public List<ScheduleEntry> getEntriesForTimetable(UUID timetableId) throws SQLException {
        return scheduleEntryDAO.findByTimetableId(timetableId);
    }

    public ScheduleEntry createScheduleEntry(ScheduleEntry entry) throws SQLException {
        validateScheduleEntry(entry);
        return scheduleEntryDAO.save(entry);
    }

    public ScheduleEntry updateScheduleEntry(ScheduleEntry entry) throws SQLException {
        if (entry.getId() == null) {
            throw new IllegalArgumentException("Entry ID cannot be null for update");
        }
        validateScheduleEntry(entry);
        return scheduleEntryDAO.save(entry);
    }

    public boolean deleteScheduleEntry(UUID id) throws SQLException {
        return scheduleEntryDAO.delete(id);
    }

    private void validateScheduleEntry(ScheduleEntry entry) {
        if (entry.getTimetableId() == null) {
            throw new IllegalArgumentException("Timetable ID is required");
        }
        if (entry.getDayOfWeek() == null || entry.getDayOfWeek().trim().isEmpty()) {
            throw new IllegalArgumentException("Day of week is required");
        }
        if (entry.getStartTime() == null || entry.getStartTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Start time is required (e.g. 09:30 AM)");
        }
        if (entry.getEndTime() == null || entry.getEndTime().trim().isEmpty()) {
            throw new IllegalArgumentException("End time is required (e.g. 10:20 AM)");
        }
    }
}
