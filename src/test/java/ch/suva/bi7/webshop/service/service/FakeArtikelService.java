package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.mock.FakeArtikelDao;
import ch.suva.bi7.webshop.service.model.ArtikelDto;

import java.math.BigDecimal;
import java.util.List;

public class FakeArtikelService extends ArtikelService {

    private final List<ArtikelDto> artikel;
    public int callCount;
    public int addArtikelCallCount;
    public int generierterKey = 1;
    private boolean throwException;

    public FakeArtikelService(List<ArtikelDto> artikel) {
        this(artikel, false);
    }

    public FakeArtikelService(List<ArtikelDto> artikel, boolean throwException) {
        super(new FakeArtikelDao());
        this.artikel = artikel;
        this.throwException = throwException;
    }

    @Override
    public List<ArtikelDto> getAllArtikel() throws Exception {
        callCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        return artikel;
    }

    @Override
    public ArtikelDto erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception {
        addArtikelCallCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        if (artikel == null || artikel.isEmpty()) {
            return null;
        }
        return artikel.get(0);
    }
}
