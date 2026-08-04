package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginUserResponseTest {

    @Test
    void LoginUserResponseHappyCase() {
        LoginUserResponse testee = new LoginUserResponse("SUCCESS", null, "Max");

        assertEquals("SUCCESS", testee.status);
        assertNull(testee.error);
        assertEquals("Max", testee.username);
    }

    @Test
    void LoginUserResponseUnHappyCase() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new LoginUserResponse(null, "FAILED", null);
        });
        assertEquals("status must not be null", exception.getMessage());
    }
}