package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDaoImpl;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BenutzerDaoImplTest {

    @Test
    void getBenutzerByEMailLiefertBenutzer() {
        BenutzerEntity erwartet = new BenutzerEntity("testBenutzer", "test@somewhere.com", "test", false);
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(erwartet);

        Optional<BenutzerEntity> benutzer = new BenutzerDaoImpl(jpa.factory())
                .holeBenutzerNachEMail("test@somewhere.com");

        assertTrue(benutzer.isPresent());
        assertSame(erwartet, benutzer.get());
        assertEquals("test@somewhere.com", jpa.queries().get(0).parameters().get("email"));
    }

    @Test
    void getBenutzerByEMailLiefertAdminStatus() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(new BenutzerEntity("admin", "admin@somewhere.com", "admin", true));

        BenutzerEntity benutzer = new BenutzerDaoImpl(jpa.factory())
                .holeBenutzerNachEMail("admin@somewhere.com").orElseThrow();

        assertTrue(benutzer.isAdmin());
    }

    @Test
    void getBenutzerByEMailOhneAdminFlagLiefertFalse() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(new BenutzerEntity("testBenutzer", "test@somewhere.com", "test", false));

        assertFalse(new BenutzerDaoImpl(jpa.factory())
                .holeBenutzerNachEMail("test@somewhere.com").orElseThrow().isAdmin());
    }

    @Test
    void getAllUsersLiefertAlleBenutzer() {
        List<BenutzerEntity> erwartet = List.of(
                new BenutzerEntity("testBenutzer", "test@somewhere.com", "test", false),
                new BenutzerEntity("testuser2", "test2@somewhere.com", "test2", false),
                new BenutzerEntity("testuser3", "test3@somewhere.com", "test3", false));
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(erwartet);

        List<BenutzerEntity> ergebnis = new BenutzerDaoImpl(jpa.factory()).holeAlleBenutzernamen();

        assertEquals(erwartet, ergebnis);
        assertEquals("SELECT b FROM BenutzerEntity b", jpa.queries().get(0).sql());
    }

    @Test
    void unbekannteEmailLiefertEmpty() {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addResult(new NoResultException());

        assertTrue(new BenutzerDaoImpl(jpa.factory())
                .holeBenutzerNachEMail("nicht@vorhanden.example").isEmpty());
    }

    @Test
    void leereEmailLiefertEmptyOhneQuery() {
        EntityManagerMock jpa = new EntityManagerMock();

        assertTrue(new BenutzerDaoImpl(jpa.factory()).holeBenutzerNachEMail(" ").isEmpty());
        assertTrue(jpa.queries().isEmpty());
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new BenutzerDaoImpl(null));
    }
}
