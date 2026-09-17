package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.util.ArrayList;
import java.util.List;

public class WarenkorbDaoMock implements WarenkorbDao {

    private final List<WarenkorbEintragEntity> items;
    public boolean updateErgebnis = true;
    public boolean deleteErgebnis = true;
    public String addEmail;
    public Integer addArtikelId;
    public Integer addMenge;
    public Integer updateId;
    public Integer updateNeueMenge;
    public Integer deleteId;
    private boolean throwException = false;

    public WarenkorbDaoMock() {
        this(new ArrayList<>());
    }

    public WarenkorbDaoMock(List<WarenkorbEintragEntity> items) {
        this(items, false);
    }

    public WarenkorbDaoMock(boolean throwException) {
        this(new ArrayList<>(), throwException);
    }

    public WarenkorbDaoMock(List<WarenkorbEintragEntity> items, boolean throwException) {
        this.throwException = throwException;
        this.items = items;
    }

    @Override
    public List<WarenkorbEintragEntity> getWarenkorbNachBenutzer(String email) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return items;
    }

    @Override
    public void fuegeArtikelZuWarenkorbHinzu(String email, int artikelId, int menge) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        addEmail = email;
        addArtikelId = artikelId;
        addMenge = menge;
    }

    @Override
    public boolean aktualisiereMenge(int warenkorbItemId, int menge) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        updateId = warenkorbItemId;
        updateNeueMenge = menge;
        return updateErgebnis;
    }

    @Override
    public boolean loescheWarenkorbEintrag(int warenkorbItemId) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        deleteId = warenkorbItemId;
        return deleteErgebnis;
    }

    @Override
    public boolean leereWarenkorbNachBenutzer(String email) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return true;
    }
}
