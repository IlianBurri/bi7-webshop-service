package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.model.AdresseDto;
import ch.suva.bi7.webshop.service.service.AdresseService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class AdresseController {

    private static final Logger logger =
            LoggerFactory.getLogger(AdresseController.class);

    private static final String STANDARD_LAND = "Schweiz";

    public final Handler ladeAdressen;
    public final Handler erstelleAdresse;
    public final Handler aktualisiereAdresse;
    public final Handler loescheAdresse;

    public AdresseController(AdresseService adresseService) {
        if (adresseService == null) {
            throw new IllegalArgumentException(
                    "adresseService must not be null");
        }

        this.ladeAdressen = ctx -> {
            try {
                String email = ctx.pathParam("email");
                ctx.status(200).json(adresseService.ladeAdressen(email));
            } catch (Exception e) {
                logger.error("Fehler beim Abrufen der Adressen: {}",
                        e.getMessage(), e);
                ctx.status(500).json(
                        Map.of("error", "Fehler beim Abrufen der Adressen."));
            }
        };

        this.erstelleAdresse = ctx -> {
            try {
                AdresseDto eingabe = ctx.bodyAsClass(AdresseDto.class);
                AdresseEntity adresse = validiereUndNormalisiere(eingabe);

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
        };

        this.aktualisiereAdresse = ctx -> {
            try {
                int adressId = Integer.parseInt(ctx.pathParam("adressId"));
                AdresseDto eingabe = ctx.bodyAsClass(AdresseDto.class);
                AdresseEntity adresse = validiereUndNormalisiere(eingabe);

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
        };

        this.loescheAdresse = ctx -> {
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
        };
    }

    private static AdresseEntity validiereUndNormalisiere(AdresseDto eingabe) {
        String userEmail = pflichtfeld(eingabe.getUserEmail(), "userEmail");
        String vorname = pflichtfeld(eingabe.getVorname(), "vorname");
        String nachname = pflichtfeld(eingabe.getNachname(), "nachname");
        String strasse = pflichtfeld(eingabe.getStrasse(), "strasse");
        String plz = pflichtfeld(eingabe.getPlz(), "plz");
        String ort = pflichtfeld(eingabe.getOrt(), "ort");

        String land = eingabe.getLand();
        land = land == null || land.trim().isEmpty()
                ? STANDARD_LAND
                : land.trim();

        return new AdresseEntity(
                null, userEmail, vorname, nachname, strasse, plz, ort, land);
    }

    private static String pflichtfeld(String wert, String feld) {
        if (wert == null || wert.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "'" + feld
                            + "' ist ein Pflichtfeld und darf nicht leer sein.");
        }
        return wert.trim();
    }
}
