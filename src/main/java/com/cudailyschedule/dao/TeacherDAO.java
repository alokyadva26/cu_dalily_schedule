package com.cudailyschedule.dao;

import com.cudailyschedule.config.DBConnection;
import com.cudailyschedule.model.Teacher;

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
 * Data Access Object for 'teachers' table.
 */
public class TeacherDAO {

    public Optional<Teacher> findByEmployeeId(String employeeId) throws SQLException {
        if (employeeId == null) return Optional.empty();
        String sql = "SELECT id, employee_id, employee_name, department, timetable_id, created_at, updated_at " +
                     "FROM teachers WHERE UPPER(employee_id) = UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Teacher> findAll() throws SQLException {
        String sql = "SELECT id, employee_id, employee_name, department, timetable_id, created_at, updated_at " +
                     "FROM teachers ORDER BY employee_name ASC";
        List<Teacher> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTeacher(rs));
            }
        }
        return list;
    }

    public Teacher save(Teacher teacher) throws SQLException {
        if (teacher.getId() == null) {
            teacher.setId(UUID.randomUUID());
        }
        String sql = "INSERT INTO teachers (id, employee_id, employee_name, department, timetable_id) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (employee_id) DO UPDATE SET " +
                     "employee_name = EXCLUDED.employee_name, " +
                     "department = EXCLUDED.department, " +
                     "timetable_id = EXCLUDED.timetable_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, teacher.getId());
            stmt.setString(2, teacher.getEmployeeId());
            stmt.setString(3, teacher.getEmployeeName());
            stmt.setString(4, teacher.getDepartment());
            stmt.setObject(5, teacher.getTimetableId());
            stmt.executeUpdate();
        }
        return teacher;
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM teachers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId((UUID) rs.getObject("id"));
        t.setEmployeeId(rs.getString("employee_id"));
        t.setEmployeeName(rs.getString("employee_name"));
        t.setDepartment(rs.getString("department"));
        t.setTimetableId((UUID) rs.getObject("timetable_id"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            t.setCreatedAt(created.toInstant().atOffset(ZoneOffset.UTC));
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            t.setUpdatedAt(updated.toInstant().atOffset(ZoneOffset.UTC));
        }
        return t;
    }
}
