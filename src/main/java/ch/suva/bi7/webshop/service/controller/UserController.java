package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.RegisterUserRequest;
import ch.suva.bi7.webshop.service.model.RegisterUserResponse;
import ch.suva.bi7.webshop.service.model.User;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Optional;

public class UserController {
    public static Handler fetchAllUsernames = ctx -> {
        UserDao dao = UserDao.instance();
        List<String> allUsers = dao.getAllUsernames();
        ctx.json(allUsers);
    };

    public static Handler fetchByEMail = ctx -> {
        String email = ctx.pathParam("email");
        UserDao dao = UserDao.instance();
        Optional<User> user = dao.getUserByEMail(email);
        if (user.isPresent()) {
            ctx.json(user.get());
        } else {
            ctx.status(404).result("Not Found: '" + email + "'\n");
        }
    };

    public static Handler register = ctx -> {
        try {
            RegisterUserRequest registerUserRequest = ctx.bodyAsClass(RegisterUserRequest.class);
            System.out.println("Register: " + registerUserRequest);

            // TODO: UserDao verwenden und anhand EMail prüfen ob bereits registriert.
            // Falls Ja: Fehler liefern.
            // Falls Nein: User-Instanz aus dem RegisterRequest erstellen und dann UserDao.addUser(user) durchführen

            RegisterUserResponse response = new RegisterUserResponse("ok", null);
            ctx.status(201).json(response);
        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            ctx.status(400).json(response);
        }
    };
}
