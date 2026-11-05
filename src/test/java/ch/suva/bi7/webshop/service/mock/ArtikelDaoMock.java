package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.math.BigDecimal;
import java.util.List;

public class ArtikelDaoMock implements ArtikelDao {

    private final List<ArtikelEntity> items;
    public Integer letzterMindestbestand;

    public ArtikelDaoMock(List<ArtikelEntity> items) {
        this.items = items;
    }

    @Override
    public List<ArtikelEntity> getAllArtikel() throws Exception {
        return items;
    }

    @Override
    public ArtikelEntity erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception {
        ArtikelEntity artikelEntity = new ArtikelEntity(name, preis, bild);
        items.add(artikelEntity);
        return artikelEntity;
    }

    @Override
    public int fuelleLagerAuf(int mindestbestand) throws Exception {
        letzterMindestbestand = mindestbestand;
        return (int) items.stream().filter(artikel -> artikel.getLagerbestand() < mindestbestand).count();
    }

    @Override
    public ArtikelEntity getArtikelById(Integer artikelId) throws Exception {
        for (ArtikelEntity artikel : items) {
            if (artikel.getArtikelId().equals(artikelId)) {
                return artikel;
            }
        }
        return null;
    }
}
