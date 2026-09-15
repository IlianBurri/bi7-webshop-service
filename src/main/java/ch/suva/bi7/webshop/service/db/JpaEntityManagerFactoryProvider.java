package ch.suva.bi7.webshop.service.db;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public final class JpaEntityManagerFactoryProvider {

    private JpaEntityManagerFactoryProvider() {
        // Instanzierung verhindern, da dies eine Utility-Klasse ist
    }

    public static EntityManagerFactory createEntityManagerFactory(String host, int port, String schema, String user, String password) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.url", buildJdbcUrl(host, port, schema));
        properties.put("jakarta.persistence.jdbc.user", user);
        properties.put("jakarta.persistence.jdbc.password", password);
        properties.put("jakarta.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");

        return Persistence.createEntityManagerFactory("webshop-hibernate", properties);
    }

    private static String buildJdbcUrl(String host, int port, String schema) {
        return "jdbc:mariadb://" + host + ":" + port + "/" + schema;
    }
}

