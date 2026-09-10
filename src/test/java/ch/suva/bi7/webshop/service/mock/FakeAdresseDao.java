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
    public AdresseEntity aktualisiereAdresse;
    public Integer deleteId;

    public FakeAdresseDao(List<AdresseEntity> adressen) {
        this.adressen = adressen;
    }

    @Override
    public List<AdresseEntity> ladeAdressenNachBenutzerEmail(String email) {
        return adressen;
    }

    @Override
    public AdresseEntity insert(AdresseEntity adresse) {
        gespeicherteAdresse = adresse;
        return new AdresseEntity(42, adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand());
    }

    @Override
    public boolean aktualisiere(int adressId, AdresseEntity adresse) {
        updateId = adressId;
        aktualisiereAdresse = adresse;
        return updateErgebnis;
    }

    @Override
    public boolean loesche(int adressId) {
        deleteId = adressId;
        return deleteErgebnis;
    }

    @Override
    public boolean existiertIdentischeAdresse(AdresseEntity adresse) {
        return existsIdenticalErgebnis;
    }
}
