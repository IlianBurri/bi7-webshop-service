package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Adresse;

import java.util.List;

public interface AdresseDao {

    List<Adresse> findByUserEmail(String email) throws Exception;

    Adresse insert(Adresse adresse) throws Exception;

    boolean update(int adressId, Adresse adresse) throws Exception;

    boolean delete(int adressId) throws Exception;

    boolean existsIdentical(Adresse adresse) throws Exception;
}
