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
        return requireProperty("db.host");
    }

    public static String getSchema() {
        return requireProperty("db.name");
    }

    public static String getUser() {
        return requireProperty("db.user");
    }

    public static String getPassword() {
        return requireProperty("db.password");
    }

    /**
     * Liefert den Wert zum Key oder wirft sofort eine Exception, wenn der Key
     * fehlt/vertippt ist. So schlägt die Konfiguration früh fehl, statt später
     * mit einem unlesbaren "null" in der Connection-URL zu scheitern.
     */
    private static String requireProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Konfigurationskey '" + key + "' fehlt in '" + PROPERTIES_FILE + "'");
        }
        return value;
    }
}
