package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.model.*;
import ch.suva.bi7.webshop.service.mapper.BenutzerMapper;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BenutzerController {

    private static final Logger logger = LoggerFactory.getLogger(BenutzerController.class);

    private static BenutzerDao benutzerDao = null;

    public BenutzerController(BenutzerDao benutzerDao) {
        if (benutzerDao == null) {
            throw new IllegalArgumentException("benutzerDao must not be null");
        }
        this.benutzerDao = benutzerDao;
    }

    private static BenutzerDao getBenutzerDao() throws Exception {
        return benutzerDao;
    }

    static void setBenutzerDaoMock(BenutzerDao benutzerDaoMock) {
        benutzerDao = benutzerDaoMock;
    }

    public static Handler fetchAlleBenutzernamen = ctx -> {
        List<String> alleBenutzer = getBenutzerDao().holeAlleBenutzernamen();
        ctx.json(alleBenutzer);
    };

    public static Handler fetchByEMail = ctx -> {
        String email = ctx.pathParam("email");
        Optional<BenutzerEntity> benutzer = getBenutzerDao().holeBenutzerNachEMail(email);
        if (benutzer.isPresent()) {
            ctx.json(BenutzerMapper.toDto(benutzer.get()));
        } else {
            ctx.status(404).result("Not Found: '" + email + "'\n");
        }
    };

    public static Handler register = ctx -> {
        try {
            RegisterBenutzerRequest registrierungsAnfrage = ctx.bodyAsClass(RegisterBenutzerRequest.class);
            logger.info("Register: username={}, email={}", registrierungsAnfrage.username, registrierungsAnfrage.email);

            BenutzerDao benutzerDao = getBenutzerDao();

            if (benutzerDao.holeBenutzerNachEMail(registrierungsAnfrage.email).isPresent()) {
                RegisterBenutzerResponse antwort = new RegisterBenutzerResponse("error", "User already exists");
                logger.info("Register abgelehnt: {}", antwort);
                ctx.status(409).json(antwort);
                return;
            }

            BenutzerEntity neuerBenutzer = new BenutzerEntity(
                    registrierungsAnfrage.username,
                    registrierungsAnfrage.email,
                    registrierungsAnfrage.password,
                    false
            );
            benutzerDao.speichereBenutzer(neuerBenutzer);

            RegisterBenutzerResponse antwort = new RegisterBenutzerResponse("ok", null);
            logger.info("Register erfolgreich: {}", antwort);
            ctx.status(201).json(antwort);
        } catch (Exception e) {
            RegisterBenutzerResponse antwort = new RegisterBenutzerResponse("error", "Bad Request: " + e.getMessage() + "\n");
            logger.error("Register fehlgeschlagen: {}", e.getMessage(), e);
            ctx.status(400).json(antwort);
        }
    };

    public static Handler login = ctx -> {
        try {
            LoginBenutzerRequest loginAnfrage = ctx.bodyAsClass(LoginBenutzerRequest.class);
            logger.info("Login: {}", loginAnfrage.email);

            String email = ctx.sessionAttribute("userEmail");
            BenutzerDao benutzerDao = getBenutzerDao();

            if (email != null) {
                if (loginAnfrage.email.equals(email)) {
                    Optional<BenutzerEntity> benutzerOptional = benutzerDao.holeBenutzerNachEMail(email);
                    String echterBenutzername = benutzerOptional.map(benutzer -> benutzer.getUsername()).orElse(email);

                    LoginBenutzerResponse antwort = new LoginBenutzerResponse(
                            "info", "Du bist bereits als " + echterBenutzername + " eingeloggt.", echterBenutzername,
                            benutzerOptional.map(benutzer -> benutzer.isAdmin()).orElse(false)
                    );
                    logger.info("Bereits eingeloggt: {}", antwort);
                    ctx.status(200).json(antwort);
                    return;
                } else {
                    LoginBenutzerResponse antwort = new LoginBenutzerResponse(
                            "error", "Es ist bereits ein anderer Benutzer (" + email + ") in dieser Session eingeloggt. Bitte zuerst ausloggen.", null,
                            false
                    );
                    logger.info("Login-Konflikt: {}", antwort);
                    ctx.status(409).json(antwort);
                    return;
                }
            }

            Optional<BenutzerEntity> benutzerOptional = benutzerDao.holeBenutzerNachEMail(loginAnfrage.email);
            if (benutzerOptional.isEmpty()) {
                LoginBenutzerResponse antwort = new LoginBenutzerResponse("error", "User does not exist: " + loginAnfrage.email, null, false);
                logger.info("Login abgelehnt: {}", antwort);
                ctx.status(409).json(antwort);
                return;
            }

            BenutzerEntity benutzer = benutzerOptional.get();
            if (!benutzer.getPassword().equals(loginAnfrage.password)) {
                LoginBenutzerResponse antwort = new LoginBenutzerResponse("error", "Wrong password for user: " + loginAnfrage.email, null, false);
                logger.info("Login abgelehnt (falsches Passwort): {}", antwort);
                ctx.status(409).json(antwort);
                return;
            }

            ctx.sessionAttribute("userEmail", benutzer.getEmail());

            LoginBenutzerResponse antwort = new LoginBenutzerResponse("ok", null, benutzer.getUsername(), benutzer.isAdmin());
            logger.info("Login erfolgreich: {}", antwort);
            ctx.status(201).json(antwort);

        } catch (Exception e) {
            LoginBenutzerResponse antwort = new LoginBenutzerResponse(
                    "error",
                    "Bad Request: " + e.getMessage() + "\n",
                    null,
                    false
            );
            logger.error("Login fehlgeschlagen: {}", e.getMessage(), e);
            ctx.status(400).json(antwort);
        }
    };

    public static Handler logout = ctx -> {
        String email = ctx.sessionAttribute("userEmail");
        LogoutBenutzerResponse antwort;
        if (email == null) {
            antwort = new LogoutBenutzerResponse("ok", "Nothing to do, no user logged in");

        } else {
            ctx.req().getSession().invalidate();
            antwort = new LogoutBenutzerResponse("ok", "User logout successful.");
        }
        logger.info("Abmeldung: {}", antwort.info);
        ctx.status(200).json(antwort);
    };
}
