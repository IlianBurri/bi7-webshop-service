package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.mapper.BenutzerMapper;
import ch.suva.bi7.webshop.service.mock.EinfachesBenutzerDaoMock;
import ch.suva.bi7.webshop.service.model.BenutzerDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FakeBenutzerService extends BenutzerService {

    private final List<BenutzerEntity> benutzerList;
    public int callCount;
    public int registriereBenutzerCallCount;
    public BenutzerEntity gespeicherterBenutzer;
    private final boolean throwException;

    public FakeBenutzerService() {
        this(Collections.emptyList(), false);
    }

    public FakeBenutzerService(List<BenutzerEntity> benutzerList) {
        this(benutzerList, false);
    }

    public FakeBenutzerService(BenutzerEntity benutzer) {
        this(benutzer != null ? List.of(benutzer) : Collections.emptyList(), false);
    }

    public FakeBenutzerService(
            List<BenutzerEntity> benutzerList,
            boolean throwException
    ) {
        super(new EinfachesBenutzerDaoMock(Optional.empty()));
        this.benutzerList = benutzerList != null ? new ArrayList<>(benutzerList) : new ArrayList<>();
        this.throwException = throwException;
    }

    public FakeBenutzerService(
            BenutzerEntity benutzer,
            boolean throwException
    ) {
        this(benutzer != null ? List.of(benutzer) : Collections.emptyList(), throwException);
    }

    @Override
    public List<String> holeAlleBenutzernamen() throws Exception {
        callCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        return benutzerList.stream().map(BenutzerEntity::getUsername).toList();
    }

    @Override
    public Optional<BenutzerDto> holeBenutzerNachEMail(String email) throws Exception {
        callCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        return findeBenutzer(email).map(BenutzerMapper::toDto);
    }

    @Override
    public BenutzerDto registriereBenutzer(String username, String email, String password) throws Exception {
        registriereBenutzerCallCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        BenutzerEntity neuerBenutzer = new BenutzerEntity(username, email, password, false);
        this.gespeicherterBenutzer = neuerBenutzer;
        this.benutzerList.add(neuerBenutzer);
        return BenutzerMapper.toDto(neuerBenutzer);
    }

    @Override
    public boolean istPasswortKorrekt(String email, String password) throws Exception {
        callCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        return findeBenutzer(email)
                .map(benutzer -> benutzer.getPassword().equals(password))
                .orElse(false);
    }

    private Optional<BenutzerEntity> findeBenutzer(String email) {
        return benutzerList.stream()
                .filter(b -> b.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
