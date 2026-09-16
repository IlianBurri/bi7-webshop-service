package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.AdresseDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.mapper.AdresseMapper;
import ch.suva.bi7.webshop.service.model.AdresseDto;

import java.util.List;
import java.util.Optional;

public class AdresseService {

    private final AdresseDao adresseDao;

    public AdresseService(AdresseDao adresseDao) {
        if (adresseDao == null) {
            throw new IllegalArgumentException("adresseDao must not be null");
        }
        this.adresseDao = adresseDao;
    }

    public List<AdresseDto> ladeAdressen(String email) throws DaoException {
        return adresseDao.ladeAdressenNachBenutzerEmail(email)
                .stream()
                .map(AdresseMapper::toDto)
                .toList();
    }

    public Optional<AdresseDto> findeIdentischeAdresse(
            AdresseEntity adresse
    ) throws DaoException {
        if (adresse == null) {
            throw new IllegalArgumentException("adresse must not be null");
        }
        if (!adresseDao.existiertIdentischeAdresse(adresse)) {
            return Optional.empty();
        }

        Optional<AdresseDto> bestehendeAdresse =
                adresseDao.ladeAdressenNachBenutzerEmail(adresse.getUserEmail())
                        .stream()
                        .filter(bestehende -> istIdentisch(bestehende, adresse))
                        .findFirst()
                        .map(AdresseMapper::toDto);
        return bestehendeAdresse.isPresent()
                ? bestehendeAdresse
                : Optional.of(AdresseMapper.toDto(adresse));
    }

    public AdresseDto erstelleAdresse(AdresseEntity adresse)
            throws DaoException {
        if (adresse == null) {
            throw new IllegalArgumentException("adresse must not be null");
        }
        return AdresseMapper.toDto(adresseDao.insert(adresse));
    }

    public Optional<AdresseDto> aktualisiereAdresse(
            int adressId,
            AdresseEntity adresse
    ) throws DaoException {
        if (adresse == null) {
            throw new IllegalArgumentException("adresse must not be null");
        }
        if (!adresseDao.aktualisiere(adressId, adresse)) {
            return Optional.empty();
        }

        AdresseEntity aktualisierteAdresse = new AdresseEntity(
                adressId,
                adresse.getUserEmail(),
                adresse.getVorname(),
                adresse.getNachname(),
                adresse.getStrasse(),
                adresse.getPlz(),
                adresse.getOrt(),
                adresse.getLand()
        );
        return Optional.of(AdresseMapper.toDto(aktualisierteAdresse));
    }

    public boolean loescheAdresse(int adressId) throws DaoException {
        return adresseDao.loesche(adressId);
    }

    private boolean istIdentisch(AdresseEntity a, AdresseEntity b) {
        return a.getVorname().equals(b.getVorname())
                && a.getNachname().equals(b.getNachname())
                && a.getStrasse().equals(b.getStrasse())
                && a.getPlz().equals(b.getPlz())
                && a.getOrt().equals(b.getOrt())
                && a.getLand().equals(b.getLand());
    }
}
