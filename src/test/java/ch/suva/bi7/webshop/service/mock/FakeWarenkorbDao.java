package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.util.List;

public class FakeWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbEintragEntity> items;
    public boolean updateErgebnis = true;
    public boolean deleteErgebnis = true;
    public String addEmail;
    public Integer addArtikelId;
    public Integer addMenge;
    public Integer updateId;
    public Integer updateNeueMenge;
    public Integer deleteId;

    public FakeWarenkorbDao(List<WarenkorbEintragEntity> items) {
        this.items = items;
    }

    @Override
    public List<WarenkorbEintragEntity> getWarenkorbNachBenutzer(String email) {
        return items;
    }

    @Override
    public void fuegeArtikelZuWarenkorbHinzu(String email, int artikelId, int menge) {
        addEmail = email;
        addArtikelId = artikelId;
        addMenge = menge;
    }

    @Override
    public boolean aktualisiereMenge(int warenkorbItemId, int menge) {
        updateId = warenkorbItemId;
        updateNeueMenge = menge;
        return updateErgebnis;
    }

    @Override
    public boolean loescheWarenkorbEintrag(int warenkorbItemId) {
        deleteId = warenkorbItemId;
        return deleteErgebnis;
    }

    @Override
    public boolean leereWarenkorbNachBenutzer(String email) {
        return true;
    }
}
