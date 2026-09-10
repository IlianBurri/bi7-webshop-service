package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.util.List;

public interface WarenkorbDao {

    List<WarenkorbEintragEntity> getWarenkorbNachBenutzer(String email) throws DaoException;

    void fuegeArtikelZuWarenkorbHinzu(String email, int artikelId, int menge) throws DaoException;

    boolean aktualisiereMenge(int warenkorbItemId, int menge) throws DaoException;

    boolean loescheWarenkorbEintrag(int warenkorbItemId) throws DaoException;

    boolean leereWarenkorbNachBenutzer(String email) throws DaoException;
}
