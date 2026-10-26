package com.cudailyschedule.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Time utility functions for IST (Asia/Kolkata) time management and countdown math.
 */
public class TimeUtils {
    public static final ZoneId KOLKATA_ZONE = ZoneId.of("Asia/Kolkata");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    /**
     * Returns current date-time anchored in Indian Standard Time (Asia/Kolkata).
     */
    public static ZonedDateTime getNowInKolkata() {
        return ZonedDateTime.now(KOLKATA_ZONE);
    }

    /**
     * Parses standard timetable string "09:30 AM" or "1:40 PM" into LocalTime.
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return LocalTime.MIDNIGHT;
        }
        String formatted = timeStr.trim().toUpperCase();
        // Ensure two-digit hour for DateTimeFormatter if single digit provided
        if (formatted.length() >= 7 && formatted.charAt(1) == ':') {
            formatted = "0" + formatted;
        }
        try {
            return LocalTime.parse(formatted, TIME_FORMATTER);
        } catch (Exception e) {
            // Fallback parsing manual splitting
            String[] parts = formatted.split(" ");
            String[] hm = parts[0].split(":");
            int hour = Integer.parseInt(hm[0]);
            int minute = Integer.parseInt(hm[1]);
            if (parts.length > 1 && "PM".equalsIgnoreCase(parts[1]) && hour < 12) {
                hour += 12;
            }
            if (parts.length > 1 && "AM".equalsIgnoreCase(parts[1]) && hour == 12) {
                hour = 0;
            }
            return LocalTime.of(hour, minute);
        }
    }

    /**
     * Formats duration between now and target into short countdown string (e.g., "45m", "1h 10m").
     */
    public static String getCountdownString(LocalTime targetTime, LocalTime fromTime) {
        long diffSeconds = Duration.between(fromTime, targetTime).getSeconds();
        if (diffSeconds <= 0) return "0m";

        long hours = diffSeconds / 3600;
        long minutes = (diffSeconds % 3600) / 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return Math.max(1, minutes) + "m";
    }

    /**
     * Formats seconds into MM:SS for digital timers and circular progress rings.
     */
    public static String formatRemainingSeconds(long totalSeconds) {
        if (totalSeconds <= 0) return "00:00";
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Structure holding 2-unit countdown breakdown for teacher timer widget.
     */
    public static class FormattedCountdown {
        public final long unit1;
        public final String label1;
        public final long unit2;
        public final String label2;

        public FormattedCountdown(long unit1, String label1, long unit2, String label2) {
            this.unit1 = unit1;
            this.label1 = label1;
            this.unit2 = unit2;
            this.label2 = label2;
        }
    }

    /**
     * Formats remaining seconds into dual-unit breakdown (e.g. 1 hr 15 min OR 45 min 20 sec).
     */
    public static FormattedCountdown formatDetailedCountdown(long remainingSeconds) {
        if (remainingSeconds <= 0) {
            return new FormattedCountdown(0, "min", 0, "sec");
        }
        if (remainingSeconds >= 3600) {
            long hours = remainingSeconds / 3600;
            long minutes = (remainingSeconds % 3600) / 60;
            return new FormattedCountdown(hours, "hr", minutes, "min");
        }
        long minutes = remainingSeconds / 60;
        long seconds = remainingSeconds % 60;
        return new FormattedCountdown(minutes, "min", seconds, "sec");
    }
}
