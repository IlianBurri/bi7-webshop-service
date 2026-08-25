package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.*;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        try {

            DBConnection dbConnection = new DBConnectionImpl(
                    DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());

            AdresseController adresseController = new AdresseController(new AdresseDaoImpl(dbConnection));
            WarenkorbController warenkorbController = new WarenkorbController(new WarenkorbDaoImpl(dbConnection));
            ArtikelController artikelController = new ArtikelController(new ArtikelDaoImpl(dbConnection));
            UserController userController = new UserController(new UserDaoImpl(dbConnection));

            var app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(it -> {
                        if (DBConfig.isDev()) {
                            it.reflectClientOrigin = true;
                        } else {
                            it.allowHost("http://localhost:8080", "http://127.0.0.1:8080");
                        }
                        it.allowCredentials = true;
                        it.exposeHeader("sessionId");
                    });
                });
                config.routes.get("/", ctx -> ctx.result("Hello World"));
                config.routes.get("/users", userController.fetchAllUsernames);
                config.routes.post("/users/login", userController.login);
                config.routes.post("/users/logout", userController.logout);
                config.routes.get("/users/me", userController.currentUser);
                config.routes.get("/users/{email}", userController.fetchByEMail);
                config.routes.post("/users/register", userController.register);
                config.routes.post("/shopping/buy", userController.shoppingBuy);
                config.routes.get("/artikel", artikelController.fetchAllArtikel);
                config.routes.post("/artikel", artikelController.addArtikel);

                config.routes.put("/artikel/add", artikelController.addArtikel);
                config.routes.get("/api/warenkorb/{email}", warenkorbController.getWarenkorb);
                config.routes.post("/api/warenkorb/add", warenkorbController.addToWarenkorb);
                config.routes.delete("/api/warenkorb/item/{id}", warenkorbController.deleteWarenkorbItem);
                config.routes.put("/api/warenkorb/item/{id}", warenkorbController.updateMenge);
                config.routes.get("/api/adressen/{email}", adresseController.getAdressen);
                config.routes.post("/api/adressen", adresseController.createAdresse);
                config.routes.put("/api/adressen/{adressId}", adresseController.updateAdresse);
                config.routes.delete("/api/adressen/{adressId}", adresseController.deleteAdresse);
            }).start(7070);
        } catch (Exception e) {
            System.err.println("Webshop konnte nicht gestartet werden: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
