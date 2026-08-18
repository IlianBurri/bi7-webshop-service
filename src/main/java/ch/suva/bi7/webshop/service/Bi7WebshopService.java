package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.AdresseController;
import ch.suva.bi7.webshop.service.controller.AdresseDaoImpl;
import ch.suva.bi7.webshop.service.controller.ArtikelController;
import ch.suva.bi7.webshop.service.controller.UserController;
import ch.suva.bi7.webshop.service.controller.WarenkorbController;
import ch.suva.bi7.webshop.service.controller.WarenkorbDaoImpl;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        try {

            DBConnection dbConnection = new DBConnectionImpl(
                    DBConfig.getHost(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());

            AdresseController adresseController = new AdresseController(new AdresseDaoImpl(dbConnection));
            WarenkorbController warenkorbController = new WarenkorbController(new WarenkorbDaoImpl(dbConnection));

            var app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(it -> {
                        if (DBConfig.isDev()) {
                            it.anyHost();
                        } else {
                            it.allowHost("http://localhost:8080", "http://127.0.0.1:8080");
                        }
                        it.exposeHeader("sessionId");
                    });
                });
                config.routes.get("/", ctx -> ctx.result("Hello World"));
                config.routes.get("/users", UserController.fetchAllUsernames);
                config.routes.post("/users/login", UserController.login);
                config.routes.post("/users/logout", UserController.logout);
                config.routes.get("/users/{email}", UserController.fetchByEMail);
                config.routes.post("/users/register", UserController.register);
                config.routes.post("/shopping/buy", UserController.shoppingBuy);
                config.routes.get("/artikel", ArtikelController.fetchAllArtikel);

                config.routes.get("/api/warenkorb/{email}", warenkorbController.getWarenkorb);
                config.routes.post("/api/warenkorb/add", warenkorbController.addToWarenkorb);
                config.routes.delete("/api/warenkorb/item/{id}", warenkorbController.deleteWarenkorbItem);
                config.routes.put("/api/warenkorb/item/{id}", warenkorbController.updateMenge);

                config.routes.get("/api/adressen/{email}", adresseController.getAdressen);
                config.routes.post("/api/adressen", adresseController.createAdresse);
                config.routes.put("/api/adressen/{adressId}", adresseController.updateAdresse);
                config.routes.delete("/api/adressen/{adressId}", adresseController.deleteAdresse);
            }).start(DBConfig.getPort());
        } catch (Exception e) {
            System.err.println("Webshop konnte nicht gestartet werden: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
