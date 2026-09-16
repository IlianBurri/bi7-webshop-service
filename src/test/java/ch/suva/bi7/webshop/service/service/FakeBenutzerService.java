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
    public int speichereBenutzerCallCount;
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
        return benutzerList.stream()
                .filter(b -> b.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .map(BenutzerMapper::toDto);
    }

    @Override
    public Optional<BenutzerEntity> holeBenutzerEntityNachEMail(String email) throws Exception {
        callCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        return benutzerList.stream()
                .filter(b -> b.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public void speichereBenutzer(BenutzerEntity benutzer) throws Exception {
        speichereBenutzerCallCount++;
        if (throwException) {
            throw new Exception("Datenbank Fehler");
        }
        this.gespeicherterBenutzer = benutzer;
        this.benutzerList.add(benutzer);
    }
}
