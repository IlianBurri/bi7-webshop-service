package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.mapper.WarenkorbEintragMapper;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;

import java.util.List;

public class WarenkorbService {

    private final WarenkorbDao warenkorbDao;
    private final ArtikelDao artikelDao;

    public WarenkorbService(WarenkorbDao warenkorbDao , ArtikelDao artikelDao) {
        if (warenkorbDao == null) {
            throw new IllegalArgumentException(
                    "warenkorbDao must not be null");
        }
        this.warenkorbDao = warenkorbDao;
        this.artikelDao = artikelDao;
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
            Integer artikelId,
            int menge
    ) throws DaoException {
        pruefeEmail(email);
        if (artikelId == null) {
            throw new IllegalArgumentException("artikel darf nicht null sein.");
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein.");
        }
        ArtikelEntity artikel = null;
        try {
            artikel = artikelDao.getArtikelById(artikelId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        warenkorbDao.fuegeArtikelZuWarenkorbHinzu(email, artikel, menge);
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
