package ch.suva.bi7.webshop.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WebshopServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebshopServiceConfig.class);

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

    public static boolean lagerAuffuellenBeimStart() {
        String envValue = System.getenv("APP_LAGER_AUFFUELLEN_BEIM_START");
        if (envValue != null && !envValue.isBlank()) {
            return parseBoolean(envValue, "APP_LAGER_AUFFUELLEN_BEIM_START");
        }
        String value = PROPERTIES.getProperty("app.lagerAuffuellenBeimStart", "false");
        if (value == null || value.isBlank()) {
            return false;
        }
        return parseBoolean(value, "app.lagerAuffuellenBeimStart");
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

    public static String getFirstCorsAllowedOrigin() {
        // getCorsAllowedOrigins() garantiert mindestens einen nicht leeren Eintrag
        return getCorsAllowedOrigins().get(0);
    }

    public static String[] getAdditionalCorsAllowedOrigins() {
        // Leeres Array, falls nur eine Origin konfiguriert ist
        List<String> origins = getCorsAllowedOrigins();
        return origins.subList(1, origins.size()).toArray(String[]::new);
    }

    public static String getSslCertPath() {
        return requireValue("app.ssl.certPath", "APP_SSL_CERT_PATH");
    }

    public static String getSslKeyPath() {
        return requireValue("app.ssl.keyPath", "APP_SSL_KEY_PATH");
    }
    public static String getSslKeyPassword() {
        String envValue = System.getenv("APP_SSL_KEY_PASSWORD");
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String value = PROPERTIES.getProperty("app.ssl.keyPassword");
        return (value == null || value.isBlank()) ? null : value;
    }

    public static void logKonfiguration() {
        logger.info("Webshop-Service startet mit folgender Konfiguration:");
        logger.info("  db.host                   = {} ({})", getDBHost(), quelle("DB_HOST"));
        logger.info("  db.port                   = {} ({})", getDBPort(), quelle("DB_PORT"));
        logger.info("  db.name                   = {} ({})", getDBSchema(), quelle("DB_NAME"));
        logger.info("  db.user                   = {} ({})", getDBUser(), quelle("DB_USER"));
        logger.info("  db.password               = ****** ({})", quelle("DB_PASSWORD"));
        logger.info("  app.runLiquibaseMigration = {} ({})", runLiquibaseMigration(), quelle("APP_RUN_LIQUIBASE_MIGRATION"));
        logger.info("  app.lagerAuffuellenBeimStart = {} ({})", lagerAuffuellenBeimStart(), quelle("APP_LAGER_AUFFUELLEN_BEIM_START"));
        logger.info("  cors.ersteOrigin          = {} ({})", getFirstCorsAllowedOrigin(), quelle("APP_CORS_ALLOWED_ORIGINS"));
        logger.info("  cors.weitereOrigins       = {} ({})", String.join(", ", getAdditionalCorsAllowedOrigins()), quelle("APP_CORS_ALLOWED_ORIGINS"));
        logger.info("  app.ssl.certPath          = {} ({})", getSslCertPath(), quelle("APP_SSL_CERT_PATH"));
        logger.info("  app.ssl.keyPath           = {} ({})", getSslKeyPath(), quelle("APP_SSL_KEY_PATH"));
        logger.info("  app.ssl.keyPassword       = {} ({})", getSslKeyPassword() == null ? "<nicht gesetzt>" : "******", quelle("APP_SSL_KEY_PASSWORD"));
    }

    private static String quelle(String envKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return "Umgebungsvariable " + envKey;
        }
        return PROPERTIES_FILE;
    }
}
