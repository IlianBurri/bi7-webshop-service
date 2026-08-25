package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.AddArtikelRequest;
import ch.suva.bi7.webshop.service.model.Artikel;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ArtikelController {

    private static final Logger logger = LoggerFactory.getLogger(ArtikelController.class);

    private static final BigDecimal MINDESTPREIS = new BigDecimal("0.01");
    private static final BigDecimal MAXIMALPREIS = new BigDecimal("99999999.99");
    private static final int MAX_NAME_LAENGE = 255;
    private static final int MAX_BILD_LAENGE = 500;

    private static ArtikelDao artikelDao = null;

    public ArtikelController(ArtikelDao artikelDao) {
        if (artikelDao == null) {
            throw new IllegalArgumentException("artikelDao must not be null");
        }
        this.artikelDao = artikelDao;
    }

    private static ArtikelDao getArtikelDao() throws Exception {
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

    public static Handler addArtikel = ctx -> {
        if (!AuthUtil.requireAdmin(ctx)) {
            return;
        }

        String email = ctx.sessionAttribute("userEmail");
        logger.info("Benutzer '{}' ist Admin, erstelle Artikel...", email);

        try {
            AddArtikelRequest eingabe = ctx.bodyAsClass(AddArtikelRequest.class);

            String name = eingabe.name == null ? null : eingabe.name.trim();
            String bild = eingabe.bild == null ? null : eingabe.bild.trim();

            validiere(name, eingabe.preis, bild);

            int artikelId = getArtikelDao().addArtikel(name, eingabe.preis, bild);
            Artikel artikel = new Artikel(artikelId, name, eingabe.preis, bild);

            logger.info("Artikel erfolgreich von '{}' erstellt: {}", email, artikel);
            ctx.status(201).json(artikel);
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