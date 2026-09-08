package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.dao.ArtikelDaoImpl;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
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

        List<ArtikelEntity> artikel = dao.getAllArtikel();

        assertFalse(artikel.isEmpty());

        ArtikelEntity ersterArtikel = artikel.get(0);

        assertEquals(1, ersterArtikel.getArtikelId());
        assertEquals("iPhone 15 Pro", ersterArtikel.getName());
        assertEquals(new BigDecimal("1199.00"), ersterArtikel.getPreis());
    }

    @Test
    void mehrereArtikelWerdenGeladen() throws Exception {

        ArtikelDao dao = getDao();

        List<ArtikelEntity> artikel = dao.getAllArtikel();

        assertTrue(artikel.size() > 1, "Es müssen mehrere Artikel geladen werden");
    }


    @Test
    void addArtikelFuehrtInsertAusUndLiefertGeneriertenKey() throws Exception {
        List<SqlStatement> updates = new ArrayList<>();
        ArtikelDaoImpl testee = new ArtikelDaoImpl(createAddMockDbConnection(updates, 42));

        int artikelId = testee.addNewArtikel(
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
                () -> testee.addNewArtikel("iPhone 16 Pro", new BigDecimal("1299.00"), null),
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

        List<ArtikelEntity> artikel = dao.getAllArtikel();

        for (ArtikelEntity a : artikel) {

            assertNotNull(a.getArtikelId());
            assertNotNull(a.getName());
            assertFalse(a.getName().isBlank());

            assertNotNull(a.getPreis());
            assertTrue(a.getPreis().compareTo(BigDecimal.ZERO) > 0);

            assertTrue(a.getBild() == null || !a.getBild().isBlank());
        }
    }
}
