package ch.suva.bi7.webshop.service.mock;

import io.javalin.http.Context;

public class BestellungContextMock extends EinfacherContextMock {

    public String gesendetesResult;

    public BestellungContextMock(Object body) {
        super(body);
    }

    @Override
    public Context result(String result) {
        gesendetesResult = result;
        return this;
    }
}
