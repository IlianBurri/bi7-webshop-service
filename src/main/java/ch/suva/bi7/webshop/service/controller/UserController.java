package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.RegisterUserRequest;
import ch.suva.bi7.webshop.service.model.RegisterUserResponse;
import ch.suva.bi7.webshop.service.model.User;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Optional;

public class UserController {

    private static UserDao userDao = null;

    private static UserDao getUserDao() throws Exception {
        if (userDao == null) {
            DBConnection dbConnection = new DBConnectionImpl("localhost", "webshopdb", "webshopuser","webshoppassword");
            userDao = new UserDaoImpl(dbConnection);
        }
        return userDao;
    }

    // For testing only
    private static void setUserDaoMock(UserDao userDaoMock) {
        userDao = userDaoMock;
    }

    public static Handler fetchAllUsernames = ctx -> {
        List<String> allUsers = getUserDao().getAllUsernames();
        ctx.json(allUsers);
    };

    public static Handler fetchByEMail = ctx -> {
        String email = ctx.pathParam("email");
        Optional<User> user = getUserDao().getUserByEMail(email);
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
