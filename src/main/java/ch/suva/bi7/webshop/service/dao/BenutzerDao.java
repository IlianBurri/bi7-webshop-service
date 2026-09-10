package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BenutzerDao {
    Optional<BenutzerEntity> holeBenutzerNachEMail(String email) throws SQLException;

    List<String> holeAlleBenutzernamen() throws SQLException;

    void speichereBenutzer(BenutzerEntity neuerBenutzer) throws Exception;
}
