package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.model.Bestellung;
import org.junit.jupiter.api.Test;

import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BestellungDaoImplTest {

    private static final String TEST_EMAIL = "bestellung.test@example.com";

    @Test
    void bestellungenProUserFilternNachSchemaSpalteUserEmail() throws Exception {
        List<SqlStatement> selects = new ArrayList<>();
        BestellungDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), selects));

        testee.getBestellungenByUserEmail(TEST_EMAIL);

        SqlStatement sql = selects.get(0);
        assertTrue(sql.sql().contains("WHERE userEmail = ?"),
                "Spaltenname muss dem Schema entsprechen (userEmail), war: " + sql.sql());
        assertTrue(sql.sql().contains("ORDER BY bestelldatum DESC"),
                "Neueste Bestellung zuerst (Spalte bestelldatum), war: " + sql.sql());
        assertFalse(sql.sql().contains("user_email"),
                "Der alte snake_case-Spaltenname darf nicht mehr verwendet werden, war: " + sql.sql());
        assertFalse(sql.sql().contains("bestellt_am"),
                "Der alte snake_case-Spaltenname darf nicht mehr verwendet werden, war: " + sql.sql());
        assertEquals(TEST_EMAIL, sql.params().get(0),
                "Nur die Bestellungen dieses Users dürfen abgefragt werden");
    }

    @Test
    void bestellungPerIdNutztSchemaSpalteBestellungId() throws Exception {
        List<SqlStatement> selects = new ArrayList<>();
        BestellungDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), selects));

        testee.getBestellungById(42);

        SqlStatement sql = selects.get(0);
        assertTrue(sql.sql().contains("WHERE bestellungId = ?"),
                "Spaltenname muss dem Schema entsprechen (bestellungId), war: " + sql.sql());
        assertEquals(List.of(42), sql.params(), "bestellungId muss als Parameter gebunden werden");
    }

    @Test
    void bestellungLesenMapptZeilenMitSchemaSpaltennamen() throws Exception {
        List<Map<String, Object>> zeilen = List.of(Map.of(
                "bestellungId", 7,
                "userEmail", TEST_EMAIL,
                "adressId", 3,
                "gesamtpreis", "3297.90",
                "status", "BEZAHLT",
                "bestelldatum", "2026-09-01 10:15:30"));
        BestellungDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(zeilen), new ArrayList<>(), new ArrayList<>()));

        List<Bestellung> bestellungen = testee.getBestellungenByUserEmail(TEST_EMAIL);

        assertEquals(1, bestellungen.size(), "Es sollte genau 1 Bestellung zurückgegeben werden");
        Bestellung bestellung = bestellungen.get(0);
        assertEquals(7, bestellung.getBestellungId());
        assertEquals(TEST_EMAIL, bestellung.getUserEmail());
        assertEquals(3, bestellung.getAdressId());
        assertEquals(new BigDecimal("3297.90"), bestellung.getGesamtpreis(),
                "Der gespeicherte Gesamtpreis muss zurückgelesen werden");
        assertEquals("BEZAHLT", bestellung.getStatus());
        assertEquals(Timestamp.valueOf("2026-09-01 10:15:30"), bestellung.getBestelltAm(),
                "bestelldatum muss auf bestelltAm gemappt werden");
    }

    @Test
    void bestellungErzeugenPersistiertGesamtpreisUndBestellpositionen() throws Exception {
        BigDecimal gesamtpreis = new BigDecimal("1199.00").multiply(BigDecimal.valueOf(2))
                .add(new BigDecimal("899.90"));
        List<WarenkorbItem> items = List.of(
                new WarenkorbItem(1, TEST_EMAIL, 5, 2, "iPhone 15 Pro", new BigDecimal("1199.00"), "bild"),
                new WarenkorbItem(2, TEST_EMAIL, 6, 1, "Samsung Galaxy S24", new BigDecimal("899.90"), "bild"));
        List<SqlStatement> updates = new ArrayList<>();
        List<SqlStatement> selects = new ArrayList<>();
        BestellungDaoImpl testee = createTestee(createDBConnectionMock(
                createResultSetMock(List.of()), updates, selects, 7));

        int bestellungId = testee.createBestellungWithItems(TEST_EMAIL, 3, gesamtpreis, items);

        assertEquals(7, bestellungId, "Der generierte Key der Bestellung muss zurückkommen");
        assertEquals(4, updates.size(), "1 INSERT bestellung + 2 INSERT bestellposition + 1 DELETE warenkorb");

        SqlStatement insertBestellung = updates.get(0);
        assertTrue(insertBestellung.sql().startsWith("INSERT INTO bestellung"), "Erwartet INSERT, war: " + insertBestellung.sql());
        assertTrue(insertBestellung.sql().contains("gesamtpreis"),
                "Der Gesamtpreis muss gespeichert werden, war: " + insertBestellung.sql());
        assertEquals(List.of(TEST_EMAIL, 3, gesamtpreis), insertBestellung.params(),
                "userEmail, adressId und gesamtpreis müssen als Parameter gebunden werden");

        SqlStatement erstePosition = updates.get(1);
        assertTrue(erstePosition.sql().startsWith("INSERT INTO bestellposition"), "Erwartet INSERT, war: " + erstePosition.sql());
        assertEquals(List.of(7, 5, 2, new BigDecimal("1199.00")), erstePosition.params(),
                "bestellungId, artikelId, anzahl und einzelpreis als Parameter");
        SqlStatement zweitePosition = updates.get(2);
        assertEquals(List.of(7, 6, 1, new BigDecimal("899.90")), zweitePosition.params(),
                "Pro Warenkorb-Item muss eine Bestellposition entstehen");

        SqlStatement leeren = updates.get(3);
        assertTrue(leeren.sql().startsWith("DELETE FROM warenkorb_item"), "Erwartet DELETE, war: " + leeren.sql());
        assertEquals(List.of(TEST_EMAIL), leeren.params(), "Warenkorb des Users muss geleert werden");
        assertTrue(selects.isEmpty(), "Nach dem Bestellen darf kein weiteres SELECT nötig sein");
    }

    @Test
    void bestellungPerIdOhneTrefferLiefertEmpty() throws Exception {
        BestellungDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), new ArrayList<>()));

        Optional<Bestellung> ergebnis = testee.getBestellungById(999);

        assertTrue(ergebnis.isEmpty(), "Ohne Treffer muss Optional.empty kommen");
    }

    @Test
    void datenbankFehlerWirdAlsDaoExceptionGeworfen() {
        BestellungDaoImpl testee = createTestee(createThrowingDBConnectionMock());

        DaoException ex = assertThrows(DaoException.class,
                () -> testee.getBestellungenByUserEmail(TEST_EMAIL),
                "SQL-Fehler müssen als DaoException (nicht als generisches Exception) nach oben propagieren");
        assertNotNull(ex.getCause(), "Die ursprüngliche SQLException muss als Cause erhalten bleiben");
    }


    private BestellungDaoImpl createTestee(DBConnection dbConnection) {
        return new BestellungDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog,
                                                List<SqlStatement> selectLog) {
        return createDBConnectionMock(resultSet, updateLog, selectLog, 1);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog,
                                                List<SqlStatement> selectLog, int generierterKey) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) {
                selectLog.add(new SqlStatement(sql, List.of(params)));
                return resultSet;
            }

            @Override
            public int executeUpdate(String sql, Object... params) {
                updateLog.add(new SqlStatement(sql, List.of(params)));
                return 1;
            }

            @Override
            public int executeUpdateReturningGeneratedKeys(String sql, Object... params) throws SQLException {
                updateLog.add(new SqlStatement(sql, List.of(params)));
                return generierterKey;
            }

            @Override
            public void beginTransaction() throws SQLException {
                // Erfolgspfad: Transaktion einfach zulassen
            }

            @Override
            public void commit() throws SQLException {
                // Erfolgspfad: Commit zulassen
            }

            @Override
            public void rollback() throws SQLException {
                // Erfolgspfad: kein Rollback noetig
            }

            @Override
            public void close() {
            }
        };
    }

    @Test
    void fehlerBeimBestellenFuehrtZuRollbackUndKeinemCommit() {
        int[] transaktion = new int[3]; // index 0 = begin, 1 = commit, 2 = rollback
        BestellungDaoImpl testee = new BestellungDaoImpl(new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) {
                return createResultSetMock(List.of());
            }

            @Override
            public int executeUpdate(String sql, Object... params) throws SQLException {
                throw new SQLException("Simulierter Fehler bei der Bestellposition");
            }

            @Override
            public int executeUpdateReturningGeneratedKeys(String sql, Object... params) {
                return 9;
            }

            @Override
            public void beginTransaction() {
                transaktion[0]++;
            }

            @Override
            public void commit() {
                transaktion[1]++;
            }

            @Override
            public void rollback() {
                transaktion[2]++;
            }

            @Override
            public void close() {
            }
        });

        DaoException ex = assertThrows(DaoException.class,
                () -> testee.createBestellungWithItems(TEST_EMAIL, 3, new BigDecimal("10.00"),
                        List.of(new WarenkorbItem(1, TEST_EMAIL, 5, 1, "iPhone 15 Pro", new BigDecimal("10.00"), "bild"))),
                "SQL-Fehler müssen als DaoException geworfen werden");
        assertNotNull(ex.getCause(), "Die ursprüngliche SQLException muss erhalten bleiben");
        assertEquals(1, transaktion[0], "Die Transaktion muss begonnen werden");
        assertEquals(0, transaktion[1], "Bei Fehler darf nicht committet werden");
        assertEquals(1, transaktion[2], "Bei Fehler muss zurückgerollt werden");
    }

    private DBConnection createThrowingDBConnectionMock() {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) throws SQLException {
                throw new SQLException("Simulierter Datenbankfehler");
            }

            @Override
            public int executeUpdate(String sql, Object... params) throws SQLException {
                throw new SQLException("Simulierter Datenbankfehler");
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
