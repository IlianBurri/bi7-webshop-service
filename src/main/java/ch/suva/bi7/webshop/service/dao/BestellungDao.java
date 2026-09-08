package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BestellungDao {

    int createBestellungWithItems(String userEmail, int adressId, BigDecimal gesamtpreis, List<WarenkorbItemEntity> items) throws DaoException;

    Optional<BestellungEntity> getBestellungById(int bestellungId) throws DaoException;

    List<BestellungEntity> getBestellungenByUserEmail(String userEmail) throws DaoException;
}
