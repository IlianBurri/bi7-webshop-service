package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Artikel;

import java.util.List;

public interface ArtikelDao {
    List<Artikel> getAllArtikel() throws Exception;
}