package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WarenkorbDaoIntegrationTest {

    private static final String TEST_EMAIL = "cart.test@example.com";
    private static final String TEST_EMAIL_ZWEITER_USER = "cart.zweiter@example.com";

    private WarenkorbDao warenkorbDao;
    private DBConnection dbConnection;

    @BeforeEach
    void setUp() throws Exception {
        try {
            dbConnection = new DBConnectionImpl(DBConfig.getHost(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
            warenkorbDao = new WarenkorbDaoImpl(dbConnection);
            dbConnection.executeUpdate("INSERT IGNORE INTO user (username, email, password) " +
                    "VALUES ('Cart Test', '" + TEST_EMAIL + "', 'test123')");
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
            dbConnection.executeUpdate("DELETE FROM user WHERE email IN ('" + TEST_EMAIL + "', '" + TEST_EMAIL_ZWEITER_USER + "')");
        } catch (Exception e) {
            System.out.println("Fehler beim Aufräumen: " + e.getMessage());
        }
    }

    @Test
    void leererWarenkorbGibtLeereListe() throws Exception {
        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void artikelHinzufuegenLegtItemMitJoindatenAn() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 2);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);

        assertEquals(1, items.size());
        WarenkorbItem item = items.get(0);
        assertEquals(TEST_EMAIL, item.getUserEmail());
        assertEquals(1, item.getArtikelId());
        assertEquals(2, item.getMenge());
        assertEquals("iPhone 15 Pro", item.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), item.getArtikelPreis());
        assertNotNull(item.getArtikelBild());
    }

    @Test
    void artikelDoppeltHinzufuegenErhoehtMenge() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 2);
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 3);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);

        assertEquals(1, items.size(), "Derselbe Artikel darf nur einmal im Warenkorb stehen");
        assertEquals(5, items.get(0).getMenge(), "2 + 3 muss 5 ergeben");
    }

    @Test
    void mehrereArtikelKommenInDenWarenkorb() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 1);
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 2, 1);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);

        assertEquals(2, items.size());
    }

    @Test
    void mengeAktualisierenSetztMengeNeu() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 2);
        int itemId = warenkorbDao.getWarenkorbByUser(TEST_EMAIL).get(0).getWarenkorbItemId();

        warenkorbDao.updateMenge(itemId, 7);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);
        assertEquals(1, items.size());
        assertEquals(7, items.get(0).getMenge());
    }

    @Test
    void warenkorbItemLoeschenEntferntItem() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 1);
        int itemId = warenkorbDao.getWarenkorbByUser(TEST_EMAIL).get(0).getWarenkorbItemId();

        warenkorbDao.deleteWarenkorbItem(itemId);

        assertTrue(warenkorbDao.getWarenkorbByUser(TEST_EMAIL).isEmpty());
    }

    @Test
    void warenkorbBleibtLeerNachUserLoeschung() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 1);
        assertEquals(1, warenkorbDao.getWarenkorbByUser(TEST_EMAIL).size());

        dbConnection.executeUpdate("DELETE FROM user WHERE email = '" + TEST_EMAIL + "'");

        assertTrue(warenkorbDao.getWarenkorbByUser(TEST_EMAIL).isEmpty(),
                "ON DELETE CASCADE muss die Warenkorb-Items mit löschen");
    }

    @Test
    void warenkorbUeberlebtLogoutUndNeuenLogin() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 2);
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 3, 1);

        DBConnection neueConnection = new DBConnectionImpl(
                DBConfig.getHost(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
        WarenkorbDao daoNachNeuemLogin = new WarenkorbDaoImpl(neueConnection);

        List<WarenkorbItem> items = daoNachNeuemLogin.getWarenkorbByUser(TEST_EMAIL);

        assertEquals(2, items.size(), "Nach Abmelden darf nichts verloren gehen");
        WarenkorbItem erster = items.get(0);
        assertEquals(1, erster.getArtikelId());
        assertEquals(2, erster.getMenge());
        assertEquals("iPhone 15 Pro", erster.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), erster.getArtikelPreis());
    }

    @Test
    void warenkorbIstProUserGetrennt() throws Exception {
        dbConnection.executeUpdate("INSERT IGNORE INTO user (username, email, password) " +
                "VALUES ('Zweiter User', '" + TEST_EMAIL_ZWEITER_USER + "', 'test123')");

        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 2);

        assertTrue(warenkorbDao.getWarenkorbByUser(TEST_EMAIL_ZWEITER_USER).isEmpty(),
                "Der andere User darf keine fremden Artikel sehen");

        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL_ZWEITER_USER, 2, 1);

        List<WarenkorbItem> userA = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);
        List<WarenkorbItem> userB = warenkorbDao.getWarenkorbByUser(TEST_EMAIL_ZWEITER_USER);

        assertEquals(1, userA.size());
        assertEquals(1, userB.size());
        assertEquals(1, userA.get(0).getArtikelId(), "User A behält nur seine eigenen Artikel");
        assertEquals(2, userB.get(0).getArtikelId());
    }

    @Test
    void sqlInjectionVersuchLiefertKeineFremdenDaten() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 1);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser("' OR '1'='1");

        assertTrue(items.isEmpty(), "SQL-Injection darf keine fremden Warenkorb-Items liefern");
    }

    @Test
    void artikelHinzufuegenMitUnbekanntemArtikelSchlaegtFehl() throws Exception {
        assertThrows(Exception.class,
                () -> warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 999999, 1),
                "Fremdschlüssel auf artikel.artikelId muss verletzt werden");

        assertTrue(warenkorbDao.getWarenkorbByUser(TEST_EMAIL).isEmpty(),
                "Bei fehlgeschlagenem INSERT darf nichts im Warenkorb landen");
    }

    @Test
    void artikelHinzufuegenMitUnbekanntemUserSchlaegtFehl() {
        assertThrows(Exception.class,
                () -> warenkorbDao.addArtikelToWarenkorb("gibts.nicht@example.com", 1, 1),
                "Fremdschlüssel auf user.email muss verletzt werden");
    }

    @Test
    void mengeErhoehenBehaeltPreisUndArtikelDaten() throws Exception {
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 1);
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 4);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);

        assertEquals(1, items.size());
        assertEquals(5, items.get(0).getMenge(), "1 + 4 muss 5 ergeben");
        assertEquals("iPhone 15 Pro", items.get(0).getArtikelName());
        assertEquals(new BigDecimal("1199.00"), items.get(0).getArtikelPreis(),
                "Der Preis kommt aus der artikel-Tabelle und darf sich bei Mengenänderung nicht verändern");
    }
}
