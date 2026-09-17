package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.dao.WarenkorbDaoImpl;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.JpaEntityManagerFactoryProvider;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WarenkorbDaoIntegrationTest {

    private static final String TEST_EMAIL = "cart.test@example.com";
    private EntityManagerFactory entityManagerFactory;
    private WarenkorbDao warenkorbDao;

    @BeforeEach
    void setUp() {
        try {
            entityManagerFactory = JpaEntityManagerFactoryProvider.createEntityManagerFactory(
                    DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(),
                    DBConfig.getUser(), DBConfig.getPassword());
            warenkorbDao = new WarenkorbDaoImpl(entityManagerFactory);
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
    void warenkorbKannGelesenWerden() throws Exception {
        List<WarenkorbEintragEntity> items = warenkorbDao.getWarenkorbNachBenutzer(TEST_EMAIL);
        assertNotNull(items);
    }
}
