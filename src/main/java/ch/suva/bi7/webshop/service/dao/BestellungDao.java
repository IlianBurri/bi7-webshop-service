package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BestellungDao {

    int erstelleBestellungMitWarenkorbItems(String userEmail, int adressId, BigDecimal gesamtpreis,
                                            List<WarenkorbEintragEntity> warenkorbEintragEntityList) throws DaoException;

    Optional<BestellungEntity> holeBestellungNachId(int bestellungId) throws DaoException;

    List<BestellungEntity> getBestellungenNachBenutzerEmail(String userEmail) throws DaoException;
}
