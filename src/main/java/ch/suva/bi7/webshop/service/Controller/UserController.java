package ch.suva.bi7.webshop.service.Controller;

import ch.suva.bi7.webshop.service.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class UserController {

    private List<User> users = Arrays.asList(
            new User(0, "Steve Rogers"),
            new User(1, "Tony Stark"),
            new User(2, "Carol Danvers")
    );

    private static UserDao userDao = null;

    private void UserDao() {
    }

    static UserDao instance() {
        if (userDao == null) {
            userDao = new UserDao();
        }
        return userDao;
    }

    Optional<User> getUserById(int id) {
        return users.stream()
                .filter(u -> u.id == id)
                .findAny();
    }

    Iterable<String> getAllUsernames() {
        return users.stream()
                .map(user -> user.name)
                .collect(Collectors.toList());
    }
}

