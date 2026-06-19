package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.UserController;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        UserController userController = new UserController();

        var app = Javalin.create(config -> {
            config.router.get("/", ctx -> ctx.result("Hello World"));
            config.router.get("/users", userController::getAllUsers);
            config.router.get("/users/{id}", userController::getUserById);
        }).start(7070);
    }
}