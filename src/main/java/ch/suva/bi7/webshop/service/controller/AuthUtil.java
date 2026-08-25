package ch.suva.bi7.webshop.service.controller;

import io.javalin.http.Context;

public final class AuthUtil {

    private AuthUtil() {}

    public static boolean requireAdmin(Context ctx) {
        Boolean isAdmin = ctx.sessionAttribute("isAdmin");
        if (!Boolean.TRUE.equals(isAdmin)) {
            ctx.status(403).json(java.util.Map.of("error", "Nur Administratoren dürfen Artikel anlegen."));
            return false;
        }
        return true;
    }
}
