package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> getUserByEMail(String email) throws SQLException;

    List<String> getAllUsernames() throws SQLException;
}
