package com.cudailyschedule.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBConnection provides thread-safe JDBC connection handling for PostgreSQL / Supabase.
 * - Handles connection creation with configurable SSL settings
 * - Provides connection diagnostics and testing
 * - Supports manual transaction demarcations (commit / rollback)
 */
public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found on classpath!", e);
        }
    }

    /**
     * Obtains a fresh database connection using settings from DBConfig.
     * Callers should use try-with-resources to ensure connections are closed.
     */
    public static Connection getConnection() throws SQLException {
        DBConfig config = DBConfig.getInstance();
        String url = config.getJdbcUrl();
        String user = config.getUser();
        String password = config.getPassword();

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to connect to PostgreSQL at " + config.getHost() + ":" + config.getPort() + " - " + e.getMessage());
            throw e;
        }
    }

    /**
     * Safely tests connection against the database with a fast timeout query (SELECT 1).
     * Returns a ConnectionTestResult containing status, response time, and error details if any.
     */
    public static ConnectionTestResult testConnection() {
        DBConfig config = DBConfig.getInstance();
        long startTime = System.currentTimeMillis();

        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        long duration = System.currentTimeMillis() - startTime;
                        return new ConnectionTestResult(true, "Successfully connected to PostgreSQL database at " + config.getHost() + " (" + duration + "ms)", duration, null);
                    }
                }
            }
            return new ConnectionTestResult(false, "Connection was established but could not execute test query", 0, null);
        } catch (SQLException e) {
            long duration = System.currentTimeMillis() - startTime;
            String errorMsg = e.getMessage();
            return new ConnectionTestResult(false, "Connection failed: " + errorMsg, duration, e);
        }
    }

    /**
     * Result record for connection testing.
     */
    public static class ConnectionTestResult {
        private final boolean success;
        private final String message;
        private final long responseTimeMs;
        private final Throwable exception;

        public ConnectionTestResult(boolean success, String message, long responseTimeMs, Throwable exception) {
            this.success = success;
            this.message = message;
            this.responseTimeMs = responseTimeMs;
            this.exception = exception;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public long getResponseTimeMs() { return responseTimeMs; }
        public Throwable getException() { return exception; }
    }
}
