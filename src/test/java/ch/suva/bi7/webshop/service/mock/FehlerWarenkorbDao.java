package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.util.List;

public class FehlerWarenkorbDao implements WarenkorbDao {

    @Override
    public List<WarenkorbEintragEntity> getWarenkorbNachBenutzer(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public void fuegeArtikelZuWarenkorbHinzu(String email, int artikelId, int menge) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean aktualisiereMenge(int warenkorbItemId, int menge) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean loescheWarenkorbEintrag(int warenkorbItemId) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean leereWarenkorbNachBenutzer(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }
}
