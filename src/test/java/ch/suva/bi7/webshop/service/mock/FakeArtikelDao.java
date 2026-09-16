package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;

import java.math.BigDecimal;
import java.util.List;

public class FakeArtikelDao implements ArtikelDao {
    @Override
    public List<ArtikelEntity> getAllArtikel() throws Exception {
        return List.of();
    }

    @Override
    public ArtikelEntity erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception {
        return null;
    }
}
