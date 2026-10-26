package com.cudailyschedule.service;

import com.cudailyschedule.model.ScheduleEntry;
import com.cudailyschedule.model.ScheduleStatus;
import com.cudailyschedule.util.TimeUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ScheduleTimingService manages time-based calculations:
 * - Determining whether a class is active, upcoming, or completed
 * - Calculating remaining time for countdowns and progress rings
 * - Preserves exact logic from cu-daily-schedule/src/lib/teacherSchedule.ts
 */
public class ScheduleTimingService {

    public enum SessionState {
        NORMAL,
        ACTIVE,
        NEXT,
        COMPLETED
    }

    /**
     * Determines the overall status for a list of daily schedule entries at the given reference time.
     */
    public ScheduleStatus calculateScheduleStatus(List<ScheduleEntry> entries, LocalTime currentTime) {
        if (entries == null || entries.isEmpty()) {
            return ScheduleStatus.finished();
        }

        // Sort chronologically by start time
        List<ScheduleEntry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparing(e -> TimeUtils.parseTime(e.getStartTime())));

        ScheduleEntry currentClass = null;
        ScheduleEntry nextClass = null;

        for (int i = 0; i < sorted.size(); i++) {
            ScheduleEntry entry = sorted.get(i);
            LocalTime start = TimeUtils.parseTime(entry.getStartTime());
            LocalTime end = TimeUtils.parseTime(entry.getEndTime());

            // Active class: currentTime is between start and end
            if (!currentTime.isBefore(start) && currentTime.isBefore(end)) {
                currentClass = entry;
                nextClass = (i + 1 < sorted.size()) ? sorted.get(i + 1) : null;
                break;
            } else if (currentTime.isBefore(start)) {
                // Next class: currentTime is before start
                nextClass = entry;
                break;
            }
        }

        if (currentClass == null && nextClass == null) {
            return ScheduleStatus.finished();
        }

        if (currentClass != null) {
            LocalTime end = TimeUtils.parseTime(currentClass.getEndTime());
            long remainingSeconds = Math.max(0, Duration.between(currentTime, end).getSeconds());
            return new ScheduleStatus(ScheduleStatus.StatusType.ONGOING, currentClass, nextClass, remainingSeconds, false);
        } else {
            LocalTime start = TimeUtils.parseTime(nextClass.getStartTime());
            long remainingSeconds = Math.max(0, Duration.between(currentTime, start).getSeconds());
            return new ScheduleStatus(ScheduleStatus.StatusType.BEFORE, null, nextClass, remainingSeconds, false);
        }
    }

    /**
     * Calculates the individual session state for a card in the timeline.
     */
    public SessionState determineSessionState(String startTimeStr, String endTimeStr, LocalTime currentTime, boolean isNextInSequence) {
        LocalTime start = TimeUtils.parseTime(startTimeStr);
        LocalTime end = TimeUtils.parseTime(endTimeStr);

        if (currentTime.isAfter(end) || currentTime.equals(end)) {
            return SessionState.COMPLETED;
        } else if (!currentTime.isBefore(start) && currentTime.isBefore(end)) {
            return SessionState.ACTIVE;
        } else if (isNextInSequence) {
            return SessionState.NEXT;
        }
        return SessionState.NORMAL;
    }
}
