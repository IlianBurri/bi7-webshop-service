package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.math.BigDecimal;
import java.util.List;

public class ArtikelDaoMock implements ArtikelDao {

    private final List<ArtikelEntity> items;

    public ArtikelDaoMock(List<ArtikelEntity> items) {
        this.items = items;
    }

    @Override
    public List<ArtikelEntity> getAllArtikel() throws Exception {
        return items;
    }

    @Override
    public ArtikelEntity erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception {
        return null;
    }

    @Override
    public ArtikelEntity getArtikelById(Integer artikelId) {
        return items.get(0);
    }
}
