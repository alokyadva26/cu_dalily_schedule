package com.cudailyschedule.dao;

import com.cudailyschedule.config.DBConnection;
import com.cudailyschedule.model.Timetable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data Access Object for 'timetables' table.
 */
public class TimetableDAO {

    public Optional<Timetable> findById(UUID id) throws SQLException {
        String sql = "SELECT id, timetable_name, timetable_type, created_at, updated_at FROM timetables WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTimetable(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Timetable> findAll() throws SQLException {
        String sql = "SELECT id, timetable_name, timetable_type, created_at, updated_at FROM timetables ORDER BY timetable_name ASC";
        List<Timetable> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTimetable(rs));
            }
        }
        return list;
    }

    public List<Timetable> findByType(String type) throws SQLException {
        String sql = "SELECT id, timetable_name, timetable_type, created_at, updated_at FROM timetables WHERE timetable_type = ? ORDER BY timetable_name ASC";
        List<Timetable> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTimetable(rs));
                }
            }
        }
        return list;
    }

    public Timetable save(Timetable timetable) throws SQLException {
        if (timetable.getId() == null) {
            timetable.setId(UUID.randomUUID());
        }
        String sql = "INSERT INTO timetables (id, timetable_name, timetable_type) VALUES (?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET timetable_name = EXCLUDED.timetable_name, timetable_type = EXCLUDED.timetable_type";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, timetable.getId());
            stmt.setString(2, timetable.getTimetableName());
            stmt.setString(3, timetable.getTimetableType());
            stmt.executeUpdate();
        }
        return timetable;
    }

    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM timetables WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Timetable mapResultSetToTimetable(ResultSet rs) throws SQLException {
        Timetable t = new Timetable();
        t.setId((UUID) rs.getObject("id"));
        t.setTimetableName(rs.getString("timetable_name"));
        t.setTimetableType(rs.getString("timetable_type"));
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
