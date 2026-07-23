package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.UserController;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:8080");
                    it.allowCredentials = true;
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
        }).start(7070);
    }
}
