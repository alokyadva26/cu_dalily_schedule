package com.cudailyschedule.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Model representing the 'schedule_entries' table.
 */
public class ScheduleEntry {
    private UUID id;
    private UUID timetableId;
    private String dayOfWeek;
    private String courseName;
    private String courseCode;
    private String startTime;
    private String endTime;
    private String section;
    private String courseType;
    private String studentGroup;
    private String blockNo;
    private String roomNo;
    private String partition;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ScheduleEntry() {}

    public ScheduleEntry(UUID id, UUID timetableId, String dayOfWeek, String courseName, String courseCode,
                         String startTime, String endTime, String section, String courseType,
                         String studentGroup, String blockNo, String roomNo, String partition) {
        this.id = id;
        this.timetableId = timetableId;
        this.dayOfWeek = dayOfWeek;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.section = section;
        this.courseType = courseType;
        this.studentGroup = studentGroup;
        this.blockNo = blockNo;
        this.roomNo = roomNo;
        this.partition = partition;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTimetableId() { return timetableId; }
    public void setTimetableId(UUID timetableId) { this.timetableId = timetableId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public String getStudentGroup() { return studentGroup; }
    public void setStudentGroup(String studentGroup) { this.studentGroup = studentGroup; }

    public String getBlockNo() { return blockNo; }
    public void setBlockNo(String blockNo) { this.blockNo = blockNo; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getPartition() { return partition; }
    public void setPartition(String partition) { this.partition = partition; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Formats classroom location cleanly (e.g. "Block-B1 / 407").
     */
    public String getFormattedRoom() {
        String block = (blockNo != null && !blockNo.isEmpty()) ? blockNo : "";
        if (!block.startsWith("Block") && !block.isEmpty()) {
            block = "Block-" + block;
        }
        String room = (roomNo != null) ? roomNo : "";
        if (!block.isEmpty() && !room.isEmpty()) {
            return block + " / " + room;
        }
        return block.isEmpty() ? room : block;
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", courseName='" + courseName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", roomNo='" + roomNo + '\'' +
                '}';
    }
}
