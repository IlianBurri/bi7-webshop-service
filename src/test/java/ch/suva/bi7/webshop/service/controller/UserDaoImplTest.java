package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.model.User;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoImplTest {

    @Test
    public void testGetUserByEMail() throws SQLException {
        // Arrange
        Map<String, Object> map1 = Map.of(
                "username", "testuser",
                "password", "test",
                "email", "test@somewhere.com");
        ResultSet resultSet = createResultSetMock(List.of(Boolean.TRUE), List.of(map1));
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
    public void testGetAllUsernames() throws SQLException {
        // TODO Schreibe ein Test mit 3 Namen als Ergebnis
    }

    private UserDaoImpl createTestee(DBConnection dbConnection) {
        return new UserDaoImpl(dbConnection);
    }

    private DBConnection createDBConnectionMock(ResultSet resultSet, int updateCount) {
        return new DBConnection() {
            @Override
            public ResultSet execute(String sql) {
                return resultSet;
            }

            @Override
            public int executeUpdate(String sql) throws SQLException {
                return updateCount;
            }

            @Override
            public void close() {
                // Nichts zu tun
            }
        };
    }

    private ResultSet createResultSetMock(List<Boolean> hasNextList, List<Map<String, Object>> result) {
        return new ResultSetMock(hasNextList, result);
    }
}
