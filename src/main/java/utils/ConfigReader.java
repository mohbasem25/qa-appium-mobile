package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads {@code config.properties} once and exposes typed accessors.
 * <p>
 * Values can be overridden at run time via JVM system properties, e.g.
 * {@code -Dappium.deviceName=Pixel_6_API_34}, which is convenient for
 * pointing CI at a differently named emulator without touching the file.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException(
                        "config.properties not found on classpath (expected in src/main/resources)");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
        // static utility - no instances
    }

    public static String get(String key) {
        // System property takes precedence over the checked-in defaults
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing config key: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
