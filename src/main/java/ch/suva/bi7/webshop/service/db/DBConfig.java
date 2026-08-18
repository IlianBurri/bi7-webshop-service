package ch.suva.bi7.webshop.service.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DBConfig {

    private static final String PROPERTIES_FILE = "application-dev.properties";

    private static final Properties PROPERTIES = loadProperties();

    private DBConfig() {
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DBConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Konfigurationsdatei '" + PROPERTIES_FILE + "' wurde nicht im Classpath gefunden");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Konfigurationsdatei '" + PROPERTIES_FILE + "' konnte nicht geladen werden", e);
        }
        return props;
    }

    public static String getHost() {
        return requireValue("db.host", "DB_HOST");
    }

    public static String getSchema() {
        return requireValue("db.name", "DB_NAME");
    }

    public static String getUser() {
        return requireValue("db.user", "DB_USER");
    }

    public static String getPassword() {
        return requireValue("db.password", "DB_PASSWORD");
    }

    public static int getPort() {
        String raw = requireValue("server.port", "SERVER_PORT");
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Konfigurationskey 'server.port' muss eine ganze Zahl sein, war: '" + raw + "'", e);
        }
    }

    public static boolean isDev() {
        String envValue = System.getenv("APP_IS_DEV");
        if (envValue != null && !envValue.isBlank()) {
            return parseBoolean(envValue, "APP_IS_DEV");
        }
        String value = PROPERTIES.getProperty("app.isDev");
        if (value == null || value.isBlank()) {
            return false;
        }
        return parseBoolean(value, "app.isDev");
    }

    private static boolean parseBoolean(String value, String source) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalStateException(
                "Konfigurationskey '" + source + "' muss 'true' oder 'false' sein, war: '" + value + "'");
    }

    private static String requireValue(String key, String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Konfigurationskey '" + key + "' fehlt in '" + PROPERTIES_FILE +
                    "' (oder Umgebungsvariable '" + envKey + "' ist nicht gesetzt)");
        }
        return value;
    }
}
