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
    void artikelHinzufuegenLegtNeuesItemAnOderErhoehtMenge() throws Exception {
        List<String> updates = new ArrayList<>();
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), updates));

        testee.addArtikelToWarenkorb("test@somewhere.com", 5, 3);

        assertEquals(1, updates.size(), "Es muss genau ein UPDATE/INSERT ausgeführt werden");
        assertTrue(updates.get(0).startsWith("INSERT INTO warenkorb_item"), "Neues Item muss eingefügt werden, war: " + updates.get(0));

        List<String> updatesBeiVorhandenemItem = new ArrayList<>();
        List<Map<String, Object>> zeilen = List.of(Map.of("warenkorbItemId", 7, "menge", 2));
        WarenkorbDaoImpl testee2 = createTestee(createDBConnectionMock(createResultSetMock(zeilen), updatesBeiVorhandenemItem));

        testee2.addArtikelToWarenkorb("test@somewhere.com", 5, 3);

        String update = updatesBeiVorhandenemItem.get(0);
        assertTrue(update.startsWith("UPDATE warenkorb_item"), "Vorhandenes Item muss aktualisiert werden, war: " + update);
        assertTrue(update.contains("menge = 5"), "2 + 3 muss 5 ergeben, war: " + update);
    }

    @Test
    void mengeAktualisierenUndItemLoeschenErzeugenSql() throws Exception {
        List<String> updates = new ArrayList<>();
        WarenkorbDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), updates));

        testee.updateMenge(42, 9);
        testee.deleteWarenkorbItem(42);

        assertEquals(2, updates.size());
        assertTrue(updates.get(0).startsWith("UPDATE warenkorb_item"), "Erwartet UPDATE, war: " + updates.get(0));
        assertTrue(updates.get(0).contains("menge = 9"));
        assertTrue(updates.get(1).startsWith("DELETE FROM warenkorb_item"), "Erwartet DELETE, war: " + updates.get(1));
        assertTrue(updates.get(1).contains("warenkorbItemId = 42"));
    }


    private WarenkorbDaoImpl createTestee(DBConnection dbConnection) {
        return new WarenkorbDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<String> updateLog) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql) {
                return resultSet;
            }

            @Override
            public int executeUpdate(String sql) {
                updateLog.add(sql);
                return 1;
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
