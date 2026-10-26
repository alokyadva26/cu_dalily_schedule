package com.cudailyschedule.model;

/**
 * Encapsulates the real-time schedule calculation status.
 */
public class ScheduleStatus {
    public enum StatusType {
        BEFORE,
        ONGOING,
        COMPLETED
    }

    private final StatusType status;
    private final ScheduleEntry currentClass;
    private final ScheduleEntry nextClass;
    private final long remainingSeconds;
    private final boolean isFinished;

    public ScheduleStatus(StatusType status, ScheduleEntry currentClass, ScheduleEntry nextClass, long remainingSeconds, boolean isFinished) {
        this.status = status;
        this.currentClass = currentClass;
        this.nextClass = nextClass;
        this.remainingSeconds = remainingSeconds;
        this.isFinished = isFinished;
    }

    public StatusType getStatus() { return status; }
    public ScheduleEntry getCurrentClass() { return currentClass; }
    public ScheduleEntry getNextClass() { return nextClass; }
    public long getRemainingSeconds() { return remainingSeconds; }
    public boolean isFinished() { return isFinished; }

    public static ScheduleStatus finished() {
        return new ScheduleStatus(StatusType.COMPLETED, null, null, 0, true);
    }
}
