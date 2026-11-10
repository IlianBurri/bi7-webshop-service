package ch.suva.bi7.webshop.service.controller;

import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class SessionTimeout {

    public static final Duration STANDARD_TIMEOUT = Duration.ofMinutes(15);

    static final String USER_EMAIL = "userEmail";
    static final String LETZTE_AKTIVITAET = "letzteAktivitaet";

    private static final Logger logger = LoggerFactory.getLogger(SessionTimeout.class);

    private final Duration timeout;
    private final Clock clock;

    public SessionTimeout(Duration timeout) {
        this(timeout, Clock.systemUTC());
    }

    SessionTimeout(Duration timeout, Clock clock) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout muss grösser als 0 sein");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock darf nicht null sein");
        }
        this.timeout = timeout;
        this.clock = clock;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public final Handler vorRequest = ctx -> {
        if (ctx.method() != HandlerType.OPTIONS) {
            beendeAbgelaufeneSession(ctx.req().getSession(false));
        }
    };

    public final Handler nachRequest = ctx -> {
        if (ctx.method() != HandlerType.OPTIONS) {
            merkeAktivitaet(ctx.req().getSession(false));
        }
    };

    boolean beendeAbgelaufeneSession(HttpSession session) {
        if (session == null || session.getAttribute(USER_EMAIL) == null) {
            return false;
        }
        Instant letzteAktivitaet = (Instant) session.getAttribute(LETZTE_AKTIVITAET);
        if (letzteAktivitaet == null) {
            return false;
        }
        Duration inaktiv = Duration.between(letzteAktivitaet, clock.instant());
        if (inaktiv.compareTo(timeout) <= 0) {
            return false;
        }
        logger.info("Session von '{}' nach {} s Inaktivität beendet (Timeout {} s)",
                session.getAttribute(USER_EMAIL), inaktiv.toSeconds(), timeout.toSeconds());
        session.invalidate();
        return true;
    }

    void merkeAktivitaet(HttpSession session) {
        if (session == null) {
            return;
        }
        try {
            if (session.getAttribute(USER_EMAIL) != null) {
                session.setAttribute(LETZTE_AKTIVITAET, clock.instant());
            }
        } catch (IllegalStateException e) {
            // Session wurde in diesem Request beendet (Logout oder Timeout)
        }
    }
}
