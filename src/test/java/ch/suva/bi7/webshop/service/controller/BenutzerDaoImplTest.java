package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDaoImpl;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.mock.ResultSetMock;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BenutzerDaoImplTest {

    @Test
    void getBenutzerByEMailLiefertBenutzer() throws SQLException {
        // Arrange
        ResultSet resultSet = createResultSetMock(List.of(user("testBenutzer", "test", "test@somewhere.com")));
        DBConnection dbConnection = createDBConnectionMock(resultSet, 0);
        BenutzerDaoImpl testee = createTestee(dbConnection);

        // Act
        Optional<BenutzerEntity> benutzerOptional = testee.holeBenutzerNachEMail("test@somewhere.com");

        // Assert
        assertTrue(benutzerOptional.isPresent());
        assertEquals("testBenutzer", benutzerOptional.get().getUsername());
        assertEquals("test", benutzerOptional.get().getPassword());
    }

    @Test
    void getBenutzerByEMailLiefertAdminStatus() throws SQLException {
        ResultSet resultSet = createResultSetMock(List.of(user("admin", "admin", "admin@somewhere.com", true)));
        DBConnection dbConnection = createDBConnectionMock(resultSet, 0);
        BenutzerDaoImpl testee = createTestee(dbConnection);

        Optional<BenutzerEntity> benutzerOptional = testee.holeBenutzerNachEMail("admin@somewhere.com");

        assertTrue(benutzerOptional.isPresent());
        assertTrue(benutzerOptional.get().isAdmin(), "Admin-Status muss aus der DB übernommen werden");
    }

    @Test
    void getBenutzerByEMailOhneAdminFlagLiefertFalse() throws SQLException {
        ResultSet resultSet = createResultSetMock(List.of(user("testBenutzer", "test", "test@somewhere.com")));
        DBConnection dbConnection = createDBConnectionMock(resultSet, 0);
        BenutzerDaoImpl testee = createTestee(dbConnection);

        Optional<BenutzerEntity> benutzerOptional = testee.holeBenutzerNachEMail("test@somewhere.com");

        assertTrue(benutzerOptional.isPresent());
        assertFalse(benutzerOptional.get().isAdmin(), "Ohne Admin-Flag muss isAdmin false sein");
    }

    @Test
    void getAllUsernamesLiefertAlleBenutzernamen() throws SQLException {
        ResultSet resultSet = createResultSetMock(List.of(
                user("testBenutzer", "test", "test@somewhere.com"),
                user("testuser2", "test2", "test2@somewhere.com"),
                user("testuser3", "test3", "test3@somewhere.com")));
        DBConnection dbConnection = createDBConnectionMock(resultSet, 0);

        BenutzerDaoImpl testee = createTestee(dbConnection);

        List<String> ergebnisBenutzernamen = testee.holeAlleBenutzernamen();

        assertEquals(
                List.of("testBenutzer", "testuser2", "testuser3"),
                ergebnisBenutzernamen,
                "Es sollten genau 3 Benutzernamen in korrekter Reihenfolge zurück gegeben werden");
    }

    private Map<String, Object> user(String username, String password, String email) {
        return user(username, password, email, false);
    }

    private Map<String, Object> user(String username, String password, String email, boolean isAdmin) {
        return Map.of(
                "username", username,
                "password", password,
                "email", email,
                "isAdmin", isAdmin);
    }

    private BenutzerDaoImpl createTestee(DBConnection dbConnection) {
        return new BenutzerDaoImpl(dbConnection);
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
