package com.cudailyschedule;

import com.cudailyschedule.model.ClassSession;
import com.cudailyschedule.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {

    private final StudentService service = new StudentService();

    @Test
    @DisplayName("StudentService should load bundled timetable.json dataset")
    public void testBundledSessionsLoaded() {
        List<ClassSession> all = service.getAllBundledSessions();
        assertNotNull(all, "Bundled sessions should not be null");
        assertFalse(all.isEmpty(), "Bundled sessions should contain records");
        assertTrue(all.size() >= 25, "Should have full week student schedule entries");
    }

    @Test
    @DisplayName("getClassesForDay should filter sessions by day of week")
    public void testGetClassesForDay() {
        List<ClassSession> mondayClasses = service.getClassesForDay("Monday");
        assertNotNull(mondayClasses);
        assertFalse(mondayClasses.isEmpty());

        for (ClassSession s : mondayClasses) {
            assertEquals("Monday", s.getDay());
        }

        List<ClassSession> tuesdayClasses = service.getClassesForDay("Tuesday");
        assertNotNull(tuesdayClasses);
        assertFalse(tuesdayClasses.isEmpty());

        for (ClassSession s : tuesdayClasses) {
            assertEquals("Tuesday", s.getDay());
        }
    }
}
