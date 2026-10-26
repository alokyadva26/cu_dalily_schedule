package com.cudailyschedule;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.Teacher;
import com.cudailyschedule.service.TeacherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherServiceTest {

    private final TeacherService teacherService = new TeacherService();

    @Test
    @DisplayName("TeacherService should resolve seed teachers by employee ID case-insensitively")
    public void testFindTeacher() {
        Optional<Teacher> neeraj = teacherService.findTeacher("E19761");
        assertTrue(neeraj.isPresent());
        assertEquals("Neeraj", neeraj.get().getEmployeeName());

        Optional<Teacher> neerajLower = teacherService.findTeacher("e19761");
        assertTrue(neerajLower.isPresent());
        assertEquals("Neeraj", neerajLower.get().getEmployeeName());

        Optional<Teacher> gitanjali = teacherService.findTeacher("E16525");
        assertTrue(gitanjali.isPresent());
        assertEquals("Gitanjali", gitanjali.get().getEmployeeName());

        Optional<Teacher> invalid = teacherService.findTeacher("NONEXISTENT_ID");
        assertFalse(invalid.isPresent());
    }

    @Test
    @DisplayName("TeacherService should retrieve timetable entries for teacher")
    public void testGetTeacherSchedule() {
        Optional<Teacher> neeraj = teacherService.findTeacher("E19761");
        assertTrue(neeraj.isPresent());

        List<ScheduleEntry> entries = teacherService.getTeacherSchedule(neeraj.get());
        assertNotNull(entries);
        assertFalse(entries.isEmpty());
        assertEquals(10, entries.size(), "Neeraj should have 10 schedule entries from seed");
    }
}
