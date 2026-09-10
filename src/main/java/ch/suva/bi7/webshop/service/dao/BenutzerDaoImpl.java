package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BenutzerDaoImpl implements BenutzerDao {

    private final DBConnection dbConnection;

    public BenutzerDaoImpl(DBConnection dbConnection) {
        if (dbConnection == null) {
            throw new IllegalArgumentException("dbConnection must not be null");
        }
        this.dbConnection = dbConnection;
    }

    @Override
    public Optional<BenutzerEntity> holeBenutzerNachEMail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        ResultSet queryResult = dbConnection.execute("SELECT * FROM user WHERE UPPER(email) = UPPER(?)", email);
        if (queryResult.next()) {
            String username = queryResult.getString("username");
            String password = queryResult.getString("password");
            boolean isAdmin = queryResult.getBoolean("isAdmin");
            BenutzerEntity benutzer = new BenutzerEntity(username, email, password, isAdmin);
            return Optional.of(benutzer);
        }
        return Optional.empty();
    }

    @Override
    public List<String> holeAlleBenutzernamen() throws SQLException {
        ResultSet queryResult = dbConnection.execute("SELECT * FROM user");

        List<String> result = new ArrayList<>();
        while (queryResult.next()) {
            result.add(queryResult.getString("username"));
        }
        return result;
    }

    @Override
    public void speichereBenutzer(BenutzerEntity neuerBenutzer) throws Exception {
        String query = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
        dbConnection.execute(query, neuerBenutzer.getUsername(), neuerBenutzer.getEmail(), neuerBenutzer.getPassword());
    }
}
