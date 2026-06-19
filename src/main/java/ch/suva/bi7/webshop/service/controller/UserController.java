package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.User;
import io.javalin.http.Handler;

import java.util.Optional;

public class UserController {
    public static Handler fetchAllUsernames = ctx -> {
        UserDao dao = UserDao.instance();
        Iterable<String> allUsers = dao.getAllUsernames();
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
}
