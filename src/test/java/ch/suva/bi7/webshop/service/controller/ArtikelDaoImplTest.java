package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.dao.ArtikelDaoImpl;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelDaoImplTest {

    @Test
    void einArtikelKannGeladenWerden() throws Exception {
        ArtikelEntity artikel = artikel(1, "iPhone 15 Pro", new BigDecimal("1199.00"),
                "https://example.com/iphone.jpg");
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(List.of(artikel));

        ArtikelEntity erster = new ArtikelDaoImpl(jpa.factory()).getAllArtikel().get(0);

        assertEquals(1, erster.getArtikelId());
        assertEquals("iPhone 15 Pro", erster.getName());
        assertEquals(new BigDecimal("1199.00"), erster.getPreis());
    }

    @Test
    void mehrereArtikelWerdenGeladen() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(List.of(
                artikel(1, "iPhone 15 Pro", new BigDecimal("1199.00"), "bild"),
                artikel(2, "Galaxy S24", new BigDecimal("899.90"), "bild")));

        assertEquals(2, new ArtikelDaoImpl(jpa.factory()).getAllArtikel().size());
    }

    @Test
    void addArtikelFuehrtPersistMitTransaktionAusUndLiefertGeneriertenKey() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.generatedKey(42);

        ArtikelEntity artikel = new ArtikelDaoImpl(jpa.factory()).erstelleNeuenArtikel(
                "iPhone 16 Pro", new BigDecimal("1299.00"), "https://example.com/iphone16.jpg");

        assertEquals(42, artikel.getArtikelId());
        assertEquals(List.of("EntityTransaction.begin", "EntityManager.persist",
                        "EntityTransaction.commit", "EntityManager.close"),
                jpa.actions());
    }

    @Test
    void addArtikelBeiSqlFehlerWirftDaoExceptionUndRolltZurueck() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.updateException(new RuntimeException("Simulierter Persist-Fehler"));

        DaoException ex = assertThrows(DaoException.class,
                () -> new ArtikelDaoImpl(jpa.factory())
                        .erstelleNeuenArtikel("iPhone 16 Pro", new BigDecimal("1299.00"), null));

        assertEquals("Simulierter Persist-Fehler", ex.getCause().getMessage());
        assertTrue(jpa.actions().contains("EntityTransaction.rollback"));
        assertFalse(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void alleArtikelHabenGueltigeDaten() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(List.of(
                artikel(1, "iPhone 15 Pro", new BigDecimal("1199.00"), "bild"),
                artikel(2, "Galaxy S24", new BigDecimal("899.90"), null)));

        ArtikelDao dao = new ArtikelDaoImpl(jpa.factory());
        for (ArtikelEntity artikel : dao.getAllArtikel()) {
            assertNotNull(artikel.getArtikelId());
            assertNotNull(artikel.getName());
            assertFalse(artikel.getName().isBlank());
            assertNotNull(artikel.getPreis());
            assertTrue(artikel.getPreis().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(artikel.getBild() == null || !artikel.getBild().isBlank());
        }
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new ArtikelDaoImpl(null));
    }

    private ArtikelEntity artikel(int id, String name, BigDecimal preis, String bild) {
        ArtikelEntity artikel = new ArtikelEntity(name, preis, bild);
        try {
            Field field = ArtikelEntity.class.getDeclaredField("artikelId");
            field.setAccessible(true);
            field.set(artikel, id);
            return artikel;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
