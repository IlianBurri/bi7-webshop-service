package ch.suva.bi7.webshop.service.mock;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class AdresseContextMock extends EinfacherContextMock {

    private final Map<String, String> pathParams = new HashMap<>();
    public String gesendetesResult;
    public boolean jsonFehler;

    public AdresseContextMock() {
        super(null);
    }

    public AdresseContextMock(Object vorgegebenerBody) {
        super(vorgegebenerBody);
    }

    public void setPathParam(String key, String value) {
        pathParams.put(key, value);
    }

    @Override
    public String pathParam(String key) {
        return pathParams.getOrDefault(key, "");
    }

    @Override
    public <T> T bodyAsClass(Class<T> clazz) {
        if (jsonFehler) {
            throw new BadRequestResponse("Invalid anfrageDaten");
        }
        return super.bodyAsClass(clazz);
    }

    @Override
    public Context result(String result) {
        gesendetesResult = result;
        return this;
    }
}
