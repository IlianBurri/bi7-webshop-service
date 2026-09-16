package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.service.WarenkorbService;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarenkorbController {

    private final Logger logger =
            LoggerFactory.getLogger(WarenkorbController.class);

    private WarenkorbService warenkorbService;

    public WarenkorbController(WarenkorbService warenkorbService) {
        if (warenkorbService == null) {
            throw new IllegalArgumentException(
                    "warenkorbService must not be null");
        }
        this.warenkorbService = warenkorbService;
    }

    public Handler ladeWarenkorb = ctx -> {
        try {
            ctx.status(200).json(warenkorbService.ladeWarenkorb(
                    ctx.pathParam("email")));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen des Warenkorbs: {}",
                    e.getMessage(), e);
            ctx.status(500).result(
                    "Fehler beim Abrufen des Warenkorbs.");
        }
    };

    public Handler fuegeArtikelZuWarenkorbHinzu = ctx -> {
        try {
            String mengeParameter = ctx.queryParam("menge");
            int menge = mengeParameter == null
                    || mengeParameter.trim().isEmpty()
                    ? 1
                    : Integer.parseInt(mengeParameter);

            warenkorbService.fuegeArtikelHinzu(
                    ctx.queryParam("email"),
                    Integer.parseInt(ctx.queryParam("artikelId")),
                    menge);
            ctx.status(201).result(
                    "Artikel zum Warenkorb hinzugefügt.");
        } catch (NumberFormatException e) {
            ctx.status(400).result(
                    "artikelId und menge müssen Zahlen sein.");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            logger.error("Fehler beim Hinzufügen zum Warenkorb: {}",
                    e.getMessage(), e);
            ctx.status(500).result(
                    "Fehler beim Hinzufügen zum Warenkorb.");
        }
    };

    public Handler aktualisiereMenge = ctx -> {
        try {
            int itemId = Integer.parseInt(ctx.pathParam("id"));
            int menge = Integer.parseInt(ctx.queryParam("menge"));
            if (!warenkorbService.aktualisiereMenge(itemId, menge)) {
                ctx.status(404).result(
                        "Warenkorb-Item nicht gefunden.");
                return;
            }
            ctx.status(200).result("Menge aktualisiert.");
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID und menge müssen Zahlen sein.");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            logger.error("Fehler beim Aktualisieren der Menge: {}",
                    e.getMessage(), e);
            ctx.status(500).result(
                    "Fehler beim Aktualisieren der Menge.");
        }
    };

    public Handler loescheWarenkorbEintrag = ctx -> {
        try {
            int itemId = Integer.parseInt(ctx.pathParam("id"));
            if (!warenkorbService.loescheWarenkorbEintrag(itemId)) {
                ctx.status(404).result(
                        "Warenkorb-Item nicht gefunden.");
                return;
            }
            ctx.status(200).result("Warenkorb-Item gelöscht.");
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID muss eine Zahl sein.");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            logger.error("Fehler beim Löschen des Warenkorb-Items: {}",
                    e.getMessage(), e);
            ctx.status(500).result(
                    "Fehler beim Löschen des Warenkorb-Items.");
        }
    };
}
