package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.model.User;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoImplTest {

    @Test
    void getUserByEMailLiefertBenutzer() throws SQLException {
        // Arrange
        ResultSet resultSet = createResultSetMock(List.of(user("testuser", "test", "test@somewhere.com")));
        DBConnection dbConnection = createDBConnectionMock(resultSet, 0);
        UserDaoImpl testee = createTestee(dbConnection);

        // Act
        Optional<User> userOptional = testee.getUserByEMail("test@somewhere.com");

        // Assert
        assertTrue(userOptional.isPresent());
        assertEquals("testuser", userOptional.get().getUsername());
        assertEquals("test", userOptional.get().getPassword());
    }

    @Test
    void getAllUsernamesLiefertAlleBenutzernamen() throws SQLException {
        ResultSet resultSet = createResultSetMock(List.of(
                user("testuser", "test", "test@somewhere.com"),
                user("testuser2", "test2", "test2@somewhere.com"),
                user("testuser3", "test3", "test3@somewhere.com")));
        DBConnection dbConnection = createDBConnectionMock(resultSet, 0);

        UserDaoImpl testee = createTestee(dbConnection);

        List<String> resultUsernames = testee.getAllUsernames();

        // Prüft Anzahl, Inhalt UND Reihenfolge in einem Schritt
        assertEquals(
                List.of("testuser", "testuser2", "testuser3"),
                resultUsernames,
                "Es sollten genau 3 Benutzernamen in korrekter Reihenfolge zurück gegeben werden");
    }

    private Map<String, Object> user(String username, String password, String email) {
        return Map.of(
                "username", username,
                "password", password,
                "email", email);
    }

    private UserDaoImpl createTestee(DBConnection dbConnection) {
        return new UserDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, int updateCount) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql, Object... params) {
                return resultSet;
            }

            @Override
            public int executeUpdate(String sql, Object... params) throws SQLException {
                return updateCount;
            }

            @Override
            public void close() {
                // Nichts zu tun
            }
        };
    }

    private ResultSet createResultSetMock(List<Map<String, Object>> result) {
        return new ResultSetMock(result);
    }
}
