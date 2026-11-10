package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.mock.HttpSessionMock;
import ch.suva.bi7.webshop.service.mock.SessionContextMock;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoginPruefungTest {

    private final LoginPruefung testee = new LoginPruefung();

    @Test
    void ohneSessionGibt401() throws Exception {
        SessionContextMock ctx = new SessionContextMock("GET", "/api/warenkorb/test@example.com", null)
                .mitPfadParameter("email", "test@example.com");

        testee.nurEingeloggt.handle(ctx);

        assertEquals(401, ctx.gesetzterStatus);
        assertEquals(Map.of("error", "Nicht eingeloggt."), ctx.gesendetesJson);
        assertTrue(ctx.restUebersprungen, "Der Endpoint darf nicht mehr ausgeführt werden");
    }

    @Test
    void abgelaufeneSessionGibt401() throws Exception {
        HttpSessionMock session = HttpSessionMock.eingeloggt("test@example.com");
        session.invalidiert = true;
        SessionContextMock ctx = new SessionContextMock("POST", "/api/bestellung/checkout", session);

        testee.nurEingeloggt.handle(ctx);

        assertEquals(401, ctx.gesetzterStatus);
        assertTrue(ctx.restUebersprungen);
    }

    @Test
    void sessionOhneLoginGibt401() throws Exception {
        SessionContextMock ctx = new SessionContextMock("POST", "/api/warenkorb/add", new HttpSessionMock());

        testee.nurEingeloggt.handle(ctx);

        assertEquals(401, ctx.gesetzterStatus);
    }

    @Test
    void eigeneEmailWirdDurchgelassen() throws Exception {
        SessionContextMock ctx = new SessionContextMock("GET", "/api/warenkorb/Test@Example.com",
                HttpSessionMock.eingeloggt("test@example.com"))
                .mitPfadParameter("email", "Test@Example.com");

        testee.nurEingeloggt.handle(ctx);

        assertEquals(0, ctx.gesetzterStatus, "Gross-/Kleinschreibung der E-Mail spielt keine Rolle");
        assertFalse(ctx.restUebersprungen);
    }

    @Test
    void endpointOhneEmailImPfadWirdDurchgelassen() throws Exception {
        SessionContextMock ctx = new SessionContextMock("PUT", "/api/warenkorb/item/7",
                HttpSessionMock.eingeloggt("test@example.com"))
                .mitPfadParameter("id", "7");

        testee.nurEingeloggt.handle(ctx);

        assertEquals(0, ctx.gesetzterStatus);
        assertFalse(ctx.restUebersprungen);
    }

    @Test
    void fremdeEmailGibt403() throws Exception {
        SessionContextMock ctx = new SessionContextMock("GET", "/api/adresse/fremd@example.com",
                HttpSessionMock.eingeloggt("test@example.com"))
                .mitPfadParameter("email", "fremd@example.com");

        testee.nurEingeloggt.handle(ctx);

        assertEquals(403, ctx.gesetzterStatus);
        assertEquals(Map.of("error", "Kein Zugriff auf Daten eines anderen Benutzers."), ctx.gesendetesJson);
        assertTrue(ctx.restUebersprungen);
    }

    @Test
    void optionsRequestWirdNichtGeprueft() throws Exception {
        SessionContextMock ctx = new SessionContextMock("OPTIONS", "/api/warenkorb/add", null);

        testee.nurEingeloggt.handle(ctx);

        assertEquals(0, ctx.gesetzterStatus);
        assertFalse(ctx.restUebersprungen);
    }
}
