package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.AddArtikelRequest;
import ch.suva.bi7.webshop.service.model.AddArtikelResponse;
import ch.suva.bi7.webshop.service.model.ArtikelDto;
import ch.suva.bi7.webshop.service.model.BenutzerDto;
import ch.suva.bi7.webshop.service.service.ArtikelService;
import ch.suva.bi7.webshop.service.service.BenutzerService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class ArtikelController {

    private final Logger logger = LoggerFactory.getLogger(ArtikelController.class);

    private final BigDecimal mindestpreis = new BigDecimal("0.01");
    private final BigDecimal maximalpreis = new BigDecimal("99999999.99");
    private final int maxNameLaenge = 255;
    private final int maxBildLaenge = 500;

    private ArtikelService artikelService;
    private BenutzerService benutzerService;

    public ArtikelController(ArtikelService artikelService, BenutzerService benutzerService) {
        if (artikelService == null) {
            throw new IllegalArgumentException("artikelService must not be null");
        }
        if (benutzerService == null) {
            throw new IllegalArgumentException("benutzerService must not be null");
        }
        this.artikelService = artikelService;
        this.benutzerService = benutzerService;
    }

    public Handler ladeAlleArtikel = ctx -> {
        try {
            ctx.status(200).json(artikelService.getAllArtikel());
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Artikel: {}", e.getMessage(), e);

            ctx.status(500).result("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    };

    public Handler erstelleNeuenArtikel = ctx -> {
        String email = ctx.sessionAttribute("userEmail");
        logger.info("Benutzer '{}' ist Admin, erstelle Artikel...", email);

        Optional<BenutzerDto> benutzerOptional = benutzerService.holeBenutzerNachEMail(email);

        if (benutzerOptional.isEmpty()) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("error", "Nicht angemeldet: " + email));
            return;
        }

        if (!benutzerOptional.get().isAdmin()) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("error", "Nur Administratoren dürfen Artikel anlegen."));
            return;
        }

        try {
            AddArtikelRequest eingabe = ctx.bodyAsClass(AddArtikelRequest.class);

            String name = eingabe.name == null ? null : eingabe.name.trim();
            String bild = eingabe.bild == null ? null : eingabe.bild.trim();

            validiere(name, eingabe.preis, bild);

            ArtikelDto artikelDto = artikelService.erstelleNeuenArtikel(name, eingabe.preis, bild);
            logger.info("Artikel erfolgreich von '{}' erstellt: {}", email, artikelDto);
            ctx.status(201).json(new AddArtikelResponse(artikelDto));
        } catch (BadRequestResponse e) {
            ctx.status(400).json(Map.of("error", "Ungültiger JSON-Request-Body."));
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.warn("AddArtikel abgelehnt: {}", e.getMessage());
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen des Artikels: {}", e.getMessage(), e);
            ctx.status(500).result("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    };

    void validiere(String name, BigDecimal preis, String bild) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("'name' ist ein Pflichtfeld und darf nicht leer sein.");
        }
        if (name.length() > maxNameLaenge) {
            throw new IllegalArgumentException("'name' darf höchstens " + maxNameLaenge + " Zeichen lang sein.");
        }
        if (preis == null) {
            throw new IllegalArgumentException("'preis' ist ein Pflichtfeld.");
        }
        if (preis.compareTo(mindestpreis) < 0 || preis.compareTo(maximalpreis) > 0) {
            throw new IllegalArgumentException(
                    "'preis' muss zwischen " + mindestpreis + " und " + maximalpreis + " liegen.");
        }
        if (bild != null && bild.length() > maxBildLaenge) {
            throw new IllegalArgumentException("'bild' darf höchstens " + maxBildLaenge + " Zeichen lang sein.");
        }
    }
}
