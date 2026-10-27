package com.cudailyschedule.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * DBConfig manages database configuration parameters, reading from:
 * 1. Environment variables
 * 2. Local .env or .env.local file (excluded from version control)
 * 3. Local config.properties file
 * 4. Fallback defaults
 */
public class DBConfig {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private boolean ssl;
    private int connectionTimeoutSeconds;

    private static DBConfig instance;

    private DBConfig() {
        loadConfiguration();
    }

    public static synchronized DBConfig getInstance() {
        if (instance == null) {
            instance = new DBConfig();
        }
        return instance;
    }

    private void loadConfiguration() {
        Properties props = new Properties();

        // 1. Try loading from .env or .env.local in current directory, subfolder, or parent directory
        File[] envFiles = new File[]{
            new File(".env"),
            new File(".env.local"),
            new File("cu-daily-schedule-java/.env"),
            new File("cu-daily-schedule-java/.env.local"),
            new File("../.env"),
            new File("../.env.local"),
            new File("config.properties")
        };

        for (File envFile : envFiles) {
            if (envFile.exists() && envFile.isFile()) {
                try (InputStream in = new FileInputStream(envFile)) {
                    props.load(in);
                    break;
                } catch (Exception ignored) {
                }
            }
        }

        // 2. Resolve values with priority: System Env > File Props > Defaults
        this.host = getSetting("DB_HOST", props, "localhost");
        this.port = Integer.parseInt(getSetting("DB_PORT", props, "5432"));
        this.database = getSetting("DB_NAME", props, "postgres");
        this.user = getSetting("DB_USER", props, "postgres");
        this.password = getSetting("DB_PASSWORD", props, "");
        
        String sslVal = getSetting("DB_SSL", props, "false");
        this.ssl = "true".equalsIgnoreCase(sslVal) || "require".equalsIgnoreCase(sslVal);
        
        this.connectionTimeoutSeconds = Integer.parseInt(getSetting("DB_TIMEOUT", props, "5"));
    }

    private String getSetting(String key, Properties props, String defaultValue) {
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.trim().isEmpty()) {
            return sysEnv.trim();
        }
        String propVal = props.getProperty(key);
        if (propVal != null && !propVal.trim().isEmpty()) {
            return propVal.trim();
        }
        return defaultValue;
    }

    public String getJdbcUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:postgresql://")
          .append(host)
          .append(":")
          .append(port)
          .append("/")
          .append(database)
          .append("?connectTimeout=").append(connectionTimeoutSeconds);

        if (ssl) {
            sb.append("&ssl=true&sslmode=require");
        } else {
            sb.append("&ssl=false");
        }
        return sb.toString();
    }

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public boolean isSsl() { return ssl; }
    public int getConnectionTimeoutSeconds() { return connectionTimeoutSeconds; }

    public void update(String host, int port, String database, String user, String password, boolean ssl) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.ssl = ssl;
    }
}
