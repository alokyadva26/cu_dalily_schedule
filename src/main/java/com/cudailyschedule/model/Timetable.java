package com.cudailyschedule.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Model representing the 'timetables' table.
 */
public class Timetable {
    private UUID id;
    private String timetableName;
    private String timetableType; // 'student' or 'teacher'
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Timetable() {}

    public Timetable(UUID id, String timetableName, String timetableType) {
        this.id = id;
        this.timetableName = timetableName;
        this.timetableType = timetableType;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTimetableName() { return timetableName; }
    public void setTimetableName(String timetableName) { this.timetableName = timetableName; }

    public String getTimetableType() { return timetableType; }
    public void setTimetableType(String timetableType) { this.timetableType = timetableType; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Timetable{" +
                "id=" + id +
                ", timetableName='" + timetableName + '\'' +
                ", timetableType='" + timetableType + '\'' +
                '}';
    }
}
