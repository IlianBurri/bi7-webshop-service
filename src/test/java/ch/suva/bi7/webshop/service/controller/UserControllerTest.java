package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.LoginUserRequest;
import ch.suva.bi7.webshop.service.model.LoginUserResponse;
import ch.suva.bi7.webshop.service.model.RegisterUserRequest;
import ch.suva.bi7.webshop.service.model.RegisterUserResponse;
import ch.suva.bi7.webshop.service.model.User;
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
        User admin = new User("Bruce Wayne", "bruce.wayne@gotham.com", "batman", true);
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(admin));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);
        UserController.login.handle(ctxMock);

        assertEquals(201, ctxMock.gesetzterStatus);
        assertEquals(true, ctxMock.sessionAttribute("isAdmin"), "Login muss isAdmin=true in die Session legen");
        assertEquals("bruce.wayne@gotham.com", ctxMock.sessionAttribute("userEmail"));

        LoginUserResponse res = (LoginUserResponse) ctxMock.gesendetesJson;
        assertTrue(res.isAdmin, "Login-Antwort muss isAdmin=true enthalten");
    }

    @Test
    void loginSetztAdminStatusFalseFuerNormalenUser() throws Exception {
        LoginUserRequest request = new LoginUserRequest("bruce.wayne@gotham.com", "batman");
        User normal = new User("Bruce Wayne", "bruce.wayne@gotham.com", "batman", false);
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(normal));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);
        UserController.login.handle(ctxMock);

        assertEquals(201, ctxMock.gesetzterStatus);
        assertEquals(false, ctxMock.sessionAttribute("isAdmin"), "Login muss isAdmin=false in die Session legen");

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

        User batman = new User("Bruce Wayne", "bruce.wayne@gotham.com", "batman", false);
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

class EinfachesUserDaoMock implements UserDao {
    public User gespeicherterUser = null;
    private Optional<User> vorgegebenerUser;

    public EinfachesUserDaoMock(Optional<User> vorgegebenerUser) {
        this.vorgegebenerUser = vorgegebenerUser;
    }

    @Override
    public void addUser(User user) throws Exception {
        this.gespeicherterUser = user;
    }

    @Override
    public Optional<User> getUserByEMail(String email) {
        return vorgegebenerUser;
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>();
    }
}

class EinfacherContextMock implements io.javalin.http.Context {
    public int gesetzterStatus = 0;
    public Object gesendetesJson = null;
    private Object vorgegebenerBody;
    private final Map<String, Object> sessionAttrs = new HashMap<>();

    public EinfacherContextMock(Object vorgegebenerBody) {
        this.vorgegebenerBody = vorgegebenerBody;
    }

    @Override
    public <T> T bodyAsClass(Class<T> clazz) {
        return clazz.cast(vorgegebenerBody);
    }

    @Override
    public <T> T sessionAttribute(@NotNull String key) {
        @SuppressWarnings("unchecked")
        T value = (T) sessionAttrs.get(key);
        return value;
    }

    @Override
    public void sessionAttribute(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            sessionAttrs.remove(key);
        } else {
            sessionAttrs.put(key, value);
        }
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
    public Map<String, String> pathParamMap() {
        return null;
    }

    @Override
    public String pathParam(String key) {
        return "";
    }

    @NotNull
    @Override
    public Endpoints endpoints() {
        return null;
    }

    @NotNull
    @Override
    public Endpoint endpoint() {
        return null;
    }

    @Override
    public <T> T appData(@NotNull Key<T> key) {
        return null;
    }

    @NotNull
    @Override
    public JsonMapper jsonMapper() {
        return null;
    }

    @Override
    public <T> T with(@NotNull Class<? extends ContextPlugin<?, T>> aClass) {
        return null;
    }

    @NotNull
    @Override
    public MultipartConfig multipartConfig() {
        return null;
    }

    @Override
    public boolean strictContentTypes() {
        return false;
    }

    @NotNull
    @Override
    public ServletOutputStream outputStream() {
        return null;
    }

    @NotNull
    @Override
    public Context minSizeForCompression(int i) {
        return null;
    }

    @NotNull
    @Override
    public Context result(@NotNull InputStream inputStream) {
        return null;
    }

    @Nullable
    @Override
    public InputStream resultInputStream() {
        return null;
    }

    @Override
    public void future(@NotNull Supplier<? extends CompletableFuture<?>> supplier) {

    }

    @Override
    public void redirect(@NotNull String s, @NotNull HttpStatus httpStatus) {

    }

    @Override
    public void writeJsonStream(@NotNull Stream<?> stream) {

    }

    @NotNull
    @Override
    public Context skipRemainingHandlers() {
        return null;
    }

    @NotNull
    @Override
    public Set<RouteRole> routeRoles() {
        return Set.of();
    }
}
