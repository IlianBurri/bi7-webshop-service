package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.AdresseDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.model.AdresseDto;
import ch.suva.bi7.webshop.service.model.DtoAndEntetyMapper;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AdresseController {

    private static final Logger logger = LoggerFactory.getLogger(AdresseController.class);

    private static final String STANDARD_LAND = "Schweiz";

    private final AdresseDao adresseDao;

    public final Handler getAdressen;
    public final Handler createAdresse;
    public final Handler updateAdresse;
    public final Handler deleteAdresse;

    public AdresseController(AdresseDao adresseDao) {
        if (adresseDao == null) {
            throw new IllegalArgumentException("adresseDao must not be null");
        }
        this.adresseDao = adresseDao;

        this.getAdressen = ctx -> {
            try {
                String email = ctx.pathParam("email");
                List<AdresseEntity> adressen = adresseDao.findByUserEmail(email);
                List<AdresseDto> response = adressen.stream()
                        .map(DtoAndEntetyMapper::adresseEntity2Dto)
                        .toList();
                ctx.status(200).json(response);
            } catch (Exception e) {
                logger.error("Fehler beim Abrufen der Adressen: {}", e.getMessage(), e);
                ctx.status(500).json(Map.of("error", "Fehler beim Abrufen der Adressen."));
            }
        };

        this.createAdresse = ctx -> {
            try {
                AdresseDto eingabe = ctx.bodyAsClass(AdresseDto.class);
                AdresseEntity adresse = validiereUndNormalisiere(eingabe);

                if (adresseDao.existsIdentical(adresse)) {
                    AdresseEntity bestehende = findeBestehendeIdentische(adresseDao, adresse);
                    ctx.status(200).json(DtoAndEntetyMapper.adresseEntity2Dto(bestehende));
                    return;
                }

                AdresseEntity gespeichert = adresseDao.insert(adresse);
                ctx.status(201).json(DtoAndEntetyMapper.adresseEntity2Dto(gespeichert));
            } catch (BadRequestResponse e) {
                ctx.status(400).json(Map.of("error", "Ungültiger JSON-Request-Body."));
            } catch (IllegalArgumentException e) {
                ctx.status(400).json(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                logger.error("Fehler beim Speichern der Adresse: {}", e.getMessage(), e);
                ctx.status(500).json(Map.of("error", "Fehler beim Speichern der Adresse."));
            }
        };

        this.updateAdresse = ctx -> {
            try {
                int adressId = Integer.parseInt(ctx.pathParam("adressId"));

                AdresseDto eingabe = ctx.bodyAsClass(AdresseDto.class);
                AdresseEntity adresse = validiereUndNormalisiere(eingabe);

                if (!adresseDao.update(adressId, adresse)) {
                    ctx.status(404).json(Map.of("error", "Adresse nicht gefunden"));
                    return;
                }

                ctx.status(200).json(DtoAndEntetyMapper.adresseEntity2Dto(mitAdressId(adresse, adressId)));
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

        this.deleteAdresse = ctx -> {
            try {
                int adressId = Integer.parseInt(ctx.pathParam("adressId"));

                if (!adresseDao.delete(adressId)) {
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
    }

    private static AdresseEntity validiereUndNormalisiere(AdresseDto eingabe) {
        String userEmail = pflichtfeld(eingabe.getUserEmail(), "userEmail");
        String vorname = pflichtfeld(eingabe.getVorname(), "vorname");
        String nachname = pflichtfeld(eingabe.getNachname(), "nachname");
        String strasse = pflichtfeld(eingabe.getStrasse(), "strasse");
        String plz = pflichtfeld(eingabe.getPlz(), "plz");
        String ort = pflichtfeld(eingabe.getOrt(), "ort");

        String land = eingabe.getLand();
        if (land == null || land.trim().isEmpty()) {
            land = STANDARD_LAND;
        } else {
            land = land.trim();
        }

        return new AdresseEntity(null, userEmail, vorname, nachname, strasse, plz, ort, land);
    }

    private static AdresseEntity mitAdressId(AdresseEntity adresse, int adressId) {
        return new AdresseEntity(adressId, adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand());
    }

    private static String pflichtfeld(String wert, String feld) {
        if (wert == null || wert.trim().isEmpty()) {
            throw new IllegalArgumentException("'" + feld + "' ist ein Pflichtfeld und darf nicht leer sein.");
        }
        return wert.trim();
    }

    private static AdresseEntity findeBestehendeIdentische(AdresseDao dao, AdresseEntity adresse) throws DaoException {
        List<AdresseEntity> vorhandene = dao.findByUserEmail(adresse.getUserEmail());
        for (AdresseEntity a : vorhandene) {
            if (istIdentisch(a, adresse)) {
                return a;
            }
        }
        return adresse;
    }

    private static boolean istIdentisch(AdresseEntity a, AdresseEntity b) {
        return a.getVorname().equals(b.getVorname())
                && a.getNachname().equals(b.getNachname())
                && a.getStrasse().equals(b.getStrasse())
                && a.getPlz().equals(b.getPlz())
                && a.getOrt().equals(b.getOrt())
                && a.getLand().equals(b.getLand());
    }

}
