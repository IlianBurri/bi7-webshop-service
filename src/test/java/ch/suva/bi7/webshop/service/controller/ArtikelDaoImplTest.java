package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.Artikel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelDaoImplTest {


    private ArtikelDao getDao() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        DBConnection dbConnection = new DBConnectionImpl(
                "localhost",
                "webshopdb",
                "webshopuser",
                "webshoppassword"
        );

        return new ArtikelDaoImpl(dbConnection);
    }


    @Test
    void tf02_einArtikelKannGeladenWerden() throws Exception {

        ArtikelDao dao = getDao();

        List<Artikel> artikel = dao.getAllArtikel();

        assertFalse(artikel.isEmpty());

        Artikel ersterArtikel = artikel.get(0);

        assertEquals(1, ersterArtikel.artikelId);
        assertEquals("iPhone 15 Pro", ersterArtikel.name);
        assertEquals(new BigDecimal("1199.00"), ersterArtikel.preis);
    }


    @Test
    void tf03_mehrereArtikelWerdenGeladen() throws Exception {

        ArtikelDao dao = getDao();

        List<Artikel> artikel = dao.getAllArtikel();

        assertEquals(10, artikel.size());
    }


    @Test
    void tf04_alleArtikelHabenGueltigeDaten() throws Exception {

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
