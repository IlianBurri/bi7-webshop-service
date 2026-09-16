package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.mock.FakeBestellungDao;
import ch.suva.bi7.webshop.service.mock.FakeWarenkorbDao;
import ch.suva.bi7.webshop.service.model.BestellungDto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FakeBestellungService extends BestellungService {

    private final List<BestellungDto> bestellungen;
    public int callCount;
    public int addBestellungCallCount;
    public int generierterKey = 1;
    public BigDecimal gesamtpreis = BigDecimal.ZERO;
    private final boolean throwException;

    public FakeBestellungService(List<BestellungDto> bestellungen) {
        this(bestellungen, false);
    }

    public FakeBestellungService(
            List<BestellungDto> bestellungen,
            boolean throwException
    ) {
        super(new FakeBestellungDao(), new FakeWarenkorbDao(Collections.emptyList()));
        this.bestellungen = bestellungen;
        this.throwException = throwException;
    }

    @Override
    public List<BestellungDto> ladeBestellungenNachBenutzer(
            String email
    ) throws DaoException {
        callCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return bestellungen;
    }

    @Override
    public Optional<BestellungDto> holeBestellungNachId(
            int bestellungId
    ) throws DaoException {
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        return bestellungen == null || bestellungen.isEmpty()
                ? Optional.empty()
                : Optional.of(bestellungen.get(0));
    }

    @Override
    public BestellungErgebnis erstelleBestellung(
            String userEmail,
            int adressId
    ) throws DaoException {
        addBestellungCallCount++;
        if (throwException) {
            throw new DaoException("Datenbank Fehler");
        }
        if (bestellungen == null || bestellungen.isEmpty()) {
            return new BestellungErgebnis(generierterKey, gesamtpreis, "BEZAHLT");
        }
        BestellungDto erste = bestellungen.get(0);
        return new BestellungErgebnis(
                erste.getBestellungId() != null ? erste.getBestellungId() : generierterKey,
                erste.getGesamtpreis() != null ? erste.getGesamtpreis() : gesamtpreis,
                erste.getStatus() != null ? erste.getStatus() : "BEZAHLT"
        );
    }
}
