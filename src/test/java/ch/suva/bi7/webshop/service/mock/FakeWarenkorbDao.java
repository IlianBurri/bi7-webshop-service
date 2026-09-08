package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;

import java.util.List;

public class FakeWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbItemEntity> items;
    public boolean updateErgebnis = true;
    public boolean deleteErgebnis = true;
    public String addEmail;
    public Integer addArtikelId;
    public Integer addMenge;
    public Integer updateId;
    public Integer updateNeueMenge;
    public Integer deleteId;

    public FakeWarenkorbDao(List<WarenkorbItemEntity> items) {
        this.items = items;
    }

    @Override
    public List<WarenkorbItemEntity> getWarenkorbByUser(String email) {
        return items;
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) {
        addEmail = email;
        addArtikelId = artikelId;
        addMenge = menge;
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) {
        updateId = warenkorbItemId;
        updateNeueMenge = menge;
        return updateErgebnis;
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) {
        deleteId = warenkorbItemId;
        return deleteErgebnis;
    }

    @Override
    public boolean clearWarenkorbByUser(String email) {
        return true;
    }
}
