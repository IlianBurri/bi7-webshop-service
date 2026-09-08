package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;

import java.util.List;

public interface AdresseDao {

    List<AdresseEntity> findByUserEmail(String email) throws DaoException;

    AdresseEntity insert(AdresseEntity adresse) throws DaoException;

    boolean update(int adressId, AdresseEntity adresse) throws DaoException;

    boolean delete(int adressId) throws DaoException;

    boolean existsIdentical(AdresseEntity adresse) throws DaoException;
}
