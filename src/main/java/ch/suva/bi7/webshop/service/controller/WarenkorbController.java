package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;
import ch.suva.bi7.webshop.service.model.WarenkorbDto;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

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
                if (email == null || email.trim().isEmpty()) {
                    ctx.status(400).result("Parameter 'email' erforderlich.");
                    return;
                }

                List<WarenkorbItemEntity> items = warenkorbDao.getWarenkorbByUser(email);
                List<WarenkorbDto> response = items.stream()
                        .map(WarenkorbController::warenkorbEntity2Dto)
                        .collect(Collectors.toList());
                ctx.status(200).json(response);
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
                if (artikelId <= 0) {
                    ctx.status(400).result("artikelId muss > 0 sein.");
                    return;
                }

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
                if (warenkorbItemId <= 0) {
                    ctx.status(400).result("ID muss > 0 sein.");
                    return;
                }

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
                if (warenkorbItemId <= 0) {
                    ctx.status(400).result("ID muss > 0 sein.");
                    return;
                }
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

    static WarenkorbDto warenkorbEntity2Dto(WarenkorbItemEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("WarenkorbItemEntity darf nicht null sein");
        }

        return new WarenkorbDto(
                entity.getWarenkorbItemId(),
                entity.getArtikelId(),
                entity.getMenge(),
                entity.getArtikelName(),
                entity.getArtikelPreis(),
                entity.getArtikelBild()
        );
    }
}
