package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.dao.WarenkorbDaoImpl;
import ch.suva.bi7.webshop.service.db.H2TestDatenbank;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbDaoIntegrationTest {

    private static final String TEST_EMAIL = "cart.test@example.com";
    private EntityManagerFactory entityManagerFactory;
    private WarenkorbDao warenkorbDao;

    @BeforeEach
    void setUp() throws Exception {
        entityManagerFactory = H2TestDatenbank.erstelleEntityManagerFactory();
        warenkorbDao = new WarenkorbDaoImpl(entityManagerFactory);
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    void warenkorbKannGelesenWerden() throws Exception {
        List<WarenkorbEintragEntity> items = warenkorbDao.getWarenkorbNachBenutzer(TEST_EMAIL);
        assertNotNull(items);
    }
}
