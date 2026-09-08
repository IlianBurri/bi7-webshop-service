package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;

import java.util.List;

public class CheckoutWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbItemEntity> items;

    public CheckoutWarenkorbDao(List<WarenkorbItemEntity> items) {
        this.items = items;
    }

    @Override
    public List<WarenkorbItemEntity> getWarenkorbByUser(String email) {
        return items;
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) {
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) {
        return true;
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) {
        return true;
    }

    @Override
    public boolean clearWarenkorbByUser(String email) {
        return true;
    }
}
