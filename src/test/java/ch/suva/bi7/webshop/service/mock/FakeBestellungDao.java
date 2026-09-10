package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class FakeBestellungDao implements BestellungDao {

    public boolean wurdeAufgerufen;
    public String letzterUserEmail;
    public int letzteAdressId;
    public BigDecimal letzterGesamtpreis;
    public List<WarenkorbEintragEntity> letzteItems;

    @Override
    public int erstelleBestellungMitWarenkorbItems(String userEmail, int adressId, BigDecimal gesamtpreis,
                                                   List<WarenkorbEintragEntity> items) {
        wurdeAufgerufen = true;
        letzterUserEmail = userEmail;
        letzteAdressId = adressId;
        letzterGesamtpreis = gesamtpreis;
        letzteItems = items;
        return 5;
    }

    @Override
    public Optional<BestellungEntity> holeBestellungNachId(int bestellungId) {
        return Optional.empty();
    }

    @Override
    public List<BestellungEntity> getBestellungenNachBenutzerEmail(String userEmail) {
        return List.of();
    }
}
