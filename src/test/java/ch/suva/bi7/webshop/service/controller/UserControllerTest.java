package ch.suva.bi7.webshop.service.controller;

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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
        RegisterUserRequest request = new RegisterUserRequest(
            "Bruce Wayne",
            "bruce.wayne@gotham.com",
            "batman"
        );

        User batman = new User("Bruce Wayne", "bruce.wayne@gotham.com", "batman");
        EinfachesUserDaoMock daoMock = new EinfachesUserDaoMock(Optional.of(batman));
        EinfacherContextMock ctxMock = new EinfacherContextMock(request);

        UserController.setUserDaoMock(daoMock);

        UserController.register.handle(ctxMock);

        assertFalse(daoMock.addUserWurdeAufgerufen);
        assertEquals(409, ctxMock.gesetzterStatus);

        RegisterUserResponse res = (RegisterUserResponse) ctxMock.gesendetesJson;
        assertEquals("error", res.status);
        assertEquals("Benutzer existiert bereits.", res.error);
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