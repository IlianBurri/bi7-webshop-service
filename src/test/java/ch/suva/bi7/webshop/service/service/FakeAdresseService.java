package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.model.AdresseDto;
import ch.suva.bi7.webshop.service.mock.FakeAdresseDao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FakeAdresseService extends AdresseService {

    private final List<AdresseDto> adressen;
    public int callCount;
    public int addAdressCallCount;
    private final boolean throwException;

    public FakeAdresseService(List<AdresseDto> adressen) {
        this(adressen, false);
    }

    public FakeAdresseService(
            List<AdresseDto> adressen,
            boolean throwException
    ) {
        super(new FakeAdresseDao(Collections.emptyList()));
        this.adressen = adressen;
        this.throwException = throwException;
    }

    @Override
    public List<AdresseDto> ladeAdressen(String email)
            throws DaoException {
        callCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return adressen;
    }

    @Override
    public AdresseDto erstelleAdresse(AdresseEntity adresse)
            throws DaoException {
        addAdressCallCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        if (adressen == null || adressen.isEmpty()) {
            return null;
        }
        return adressen.get(0);
    }

    @Override
    public Optional<AdresseDto> findeIdentischeAdresse(
            AdresseEntity adresse
    ) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return adressen == null || adressen.isEmpty()
                ? Optional.empty()
                : Optional.of(adressen.get(0));
    }

    @Override
    public Optional<AdresseDto> aktualisiereAdresse(
            int adressId,
            AdresseEntity adresse
    ) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return adressen == null || adressen.isEmpty()
                ? Optional.empty()
                : Optional.of(adressen.get(0));
    }

    @Override
    public boolean loescheAdresse(int adressId)
            throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return true;
    }
}
