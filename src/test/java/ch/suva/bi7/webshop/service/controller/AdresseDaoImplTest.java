package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.AdresseDaoImpl;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdresseDaoImplTest {

    private static final String TEST_EMAIL = "max@example.ch";
    private static final AdresseEntity BEISPIEL_ADRESSE =
            new AdresseEntity(null, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");

    @Test
    void adressenLesenLiefertAdressenMitAllenFeldern() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(List.of(
                new AdresseEntity(1, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz"),
                new AdresseEntity(2, TEST_EMAIL, "Anna", "Beispiel", "Testweg 2", "3000", "Bern", "Schweiz")));

        List<AdresseEntity> adressen = new AdresseDaoImpl(jpa.factory()).ladeAdressenNachBenutzerEmail(TEST_EMAIL);

        assertEquals(2, adressen.size());
        AdresseEntity erste = adressen.get(0);
        assertEquals(1, erste.getAdressId());
        assertEquals(TEST_EMAIL, erste.getUserEmail());
        assertEquals("Max", erste.getVorname());
        assertEquals("Muster", erste.getNachname());
        assertEquals("Musterstrasse 1", erste.getStrasse());
        assertEquals("8000", erste.getPlz());
        assertEquals("Zuerich", erste.getOrt());
        assertEquals("Schweiz", erste.getLand());
    }

    @Test
    void adressenLesenOhneTrefferLiefertLeereListe() throws Exception {
        assertTrue(new AdresseDaoImpl(new EntityManagerMock().factory())
                .ladeAdressenNachBenutzerEmail(TEST_EMAIL).isEmpty());
    }

    @Test
    void insertPersistiertAdresseUndNutztGeneriertenKey() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        AdresseEntity gespeichert = new AdresseDaoImpl(jpa.factory()).insert(BEISPIEL_ADRESSE);

        assertSame(BEISPIEL_ADRESSE, gespeichert);
        assertTrue(jpa.actions().contains("EntityTransaction.begin"));
        assertTrue(jpa.actions().contains("EntityManager.persist"));
        assertTrue(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void updateErzeugtUpdateMitAllenFeldernUndAdressId() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        assertTrue(new AdresseDaoImpl(jpa.factory()).aktualisiere(7, BEISPIEL_ADRESSE));

        assertTrue(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void deleteFindetAdresseUndEntferntSie() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.find(AdresseEntity.class, 7, BEISPIEL_ADRESSE);

        assertTrue(new AdresseDaoImpl(jpa.factory()).loesche(7));
        assertTrue(jpa.actions().contains("EntityTransaction.begin"));
        assertTrue(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void existsIdenticalLiefertTrueBeiTreffer() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(1L);

        assertTrue(new AdresseDaoImpl(jpa.factory()).existiertIdentischeAdresse(BEISPIEL_ADRESSE));
        assertTrue(jpa.actions().contains("createQueryProxy"));
    }

    @Test
    void existsIdenticalLiefertFalseOhneTreffer() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(0L);
        assertFalse(new AdresseDaoImpl(jpa.factory()).existiertIdentischeAdresse(BEISPIEL_ADRESSE));
    }

    @Test
    void boesartigeEingabeWirdAlsParameterGebunden() throws Exception {
        String boeseEingabe = "max' OR '1'='1";
        EntityManagerMock jpa = new EntityManagerMock();

        new AdresseDaoImpl(jpa.factory()).ladeAdressenNachBenutzerEmail(boeseEingabe);

        assertTrue(jpa.actions().contains("createQueryProxy"));
    }

    @Test
    void aktualisierenUndLoeschenMeldenFehlendeZeilen() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addUpdateCount(0);
        AdresseDaoImpl testee = new AdresseDaoImpl(jpa.factory());
        assertFalse(testee.aktualisiere(999, BEISPIEL_ADRESSE));

        EntityManagerMock noMatch = new EntityManagerMock();
        assertFalse(new AdresseDaoImpl(noMatch.factory()).loesche(999));
    }

    @Test
    void datenbankFehlerWirdAlsDaoExceptionGeworfen() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.queryException(new RuntimeException("Simulierter Datenbankfehler"));

        DaoException ex = assertThrows(DaoException.class,
                () -> new AdresseDaoImpl(jpa.factory()).ladeAdressenNachBenutzerEmail(TEST_EMAIL));
        assertNotNull(ex.getCause());
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new AdresseDaoImpl(null));
    }
}
