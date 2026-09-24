package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.AdresseDto;
import ch.suva.bi7.webshop.service.model.AdresseRequest;
import ch.suva.bi7.webshop.service.service.AdresseService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class AdresseController {

    private final Logger logger =
            LoggerFactory.getLogger(AdresseController.class);

    private final String standardLand = "Schweiz";

    private final AdresseService adresseService;

    public final Handler ladeAdressen = this::ladeAdressen;
    public final Handler erstelleAdresse = this::erstelleAdresse;
    public final Handler aktualisiereAdresse = this::aktualisiereAdresse;
    public final Handler loescheAdresse = this::loescheAdresse;

    public AdresseController(AdresseService adresseService) {
        if (adresseService == null) {
            throw new IllegalArgumentException(
                    "adresseService must not be null");
        }
        this.adresseService = adresseService;
    }

    private void ladeAdressen(Context ctx) {
        try {
            String email = ctx.pathParam("email");
            ctx.status(200).json(adresseService.ladeAdressen(email));
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Adressen: {}",
                    e.getMessage(), e);
            ctx.status(500).json(
                    Map.of("error", "Fehler beim Abrufen der Adressen."));
        }
    }

    private void erstelleAdresse(Context ctx) {
        try {
            AdresseRequest eingabe = ctx.bodyAsClass(AdresseRequest.class);
            AdresseDto adresse = validiereUndNormalisiere(eingabe);

            Optional<AdresseDto> bestehende =
                    adresseService.findeIdentischeAdresse(adresse);
            if (bestehende.isPresent()) {
                ctx.status(200).json(bestehende.get());
                return;
            }

            ctx.status(201).json(adresseService.erstelleAdresse(adresse));
        } catch (BadRequestResponse e) {
            ctx.status(400).json(
                    Map.of("error", "Ungültiger JSON-Request-Body."));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Fehler beim Speichern der Adresse: {}",
                    e.getMessage(), e);
            ctx.status(500).json(
                    Map.of("error", "Fehler beim Speichern der Adresse."));
        }
    }

    private void aktualisiereAdresse(Context ctx) {
        try {
            int adressId = Integer.parseInt(ctx.pathParam("adressId"));
            AdresseRequest eingabe = ctx.bodyAsClass(AdresseRequest.class);
            AdresseDto adresse = validiereUndNormalisiere(eingabe);

            Optional<AdresseDto> aktualisiert =
                    adresseService.aktualisiereAdresse(adressId, adresse);
            if (aktualisiert.isEmpty()) {
                ctx.status(404).json(
                        Map.of("error", "Adresse nicht gefunden"));
                return;
            }

            ctx.status(200).json(aktualisiert.get());
        } catch (NumberFormatException e) {
            ctx.status(400).json(
                    Map.of("error", "adressId muss eine Zahl sein."));
        } catch (BadRequestResponse e) {
            ctx.status(400).json(
                    Map.of("error", "Ungültiger JSON-Request-Body."));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Fehler beim Aktualisieren der Adresse: {}",
                    e.getMessage(), e);
            ctx.status(500).json(Map.of(
                    "error", "Fehler beim Aktualisieren der Adresse."));
        }
    }

    private void loescheAdresse(Context ctx) {
        try {
            int adressId = Integer.parseInt(ctx.pathParam("adressId"));
            if (!adresseService.loescheAdresse(adressId)) {
                ctx.status(404).json(
                        Map.of("error", "Adresse nicht gefunden"));
                return;
            }
            ctx.status(200).result("Adresse gelöscht.");
        } catch (NumberFormatException e) {
            ctx.status(400).json(
                    Map.of("error", "adressId muss eine Zahl sein."));
        } catch (Exception e) {
            logger.error("Fehler beim Löschen der Adresse: {}",
                    e.getMessage(), e);
            ctx.status(500).json(
                    Map.of("error", "Fehler beim Löschen der Adresse."));
        }
    }

    AdresseDto validiereUndNormalisiere(AdresseRequest eingabe) {
        if (eingabe == null) {
            throw new IllegalArgumentException(
                    "Der Request-Body darf nicht leer sein.");
        }

        String userEmail = pflichtfeld(eingabe.userEmail, "userEmail");
        String vorname = pflichtfeld(eingabe.vorname, "vorname");
        String nachname = pflichtfeld(eingabe.nachname, "nachname");
        String strasse = pflichtfeld(eingabe.strasse, "strasse");
        String plz = pflichtfeld(eingabe.plz, "plz");
        String ort = pflichtfeld(eingabe.ort, "ort");

        String land = eingabe.land;
        land = land == null || land.trim().isEmpty()
                ? standardLand
                : land.trim();

        return new AdresseDto(
                null, userEmail, vorname, nachname, strasse, plz, ort, land);
    }

    private String pflichtfeld(String wert, String feld) {
        if (wert == null || wert.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "'" + feld
                            + "' ist ein Pflichtfeld und darf nicht leer sein.");
        }
        return wert.trim();
    }
}
