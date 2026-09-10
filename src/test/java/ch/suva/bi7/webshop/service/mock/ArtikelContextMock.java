package ch.suva.bi7.webshop.service.mock;

import io.javalin.http.Context;

public class ArtikelContextMock extends EinfacherContextMock {

    public String gesendetesResult;

    public ArtikelContextMock() {
        this(null);
    }

    public ArtikelContextMock(Object anfrageDaten) {
        super(anfrageDaten);
    }

    @Override
    public Context result(String result) {
        this.gesendetesResult = result;
        return this;
    }
}
