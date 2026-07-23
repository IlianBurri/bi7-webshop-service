package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Optional;

public class UserController {

    private static UserDao userDao = null;

    private static UserDao getUserDao() throws Exception {
        if (userDao == null) {
            DBConnection dbConnection = new DBConnectionImpl("localhost", "webshopdb", "webshopuser", "webshoppassword");
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
                RegisterUserResponse response = new RegisterUserResponse("error", "User already exists");
                System.out.println(response);
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
            System.out.println(response);
            ctx.status(201).json(response);
        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            System.out.println(response);
            ctx.status(400).json(response);
        }
    };

    public static Handler login = ctx -> {
        try {
            LoginUserRequest loginUserRequest = ctx.bodyAsClass(LoginUserRequest.class);
            System.out.println("Login: " + loginUserRequest.email);

            String email = ctx.sessionAttribute("userEmail");
            if (email != null) {
                if (loginUserRequest.email.equals(email)) {
                    System.out.println("Already logged in: " + email);
                } else {
                    System.out.println("Already logged in: " + email + " but trying to login as " + loginUserRequest.email);
                }
            } else {
                System.out.println("Not logged in: " + email);
            }

            UserDao userDao = getUserDao();

            Optional<User> userOptional = userDao.getUserByEMail(loginUserRequest.email);
            if (userOptional.isEmpty()) {
                LoginUserResponse response = new LoginUserResponse("error", "User does not exist: " + loginUserRequest.email, null);
                ctx.status(409).json(response);
                System.out.println(response);
                return;
            }

            User user = userOptional.get();
            if (!user.password.equals(loginUserRequest.password)) {
                LoginUserResponse response = new LoginUserResponse("error", "Wrong password for user: " + loginUserRequest.email, null);
                ctx.status(409).json(response);
                System.out.println(response);
                return;
            }

            ctx.sessionAttribute("userEmail", user.email);

            //ctx.header("sessionId", ctx.req().getSession().getId());

            LoginUserResponse response = new LoginUserResponse("ok", null, user.username);
            System.out.println(response);
            ctx.status(201).json(response);

        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            System.out.println(response);
            ctx.status(400).json(response);
        }
    };
    public static Handler logout = ctx -> {
        String email = ctx.sessionAttribute("userEmail");
        LogoutUserResponse response;
        if (email == null) {
            response = new LogoutUserResponse("ok", "Nothing to do, no user logged in");

        } else {
            ctx.req().getSession().invalidate();
            response = new LogoutUserResponse("ok", "User logout successful.");
        }
        System.out.println(response.info);
        ctx.status(200).json(response);
    };

    public static Handler shoppingBuy = ctx -> {

        String email = ctx.sessionAttribute("userEmail");
        if (email == null) {
            System.out.println("No user logged in, redirect to login");
            ctx.redirect("login.html");
        } else {
            UserDao userDao = getUserDao();
            Optional<User> userOptional = userDao.getUserByEMail(email);
            if (userOptional.isEmpty()) {
                System.out.println("User '" + email + "' not found, redirect to error page");
                ctx.redirect("error.html");
            } else {
                System.out.println("User found, finish shopping: " + userOptional.get());
                // ..... TODO: Kaufvorgang für den gefunden User abschliessen
            }
        }
    };
}
