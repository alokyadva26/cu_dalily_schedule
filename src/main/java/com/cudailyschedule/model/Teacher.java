package com.cudailyschedule.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Model representing the 'teachers' table.
 */
public class Teacher {
    private UUID id;
    private String employeeId;
    private String employeeName;
    private String department;
    private UUID timetableId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Teacher() {}

    public Teacher(UUID id, String employeeId, String employeeName, String department, UUID timetableId) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.timetableId = timetableId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public UUID getTimetableId() { return timetableId; }
    public void setTimetableId(UUID timetableId) { this.timetableId = timetableId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Teacher{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", timetableId=" + timetableId +
                '}';
    }
}
