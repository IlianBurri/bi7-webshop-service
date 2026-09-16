package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.mapper.BenutzerMapper;
import ch.suva.bi7.webshop.service.model.BenutzerDto;

import java.util.List;
import java.util.Optional;

public class BenutzerService {

    private final BenutzerDao benutzerDao;

    public BenutzerService(BenutzerDao benutzerDao) {
        if (benutzerDao == null) {
            throw new IllegalArgumentException("benutzerDao must not be null");
        }
        this.benutzerDao = benutzerDao;
    }

    public List<String> holeAlleBenutzernamen() throws Exception {
        return benutzerDao.holeAlleBenutzernamen();
    }

    public Optional<BenutzerDto> holeBenutzerNachEMail(String email) throws Exception {
        return benutzerDao.holeBenutzerNachEMail(email)
                .map(BenutzerMapper::toDto);
    }

    public Optional<BenutzerEntity> holeBenutzerEntityNachEMail(String email) throws Exception {
        return benutzerDao.holeBenutzerNachEMail(email);
    }

    public void speichereBenutzer(BenutzerEntity benutzer) throws Exception {
        benutzerDao.speichereBenutzer(benutzer);
    }
}
