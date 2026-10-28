package ch.suva.bi7.webshop.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class WebshopServiceConfig {

    private static final String PROPERTIES_FILE = "application-dev.properties";

    private static final Properties PROPERTIES = loadProperties();

    private WebshopServiceConfig() {
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = WebshopServiceConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
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

    public static String getDBHost() {
        return requireValue("db.host", "DB_HOST");
    }

    public static String getDBSchema() {
        return requireValue("db.name", "DB_NAME");
    }

    public static String getDBUser() {
        return requireValue("db.user", "DB_USER");
    }

    public static String getDBPassword() {
        return requireValue("db.password", "DB_PASSWORD");
    }

    public static int getDBPort() {
        return Integer.valueOf(requireValue("db.port", "DB_PORT")).intValue();
    }

    public static boolean runLiquibaseMigration() {
        String envValue = System.getenv("APP_RUN_LIQUIBASE_MIGRATION");
        if (envValue != null && !envValue.isBlank()) {
            return parseBoolean(envValue, "APP_RUN_LIQUIBASE_MIGRATION");
        }
        String value = PROPERTIES.getProperty("app.runLiquibaseMigration", "true");
        if (value == null || value.isBlank()) {
            return true;
        }
        return parseBoolean(value, "app.runLiquibaseMigration");
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

    public static List<String> getCorsAllowedOrigins() {
        String value = requireValue("app.corsAllowedOrigins", "APP_CORS_ALLOWED_ORIGINS");
        List<String> origins = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        if (origins.isEmpty()) {
            throw new IllegalStateException(
                    "Konfigurationskey 'app.corsAllowedOrigins' muss mindestens eine Origin enthalten");
        }
        return origins;
    }
}
