package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.dao.BestellungDaoImpl;
import ch.suva.bi7.webshop.service.db.H2TestDatenbank;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BestellungDaoIntegrationTest {

    private static final String TEST_EMAIL = "bestellung.it@example.com";
    private EntityManagerFactory entityManagerFactory;
    private BestellungDao bestellungDao;

    @BeforeEach
    void setUp() throws Exception {
        entityManagerFactory = H2TestDatenbank.erstelleEntityManagerFactory();
        bestellungDao = new BestellungDaoImpl(entityManagerFactory);
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    void bestellungenKoennenProBenutzerGelesenWerden() throws Exception {
        List<BestellungEntity> bestellungen = bestellungDao.getBestellungenNachBenutzerEmail(TEST_EMAIL);
        assertNotNull(bestellungen);
    }
}
