package com.cudailyschedule.dao;

import com.cudailyschedule.config.DBConnection;
import com.cudailyschedule.model.Student;

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
 * Data Access Object for 'students' table.
 */
public class StudentDAO {

    public Optional<Student> findByStudentUid(String studentUid) throws SQLException {
        if (studentUid == null) return Optional.empty();
        String sql = "SELECT id, student_uid, student_name, timetable_id, created_at, updated_at " +
                     "FROM students WHERE UPPER(student_uid) = UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentUid.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, student_uid, student_name, timetable_id, created_at, updated_at " +
                     "FROM students ORDER BY student_name ASC";
        List<Student> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        }
        return list;
    }

    public Student save(Student student) throws SQLException {
        if (student.getId() == null) {
            student.setId(UUID.randomUUID());
        }
        String sql = "INSERT INTO students (id, student_uid, student_name, timetable_id) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT (student_uid) DO UPDATE SET " +
                     "student_name = EXCLUDED.student_name, " +
                     "timetable_id = EXCLUDED.timetable_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, student.getId());
            stmt.setString(2, student.getStudentUid());
            stmt.setString(3, student.getStudentName());
            stmt.setObject(4, student.getTimetableId());
            stmt.executeUpdate();
        }
        return student;
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId((UUID) rs.getObject("id"));
        s.setStudentUid(rs.getString("student_uid"));
        s.setStudentName(rs.getString("student_name"));
        s.setTimetableId((UUID) rs.getObject("timetable_id"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            s.setCreatedAt(created.toInstant().atOffset(ZoneOffset.UTC));
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            s.setUpdatedAt(updated.toInstant().atOffset(ZoneOffset.UTC));
        }
        return s;
    }
}
