package com.cudailyschedule.service;

import com.cudailyschedule.config.DBConfig;
import com.cudailyschedule.config.DBConnection;

/**
 * DatabaseService coordinates database connection status and health checks.
 */
public class DatabaseService {
    private static DBConnection.ConnectionTestResult lastTestResult;

    public static synchronized DBConnection.ConnectionTestResult checkConnection() {
        lastTestResult = DBConnection.testConnection();
        return lastTestResult;
    }

    public static synchronized DBConnection.ConnectionTestResult getLastTestResult() {
        if (lastTestResult == null) {
            return checkConnection();
        }
        return lastTestResult;
    }

    public static boolean isConnected() {
        DBConnection.ConnectionTestResult res = getLastTestResult();
        return res != null && res.isSuccess();
    }

    public static String getStatusMessage() {
        DBConnection.ConnectionTestResult res = getLastTestResult();
        if (res == null) return "Unknown database status";
        if (res.isSuccess()) {
            return "Connected to " + DBConfig.getInstance().getHost() + " (" + res.getResponseTimeMs() + "ms)";
        } else {
            return "Offline / Fallback Mode (" + DBConfig.getInstance().getHost() + " unreachable)";
        }
    }
}
