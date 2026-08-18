package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private final DBConnection dbConnection;

    public UserDaoImpl(DBConnection dbConnection) {
        if (dbConnection == null) {
            throw new IllegalArgumentException("dbConnection must not be null");
        }
        this.dbConnection = dbConnection;
    }

    @Override
    public Optional<User> getUserByEMail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        ResultSet queryResult = dbConnection.execute("SELECT * FROM user WHERE UPPER(email) = UPPER(?)", email);
        if (queryResult.next()) {
            String username = queryResult.getString("username");
            String password = queryResult.getString("password");
            User user = new User(username, email, password);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<String> getAllUsernames() throws SQLException {
        ResultSet queryResult = dbConnection.execute("SELECT * FROM user");

        List<String> result = new ArrayList<>();
        while (queryResult.next()) {
            result.add(queryResult.getString("username"));
        }
        return result;
    }

    @Override
    public void addUser(User newUser) throws Exception {
        String query = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
        dbConnection.execute(query, newUser.username, newUser.email, newUser.password);
    }
}
