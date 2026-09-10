package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;
import ch.suva.bi7.webshop.service.model.BestellungDto;
import ch.suva.bi7.webshop.service.mapper.BestellungMapper;
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

                List<WarenkorbItemEntity> warenkorbItemEntityList = warenkorbDao.getWarenkorbByUser(sessionEmail);
                if (warenkorbItemEntityList.isEmpty()) {
                    ctx.status(400).result("Warenkorb ist leer.");
                    return;
                }

                BigDecimal gesamtpreis = warenkorbItemEntityList.stream()
                        .map(warenkorbItemEntity -> warenkorbItemEntity.getArtikelPreis()
                                .multiply(BigDecimal.valueOf(warenkorbItemEntity.getMenge())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                int bestellungId = bestellungDao.erstelleBestellungMitWarenkorbItems(
                        sessionEmail, adressId, gesamtpreis, warenkorbItemEntityList);

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
                List<BestellungEntity> bestellungEntityList = bestellungDao.getBestellungenByUserEmail(email);
                List<BestellungDto> bestellungDtoList = bestellungEntityList.stream()
                        .map(BestellungMapper::toDto)
                        .toList();
                ctx.status(200).json(bestellungDtoList);
            } catch (Exception e) {
                logger.error("Fehler beim Abrufen der Bestellungen: {}", e.getMessage(), e);
                ctx.status(500).result("Fehler beim Laden der Bestellungen.");
            }
        };
    }
}
