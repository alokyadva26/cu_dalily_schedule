package com.cudailyschedule;

import com.cudailyschedule.config.DBConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DBConfigTest {

    @Test
    @DisplayName("DBConfig singleton should load default or environment values")
    public void testDBConfigInstance() {
        DBConfig config = DBConfig.getInstance();
        assertNotNull(config, "DBConfig instance should not be null");
        assertNotNull(config.getHost(), "Host should be configured");
        assertTrue(config.getPort() > 0, "Port should be positive");
        assertNotNull(config.getDatabase(), "Database name should not be null");
        assertNotNull(config.getJdbcUrl(), "JDBC URL should not be null");
    }

    @Test
    @DisplayName("JDBC URL should correctly format host, port, database, and SSL")
    public void testJdbcUrlFormatting() {
        DBConfig config = DBConfig.getInstance();
        config.update("db.sample.supabase.co", 5432, "postgres", "postgres", "secret", true);
        String url = config.getJdbcUrl();
        assertTrue(url.startsWith("jdbc:postgresql://db.sample.supabase.co:5432/postgres"), "URL format mismatch");
        assertTrue(url.contains("ssl=true") && url.contains("sslmode=require"), "SSL parameters should be set");

        config.update("localhost", 5432, "postgres", "postgres", "secret", false);
        String nonSslUrl = config.getJdbcUrl();
        assertTrue(nonSslUrl.contains("ssl=false"), "Non-SSL should specify ssl=false");
    }
}
