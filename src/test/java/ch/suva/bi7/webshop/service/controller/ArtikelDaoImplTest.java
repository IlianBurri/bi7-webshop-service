package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.Artikel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelDaoImplTest {


    private ArtikelDao getDao() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        DBConnection dbConnection = new DBConnectionImpl(
                DBConfig.getHost(),
                DBConfig.getPort(),
                DBConfig.getSchema(),
                DBConfig.getUser(),
                DBConfig.getPassword()
        );

        return new ArtikelDaoImpl(dbConnection);
    }


    @Test
    void einArtikelKannGeladenWerden() throws Exception {

        ArtikelDao dao = getDao();

        List<Artikel> artikel = dao.getAllArtikel();

        assertFalse(artikel.isEmpty());

        Artikel ersterArtikel = artikel.get(0);

        assertEquals(1, ersterArtikel.artikelId);
        assertEquals("iPhone 15 Pro", ersterArtikel.name);
        assertEquals(new BigDecimal("1199.00"), ersterArtikel.preis);
    }


    @Test
    void mehrereArtikelWerdenGeladen() throws Exception {

        ArtikelDao dao = getDao();

        List<Artikel> artikel = dao.getAllArtikel();

        assertEquals(10, artikel.size());
    }


    @Test
    void addArtikelFuehrtInsertAusUndLiefertGeneriertenKey() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        ArtikelDaoImpl testee = new ArtikelDaoImpl(createAddMockDbConnection(updates, 42));

        int artikelId = testee.addArtikel(
                "iPhone 16 Pro", new BigDecimal("1299.00"), "https://example.com/iphone16.jpg");

        assertEquals(42, artikelId, "Die generierte artikelId muss zurückgegeben werden");
        assertEquals(1, updates.size(), "Es muss genau ein Statement ausgeführt werden");

        SqlStatement insert = updates.get(0);
        assertTrue(insert.sql().startsWith("INSERT INTO artikel"),
                "Erwartet INSERT, war: " + insert.sql());
        for (String spalte : List.of("name", "preis", "bild")) {
            assertTrue(insert.sql().contains(spalte), "INSERT muss Spalte '" + spalte + "' enthalten, war: " + insert.sql());
        }
        assertTrue(insert.sql().contains("VALUES (?, ?, ?)"),
                "Alle Werte müssen als Parameter kommen, war: " + insert.sql());
        assertEquals(
                List.of("iPhone 16 Pro", new BigDecimal("1299.00"), "https://example.com/iphone16.jpg"),
                insert.params(), "Name, Preis und Bild müssen als PreparedStatement-Parameter gebunden werden");
    }

    @Test
    void addArtikelBeiSqlFehlerWirftDaoException() {
        ArtikelDaoImpl testee = new ArtikelDaoImpl(new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) throws SQLException {
                throw new SQLException("Simulierter Datenbankfehler");
            }

            @Override
            public int executeUpdate(String sql, Object... params) throws SQLException {
                throw new SQLException("Simulierter Datenbankfehler");
            }

            @Override
            public int executeUpdateReturningGeneratedKeys(String sql, Object... params) throws SQLException {
                throw new SQLException("Simulierter Datenbankfehler");
            }

            @Override
            public void close() {
            }
        });

        DaoException ex = assertThrows(DaoException.class,
                () -> testee.addArtikel("iPhone 16 Pro", new BigDecimal("1299.00"), null),
                "SQL-Fehler müssen als DaoException nach oben propagieren");
        assertNotNull(ex.getCause(), "Die ursprüngliche SQLException muss als Cause erhalten bleiben");
    }

    private DBConnection createAddMockDbConnection(List<SqlStatement> updates, int generierterKey) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int executeUpdate(String sql, Object... params) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int executeUpdateReturningGeneratedKeys(String sql, Object... params) {
                updates.add(new SqlStatement(sql, List.of(params)));
                return generierterKey;
            }

            @Override
            public void close() {
            }
        };
    }

    @Test
    void alleArtikelHabenGueltigeDaten() throws Exception {

        ArtikelDao dao = getDao();

        List<Artikel> artikel = dao.getAllArtikel();

        for (Artikel a : artikel) {

            assertNotNull(a.artikelId);
            assertNotNull(a.name);
            assertFalse(a.name.isBlank());

            assertNotNull(a.preis);
            assertTrue(a.preis.compareTo(BigDecimal.ZERO) >= 0);

            assertNotNull(a.bild);
        }
    }
}
