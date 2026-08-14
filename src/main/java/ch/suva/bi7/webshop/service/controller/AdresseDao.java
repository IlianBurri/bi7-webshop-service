package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Adresse;

import java.util.List;

public interface AdresseDao {

    List<Adresse> findByUserEmail(String email) throws DaoException;

    Adresse insert(Adresse adresse) throws DaoException;

    boolean update(int adressId, Adresse adresse) throws DaoException;

    boolean delete(int adressId) throws DaoException;

    boolean existsIdentical(Adresse adresse) throws DaoException;
}
