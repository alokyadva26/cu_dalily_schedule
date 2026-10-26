package com.cudailyschedule;

import com.cudailyschedule.config.DBConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    @DisplayName("testConnection should execute safely and return diagnostic result without crashing")
    public void testSafeConnectionDiagnostic() {
        DBConnection.ConnectionTestResult result = DBConnection.testConnection();
        assertNotNull(result, "ConnectionTestResult should never be null");
        assertNotNull(result.getMessage(), "Diagnostic message should be populated");

        // Regardless of whether DB is online or offline, it must report accurately
        if (result.isSuccess()) {
            assertTrue(result.getMessage().contains("Successfully connected"), "Success message expected");
            assertTrue(result.getResponseTimeMs() >= 0);
        } else {
            assertTrue(result.getMessage().contains("Connection failed") || result.getMessage().contains("could not execute"), "Failure message expected");
        }
    }
}
