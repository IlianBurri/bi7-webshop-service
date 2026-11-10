package ch.suva.bi7.webshop.service.db;

import ch.suva.bi7.webshop.service.config.WebshopServiceConfig;
import jakarta.persistence.EntityManagerFactory;

public final class H2TestDatenbank {

    private static boolean migriert;

    private H2TestDatenbank() {
    }

    public static synchronized EntityManagerFactory erstelleEntityManagerFactory() throws Exception {
        String url = WebshopServiceConfig.getDBUrl();
        String driver = WebshopServiceConfig.getDBDriver();
        String user = WebshopServiceConfig.getDBUser();
        String password = WebshopServiceConfig.getDBPassword();

        if (!migriert) {
            LiquibaseMigrationRunner.migrate(url, driver, user, password);
            migriert = true;
        }
        return JpaEntityManagerFactoryProvider.createEntityManagerFactory(
                url, driver, user, password, WebshopServiceConfig.getHbm2ddlAuto());
    }
}
