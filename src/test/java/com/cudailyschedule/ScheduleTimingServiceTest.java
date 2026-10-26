package com.cudailyschedule;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.service.ScheduleTimingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTimingServiceTest {

    private final ScheduleTimingService service = new ScheduleTimingService();

    @Test
    @DisplayName("calculateScheduleStatus should detect ongoing active class")
    public void testActiveClassDetection() {
        List<ScheduleEntry> entries = new ArrayList<>();
        UUID tid = UUID.randomUUID();
        entries.add(new ScheduleEntry(UUID.randomUUID(), tid, "Monday", "Algorithms", "CS-101", "09:30 AM", "10:20 AM", "SEC-A", "Lecture", "All", "B1", "401", "A"));
        entries.add(new ScheduleEntry(UUID.randomUUID(), tid, "Monday", "Database", "CS-102", "10:30 AM", "11:20 AM", "SEC-A", "Lecture", "All", "B1", "402", "A"));

        // Test time: 09:45 AM (during first class)
        LocalTime testTime = LocalTime.of(9, 45);
        ScheduleStatus status = service.calculateScheduleStatus(entries, testTime);

        assertEquals(ScheduleStatus.StatusType.ONGOING, status.getStatus());
        assertFalse(status.isFinished());
        assertNotNull(status.getCurrentClass());
        assertEquals("Algorithms", status.getCurrentClass().getCourseName());
        assertNotNull(status.getNextClass());
        assertEquals("Database", status.getNextClass().getCourseName());
        // Remaining from 09:45 to 10:20 is 35 minutes = 2100 seconds
        assertEquals(2100, status.getRemainingSeconds());
    }

    @Test
    @DisplayName("calculateScheduleStatus should detect upcoming next class")
    public void testUpcomingClassDetection() {
        List<ScheduleEntry> entries = new ArrayList<>();
        UUID tid = UUID.randomUUID();
        entries.add(new ScheduleEntry(UUID.randomUUID(), tid, "Monday", "Algorithms", "CS-101", "10:00 AM", "11:00 AM", "SEC-A", "Lecture", "All", "B1", "401", "A"));

        // Test time: 09:30 AM (before first class)
        LocalTime testTime = LocalTime.of(9, 30);
        ScheduleStatus status = service.calculateScheduleStatus(entries, testTime);

        assertEquals(ScheduleStatus.StatusType.BEFORE, status.getStatus());
        assertFalse(status.isFinished());
        assertNull(status.getCurrentClass());
        assertNotNull(status.getNextClass());
        assertEquals("Algorithms", status.getNextClass().getCourseName());
        // Remaining from 09:30 to 10:00 is 30 minutes = 1800 seconds
        assertEquals(1800, status.getRemainingSeconds());
    }

    @Test
    @DisplayName("calculateScheduleStatus should return finished when all classes ended")
    public void testFinishedClasses() {
        List<ScheduleEntry> entries = new ArrayList<>();
        UUID tid = UUID.randomUUID();
        entries.add(new ScheduleEntry(UUID.randomUUID(), tid, "Monday", "Algorithms", "CS-101", "09:00 AM", "10:00 AM", "SEC-A", "Lecture", "All", "B1", "401", "A"));

        // Test time: 11:00 AM (after all classes)
        LocalTime testTime = LocalTime.of(11, 0);
        ScheduleStatus status = service.calculateScheduleStatus(entries, testTime);

        assertTrue(status.isFinished());
        assertEquals(ScheduleStatus.StatusType.COMPLETED, status.getStatus());
        assertNull(status.getCurrentClass());
        assertNull(status.getNextClass());
    }
}
