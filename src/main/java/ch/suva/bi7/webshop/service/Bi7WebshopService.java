package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.UserController;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
            config.routes.get("/", ctx -> ctx.result("Hello World"));
            config.routes.get("/users", UserController.fetchAllUsernames);
            config.routes.get("/users/{email}", UserController.fetchByEMail);
        }).start(7070);
    }
}
