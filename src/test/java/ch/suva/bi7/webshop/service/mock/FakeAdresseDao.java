package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.AdresseDao;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;

import java.util.List;

public class FakeAdresseDao implements AdresseDao {

    private final List<AdresseEntity> adressen;
    public boolean existsIdenticalErgebnis;
    public boolean updateErgebnis = true;
    public boolean deleteErgebnis = true;
    public AdresseEntity gespeicherteAdresse;
    public Integer updateId;
    public AdresseEntity updateAdresse;
    public Integer deleteId;

    public FakeAdresseDao(List<AdresseEntity> adressen) {
        this.adressen = adressen;
    }

    @Override
    public List<AdresseEntity> findByUserEmail(String email) {
        return adressen;
    }

    @Override
    public AdresseEntity insert(AdresseEntity adresse) {
        gespeicherteAdresse = adresse;
        return new AdresseEntity(42, adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand());
    }

    @Override
    public boolean update(int adressId, AdresseEntity adresse) {
        updateId = adressId;
        updateAdresse = adresse;
        return updateErgebnis;
    }

    @Override
    public boolean delete(int adressId) {
        deleteId = adressId;
        return deleteErgebnis;
    }

    @Override
    public boolean existsIdentical(AdresseEntity adresse) {
        return existsIdenticalErgebnis;
    }
}
