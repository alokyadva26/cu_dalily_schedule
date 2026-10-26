package com.cudailyschedule;

import com.cudailyschedule.util.TimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TimeUtilsTest {

    @Test
    @DisplayName("parseTime should parse standard AM/PM time strings")
    public void testParseTime() {
        LocalTime morning = TimeUtils.parseTime("09:30 AM");
        assertEquals(9, morning.getHour());
        assertEquals(30, morning.getMinute());

        LocalTime afternoon = TimeUtils.parseTime("01:40 PM");
        assertEquals(13, afternoon.getHour());
        assertEquals(40, afternoon.getMinute());

        LocalTime noon = TimeUtils.parseTime("12:00 PM");
        assertEquals(12, noon.getHour());
        assertEquals(0, noon.getMinute());

        LocalTime midnight = TimeUtils.parseTime("12:00 AM");
        assertEquals(0, midnight.getHour());
        assertEquals(0, midnight.getMinute());
    }

    @Test
    @DisplayName("getNowInKolkata should return time anchored in Asia/Kolkata timezone")
    public void testKolkataTimezone() {
        ZonedDateTime now = TimeUtils.getNowInKolkata();
        assertNotNull(now);
        assertEquals("Asia/Kolkata", now.getZone().getId());
    }

    @Test
    @DisplayName("getCountdownString should produce correct hour and minute breakdown")
    public void testCountdownString() {
        LocalTime from = LocalTime.of(10, 0);
        LocalTime to1 = LocalTime.of(10, 45);
        assertEquals("45m", TimeUtils.getCountdownString(to1, from));

        LocalTime to2 = LocalTime.of(11, 30);
        assertEquals("1h 30m", TimeUtils.getCountdownString(to2, from));
    }

    @Test
    @DisplayName("formatRemainingSeconds should produce MM:SS format")
    public void testFormatRemainingSeconds() {
        assertEquals("05:30", TimeUtils.formatRemainingSeconds(330));
        assertEquals("00:45", TimeUtils.formatRemainingSeconds(45));
        assertEquals("00:00", TimeUtils.formatRemainingSeconds(0));
    }

    @Test
    @DisplayName("formatDetailedCountdown should break down hours and minutes or minutes and seconds")
    public void testDetailedCountdown() {
        TimeUtils.FormattedCountdown fmt1 = TimeUtils.formatDetailedCountdown(3665);
        assertEquals(1, fmt1.unit1);
        assertEquals("hr", fmt1.label1);
        assertEquals(1, fmt1.unit2);
        assertEquals("min", fmt1.label2);

        TimeUtils.FormattedCountdown fmt2 = TimeUtils.formatDetailedCountdown(125);
        assertEquals(2, fmt2.unit1);
        assertEquals("min", fmt2.label1);
        assertEquals(5, fmt2.unit2);
        assertEquals("sec", fmt2.label2);
    }
}
