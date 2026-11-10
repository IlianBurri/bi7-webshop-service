package ch.suva.bi7.webshop.service.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public final class JpaEntityManagerFactoryProvider {

    private JpaEntityManagerFactoryProvider() {
        // Instanzierung verhindern, da dies eine Utility-Klasse ist
    }

    public static EntityManagerFactory createEntityManagerFactory(String url, String driver, String user, String password, String hbm2ddlAuto) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", url);
        properties.put("jakarta.persistence.jdbc.user", user);
        properties.put("jakarta.persistence.jdbc.password", password);
        properties.put("jakarta.persistence.jdbc.driver", driver);
        if (hbm2ddlAuto != null) {
            properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        }

        return Persistence.createEntityManagerFactory("webshop-hibernate", properties);
    }
}

