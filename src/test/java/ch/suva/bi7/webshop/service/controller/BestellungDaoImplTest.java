package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BestellungDaoImpl;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungStatus;
import ch.suva.bi7.webshop.service.db.entity.BestellPositionEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BestellungDaoImplTest {

    private static final String TEST_EMAIL = "bestellung.test@example.com";

    @Test
    void bestellungenProUserFilternNachEmailUndSortieren() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        new BestellungDaoImpl(jpa.factory()).getBestellungenNachBenutzerEmail(TEST_EMAIL);

        assertTrue(jpa.actions().contains("createQueryProxy"));
    }

    @Test
    void bestellungPerIdNutztFind() throws Exception {
        BestellungEntity erwartet = bestellung(42);
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.find(BestellungEntity.class, 42, erwartet);

        assertSame(erwartet, new BestellungDaoImpl(jpa.factory()).holeBestellungNachId(42).orElseThrow());
    }

    @Test
    void bestellungLesenLiefertGemappteEntity() throws Exception {
        BestellungEntity erwartet = EntityHelper.createBestellungEntity(7, TEST_EMAIL, 3, new BigDecimal("3297.90"),
                BestellungStatus.BEZAHLT, Timestamp.valueOf("2026-09-01 10:15:30"));
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(List.of(erwartet));

        BestellungEntity bestellung = new BestellungDaoImpl(jpa.factory())
                .getBestellungenNachBenutzerEmail(TEST_EMAIL).get(0);

        assertEquals(7, bestellung.getBestellungId());
        assertEquals(TEST_EMAIL, bestellung.getUserEmail());
        assertEquals(3, bestellung.getAdressId());
        assertEquals(new BigDecimal("3297.90"), bestellung.getGesamtpreis());
        assertEquals(BestellungStatus.BEZAHLT, bestellung.getStatus());
        assertEquals(Timestamp.valueOf("2026-09-01 10:15:30"), bestellung.getBestelltAm());
    }

    @Test
    void bestellungErzeugenPersistiertGesamtpreisUndBestellpositionen() throws Exception {
        BigDecimal gesamtpreis = new BigDecimal("3297.90");
        List<WarenkorbEintragEntity> items = List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 2, "iPhone 15 Pro",
                        new BigDecimal("1199.00"), "bild"),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 6, 1, "Samsung Galaxy S24",
                        new BigDecimal("899.90"), "bild"));
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.generatedKey(7);

        assertEquals(7, new BestellungDaoImpl(jpa.factory())
                .erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3, gesamtpreis, items));


        assertEquals(3, jpa.persistedEntities().size());
        assertInstanceOf(BestellungEntity.class, jpa.persistedEntities().get(0));
        assertInstanceOf(BestellPositionEntity.class, jpa.persistedEntities().get(1));
        assertInstanceOf(BestellPositionEntity.class, jpa.persistedEntities().get(2));

        BestellPositionEntity erstePosition = (BestellPositionEntity) jpa.persistedEntities().get(1);
        assertEquals(7, erstePosition.getBestellungId());
        assertEquals(5, erstePosition.getArtikelId());
        assertEquals(2, erstePosition.getAnzahl());
        assertEquals(new BigDecimal("1199.00"), erstePosition.getEinzelpreis());

        assertTrue(jpa.actions().contains("EntityManager.flush"));
        assertTrue(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void bestellungPerIdOhneTrefferLiefertEmpty() throws Exception {
        assertTrue(new BestellungDaoImpl(new EntityManagerMock().factory())
                .holeBestellungNachId(999).isEmpty());
    }

    @Test
    void datenbankFehlerWirdAlsDaoExceptionGeworfen() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.queryException(new RuntimeException("Simulierter Datenbankfehler"));

        DaoException ex = assertThrows(DaoException.class,
                () -> new BestellungDaoImpl(jpa.factory()).getBestellungenNachBenutzerEmail(TEST_EMAIL));
        assertNotNull(ex.getCause());
    }

    @Test
    void fehlerBeimBestellenFuehrtZuRollbackUndKeinemCommit() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.generatedKey(9);
        jpa.updateException(new RuntimeException("Simulierter Fehler bei der Bestellposition"));
        BestellungDaoImpl testee = new BestellungDaoImpl(jpa.factory());

        DaoException ex = assertThrows(DaoException.class,
                () -> testee.erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3, new BigDecimal("10.00"),
                        List.of(EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 1, "iPhone 15 Pro",
                                new BigDecimal("10.00"), "bild"))));
        assertNotNull(ex.getCause());
        assertTrue(jpa.actions().contains("EntityTransaction.begin"));
        assertTrue(jpa.actions().contains("EntityTransaction.rollback"));
        assertFalse(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new BestellungDaoImpl(null));
    }

    private BestellungEntity bestellung(int id) {
        return EntityHelper.createBestellungEntity(id, TEST_EMAIL, 3, new BigDecimal("10.00"),
                BestellungStatus.OFFEN, new Timestamp(0));
    }
}
