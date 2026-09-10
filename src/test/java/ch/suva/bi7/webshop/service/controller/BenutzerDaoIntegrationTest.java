package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.dao.BenutzerDaoImpl;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class BenutzerDaoIntegrationTest {

    private BenutzerDao benutzerDao;
    private DBConnection dbConnection;

    @BeforeEach
    void setUp() throws Exception {
        try {
            dbConnection = new DBConnectionImpl(DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
            benutzerDao = new BenutzerDaoImpl(dbConnection);
        } catch (Exception e) {
            assumeTrue(false, "MariaDB not available: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (dbConnection == null) {
            return;
        }
        try {
            System.out.println("Aktuelle Benutzer in der user Tabelle");
            System.out.println("---------------------------------------");
            java.sql.ResultSet rs = dbConnection.execute("SELECT username, email FROM user");
            if (rs != null) {
                while (rs.next()) {
                    System.out.println("username: " + rs.getString("username") + " | E-Mail: " + rs.getString("email"));
                }
            }

            dbConnection.execute("DELETE FROM user WHERE email IN " +
                    "(" + "'bruce.wayne@gotham.com', " +
                    "'spidey@dailybugle.com', " +
                    "'hawk.eye@arrow.com', " +
                    "'black.widow@avengers.com')");

        } catch (Exception e) {
            System.out.println("Fehler beim Anzeigen/Aufräumen: " + e.getMessage());
        }
    }

    @Test
    void speichereBenutzer() throws Exception {
        BenutzerEntity testBenutzer = new BenutzerEntity("Bruce Wayne", "bruce.wayne@gotham.com", "bruce", false);

        benutzerDao.speichereBenutzer(testBenutzer);
        Optional<BenutzerEntity> gefundenerBenutzer = benutzerDao.holeBenutzerNachEMail("bruce.wayne@gotham.com");

        assertTrue(gefundenerBenutzer.isPresent(), "Benutzer wurde erfolgreich in der Datenbank registriert sein");
        assertEquals("Bruce Wayne", gefundenerBenutzer.get().getUsername());
    }

    @Test
    void holeBenutzerNachEMail() throws Exception {
        BenutzerEntity testBenutzer = new BenutzerEntity("Peter Parker", "spidey@dailybugle.com", "webslinger", false);
        benutzerDao.speichereBenutzer(testBenutzer);

        Optional<BenutzerEntity> gefundenerBenutzer = benutzerDao.holeBenutzerNachEMail("spidey@dailybugle.com");
        assertTrue(gefundenerBenutzer.isPresent());
        assertEquals("Peter Parker", gefundenerBenutzer.get().getUsername());
    }

    @Test
    void holeAlleBenutzernamen() throws Exception {
        benutzerDao.speichereBenutzer(new BenutzerEntity("Hawk Eye", "hawk.eye@arrow.com", "target", false));
        benutzerDao.speichereBenutzer(new BenutzerEntity("Black Widow", "black.widow@avengers.com", "spider", false));

        List<String> usernames = benutzerDao.holeAlleBenutzernamen();

        assertNotNull(usernames);
        assertTrue(usernames.contains("Hawk Eye"));
        assertTrue(usernames.contains("Black Widow"));
    }
}
