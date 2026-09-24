package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.CheckoutRequest;
import ch.suva.bi7.webshop.service.model.CheckoutResponse;
import ch.suva.bi7.webshop.service.service.BestellungService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BestellungController {

    private final Logger logger =
            LoggerFactory.getLogger(BestellungController.class);

    private BestellungService bestellungService;

    public BestellungController(BestellungService bestellungService) {
        if (bestellungService == null) {
            throw new IllegalArgumentException(
                    "bestellungService darf nicht null sein");
        }
        this.bestellungService = bestellungService;
    }

    public Handler erstelleBestellung = ctx -> {
        String sessionEmail = ctx.sessionAttribute("userEmail");
        if (sessionEmail == null) {
            ctx.status(401).result("Nicht eingeloggt.");
            return;
        }

        try {
            CheckoutRequest anfrage = ctx.bodyAsClass(CheckoutRequest.class);

            BestellungService.BestellungErgebnis ergebnis =
                    bestellungService.erstelleBestellung(
                            sessionEmail, anfrage.adressId);

            ctx.status(201).json(new CheckoutResponse(
                    ergebnis.bestellungId(),
                    ergebnis.gesamtpreis(),
                    ergebnis.status()));
        } catch (BadRequestResponse e) {
            ctx.status(400).result("Ungültiger JSON-Request-Body.");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            logger.error("Fehler beim Erstellen der Bestellung: {}",
                    e.getMessage(), e);
            ctx.status(500).result(
                    "Fehler bei der Bestellabwicklung.");
        }
    };

    public Handler ladeBestellungenNachBenutzer = ctx -> {
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
