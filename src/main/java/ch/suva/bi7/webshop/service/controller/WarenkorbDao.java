package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.util.List;

public interface WarenkorbDao {

    List<WarenkorbItem> getWarenkorbByUser(String email) throws DaoException;

    void addArtikelToWarenkorb(String email, int artikelId, int menge) throws DaoException;

    /**
     * Setzt die Menge eines Warenkorb-Items neu.
     *
     * @return {@code true}, wenn genau ein Datensatz aktualisiert wurde,
     *         {@code false} wenn die id nicht existiert (→ Controller kann 404 liefern)
     */
    boolean updateMenge(int warenkorbItemId, int menge) throws DaoException;

    /**
     * Löscht ein Warenkorb-Item.
     *
     * @return {@code true}, wenn genau ein Datensatz gelöscht wurde,
     *         {@code false} wenn die id nicht existiert (→ Controller kann 404 liefern)
     */
    boolean deleteWarenkorbItem(int warenkorbItemId) throws DaoException;
}
