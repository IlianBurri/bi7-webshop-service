package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;

import java.math.BigDecimal;
import java.util.List;

public interface ArtikelDao {
    List<ArtikelEntity> getAllArtikel() throws Exception;

    int erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception;
}
