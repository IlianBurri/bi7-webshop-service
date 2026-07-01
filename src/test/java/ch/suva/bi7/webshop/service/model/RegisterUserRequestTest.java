package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserRequestTest {
    @Test
    public void registerUserRequestHappyCase() {
        String expectedUsername = "testUser";
        String expectedEmail = "user@example.com";
        String expectedPassword = "securePassword123";

        RegisterUserRequest testee = new RegisterUserRequest(expectedUsername, expectedEmail, expectedPassword);

        assertEquals(expectedUsername, testee.username);
        assertEquals(expectedEmail, testee.email);
        assertEquals(expectedPassword, testee.password);
    }

    @Test
    public void registerUserRequestUnHappyCaseUsernameNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RegisterUserRequest(null, "user@example.com", "securePassword123");
        });
        assertEquals("username, email and password must not be null", exception.getMessage());
    }

    @Test
    public void registerUserRequestUnHappyCaseEmailNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RegisterUserRequest("testUser", null, "securePassword123");
        });
        assertEquals("username, email and password must not be null", exception.getMessage());
    }

    @Test
    public void registerUserRequestUnHappyCasePasswordNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RegisterUserRequest("testUser", "user@example.com", null);
        });
        assertEquals("username, email and password must not be null", exception.getMessage());
    }
}
