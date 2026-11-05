package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BestellungDaoImpl;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.LagerbestandException;
import ch.suva.bi7.webshop.service.db.entity.*;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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
                BestellungStatusEnum.BEZAHLT, Timestamp.valueOf("2026-09-01 10:15:30"));
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(List.of(erwartet));

        BestellungEntity bestellung = new BestellungDaoImpl(jpa.factory())
                .getBestellungenNachBenutzerEmail(TEST_EMAIL).get(0);

        assertEquals(7, bestellung.getBestellungId());
        assertEquals(TEST_EMAIL, bestellung.getUserEmail());
        assertEquals(3, bestellung.getAdressId());
        assertEquals(new BigDecimal("3297.90"), bestellung.getGesamtpreis());
        assertEquals(BestellungStatusEnum.BEZAHLT, bestellung.getStatus());
        assertEquals(Timestamp.valueOf("2026-09-01 10:15:30"), bestellung.getBestelltAm());
    }

    @Test
    void bestellungErzeugenPersistiertGesamtpreisUndBestellpositionen() throws Exception {
        BigDecimal gesamtpreis = new BigDecimal("3297.90");
        List<WarenkorbEintragEntity> items = List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 2),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 6, 1));
        Map<Integer, BigDecimal> artikelPreise = Map.of(
                5, new BigDecimal("1199.00"),
                6, new BigDecimal("1000"));
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.generatedKey(7);

        assertEquals(7, new BestellungDaoImpl(jpa.factory())
                .erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3, gesamtpreis, items, artikelPreise));


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
                        List.of(EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 1)),
                        Map.of(5, new BigDecimal("1000"))));
        assertNotNull(ex.getCause());
        assertTrue(jpa.actions().contains("EntityTransaction.begin"));
        assertTrue(jpa.actions().contains("EntityTransaction.rollback"));
        assertFalse(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void erfolgreicheBestellungReduziertLagerbestandSortiertUndLeertWarenkorbInDerselbenTransaktion() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.generatedKey(7);
        List<WarenkorbEintragEntity> items = List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 6, 1),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 5, 2));

        new BestellungDaoImpl(jpa.factory()).erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3,
                new BigDecimal("3297.90"), items, Map.of(5, new BigDecimal("1199.00"), 6, new BigDecimal("899.90")));

        List<String> updates = jpa.actions().stream().filter(a -> a.startsWith("nativeQuery: UPDATE artikel")).toList();
        assertEquals(2, updates.size(), "Pro Artikel genau ein bedingtes UPDATE");
        assertTrue(updates.get(0).contains("lagerbestand >= :menge"), "Das UPDATE muss den Bestand in der WHERE-Bedingung prüfen");
        assertEquals(List.of("menge=2", "artikelId=5", "menge=1", "artikelId=6"), jpa.parameters().subList(0, 4),
                "Artikel müssen nach artikelId sortiert gesperrt werden (Deadlock-Schutz)");

        int indexLetztesUpdate = jpa.actions().lastIndexOf(updates.get(1));
        int indexErsterPersist = jpa.actions().indexOf("EntityManager.persist");
        int indexDelete = jpa.actions().indexOf("nativeQuery: DELETE FROM warenkorb_item WHERE userEmail = :email");
        int indexCommit = jpa.actions().indexOf("EntityTransaction.commit");
        assertTrue(indexLetztesUpdate < indexErsterPersist, "Erst Lager reduzieren, dann Bestellung speichern");
        assertTrue(indexErsterPersist < indexDelete && indexDelete < indexCommit,
                "Warenkorb muss VOR dem Commit (also in derselben Transaktion) geleert werden");
        assertTrue(jpa.parameters().contains("email=" + TEST_EMAIL));
    }

    @Test
    void zuWenigLagerbestandRolltGanzeBestellungZurueck() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.generatedKey(7);
        jpa.addUpdateCount(1); // Artikel 5: genug an Lager
        jpa.addUpdateCount(0); // Artikel 6: UPDATE trifft keine Zeile -> nicht genug an Lager
        List<WarenkorbEintragEntity> items = List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 2),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 6, 4));

        LagerbestandException ex = assertThrows(LagerbestandException.class,
                () -> new BestellungDaoImpl(jpa.factory()).erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3,
                        new BigDecimal("100.00"), items, Map.of(5, new BigDecimal("10"), 6, new BigDecimal("20"))));

        assertEquals(6, ex.getArtikelId(), "Die Exception muss den betroffenen Artikel nennen");
        assertTrue(jpa.actions().contains("EntityTransaction.rollback"),
                "Rollback macht auch die bereits erfolgte Reduktion von Artikel 5 rückgängig");
        assertFalse(jpa.actions().contains("EntityTransaction.commit"));
        assertTrue(jpa.persistedEntities().isEmpty(), "Es darf keine Bestellung gespeichert werden");
        assertFalse(jpa.actions().stream().anyMatch(a -> a.contains("DELETE FROM warenkorb_item")),
                "Der Warenkorb bleibt bei Abbruch erhalten");
    }

    @Test
    void ausverkaufterErsterArtikelBrichtSofortAb() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addUpdateCount(0);
        List<WarenkorbEintragEntity> items = List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 1),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 6, 1));

        LagerbestandException ex = assertThrows(LagerbestandException.class,
                () -> new BestellungDaoImpl(jpa.factory()).erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3,
                        new BigDecimal("30.00"), items, Map.of(5, new BigDecimal("10"), 6, new BigDecimal("20"))));

        assertEquals(5, ex.getArtikelId());
        assertEquals(1, jpa.actions().stream().filter(a -> a.startsWith("nativeQuery: UPDATE artikel")).count(),
                "Nach dem ersten fehlenden Artikel werden keine weiteren UPDATEs ausgeführt");
        assertTrue(jpa.actions().contains("EntityTransaction.rollback"));
    }

    @Test
    void negativeMengeWirdAbgelehntBevorLagerbestandGeaendertWird() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        WarenkorbEintragEntity manipuliert = EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 1);
        java.lang.reflect.Field menge = WarenkorbEintragEntity.class.getDeclaredField("menge");
        menge.setAccessible(true);
        menge.set(manipuliert, -3); // simuliert einen ungültigen Wert direkt aus der DB

        assertThrows(DaoException.class,
                () -> new BestellungDaoImpl(jpa.factory()).erstelleBestellungMitWarenkorbItems(TEST_EMAIL, 3,
                        new BigDecimal("10.00"), List.of(manipuliert), Map.of(5, new BigDecimal("10"))));

        assertFalse(jpa.actions().stream().anyMatch(a -> a.startsWith("nativeQuery: UPDATE artikel")),
                "Eine negative Menge würde den Lagerbestand erhöhen und darf nie ans UPDATE gehen");
        assertTrue(jpa.actions().contains("EntityTransaction.rollback"));
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new BestellungDaoImpl(null));
    }

    private BestellungEntity bestellung(int id) {
        return EntityHelper.createBestellungEntity(id, TEST_EMAIL, 3, new BigDecimal("10.00"),
                BestellungStatusEnum.OFFEN, new Timestamp(0));
    }
}
