package com.skillbridge.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Properties;

/**
 * Central configuration. Values come from (in priority order):
 * 1. a JVM option   (-Ddb.password=...)
 * 2. an environment variable (DB_PASSWORD)
 * 3. db.properties on the classpath
 */
public final class Config {
    private static final Properties FILE = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                FILE.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Config() {
    }

    public static String get(String key, String defaultValue) {
        String v = System.getProperty(key);
        if (v == null) {
            v = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (v == null) {
            v = FILE.getProperty(key);
        }
        return v == null ? defaultValue : v.trim();
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Time zone used for "today" and "now" in booking rules. */
    public static ZoneId zone() {
        try {
            return ZoneId.of(get("app.timezone", "Asia/Kolkata"));
        } catch (Exception e) {
            return ZoneId.of("Asia/Kolkata");
        }
    }
}
