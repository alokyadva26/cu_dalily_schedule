package com.cudailyschedule;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.service.AdminScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AdminScheduleServiceTest {

    private final AdminScheduleService service = new AdminScheduleService();

    @Test
    @DisplayName("Validation should reject schedule entries with missing required fields")
    public void testValidationFailure() {
        ScheduleEntry missingTimetable = new ScheduleEntry();
        assertThrows(IllegalArgumentException.class, () -> service.createScheduleEntry(missingTimetable));

        ScheduleEntry missingDay = new ScheduleEntry();
        missingDay.setTimetableId(UUID.randomUUID());
        missingDay.setDayOfWeek("");
        assertThrows(IllegalArgumentException.class, () -> service.createScheduleEntry(missingDay));

        ScheduleEntry missingTime = new ScheduleEntry();
        missingTime.setTimetableId(UUID.randomUUID());
        missingTime.setDayOfWeek("Monday");
        missingTime.setStartTime("");
        assertThrows(IllegalArgumentException.class, () -> service.createScheduleEntry(missingTime));
    }
}
