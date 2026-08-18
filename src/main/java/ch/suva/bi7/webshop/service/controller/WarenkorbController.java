package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WarenkorbController {

    private static final Logger logger = LoggerFactory.getLogger(WarenkorbController.class);

    public final Handler getWarenkorb;
    public final Handler addToWarenkorb;
    public final Handler updateMenge;
    public final Handler deleteWarenkorbItem;

    public WarenkorbController(WarenkorbDao warenkorbDao) {
        if (warenkorbDao == null) {
            throw new IllegalArgumentException("warenkorbDao must not be null");
        }

        this.getWarenkorb = ctx -> {
            try {
                String email = ctx.pathParam("email");
                List<WarenkorbItem> items = warenkorbDao.getWarenkorbByUser(email);
                ctx.status(200).json(items);
            } catch (Exception e) {
                logger.error("Fehler beim Abrufen des Warenkorbs: {}", e.getMessage(), e);
                ctx.status(500).result("Fehler beim Abrufen des Warenkorbs.");
            }
        };

        this.addToWarenkorb = ctx -> {
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

                warenkorbDao.addArtikelToWarenkorb(email, artikelId, menge);
                ctx.status(201).result("Artikel zum Warenkorb hinzugefügt.");
            } catch (NumberFormatException e) {
                logger.error("Ungültige Eingabe: {}", e.getMessage());
                ctx.status(400).result("artikelId und menge müssen Zahlen sein.");
            } catch (Exception e) {
                logger.error("Fehler beim Hinzufügen zum Warenkorb: {}", e.getMessage(), e);
                ctx.status(500).result("Fehler beim Hinzufügen zum Warenkorb.");
            }
        };

        this.updateMenge = ctx -> {
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

                if (!warenkorbDao.updateMenge(warenkorbItemId, menge)) {
                    ctx.status(404).result("Warenkorb-Item nicht gefunden.");
                    return;
                }

                ctx.status(200).result("Menge aktualisiert.");
            } catch (NumberFormatException e) {
                logger.error("Ungültige Eingabe: {}", e.getMessage());
                ctx.status(400).result("ID und menge müssen Zahlen sein.");
            } catch (Exception e) {
                logger.error("Fehler beim Aktualisieren der Menge: {}", e.getMessage(), e);
                ctx.status(500).result("Fehler beim Aktualisieren der Menge.");
            }
        };

        this.deleteWarenkorbItem = ctx -> {
            try {
                String idStr = ctx.pathParam("id");
                int warenkorbItemId = Integer.parseInt(idStr);
                if (!warenkorbDao.deleteWarenkorbItem(warenkorbItemId)) {
                    ctx.status(404).result("Warenkorb-Item nicht gefunden.");
                    return;
                }
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
}
