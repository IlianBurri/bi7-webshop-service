package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class UserDaoIntegrationTest {

    private UserDao userDao;
    private DBConnection dbConnection;

    @BeforeEach
    void setUp() throws Exception {
        try {
            dbConnection = new DBConnectionImpl("localhost", "webshopdb", "webshopuser","webshoppassword");
            userDao = new UserDaoImpl(dbConnection);
        } catch (Exception e) {
            assumeTrue(false, "MariaDB not available: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (dbConnection == null) {
            return;
        }
        try {
            System.out.println("Aktuelle Benutzer in der user Tabelle");
            System.out.println("---------------------------------------");
            java.sql.ResultSet rs = dbConnection.execute("SELECT username, email FROM user");
            if (rs != null) {
                while (rs.next()) {
                    System.out.println("username: " + rs.getString("username") + " | E-Mail: " + rs.getString("email"));
                }
            }

            dbConnection.execute("DELETE FROM user WHERE email IN " +
                    "(" + "'bruce.wayne@gotham.com', " +
                    "'spidey@dailybugle.com', " +
                    "'hawk.eye@arrow.com', " +
                    "'black.widow@avengers.com')");

        } catch (Exception e) {
            System.out.println("Fehler beim Anzeigen/Aufräumen: " + e.getMessage());
        }
    }

    @Test
    void addUser() throws Exception {
        User testuser = new User("Bruce Wayne", "bruce.wayne@gotham.com", "bruce");

        userDao.addUser(testuser);
        Optional<User> foundUser = userDao.getUserByEMail("bruce.wayne@gotham.com");

        assertTrue(foundUser.isPresent(), "User wurde erfolgreich in der Datenbank registriert sein");
        assertEquals("Bruce Wayne", foundUser.get().username);
    }

    @Test
    void getUserByEMail() throws Exception {
        User testuser = new User("Peter Parker", "spidey@dailybugle.com", "webslinger");
        userDao.addUser(testuser);

        Optional<User> foundUser = userDao.getUserByEMail("spidey@dailybugle.com");
        assertTrue(foundUser.isPresent());
        assertEquals("Peter Parker", foundUser.get().username);
    }

    @Test
    void getAllUsernames() throws Exception {
        userDao.addUser(new User("Hawk Eye", "hawk.eye@arrow.com", "target"));
        userDao.addUser(new User("Black Widow", "black.widow@avengers.com", "spider"));

        List<String> usernames = userDao.getAllUsernames();

        assertNotNull(usernames);
        assertTrue(usernames.contains("Hawk Eye"));
        assertTrue(usernames.contains("Black Widow"));
    }
}