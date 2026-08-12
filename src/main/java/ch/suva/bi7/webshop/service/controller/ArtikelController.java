package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.Artikel;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ArtikelController {

    private static final Logger logger = LoggerFactory.getLogger(ArtikelController.class);

    private static ArtikelDao artikelDao = null;

    private static ArtikelDao getArtikelDao() throws Exception {
        if (artikelDao == null) {
            DBConnection dbConnection = new DBConnectionImpl(DBConfig.getHost(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
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
            logger.error("Fehler beim Abrufen der Artikel: {}", e.getMessage(), e);

            ctx.status(500).result("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    };
}