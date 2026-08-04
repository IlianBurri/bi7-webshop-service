package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.Artikel;
import io.javalin.http.Handler;

import java.util.List;

public class ArtikelController {

    private static ArtikelDao artikelDao = null;

    private static ArtikelDao getArtikelDao() throws Exception {
        if (artikelDao == null) {
            DBConnection dbConnection = new DBConnectionImpl("localhost", "webshopdb", "webshopuser", "webshoppassword");
            artikelDao = new ArtikelDaoImpl(dbConnection);
        }
        return artikelDao;
    }

    static void setArtikelDaoMock(ArtikelDao artikelDaoMock) {
        artikelDao = artikelDaoMock;
    }

    public static Handler fetchAllArtikel = ctx -> {
        try {
            List<Artikel> artikelListe = getArtikelDao().getAllArtikel();
            ctx.status(200).json(artikelListe);
        } catch (Exception e) {
            System.out.println("Fehler beim Abrufen der Artikel: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result("Internal Server Error: " + e.getMessage() + "\n");
        }
    };
}