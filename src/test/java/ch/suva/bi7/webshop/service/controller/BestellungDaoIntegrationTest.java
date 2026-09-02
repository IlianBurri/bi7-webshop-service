package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.Bestellung;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class BestellungDaoIntegrationTest {

    private static final String TEST_EMAIL = "bestellung.it@example.com";

    private BestellungDao bestellungDao;
    private WarenkorbDao warenkorbDao;
    private DBConnection dbConnection;

    @BeforeEach
    void setUp() throws Exception {
        try {
            dbConnection = new DBConnectionImpl(DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
            bestellungDao = new BestellungDaoImpl(dbConnection);
            warenkorbDao = new WarenkorbDaoImpl(dbConnection);
            dbConnection.executeUpdate("INSERT IGNORE INTO user (username, email, password) " +
                    "VALUES ('Bestell Test', '" + TEST_EMAIL + "', 'test123')");
            assumeTrue(bestellungHatGesamtpreisSpalte(),
                    "Migration mit bestellung.gesamtpreis wurde nicht angewendet (Backend einmal starten)");
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
            dbConnection.executeUpdate("DELETE FROM bestellung WHERE userEmail = '" + TEST_EMAIL + "'");
            dbConnection.executeUpdate("DELETE FROM user WHERE email = '" + TEST_EMAIL + "'");
        } catch (Exception e) {
            System.out.println("Fehler beim Aufräumen: " + e.getMessage());
        }
    }

    @Test
    void bestellungErzeugenSpeichertGesamtpreisPositionenUndLeertWarenkorb() throws Exception {
        int adressId = legeAdresseAn();
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 1, 2);
        warenkorbDao.addArtikelToWarenkorb(TEST_EMAIL, 2, 1);

        List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(TEST_EMAIL);
        assertEquals(2, items.size());
        BigDecimal gesamtpreis = items.stream()
                .map(item -> item.getArtikelPreis().multiply(BigDecimal.valueOf(item.getMenge())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int bestellungId = bestellungDao.createBestellungWithItems(TEST_EMAIL, adressId, gesamtpreis, items);

        assertTrue(bestellungId > 0, "Der generierte Bestell-Key muss zurückkommen");
        assertTrue(warenkorbDao.getWarenkorbByUser(TEST_EMAIL).isEmpty(),
                "Nach der Bestellung muss der Warenkorb geleert sein");

        Optional<Bestellung> gespeichert = bestellungDao.getBestellungById(bestellungId);
        assertTrue(gespeichert.isPresent(), "Die Bestellung muss sich per ID lesen lassen");
        Bestellung bestellung = gespeichert.get();
        assertEquals(TEST_EMAIL, bestellung.getUserEmail());
        assertEquals(adressId, bestellung.getAdressId());
        assertEquals(gesamtpreis, bestellung.getGesamtpreis(),
                "Der berechnete Gesamtpreis muss in der DB gespeichert und zurückgelesen werden");
        assertEquals("BEZAHLT", bestellung.getStatus());
        assertNotNull(bestellung.getBestelltAm(), "bestelldatum muss gesetzt sein");

        assertEquals(2, anzahlPositionen(bestellungId), "Pro Warenkorb-Item muss eine Bestellposition entstehen");
        assertEquals(anfangsPreisVonArtikel(1), einzelpreisVonPosition(bestellungId, 1),
                "Der Preis muss zum Bestellzeitpunkt festgehalten werden");
        assertEquals(anfangsPreisVonArtikel(2), einzelpreisVonPosition(bestellungId, 2));

        List<Bestellung> proUser = bestellungDao.getBestellungenByUserEmail(TEST_EMAIL);
        assertEquals(1, proUser.size(), "Die Bestellung muss über die User-Liste auffindbar sein");
        assertEquals(bestellungId, proUser.get(0).getBestellungId());
        assertEquals(gesamtpreis, proUser.get(0).getGesamtpreis());
    }

    private int legeAdresseAn() throws Exception {
        return dbConnection.executeUpdateReturningGeneratedKeys(
                "INSERT INTO adresse (userEmail, vorname, nachname, strasse, plz, ort, land) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");
    }

    private boolean bestellungHatGesamtpreisSpalte() {
        try (ResultSet rs = dbConnection.execute(
                "SELECT COUNT(*) AS c FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = 'bestellung' AND column_name = 'gesamtpreis'")) {
            return rs != null && rs.next() && rs.getInt("c") > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private int anzahlPositionen(int bestellungId) throws Exception {
        try (ResultSet rs = dbConnection.execute(
                "SELECT COUNT(*) AS c FROM bestellposition WHERE bestellungId = ?", bestellungId)) {
            rs.next();
            return rs.getInt("c");
        }
    }

    private BigDecimal einzelpreisVonPosition(int bestellungId, int artikelId) throws Exception {
        try (ResultSet rs = dbConnection.execute(
                "SELECT einzelpreis FROM bestellposition WHERE bestellungId = ? AND artikelId = ?",
                bestellungId, artikelId)) {
            rs.next();
            return rs.getBigDecimal("einzelpreis");
        }
    }

    private BigDecimal anfangsPreisVonArtikel(int artikelId) throws Exception {
        try (ResultSet rs = dbConnection.execute("SELECT preis FROM artikel WHERE artikelId = ?", artikelId)) {
            rs.next();
            return rs.getBigDecimal("preis");
        }
    }
}
