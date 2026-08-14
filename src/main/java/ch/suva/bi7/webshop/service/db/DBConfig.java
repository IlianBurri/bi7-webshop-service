package ch.suva.bi7.webshop.service.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lädt die Datenbank-Konfiguration aus dem File {@code application-dev.properties}
 * (Classpath-Ressource unter {@code src/main/resources}).
 * So sind die Zugangsdaten nicht mehr im Code hartkodiert, sondern zentral
 * in einer Konfigurationsdatei.
 */
public final class DBConfig {

    private static final String PROPERTIES_FILE = "application-dev.properties";

    private static final Properties PROPERTIES = loadProperties();

    private DBConfig() {
        // Utility-Klasse: keine Instanzen erlaubt
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

    /**
     * Liefert den Wert zum Key. Eine gesetzte Umgebungsvariable hat Vorrang
     * (so können echte Zugangsdaten lokal gesetzt werden, ohne sie ins
     * Repository zu committen). Ist beides nicht gesetzt, wirft die Methode
     * sofort eine Exception – so schlägt die Konfiguration früh fehl, statt
     * später mit einem unlesbaren "null" in der Connection-URL zu scheitern.
     */
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
