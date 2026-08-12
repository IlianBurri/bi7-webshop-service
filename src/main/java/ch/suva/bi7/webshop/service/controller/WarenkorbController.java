package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WarenkorbController {

    private static final Logger logger = LoggerFactory.getLogger(WarenkorbController.class);

    private static WarenkorbDao warenkorbDao = null;

    private static WarenkorbDao getWarenkorbDao() throws Exception {
        if (warenkorbDao == null) {
            DBConnection dbConnection = new DBConnectionImpl(DBConfig.getHost(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
            warenkorbDao = new WarenkorbDaoImpl(dbConnection);
        }
        return warenkorbDao;
    }

    static void setWarenkorbDaoMock(WarenkorbDao warenkorbDaoMock) {
        warenkorbDao = warenkorbDaoMock;
    }


    public static Handler getWarenkorb = ctx -> {
        try {
            String email = ctx.pathParam("email");
            List<WarenkorbItem> items = getWarenkorbDao().getWarenkorbByUser(email);
            ctx.status(200).json(items);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen des Warenkorbs: {}", e.getMessage(), e);
            ctx.status(500).result("Fehler beim Abrufen des Warenkorbs.");
        }
    };


    public static Handler addToWarenkorb = ctx -> {
        try {
            String email = ctx.queryParam("email");
            String artikelIdStr = ctx.queryParam("artikelId");

            if (email == null || email.trim().isEmpty()) {
                ctx.status(400).result("Parameter 'email' erforderlich.");
                return;
            }
            if (artikelIdStr == null || artikelIdStr.trim().isEmpty()) {
                ctx.status(400).result("Parameter 'artikelId' erforderlich.");
                return;
            }

            int artikelId = Integer.parseInt(artikelIdStr);

            int menge = 1;
            String mengeStr = ctx.queryParam("menge");
            if (mengeStr != null && !mengeStr.trim().isEmpty()) {
                menge = Integer.parseInt(mengeStr);
                if (menge <= 0) {
                    ctx.status(400).result("menge muss > 0 sein.");
                    return;
                }
            }

            getWarenkorbDao().addArtikelToWarenkorb(email, artikelId, menge);
            ctx.status(201).result("Artikel zum Warenkorb hinzugefügt.");
        } catch (NumberFormatException e) {
            logger.error("Ungültige Eingabe: {}", e.getMessage());
            ctx.status(400).result("artikelId und menge müssen Zahlen sein.");
        } catch (Exception e) {
            logger.error("Fehler beim Hinzufügen zum Warenkorb: {}", e.getMessage(), e);
            ctx.status(500).result("Fehler beim Hinzufügen zum Warenkorb.");
        }
    };

    /**
     * PUT /api/warenkorb/item/{id}
     * Setzt die Menge eines Warenkorb-Items neu.
     * Query-Parameter: menge
     */
    public static Handler updateMenge = ctx -> {
        try {
            String idStr = ctx.pathParam("id");
            int warenkorbItemId = Integer.parseInt(idStr);

            String mengeStr = ctx.queryParam("menge");
            if (mengeStr == null || mengeStr.trim().isEmpty()) {
                ctx.status(400).result("Parameter 'menge' erforderlich.");
                return;
            }

            int menge = Integer.parseInt(mengeStr);
            if (menge <= 0) {
                ctx.status(400).result("menge muss > 0 sein.");
                return;
            }

            getWarenkorbDao().updateMenge(warenkorbItemId, menge);
            ctx.status(200).result("Menge aktualisiert.");
        } catch (NumberFormatException e) {
            logger.error("Ungültige Eingabe: {}", e.getMessage());
            ctx.status(400).result("ID und menge müssen Zahlen sein.");
        } catch (Exception e) {
            logger.error("Fehler beim Aktualisieren der Menge: {}", e.getMessage(), e);
            ctx.status(500).result("Fehler beim Aktualisieren der Menge.");
        }
    };

    /**
     * DELETE /api/warenkorb/item/{id}
     * Löscht ein Warenkorb-Item.
     */
    public static Handler deleteWarenkorbItem = ctx -> {
        try {
            String idStr = ctx.pathParam("id");
            int warenkorbItemId = Integer.parseInt(idStr);
            getWarenkorbDao().deleteWarenkorbItem(warenkorbItemId);
            ctx.status(200).result("Warenkorb-Item gelöscht.");
        } catch (NumberFormatException e) {
            logger.error("Ungültige ID: {}", e.getMessage());
            ctx.status(400).result("ID muss eine Zahl sein.");
        } catch (Exception e) {
            logger.error("Fehler beim Löschen des Warenkorb-Items: {}", e.getMessage(), e);
            ctx.status(500).result("Fehler beim Löschen des Warenkorb-Items.");
        }
    };
}
