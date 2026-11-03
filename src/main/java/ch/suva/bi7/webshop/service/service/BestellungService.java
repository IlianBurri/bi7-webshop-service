package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.mapper.BestellungMapper;
import ch.suva.bi7.webshop.service.model.BestellungDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BestellungService {

    private final BestellungDao bestellungDao;
    private final WarenkorbDao warenkorbDao;
    private final ArtikelDao artikelDao;

    public BestellungService(
            BestellungDao bestellungDao,
            WarenkorbDao warenkorbDao,
            ArtikelDao artikelDao
    ) {
        if (bestellungDao == null || warenkorbDao == null || artikelDao == null) {
            throw new IllegalArgumentException("DAOs dürfen nicht null sein");
        }
        this.bestellungDao = bestellungDao;
        this.warenkorbDao = warenkorbDao;
        this.artikelDao = artikelDao;
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

        Map<Integer, BigDecimal> artikelPreise = new HashMap<>();
        for (WarenkorbEintragEntity item : warenkorb) {
            ArtikelEntity artikel;
            try {
                artikel = artikelDao.getArtikelById(item.getArtikelId());
            } catch (Exception e) {
                throw new DaoException(
                        "Artikel " + item.getArtikelId() + " konnte nicht geladen werden.", e);
            }
            if (artikel == null) {
                throw new IllegalArgumentException(
                        "Artikel " + item.getArtikelId() + " nicht gefunden.");
            }
            artikelPreise.put(item.getArtikelId(), artikel.getPreis());
        }

        BigDecimal gesamtpreis = warenkorb.stream()
                .map(item -> artikelPreise.get(item.getArtikelId())
                        .multiply(BigDecimal.valueOf(item.getMenge())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int bestellungId = bestellungDao.erstelleBestellungMitWarenkorbItems(
                userEmail, adressId, gesamtpreis, warenkorb, artikelPreise);

        return new BestellungErgebnis(bestellungId, gesamtpreis, "BEZAHLT");
    }

    public record BestellungErgebnis(
            int bestellungId,
            BigDecimal gesamtpreis,
            String status
    ) {
    }
}
