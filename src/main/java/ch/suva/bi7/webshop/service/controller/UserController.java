package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.*;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    static void setUserDaoMock(UserDao userDaoMock) {
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

            UserDao dao = getUserDao();

            if (dao.getUserByEMail(registerUserRequest.email).isPresent()) {
                RegisterUserResponse response = new RegisterUserResponse("error", "Benutzer existiert bereits.");
                ctx.status(409).json(response);
                return;
            }

            User newUser = new User(
                    registerUserRequest.username,
                    registerUserRequest.email,
                    registerUserRequest.password
            );
            dao.addUser(newUser);

            RegisterUserResponse response = new RegisterUserResponse("ok", null);
            ctx.status(201).json(response);
        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            ctx.status(400).json(response);
        }
    };

    public static Handler login = ctx -> {
        try {
            LoginUserRequest loginUserRequest = ctx.bodyAsClass(LoginUserRequest.class);
            System.out.println("Login: " + loginUserRequest);

            UserDao userDao = getUserDao();
//            UserSessionDao userSessionDao = null;

            Optional<User> userOptional = userDao.getUserByEMail(loginUserRequest.email);
            if (userOptional.isEmpty()) {
                LoginUserResponse response = new LoginUserResponse("error", "Benutzer existiert nicht.");
                ctx.status(409).json(response);
                return;
            }

            User user = userOptional.get();
            if (!user.password.equals(loginUserRequest.password)) {
                LoginUserResponse response = new LoginUserResponse("error", "Falsches Passwort.");
                ctx.status(409).json(response);
                return;
            }
            String sessionId = UUID.randomUUID().toString();
            // TODO Schritt 1: 'UserSessionDao' ohne DB also nur im Memory speichern.
            // TODO Schritt 2: Neue Datenbank-Tabelle um für einen User eine SessionId zu speichern
//            UserSession userSession = new UserSession(user.email, sessionId);
//            userSessionDao.saveUserSession(userSession);

            LoginUserResponse response = new LoginUserResponse("ok", null);
            ctx.status(201).json(response);
            ctx.header("sessionId", sessionId);
            //ctx.sessionAttribute("sessionId", sessionId);
            System.out.println("sessionId: " + sessionId);
        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            ctx.status(400).json(response);
        }
    };

    public static Handler logout = ctx -> {
        // TODO Logout implemenieren....
        LoginUserResponse response = new LoginUserResponse("error", "Logout: Not implemented yet");
        ctx.status(409).json(response);
    };

    public static Handler shoppingBuy = ctx -> {
        String sessionId = ctx.res().getHeader("sessionId");
        System.out.println("sessionId: " + sessionId);
        if (sessionId == null) {
            // Fehlerbeandlung: Keine SessionId vorhanden, d.h. User ist angemeldet!!!
        }

        // Im UserSessionDao die Session 'sessionId' finden und den User ermitteln'

        // Fehlerbehandlung: User nicht gefunden

        // Falls User eingeloggt: Kaufvorgang für den gefunden User abschliessen
    };
}
