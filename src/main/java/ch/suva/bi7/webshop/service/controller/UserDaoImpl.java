package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UserDaoImpl implements UserDao {

    final DBConnection dbConnection;

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
        ResultSet queryResult = dbConnection.execute("SELECT * FROM user where upper(email)='" + email.toUpperCase() + "'");
        if (queryResult.next()) {
            String username = queryResult.getString("username");
            String password = queryResult.getString("password");
            User user = new User(username, email, password);
            System.out.println("User aus DB gelesen: '" + email + "', '" + username + "', '" + password + "'");
            return Optional.of(user);
        }
        System.out.println("User nicht gefunden: '" + email + "'");
        return Optional.empty();
    }

    @Override
    public List<String> getAllUsernames() throws SQLException {
        ResultSet queryResult = dbConnection.execute("SELECT * FROM user");

        List<String> result = new ArrayList<>();
        while (queryResult.next()) {
            String email = queryResult.getString("email");
            String username = queryResult.getString("username");
            String password = queryResult.getString("password");
            System.out.println("User aus DB gelesen: '" + email + "', '" + username + "', '" + password + "'");
            result.add(username);
        }
        return result;
    }
}
