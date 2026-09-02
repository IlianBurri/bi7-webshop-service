package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Bestellung;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class BestellungController {

    private static final Logger logger = LoggerFactory.getLogger(BestellungController.class);

    public final Handler createBestellung;
    public final Handler getBestellungenByUser;

    public BestellungController(BestellungDao bestellungDao, WarenkorbDao warenkorbDao) {
        if (bestellungDao == null || warenkorbDao == null) {
            throw new IllegalArgumentException("DAOs dürfen nicht null sein");
        }

        this.createBestellung = ctx -> {
            String sessionEmail = ctx.sessionAttribute("userEmail");
            if (sessionEmail == null) {
                ctx.status(401).result("Nicht eingeloggt.");
                return;
            }

            try {
                Map<String, Object> body = ctx.bodyAsClass(Map.class);
                int adressId = ((Number) body.get("adressId")).intValue();

                List<WarenkorbItem> cartItems = warenkorbDao.getWarenkorbByUser(sessionEmail);
                if (cartItems.isEmpty()) {
                    ctx.status(400).result("Warenkorb ist leer.");
                    return;
                }

                BigDecimal gesamtpreis = cartItems.stream()
                        .map(item -> item.getArtikelPreis().multiply(BigDecimal.valueOf(item.getMenge())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                int bestellungId = bestellungDao.createBestellungWithItems(sessionEmail, adressId, gesamtpreis, cartItems);

                ctx.status(201).json(Map.of(
                        "bestellungId", bestellungId,
                        "gesamtpreis", gesamtpreis,
                        "status", "BEZAHLT"));
            } catch (Exception e) {
                logger.error("Fehler beim Erstellen der Bestellung: {}", e.getMessage(), e);
                ctx.status(500).result("Fehler bei der Bestellabwicklung.");
            }
        };

        this.getBestellungenByUser = ctx -> {
            String email = ctx.pathParam("email");
            try {
                List<Bestellung> bestellungen = bestellungDao.getBestellungenByUserEmail(email);
                ctx.status(200).json(bestellungen);
            } catch (Exception e) {
                logger.error("Fehler beim Abrufen der Bestellungen: {}", e.getMessage(), e);
                ctx.status(500).result("Fehler beim Laden der Bestellungen.");
            }
        };
    }
}