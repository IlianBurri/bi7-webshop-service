package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.model.Adresse;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdresseDaoImplTest {

    private static final String TEST_EMAIL = "max@example.ch";

    private static final Adresse BEISPIEL_ADRESSE =
            new Adresse(0, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");

    private static final int GENERIERTER_KEY = 42;

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
    }

    @Test
    void adressenLesenOhneTrefferLiefertLeereListe() throws Exception {
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 1));

        assertTrue(testee.findByUserEmail(TEST_EMAIL).isEmpty(), "Ohne Treffer muss eine leere Liste kommen");
    }

    @Test
    void insertErzeugtInsertUndNutztGeneriertenKeyAusJdbc() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        List<SqlStatement> selects = new ArrayList<>();
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(
                createResultSetMock(List.of()), updates, 1, selects, GENERIERTER_KEY));

        Adresse gespeichert = testee.insert(BEISPIEL_ADRESSE);

        assertEquals(GENERIERTER_KEY, gespeichert.adressId(),
                "Die adressId muss direkt aus dem JDBC-generierten Key kommen");
        assertTrue(selects.isEmpty(),
                "Nach dem INSERT darf kein SELECT mehr zum Wiederfinden nötig sein (Race-Condition-Fix)");

        SqlStatement insert = updates.get(0);
        assertTrue(insert.sql().startsWith("INSERT INTO adresse"), "Erwartet INSERT, war: " + insert.sql());
        for (String spalte : List.of("userEmail", "vorname", "nachname", "strasse", "plz", "ort", "land")) {
            assertTrue(insert.sql().contains(spalte), "INSERT muss Spalte '" + spalte + "' enthalten, war: " + insert.sql());
        }
        assertTrue(insert.sql().contains("VALUES (?, ?, ?, ?, ?, ?, ?)"),
                "Alle Werte müssen als Parameter kommen, war: " + insert.sql());
        assertEquals(
                List.of(TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz"),
                insert.params(), "Alle Felder müssen als PreparedStatement-Parameter gebunden werden");
    }

    @Test
    void updateErzeugtUpdateMitAllenFeldernUndAdressId() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), updates, 1));

        assertTrue(testee.update(7, BEISPIEL_ADRESSE), "Bei Erfolg muss true zurückkommen");

        SqlStatement update = updates.get(0);
        assertTrue(update.sql().startsWith("UPDATE adresse"), "Erwartet UPDATE, war: " + update.sql());
        assertTrue(update.sql().contains("SET userEmail = ?, vorname = ?, nachname = ?, strasse = ?, plz = ?, ort = ?, land = ?"),
                "Alle Felder müssen gesetzt werden, war: " + update.sql());
        assertTrue(update.sql().contains("WHERE adressId = ?"), "WHERE muss auf der adressId liegen, war: " + update.sql());
        assertEquals(
                List.of(TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz", 7),
                update.params(), "7 Felder + adressId müssen als Parameter gebunden werden");
    }

    @Test
    void deleteErzeugtDeleteMitAdressId() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), updates, 1));

        assertTrue(testee.delete(7), "Bei Erfolg muss true zurückkommen");

        SqlStatement delete = updates.get(0);
        assertTrue(delete.sql().startsWith("DELETE FROM adresse"), "Erwartet DELETE, war: " + delete.sql());
        assertTrue(delete.sql().contains("WHERE adressId = ?"), "DELETE muss die adressId treffen, war: " + delete.sql());
        assertEquals(List.of(7), delete.params(), "adressId muss als Parameter gebunden werden");
    }

    @Test
    void existsIdenticalLiefertTrueBeiTreffer() throws Exception {
        List<SqlStatement> selects = new ArrayList<>();
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(
                createResultSetMock(List.of(Map.of("adressId", 5))), new ArrayList<>(), 1, selects));

        assertTrue(testee.existsIdentical(BEISPIEL_ADRESSE), "Bei vorhandenem Treffer muss true kommen");

        SqlStatement sql = selects.get(0);
        assertTrue(sql.sql().contains("FROM adresse"), "Erwartet SELECT auf adresse, war: " + sql.sql());
        assertTrue(sql.sql().contains("userEmail = ? AND vorname = ? AND nachname = ? AND strasse = ? " +
                        "AND plz = ? AND ort = ? AND land = ?"),
                "Alle Felder müssen als Bedingung gebunden werden, war: " + sql.sql());
        assertEquals(
                List.of(TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz"),
                sql.params(), "Die Felder müssen als Parameter gebunden werden");
    }

    @Test
    void existsIdenticalLiefertFalseOhneTreffer() throws Exception {
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 1));

        assertFalse(testee.existsIdentical(BEISPIEL_ADRESSE), "Ohne Treffer muss false kommen");
    }

    @Test
    void boesartigeEingabeMitApostrophWirdAlsParameterGebundenUndVerlaesstSqlNicht() throws Exception {
        String boeseEingabe = "max' OR '1'='1";
        List<SqlStatement> selects = new ArrayList<>();
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 1, selects));

        testee.findByUserEmail(boeseEingabe);

        SqlStatement sql = selects.get(0);
        assertFalse(sql.sql().contains(boeseEingabe), "Der Eingabewert darf nicht im SQL-String landen, war: " + sql.sql());
        assertTrue(sql.sql().contains("userEmail = ?"), "Email muss über einen Platzhalter gebunden werden, war: " + sql.sql());
        assertEquals(boeseEingabe, sql.params().get(0), "Der Wert muss als Parameter übergeben und so neutralisiert werden");
    }

    @Test
    void aktualisierenUndLoeschenMeldenFehlendeZeilen() throws Exception {
        AdresseDaoImpl testee = createTestee(createDBConnectionMock(createResultSetMock(List.of()), new ArrayList<>(), 0));

        assertFalse(testee.update(999, BEISPIEL_ADRESSE), "Wenn keine Zeile aktualisiert wird, muss false zurückkommen");
        assertFalse(testee.delete(999), "Wenn keine Zeile gelöscht wird, muss false zurückkommen");
    }

    @Test
    void datenbankFehlerWirdAlsDaoExceptionGeworfen() {
        AdresseDaoImpl testee = createTestee(createThrowingDBConnectionMock());

        DaoException ex = assertThrows(DaoException.class,
                () -> testee.findByUserEmail(TEST_EMAIL),
                "SQL-Fehler müssen als DaoException (nicht als generisches Exception) nach oben propagieren");
        assertNotNull(ex.getCause(), "Die ursprüngliche SQLException muss als Cause erhalten bleiben");
    }


    private AdresseDaoImpl createTestee(DBConnection dbConnection) {
        return new AdresseDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog, int updateErgebnis) {
        return createDBConnectionMock(resultSet, updateLog, updateErgebnis, new ArrayList<>(), GENERIERTER_KEY);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog, int updateErgebnis,
                                                List<SqlStatement> selectLog) {
        return createDBConnectionMock(resultSet, updateLog, updateErgebnis, selectLog, GENERIERTER_KEY);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, List<SqlStatement> updateLog, int updateErgebnis,
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
                return updateErgebnis;
            }

            @Override
            public int executeUpdateReturningGeneratedKeys(String sql, Object... params) throws SQLException {
                updateLog.add(new SqlStatement(sql, List.of(params)));
                return generierterKey;
            }

            @Override
            public void close() {
            }
        };
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
