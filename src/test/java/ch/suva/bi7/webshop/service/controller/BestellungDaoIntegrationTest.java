package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.dao.BestellungDaoImpl;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.JpaEntityManagerFactoryProvider;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class BestellungDaoIntegrationTest {

    private static final String TEST_EMAIL = "bestellung.it@example.com";
    private EntityManagerFactory entityManagerFactory;
    private BestellungDao bestellungDao;

    @BeforeEach
    void setUp() {
        try {
            entityManagerFactory = JpaEntityManagerFactoryProvider.createEntityManagerFactory(
                    DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(),
                    DBConfig.getUser(), DBConfig.getPassword());
            bestellungDao = new BestellungDaoImpl(entityManagerFactory);
        } catch (Exception e) {
            assumeTrue(false, "MariaDB not available: " + e.getMessage());
        }
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
