package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class UserDao {

    //final DBConnection dbConnection;

    private List<User> users = Arrays.asList(
            new User("Steve Rogers", "steve.rogers@microsoft.com", "steve"),
            new User("Tony Stark", "t.stark@industries.com", "tony"),
            new User("Carol Danvers", "cd@amazon.com", "carol")
    );

    private static UserDao userDao = null;

    private UserDao() throws Exception {
       //dbConnection = new DBConnection("localhost", "webshopdb", "webshopuser","webshoppassword");
    }

    static UserDao instance() throws Exception {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }

    Optional<User> getUserByEMail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return users.stream()
                .filter(u -> u.email.toLowerCase().trim().equals(email.toLowerCase().trim()))
                .findAny();
    }

    List<String> getAllUsernames() {
        return users.stream()
                .map(user -> user.username)
                .collect(Collectors.toList());
    }
}
