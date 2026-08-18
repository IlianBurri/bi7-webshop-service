package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbDaoImplTest {

    @Test
    void warenkorbLesenLiefertItemsMitJoindaten() throws Exception {
        List<Map<String, Object>> zeilen = List.of(
                Map.of(
                        "warenkorbItemId", 1,
                        "userEmail", "test@somewhere.com",
                        "artikelId", 5,
                        "menge", 3,
                        "artikelName", "iPhone 15 Pro",
                        "artikelPreis", "1199.00",
                        "artikelBild", "https://example.com/iphone.jpg"),
                Map.of(
                        "warenkorbItemId", 2,
                        "userEmail", "test@somewhere.com",
                        "artikelId", 6,
                        "menge", 1,
                        "artikelName", "Samsung Galaxy S24",
                        "artikelPreis", "899.90",
                        "artikelBild", "https://example.com/galaxy.jpg")
        );
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(zeilen), new ArrayList<>()));

        List<WarenkorbItem> items = testee.getWarenkorbByUser("test@somewhere.com");

        assertEquals(2, items.size(), "Es sollten genau 2 Warenkorb-Items zurück gegeben werden");
        WarenkorbItem erster = items.get(0);
        assertEquals(1, erster.getWarenkorbItemId());
        assertEquals("test@somewhere.com", erster.getUserEmail());
        assertEquals(5, erster.getArtikelId());
        assertEquals(3, erster.getMenge());
        assertEquals("iPhone 15 Pro", erster.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), erster.getArtikelPreis());
        assertEquals("https://example.com/iphone.jpg", erster.getArtikelBild());

        WarenkorbDaoImpl leererTestee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>()));
        assertTrue(leererTestee.getWarenkorbByUser("test@somewhere.com").isEmpty(), "Ohne Treffer muss eine leere Liste kommen");
    }

    @Test
    void artikelHinzufuegenNutztAtomaresUpsert() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        List<SqlStatement> selects = new ArrayList<>();
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), updates, selects));

        testee.addArtikelToWarenkorb("test@somewhere.com", 5, 3);

        assertTrue(selects.isEmpty(), "Kein Check-SELECT mehr nötig – das Upsert ist atomar");
        assertEquals(1, updates.size(), "Es muss genau ein Statement ausgeführt werden");
        SqlStatement upsert = updates.get(0);
        assertTrue(upsert.sql().startsWith("INSERT INTO warenkorb_item"),
                "Erwartet INSERT, war: " + upsert.sql());
        assertTrue(upsert.sql().contains("ON DUPLICATE KEY UPDATE menge = menge + VALUES(menge)"),
                "Bei vorhandenem Item muss die Menge erhöht werden, war: " + upsert.sql());
        assertEquals(List.of("test@somewhere.com", 5, 3), upsert.params(),
                "User, Artikel und Menge müssen als PreparedStatement-Parameter gebunden werden");
    }

    @Test
    void warenkorbLesenJoinsArtikelUndFiltertNachEmail() throws Exception {
        List<SqlStatement> selects = new ArrayList<>();
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), selects));

        testee.getWarenkorbByUser("kunde@example.com");

        SqlStatement sql = selects.get(0);
        assertTrue(sql.sql().contains("JOIN artikel"), "Preis/Name/Bild müssen per JOIN aus der artikel-Tabelle kommen, war: " + sql.sql());
        assertTrue(sql.sql().contains("WHERE w.userEmail = ?"), "Email muss als Parameter gebunden werden (kein String-Einbau), war: " + sql.sql());
        assertEquals("kunde@example.com", sql.params().get(0), "Nur der Warenkorb dieses Users darf abgefragt werden");
        assertTrue(sql.sql().contains("a.preis AS artikelPreis"), "Der Preis muss aus der DB gelesen werden (nicht vom Client), war: " + sql.sql());
    }

    @Test
    void mengeAktualisierenUndItemLoeschenErzeugenSql() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), updates));

        assertTrue(testee.updateMenge(42, 9), "Bei Erfolg muss true zurückkommen");
        assertTrue(testee.deleteWarenkorbItem(42), "Bei Erfolg muss true zurückkommen");

        assertEquals(2, updates.size());
        SqlStatement update = updates.get(0);
        assertTrue(update.sql().startsWith("UPDATE warenkorb_item"), "Erwartet UPDATE, war: " + update.sql());
        assertTrue(update.sql().contains("menge = ?"), "Menge muss als Parameter kommen, war: " + update.sql());
        assertEquals(List.of(9, 42), update.params(), "Neue Menge und warenkorbItemId als Parameter");

        SqlStatement delete = updates.get(1);
        assertTrue(delete.sql().startsWith("DELETE FROM warenkorb_item"), "Erwartet DELETE, war: " + delete.sql());
        assertEquals(List.of(42), delete.params(), "warenkorbItemId als Parameter");
    }

    @Test
    void mengeAktualisierenUndLoeschenMeldenFehlendeZeilen() throws Exception {
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 0));

        assertFalse(testee.updateMenge(999, 3), "Wenn keine Zeile aktualisiert wird, muss false zurückkommen");
        assertFalse(testee.deleteWarenkorbItem(999), "Wenn keine Zeile gelöscht wird, muss false zurückkommen");
    }


    private WarenkorbDaoImpl createTestee(DBConnection dbConnection) {
        return new WarenkorbDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog) {
        return createDBConnectionMock(resultSet, updateLog, new ArrayList<>(), 1);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog, List<SqlStatement> selectLog) {
        return createDBConnectionMock(resultSet, updateLog, selectLog, 1);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog, int updateErgebnis) {
        return createDBConnectionMock(resultSet, updateLog, new ArrayList<>(), updateErgebnis);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog,
                                                List<SqlStatement> selectLog, int updateErgebnis) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) {
                selectLog.add(new SqlStatement(sql, List.of(params)));
                return resultSet;
            }

            @Override
            public int executeUpdate(String sql, Object... params) {
                updateLog.add(new SqlStatement(sql, List.of(params)));
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
