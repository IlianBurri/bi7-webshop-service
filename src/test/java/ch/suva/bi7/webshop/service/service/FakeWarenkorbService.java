package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.mock.WarenkorbDaoMock;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;

import java.util.Collections;
import java.util.List;

public class FakeWarenkorbService extends WarenkorbService {

    private final List<WarenkorbEintragDto> eintraege;
    public int callCount;
    public int addArtikelCallCount;
    public int updateMengeCallCount;
    public int deleteCallCount;
    public int leereWarenkorbCallCount;
    public boolean updateErgebnis = true;
    public boolean deleteErgebnis = true;
    public boolean leereErgebnis = true;
    public String lastAddedEmail;
    public int lastAddedArtikelId;
    public int lastAddedMenge;
    public int lastUpdatedId;
    public int lastUpdatedMenge;
    public int lastDeletedId;
    private final boolean throwException;

    public FakeWarenkorbService() {
        this(Collections.emptyList(), false);
    }

    public FakeWarenkorbService(List<WarenkorbEintragDto> eintraege) {
        this(eintraege, false);
    }

    public FakeWarenkorbService(
            List<WarenkorbEintragDto> eintraege,
            boolean throwException
    ) {
        super(new WarenkorbDaoMock(Collections.emptyList()));
        this.eintraege = eintraege;
        this.throwException = throwException;
    }

    @Override
    public List<WarenkorbEintragDto> ladeWarenkorb(String email) throws DaoException {
        callCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return eintraege;
    }

    @Override
    public void fuegeArtikelHinzu(
            String email,
            int artikelId,
            int menge
    ) throws DaoException {
        addArtikelCallCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        this.lastAddedEmail = email;
        this.lastAddedArtikelId = artikelId;
        this.lastAddedMenge = menge;
    }

    @Override
    public boolean aktualisiereMenge(
            int warenkorbItemId,
            int menge
    ) throws DaoException {
        updateMengeCallCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        this.lastUpdatedId = warenkorbItemId;
        this.lastUpdatedMenge = menge;
        return updateErgebnis;
    }

    @Override
    public boolean loescheWarenkorbEintrag(int warenkorbItemId) throws DaoException {
        deleteCallCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        this.lastDeletedId = warenkorbItemId;
        return deleteErgebnis;
    }

    @Override
    public boolean leereWarenkorb(String email) throws DaoException {
        leereWarenkorbCallCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return leereErgebnis;
    }
}
