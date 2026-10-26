package com.cudailyschedule.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model representing a class session in the student schedule format.
 * Matches timetable.json and maps to schedule_entries in PostgreSQL.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassSession {
    private String day;
    private String startTime;
    private String endTime;
    private String subject;
    private String faculty;
    private String room;

    public ClassSession() {}

    public ClassSession(String day, String startTime, String endTime, String subject, String faculty, String room) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.faculty = faculty;
        this.room = room;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public boolean isBreak() {
        return subject != null && subject.toLowerCase().contains("break");
    }

    @Override
    public String toString() {
        return "ClassSession{" +
                "day='" + day + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", room='" + room + '\'' +
                '}';
    }
}
