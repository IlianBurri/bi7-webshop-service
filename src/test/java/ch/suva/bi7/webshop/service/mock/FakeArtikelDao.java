package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;

import java.math.BigDecimal;
import java.util.List;

public class FakeArtikelDao implements ArtikelDao {

    private final List<ArtikelEntity> artikel;
    public int callCount;
    public int addArtikelCallCount;
    public int generierterKey = 1;

    public FakeArtikelDao(List<ArtikelEntity> artikel) {
        this.artikel = artikel;
    }

    @Override
    public List<ArtikelEntity> getAllArtikel() {
        callCount++;
        return artikel;
    }

    @Override
    public int addNewArtikel(String name, BigDecimal preis, String bild) {
        addArtikelCallCount++;
        return generierterKey;
    }
}
