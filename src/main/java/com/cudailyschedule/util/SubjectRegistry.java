package com.cudailyschedule.util;

import java.util.HashMap;
import java.util.Map;

/**
 * SubjectRegistry maps university course codes to friendly course titles.
 * Exact mirror of subjectMap from cu-daily-schedule/src/lib/utils.ts.
 */
public class SubjectRegistry {
    private static final Map<String, String> SUBJECT_MAP = new HashMap<>();

    static {
        SUBJECT_MAP.put("24CSH-318", "ServiceNow-I");
        SUBJECT_MAP.put("24CSP-304", "Full Stack Development - II");
        SUBJECT_MAP.put("24CSP-305", "Competitive Coding-II");
        SUBJECT_MAP.put("24CST-302", "Computer Networks");
        SUBJECT_MAP.put("24CSH-301", "Project Based Learning in Java");
        SUBJECT_MAP.put("24SMT-341", "Probability and Statistics");
        SUBJECT_MAP.put("24TDP-311", "Soft Skills-III");
        SUBJECT_MAP.put("24TDT-312", "Aptitude-III");
        SUBJECT_MAP.put("24CST-331", "Big Data Fundamentals (Through SWAYAM)");
        SUBJECT_MAP.put("24CSI-305", "Institute/Industrial Summer Training");
        SUBJECT_MAP.put("25CSH-204", "Database Management System");
        SUBJECT_MAP.put("26CSH-101", "Logical Thinking and Problem Solving");
    }

    /**
     * Resolves raw subject code (e.g. "24CST-302..L::GP-All") to readable subject name.
     */
    public static String getSubjectName(String rawSubject) {
        if (rawSubject == null || rawSubject.trim().isEmpty()) {
            return "Class";
        }
        String clean = rawSubject.trim();
        if (clean.toLowerCase().contains("break")) {
            return "Break";
        }
        String code = clean.split("\\.\\.")[0].trim();
        return SUBJECT_MAP.getOrDefault(code, code);
    }

    /**
     * Extracts just the course code (e.g. "24CST-302").
     */
    public static String extractCourseCode(String rawSubject) {
        if (rawSubject == null) return "";
        return rawSubject.split("\\.\\.")[0].trim();
    }
}
