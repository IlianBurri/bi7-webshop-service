package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.model.Adresse;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdresseDaoImplTest {

    private static final String TEST_EMAIL = "max@example.ch";

    @Test
    void adressenLesenLiefertAdressenMitAllenFeldern() throws Exception {
        List<Map<String, Object>> zeilen = List.of(
                Map.of(
                        "adressId", 1,
                        "userEmail", TEST_EMAIL,
                        "vorname", "Max",
                        "nachname", "Muster",
                        "strasse", "Musterstrasse 1",
                        "plz", "8000",
                        "ort", "Zuerich",
                        "land", "Schweiz"),
                Map.of(
                        "adressId", 2,
                        "userEmail", TEST_EMAIL,
                        "vorname", "Anna",
                        "nachname", "Beispiel",
                        "strasse", "Testweg 2",
                        "plz", "3000",
                        "ort", "Bern",
                        "land", "Schweiz")
        );
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(zeilen), new ArrayList<>(), 1));

        List<Adresse> adressen = testee.findByUserEmail(TEST_EMAIL);

        assertEquals(2, adressen.size(), "Es sollten genau 2 Adressen zurückgegeben werden");
        Adresse erste = adressen.get(0);
        assertEquals(1, erste.adressId());
        assertEquals(TEST_EMAIL, erste.userEmail());
        assertEquals("Max", erste.vorname());
        assertEquals("Muster", erste.nachname());
        assertEquals("Musterstrasse 1", erste.strasse());
        assertEquals("8000", erste.plz());
        assertEquals("Zuerich", erste.ort());
        assertEquals("Schweiz", erste.land());

        AdresseDaoImpl leererTestee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 1));
        assertTrue(leererTestee.findByUserEmail(TEST_EMAIL).isEmpty(), "Ohne Treffer muss eine leere Liste kommen");
    }

    @Test
    void schreiboperationenErzeugenKorrekteSqlBefehle() throws Exception {
        Adresse adresse = new Adresse(0, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");
        List<Map<String, Object>> gespeicherteZeile = List.of(Map.of(
                "adressId", 7,
                "userEmail", TEST_EMAIL,
                "vorname", "Max",
                "nachname", "Muster",
                "strasse", "Musterstrasse 1",
                "plz", "8000",
                "ort", "Zuerich",
                "land", "Schweiz"));
        List<String> updates = new ArrayList<>();
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(gespeicherteZeile), updates, 1));

        Adresse gespeichert = testee.insert(adresse);
        assertEquals(7, gespeichert.adressId(), "Die vergebene adressId muss zurückgegeben werden");
        assertTrue(updates.get(0).startsWith("INSERT INTO adresse"), "Erwartet INSERT, war: " + updates.get(0));

        assertTrue(testee.update(7, adresse), "Bei Erfolg muss true zurückkommen");
        assertTrue(updates.get(1).startsWith("UPDATE adresse"), "Erwartet UPDATE, war: " + updates.get(1));
        assertTrue(updates.get(1).contains("adressId = 7"));

        assertTrue(testee.delete(7), "Bei Erfolg muss true zurückkommen");
        assertTrue(updates.get(2).startsWith("DELETE FROM adresse"), "Erwartet DELETE, war: " + updates.get(2));
        assertTrue(updates.get(2).contains("adressId = 7"));
    }

    @Test
    void aktualisierenUndLoeschenMeldenFehlendeZeilen() throws Exception {
        Adresse adresse = new Adresse(0, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 0));

        assertFalse(testee.update(999, adresse), "Wenn keine Zeile aktualisiert wird, muss false zurückkommen");
        assertFalse(testee.delete(999), "Wenn keine Zeile gelöscht wird, muss false zurückkommen");
    }


    private AdresseDaoImpl createTestee(DBConnection dbConnection) {
        return new AdresseDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<String> updateLog, int updateErgebnis) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql) {
                return resultSet;
            }

            @Override
            public int executeUpdate(String sql) {
                updateLog.add(sql);
                return updateErgebnis;
            }

            @Override
            public void close() {
            }
        };
    }

    private ResultSet createResultSetMock(List<Map<String, Object>> result) {
        return new ResultSetMock(result);
    }
}
