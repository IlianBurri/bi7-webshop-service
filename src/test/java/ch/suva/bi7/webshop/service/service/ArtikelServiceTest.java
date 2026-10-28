package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import ch.suva.bi7.webshop.service.mock.ArtikelDaoMock;
import ch.suva.bi7.webshop.service.mock.FakeArtikelDao;
import ch.suva.bi7.webshop.service.model.ArtikelDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelServiceTest {

    @Test
    void artikelPerIdWirdAlsDtoGeliefert() {
        ArtikelService testee = new ArtikelService(new ArtikelDaoMock(List.of(
                EntityHelper.createArtikelEntity(5, "iPhone 15 Pro", new BigDecimal("1199.00"), "bild.png"),
                EntityHelper.createArtikelEntity(6, "Galaxy S24", new BigDecimal("899.90"), null))));

        ArtikelDto artikel = testee.getArtikelById(6);

        assertNotNull(artikel);
        assertEquals(6, artikel.getArtikelId());
        assertEquals("Galaxy S24", artikel.getName());
        assertEquals(new BigDecimal("899.90"), artikel.getPreis());
        assertNull(artikel.getBild());
    }

    @Test
    void artikelPerIdOhneTrefferLiefertNull() {
        ArtikelService testee = new ArtikelService(new ArtikelDaoMock(List.of(
                EntityHelper.createArtikelEntity(5, "iPhone 15 Pro", new BigDecimal("1199.00"), "bild.png"))));

        assertNull(testee.getArtikelById(999));
    }

    @Test
    void datenbankFehlerBeiArtikelPerIdWirdAlsRuntimeExceptionGeworfen() {
        Exception datenbankFehler = new Exception("Datenbank Fehler");
        ArtikelService testee = new ArtikelService(new FakeArtikelDao() {
            @Override
            public ArtikelEntity getArtikelById(Integer artikelId) throws Exception {
                throw datenbankFehler;
            }
        });

        RuntimeException ex = assertThrows(RuntimeException.class, () -> testee.getArtikelById(5));
        assertSame(datenbankFehler, ex.getCause());
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new ArtikelService(null));
    }
}
