package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.util.List;

public interface WarenkorbDao {

    List<WarenkorbItem> getWarenkorbByUser(String email) throws Exception;

    void addArtikelToWarenkorb(String email, int artikelId, int menge) throws Exception;

    void updateMenge(int warenkorbItemId, int menge) throws Exception;

    void deleteWarenkorbItem(int warenkorbItemId) throws Exception;
}
