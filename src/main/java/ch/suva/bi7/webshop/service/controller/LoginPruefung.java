package ch.suva.bi7.webshop.service.controller;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginPruefung {

    private static final Logger logger = LoggerFactory.getLogger(LoginPruefung.class);

    public final Handler nurEingeloggt = this::pruefe;

    private void pruefe(Context ctx) {
        if (ctx.method() == HandlerType.OPTIONS) {
            return;
        }

        String sessionEmail = eingeloggteEmail(ctx.req().getSession(false));
        if (sessionEmail == null) {
            logger.info("{} {} abgelehnt: nicht eingeloggt", ctx.method(), ctx.path());
            stoppe(ctx, HttpStatus.UNAUTHORIZED, "Nicht eingeloggt.");
            return;
        }

        Map<String, String> pfadParameter = ctx.pathParamMap();
        String pfadEmail = pfadParameter == null ? null : pfadParameter.get("email");
        if (pfadEmail != null && !pfadEmail.trim().equalsIgnoreCase(sessionEmail)) {
            logger.info("{} {} abgelehnt: '{}' ist nicht '{}'", ctx.method(), ctx.path(), sessionEmail, pfadEmail);
            stoppe(ctx, HttpStatus.FORBIDDEN, "Kein Zugriff auf Daten eines anderen Benutzers.");
        }
    }

    static String eingeloggteEmail(HttpSession session) {
        if (session == null) {
            return null;
        }
        try {
            Object email = session.getAttribute(SessionTimeout.USER_EMAIL);
            return email == null ? null : email.toString();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private static void stoppe(Context ctx, HttpStatus status, String meldung) {
        ctx.status(status).json(Map.of("error", meldung));
        ctx.skipRemainingHandlers();
    }
}
