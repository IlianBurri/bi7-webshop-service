package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.AdresseDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;

import java.util.List;

public class FehlerAdresseDao implements AdresseDao {

    @Override
    public List<AdresseEntity> ladeAdressenNachBenutzerEmail(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public AdresseEntity insert(AdresseEntity adresse) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean aktualisiere(int adressId, AdresseEntity adresse) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean loesche(int adressId) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean existiertIdentischeAdresse(AdresseEntity adresse) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }
}
