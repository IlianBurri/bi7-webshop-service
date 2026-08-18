package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.*;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static UserDao userDao = null;

    public UserController(UserDao userDao) {
        if (userDao == null) {
            throw new IllegalArgumentException("userDao must not be null");
        }
        this.userDao = userDao;
    }

    private static UserDao getUserDao() throws Exception {
        return userDao;
    }

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
            logger.info("Register: username={}, email={}", registerUserRequest.username, registerUserRequest.email);

            UserDao dao = getUserDao();

            if (dao.getUserByEMail(registerUserRequest.email).isPresent()) {
                RegisterUserResponse response = new RegisterUserResponse("error", "User already exists");
                logger.info("Register abgelehnt: {}", response);
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
            logger.info("Register erfolgreich: {}", response);
            ctx.status(201).json(response);
        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            logger.error("Register fehlgeschlagen: {}", e.getMessage(), e);
            ctx.status(400).json(response);
        }
    };

    public static Handler login = ctx -> {
        try {
            LoginUserRequest loginUserRequest = ctx.bodyAsClass(LoginUserRequest.class);
            logger.info("Login: {}", loginUserRequest.email);

            String email = ctx.sessionAttribute("userEmail");
            UserDao userDao = getUserDao();

            if (email != null) {
                if (loginUserRequest.email.equals(email)) {
                    Optional<User> userOptional = userDao.getUserByEMail(email);
                    String realUsername = userOptional.map(u -> u.username).orElse(email);

                    LoginUserResponse response = new LoginUserResponse(
                            "info", "Du bist bereits als " + realUsername + " eingeloggt.", realUsername
                    );
                    logger.info("Bereits eingeloggt: {}", response);
                    ctx.status(200).json(response);
                    return;
                } else {
                    LoginUserResponse response = new LoginUserResponse(
                            "error", "Es ist bereits ein anderer User (" + email + ") in dieser Session eingeloggt. Bitte zuerst ausloggen.", null
                    );
                    logger.info("Login-Konflikt: {}", response);
                    ctx.status(409).json(response);
                    return;
                }
            }

            Optional<User> userOptional = userDao.getUserByEMail(loginUserRequest.email);
            if (userOptional.isEmpty()) {
                LoginUserResponse response = new LoginUserResponse("error", "User does not exist: " + loginUserRequest.email, null);
                logger.info("Login abgelehnt: {}", response);
                ctx.status(409).json(response);
                return;
            }

            User user = userOptional.get();
            if (!user.password.equals(loginUserRequest.password)) {
                LoginUserResponse response = new LoginUserResponse("error", "Wrong password for user: " + loginUserRequest.email, null);
                logger.info("Login abgelehnt (falsches Passwort): {}", response);
                ctx.status(409).json(response);
                return;
            }

            ctx.sessionAttribute("userEmail", user.email);

            LoginUserResponse response = new LoginUserResponse("ok", null, user.username);
            logger.info("Login erfolgreich: {}", response);
            ctx.status(201).json(response);

        } catch (Exception e) {
            RegisterUserResponse response = new RegisterUserResponse("error", "Bad Request: " + e.getMessage() + "\n");
            logger.error("Login fehlgeschlagen: {}", e.getMessage(), e);
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
        logger.info("Logout: {}", response.info);
        ctx.status(200).json(response);
    };

    public static Handler shoppingBuy = ctx -> {

        String email = ctx.sessionAttribute("userEmail");
        if (email == null) {
            logger.info("No user logged in, redirect to login");
            ctx.redirect("login.html");
        } else {
            UserDao userDao = getUserDao();
            Optional<User> userOptional = userDao.getUserByEMail(email);
            if (userOptional.isEmpty()) {
                logger.info("User '{}' not found, redirect to error page", email);
                ctx.redirect("error.html");
            } else {
                logger.info("User found, finish shopping: {}", userOptional.get());
            }
        }
    };
}
