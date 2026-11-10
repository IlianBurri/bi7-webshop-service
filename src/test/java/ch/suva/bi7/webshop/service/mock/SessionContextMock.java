package ch.suva.bi7.webshop.service.mock;

import io.javalin.http.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class SessionContextMock extends EinfacherContextMock {
    private final HttpServletRequest request;
    private final Map<String, String> pfadParameter = new HashMap<>();
    public boolean restUebersprungen = false;

    public SessionContextMock(String httpMethode, String pfad, HttpSessionMock session) {
        super(null);
        HttpSession httpSession = session == null ? null : session.session;
        request = (HttpServletRequest) Proxy.newProxyInstance(
                SessionContextMock.class.getClassLoader(), new Class<?>[]{HttpServletRequest.class},
                (proxy, methode, args) -> switch (methode.getName()) {
                    case "getMethod" -> httpMethode;
                    case "getRequestURI" -> pfad;
                    case "getContextPath" -> "";
                    case "getSession" -> httpSession;
                    default -> null;
                });
    }

    public SessionContextMock mitPfadParameter(String name, String wert) {
        pfadParameter.put(name, wert);
        return this;
    }

    @Override
    public HttpServletRequest req() {
        return request;
    }

    @Override
    public Map<String, String> pathParamMap() {
        return pfadParameter;
    }

    @Override
    public Context skipRemainingHandlers() {
        restUebersprungen = true;
        return this;
    }
}
