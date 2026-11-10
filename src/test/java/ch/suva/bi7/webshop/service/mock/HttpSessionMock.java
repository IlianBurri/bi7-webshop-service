package ch.suva.bi7.webshop.service.mock;

import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class HttpSessionMock {
    public final Map<String, Object> attribute = new HashMap<>();
    public boolean invalidiert = false;

    public final HttpSession session = (HttpSession) Proxy.newProxyInstance(
            HttpSessionMock.class.getClassLoader(), new Class<?>[]{HttpSession.class},
            (p, methode, args) -> {
                if (invalidiert && !methode.getName().equals("invalidate")) {
                    throw new IllegalStateException("Session ist bereits ungültig");
                }
                return switch (methode.getName()) {
                    case "getAttribute" -> attribute.get((String) args[0]);
                    case "setAttribute" -> attribute.put((String) args[0], args[1]);
                    case "invalidate" -> {
                        invalidiert = true;
                        yield null;
                    }
                    default -> null;
                };
            });

    public static HttpSessionMock eingeloggt(String email) {
        HttpSessionMock mock = new HttpSessionMock();
        mock.attribute.put("userEmail", email);
        return mock;
    }
}
