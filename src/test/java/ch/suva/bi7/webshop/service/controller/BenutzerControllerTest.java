package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.mock.EinfacherContextMock;
import ch.suva.bi7.webshop.service.mock.EinfachesBenutzerDaoMock;
import ch.suva.bi7.webshop.service.model.LoginBenutzerRequest;
import ch.suva.bi7.webshop.service.model.LoginBenutzerResponse;
import ch.suva.bi7.webshop.service.model.RegisterBenutzerRequest;
import ch.suva.bi7.webshop.service.model.RegisterBenutzerResponse;
import ch.suva.bi7.webshop.service.model.BenutzerDto;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class BenutzerControllerTest {

    @Test
    void registerErfolgreich() throws Exception {

        RegisterBenutzerRequest request = new RegisterBenutzerRequest("Peter Parker", "spidey@dailybugle.com", "webslinger");

        EinfachesBenutzerDaoMock daoMock = new EinfachesBenutzerDaoMock(Optional.empty());
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        BenutzerController.setBenutzerDaoMock(daoMock);
        BenutzerController.register.handle(ctxMock);

        assertNotNull(daoMock.gespeicherterBenutzer);
        assertEquals("Peter Parker", daoMock.gespeicherterBenutzer.getUsername());
        assertEquals(201, ctxMock.gesetzterStatus);

        RegisterBenutzerResponse res = (RegisterBenutzerResponse) ctxMock.gesendetesJson;
        assertEquals("ok", res.status);
    }

    @Test
    void loginSetztAdminStatusInSession() throws Exception {
        LoginBenutzerRequest request = new LoginBenutzerRequest("bruce.wayne@gotham.com", "batman");
        BenutzerEntity admin = new BenutzerEntity("Bruce Wayne", "bruce.wayne@gotham.com", "batman", true);
        EinfachesBenutzerDaoMock daoMock = new EinfachesBenutzerDaoMock(Optional.of(admin));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        BenutzerController.setBenutzerDaoMock(daoMock);
        BenutzerController.login.handle(ctxMock);

        assertEquals(201, ctxMock.gesetzterStatus);
        assertEquals("bruce.wayne@gotham.com", ctxMock.sessionAttribute("userEmail"));

        LoginBenutzerResponse res = (LoginBenutzerResponse) ctxMock.gesendetesJson;
        assertTrue(res.isAdmin, "Login-Antwort muss isAdmin=true enthalten");
    }

    @Test
    void loginSetztAdminStatusFalseFuerNormalenUser() throws Exception {
        LoginBenutzerRequest request = new LoginBenutzerRequest("bruce.wayne@gotham.com", "batman");
        BenutzerEntity normal = new BenutzerEntity("Bruce Wayne", "bruce.wayne@gotham.com", "batman", false);
        EinfachesBenutzerDaoMock daoMock = new EinfachesBenutzerDaoMock(Optional.of(normal));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        BenutzerController.setBenutzerDaoMock(daoMock);
        BenutzerController.login.handle(ctxMock);

        assertEquals(201, ctxMock.gesetzterStatus);

        LoginBenutzerResponse res = (LoginBenutzerResponse) ctxMock.gesendetesJson;
        assertFalse(res.isAdmin, "Login-Antwort muss isAdmin=false enthalten");
    }

    @Test
    void registerBeiExistierendemUserLiefert409() throws Exception {
        RegisterBenutzerRequest request = new RegisterBenutzerRequest(
            "Bruce Wayne",
            "bruce.wayne@gotham.com",
            "batman"
        );

        BenutzerEntity batman = new BenutzerEntity("Bruce Wayne", "bruce.wayne@gotham.com", "batman", false);
        EinfachesBenutzerDaoMock daoMock = new EinfachesBenutzerDaoMock(Optional.of(batman));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        BenutzerController.setBenutzerDaoMock(daoMock);

        BenutzerController.register.handle(ctxMock);

        assertNull(daoMock.gespeicherterBenutzer);
        assertEquals(409, ctxMock.gesetzterStatus);

        RegisterBenutzerResponse res = (RegisterBenutzerResponse) ctxMock.gesendetesJson;
        assertEquals("error", res.status);
        assertEquals("User already exists", res.error);
    }
}
