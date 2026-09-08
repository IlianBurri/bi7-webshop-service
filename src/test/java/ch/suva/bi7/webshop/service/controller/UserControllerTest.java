package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.mock.EinfacherContextMock;
import ch.suva.bi7.webshop.service.mock.EinfachesUserDaoMock;
import ch.suva.bi7.webshop.service.model.LoginUserRequest;
import ch.suva.bi7.webshop.service.model.LoginUserResponse;
import ch.suva.bi7.webshop.service.model.RegisterUserRequest;
import ch.suva.bi7.webshop.service.model.RegisterUserResponse;
import ch.suva.bi7.webshop.service.db.entity.UserEntity;
import io.javalin.config.Key;
import io.javalin.config.MultipartConfig;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.ContextPlugin;
import io.javalin.router.Endpoint;
import io.javalin.router.Endpoints;
import io.javalin.security.RouteRole;
import jakarta.servlet.ServletOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void registerErfolgreich() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest("Peter Parker", "spidey@dailybugle.com", "webslinger");

        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.empty());
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);
        UserController.register.handle(ctxMock);

        assertNotNull(daoMock.gespeicherterUser);
        assertEquals("Peter Parker", daoMock.gespeicherterUser.getUsername());
        assertEquals(201, ctxMock.gesetzterStatus);

        RegisterUserResponse res = (RegisterUserResponse) ctxMock.gesendetesJson;
        assertEquals("ok", res.status);
    }

    @Test
    void loginSetztAdminStatusInSession() throws Exception {
        LoginUserRequest request = new LoginUserRequest("bruce.wayne@gotham.com", "batman");
        UserEntity admin = new UserEntity("Bruce Wayne", "bruce.wayne@gotham.com", "batman", true);
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(admin));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);
        UserController.login.handle(ctxMock);

        assertEquals(201, ctxMock.gesetzterStatus);
        assertEquals("bruce.wayne@gotham.com", ctxMock.sessionAttribute("userEmail"));

        LoginUserResponse res = (LoginUserResponse) ctxMock.gesendetesJson;
        assertTrue(res.isAdmin, "Login-Antwort muss isAdmin=true enthalten");
    }

    @Test
    void loginSetztAdminStatusFalseFuerNormalenUser() throws Exception {
        LoginUserRequest request = new LoginUserRequest("bruce.wayne@gotham.com", "batman");
        UserEntity normal = new UserEntity("Bruce Wayne", "bruce.wayne@gotham.com", "batman", false);
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(normal));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);
        UserController.login.handle(ctxMock);

        assertEquals(201, ctxMock.gesetzterStatus);

        LoginUserResponse res = (LoginUserResponse) ctxMock.gesendetesJson;
        assertFalse(res.isAdmin, "Login-Antwort muss isAdmin=false enthalten");
    }
// TODO Löschen oder Umschreiben:
//    @Test
//    void currentUserLiefertAdminStatusAusSession() throws Exception {
//        EinfacherContextMock ctxMock = new EinfacherContextMock(null);
//        ctxMock.sessionAttribute("userEmail", "bruce.wayne@gotham.com");
//        ctxMock.sessionAttribute("isAdmin", true);
//
//        UserController.currentUser.handle(ctxMock);
//
//        assertEquals(200, ctxMock.gesetzterStatus);
//        @SuppressWarnings("unchecked")
//        java.util.Map<String, Object> res = (java.util.Map<String, Object>) ctxMock.gesendetesJson;
//        assertEquals("bruce.wayne@gotham.com", res.get("email"));
//        assertEquals(true, res.get("isAdmin"), "Admin-Status muss aus der Session kommen");
//    }
//
//    @Test
//    void currentUserLiefertFalseFuerNormalenUser() throws Exception {
//        EinfacherContextMock ctxMock = new EinfacherContextMock(null);
//        ctxMock.sessionAttribute("userEmail", "peter.parker@dailybugle.com");
//        ctxMock.sessionAttribute("isAdmin", false);
//
//        UserController.currentUser.handle(ctxMock);
//
//        assertEquals(200, ctxMock.gesetzterStatus);
//        @SuppressWarnings("unchecked")
//        java.util.Map<String, Object> res = (java.util.Map<String, Object>) ctxMock.gesendetesJson;
//        assertEquals(false, res.get("isAdmin"), "Ohne Admin-Session muss isAdmin false sein");
//    }
//
//    @Test
//    void currentUserOhneSessionLiefert401() throws Exception {
//        EinfacherContextMock ctxMock = new EinfacherContextMock(null);
//
//        UserController.currentUser.handle(ctxMock);
//
//        assertEquals(401, ctxMock.gesetzterStatus);
//    }

    @Test
    void registerBeiExistierendemUserLiefert409() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
            "Bruce Wayne",
            "bruce.wayne@gotham.com",
            "batman"
        );

        UserEntity batman = new UserEntity("Bruce Wayne", "bruce.wayne@gotham.com", "batman", false);
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(batman));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);

        UserController.register.handle(ctxMock);

        assertNull(daoMock.gespeicherterUser);
        assertEquals(409, ctxMock.gesetzterStatus);

        RegisterUserResponse res = (RegisterUserResponse) ctxMock.gesendetesJson;
        assertEquals("error", res.status);
        assertEquals("User already exists", res.error);
    }
}




