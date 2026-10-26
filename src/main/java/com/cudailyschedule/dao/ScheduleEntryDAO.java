package com.cudailyschedule.dao;

import com.cudailyschedule.config.DBConnection;
import com.cudailyschedule.model.ScheduleEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data Access Object for 'schedule_entries' table.
 */
public class ScheduleEntryDAO {

    public List<ScheduleEntry> findByTimetableId(UUID timetableId) throws SQLException {
        String sql = "SELECT id, timetable_id, day_of_week, course_name, course_code, start_time, end_time, " +
                     "section, course_type, student_group, block_no, room_no, partition, created_at, updated_at " +
                     "FROM schedule_entries WHERE timetable_id = ?";
        List<ScheduleEntry> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, timetableId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntry(rs));
                }
            }
        }
        return list;
    }

    public List<ScheduleEntry> findByTimetableIdAndDay(UUID timetableId, String dayOfWeek) throws SQLException {
        String sql = "SELECT id, timetable_id, day_of_week, course_name, course_code, start_time, end_time, " +
                     "section, course_type, student_group, block_no, room_no, partition, created_at, updated_at " +
                     "FROM schedule_entries WHERE timetable_id = ? AND LOWER(day_of_week) = LOWER(?)";
        List<ScheduleEntry> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, timetableId);
            stmt.setString(2, dayOfWeek.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntry(rs));
                }
            }
        }
        return list;
    }

    public Optional<ScheduleEntry> findById(UUID id) throws SQLException {
        String sql = "SELECT id, timetable_id, day_of_week, course_name, course_code, start_time, end_time, " +
                     "section, course_type, student_group, block_no, room_no, partition, created_at, updated_at " +
                     "FROM schedule_entries WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntry(rs));
                }
            }
        }
        return Optional.empty();
    }

    public ScheduleEntry save(ScheduleEntry entry) throws SQLException {
        if (entry.getId() == null) {
            entry.setId(UUID.randomUUID());
        }
        String sql = "INSERT INTO schedule_entries " +
                     "(id, timetable_id, day_of_week, course_name, course_code, start_time, end_time, " +
                     "section, course_type, student_group, block_no, room_no, partition) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "day_of_week = EXCLUDED.day_of_week, " +
                     "course_name = EXCLUDED.course_name, " +
                     "course_code = EXCLUDED.course_code, " +
                     "start_time = EXCLUDED.start_time, " +
                     "end_time = EXCLUDED.end_time, " +
                     "section = EXCLUDED.section, " +
                     "course_type = EXCLUDED.course_type, " +
                     "student_group = EXCLUDED.student_group, " +
                     "block_no = EXCLUDED.block_no, " +
                     "room_no = EXCLUDED.room_no, " +
                     "partition = EXCLUDED.partition";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, entry.getId());
            stmt.setObject(2, entry.getTimetableId());
            stmt.setString(3, entry.getDayOfWeek());
            stmt.setString(4, entry.getCourseName());
            stmt.setString(5, entry.getCourseCode());
            stmt.setString(6, entry.getStartTime());
            stmt.setString(7, entry.getEndTime());
            stmt.setString(8, entry.getSection());
            stmt.setString(9, entry.getCourseType());
            stmt.setString(10, entry.getStudentGroup());
            stmt.setString(11, entry.getBlockNo());
            stmt.setString(12, entry.getRoomNo());
            stmt.setString(13, entry.getPartition());
            stmt.executeUpdate();
        }
        return entry;
    }

    /**
     * Executes a batch insert in a single database transaction.
     */
    public void batchInsert(List<ScheduleEntry> entries) throws SQLException {
        String sql = "INSERT INTO schedule_entries " +
                     "(id, timetable_id, day_of_week, course_name, course_code, start_time, end_time, " +
                     "section, course_type, student_group, block_no, room_no, partition) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (ScheduleEntry entry : entries) {
                        if (entry.getId() == null) {
                            entry.setId(UUID.randomUUID());
                        }
                        stmt.setObject(1, entry.getId());
                        stmt.setObject(2, entry.getTimetableId());
                        stmt.setString(3, entry.getDayOfWeek());
                        stmt.setString(4, entry.getCourseName());
                        stmt.setString(5, entry.getCourseCode());
                        stmt.setString(6, entry.getStartTime());
                        stmt.setString(7, entry.getEndTime());
                        stmt.setString(8, entry.getSection());
                        stmt.setString(9, entry.getCourseType());
                        stmt.setString(10, entry.getStudentGroup());
                        stmt.setString(11, entry.getBlockNo());
                        stmt.setString(12, entry.getRoomNo());
                        stmt.setString(13, entry.getPartition());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM schedule_entries WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public int deleteByTimetableId(UUID timetableId) throws SQLException {
        String sql = "DELETE FROM schedule_entries WHERE timetable_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, timetableId);
            return stmt.executeUpdate();
        }
    }

    private ScheduleEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        ScheduleEntry e = new ScheduleEntry();
        e.setId((UUID) rs.getObject("id"));
        e.setTimetableId((UUID) rs.getObject("timetable_id"));
        e.setDayOfWeek(rs.getString("day_of_week"));
        e.setCourseName(rs.getString("course_name"));
        e.setCourseCode(rs.getString("course_code"));
        e.setStartTime(rs.getString("start_time"));
        e.setEndTime(rs.getString("end_time"));
        e.setSection(rs.getString("section"));
        e.setCourseType(rs.getString("course_type"));
        e.setStudentGroup(rs.getString("student_group"));
        e.setBlockNo(rs.getString("block_no"));
        e.setRoomNo(rs.getString("room_no"));
        e.setPartition(rs.getString("partition"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            e.setCreatedAt(created.toInstant().atOffset(ZoneOffset.UTC));
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            e.setUpdatedAt(updated.toInstant().atOffset(ZoneOffset.UTC));
        }
        return e;
    }
}
