package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.AdresseController;
import ch.suva.bi7.webshop.service.controller.ArtikelController;
import ch.suva.bi7.webshop.service.controller.UserController;
import ch.suva.bi7.webshop.service.controller.WarenkorbController;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
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

            config.routes.get("/api/warenkorb/{email}", WarenkorbController.getWarenkorb);
            config.routes.post("/api/warenkorb/add", WarenkorbController.addToWarenkorb);
            config.routes.delete("/api/warenkorb/item/{id}", WarenkorbController.deleteWarenkorbItem);
            config.routes.put("/api/warenkorb/item/{id}", WarenkorbController.updateMenge);

            config.routes.get("/api/adressen/{email}", AdresseController.getAdressen);
            config.routes.post("/api/adressen", AdresseController.createAdresse);
            config.routes.put("/api/adressen/{adressId}", AdresseController.updateAdresse);
            config.routes.delete("/api/adressen/{adressId}", AdresseController.deleteAdresse);
        }).start(7070);
    }
}
