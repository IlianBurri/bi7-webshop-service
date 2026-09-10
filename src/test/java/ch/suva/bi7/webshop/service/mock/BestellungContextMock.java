package ch.suva.bi7.webshop.service.mock;

import io.javalin.http.Context;

public class BestellungContextMock extends EinfacherContextMock {

    public String gesendetesResult;

    public BestellungContextMock(Object anfrageDaten) {
        super(anfrageDaten);
    }

    @Override
    public Context result(String result) {
        gesendetesResult = result;
        return this;
    }
}
