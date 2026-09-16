package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.service.BestellungService;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BestellungController {

    private static final Logger logger =
            LoggerFactory.getLogger(BestellungController.class);

    public final Handler erstelleBestellung;
    public final Handler ladeBestellungenNachBenutzer;

    public BestellungController(BestellungService bestellungService) {
        if (bestellungService == null) {
            throw new IllegalArgumentException(
                    "bestellungService darf nicht null sein");
        }

        this.erstelleBestellung = ctx -> {
            String sessionEmail = ctx.sessionAttribute("userEmail");
            if (sessionEmail == null) {
                ctx.status(401).result("Nicht eingeloggt.");
                return;
            }

            try {
                Map<String, Object> anfrageDaten = ctx.bodyAsClass(Map.class);
                Object adressIdWert = anfrageDaten.get("adressId");
                if (!(adressIdWert instanceof Number nummer)) {
                    throw new IllegalArgumentException(
                            "adressId muss eine Zahl sein.");
                }

                BestellungService.BestellungErgebnis ergebnis =
                        bestellungService.erstelleBestellung(
                                sessionEmail, nummer.intValue());

                ctx.status(201).json(Map.of(
                        "bestellungId", ergebnis.bestellungId(),
                        "gesamtpreis", ergebnis.gesamtpreis(),
                        "status", ergebnis.status()));
            } catch (IllegalArgumentException e) {
                ctx.status(400).result(e.getMessage());
            } catch (Exception e) {
                logger.error("Fehler beim Erstellen der Bestellung: {}",
                        e.getMessage(), e);
                ctx.status(500).result(
                        "Fehler bei der Bestellabwicklung.");
            }
        };

        this.ladeBestellungenNachBenutzer = ctx -> {
            String email = ctx.pathParam("email");
            try {
                ctx.status(200).json(
                        bestellungService.ladeBestellungenNachBenutzer(email));
            } catch (Exception e) {
                logger.error("Fehler beim Abrufen der Bestellungen: {}",
                        e.getMessage(), e);
                ctx.status(500).result(
                        "Fehler beim Laden der Bestellungen.");
            }
        };
    }
}
