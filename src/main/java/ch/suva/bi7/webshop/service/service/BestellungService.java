package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.mapper.BestellungMapper;
import ch.suva.bi7.webshop.service.model.BestellungDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BestellungService {

    private final BestellungDao bestellungDao;
    private final WarenkorbDao warenkorbDao;

    public BestellungService(
            BestellungDao bestellungDao,
            WarenkorbDao warenkorbDao
    ) {
        if (bestellungDao == null || warenkorbDao == null) {
            throw new IllegalArgumentException("DAOs dürfen nicht null sein");
        }
        this.bestellungDao = bestellungDao;
        this.warenkorbDao = warenkorbDao;
    }

    public List<BestellungDto> ladeBestellungenNachBenutzer(
            String email
    ) throws DaoException {
        List<BestellungEntity> bestellungen =
                bestellungDao.getBestellungenNachBenutzerEmail(email);

        return bestellungen.stream()
                .map(BestellungMapper::toDto)
                .toList();
    }

    public Optional<BestellungDto> holeBestellungNachId(
            int bestellungId
    ) throws DaoException {
        return bestellungDao.holeBestellungNachId(bestellungId)
                .map(BestellungMapper::toDto);
    }

    public BestellungErgebnis erstelleBestellung(
            String userEmail,
            int adressId
    ) throws DaoException {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "userEmail darf nicht null/leer sein");
        }
        if (adressId <= 0) {
            throw new IllegalArgumentException("adressId muss > 0 sein");
        }

        List<WarenkorbEintragEntity> warenkorb =
                warenkorbDao.getWarenkorbNachBenutzer(userEmail);
        if (warenkorb.isEmpty()) {
            throw new IllegalArgumentException("Warenkorb ist leer.");
        }

        BigDecimal gesamtpreis = warenkorb.stream()
                .map(item -> item.getArtikelPreis()
                        .multiply(BigDecimal.valueOf(item.getMenge())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int bestellungId = bestellungDao.erstelleBestellungMitWarenkorbItems(
                userEmail, adressId, gesamtpreis, warenkorb);

        return new BestellungErgebnis(bestellungId, gesamtpreis, "BEZAHLT");
    }

    public record BestellungErgebnis(
            int bestellungId,
            BigDecimal gesamtpreis,
            String status
    ) {
    }
}
