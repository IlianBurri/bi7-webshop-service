package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;

import java.math.BigDecimal;
import java.util.List;

public class FehlerArtikelDao implements ArtikelDao {

    public int callCount;

    @Override
    public List<ArtikelEntity> getAllArtikel() throws Exception {
        callCount++;
        throw new Exception("Datenbank Fehler");
    }

    @Override
    public int erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception {
        callCount++;
        throw new Exception("Datenbank Fehler");
    }
}
