package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;

import java.util.List;

public interface WarenkorbDao {

    List<WarenkorbItemEntity> getWarenkorbByUser(String email) throws DaoException;

    void addArtikelToWarenkorb(String email, int artikelId, int menge) throws DaoException;

    boolean updateMenge(int warenkorbItemId, int menge) throws DaoException;

    boolean deleteWarenkorbItem(int warenkorbItemId) throws DaoException;

    boolean clearWarenkorbByUser(String email) throws DaoException;
}
