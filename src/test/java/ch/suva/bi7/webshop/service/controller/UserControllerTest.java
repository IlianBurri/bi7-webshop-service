package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.mock.EinfacherContextMock;
import ch.suva.bi7.webshop.service.mock.EinfachesUserDaoMock;
import ch.suva.bi7.webshop.service.model.LoginUserRequest;
import ch.suva.bi7.webshop.service.model.LoginUserResponse;
import ch.suva.bi7.webshop.service.model.RegisterUserRequest;
import ch.suva.bi7.webshop.service.model.RegisterUserResponse;
import ch.suva.bi7.webshop.service.model.UserDto;
import ch.suva.bi7.webshop.service.db.entity.UserEntity;
import org.junit.jupiter.api.Test;
import java.util.Optional;
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




