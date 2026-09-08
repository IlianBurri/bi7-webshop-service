package ch.suva.bi7.webshop.service.mock;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class WarenkorbContextMock extends EinfacherContextMock {

    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    public String gesendetesResult;

    public WarenkorbContextMock() {
        super(null);
    }

    public void setPathParam(String key, String value) {
        pathParams.put(key, value);
    }

    public void setQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    @Override
    public String pathParam(String key) {
        return pathParams.getOrDefault(key, "");
    }

    @Override
    public String queryParam(String key) {
        return queryParams.get(key);
    }

    @Override
    public Context result(String result) {
        gesendetesResult = result;
        return this;
    }
}
