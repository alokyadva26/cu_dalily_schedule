package com.cudailyschedule.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Model representing the 'students' table.
 */
public class Student {
    private UUID id;
    private String studentUid;
    private String studentName;
    private UUID timetableId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Student() {}

    public Student(UUID id, String studentUid, String studentName, UUID timetableId) {
        this.id = id;
        this.studentUid = studentUid;
        this.studentName = studentName;
        this.timetableId = timetableId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public UUID getTimetableId() { return timetableId; }
    public void setTimetableId(UUID timetableId) { this.timetableId = timetableId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Student{" +
                "studentUid='" + studentUid + '\'' +
                ", studentName='" + studentName + '\'' +
                ", timetableId=" + timetableId +
                '}';
    }
}
