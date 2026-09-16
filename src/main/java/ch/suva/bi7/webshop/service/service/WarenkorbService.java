package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.mapper.WarenkorbEintragMapper;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;

import java.util.List;

public class WarenkorbService {

    private final WarenkorbDao warenkorbDao;

    public WarenkorbService(WarenkorbDao warenkorbDao) {
        if (warenkorbDao == null) {
            throw new IllegalArgumentException(
                    "warenkorbDao must not be null");
        }
        this.warenkorbDao = warenkorbDao;
    }

    public List<WarenkorbEintragDto> ladeWarenkorb(String email)
            throws DaoException {
        pruefeEmail(email);
        return warenkorbDao.getWarenkorbNachBenutzer(email)
                .stream()
                .map(WarenkorbEintragMapper::toDto)
                .toList();
    }

    public void fuegeArtikelHinzu(
            String email,
            int artikelId,
            int menge
    ) throws DaoException {
        pruefeEmail(email);
        if (artikelId <= 0) {
            throw new IllegalArgumentException("artikelId muss > 0 sein.");
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein.");
        }
        warenkorbDao.fuegeArtikelZuWarenkorbHinzu(email, artikelId, menge);
    }

    public boolean aktualisiereMenge(
            int warenkorbItemId,
            int menge
    ) throws DaoException {
        if (warenkorbItemId <= 0) {
            throw new IllegalArgumentException("ID muss > 0 sein.");
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein.");
        }
        return warenkorbDao.aktualisiereMenge(warenkorbItemId, menge);
    }

    public boolean loescheWarenkorbEintrag(int warenkorbItemId)
            throws DaoException {
        if (warenkorbItemId <= 0) {
            throw new IllegalArgumentException("ID muss > 0 sein.");
        }
        return warenkorbDao.loescheWarenkorbEintrag(warenkorbItemId);
    }

    public boolean leereWarenkorb(String email) throws DaoException {
        pruefeEmail(email);
        return warenkorbDao.leereWarenkorbNachBenutzer(email);
    }

    private void pruefeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Parameter 'email' erforderlich.");
        }
    }
}
