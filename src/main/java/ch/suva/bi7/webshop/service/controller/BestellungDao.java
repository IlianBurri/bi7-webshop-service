package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Bestellung;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BestellungDao {

    int createBestellungWithItems(String userEmail, int adressId, BigDecimal gesamtpreis, List<WarenkorbItem> items) throws DaoException;

    Optional<Bestellung> getBestellungById(int bestellungId) throws DaoException;

    List<Bestellung> getBestellungenByUserEmail(String userEmail) throws DaoException;
}