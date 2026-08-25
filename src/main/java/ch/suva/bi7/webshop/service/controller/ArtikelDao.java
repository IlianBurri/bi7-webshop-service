package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Artikel;

import java.math.BigDecimal;
import java.util.List;

public interface ArtikelDao {
    List<Artikel> getAllArtikel() throws Exception;

    int addArtikel(String name, BigDecimal preis, String bild) throws Exception;
}