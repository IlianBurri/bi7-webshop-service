package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;

import java.util.List;

public class FehlerWarenkorbDao implements WarenkorbDao {

    @Override
    public List<WarenkorbItemEntity> getWarenkorbByUser(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean clearWarenkorbByUser(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }
}
