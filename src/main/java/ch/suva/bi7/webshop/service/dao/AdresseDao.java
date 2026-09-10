package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;

import java.util.List;

public interface AdresseDao {

    List<AdresseEntity> ladeAdressenNachBenutzerEmail(String email) throws DaoException;

    AdresseEntity insert(AdresseEntity adresse) throws DaoException;

    boolean aktualisiere(int adressId, AdresseEntity adresse) throws DaoException;

    boolean loesche(int adressId) throws DaoException;

    boolean existiertIdentischeAdresse(AdresseEntity adresse) throws DaoException;
}
