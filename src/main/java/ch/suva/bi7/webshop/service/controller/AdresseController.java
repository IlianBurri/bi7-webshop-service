package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.Adresse;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AdresseController {

    private static final Logger logger = LoggerFactory.getLogger(AdresseController.class);

    private static final String STANDARD_LAND = "Schweiz";

    private static AdresseDao adresseDao = null;

    private static AdresseDao getAdresseDao() throws Exception {
        if (adresseDao == null) {
            DBConnection dbConnection = new DBConnectionImpl(DBConfig.getHost(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());
            adresseDao = new AdresseDaoImpl(dbConnection);
        }
        return adresseDao;
    }

    static void setAdresseDaoMock(AdresseDao adresseDaoMock) {
        adresseDao = adresseDaoMock;
    }

    public static Handler getAdressen = ctx -> {
        try {
            String email = ctx.pathParam("email");
            List<Adresse> adressen = getAdresseDao().findByUserEmail(email);
            ctx.status(200).json(adressen);
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Adressen: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Fehler beim Abrufen der Adressen."));
        }
    };


    public static Handler createAdresse = ctx -> {
        try {
            Adresse eingabe = ctx.bodyAsClass(Adresse.class);
            Adresse adresse = validiereUndNormalisiere(eingabe);

            AdresseDao dao = getAdresseDao();
            if (dao.existsIdentical(adresse)) {
                Adresse bestehende = findeBestehendeIdentische(dao, adresse);
                ctx.status(200).json(bestehende);
                return;
            }

            Adresse gespeichert = dao.insert(adresse);
            ctx.status(201).json(gespeichert);
        } catch (BadRequestResponse e) {
            ctx.status(400).json(Map.of("error", "Ungültiger JSON-Request-Body."));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Fehler beim Speichern der Adresse: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Fehler beim Speichern der Adresse."));
        }
    };


    public static Handler updateAdresse = ctx -> {
        try {
            int adressId = Integer.parseInt(ctx.pathParam("adressId"));

            Adresse eingabe = ctx.bodyAsClass(Adresse.class);
            Adresse adresse = validiereUndNormalisiere(eingabe);

            AdresseDao dao = getAdresseDao();
            if (!dao.update(adressId, adresse)) {
                ctx.status(404).json(Map.of("error", "Adresse nicht gefunden"));
                return;
            }

            ctx.status(200).json(mitAdressId(adresse, adressId));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "adressId muss eine Zahl sein."));
        } catch (BadRequestResponse e) {
            ctx.status(400).json(Map.of("error", "Ungültiger JSON-Request-Body."));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Fehler beim Aktualisieren der Adresse: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Fehler beim Aktualisieren der Adresse."));
        }
    };


    public static Handler deleteAdresse = ctx -> {
        try {
            int adressId = Integer.parseInt(ctx.pathParam("adressId"));

            AdresseDao dao = getAdresseDao();
            if (!dao.delete(adressId)) {
                ctx.status(404).json(Map.of("error", "Adresse nicht gefunden"));
                return;
            }

            ctx.status(200).result("Adresse gelöscht.");
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "adressId muss eine Zahl sein."));
        } catch (Exception e) {
            logger.error("Fehler beim Löschen der Adresse: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Fehler beim Löschen der Adresse."));
        }
    };


    private static Adresse validiereUndNormalisiere(Adresse eingabe) {
        String userEmail = pflichtfeld(eingabe.userEmail(), "userEmail");
        String vorname = pflichtfeld(eingabe.vorname(), "vorname");
        String nachname = pflichtfeld(eingabe.nachname(), "nachname");
        String strasse = pflichtfeld(eingabe.strasse(), "strasse");
        String plz = pflichtfeld(eingabe.plz(), "plz");
        String ort = pflichtfeld(eingabe.ort(), "ort");

        String land = eingabe.land();
        if (land == null || land.trim().isEmpty()) {
            land = STANDARD_LAND;
        } else {
            land = land.trim();
        }

        return new Adresse(0, userEmail, vorname, nachname, strasse, plz, ort, land);
    }

    private static Adresse mitAdressId(Adresse adresse, int adressId) {
        return new Adresse(adressId, adresse.userEmail(), adresse.vorname(), adresse.nachname(),
                adresse.strasse(), adresse.plz(), adresse.ort(), adresse.land());
    }

    private static String pflichtfeld(String wert, String feld) {
        if (wert == null || wert.trim().isEmpty()) {
            throw new IllegalArgumentException("'" + feld + "' ist ein Pflichtfeld und darf nicht leer sein.");
        }
        return wert.trim();
    }

    private static Adresse findeBestehendeIdentische(AdresseDao dao, Adresse adresse) throws Exception {
        List<Adresse> vorhandene = dao.findByUserEmail(adresse.userEmail());
        for (Adresse a : vorhandene) {
            if (istIdentisch(a, adresse)) {
                return a;
            }
        }
        return adresse;
    }

    private static boolean istIdentisch(Adresse a, Adresse b) {
        return a.vorname().equals(b.vorname())
                && a.nachname().equals(b.nachname())
                && a.strasse().equals(b.strasse())
                && a.plz().equals(b.plz())
                && a.ort().equals(b.ort())
                && a.land().equals(b.land());
    }
}
