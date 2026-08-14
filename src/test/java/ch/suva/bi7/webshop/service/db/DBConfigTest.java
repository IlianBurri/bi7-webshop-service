package ch.suva.bi7.webshop.service.db;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prüft, dass die Konfigurationsdatei alle Pflicht-Keys enthält.
 * Fehlt ein Key, schlägt DBConfig erst beim Start fehl – dieser Test
 * macht das früh und ohne laufende Datenbank sichtbar.
 */
class DBConfigTest {

    private static final String PROPERTIES_FILE = "application-dev.properties";

    private static final List<String> PFLICHT_KEYS = List.of("db.host", "db.name", "db.user", "db.password");

    @Test
    void propertiesDateiEnthaeltAllePflichtKeys() throws IOException {
        Properties props = new Properties();
        try (InputStream in = DBConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            assertNotNull(in, "Konfigurationsdatei '" + PROPERTIES_FILE + "' muss im Classpath liegen");
            props.load(in);
        }

        for (String key : PFLICHT_KEYS) {
            assertTrue(props.containsKey(key), "Pflicht-Key '" + key + "' fehlt in '" + PROPERTIES_FILE + "'");
            assertTrue(!props.getProperty(key).isBlank(),
                    "Pflicht-Key '" + key + "' darf nicht leer sein");
        }
    }
}
