package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.model.AddArtikelRequest;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.model.AddArtikelResponse;
import ch.suva.bi7.webshop.service.model.ArtikelDto;
import ch.suva.bi7.webshop.service.service.ArtikelService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class ArtikelController {

    private static final Logger logger = LoggerFactory.getLogger(ArtikelController.class);

    private static final BigDecimal MINDESTPREIS = new BigDecimal("0.01");
    private static final BigDecimal MAXIMALPREIS = new BigDecimal("99999999.99");
    private static final int MAX_NAME_LAENGE = 255;
    private static final int MAX_BILD_LAENGE = 500;

    private ArtikelService artikelService;
    private static BenutzerDao benutzerDao = null;

    public ArtikelController(ArtikelService artikelService, BenutzerDao benutzerDao) {
        if (artikelService == null) {
            throw new IllegalArgumentException("artikelService must not be null");
        }
        if (benutzerDao == null) {
            throw new IllegalArgumentException("benutzerDao must not be null");
        }
        this.artikelService = artikelService;
        this.benutzerDao = benutzerDao;
    }

    private ArtikelService getArtikelService() throws Exception {
        return artikelService;
    }

    public Handler ladeAlleArtikel = ctx -> {
        try {
            ctx.status(200).json(getArtikelService().getAllArtikel());
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Artikel: {}", e.getMessage(), e);

            ctx.status(500).result("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    };

    public Handler erstelleNeuenArtikel = ctx -> {
        String email = ctx.sessionAttribute("userEmail");
        logger.info("Benutzer '{}' ist Admin, erstelle Artikel...", email);

        Optional<BenutzerEntity> benutzerOptional = benutzerDao.holeBenutzerNachEMail(email);

        if (benutzerOptional.isEmpty()) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(java.util.Map.of("error", "Nicht angemeldet: " + email));
            return;
        }

        if (!benutzerOptional.get().isAdmin()) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(java.util.Map.of("error", "Nur Administratoren dürfen Artikel anlegen."));
            return;
        }

        try {
            AddArtikelRequest eingabe = ctx.bodyAsClass(AddArtikelRequest.class);

            String name = eingabe.name == null ? null : eingabe.name.trim();
            String bild = eingabe.bild == null ? null : eingabe.bild.trim();

            validiere(name, eingabe.preis, bild);

            ArtikelDto artikelDto = getArtikelService().erstelleNeuenArtikel(name, eingabe.preis, bild);
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

      static void validiere(String name, BigDecimal preis, String bild) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("'name' ist ein Pflichtfeld und darf nicht leer sein.");
        }
        if (name.length() > MAX_NAME_LAENGE) {
            throw new IllegalArgumentException("'name' darf höchstens " + MAX_NAME_LAENGE + " Zeichen lang sein.");
        }
        if (preis == null) {
            throw new IllegalArgumentException("'preis' ist ein Pflichtfeld.");
        }
        if (preis.compareTo(MINDESTPREIS) < 0 || preis.compareTo(MAXIMALPREIS) > 0) {
            throw new IllegalArgumentException(
                    "'preis' muss zwischen " + MINDESTPREIS + " und " + MAXIMALPREIS + " liegen.");
        }
        if (bild != null && bild.length() > MAX_BILD_LAENGE) {
            throw new IllegalArgumentException("'bild' darf höchstens " + MAX_BILD_LAENGE + " Zeichen lang sein.");
        }
    }
}
