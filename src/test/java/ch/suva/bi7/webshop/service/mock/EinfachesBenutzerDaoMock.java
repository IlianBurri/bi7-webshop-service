package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EinfachesBenutzerDaoMock implements BenutzerDao {
    public BenutzerEntity gespeicherterBenutzer = null;
    private Optional<BenutzerEntity> vorgegebenerBenutzer;

    public EinfachesBenutzerDaoMock(Optional<BenutzerEntity> vorgegebenerBenutzer) {
        this.vorgegebenerBenutzer = vorgegebenerBenutzer;
    }

    @Override
    public void speichereBenutzer(BenutzerEntity user) throws Exception {
        this.gespeicherterBenutzer = user;
    }

    @Override
    public Optional<BenutzerEntity> holeBenutzerNachEMail(String email) {
        return vorgegebenerBenutzer;
    }

    @Override
    public List<String> holeAlleBenutzernamen() {
        return new ArrayList<>();
    }
}
