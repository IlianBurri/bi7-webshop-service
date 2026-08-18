package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.util.List;

public interface WarenkorbDao {

    List<WarenkorbItem> getWarenkorbByUser(String email) throws DaoException;

    void addArtikelToWarenkorb(String email, int artikelId, int menge) throws DaoException;

    boolean updateMenge(int warenkorbItemId, int menge) throws DaoException;

    boolean deleteWarenkorbItem(int warenkorbItemId) throws DaoException;
}
