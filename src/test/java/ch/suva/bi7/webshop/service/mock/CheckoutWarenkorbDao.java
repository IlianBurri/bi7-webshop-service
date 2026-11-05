package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.util.List;
import java.util.Optional;

public class CheckoutWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbEintragEntity> items;
    public boolean wurdeGeleert;

    public CheckoutWarenkorbDao(List<WarenkorbEintragEntity> items) {
        this.items = items;
    }

    @Override
    public List<WarenkorbEintragEntity> getWarenkorbNachBenutzer(String email) {
        return items;
    }

    @Override
    public Optional<WarenkorbEintragEntity> getWarenkorbEintragById(int warenkorbItemId) {
        return items.stream()
                .filter(eintrag -> eintrag.getWarenkorbItemId() == warenkorbItemId)
                .findFirst();
    }

    @Override
    public void fuegeArtikelZuWarenkorbHinzu(String email, Integer artikelId, int menge) {
    }

    @Override
    public boolean aktualisiereMenge(int warenkorbItemId, int menge) {
        return true;
    }

    @Override
    public boolean loescheWarenkorbEintrag(int warenkorbItemId) {
        return true;
    }

    @Override
    public boolean leereWarenkorbNachBenutzer(String email) {
        wurdeGeleert = true;
        return true;
    }
}
