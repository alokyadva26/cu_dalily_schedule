package com.cudailyschedule;

import com.cudailyschedule.util.SubjectRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubjectRegistryTest {

    @Test
    @DisplayName("SubjectRegistry should resolve known Chandigarh University subject codes")
    public void testSubjectMapping() {
        assertEquals("Competitive Coding-II", SubjectRegistry.getSubjectName("24CSP-305..P::GP-A"));
        assertEquals("Computer Networks", SubjectRegistry.getSubjectName("24CST-302..L::GP-All"));
        assertEquals("Project Based Learning in Java", SubjectRegistry.getSubjectName("24CSH-301..L::GP-All"));
        assertEquals("Full Stack Development - II", SubjectRegistry.getSubjectName("24CSP-304"));
        assertEquals("ServiceNow-I", SubjectRegistry.getSubjectName("24CSH-318"));
    }

    @Test
    @DisplayName("SubjectRegistry should handle break entries and fallback to raw code")
    public void testBreakAndFallback() {
        assertEquals("Break", SubjectRegistry.getSubjectName("Break"));
        assertEquals("Break", SubjectRegistry.getSubjectName("lunch break"));
        assertEquals("UnknownCourse-999", SubjectRegistry.getSubjectName("UnknownCourse-999..L"));
    }
}
