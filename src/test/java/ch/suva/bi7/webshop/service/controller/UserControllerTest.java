package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.RegisterUserRequest;
import ch.suva.bi7.webshop.service.model.RegisterUserResponse;
import ch.suva.bi7.webshop.service.model.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Test
    void testRegister_Erfolgreich() throws Exception {

        RegisterUserRequest request = new RegisterUserRequest("Peter Parker", "spidey@dailybugle.com", "webslinger");

        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.empty());
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);
        UserController.register.handle(ctxMock);

        assertTrue(daoMock.addUserWurdeAufgerufen);
        assertEquals("Peter Parker", daoMock.gespeicherterUser.username);
        assertEquals(201, ctxMock.gesetzterStatus);

        RegisterUserResponse res = (RegisterUserResponse) ctxMock.gesendetesJson;
        assertEquals("ok", res.status);
    }

    @Test
    void testRegister_UserExistiertBereits() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.email = "bruce.wayne@gotham.com";

        User batman = new User("Bruce Wayne", "bruce.wayne@gotham.com", "batman");
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(batman));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);

        UserController.register.handle(ctxMock);

        assertFalse(daoMock.addUserWurdeAufgerufen);
        assertEquals(409, ctxMock.gesetzterStatus);

        RegisterUserResponse res = (RegisterUserResponse) ctxMock.gesendetesJson;
        assertEquals("error", res.status);
        assertEquals("Benutzer existiert bereits.", res.message);
    }
}

class EinfachesUserDaoMock implements UserDao {
    public boolean addUserWurdeAufgerufen = false;
    public User gespeicherterUser = null;
    private Optional<User> vorgegebenerUser;

    public EinfachesUserDaoMock(Optional<User> vorgegebenerUser) {
        this.vorgegebenerUser = vorgegebenerUser;
    }

    @Override
    public void addUser(User user) throws Exception {
        this.addUserWurdeAufgerufen = true;
        this.gespeicherterUser = user;
    }

    @Override
    public Optional<User> getUserByEMail(String email) throws Exception {
        return vorgegebenerUser;
    }

    @Override
    public List<String> getAllUsernames() throws Exception {
        return new ArrayList<>();
    }
}

class EinfacherContextMock implements io.javalin.http.Context {
    public int gesetzterStatus = 0;
    public Object gesendetesJson = null;
    private Object vorgegebenerBody;

    public EinfacherContextMock(Object vorgegebenerBody) {
        this.vorgegebenerBody = vorgegebenerBody;
    }

    @Override
    public <T> T bodyAsClass(Class<T> clazz) {
        return clazz.cast(vorgegebenerBody);
    }

    @Override
    public io.javalin.http.Context status(int status) {
        this.gesetzterStatus = status;
        return this;
    }

    @Override
    public io.javalin.http.Context json(Object obj) {
        this.gesendetesJson = obj;
        return this;
    }

    @Override
    public jakarta.servlet.http.HttpServletRequest req() {
        return null;
    }

    @Override
    public jakarta.servlet.http.HttpServletResponse res() {
        return null;
    }

    @Override
    public java.util.Map<String, String> pathParamMap() {
        return null;
    }

    @Override
    public String pathParam(String key) {
        return "";
    }
}