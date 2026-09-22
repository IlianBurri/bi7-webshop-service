package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.dao.WarenkorbDaoImpl;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbDaoImplTest {

    @Test
    void warenkorbLesenLiefertItemsMitJoindaten() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        List<WarenkorbEintragEntity> tuples = List.of(
                new WarenkorbEintragEntity(1, "test@somewhere.com", 5, 3, "iPhone 15 Pro",
                        new BigDecimal("1199.00"), "https://example.com/iphone.jpg"),
                new WarenkorbEintragEntity(2, "test@somewhere.com", 6, 1, "Samsung Galaxy S24",
                        new BigDecimal("899.90"), "https://example.com/galaxy.jpg"));
        jpa.addResult(tuples);

        WarenkorbDao testee = new WarenkorbDaoImpl(jpa.factory());

        List<WarenkorbEintragEntity> items = testee.getWarenkorbNachBenutzer("test@somewhere.com");

        assertEquals(2, items.size());
        WarenkorbEintragEntity erster = items.get(0);
        assertEquals(1, erster.getWarenkorbItemId());
        assertEquals("test@somewhere.com", erster.getUserEmail());
        assertEquals(5, erster.getArtikelId());
        assertEquals(3, erster.getMenge());
        assertEquals("iPhone 15 Pro", erster.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), erster.getArtikelPreis());
        assertEquals("https://example.com/iphone.jpg", erster.getArtikelBild());

        EntityManagerMock leer = new EntityManagerMock();
        assertTrue(new WarenkorbDaoImpl( leer.factory())
                .getWarenkorbNachBenutzer("test@somewhere.com").isEmpty());
    }

    @Test
    void artikelHinzufuegenNutztAtomaresUpsert() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();

        new WarenkorbDaoImpl(jpa.factory())
                .fuegeArtikelZuWarenkorbHinzu("test@somewhere.com", 5, 3);

        assertTrue(jpa.actions().contains("EntityTransaction.begin"));
        assertTrue(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void warenkorbLesenJoinsArtikelUndFiltertNachEmail() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();

        new WarenkorbDaoImpl(jpa.factory()).getWarenkorbNachBenutzer("kunde@example.com");

        assertTrue(jpa.actions().contains("createQueryProxy"));
    }

    @Test
    void mengeAktualisierenUndItemLoeschenErzeugenSql() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();

        assertTrue(new WarenkorbDaoImpl(jpa.factory()).aktualisiereMenge(42, 9));
        assertTrue(new WarenkorbDaoImpl(jpa.factory()).loescheWarenkorbEintrag(42));

        assertTrue(jpa.actions().contains("EntityManager.close"));
    }

    @Test
    void mengeAktualisierenUndLoeschenMeldenFehlendeZeilen() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addUpdateCount(0);
        jpa.addUpdateCount(0);
        WarenkorbDaoImpl testee = new WarenkorbDaoImpl(jpa.factory());

        assertFalse(testee.aktualisiereMenge(999, 3));
        assertFalse(testee.loescheWarenkorbEintrag(999));
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new WarenkorbDaoImpl(null));
    }

}
