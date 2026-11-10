package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.dao.BenutzerDaoImpl;
import ch.suva.bi7.webshop.service.db.H2TestDatenbank;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BenutzerDaoIntegrationTest {

    private BenutzerDao benutzerDao;
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() throws Exception {
        entityManagerFactory = H2TestDatenbank.erstelleEntityManagerFactory();
        benutzerDao = new BenutzerDaoImpl(entityManagerFactory);
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory == null) {
            return;
        }
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM user WHERE email IN (:emails)")
                    .setParameter("emails", List.of(
                            "bruce.wayne@gotham.com", "spidey@dailybugle.com",
                            "hawk.eye@arrow.com", "black.widow@avengers.com"))
                    .executeUpdate();
            em.getTransaction().commit();
        } finally {
            entityManagerFactory.close();
        }
    }

    @Test
    void speichereBenutzer() throws Exception {
        BenutzerEntity testBenutzer = new BenutzerEntity("Bruce Wayne", "bruce.wayne@gotham.com", "bruce", false);
        benutzerDao.speichereBenutzer(testBenutzer);

        BenutzerEntity gefunden = benutzerDao.holeBenutzerNachEMail("bruce.wayne@gotham.com").orElseThrow();
        assertEquals("Bruce Wayne", gefunden.getUsername());
    }

    @Test
    void holeAlleBenutzer() throws Exception {
        benutzerDao.speichereBenutzer(new BenutzerEntity("Hawk Eye", "hawk.eye@arrow.com", "target", false));
        benutzerDao.speichereBenutzer(new BenutzerEntity("Black Widow", "black.widow@avengers.com", "spider", false));

        List<BenutzerEntity> benutzer = benutzerDao.holeAlleBenutzernamen();

        assertTrue(benutzer.size() >= 2);

        assertTrue(benutzer.stream().anyMatch(user -> "Hawk Eye".equals(user.getUsername())));
        assertTrue(benutzer.stream().anyMatch(user -> "Black Widow".equals(user.getUsername())));
    }
}
