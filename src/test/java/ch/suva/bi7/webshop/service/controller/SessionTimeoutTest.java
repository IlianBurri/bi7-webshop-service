package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.mock.HttpSessionMock;
import ch.suva.bi7.webshop.service.mock.SessionContextMock;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class SessionTimeoutTest {

    private static final Instant LOGIN = Instant.parse("2026-11-05T10:00:00Z");

    @Test
    void knappVorAblaufBleibtSessionGueltig() {
        HttpSessionMock session = eingeloggteSession(LOGIN);

        boolean beendet = timeoutUm(LOGIN.plus(Duration.ofMinutes(15))).beendeAbgelaufeneSession(session.session);

        assertFalse(beendet, "Genau 15 Minuten sind noch nicht abgelaufen");
        assertFalse(session.invalidiert);
    }

    @Test
    void nachAblaufWirdSessionBeendet() {
        HttpSessionMock session = eingeloggteSession(LOGIN);

        boolean beendet = timeoutUm(LOGIN.plus(Duration.ofMinutes(15)).plusSeconds(1))
                .beendeAbgelaufeneSession(session.session);

        assertTrue(beendet);
        assertTrue(session.invalidiert);
    }

    @Test
    void aktivitaetSetztTimerZurueck() {
        HttpSessionMock session = eingeloggteSession(LOGIN);

        Instant nachZehnMinuten = LOGIN.plus(Duration.ofMinutes(10));
        timeoutUm(nachZehnMinuten).merkeAktivitaet(session.session);
        boolean beendet = timeoutUm(LOGIN.plus(Duration.ofMinutes(20))).beendeAbgelaufeneSession(session.session);

        assertEquals(nachZehnMinuten, session.attribute.get(SessionTimeout.LETZTE_AKTIVITAET));
        assertFalse(beendet, "20 Minuten nach Login, aber nur 10 Minuten nach der letzten Aktivität");
        assertFalse(session.invalidiert);
    }

    @Test
    void gastOhneSessionWirdIgnoriert() {
        SessionTimeout testee = timeoutUm(LOGIN);

        assertFalse(testee.beendeAbgelaufeneSession(null));
        assertDoesNotThrow(() -> testee.merkeAktivitaet(null));
    }

    @Test
    void sessionOhneLoginBekommtKeinenZeitstempel() {
        HttpSessionMock session = new HttpSessionMock();

        timeoutUm(LOGIN).merkeAktivitaet(session.session);

        assertFalse(session.attribute.containsKey(SessionTimeout.LETZTE_AKTIVITAET));
        assertFalse(timeoutUm(LOGIN.plus(Duration.ofHours(1))).beendeAbgelaufeneSession(session.session));
    }

    @Test
    void merkeAktivitaetIgnoriertBeendeteSession() {
        HttpSessionMock session = eingeloggteSession(LOGIN);
        session.invalidiert = true;

        assertDoesNotThrow(() -> timeoutUm(LOGIN).merkeAktivitaet(session.session));
    }

    @Test
    void handlerBeendenSessionUndSetzenZeitstempel() throws Exception {
        HttpSessionMock abgelaufen = eingeloggteSession(LOGIN);
        SessionTimeout testee = timeoutUm(LOGIN.plus(Duration.ofMinutes(16)));

        testee.vorRequest.handle(contextMit("GET", abgelaufen));
        assertTrue(abgelaufen.invalidiert);

        HttpSessionMock aktiv = eingeloggteSession(LOGIN);
        testee.nachRequest.handle(contextMit("POST", aktiv));
        assertEquals(LOGIN.plus(Duration.ofMinutes(16)), aktiv.attribute.get(SessionTimeout.LETZTE_AKTIVITAET));
    }

    @Test
    void optionsRequestsWerdenUebersprungen() throws Exception {
        HttpSessionMock session = eingeloggteSession(LOGIN);
        SessionTimeout testee = timeoutUm(LOGIN.plus(Duration.ofMinutes(16)));

        testee.vorRequest.handle(contextMit("OPTIONS", session));
        testee.nachRequest.handle(contextMit("OPTIONS", session));

        assertFalse(session.invalidiert, "CORS-Vorabfragen dürfen die Session nicht beenden");
        assertEquals(LOGIN, session.attribute.get(SessionTimeout.LETZTE_AKTIVITAET));
    }

    @Test
    void konstruktorLehntUngueltigenTimeoutAb() {
        assertThrows(IllegalArgumentException.class, () -> new SessionTimeout(null));
        assertThrows(IllegalArgumentException.class, () -> new SessionTimeout(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new SessionTimeout(Duration.ofMinutes(-1)));
        assertEquals(Duration.ofMinutes(15), SessionTimeout.STANDARD_TIMEOUT);
    }

    private static SessionTimeout timeoutUm(Instant jetzt) {
        return new SessionTimeout(SessionTimeout.STANDARD_TIMEOUT, Clock.fixed(jetzt, ZoneOffset.UTC));
    }

    private static HttpSessionMock eingeloggteSession(Instant letzteAktivitaet) {
        HttpSessionMock session = HttpSessionMock.eingeloggt("test@example.com");
        session.attribute.put(SessionTimeout.LETZTE_AKTIVITAET, letzteAktivitaet);
        return session;
    }

    private static SessionContextMock contextMit(String httpMethode, HttpSessionMock session) {
        return new SessionContextMock(httpMethode, "/api/test", session);
    }
}
