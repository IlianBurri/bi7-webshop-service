package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.dao.ArtikelDaoImpl;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.JpaEntityManagerFactoryProvider;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.mock.*;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelDaoImplTest {


    private ArtikelDao getRealArtikelDao() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new ArtikelDaoImpl(JpaEntityManagerFactoryProvider.createEntityManagerFactory(
                DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword()));
    }

    @Test
    void einArtikelKannGeladenWerden() throws Exception {

        ArtikelDao dao = getRealArtikelDao();

        List<ArtikelEntity> artikel = dao.getAllArtikel();

        assertFalse(artikel.isEmpty());

        ArtikelEntity ersterArtikel = artikel.get(0);

        assertEquals(1, ersterArtikel.getArtikelId());
        assertEquals("iPhone 15 Pro", ersterArtikel.getName());
        assertEquals(new BigDecimal("1199.00"), ersterArtikel.getPreis());
    }

    @Test
    void mehrereArtikelWerdenGeladen() throws Exception {

        ArtikelDao dao = getRealArtikelDao();

        List<ArtikelEntity> artikel = dao.getAllArtikel();

        assertTrue(artikel.size() > 1, "Es müssen mehrere Artikel geladen werden");
    }


    @Test
    void addArtikelFuehrtInsertAusUndLiefertGeneriertenKey() throws Exception {
        List<String> actions = new ArrayList<>();
        ArtikelDao testee = new ArtikelDaoImpl(createEntityManagerFactoryMock(actions, 42, false));

        ArtikelEntity artikelEntity = testee.erstelleNeuenArtikel(
                "iPhone 16 Pro", new BigDecimal("1299.00"), "https://example.com/iphone16.jpg");

        assertEquals(42, artikelEntity.getArtikelId(), "Die generierte artikelId muss zurückgegeben werden");

        assertEquals(3, actions.size(), "Es müssen 3 Action's ausgeführt werden");
        assertTrue(actions.contains("EntityTransaction.begin"));
        assertTrue(actions.contains("EntityTransaction.commit"));
        assertTrue(actions.contains("EntityManager.persist for Id: 42"));
    }

    @Test
    void addArtikelBeiSqlFehlerWirftDaoException() {
        ArtikelDao testee = new ArtikelDaoImpl(createEntityManagerFactoryMock(new ArrayList<>(), 0, true));

        DaoException ex = assertThrows(DaoException.class,
                () -> testee.erstelleNeuenArtikel("iPhone 16 Pro", new BigDecimal("1299.00"), null),
                "SQL-Fehler müssen als DaoException nach oben propagieren");
        assertEquals("Simulierter Persist-Fehler", ex.getCause().getMessage(), "Die ursprüngliche SQLException muss als Cause erhalten bleiben");
    }

    @Test
    void alleArtikelHabenGueltigeDaten() throws Exception {

        ArtikelDao dao = getRealArtikelDao();

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

    private EntityManagerFactory createEntityManagerFactoryMock(List<String> actions, int generierterKey, boolean simulatePersistError) {
        EntityTransaction entityTransaction = new EntityTransactionMock(actions);
        EntityManager em = new EntityManagerMock(entityTransaction, actions, generierterKey, simulatePersistError);
        return new EntityManagerFactoryMock(em);
    }
}
